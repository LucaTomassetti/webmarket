/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;


import it.univaq.lucaepio.webmarket.model.*;
import it.univaq.lucaepio.webmarket.service.CategoryService;
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

@WebServlet("/createPurchaseRequest")
public class CreatePurchaseRequest extends HttpServlet {
    private CategoryService categoryService;
    private PurchaseRequestService purchaseRequestService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
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

        List<Category> categories = categoryService.getAllCategoriesWithSubcategories();
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("user", currentUser);
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

        List<Category> categories = categoryService.getAllCategoriesWithSubcategories();
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("user", currentUser);

        if (subcategoryIdStr != null && !subcategoryIdStr.isEmpty()) {
            Long subcategoryId = Long.parseLong(subcategoryIdStr);
            Subcategory selectedSubcategory = categoryService.getSubcategoryById(subcategoryId);
            data.put("selectedSubcategoryId", subcategoryId);
            data.put("selectedSubcategory", selectedSubcategory);

            if ("submit".equals(action)) {
                // Process the final submission
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

                PurchaseRequest purchaseRequest = purchaseRequestService.createPurchaseRequest(currentUser, selectedSubcategory, characteristics, notes);
                response.sendRedirect("viewPurchaseRequests");
                return;
            }
        }

        processTemplate(response, "create_purchase_request.ftl.html", data);
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
