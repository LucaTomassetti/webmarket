/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;
import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.service.CategoryService;
import it.univaq.lucaepio.webmarket.util.JPAUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import jakarta.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucat
 */

@WebServlet("/home")
public class Home extends HttpServlet {

    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setServletContextForTemplateLoading(getServletContext(), "/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CategoryService categoryService = new CategoryService(em);
            List<Category> categories = categoryService.getAllCategories();

            // Debug: print categories
            System.out.println("Categories: " + categories);
            for (Category category : categories) {
                System.out.println("Category: " + category.getName() + ", Subcategories: " + category.getSubcategoriesWithCharacteristics());
            }

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("categories", categories);

            Template template = freemarkerConfig.getTemplate("home.ftl");
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new ServletException("Error processing template", e);
        } finally {
            em.close();
        }
    }
}
