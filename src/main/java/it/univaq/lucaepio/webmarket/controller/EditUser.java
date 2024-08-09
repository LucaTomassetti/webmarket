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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        String userId = request.getParameter("id");
        User userToEdit = userService.getUserById(Long.parseLong(userId));

        Map<String, Object> data = new HashMap<>();
        data.put("user", userToEdit);

        processTemplate(response, "edit_user.ftl.html", data);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String userType = request.getParameter("userType");

        User user = userService.getUserById(Long.parseLong(userId));
        user.setUsername(username);
        user.setEmail(email);
        user.setUserType(User.UserType.valueOf(userType));

        userService.updateUser(user);

        String message;
        try {
            userService.updateUser(user);
            message = "Utente aggiornato con successo!";
        } catch (Exception e) {
            message = "Errore durante l'aggiornamento dell'utente: " + e.getMessage();
        }

        // Reindirizza alla pagina di gestione utenti con il messaggio
        response.sendRedirect("manageUsers?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));
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
