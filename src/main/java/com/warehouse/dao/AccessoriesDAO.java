package com.warehouse.dao;

import com.warehouse.models.Accessories;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class AccessoriesDAO {

    // Метод для получения всех товаров категории Accessories
    public static List<Accessories> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Accessories> query = session.createQuery("from Accessories", Accessories.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Accessories findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Accessories.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Accessories accessories) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(accessories);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Accessories accessories) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(accessories);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Accessories accessories) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(accessories);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Accessories updatedAccessories) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Accessories accessories = session.get(Accessories.class, articul);
            if (accessories != null) {
                accessories.setSubcategory(updatedAccessories.getSubcategory());
                accessories.setVendor(updatedAccessories.getVendor());
                accessories.setMaterial(updatedAccessories.getMaterial());
                accessories.setColour(updatedAccessories.getColour());
                accessories.setSize(updatedAccessories.getSize());
                accessories.setFeatures(updatedAccessories.getFeatures());
                accessories.setAppointment(updatedAccessories.getAppointment());
                session.update(accessories);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Accessories findByArticul(int articul) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Accessories> query = session.createQuery("FROM Accessories a WHERE a.articul = :articul", Accessories.class);
            query.setParameter("articul", articul);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
