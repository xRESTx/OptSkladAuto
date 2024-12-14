package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Client;
import com.warehouse.entities.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class OrdersPage {

    public static void showOrdersPage() {
        JFrame frame = new JFrame("Order Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Orders List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных заказов
        String[] columnNames = {"ID", "Client", "Status", "Total Amount", "Order Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable ordersTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления заказа
        JButton addButton = new JButton("Add Order");
        addButton.addActionListener(e -> addOrder(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования заказа
        JButton editButton = new JButton("Edit Order");
        editButton.addActionListener(e -> editOrder(ordersTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления заказа
        JButton deleteButton = new JButton("Delete Order");
        deleteButton.addActionListener(e -> deleteOrder(ordersTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadOrderData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadOrderData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            List<Order> orders = session.createQuery("from Order", Order.class).list();

            for (Order order : orders) {
                tableModel.addRow(new Object[] {
                        order.getId(),
                        order.getClient().getFullName(), // Assuming getFullName() exists in Client class
                        order.getStatus(),
                        order.getAmount(),
                        order.getOrderDate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addOrder(DefaultTableModel tableModel) {
        // Создаем выпадающий список для клиентов
        JComboBox<Client> clientComboBox = new JComboBox<>();

        // Загружаем существующих клиентов из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            List<Client> clients = session.createQuery("from Client", Client.class).list();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField statusField = new JTextField();
        JTextField amountField = new JTextField();

        // Используем JDateChooser для выбора даты
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Формат отображения даты

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Client:"));
        panel.add(clientComboBox);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Total Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Order Date:"));
        panel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Order", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Проверяем, выбран ли клиент
            Client selectedClient = (Client) clientComboBox.getSelectedItem();
            if (selectedClient == null) {
                JOptionPane.showMessageDialog(null, "Please select a client.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = statusField.getText();
            double amount = Double.parseDouble(amountField.getText());

            // Получаем выбранную дату (если дата не выбрана, будет null)
            java.util.Date orderDateUtil = dateChooser.getDate();
            if (orderDateUtil == null) {
                JOptionPane.showMessageDialog(null, "Please select a valid order date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate orderDate = orderDateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем заказ
                Order order = new Order();
                order.setClient(selectedClient);
                order.setStatus(status);
                order.setAmount(amount);
                order.setOrderDate(orderDate);

                session.save(order);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[] {
                        order.getId(),
                        order.getClient().getFullName(),
                        order.getStatus(),
                        order.getAmount(),
                        order.getOrderDate()
                });

                JOptionPane.showMessageDialog(null, "Order added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void editOrder(JTable ordersTable, DefaultTableModel tableModel) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 2);
        String currentAmount = tableModel.getValueAt(selectedRow, 3).toString();

        JTextField statusField = new JTextField(currentStatus);
        JTextField amountField = new JTextField(currentAmount);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Total Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Order", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = statusField.getText();
            double newAmount = Double.parseDouble(amountField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Order.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Order order = session.get(Order.class, orderId);
                if (order != null) {
                    order.setStatus(newStatus);
                    order.setAmount(newAmount);
                    session.update(order);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newStatus, selectedRow, 2);
                    tableModel.setValueAt(newAmount, selectedRow, 3);

                    JOptionPane.showMessageDialog(null, "Order updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deleteOrder(JTable ordersTable, DefaultTableModel tableModel) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this order?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Order.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Получаем заказ по ID и удаляем
                Order order = session.get(Order.class, orderId);
                if (order != null) {
                    session.delete(order);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Order deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
