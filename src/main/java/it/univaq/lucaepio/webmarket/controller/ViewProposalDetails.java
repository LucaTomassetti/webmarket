/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.PurchaseProposal;
import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.PurchaseProposalService;
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

@WebServlet("/viewProposalDetails")
public class ViewProposalDetails extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ViewProposalDetails.class.getName());
    private PurchaseProposalService purchaseProposalService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        purchaseProposalService = new PurchaseProposalService();
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

        String proposalId = request.getParameter("id");
        if (proposalId == null || proposalId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing proposal ID");
            return;
        }

        try {
            PurchaseProposal proposal = purchaseProposalService.getProposalById(Long.parseLong(proposalId));
            if (proposal == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposal not found");
                return;
            }

            // Verifica che l'utente sia autorizzato a visualizzare questa proposta
            if (currentUser.getUserType() == User.UserType.Ordinante && !proposal.getPurchaseRequest().getOrderer().getUserID().equals(currentUser.getUserID())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this proposal");
                return;
            }

            if (currentUser.getUserType() == User.UserType.Tecnico && !proposal.getTechnician().getUserID().equals(currentUser.getUserID())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this proposal");
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("user", currentUser);
            data.put("proposal", proposal);

            processTemplate(response, "proposal_details.ftl.html", data);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing proposal details", e);
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
