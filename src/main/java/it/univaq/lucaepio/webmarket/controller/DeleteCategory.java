/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import it.univaq.lucaepio.webmarket.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author lucat
 */
@WebServlet("/deleteCategory")
public class DeleteCategory extends HttpServlet {
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long categoryId = Long.parseLong(request.getParameter("id"));
        
        try {
            categoryService.deleteCategory(categoryId);
            response.sendRedirect("manageCategories?message=Categoria eliminata con successo");
        } catch (Exception e) {
            response.sendRedirect("manageCategories?error=Errore durante l'eliminazione della categoria: " + e.getMessage());
        }
    }
}
