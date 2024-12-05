package com.warehouse.dao;

import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;
import com.warehouse.entities.Product;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class OrderItemDAO {

    private SessionFactory sessionFactory;
    private Session session;
    public OrderItemDAO(Session session) {
        this.session = session;
    }
    public OrderItemDAO() {
        // Конфигурация и создание фабрики сессий
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(OrderItem.class)
                .buildSessionFactory();
    }

    // Метод для получения списка товаров по ID заказа
    public List<OrderItem> findByOrderId(int orderId) {
        // Открываем сессию
        Session session = sessionFactory.openSession();

        try {
            // Выполняем запрос
            List<OrderItem> orderItems = session.createQuery("FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class)
                    .setParameter("orderId", orderId)
                    .getResultList();
            return orderItems;
        } finally {
            session.close(); // Закрываем сессию
        }
    }


    public void save(OrderItem orderItem) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(orderItem);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public OrderItem findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(OrderItem.class, id);
        }
    }

    public List<OrderItem> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM OrderItem", OrderItem.class).list();
        }
    }

    public void update(OrderItem orderItem) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(orderItem);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(OrderItem orderItem) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(orderItem);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public List<OrderItem> getItemsByOrder(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM OrderItem oi WHERE oi.order = :order";
            return session.createQuery(hql, OrderItem.class)
                    .setParameter("order", order)
                    .list();
        }
    }

    // Метод добавления элемента в заказ (оставим без изменений)
    public OrderItem addItemToOrder(Order order, int productId, int quantity) {
        if (order == null) {
            System.out.println("Order is null.");
            return null;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Проверяем, сохранен ли заказ
            if (order.getId() == 0) {
                session.save(order); // Если заказ новый, сохраняем его
            }

            // Загружаем продукт
            Product product = session.get(Product.class, productId);
            if (product == null) {
                System.out.println("Product not found for ID: " + productId);
                return null;
            }

            if (quantity <= 0) {
                System.out.println("Invalid quantity: " + quantity);
                return null;
            }

            if (product.getStockQuantity() < quantity) {
                System.out.println("Not enough stock available for product " + productId);
                return null;
            }

            // Создаем новый элемент заказа
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Связываем элемент с заказом
            orderItem.setProduct(product); // Устанавливаем продукт
            orderItem.setQuantity(quantity); // Устанавливаем количество

            // Сохраняем элемент заказа
            session.save(orderItem);

            // Обновляем количество товара на складе
            product.setStockQuantity(product.getStockQuantity() - quantity);
            session.update(product);

            transaction.commit(); // Завершаем транзакцию
            System.out.println("Item added to order: " + orderItem.getId());
            return orderItem;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


