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
    
    /**
     * Questo metodo serve per convertire una stringa JSON (memorizzata nel campo subcategoriesJson) in una struttura dati Java. 
     * La chiave esterna è il nome della sottocategoria.
     * Il valore è un'altra mappa che rappresenta le caratteristiche della sottocategoria (la chiave è il nome della caratteristica
     * mentre il valore è il valore della caratteristica)
     * Es. : {
                "Laptop": {
                  "RAM": "8GB",
                  "CPU": "Intel i5",
                  "Storage": "256GB SSD"
                },
                "Smartphone": {
                  "Screen": "6.1 inch",
                  "Camera": "12MP",
                  "Battery": "3000mAh"
                }
              }
     * 
     * @return 
     */
    public Map<String, Map<String, String>> getSubcategoriesWithCharacteristics() {
        //gestisce il caso di nessuna sottocategoria, anche per evitare errori
        if (subcategoriesJson == null || subcategoriesJson.isEmpty()) {
            return new HashMap<>();
        }
        //Creo un'istanza di ObjectMapper dalla libreria Jackson.
        //Questa classe è usata per convertire tra JSON e oggetti Java.
        ObjectMapper mapper = new ObjectMapper();
        try {
            //readValue converte la stringa JSON in un oggetto Java.
            //TypeReference specifica il tipo esatto in cui convertire il JSON.
            //In questo caso, stiamo convertendo in Map<String, Map<String, String>>.
            return mapper.readValue(subcategoriesJson, 
                new TypeReference<Map<String, Map<String, String>>>() {});
        } catch (IOException e) {
            //In caso di errore, viene restituita una mappa vuota.
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
