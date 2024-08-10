package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import it.univaq.lucaepio.webmarket.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestisce la logica di business relativa agli utenti, come la registrazione, l'autenticazione e altre operazioni specifiche degli utenti. 
 * Utilizza GenericDAO per l'accesso ai dati e implementa logiche aggiuntive come l'hashing delle password.
 * 
 * @author lucat
 */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public boolean isEmailExists(String email) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public boolean isUsernameExists(String username) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
            query.setParameter("username", username);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public User registerUser(String username, String email, String password, User.UserType userType) throws UserAlreadyExistsException {
        if (isEmailExists(email)) {
            throw new UserAlreadyExistsException("Email già esistente");
        }
        if (isUsernameExists(username)) {
            throw new UserAlreadyExistsException("Username già esistente");
        }

        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setUserType(userType);
            em.persist(user);
            tx.commit();
            LOGGER.info("Utente registrato con ID: " + user.getUserID());
            return user;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la registrazione dell'utente", e);
            throw new RuntimeException("Errore durante la registrazione dell'utente", e);
        } finally {
            em.close();
        }
    }

    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public User authenticateUser(String username, String password) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();
            
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'autenticazione dell'utente", e);
            throw new RuntimeException("Errore durante l'autenticazione dell'utente", e);
        } finally {
            em.close();
        }
    }

    public User getUserById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public List<User> getUsersPage(int pageNumber, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("FROM User", User.class);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long getTotalUsersCount() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public void updateUser(User user) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'utente", e);
            throw new RuntimeException("Errore durante l'aggiornamento dell'utente", e);
        } finally {
            em.close();
        }
    }
    public User updateUserCredentials(Long userId, String newUsername, String oldPassword, String newPassword, String confirmPassword) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Utente non trovato.");
            }

            // Verifica la vecchia password
            if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("La vecchia password non è corretta.");
            }

            // Verifica che la nuova password e la conferma corrispondano
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("La nuova password e la conferma non corrispondono.");
            }

            // Aggiorna username e password
            user.setUsername(newUsername);
            user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));

            em.merge(user);
            tx.commit();
            LOGGER.info("Credenziali dell'utente aggiornate con successo. ID: " + userId);
            return user;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento delle credenziali dell'utente", e);
            throw new IllegalArgumentException("Errore durante l'aggiornamento delle credenziali: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void deleteUser(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'utente", e);
            throw new RuntimeException("Errore durante l'eliminazione dell'utente", e);
        } finally {
            em.close();
        }
    }

    public User getUserByUsername(String username) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public User getUserByEmail(String email) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}