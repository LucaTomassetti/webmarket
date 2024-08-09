/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;
import it.univaq.lucaepio.webmarket.model.User;
import it.univaq.lucaepio.webmarket.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author lucat
 */

@WebServlet("/deleteUser")
public class DeleteUser extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getUserType() != User.UserType.Amministratore) {
            response.sendRedirect("login");
            return;
        }

        String userId = request.getParameter("id");
        String message = "";

        if (userId != null && !userId.isEmpty()) {
            try {
                Long id = Long.parseLong(userId);
                User userToDelete = userService.getUserById(id);
                if (userToDelete != null) {
                    userService.deleteUser(id);
                    message = "Utente " + userToDelete.getUsername() + " eliminato con successo.";
                } else {
                    message = "Utente non trovato.";
                }
            } catch (NumberFormatException e) {
                message = "ID utente non valido.";
            } catch (Exception e) {
                message = "Si è verificato un errore durante l'eliminazione dell'utente.";
            }
        } else {
            message = "ID utente mancante.";
        }

        // Codifica il messaggio per l'uso in un URL
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        response.sendRedirect("manageUsers?message=" + encodedMessage);
    }
}