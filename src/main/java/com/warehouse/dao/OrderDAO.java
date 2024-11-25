package com.warehouse.dao;

import com.warehouse.models.OrderComponent;
import com.warehouse.models.Order;
import com.warehouse.models.Employee;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.warehouse.models.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class OrderDAO {
    // Получение заказа по номеру
    public static Order findOrderByNumber(int orderNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Order o WHERE o.numberOrder = :numberOrder";
            Query<Order> query = session.createQuery(hql, Order.class);
            query.setParameter("numberOrder", orderNumber);
            return query.uniqueResult(); // Получаем единственный результат
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    // Получение заказа по ID
    public static Order findOrderById(int numberOrder) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Order.class, numberOrder);
        }
    }

    // Получение товаров, связанных с заказом
    public static List<OrderComponent> findOrderItems(int numberOrder) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM OrderComponent oc WHERE oc.order.numberOrder = :numberOrder";
            Query<OrderComponent> query = session.createQuery(hql, OrderComponent.class);
            query.setParameter("numberOrder", numberOrder);
            return query.getResultList();
        }
    }

    // Получение всех заказов
    public static List<Order> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order", Order.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving orders.");
        }
    }

    // Получение отфильтрованных заказов по логину или другим параметрам
    public static List<Order> filterOrders(String searchTerm) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String queryString = "FROM Order WHERE account LIKE :searchTerm";
            Query<Order> query = session.createQuery(queryString, Order.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            return query.getResultList();
        }
    }

    // Удаление заказа
    public static void deleteOrder(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                session.delete(order);
                session.getTransaction().commit();
            }
        }
    }

    // Получение всех логинов сотрудников
    public static String[] getAllEmployeeLogins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT e.employeeLogin FROM Employee e";
            Query<String> query = session.createQuery(hql, String.class);
            List<String> resultList = query.getResultList();
            return resultList.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    // Получение сотрудника по логину
    public static Employee findEmployeeByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE e.employeeLogin = :login";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("login", login);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Обновление данных заказа
    public static void updateOrder(Order updatedOrder) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Обновляем дату заказа, если она была изменена
            if (updatedOrder.getDateOrder() != null) {
                // Устанавливаем новую дату
                updatedOrder.setDateOrder(updatedOrder.getDateOrder());
            }

            // Обновляем остальные поля заказа
            session.update(updatedOrder); // Обновляем заказ в базе данных
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating order.");
        }
    }

    public static List<Order> sortOrders(String column, boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            String queryString = "FROM Order o ORDER BY o." + column + " " + order;

            Query<Order> query = session.createQuery(queryString, Order.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sorting orders by " + column, e);
        }
    }
    public static List<Order> findOrdersByUserLogin(String userLogin) {
        // Получаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Создаем HQL запрос для поиска заказов по логину пользователя
            String hql = "FROM Order o WHERE o.account.login = :userLogin"; // :userLogin - параметр запроса

            // Выполняем запрос
            Query<Order> query = session.createQuery(hql, Order.class);
            query.setParameter("userLogin", userLogin); // Устанавливаем значение для параметра

            // Получаем список заказов
            List<Order> orders = query.getResultList();
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Возвращаем null в случае ошибки
        }
    }
    public static int createOrder(Order order) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        int orderId = 0;

        try {
            transaction = session.beginTransaction();
            orderId = (Integer) session.save(order); // Сохраняем заказ и получаем его ID
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return orderId; // Возвращаем ID нового заказа
    }
}
