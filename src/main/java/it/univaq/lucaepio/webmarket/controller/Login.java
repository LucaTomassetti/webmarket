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
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lucat
 */

@WebServlet("/login")
public class Login extends HttpServlet {
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
        HttpSession session = request.getSession();
        if(session.getAttribute("user") != null){
            response.sendRedirect("home");
        }else{
            
        }
        processTemplate(response, "login.ftl.html", new HashMap<>());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.authenticateUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("home");
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("error", "Username o password non validi");
            processTemplate(response, "login.ftl.html", data);
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