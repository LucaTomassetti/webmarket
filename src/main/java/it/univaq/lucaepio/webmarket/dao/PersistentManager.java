package it.univaq.lucaepio.webmarket.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PersistentManager {
    private static PersistentManager instance;
    private SessionFactory sessionFactory;

    private PersistentManager() {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static synchronized PersistentManager getInstance() {
        if (instance == null) {
            instance = new PersistentManager();
        }
        return instance;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    public void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}