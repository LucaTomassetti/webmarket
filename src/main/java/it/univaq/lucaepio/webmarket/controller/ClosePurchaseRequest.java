/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.univaq.lucaepio.webmarket.model.*;
import it.univaq.lucaepio.webmarket.service.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucat
 */

@WebServlet("/closePurchaseRequest")
public class ClosePurchaseRequest extends HttpServlet {
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
        if (currentUser == null || currentUser.getUserType() != User.UserType.Ordinante) {
            response.sendRedirect("login");
            return;
        }

        Long requestId = Long.parseLong(request.getParameter("id"));
        PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(requestId);

        if (purchaseRequest == null || !purchaseRequest.getOrderer().getUserID().equals(currentUser.getUserID())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("purchaseRequest", purchaseRequest);

        processTemplate(response, "close_purchase_request.ftl.html", data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Ordinante) {
            response.sendRedirect("login");
            return;
        }

        Long requestId = Long.parseLong(request.getParameter("id"));
        String action = request.getParameter("action");
        String rejectionReason = request.getParameter("rejectionReason");

        PurchaseRequest.Status finalStatus;
        switch (action) {
            case "accept":
                finalStatus = PurchaseRequest.Status.COMPLETED;
                break;
            case "reject_not_conforming":
                finalStatus = PurchaseRequest.Status.REJECTED_NOT_CONFORMING;
                break;
            case "reject_not_working":
                finalStatus = PurchaseRequest.Status.REJECTED_NOT_WORKING;
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
        }

        purchaseRequestService.closePurchaseRequest(requestId, finalStatus, rejectionReason);
        response.sendRedirect("viewPurchaseRequests");
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
