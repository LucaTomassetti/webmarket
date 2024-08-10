/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.util;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Questo filtro implementa un meccanismo di autenticazione per l'applicazione. 
 * Intercetta tutte le richieste e verifica se l'utente è autenticato. 
 * Se l'utente non è autenticato e sta cercando di accedere a una risorsa protetta, viene reindirizzato alla pagina di login. 
 * Questo assicura che solo gli utenti autenticati possano accedere alle parti protette dell'applicazione.
 * 
 * @author lucat
 */

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String loginURI = request.getContextPath() + "/login";
        String registerURI = request.getContextPath() + "/register";

        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = request.getRequestURI().equals(loginURI);
        boolean registerRequest = request.getRequestURI().equals(registerURI);

        if (loggedIn || loginRequest || registerRequest) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
