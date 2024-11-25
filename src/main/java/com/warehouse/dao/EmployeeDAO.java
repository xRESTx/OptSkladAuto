package com.warehouse.dao;

import com.warehouse.models.*;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeDAO {

    public static void save(Employee employee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(employee);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Employee employee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(employee);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Employee employee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(employee);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Employee getEmployeeByLogin(String login) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Employee employee = null;
        try {
            employee = (Employee) session.createQuery("FROM Employee WHERE employeeLogin = :login")
                    .setParameter("login", login)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return employee;
    }

    public static List<Contract> loadContracts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Contract", Contract.class).getResultList();
        }
    }

    public static List<Department> loadDepartments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL запрос для получения всех департаментов
            return session.createQuery("FROM Department", Department.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error loading departments: " + e.getMessage(), e);
        }
    }

    public static void saveOrUpdate(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(employee);
            session.getTransaction().commit();
        }
    }

    public static List<Employee> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee", Employee.class).getResultList();
        }
    }

    public static Employee findById(int passportData) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Employee.class, passportData);
        }
    }

    public static void deleteEmployee(int passportData) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Employee emp = session.get(Employee.class, passportData);
            if (emp != null) {
                session.delete(emp);
            }
            session.getTransaction().commit();
        }
    }

    public static List<Employee> filterEmployees(String searchTerm) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE " +
                    "CAST(e.passportData AS string) LIKE :searchTerm OR " +
                    "e.employeeLogin LIKE :searchTerm OR " +
                    "e.lastName LIKE :searchTerm OR " +
                    "e.firstName LIKE :searchTerm OR " +
                    "e.middleName LIKE :searchTerm OR " +
                    "CAST(e.birthday AS string) LIKE :searchTerm";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            return query.getResultList();
        }
    }
    public static void addEmployeeWithContract(Employee employee, Contract contract) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Сохраняем контракт
            session.saveOrUpdate(contract);
            employee.setContract(contract);

            // Сохраняем сотрудника
            session.saveOrUpdate(employee);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error adding employee with contract: " + e.getMessage(), e);
        }
    }

}
