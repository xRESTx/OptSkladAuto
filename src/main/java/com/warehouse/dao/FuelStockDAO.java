package com.warehouse.dao;

import com.warehouse.models.FuelStock;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FuelStockDAO {

    public void saveFuelStock(FuelStock fuelStock) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(fuelStock);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public FuelStock getFuelStockById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(FuelStock.class, id);
        }
    }

    public List<FuelStock> getAllFuelStocks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM FuelStock", FuelStock.class).list();
        }
    }

    public void updateFuelStock(FuelStock fuelStock) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(fuelStock);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteFuelStock(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            FuelStock fuelStock = session.get(FuelStock.class, id);
            if (fuelStock != null) {
                session.delete(fuelStock);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
