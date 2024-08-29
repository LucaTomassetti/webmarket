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
import org.hibernate.Hibernate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucat
 */
public class PurchaseRequestService {
    private static final Logger LOGGER = Logger.getLogger(PurchaseRequestService.class.getName());

    public PurchaseRequest createPurchaseRequest(User orderer, Subcategory subcategory, List<RequestCharacteristic> characteristics, String notes, boolean isPriority) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = new PurchaseRequest();
            request.setOrderer(orderer);
            request.setSubcategory(subcategory);
            request.setNotes(notes);
            request.setPriority(isPriority);
            
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

    public PurchaseRequest getPurchaseRequestById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            PurchaseRequest request = em.find(PurchaseRequest.class, id);
            if (request != null) {
                Hibernate.initialize(request.getCharacteristics());
            }
            return request;
        } finally {
            em.close();
        }
    }

    public void updatePurchaseRequest(PurchaseRequest request) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della richiesta di acquisto", e);
            throw new RuntimeException("Errore durante l'aggiornamento della richiesta di acquisto", e);
        } finally {
            em.close();
        }
    }

    public void deletePurchaseRequest(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = em.find(PurchaseRequest.class, id);
            if (request != null && request.getStatus() == PurchaseRequest.Status.PENDING) {
                em.remove(request);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione della richiesta di acquisto", e);
            throw new RuntimeException("Errore durante l'eliminazione della richiesta di acquisto", e);
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
    public List<PurchaseRequest> getPurchaseRequestsByUserPaginated(User user, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.orderer = :user ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("user", user);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<PurchaseRequest> getPurchaseRequestsByUserAndStatusPaginated(User user, PurchaseRequest.Status status, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.orderer = :user AND pr.status = :status ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("user", user);
            query.setParameter("status", status);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalPurchaseRequestsCountByUser(User user) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pr) FROM PurchaseRequest pr WHERE pr.orderer = :user",
                Long.class
            );
            query.setParameter("user", user);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public long getTotalPurchaseRequestsCountByUserAndStatus(User user, PurchaseRequest.Status status) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pr) FROM PurchaseRequest pr WHERE pr.orderer = :user AND pr.status = :status",
                Long.class
            );
            query.setParameter("user", user);
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    public List<PurchaseRequest> getPendingRequestsForTechnicians(int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.status = :status AND pr.assignedTechnician IS NULL ORDER BY pr.isPriority DESC, pr.createdAt ASC",
                PurchaseRequest.class
            );
            query.setParameter("status", PurchaseRequest.Status.PENDING);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalPendingRequestsCountForTechnicians() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pr) FROM PurchaseRequest pr WHERE pr.status = :status AND pr.assignedTechnician IS NULL",
                Long.class
            );
            query.setParameter("status", PurchaseRequest.Status.PENDING);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public void assignTechnician(Long requestId, User technician) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = em.find(PurchaseRequest.class, requestId);
            if (request != null && request.getStatus() == PurchaseRequest.Status.PENDING) {
                request.setAssignedTechnician(technician);
                request.setStatus(PurchaseRequest.Status.IN_PROGRESS);
                em.merge(request);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'assegnazione del tecnico alla richiesta", e);
            throw new RuntimeException("Errore durante l'assegnazione del tecnico alla richiesta", e);
        } finally {
            em.close();
        }
    }
    public List<PurchaseRequest> getAssignedRequestsForTechnician(User technician, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.assignedTechnician = :technician AND pr.status = :status ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("technician", technician);
            query.setParameter("status", PurchaseRequest.Status.IN_PROGRESS);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalAssignedRequestsCountForTechnician(User technician) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pr) FROM PurchaseRequest pr WHERE pr.assignedTechnician = :technician AND pr.status = :status",
                Long.class
            );
            query.setParameter("technician", technician);
            query.setParameter("status", PurchaseRequest.Status.IN_PROGRESS);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public void updateRequestStatus(Long requestId, PurchaseRequest.Status newStatus) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = em.find(PurchaseRequest.class, requestId);
            if (request != null) {
                request.setStatus(newStatus);
                em.merge(request);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dello stato della richiesta", e);
            throw new RuntimeException("Errore durante l'aggiornamento dello stato della richiesta", e);
        } finally {
            em.close();
        }
    }
    public void closePurchaseRequest(Long requestId, PurchaseRequest.Status finalStatus, String rejectionReason) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PurchaseRequest request = em.find(PurchaseRequest.class, requestId);
            if (request != null && request.getStatus() == PurchaseRequest.Status.IN_PROGRESS) {
                request.setStatus(finalStatus);
                if (finalStatus != PurchaseRequest.Status.COMPLETED) {
                    request.setRejectionReason(rejectionReason);
                }
                em.merge(request);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la chiusura della richiesta di acquisto", e);
            throw new RuntimeException("Errore durante la chiusura della richiesta di acquisto", e);
        } finally {
            em.close();
        }
    }
    public List<PurchaseRequest> getCompletedAndRejectedRequestsForTechnician(User technician, int page, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<PurchaseRequest> query = em.createQuery(
                "SELECT pr FROM PurchaseRequest pr WHERE pr.assignedTechnician = :technician " +
                "AND pr.status IN (:completedStatus, :rejectedNotConformingStatus, :rejectedNotWorkingStatus) " +
                "ORDER BY pr.createdAt DESC",
                PurchaseRequest.class
            );
            query.setParameter("technician", technician);
            query.setParameter("completedStatus", PurchaseRequest.Status.COMPLETED);
            query.setParameter("rejectedNotConformingStatus", PurchaseRequest.Status.REJECTED_NOT_CONFORMING);
            query.setParameter("rejectedNotWorkingStatus", PurchaseRequest.Status.REJECTED_NOT_WORKING);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalCompletedAndRejectedRequestsCountForTechnician(User technician) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(pr) FROM PurchaseRequest pr WHERE pr.assignedTechnician = :technician " +
                "AND pr.status IN (:completedStatus, :rejectedNotConformingStatus, :rejectedNotWorkingStatus)",
                Long.class
            );
            query.setParameter("technician", technician);
            query.setParameter("completedStatus", PurchaseRequest.Status.COMPLETED);
            query.setParameter("rejectedNotConformingStatus", PurchaseRequest.Status.REJECTED_NOT_CONFORMING);
            query.setParameter("rejectedNotWorkingStatus", PurchaseRequest.Status.REJECTED_NOT_WORKING);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
