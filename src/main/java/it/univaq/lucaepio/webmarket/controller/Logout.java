/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.UserService;
import it.univaq.lucaepio.webmarket.util.FreemarkerConfig;
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

/**
 *
 * @author lucat
 */

@WebServlet("/logout")
public class Logout extends HttpServlet{
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
        if (freemarkerConfig == null) {
            throw new ServletException("FreeMarker configuration not found in servlet context");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("login");
        renderTemplate(response, "login.ftl.html", new HashMap<>());
    }

    private void renderTemplate(HttpServletResponse response, String templateName, Map<String, Object> dataModel) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new IOException("Error processing FreeMarker template", e);
        }
    }
}
  
