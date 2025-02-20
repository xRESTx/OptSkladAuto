package com.warehouse.dao;

import com.warehouse.entities.Client;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ClientDAO {

    public int getClientIdByUsername(String username) {
        int clientId = -1;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c.id FROM Client c WHERE c.account.username = :username";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("username", username);
            clientId = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientId;
    }

    public boolean save(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
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

    public void delete(int clientId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                session.delete(client);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
