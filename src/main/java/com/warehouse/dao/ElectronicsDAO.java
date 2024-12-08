package com.warehouse.dao;

import com.warehouse.models.Electronics;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ElectronicsDAO {

    // Метод для получения всех товаров категории Electronics
    public static List<Electronics> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Electronics> query = session.createQuery("from Electronics", Electronics.class);
            return query.list();
        }
    }

    // Метод для получения товара по articul
    public Electronics findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Electronics.class, articul);
        }
    }

    // Метод для добавления нового товара
    public static void save(Electronics electronics) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(electronics);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления данных товара
    public void update(Electronics electronics) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(electronics);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для удаления товара
    public void delete(Electronics electronics) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(electronics);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Метод для обновления конкретных данных товара по articul
    public static void updateRow(int articul, Electronics updatedElectronics) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Electronics electronics = session.get(Electronics.class, articul);
            if (electronics != null) {
                electronics.setSubcategory(updatedElectronics.getSubcategory());
                electronics.setVendor(updatedElectronics.getVendor());
                electronics.setSupported(updatedElectronics.getSupported());
                electronics.setPermission(updatedElectronics.getPermission());
                electronics.setWarranty(updatedElectronics.getWarranty());
                electronics.setConnect(updatedElectronics.getConnect());
                electronics.setScreen(updatedElectronics.getScreen());
                session.update(electronics);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
