/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.model.PurchaseProposal;
import it.univaq.lucaepio.webmarket.model.PurchaseRequest;
import it.univaq.lucaepio.webmarket.model.User;
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

public class PurchaseProposalService {
    private static final Logger LOGGER = Logger.getLogger(PurchaseProposalService.class.getName());

    public PurchaseProposal createProposal(PurchaseRequest purchaseRequest, User technician, String manufacturerName, String productName, String productCode, Double price, String url, String notes) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            PurchaseProposal proposal = new PurchaseProposal();
            proposal.setPurchaseRequest(purchaseRequest);
            proposal.setTechnician(technician);
            proposal.setManufacturerName(manufacturerName);
            proposal.setProductName(productName);
            proposal.setProductCode(productCode);
            proposal.setPrice(price);
            proposal.setUrl(url);
            proposal.setNotes(notes);
            
            em.persist(proposal);
            tx.commit();
            
            return proposal;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la creazione della proposta d'acquisto", e);
            throw new RuntimeException("Errore durante la creazione della proposta d'acquisto", e);
        } finally {
            em.close();
        }
    }

    public List<PurchaseProposal> getProposalsByTechnician(User technician, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseProposal> query = em.createQuery("SELECT pp FROM PurchaseProposal pp WHERE pp.technician = :technician ORDER BY pp.createdAt DESC",
                PurchaseProposal.class
            );
            query.setParameter("technician", technician);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<PurchaseProposal> getProposalsByOrderer(User orderer, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseProposal> query = em.createQuery("SELECT pp FROM PurchaseProposal pp WHERE pp.purchaseRequest.orderer = :orderer ORDER BY pp.createdAt DESC",
                PurchaseProposal.class
            );
            query.setParameter("orderer", orderer);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalProposalsCountByTechnician(User technician) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pp) FROM PurchaseProposal pp WHERE pp.technician = :technician",
                Long.class
            );
            query.setParameter("technician", technician);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public long getTotalProposalsCountByOrderer(User orderer) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pp) FROM PurchaseProposal pp WHERE pp.purchaseRequest.orderer = :orderer",
                Long.class
            );
            query.setParameter("orderer", orderer);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public boolean hasPendingProposal(Long requestId) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pp) FROM PurchaseProposal pp WHERE pp.purchaseRequest.id = :requestId AND pp.status = :status",
                Long.class
            );
            query.setParameter("requestId", requestId);
            query.setParameter("status", PurchaseProposal.Status.PENDING);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public void updateProposalStatus(Long proposalId, PurchaseProposal.Status newStatus, String rejectionReason) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseProposal proposal = em.find(PurchaseProposal.class, proposalId);
            if (proposal != null) {
                proposal.setStatus(newStatus);
                if (newStatus == PurchaseProposal.Status.REJECTED) {
                    proposal.setRejectionReason(rejectionReason);
                }
                em.merge(proposal);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Errore durante l'aggiornamento dello stato della proposta", e);
        } finally {
            em.close();
        }
    }
    public PurchaseProposal getProposalById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            return em.find(PurchaseProposal.class, id);
        } finally {
            em.close();
        }
    }

    public List<PurchaseProposal> getProposalsByRequestId(Long requestId) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseProposal> query = em.createQuery(
                "SELECT pp FROM PurchaseProposal pp WHERE pp.purchaseRequest.id = :requestId ORDER BY pp.createdAt DESC",
                PurchaseProposal.class
            );
            query.setParameter("requestId", requestId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public List<PurchaseProposal> getProposalsByPurchaseRequest(PurchaseRequest purchaseRequest) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseProposal> query = em.createQuery(
                "SELECT pp FROM PurchaseProposal pp WHERE pp.purchaseRequest = :purchaseRequest ORDER BY pp.createdAt DESC",
                PurchaseProposal.class
            );
            query.setParameter("purchaseRequest", purchaseRequest);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle proposte per la richiesta di acquisto", e);
            throw new RuntimeException("Errore durante il recupero delle proposte", e);
        } finally {
            em.close();
        }
    }
    public boolean hasAcceptedProposal(Long requestId) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pp) FROM PurchaseProposal pp WHERE pp.purchaseRequest.id = :requestId AND pp.status = :status",
                Long.class
            );
            query.setParameter("requestId", requestId);
            query.setParameter("status", PurchaseProposal.Status.ACCEPTED);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
