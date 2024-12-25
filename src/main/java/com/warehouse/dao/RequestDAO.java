package com.warehouse.dao;

import com.warehouse.models.Request;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RequestDAO {

    public void save(Request request) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static Request findById(int requestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Request.class, requestId);
        }
    }

    public static List<Request> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Request", Request.class).list();
        }
    }

    public static void update(Request request) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void delete(Request request) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(request);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public static void deleteRequestById(int requestId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Получаем запрос по id
            Request request = session.get(Request.class, requestId);
            if (request != null) {
                // Удаляем запрос
                session.delete(request);
                System.out.println("Request with ID " + requestId + " deleted successfully.");
            } else {
                System.out.println("Request with ID " + requestId + " not found.");
            }

            // Завершаем транзакцию
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static List<Request> findRequestsByEmployeeLogin(String employeeLogin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Request WHERE employee.employeeLogin = :login", Request.class)
                    .setParameter("login", employeeLogin)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch requests for employee login: " + employeeLogin);
        }
    }


}
