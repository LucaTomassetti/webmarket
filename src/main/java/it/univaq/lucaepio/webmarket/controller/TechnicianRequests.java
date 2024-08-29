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
import it.univaq.lucaepio.webmarket.util.PaginationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucat
 */

@WebServlet("/technicianRequests")
public class TechnicianRequests extends HttpServlet {
    private PurchaseRequestService purchaseRequestService;
    private Configuration freemarkerConfig;
    private static final int REQUESTS_PER_PAGE = 10;

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
        if (currentUser == null || currentUser.getUserType() != User.UserType.Tecnico) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "viewPending";
        }

        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            currentPage = Integer.parseInt(pageParam);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("currentAction", action);

        switch (action) {
            case "viewPending":
                handleViewPending(currentUser, currentPage, data);
                break;
            case "viewAssigned":
                handleViewAssigned(currentUser, currentPage, data);
                break;
            case "viewHistory":
                handleViewHistory(currentUser, currentPage, data);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
        }

        processTemplate(response, "technician_requests.ftl.html", data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Tecnico) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        Long requestId = Long.parseLong(request.getParameter("requestId"));

        switch (action) {
            case "assignRequest":
                purchaseRequestService.assignTechnician(requestId, currentUser);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
        }

        response.sendRedirect("technicianRequests?action=viewAssigned");
    }
    private void handleViewHistory(User currentUser, int currentPage, Map<String, Object> data) {
        List<PurchaseRequest> requests = purchaseRequestService.getCompletedAndRejectedRequestsForTechnician(currentUser, currentPage, REQUESTS_PER_PAGE);
        long totalRequests = purchaseRequestService.getTotalCompletedAndRejectedRequestsCountForTechnician(currentUser);
        
        PaginationUtil pagination = new PaginationUtil(REQUESTS_PER_PAGE, currentPage, totalRequests);
        
        data.put("requests", requests);
        data.put("pagination", pagination);
    }
    private void handleViewPending(User currentUser, int currentPage, Map<String, Object> data) {
        List<PurchaseRequest> requests = purchaseRequestService.getPendingRequestsForTechnicians(currentPage, REQUESTS_PER_PAGE);
        long totalRequests = purchaseRequestService.getTotalPendingRequestsCountForTechnicians();
        
        PaginationUtil pagination = new PaginationUtil(REQUESTS_PER_PAGE, currentPage, totalRequests);
        
        data.put("requests", requests);
        data.put("pagination", pagination);
    }

    private void handleViewAssigned(User currentUser, int currentPage, Map<String, Object> data) {
        List<PurchaseRequest> requests = purchaseRequestService.getAssignedRequestsForTechnician(currentUser, currentPage, REQUESTS_PER_PAGE);
        long totalRequests = purchaseRequestService.getTotalAssignedRequestsCountForTechnician(currentUser);
        
        PaginationUtil pagination = new PaginationUtil(REQUESTS_PER_PAGE, currentPage, totalRequests);
        
        data.put("requests", requests);
        data.put("pagination", pagination);
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
