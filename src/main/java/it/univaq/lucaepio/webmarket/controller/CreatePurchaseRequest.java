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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucat
 */

@WebServlet("/createPurchaseRequest")
public class CreatePurchaseRequest extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CreatePurchaseRequest.class.getName());
    
    private CategoryService categoryService;
    private PurchaseRequestService purchaseRequestService;
    private PayPalService payPalService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
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
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Ordinante) {
            response.sendRedirect("login");
            return;
        }

        String subcategoryIdStr = request.getParameter("subcategoryId");
        Map<String, Object> data = createDataMap(currentUser);

        if (subcategoryIdStr != null && !subcategoryIdStr.isEmpty()) {
            Long subcategoryId = Long.parseLong(subcategoryIdStr);
            Subcategory selectedSubcategory = categoryService.getSubcategoryById(subcategoryId);
            data.put("selectedSubcategory", selectedSubcategory);
            data.put("characteristics", categoryService.getCharacteristicsBySubcategory(subcategoryId));
        }

        processTemplate(response, "create_purchase_request.ftl.html", data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Ordinante) {
            response.sendRedirect("login");
            return;
        }

        String subcategoryIdStr = request.getParameter("subcategoryId");
        String action = request.getParameter("action");
        boolean isPriority = "true".equals(request.getParameter("isPriority"));

        if ("submit".equals(action) && subcategoryIdStr != null && !subcategoryIdStr.isEmpty()) {
            Long subcategoryId = Long.parseLong(subcategoryIdStr);
            Subcategory selectedSubcategory = categoryService.getSubcategoryById(subcategoryId);
            String notes = request.getParameter("notes");
            List<RequestCharacteristic> characteristics = new ArrayList<>();
            
            for (Characteristic characteristic : selectedSubcategory.getCharacteristics()) {
                String value = request.getParameter("characteristic_" + characteristic.getId());
                boolean isIndifferent = request.getParameter("indifferent_" + characteristic.getId()) != null;
                if (isIndifferent) {
                    value = "indifferente";
                }
                if (value != null && !value.isEmpty()) {
                    RequestCharacteristic reqChar = new RequestCharacteristic();
                    reqChar.setCharacteristic(characteristic);
                    reqChar.setValue(value);
                    characteristics.add(reqChar);
                }
            }

            if (isPriority) {
                if (payPalService == null) {
                    request.setAttribute("error", "Il pagamento PayPal non è disponibile al momento. Riprova più tardi.");
                    processTemplate(response, "create_purchase_request.ftl.html", createDataMap(currentUser));
                    return;
                }
                
                try {
                    String orderId = payPalService.createOrder();
                    session.setAttribute("paypalOrderId", orderId);
                    session.setAttribute("pendingPurchaseRequest", new PendingPurchaseRequest(currentUser, selectedSubcategory, characteristics, notes, true));
                    response.sendRedirect("paypalCheckout?orderId=" + orderId);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to create PayPal order", e);
                    request.setAttribute("error", "Si è verificato un errore durante la creazione dell'ordine PayPal. Riprova più tardi.");
                    processTemplate(response, "create_purchase_request.ftl.html", createDataMap(currentUser));
                }
            } else {
                PurchaseRequest purchaseRequest = purchaseRequestService.createPurchaseRequest(currentUser, selectedSubcategory, characteristics, notes, false);
                response.sendRedirect("viewPurchaseRequests");
            }
        } else {
            // Se non è una sottomissione valida, torniamo alla pagina di creazione
            doGet(request, response);
        }
    }

    private Map<String, Object> createDataMap(User currentUser) {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryService.getAllCategoriesWithSubcategories());
        data.put("user", currentUser);
        return data;
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