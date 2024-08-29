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

@WebServlet("/purchaseProposals")
public class PurchaseProposalController extends HttpServlet {
    private PurchaseProposalService purchaseProposalService;
    private PurchaseRequestService purchaseRequestService;
    private Configuration freemarkerConfig;
    private static final int PROPOSALS_PER_PAGE = 10;

    @Override
    public void init() throws ServletException {
        super.init();
        purchaseProposalService = new PurchaseProposalService();
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

        String action = request.getParameter("action");
        if (action == null) {
            action = "viewProposals";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);

        try {
            switch (action) {
                case "viewProposals":
                    handleViewProposals(request, currentUser, data);
                    break;
                case "viewExistingProposals":
                    handleViewExistingProposals(request, currentUser, data);
                    break;
                case "viewDetails":
                    handleViewDetails(request, currentUser, data);
                    break;
                case "createProposalForm":
                    handleCreateProposalForm(request, currentUser, data);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
        } catch (Exception e) {
            throw new ServletException("Error processing request", e);
        }

        String template = determineTemplate(action, currentUser);
        processTemplate(response, template, data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "createProposal":
                    handleCreateProposal(request, currentUser, response);
                    break;
                case "updateProposalStatus":
                    handleUpdateProposalStatus(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
        } catch (Exception e) {
            throw new ServletException("Error processing request", e);
        }
    }

    private void handleViewProposals(HttpServletRequest request, User currentUser, Map<String, Object> data) {
        int currentPage = getPageNumber(request);
        List<PurchaseProposal> proposals;
        long totalProposals;

        if (currentUser.getUserType() == User.UserType.Tecnico) {
            proposals = purchaseProposalService.getProposalsByTechnician(currentUser, currentPage, PROPOSALS_PER_PAGE);
            totalProposals = purchaseProposalService.getTotalProposalsCountByTechnician(currentUser);
        } else {
            proposals = purchaseProposalService.getProposalsByOrderer(currentUser, currentPage, PROPOSALS_PER_PAGE);
            totalProposals = purchaseProposalService.getTotalProposalsCountByOrderer(currentUser);
        }

        PaginationUtil pagination = new PaginationUtil(PROPOSALS_PER_PAGE, currentPage, totalProposals);

        data.put("proposals", proposals);
        data.put("pagination", pagination);
    }

    private void handleViewExistingProposals(HttpServletRequest request, User currentUser, Map<String, Object> data) throws ServletException {
        String requestId = request.getParameter("requestId");
        if (requestId == null || requestId.isEmpty()) {
            throw new ServletException("Missing request ID");
        }

        PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(Long.parseLong(requestId));
        List<PurchaseProposal> existingProposals = purchaseProposalService.getProposalsByRequestId(Long.parseLong(requestId));

        data.put("purchaseRequest", purchaseRequest);
        data.put("existingProposals", existingProposals);

        boolean canCreateNewProposal = existingProposals.isEmpty() || 
                                       existingProposals.get(0).getStatus() == PurchaseProposal.Status.REJECTED;
        data.put("canCreateNewProposal", canCreateNewProposal);
    }

    private void handleViewDetails(HttpServletRequest request, User currentUser, Map<String, Object> data) throws ServletException {
        String proposalId = request.getParameter("id");
        if (proposalId == null || proposalId.isEmpty()) {
            throw new ServletException("Missing proposal ID");
        }

        PurchaseProposal proposal = purchaseProposalService.getProposalById(Long.parseLong(proposalId));
        if (proposal == null) {
            throw new ServletException("Proposal not found");
        }

        data.put("proposal", proposal);
    }

    private void handleCreateProposalForm(HttpServletRequest request, User currentUser, Map<String, Object> data) throws ServletException {
        if (currentUser.getUserType() != User.UserType.Tecnico) {
            throw new ServletException("Unauthorized access");
        }

        String requestId = request.getParameter("requestId");
        if (requestId == null || requestId.isEmpty()) {
            throw new ServletException("Missing request ID");
        }

        PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(Long.parseLong(requestId));
        data.put("purchaseRequest", purchaseRequest);
    }

    private void handleCreateProposal(HttpServletRequest request, User currentUser, HttpServletResponse response) throws ServletException, IOException {
        if (currentUser.getUserType() != User.UserType.Tecnico) {
            throw new ServletException("Unauthorized access");
        }

        Long requestId = Long.parseLong(request.getParameter("requestId"));
        String manufacturerName = request.getParameter("manufacturerName");
        String productName = request.getParameter("productName");
        String productCode = request.getParameter("productCode");
        Double price = Double.parseDouble(request.getParameter("price"));
        String url = request.getParameter("url");
        String notes = request.getParameter("notes");

        PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(requestId);
        PurchaseProposal newProposal = purchaseProposalService.createProposal(purchaseRequest, currentUser, manufacturerName, productName, productCode, price, url, notes);

        response.sendRedirect("purchaseProposals?action=viewExistingProposals&requestId=" + requestId);
    }

    private void handleUpdateProposalStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long proposalId = Long.parseLong(request.getParameter("proposalId"));
        String newStatusStr = request.getParameter("newStatus");
        PurchaseProposal.Status newStatus = PurchaseProposal.Status.valueOf(newStatusStr);
        String rejectionReason = request.getParameter("rejectionReason");

        purchaseProposalService.updateProposalStatus(proposalId, newStatus, rejectionReason);
        response.sendRedirect("purchaseProposals?action=viewProposals");
    }

    private String determineTemplate(String action, User user) {
        switch (action) {
            case "viewProposals":
                return user.getUserType() == User.UserType.Tecnico ? "technician_proposals.ftl.html" : "orderer_proposals.ftl.html";
            case "viewExistingProposals":
                return "existing_proposals.ftl.html";
            case "viewDetails":
                return "proposal_details.ftl.html";
            case "createProposalForm":
                return "create_proposal_form.ftl.html";
            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    private int getPageNumber(HttpServletRequest request) {
        String pageParam = request.getParameter("page");
        return (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
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
