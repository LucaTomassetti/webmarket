/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.model;

import java.util.List;

/**
 *
 * @author lucat
 */

public class PendingPurchaseRequest {
    private User orderer;
    private Subcategory subcategory;
    private List<RequestCharacteristic> characteristics;
    private String notes;
    private boolean isPriority;

    public PendingPurchaseRequest(User orderer, Subcategory subcategory, List<RequestCharacteristic> characteristics, String notes, boolean isPriority) {
        this.orderer = orderer;
        this.subcategory = subcategory;
        this.characteristics = characteristics;
        this.notes = notes;
        this.isPriority = isPriority;
    }

    // Getters and setters

    public User getOrderer() {
        return orderer;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public List<RequestCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isPriority() {
        return isPriority;
    }
}
