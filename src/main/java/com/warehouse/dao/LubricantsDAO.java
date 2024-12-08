package com.warehouse.dao;

import com.warehouse.models.Lubricants;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class LubricantsDAO {

    // Метод для получения всех товаров категории Lubricants
    public static List<Lubricants> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Lubricants> query = session.createQuery("from Lubricants", Lubricants.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Lubricants findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Lubricants.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Lubricants lubricants) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(lubricants);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Lubricants lubricants) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(lubricants);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Lubricants lubricants) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(lubricants);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Lubricants updatedLubricants) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Lubricants lubricants = session.get(Lubricants.class, articul);
            if (lubricants != null) {
                lubricants.setSubcategory(updatedLubricants.getSubcategory());
                lubricants.setVendor(updatedLubricants.getVendor());
                lubricants.setViscosity(updatedLubricants.getViscosity());
                lubricants.setTemperature(updatedLubricants.getTemperature());
                lubricants.setVolume(updatedLubricants.getVolume());
                lubricants.setViscosity(updatedLubricants.getViscosity());
                lubricants.setExpiration(updatedLubricants.getExpiration());
                session.update(lubricants);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
