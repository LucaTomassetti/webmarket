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
import it.univaq.lucaepio.webmarket.util.PaginationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucat
 */

@WebServlet("/manageUsers")
public class ManageUsers extends HttpServlet {
    private UserService userService;
    private Configuration freemarkerConfig;
    private static final int USERS_PER_PAGE = 5;

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

        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            currentPage = Integer.parseInt(pageParam);
        }

        long totalUsers = userService.getTotalUsersCount();
        List<User> usersOnPage = userService.getUsersPage(currentPage, USERS_PER_PAGE);
        PaginationUtil pagination = new PaginationUtil(USERS_PER_PAGE, currentPage, totalUsers);

        Map<String, Object> data = new HashMap<>();
        data.put("users", usersOnPage);
        data.put("pagination", pagination);
        data.put("user", currentUser);

        String successParam = request.getParameter("success");
        if (successParam != null && !successParam.isEmpty()) {
            String decodedMessage = URLDecoder.decode(successParam, StandardCharsets.UTF_8.toString());
            data.put("message", new Message("success", decodedMessage));
        }

        String errorParam = request.getParameter("error");
        if (errorParam != null && !errorParam.isEmpty()) {
            String decodedMessage = URLDecoder.decode(errorParam, StandardCharsets.UTF_8.toString());
            data.put("message", new Message("danger", decodedMessage));
        }

        processTemplate(response, "manage_users.ftl.html", data);
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