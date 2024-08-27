package it.univaq.lucaepio.webmarket.service;

import it.univaq.lucaepio.webmarket.model.Category;
import it.univaq.lucaepio.webmarket.model.Subcategory;
import it.univaq.lucaepio.webmarket.model.Characteristic;
import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
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

    public Category createCategory(Category category) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Persisti la categoria
            em.persist(category);
            
            // Persisti le sottocategorie e le caratteristiche
            for (Subcategory subcategory : category.getSubcategories()) {
                subcategory.setCategory(category);
                em.persist(subcategory);
                
                for (Characteristic characteristic : subcategory.getCharacteristics()) {
                    characteristic.setSubcategory(subcategory);
                    em.persist(characteristic);
                }
            }
            
            tx.commit();
            LOGGER.info("Categoria creata con ID: " + category.getId());
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

    public void updateCategory(Category category) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category existingCategory = em.find(Category.class, category.getId());
            existingCategory.setName(category.getName());
            
            // Rimuovi sottocategorie e caratteristiche esistenti
            for (Subcategory subcategory : existingCategory.getSubcategories()) {
                for (Characteristic characteristic : subcategory.getCharacteristics()) {
                    em.remove(characteristic);
                }
                em.remove(subcategory);
            }
            existingCategory.getSubcategories().clear();
            
            // Aggiungi nuove sottocategorie e caratteristiche
            for (Subcategory subcategory : category.getSubcategories()) {
                subcategory.setCategory(existingCategory);
                em.persist(subcategory);
                for (Characteristic characteristic : subcategory.getCharacteristics()) {
                    characteristic.setSubcategory(subcategory);
                    em.persist(characteristic);
                }
                existingCategory.getSubcategories().add(subcategory);
            }
            
            em.merge(existingCategory);
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

    public List<Category> getAllCategories() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            // Primo passaggio: recupera tutte le categorie con le sottocategorie
            TypedQuery<Category> query = em.createQuery(
                "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subcategories", 
                Category.class
            );
            List<Category> categories = query.getResultList();

            // Secondo passaggio: per ogni categoria, recupera le caratteristiche delle sottocategorie
            for (Category category : categories) {
                for (Subcategory subcategory : category.getSubcategories()) {
                    TypedQuery<Characteristic> characteristicsQuery = em.createQuery(
                        "SELECT c FROM Characteristic c WHERE c.subcategory = :subcategory",
                        Characteristic.class
                    );
                    characteristicsQuery.setParameter("subcategory", subcategory);
                    List<Characteristic> characteristics = characteristicsQuery.getResultList();
                    subcategory.setCharacteristics(characteristics);
                }
            }

            return categories;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le categorie", e);
            throw new RuntimeException("Errore durante il recupero di tutte le categorie", e);
        } finally {
            em.close();
        }
    }

    public Category getCategoryById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                // Carica esplicitamente le sottocategorie
                TypedQuery<Subcategory> subcategoryQuery = em.createQuery(
                    "SELECT s FROM Subcategory s LEFT JOIN FETCH s.characteristics WHERE s.category = :category",
                    Subcategory.class
                );
                subcategoryQuery.setParameter("category", category);
                List<Subcategory> subcategories = subcategoryQuery.getResultList();
                category.setSubcategories(subcategories);
            }
            return category;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero della categoria con ID: " + id, e);
            throw new RuntimeException("Errore durante il recupero della categoria", e);
        } finally {
            em.close();
        }
    }
    public List<Category> getCategoriesPage(int pageNumber, int pageSize) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            // Primo passaggio: recupera le categorie con le sottocategorie
            TypedQuery<Category> query = em.createQuery(
                "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subcategories", 
                Category.class
            );
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            List<Category> categories = query.getResultList();

            // Secondo passaggio: per ogni categoria, recupera le caratteristiche delle sottocategorie
            for (Category category : categories) {
                for (Subcategory subcategory : category.getSubcategories()) {
                    TypedQuery<Characteristic> characteristicsQuery = em.createQuery(
                        "SELECT c FROM Characteristic c WHERE c.subcategory = :subcategory",
                        Characteristic.class
                    );
                    characteristicsQuery.setParameter("subcategory", subcategory);
                    List<Characteristic> characteristics = characteristicsQuery.getResultList();
                    subcategory.setCharacteristics(characteristics);
                }
            }

            return categories;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero della pagina di categorie", e);
            throw new RuntimeException("Errore durante il recupero della pagina di categorie", e);
        } finally {
            em.close();
        }
    }

    public long getTotalCategoriesCount() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Category c", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il conteggio totale delle categorie", e);
            throw new RuntimeException("Errore durante il conteggio totale delle categorie", e);
        } finally {
            em.close();
        }
    }
    public Subcategory getSubcategoryById(Long id) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Subcategory> query = em.createQuery(
                "SELECT s FROM Subcategory s LEFT JOIN FETCH s.characteristics WHERE s.id = :id",
                Subcategory.class
            );
            query.setParameter("id", id);
            Subcategory subcategory = query.getSingleResult();
            
            // Carica esplicitamente la categoria associata
            subcategory.getCategory().getName(); // Questo forza il caricamento della categoria
            
            return subcategory;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero della sottocategoria con ID: " + id, e);
            throw new RuntimeException("Errore durante il recupero della sottocategoria", e);
        } finally {
            em.close();
        }
    }
    public List<Subcategory> getSubcategoriesByCategory(Long categoryId) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Subcategory> query = em.createQuery(
                "SELECT s FROM Subcategory s WHERE s.category.id = :categoryId",
                Subcategory.class
            );
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle sottocategorie per la categoria con ID: " + categoryId, e);
            throw new RuntimeException("Errore durante il recupero delle sottocategorie", e);
        } finally {
            em.close();
        }
    }

    public List<Characteristic> getCharacteristicsBySubcategory(Long subcategoryId) {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Characteristic> query = em.createQuery(
                "SELECT c FROM Characteristic c WHERE c.subcategory.id = :subcategoryId",
                Characteristic.class
            );
            query.setParameter("subcategoryId", subcategoryId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle caratteristiche per la sottocategoria con ID: " + subcategoryId, e);
            throw new RuntimeException("Errore durante il recupero delle caratteristiche", e);
        } finally {
            em.close();
        }
    }
    public List<Category> getAllCategoriesWithSubcategories() {
        EntityManager em = PersistentManager.getInstance().getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subcategories", 
                Category.class
            );
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le categorie con sottocategorie", e);
            throw new RuntimeException("Errore durante il recupero di tutte le categorie con sottocategorie", e);
        } finally {
            em.close();
        }
    }
}