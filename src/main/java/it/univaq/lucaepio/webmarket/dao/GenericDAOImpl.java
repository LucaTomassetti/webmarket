package it.univaq.lucaepio.webmarket.dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.save(entity);
                tx.commit();
                return entity;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public T findById(ID id) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            return session.get(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = PersistentManager.getInstance().getSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.update(entity);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(T entity) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.delete(entity);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}