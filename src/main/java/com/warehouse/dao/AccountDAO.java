package com.warehouse.dao;

import com.warehouse.entities.Account;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AccountDAO {

    public boolean save(Account account) {
        try {
            // Assuming you are using Hibernate or any ORM to save
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(account);
            session.getTransaction().commit();
            session.close();
            return true;  // Return true if save is successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if an exception occurs
        }
    }

    public Account findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Account.class, id);
        }
    }

    public List<Account> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Account", Account.class).list();
        }
    }

    // Новый метод для поиска по имени пользователя
    public Account findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Account WHERE username = :username", Account.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    public void update(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(account);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(account);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
