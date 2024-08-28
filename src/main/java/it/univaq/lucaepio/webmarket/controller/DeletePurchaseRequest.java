/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.*;
import it.univaq.lucaepio.webmarket.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author lucat
 */

@WebServlet("/deletePurchaseRequest")
public class DeletePurchaseRequest extends HttpServlet {
    private PurchaseRequestService purchaseRequestService;

    @Override
    public void init() throws ServletException {
        super.init();
        purchaseRequestService = new PurchaseRequestService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        PurchaseRequest purchaseRequest = purchaseRequestService.getPurchaseRequestById(Long.parseLong(requestId));
        if (purchaseRequest == null || !purchaseRequest.getOrderer().getUserID().equals(currentUser.getUserID()) || purchaseRequest.getStatus() != PurchaseRequest.Status.PENDING) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase request not found or not deletable");
            return;
        }

        purchaseRequestService.deletePurchaseRequest(Long.parseLong(requestId));

        response.sendRedirect("viewPurchaseRequests");
    }
}
