package com.warehouse.dao;

import com.warehouse.models.FuelType;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FuelTypeDAO {

    public void saveFuelType(FuelType fuelType) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(fuelType);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public FuelType getFuelTypeById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(FuelType.class, id);
        }
    }

    public List<FuelType> getAllFuelTypes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM FuelType", FuelType.class).list();
        }
    }

    public void updateFuelType(FuelType fuelType) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(fuelType);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteFuelType(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            FuelType fuelType = session.get(FuelType.class, id);
            if (fuelType != null) {
                session.delete(fuelType);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
