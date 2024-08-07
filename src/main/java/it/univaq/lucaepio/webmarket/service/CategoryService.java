/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.service;
import it.univaq.lucaepio.webmarket.model.Category;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucat
 */

public class CategoryService {
    private EntityManager em;

    public CategoryService(EntityManager em) {
        this.em = em;
    }

    public List<Category> getAllCategories() {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c", Category.class);
        return query.getResultList();
    }

    public Category getCategoryById(Long id) {
        return em.find(Category.class, id);
    }

    public void createCategory(String name, Map<String, Map<String, String>> subcategories) {
        em.getTransaction().begin();
        try {
            Category category = new Category();
            category.setName(name);
            
            ObjectMapper mapper = new ObjectMapper();
            String subcategoriesJson = mapper.writeValueAsString(subcategories);
            category.setSubcategoriesJson(subcategoriesJson);
            
            em.persist(category);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error creating category", e);
        }
    }

    public void updateCategory(Long id, String name, Map<String, Map<String, String>> subcategories) {
        em.getTransaction().begin();
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                category.setName(name);
                
                ObjectMapper mapper = new ObjectMapper();
                String subcategoriesJson = mapper.writeValueAsString(subcategories);
                category.setSubcategoriesJson(subcategoriesJson);
                
                em.merge(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error updating category", e);
        }
    }

    public void deleteCategory(Long id) {
        em.getTransaction().begin();
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error deleting category", e);
        }
    }

    public void addSubcategory(Long categoryId, String subcategoryName, Map<String, String> characteristics) {
        em.getTransaction().begin();
        try {
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                Map<String, Map<String, String>> subcategories = category.getSubcategoriesWithCharacteristics();
                subcategories.put(subcategoryName, characteristics);
                
                ObjectMapper mapper = new ObjectMapper();
                String subcategoriesJson = mapper.writeValueAsString(subcategories);
                category.setSubcategoriesJson(subcategoriesJson);
                
                em.merge(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error adding subcategory", e);
        }
    }

    public void removeSubcategory(Long categoryId, String subcategoryName) {
        em.getTransaction().begin();
        try {
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                Map<String, Map<String, String>> subcategories = category.getSubcategoriesWithCharacteristics();
                subcategories.remove(subcategoryName);
                
                ObjectMapper mapper = new ObjectMapper();
                String subcategoriesJson = mapper.writeValueAsString(subcategories);
                category.setSubcategoriesJson(subcategoriesJson);
                
                em.merge(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error removing subcategory", e);
        }
    }
}