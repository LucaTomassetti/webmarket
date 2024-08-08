package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.CategoryService;
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
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class Home extends HttpServlet {
    private CategoryService categoryService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
        if (freemarkerConfig == null) {
            throw new ServletException("FreeMarker configuration not found in servlet context");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        List<Category> categories = categoryService.getAllCategories();

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("categories", categories);

        processTemplate(response, "home.ftl.html", data);
    }

    private void processTemplate(HttpServletResponse response, String templateName, Map<String, Object> data) throws IOException {
        Template template = freemarkerConfig.getTemplate(templateName);
        response.setContentType("text/html");
        try {
            template.process(data, response.getWriter());
        } catch (TemplateException e) {
            throw new IOException("Error processing FreeMarker template", e);
        }
    }
}