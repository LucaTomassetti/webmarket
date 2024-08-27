/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.model.PurchaseRequest;
import it.univaq.lucaepio.webmarket.service.PurchaseRequestService;
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

/**
 *
 * @author lucat
 */

@WebServlet("/viewPurchaseRequests")
public class ViewPurchaseRequests extends HttpServlet {
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

        String statusFilter = request.getParameter("status");
        List<PurchaseRequest> purchaseRequests;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            PurchaseRequest.Status status = PurchaseRequest.Status.valueOf(statusFilter.toUpperCase());
            purchaseRequests = purchaseRequestService.getPurchaseRequestsByUserAndStatus(currentUser, status);
        } else {
            purchaseRequests = purchaseRequestService.getPurchaseRequestsByUser(currentUser);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("purchaseRequests", purchaseRequests);
        data.put("currentStatus", statusFilter);

        processTemplate(response, "view_purchase_requests.ftl.html", data);
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
