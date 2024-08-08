package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.dao.GenericDAO;
import it.univaq.lucaepio.webmarket.dao.GenericDAOImpl;
import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryService {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private GenericDAO<Category, Long> categoryDAO;

    public CategoryService() {
        this.categoryDAO = new GenericDAOImpl<>(Category.class);
    }

    public Category createCategory(String name, String subcategoriesJson) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Category category = new Category();
                category.setName(name);
                category.setSubcategoriesJson(subcategoriesJson);
                category = categoryDAO.save(category);
                tx.commit();
                return category;
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante la creazione della categoria", e);
                throw new RuntimeException("Error creating category", e);
            }
        }
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryDAO.findById(id);
    }

    public void updateCategory(Category category) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                categoryDAO.update(category);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della categoria", e);
                throw new RuntimeException("Error updating category", e);
            }
        }
    }

    public void deleteCategory(Long id) {
        try (Session session = PersistentManager.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Category category = categoryDAO.findById(id);
                if (category != null) {
                    categoryDAO.delete(category);
                }
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione della categoria", e);
                throw new RuntimeException("Error deleting category", e);
            }
        }
    }
}