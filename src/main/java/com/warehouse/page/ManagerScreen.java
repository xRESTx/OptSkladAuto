package com.warehouse.page;

import com.warehouse.page.adminPage.*;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import java.awt.*;

public class ManagerScreen {
    public static void showManagerScreen(String managerName) {
        JFrame frame = new JFrame("Manager Dashboard");
        frame.setSize(600, 750);  // Увеличено окно для удобства
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок приветствия
        JLabel managerLabel = new JLabel("Welcome, " + managerName + "!");
        managerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        managerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(managerLabel, BorderLayout.NORTH);

        // Панель кнопок с 2 столбцами
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 2, 10, 10)); // 9 строк и 2 столбца, с отступами между элементами

        // Метод для создания кнопок с количеством элементов
        addButtonWithLabel(buttonPanel, "Clients", ClientsPage::showClientsPage, getClientsCount());
        addButtonWithLabel(buttonPanel, "Employees", EmployeesPage::showEmployeesPage, getEmployeesCount());
        addButtonWithLabel(buttonPanel, "Orders", OrdersPage::showOrdersPage, getOrdersCount());
        addButtonWithLabel(buttonPanel, "Requests", RequestsPage::showRequestsPage, getRequestsCount());
        addButtonWithLabel(buttonPanel, "Departments", DepartmentsPage::showDepartmentsPage, getDepartmentsCount());
        addButtonWithLabel(buttonPanel, "Products", ProductsPage::showProductsPage, getProductsCount());
        addButtonWithLabel(buttonPanel, "Payments", PaymentsPage::showPaymentsPage, getPaymentsCount());
        addButtonWithLabel(buttonPanel, "Order Items", OrderItemsPage::showOrderItemsPage, getOrderItemsCount());
        addButtonWithLabel(buttonPanel, "Suppliers", SuppliersPage::showSuppliersPage, getSuppliersCount());
        addButtonWithLabel(buttonPanel, "Supplies", SuppliesPage::showSuppliesPage, getSuppliesCount());

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);  // Центрируем окно на экране
        frame.setVisible(true);  // Делаем окно видимым
    }

    private static void addButtonWithLabel(JPanel panel, String buttonText, Runnable action, int count) {
        // Создаем кнопку с фиксированным размером
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(200, 50)); // Фиксированный размер кнопки
        button.addActionListener(e -> action.run());

        // Создаем метку для отображения количества элементов
        JLabel label = new JLabel("Count: " + count);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        // Добавляем кнопку и метку в панель
        panel.add(button);  // Добавляем кнопку
        panel.add(label);   // Добавляем метку под кнопкой
    }


    private static int getClientsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(c) FROM Client c";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private static int getEmployeesCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(e) FROM Employee e";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getOrdersCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(o) FROM Order o";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getRequestsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM Request r";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getDepartmentsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(d) FROM Department d";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getProductsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Product p";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getPaymentsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Payment p";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getOrderItemsCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(oi) FROM OrderItem oi";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getSuppliersCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(s) FROM Supplier s";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getSuppliesCount() {
        // Создаем сессию Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(s) FROM Delivery s";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
