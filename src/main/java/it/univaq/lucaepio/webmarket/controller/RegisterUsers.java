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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 
 * @author lucat
 */


@WebServlet("/registerUsers")
public class RegisterUsers extends HttpServlet {
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
        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        processTemplate(response, "registerUsers.ftl.html", data);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String userType = request.getParameter("userType");

        Map<String, Object> data = new HashMap<>();
        data.put("user", currentUser);

        if (!password.equals(confirmPassword)) {
            data.put("message", new Message("danger", "Le password non coincidono"));
            data.put("username", username);
            data.put("email", email);
            data.put("selectedUserType", userType);
            processTemplate(response, "registerUsers.ftl.html", data);
            return;
        }

        try {
            User newUser = userService.registerUser(username, email, password, User.UserType.valueOf(userType));
            response.sendRedirect("manageUsers?success=Utente registrato con successo");
            return;
        } catch (UserService.UserAlreadyExistsException e) {
            data.put("message", new Message("danger", "Email o Username già esistenti: " + e.getMessage()));
        } catch (Exception e) {
            data.put("message", new Message("danger", "Errore durante la registrazione: " + e.getMessage()));
        }

        // Se arriviamo qui, c'è stato un errore. Ripopoliamo i campi del form per comodità dell'utente
        data.put("username", username);
        data.put("email", email);
        data.put("selectedUserType", userType);

        processTemplate(response, "registerUsers.ftl.html", data);
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