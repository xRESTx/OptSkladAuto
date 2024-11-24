package com.warehouse.dao;

import com.warehouse.models.Autotovar;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AutotovarDAO {

    public void save(Autotovar autotovar) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.save(autotovar);  // Сохраняем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public Autotovar findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Autotovar.class, articul);  // Получаем объект по идентификатору
        }
    }

    public List<Autotovar> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Autotovar", Autotovar.class).list();  // Получаем все объекты
        }
    }

    public void update(Autotovar autotovar) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.update(autotovar);  // Обновляем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public void delete(Autotovar autotovar) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.delete(autotovar);  // Удаляем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }
}
