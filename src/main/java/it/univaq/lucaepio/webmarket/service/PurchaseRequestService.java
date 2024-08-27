/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.model.*;
import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucat
 */
public class PurchaseRequestService {
    private static final Logger LOGGER = Logger.getLogger(PurchaseRequestService.class.getName());

    public PurchaseRequest createPurchaseRequest(User orderer, Subcategory subcategory, List<RequestCharacteristic> characteristics, String notes) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = new PurchaseRequest();
            request.setOrderer(orderer);
            request.setSubcategory(subcategory);
            request.setNotes(notes);
            
            for (RequestCharacteristic characteristic : characteristics) {
                request.addCharacteristic(characteristic);
            }
            
            em.persist(request);
            tx.commit();
            return request;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la creazione della richiesta di acquisto", e);
            throw new RuntimeException("Errore durante la creazione della richiesta di acquisto", e);
        } finally {
            em.close();
        }
    }

    public List<PurchaseRequest> getPurchaseRequestsByUser(User user) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.orderer = :user ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("user", user);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<PurchaseRequest> getPurchaseRequestsByUserAndStatus(User user, PurchaseRequest.Status status) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.orderer = :user AND pr.status = :status ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("user", user);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
