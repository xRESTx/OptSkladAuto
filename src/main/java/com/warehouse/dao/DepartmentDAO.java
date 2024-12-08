package com.warehouse.dao;

import com.warehouse.models.Department;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DepartmentDAO {

    public void save(Department department) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(department);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static Department findById(int departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Department.class, departmentId);
        }
    }

    public static List<Department> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Department", Department.class).list();
        }
    }

    public static void update(Department department) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(department);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void delete(Department department) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(department);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void deleteDepartmentById(int departmentId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Проверка, привязан ли департамент к сотрудникам
            long count = (long) session.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
                    .setParameter("departmentId", departmentId)
                    .uniqueResult();

            if (count > 0) {
                // Если департамент привязан к сотрудникам, то не удаляем его
                System.out.println("Cannot delete department. It has linked employees.");
                return; // Возвращаемся, не удаляя департамент
            }

            // Получаем департамент по ID
            Department department = session.get(Department.class, departmentId);
            if (department != null) {
                // Удаляем департамент
                session.delete(department);
                System.out.println("Department deleted successfully.");
            } else {
                System.out.println("Department not found.");
            }

            // Завершаем транзакцию
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addDepartment(Department department) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Сохраняем новый департамент в базе данных
            session.save(department);

            // Завершаем транзакцию
            transaction.commit();

            System.out.println("Department added successfully.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
