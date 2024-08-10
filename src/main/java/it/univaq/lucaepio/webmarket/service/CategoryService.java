package it.univaq.lucaepio.webmarket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fornisce la logica di business specifica per le operazioni sulle categorie. 
 * Utilizza GenericDAO per l'accesso ai dati, aggiungendo eventuali metodi o validazioni necessarie.
 * 
 * @author lucat
 */
public class CategoryService {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private ObjectMapper objectMapper = new ObjectMapper();

    public Category createCategory(String name, String subcategoriesJson) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = new Category();
            category.setName(name);
            category.setSubcategoriesJson(subcategoriesJson);
            em.persist(category);
            tx.commit();
            LOGGER.info("Categoria creata con ID: " + category.getCategoryID());
            return category;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la creazione della categoria", e);
            throw new RuntimeException("Errore durante la creazione della categoria", e);
        } finally {
            em.close();
        }
    }

    public List<Category> getAllCategories() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery("FROM Category", Category.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Category getCategoryById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public void updateCategory(Category category) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della categoria", e);
            throw new RuntimeException("Errore durante l'aggiornamento della categoria", e);
        } finally {
            em.close();
        }
    }

    public void deleteCategory(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione della categoria", e);
            throw new RuntimeException("Errore durante l'eliminazione della categoria", e);
        } finally {
            em.close();
        }
    }

    public void addSubcategory(Long categoryId, String subcategoryName, Map<String, String> characteristics) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                Map<String, Map<String, String>> subcategories = category.getSubcategoriesWithCharacteristics();
                subcategories.put(subcategoryName, characteristics);
                category.setSubcategoriesJson(objectMapper.writeValueAsString(subcategories));
                em.merge(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta della sottocategoria", e);
            throw new RuntimeException("Errore durante l'aggiunta della sottocategoria", e);
        } finally {
            em.close();
        }
    }

    public void removeSubcategory(Long categoryId, String subcategoryName) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                Map<String, Map<String, String>> subcategories = category.getSubcategoriesWithCharacteristics();
                subcategories.remove(subcategoryName);
                category.setSubcategoriesJson(objectMapper.writeValueAsString(subcategories));
                em.merge(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Errore durante la rimozione della sottocategoria", e);
            throw new RuntimeException("Errore durante la rimozione della sottocategoria", e);
        } finally {
            em.close();
        }
    }
}