/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.lucaepio.webmarket.model.Characteristic;
import it.univaq.lucaepio.webmarket.service.CategoryService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author lucat
 */

@WebServlet("/getCharacteristics")
public class GetCharacteristics extends HttpServlet {
    private CategoryService categoryService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long subcategoryId = Long.parseLong(request.getParameter("subcategoryId"));
        List<Characteristic> characteristics = categoryService.getCharacteristicsBySubcategory(subcategoryId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), characteristics);
    }
}
