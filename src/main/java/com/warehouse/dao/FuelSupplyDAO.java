package com.warehouse.dao;

import com.warehouse.models.FuelSupply;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FuelSupplyDAO {

    public void saveFuelSupply(FuelSupply fuelSupply) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(fuelSupply);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public FuelSupply getFuelSupplyById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(FuelSupply.class, id);
        }
    }

    public List<FuelSupply> getAllFuelSupplies() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM FuelSupply", FuelSupply.class).list();
        }
    }

    public void updateFuelSupply(FuelSupply fuelSupply) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(fuelSupply);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteFuelSupply(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            FuelSupply fuelSupply = session.get(FuelSupply.class, id);
            if (fuelSupply != null) {
                session.delete(fuelSupply);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
