package com.warehouse.dao;

import com.warehouse.models.Payment;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PaymentDAO {

    public void save(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static Payment findById(int paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Payment.class, paymentId);
        }
    }

    public static List<Payment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        }
    }

    public void update(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

// Метод для обновления платежа
public static void updatePayment(Payment payment) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        session.beginTransaction();
        session.update(payment);  // Обновление платежа в базе данных
        session.getTransaction().commit();
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error updating payment", e);
    }
}

    public static void deletePayment(int paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Начинаем транзакцию
            Transaction transaction = session.beginTransaction();

            // Получаем платеж по ID
            Payment payment = session.get(Payment.class, paymentId);

            if (payment != null) {
                // Удаляем платеж из базы данных
                session.delete(payment);
            }

            // Завершаем транзакцию
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting payment", e);
        }
    }

    public static List<Payment> filterPayments(String searchTerm) {
        // Открываем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Создаем HQL-запрос с приведением числовых полей к строковому типу
            String hql = "FROM Payment p WHERE " +
                    "CAST(p.paymentId AS string) LIKE :searchTerm OR " +  // Преобразуем paymentId в строку для поиска
                    "CAST(p.order.numberOrder AS string) LIKE :searchTerm OR " + // Преобразуем order.numberOrder в строку для поиска
                    "CAST(p.datePayment AS string) LIKE :searchTerm OR " + // Преобразуем дату в строку для поиска
                    "p.statusPayment LIKE :searchTerm"; // Поиск по статусу платежа

            // Создаем запрос
            Query<Payment> query = session.createQuery(hql, Payment.class);
            // Устанавливаем параметр для поиска
            query.setParameter("searchTerm", "%" + searchTerm + "%"); // Используем % для поиска по подстроке

            // Выполняем запрос и получаем список платежей
            return query.getResultList();
        }
    }


    public static void savePayment(Payment payment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Начинаем транзакцию
            Transaction transaction = session.beginTransaction();

            // Сохраняем новый платеж в базе данных
            session.save(payment);

            // Завершаем транзакцию
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving payment", e);
        }
    }

}
