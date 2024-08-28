/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.*;
import it.univaq.lucaepio.webmarket.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucat
 */

@WebServlet(urlPatterns = {"/paypalCheckout", "/paypalConfirm", "/paypalCancel"})
public class PayPalCheckout extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PayPalCheckout.class.getName());
    
    private PayPalService payPalService;
    private PurchaseRequestService purchaseRequestService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        purchaseRequestService = new PurchaseRequestService();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
        String baseUrl = getServletContext().getInitParameter("baseUrl");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new ServletException("baseUrl not configured in web.xml");
        }
        try {
            payPalService = new PayPalService(baseUrl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize PayPalService", e);
            throw new ServletException("Failed to initialize PayPalService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        LOGGER.info("Received request for path: " + servletPath);
        switch (servletPath) {
            case "/paypalCheckout":
                handleCheckout(request, response);
                break;
            case "/paypalConfirm":
                handleConfirm(request, response);
                break;
            case "/paypalCancel":
                handleCancel(request, response);
                break;
            default:
                LOGGER.warning("Unhandled path: " + servletPath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    private void handleCheckout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Ordinante) {
            response.sendRedirect("login");
            return;
        }

        PendingPurchaseRequest pendingRequest = (PendingPurchaseRequest) session.getAttribute("pendingPurchaseRequest");
        if (pendingRequest == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session state");
            return;
        }

        try {
            String orderId = payPalService.createOrder();
            session.setAttribute("paypalOrderId", orderId);
            
            // Redirect to PayPal for payment
            String approveUrl = payPalService.getApproveUrl(orderId);
            response.sendRedirect(approveUrl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create PayPal order", e);
            Map<String, Object> data = new HashMap<>();
            data.put("error", "Si è verificato un errore durante la creazione dell'ordine PayPal. Riprova più tardi.");
            processTemplate(response, "error.ftl.html", data);
        }
    }
    private void handleConfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling PayPal confirmation");
        HttpSession session = request.getSession(false);
        
        // Verifica se l'utente è ancora autenticato
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            LOGGER.warning("User not authenticated in handleConfirm");
            response.sendRedirect("login");
            return;
        }

        String paypalOrderId = (String) session.getAttribute("paypalOrderId");
        PendingPurchaseRequest pendingRequest = (PendingPurchaseRequest) session.getAttribute("pendingPurchaseRequest");

        if (paypalOrderId == null || pendingRequest == null) {
            LOGGER.warning("Invalid session state in handleConfirm");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session state");
            return;
        }

        try {
            boolean paymentSuccessful = payPalService.captureOrder(paypalOrderId);

            if (paymentSuccessful) {
                PurchaseRequest purchaseRequest = purchaseRequestService.createPurchaseRequest(
                    currentUser,
                    pendingRequest.getSubcategory(),
                    pendingRequest.getCharacteristics(),
                    pendingRequest.getNotes(),
                    true
                );
                session.removeAttribute("paypalOrderId");
                session.removeAttribute("pendingPurchaseRequest");
                
                Map<String, Object> data = new HashMap<>();
                data.put("user", currentUser);
                data.put("purchaseRequest", purchaseRequest);
                processTemplate(response, "paypal_confirm.ftl.html", data);
            } else {
                LOGGER.warning("Payment failed in handleConfirm");
                response.sendError(HttpServletResponse.SC_PAYMENT_REQUIRED, "Payment failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing PayPal payment", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing payment");
        }
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling PayPal cancellation");
        HttpSession session = request.getSession(false);
        session.removeAttribute("paypalOrderId");
        session.removeAttribute("pendingPurchaseRequest");
        response.sendRedirect("createPurchaseRequest");
    }

    private void processTemplate(HttpServletResponse response, String templateName, Map<String, Object> data) throws IOException {
        response.setContentType("text/html");
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(data, response.getWriter());
        } catch (TemplateException e) {
            throw new IOException("Error processing FreeMarker template", e);
        }
    }
}