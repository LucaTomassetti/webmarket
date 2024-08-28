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

@WebServlet("/viewPurchaseRequestDetails")
public class ViewPurchaseRequestDetails extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ViewPurchaseRequestDetails.class.getName());
    private PurchaseRequestService purchaseRequestService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        purchaseRequestService = new PurchaseRequestService();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        String requestId = request.getParameter("id");
        if (requestId == null || requestId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing request ID");
            return;
        }

        try {
            PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(Long.parseLong(requestId));
            if (purchaseRequest == null || !purchaseRequest.getOrderer().getUserID().equals(currentUser.getUserID())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase request not found");
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("user", currentUser);
            data.put("purchaseRequest", purchaseRequest);

            processTemplate(response, "purchase_request_details.ftl.html", data);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing purchase request details", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request");
        }
    }

    private void processTemplate(HttpServletResponse response, String templateName, Map<String, Object> data) throws IOException {
        response.setContentType("text/html");
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(data, response.getWriter());
        } catch (TemplateException e) {
            LOGGER.log(Level.SEVERE, "Error processing template", e);
            throw new IOException("Error processing FreeMarker template", e);
        }
    }
}
