package com.warehouse.dao;

import com.warehouse.models.Autotovar;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AutotovarDAO {

    public static void save(Autotovar autotovar) {
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

    public static Autotovar findById(int articul) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Autotovar.class, articul);  // Получаем объект по идентификатору
        }
    }

    public static List<Autotovar> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Autotovar", Autotovar.class).list();  // Получаем все объекты
        }
    }

    public static void update(Autotovar autotovar) {
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

    public static void deleteProduct(int articul) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Используем HQL для удаления записи
            String hql = "DELETE FROM Autotovar WHERE articul = :articul";
            session.createQuery(hql)
                    .setParameter("articul", articul)
                    .executeUpdate();

            transaction.commit();
            System.out.println("Product deleted successfully: Articul = " + articul);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Error occurred while deleting product: Articul = " + articul, e);
        }
    }


    public static List<Autotovar> sortProducts(String column, boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            String queryString = "FROM Autotovar ORDER BY " + column + " " + order;

            Query<Autotovar> query = session.createQuery(queryString, Autotovar.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sorting products by " + column, e);
        }
    }




    public static List<Autotovar> filterProducts(String searchTerm) {
        List<Autotovar> filteredProducts = null;

        // Открываем новую сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Используем HQL для поиска продуктов по имени
            String hql = "FROM Autotovar WHERE name LIKE :searchTerm";
            Query<Autotovar> query = session.createQuery(hql, Autotovar.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%"); // Подстановка параметра поиска

            // Выполняем запрос и получаем список отфильтрованных продуктов
            filteredProducts = query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибок
        }

        return filteredProducts;
    }

    public static Autotovar getProductByArticul(String articul) {
        Autotovar product = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            product = session.get(Autotovar.class, Integer.parseInt(articul));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return product;
    }


    public static void addProduct(Autotovar product) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Начинаем транзакцию
            transaction = session.beginTransaction();

            // Сохраняем новый объект
            session.save(product);

            // Подтверждаем транзакцию
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Откат транзакции в случае ошибки
            }
            e.printStackTrace();
        }
    }

    public static void updateProduct(Autotovar product) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(product);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Autotovar> findAllByCategory(String category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Autotovar> query = session.createQuery("FROM Autotovar WHERE category = :category", Autotovar.class);
            query.setParameter("category", category);
            return query.getResultList();
        }
    }

}
