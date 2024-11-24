package com.warehouse.dao;

import com.warehouse.models.Account;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AccountDAO {

    public void save(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.save(account);  // Сохраняем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public Account findById(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Account.class, login);  // Получаем Account по ID
        }
    }

    public List<Account> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Account", Account.class).list();  // Получаем все записи
        }
    }

    public void update(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.update(account);  // Обновляем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public void delete(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.delete(account);  // Удаляем объект
            transaction.commit();  // Коммитим изменения
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откатываем в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public Account getAccountByLogin(String login) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Account account = null;
        try {
            account = (Account) session.createQuery("FROM Account WHERE login = :login")
                    .setParameter("login", login)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return account;
    }
}
