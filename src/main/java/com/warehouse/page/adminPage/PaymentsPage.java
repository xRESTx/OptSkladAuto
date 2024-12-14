package com.warehouse.page.adminPage;

import com.warehouse.entities.Payment;
import com.warehouse.entities.Client;
import com.warehouse.entities.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentsPage {

    public static void showPaymentsPage() {
        JFrame frame = new JFrame("Payment Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Payment List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных платежей
        String[] columnNames = {"ID", "Client", "Order", "Amount", "Payment Date", "Method", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable paymentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления платежа
        JButton addButton = new JButton("Add Payment");
        addButton.addActionListener(e -> addPayment(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования платежа
        JButton editButton = new JButton("Edit Payment");
        editButton.addActionListener(e -> editPayment(paymentTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления платежа
        JButton deleteButton = new JButton("Delete Payment");
        deleteButton.addActionListener(e -> deletePayment(paymentTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadPaymentData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadPaymentData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех платежей из базы данных
            List<Payment> payments = session.createQuery("from Payment", Payment.class).list();

            // Добавление данных в таблицу
            for (Payment payment : payments) {
                tableModel.addRow(new Object[]{
                        payment.getId(),
                        payment.getClient().getFullName(),
                        payment.getOrder().getId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getPaymentMethod(),
                        payment.getStatus()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addPayment(DefaultTableModel tableModel) {
        // Создаем выпадающий список для клиентов
        JComboBox<Client> clientComboBox = new JComboBox<>();

        // Создаем выпадающий список для заказов
        JComboBox<Order> orderComboBox = new JComboBox<>();

        // Загружаем существующих клиентов и заказы
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(Payment.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            // Загружаем клиентов
            List<Client> clients = session.createQuery("from Client", Client.class).list();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }

            // Загружаем заказы
            List<Order> orders = session.createQuery("from Order", Order.class).list();
            for (Order order : orders) {
                orderComboBox.addItem(order);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading clients or orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Поля для других данных
        JTextField amountField = new JTextField();
        JTextField paymentDateField = new JTextField();
        JTextField paymentMethodField = new JTextField();
        JTextField statusField = new JTextField();

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Client:"));
        panel.add(clientComboBox);
        panel.add(new JLabel("Order:"));
        panel.add(orderComboBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Payment Date:"));
        panel.add(paymentDateField);
        panel.add(new JLabel("Payment Method:"));
        panel.add(paymentMethodField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Payment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Client selectedClient = (Client) clientComboBox.getSelectedItem();
            Order selectedOrder = (Order) orderComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            String paymentDate = paymentDateField.getText();
            String paymentMethod = paymentMethodField.getText();
            String status = statusField.getText();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем новый платеж
                Payment payment = new Payment();
                payment.setClient(selectedClient);
                payment.setOrder(selectedOrder);
                payment.setAmount(amount);
                payment.setPaymentDate(paymentDate.isEmpty() ? null : LocalDate.parse(paymentDate));
                payment.setPaymentMethod(paymentMethod);
                payment.setStatus(status);

                session.save(payment);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[]{
                        payment.getId(),
                        selectedClient.getFullName(),
                        selectedOrder.getId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getPaymentMethod(),
                        payment.getStatus()
                });

                JOptionPane.showMessageDialog(null, "Payment added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        factory.close();
    }

    private static void editPayment(JTable paymentTable, DefaultTableModel tableModel) {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a payment to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentAmount = String.valueOf(tableModel.getValueAt(selectedRow, 3));
        String currentPaymentDate = String.valueOf(tableModel.getValueAt(selectedRow, 4));
        String currentPaymentMethod = (String) tableModel.getValueAt(selectedRow, 5);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        JTextField amountField = new JTextField(currentAmount);
        JTextField paymentDateField = new JTextField(currentPaymentDate);
        JTextField paymentMethodField = new JTextField(currentPaymentMethod);
        JTextField statusField = new JTextField(currentStatus);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Payment Date:"));
        panel.add(paymentDateField);
        panel.add(new JLabel("Payment Method:"));
        panel.add(paymentMethodField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Payment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            double newAmount = Double.parseDouble(amountField.getText());
            String newPaymentDate = paymentDateField.getText();
            String newPaymentMethod = paymentMethodField.getText();
            String newStatus = statusField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Payment.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Payment payment = session.get(Payment.class, paymentId);
                if (payment != null) {
                    payment.setAmount(newAmount);
                    payment.setPaymentDate(newPaymentDate.isEmpty() ? null : LocalDate.parse(newPaymentDate));
                    payment.setPaymentMethod(newPaymentMethod);
                    payment.setStatus(newStatus);
                    session.update(payment);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newAmount, selectedRow, 3);
                    tableModel.setValueAt(newPaymentDate, selectedRow, 4);
                    tableModel.setValueAt(newPaymentMethod, selectedRow, 5);
                    tableModel.setValueAt(newStatus, selectedRow, 6);

                    JOptionPane.showMessageDialog(null, "Payment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            factory.close();
        }
    }

    private static void deletePayment(JTable paymentTable, DefaultTableModel tableModel) {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a payment to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this payment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Payment.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Payment payment = session.get(Payment.class, paymentId);
                if (payment != null) {
                    session.delete(payment);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Payment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            factory.close();
        }
    }
}
