/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;
import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.UserService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.univaq.lucaepio.webmarket.util.Message;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lucat
 */

@WebServlet("/editUser")
public class EditUser extends HttpServlet {
    private UserService userService;
    private Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
        freemarkerConfig = (Configuration) getServletContext().getAttribute("freemarker_config");
        if (freemarkerConfig == null) {
            throw new ServletException("FreeMarker configuration not found in servlet context");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        String userId = request.getParameter("id");
        User userToEdit = userService.getUserById(Long.valueOf(userId));
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("userToEdit", userToEdit);

        String error = request.getParameter("error");
        if (error != null) {
            data.put("message", new Message("danger", error));
        }

        processTemplate(response, "edit_user.ftl.html", data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        String userId = request.getParameter("id");
        String username = request.getParameter("username");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            String errorMessage = URLEncoder.encode("Le nuove password non coincidono! Riprovare", StandardCharsets.UTF_8.toString());
            response.sendRedirect("editUser?id=" + userId + "&error=" + errorMessage);
            return;
        }

        try {
            User updatedUser = userService.updateUserCredentials(Long.valueOf(userId), username, oldPassword, newPassword, confirmPassword);
            String successMessage = URLEncoder.encode("Utente " + username + " aggiornato con successo!", StandardCharsets.UTF_8.toString());
            response.sendRedirect("manageUsers?success=" + successMessage);
        } catch (IllegalArgumentException e) {
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString());
            response.sendRedirect("editUser?id=" + userId + "&error=" + errorMessage);
        }
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