package com.warehouse.dao;

import com.warehouse.entities.Client;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ClientDAO {

    public static int getClientIdByUsername(String username) {
        int clientId = -1;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c.clientId " +
                         "FROM Client c " +
                         "JOIN c.account a " +
                         "WHERE a.username = :username";

            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("username", username);
            clientId = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientId;
    }


    public boolean save(Client client) {
        try {
            // Assuming you are using Hibernate or any ORM to save
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(client);
            session.getTransaction().commit();
            session.close();
            return true;  // Return true if save is successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if an exception occurs
        }
    }

    public Client findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Client.class, id);
        }
    }

    public List<Client> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client", Client.class).list();
        }
    }

    public void update(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
