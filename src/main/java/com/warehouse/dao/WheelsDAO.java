package com.warehouse.dao;

import com.warehouse.models.Wheels;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class WheelsDAO {

    // Метод для получения всех товаров категории Wheels
    public static List<Wheels> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Wheels> query = session.createQuery("from Wheels", Wheels.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Wheels findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Wheels.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Wheels wheels) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(wheels);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Wheels wheels) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(wheels);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Wheels wheels) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(wheels);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Wheels updatedWheels) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Wheels wheels = session.get(Wheels.class, articul);
            if (wheels != null) {
                wheels.setSubcategory(updatedWheels.getSubcategory());
                wheels.setVendor(updatedWheels.getVendor());
                wheels.setMaterial(updatedWheels.getMaterial());
                wheels.setSize(updatedWheels.getSize());
                wheels.setSeasonality(updatedWheels.getSeasonality());
                wheels.setColour(updatedWheels.getColour());
                wheels.setProtector(updatedWheels.getProtector());
                session.update(wheels);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Wheels findByArticul(int articul) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Wheels> query = session.createQuery("FROM Wheels w WHERE w.articul = :articul", Wheels.class);
            query.setParameter("articul", articul);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
