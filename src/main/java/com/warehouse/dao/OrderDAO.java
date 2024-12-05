package com.warehouse.dao;

import com.warehouse.entities.Client;
import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class OrderDAO {

    public void save(Order order) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Order findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Order.class, id);
        }
    }

    public List<Order> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order", Order.class).list();
        }
    }

    public void update(Order order) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(order);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Order order) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(order);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public Order createOrder(int clientId) {
        // Используем try-with-resources для автоматического закрытия сессии
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Order order = null;

            try {
                // Получаем клиента из базы данных
                Client client = getClientById(clientId);
                if (client == null) {
                    throw new RuntimeException("Client not found for ID: " + clientId);
                }

                // Создаем заказ
                order = new Order();
                order.setClient(client); // Устанавливаем клиента в заказ
                order.setOrderDate(LocalDate.now()); // Устанавливаем текущую дату
                order.setStatus("NEW"); // Устанавливаем статус заказа (например, "NEW")

                session.save(order); // Сохраняем заказ
                transaction.commit(); // Завершаем транзакцию

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback(); // Откат транзакции в случае ошибки
                }
                throw new RuntimeException("Error while creating order", e); // Ретранслируем исключение с подробной информацией
            }

            return order; // Возвращаем заказ
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Возвращаем null в случае ошибки
        }
    }
    public Client getClientById(int clientId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Client client = null;
        try {
            client = session.get(Client.class, clientId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return client;
    }

    public List<Order> getOrdersByClientId(int clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order o WHERE o.client.id = :clientId", Order.class)
                    .setParameter("clientId", clientId)
                    .getResultList();
        }
    }
    public List<Order> getOrdersByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order WHERE status = :status", Order.class)
                    .setParameter("status", status)
                    .list();
        }
    }
    public OrderItem addItemToOrder(Order order, int productId, int quantity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Создаем новый элемент заказа
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Устанавливаем объект заказа
            orderItem.setId(productId);
            orderItem.setQuantity(quantity);

            session.save(orderItem);
            transaction.commit();

            return orderItem; // Возвращаем созданный элемент
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null; // Возвращаем null в случае ошибки
        }
    }


    public void updateOrderStatus(int orderId, String newStatus) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public long countOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(o) FROM Order o", Long.class).uniqueResult();
        }
    }

    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order WHERE orderDate BETWEEN :startDate AND :endDate", Order.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .list();
        }
    }

    public void deleteOrdersByClientId(int clientId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Order WHERE clientId = :clientId")
                    .setParameter("clientId", clientId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
