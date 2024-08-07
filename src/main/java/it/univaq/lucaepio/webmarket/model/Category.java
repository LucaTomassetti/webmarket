/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.model;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lucat
 */

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryID;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "json")
    private String subcategoriesJson;

    public Map<String, Map<String, String>> getSubcategoriesWithCharacteristics() {
        if (subcategoriesJson == null || subcategoriesJson.isEmpty()) {
            return new HashMap<>();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(subcategoriesJson, 
                new TypeReference<Map<String, Map<String, String>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Getters and setters
    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubcategoriesJson() {
        return subcategoriesJson;
    }

    public void setSubcategoriesJson(String subcategoriesJson) {
        this.subcategoriesJson = subcategoriesJson;
    }
}
