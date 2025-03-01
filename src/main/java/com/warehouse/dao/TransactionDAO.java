package com.warehouse.dao;

import com.warehouse.models.Transaction;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class TransactionDAO {

    public void saveTransaction(Transaction transaction) {
        org.hibernate.Transaction hTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hTransaction = session.beginTransaction();
            session.save(transaction);
            hTransaction.commit();
        } catch (Exception e) {
            if (hTransaction != null) hTransaction.rollback();
            e.printStackTrace();
        }
    }

    public Transaction getTransactionById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Transaction.class, id);
        }
    }

    public List<Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Transaction", Transaction.class).list();
        }
    }

    public void updateTransaction(Transaction transaction) {
        org.hibernate.Transaction hTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hTransaction = session.beginTransaction();
            session.update(transaction);
            hTransaction.commit();
        } catch (Exception e) {
            if (hTransaction != null) hTransaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteTransaction(int id) {
        org.hibernate.Transaction hTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hTransaction = session.beginTransaction();
            Transaction transaction = session.get(Transaction.class, id);
            if (transaction != null) {
                session.delete(transaction);
                hTransaction.commit();
            }
        } catch (Exception e) {
            if (hTransaction != null) hTransaction.rollback();
            e.printStackTrace();
        }
    }
}
