package com.warehouse.dao;

import com.warehouse.models.Chemistry;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ChemistryDAO {

    // Метод для получения всех товаров категории Chemistry
    public static List<Chemistry> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Chemistry> query = session.createQuery("from Chemistry", Chemistry.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Chemistry findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Chemistry.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Chemistry chemistry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chemistry);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Chemistry chemistry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(chemistry);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Chemistry chemistry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(chemistry);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Chemistry updatedChemistry) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Chemistry chemistry = session.get(Chemistry.class, articul);
            if (chemistry != null) {
                // Обновляем поля объекта на основе данных из updatedChemistry
                chemistry.setSubcategory(updatedChemistry.getSubcategory());
                chemistry.setVendor(updatedChemistry.getVendor());
                chemistry.setCompositions(updatedChemistry.getCompositions());
                chemistry.setConcentration(updatedChemistry.getConcentration());
                chemistry.setTarget(updatedChemistry.getTarget());
                chemistry.setVolume(updatedChemistry.getVolume());
                chemistry.setExpirationDate(updatedChemistry.getExpirationDate());
                session.update(chemistry);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Chemistry findByArticul(int articul) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Chemistry> query = session.createQuery("FROM Chemistry c WHERE c.articul = :articul", Chemistry.class);
            query.setParameter("articul", articul);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
