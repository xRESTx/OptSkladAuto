package com.warehouse.dao;

import com.warehouse.models.Repair;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RepairDAO {

    // Метод для получения всех товаров категории Repair
    public static List<Repair> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Repair> query = session.createQuery("from Repair", Repair.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Repair findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Repair.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Repair repair) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(repair);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Repair repair) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(repair);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Repair repair) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(repair);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Repair updatedRepair) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Repair repair = session.get(Repair.class, articul);
            if (repair != null) {
                repair.setSubcategory(updatedRepair.getSubcategory());
                repair.setVendor(updatedRepair.getVendor());
                repair.setWeight(updatedRepair.getWeight());
                repair.setSize(updatedRepair.getSize());
                repair.setOem(updatedRepair.getOem());
                repair.setMaterial(updatedRepair.getMaterial());
                repair.setCompatibility(updatedRepair.getCompatibility());
                session.update(repair);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Repair findByArticul(int articul) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Repair> query = session.createQuery("FROM Repair r WHERE r.articul = :articul", Repair.class);
            query.setParameter("articul", articul);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
