package com.warehouse.dao;

import com.warehouse.models.Supplier;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SupplierDAO {

    public void saveSupplier(Supplier supplier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(supplier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Supplier getSupplierById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Supplier.class, id);
        }
    }

    public List<Supplier> getAllSuppliers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Supplier", Supplier.class).list();
        }
    }

    public void updateSupplier(Supplier supplier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(supplier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteSupplier(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Supplier supplier = session.get(Supplier.class, id);
            if (supplier != null) {
                session.delete(supplier);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
