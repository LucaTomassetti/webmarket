/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.model.Subcategory;
import it.univaq.lucaepio.webmarket.model.Characteristic;
import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.CategoryService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.univaq.lucaepio.webmarket.util.Message;
import it.univaq.lucaepio.webmarket.util.PaginationUtil;

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

@WebServlet("/manageCategories")
public class ManageCategories extends HttpServlet {
    private CategoryService categoryService;
    private Configuration freemarkerConfig;
    private static final int CATEGORIES_PER_PAGE = 5;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            currentPage = Integer.parseInt(pageParam);
        }

        long totalCategories = categoryService.getTotalCategoriesCount();
        List<Category> categories = categoryService.getCategoriesPage(currentPage, CATEGORIES_PER_PAGE);
        PaginationUtil pagination = new PaginationUtil(CATEGORIES_PER_PAGE, currentPage, totalCategories);

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("pagination", pagination);
        data.put("user", currentUser);

        String successMessage = request.getParameter("message");
        String errorMessage = request.getParameter("error");

        if (successMessage != null && !successMessage.isEmpty()) {
            data.put("message", new Message("success", successMessage));
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            data.put("message", new Message("danger", errorMessage));
        }

        processTemplate(response, "manage_categories.ftl.html", data);
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