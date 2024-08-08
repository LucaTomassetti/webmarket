package it.univaq.lucaepio.webmarket.dao;

import java.util.List;
/**
 * Questa interfaccia definisce le operazioni CRUD (Create, Read, Update, Delete) di base che dovrebbero essere implementate per ogni entità del dominio. 
 * Fornisce un contratto comune per l'accesso ai dati.
 * 
 * @author lucat
 * @param <T>
 * @param <ID> 
 */
public interface GenericDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void delete(T entity);
}