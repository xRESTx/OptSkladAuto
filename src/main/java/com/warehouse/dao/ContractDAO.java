package com.warehouse.dao;

import com.warehouse.models.*;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.List;

public class ContractDAO {

    public void save(Contract contract) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(contract);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static Contract findById(int contractNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Contract.class, contractNumber);
        }
    }

    public static List<Contract> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Contract", Contract.class).list();
        }
    }

    public static void updateContract(Contract contract) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.update(contract);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void delete(Contract contract) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(contract);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void saveOrUpdate(Contract contract) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(contract);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public static Employee getEmployeeByContract(int contractNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Employee employee = null;
        try {
            // Создаем запрос, чтобы найти сотрудника по номеру контракта
            Query query = session.createQuery("FROM Employee e WHERE e.contract.contractNumber = :contractNumber");
            query.setParameter("contractNumber", contractNumber);

            employee = (Employee) ((org.hibernate.query.Query<?>) query).uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return employee;
    }
    public static boolean deleteContractById(int contractId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Сначала получаем контракт по ID
            Contract contract = session.get(Contract.class, contractId);

            if (contract == null) {
                // Если контракт не найден
                System.out.println("Contract not found!");
                return false;
            }

            // Проверяем, связан ли контракт с сотрудником
            Employee employee = getEmployeeByContract(contract.getContractNumber());
            if (employee != null) {
                // Если контракт связан с сотрудником, не разрешаем удаление
                System.out.println("Contract cannot be deleted because it's associated with an employee.");
                return false;
            }

            // Если контракт не связан с сотрудником, удаляем его
            session.delete(contract);
            transaction.commit();
            System.out.println("Contract deleted successfully!");
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
