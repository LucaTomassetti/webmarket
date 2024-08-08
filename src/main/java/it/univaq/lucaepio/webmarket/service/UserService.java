package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.dao.GenericDAO;
import it.univaq.lucaepio.webmarket.dao.GenericDAOImpl;
import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import it.univaq.lucaepio.webmarket.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
    private GenericDAO<User, Long> userDAO;

    public UserService() {
        this.userDAO = new GenericDAOImpl<>(User.class);
    }

    public User registerUser(String username, String email, String password, User.UserType userType) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                LOGGER.info("Iniziando la registrazione per l'utente: " + username);
                
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                user.setUserType(userType);

                user = userDAO.save(user);
                
                tx.commit();
                LOGGER.info("Utente registrato con ID: " + user.getUserID());
                return user;
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante la registrazione dell'utente", e);
                throw new RuntimeException("Error registering user", e);
            }
        }
    }

    public User authenticateUser(String username, String password) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            User user = session.createQuery("FROM User WHERE username = :username", User.class)
                               .setParameter("username", username)
                               .uniqueResult();
            
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'autenticazione dell'utente", e);
            throw new RuntimeException("Error authenticating user", e);
        }
    }

    public User getUserById(Long id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public void updateUser(User user) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                userDAO.update(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'utente", e);
                throw new RuntimeException("Error updating user", e);
            }
        }
    }

    public void deleteUser(Long id) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User user = userDAO.findById(id);
                if (user != null) {
                    userDAO.delete(user);
                }
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'utente", e);
                throw new RuntimeException("Error deleting user", e);
            }
        }
    }

    public User getUserByUsername(String username) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                          .setParameter("username", username)
                          .uniqueResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'utente per username", e);
            throw new RuntimeException("Error retrieving user by username", e);
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                          .setParameter("email", email)
                          .uniqueResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'utente per email", e);
            throw new RuntimeException("Error retrieving user by email", e);
        }
    }
}