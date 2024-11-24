package com.warehouse.dao;

import com.warehouse.models.OrderComponent;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class OrderComponentDAO {
    public static List<OrderComponent> findComponentsByOrderNumber(String orderNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM OrderComponent oc WHERE oc.order.numberOrder = :numberOrder";
            Query<OrderComponent> query = session.createQuery(hql, OrderComponent.class);
            query.setParameter("numberOrder", orderNumber);
            return query.list(); // Возвращаем список всех компонентов заказа
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    public void save(OrderComponent orderComponent) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(orderComponent);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public OrderComponent findById(int numberOrder) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(OrderComponent.class, numberOrder);
        }
    }

    public List<OrderComponent> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM OrderComponent", OrderComponent.class).list();
        }
    }

    public void update(OrderComponent orderComponent) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(orderComponent);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(OrderComponent orderComponent) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(orderComponent);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
