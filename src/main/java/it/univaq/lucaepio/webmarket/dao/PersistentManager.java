package it.univaq.lucaepio.webmarket.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
/**
 * Gestisce la creazione e la fornitura di SessionFactory e Session di Hibernate. 
 * Implementa il pattern Singleton per garantire un'unica istanza di SessionFactory in tutta l'applicazione.
 * SessionFactory:
    È un oggetto thread-safe e immutabile.
    Viene creata una sola volta durante l'inizializzazione dell'applicazione.
    È costosa da creare, quindi si usa tipicamente un singleton.
    Rappresenta il database nell' applicativo.
    Mantiene le configurazioni e le mappature delle entità.
    È utilizzata per ottenere oggetti Session.

    Funzioni principali:

    Crea e gestisce le connessioni al database.
    Memorizza nella cache le mappature e le configurazioni.
    Fornisce le Session per interagire con il database.

 * Session:
    È un oggetto leggero e non thread-safe.
    Viene creata e distrutta per ogni operazione sul database.
    Fornisce un'interfaccia per eseguire operazioni CRUD sulle entità.

    Funzioni principali:

    Gestisce il ciclo di vita delle entità (persistenza, aggiornamento, cancellazione).
    Esegue query e gestisce transazioni.
    Mantiene una cache di primo livello per le entità.

 * Uso tipico:

    Ottieni una Session dalla SessionFactory.
    Inizia una transazione.
    Esegui operazioni sul database.
    Commit o rollback della transazione.
    Chiudi la Session.
 * 
 * @author lucat
 *
 */

public class PersistentManager {
    private static PersistentManager instance;
    private EntityManagerFactory entityManagerFactory;

    private PersistentManager() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("WebMarketPU");
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static synchronized PersistentManager getInstance() {
        if (instance == null) {
            instance = new PersistentManager();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}