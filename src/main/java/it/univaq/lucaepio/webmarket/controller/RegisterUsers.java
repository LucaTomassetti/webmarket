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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

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
        //Controllo se l'admin è autenticato
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        //Se non lo è, lo reindirizzo nel login
        if (currentUser == null && currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("registerUsers", true);
        data.put("user", currentUser);
        //Carico il template per la registrazione
        processTemplate(response, "registerUsers.ftl.html", data);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Prelevo i dati dalla form
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        Map<String, Object> data = new HashMap<>();
        try {
            //Registro l'utente salvandolo nel DB
            User user = userService.registerUser(username, email, password, User.UserType.valueOf(userType));
            //Reindirizzo nella pagina del login
            response.sendRedirect("login");
        } catch (Exception e) {
            data.put("error", "Errore durante la registrazione: " + e.getMessage());
            processTemplate(response, "register.ftl.html", data);
        }
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