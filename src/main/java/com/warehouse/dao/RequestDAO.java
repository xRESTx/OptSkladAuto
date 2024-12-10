package com.warehouse.dao;

import com.warehouse.entities.Client;
import com.warehouse.entities.Order;
import com.warehouse.entities.Request;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class RequestDAO {

    public void save(Request request) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public void createRequest(Client client, Order order, String description) {
        RequestDAO requestDAO = new RequestDAO();

        Request request = new Request();
        request.setClient(client);
        request.setDescription(description);
        request.setStatus("Open"); // Устанавливаем статус "Открыто"
        request.setCreationDate(LocalDate.now());

        requestDAO.save(request);
        System.out.println("Обращение успешно создано!");
    }

    public Request findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Request.class, id);
        }
    }

    public List<Request> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Request", Request.class).list();
        }
    }
    public List<Request> findRequestsByOrderId(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Request> query = session.createQuery("FROM Request r WHERE r.order.id = :orderId", Request.class);
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }


    public void update(Request request) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Request request) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
