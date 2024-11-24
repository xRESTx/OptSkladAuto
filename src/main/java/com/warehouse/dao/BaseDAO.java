package com.warehouse.dao;

import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

public abstract class BaseDAO<T> {
    private final Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        executeTransaction(session -> session.save(entity));
    }

    public T findById(Object id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, (Serializable) id);
        }
    }

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    public void update(T entity) {
        executeTransaction(session -> session.update(entity));
    }

    public void delete(T entity) {
        executeTransaction(session -> session.delete(entity));
    }

    private void executeTransaction(TransactionConsumer consumer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction(); // Начинаем транзакцию
            consumer.accept(session);  // Выполняем операцию
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // В случае ошибки откатываем транзакцию
            }
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface TransactionConsumer {
        void accept(Session session);
    }
}
