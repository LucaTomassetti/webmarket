package it.univaq.lucaepio.webmarket.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

/**
 * Implementa l'interfaccia GenericDAO, fornendo l'implementazione concreta delle operazioni CRUD utilizzando Hibernate. 
 * Questa classe è progettata per essere estesa da DAO specifici per entità, se necessario.
 * 
 * @author lucat
 * @param <T>
 * @param <ID> 
 */

public class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {
    protected Class<T> entityClass;

    public GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T findById(ID id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            return em.createQuery("from " + entityClass.getName(), entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(T entity) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}