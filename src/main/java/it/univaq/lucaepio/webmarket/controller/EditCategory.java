/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.model.Subcategory;
import it.univaq.lucaepio.webmarket.model.Characteristic;
import it.univaq.lucaepio.webmarket.service.CategoryService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.univaq.lucaepio.webmarket.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucat
 */
@WebServlet("/editCategory")
public class EditCategory extends HttpServlet {
    private CategoryService categoryService;
    private Configuration freemarkerConfig;

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
        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        Long categoryId = Long.parseLong(request.getParameter("id"));
        Category category = categoryService.getCategoryById(categoryId);
        data.put("category", category);
        data.put("subcategoryCount", 1);
        processTemplate(response, "edit_category.ftl.html", data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long categoryId = Long.parseLong(request.getParameter("categoryId"));
        String categoryName = request.getParameter("categoryName");
        String[] subcategoryNames = request.getParameterValues("subcategoryName[]");
        String[] characteristicNames = request.getParameterValues("characteristicName[]");

        Category category = categoryService.getCategoryById(categoryId);
        category.setName(categoryName);

        List<Subcategory> subcategories = new ArrayList<>();
        if (subcategoryNames != null) {
            for (int i = 0; i < subcategoryNames.length; i++) {
                Subcategory subcategory = new Subcategory();
                subcategory.setName(subcategoryNames[i]);
                subcategory.setCategory(category);

                List<Characteristic> characteristics = new ArrayList<>();
                if (characteristicNames != null && characteristicNames.length > i) {
                    String[] charNames = characteristicNames[i].split(",");
                    for (String charName : charNames) {
                        Characteristic characteristic = new Characteristic();
                        characteristic.setName(charName.trim());
                        characteristic.setSubcategory(subcategory);
                        characteristics.add(characteristic);
                    }
                }
                subcategory.setCharacteristics(characteristics);
                subcategories.add(subcategory);
            }
        }
        category.setSubcategories(subcategories);

        categoryService.updateCategory(category);
        response.sendRedirect("manageCategories");
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
