package com.warehouse.page.clientPage;

import com.warehouse.entities.Payment;
import com.warehouse.entities.Client;
import com.warehouse.entities.Invoice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentPage {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Payment.class)
            .addAnnotatedClass(Client.class)   // Добавим Client, если он используется в запросе
            .addAnnotatedClass(Invoice.class)  // Добавим Invoice, если он используется в запросе
            .buildSessionFactory();

    public static void showPaymentPage(int clientId) {
        JFrame frame = new JFrame("Payment Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Payment List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Invoice ID", "Payment Date", "Amount", "Payment Method"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable paymentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("Edit Payment");
        editButton.addActionListener(e -> editPayment(paymentTable, tableModel));
        buttonPanel.add(editButton); // Добавляем кнопку редактирования

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton); // Кнопка закрытия

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        loadPaymentData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private static void editPayment(JTable paymentTable, DefaultTableModel tableModel) {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) return;

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);

        // Создание диалогового окна
        JDialog editPaymentDialog = new JDialog();
        editPaymentDialog.setTitle("Edit Payment");
        editPaymentDialog.setSize(400, 250);
        editPaymentDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        editPaymentDialog.add(formPanel, BorderLayout.CENTER);

        JLabel dateLabel = new JLabel("Payment Date (yyyy-MM-dd, optional): ");
        JTextField dateField = new JTextField();

        JLabel methodLabel = new JLabel("Payment Method: ");
        JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"Card", "Cash", "Online"});

        JLabel amountLabel = new JLabel("Amount: ");
        JTextField amountField = new JTextField();

        try (Session session = factory.openSession()) {
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                dateField.setText(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "");  // Обрабатываем null
                methodComboBox.setSelectedItem(payment.getPaymentMethod());
                amountField.setText(String.valueOf(payment.getAmount()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading payment details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(methodLabel);
        formPanel.add(methodComboBox);
        formPanel.add(amountLabel);
        formPanel.add(amountField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String dateText = dateField.getText().trim();
            LocalDate paymentDate = dateText.isEmpty() ? null : LocalDate.parse(dateText);  // Позволяет null
            String paymentMethod = methodComboBox.getSelectedItem().toString();
            double paymentAmount;

            try {
                paymentAmount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid amount format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Session session = factory.openSession()) {
                session.beginTransaction();
                Payment payment = session.get(Payment.class, paymentId);
                if (payment != null) {
                    payment.setPaymentDate(paymentDate);
                    payment.setPaymentMethod(paymentMethod);
                    payment.setAmount(paymentAmount);

                    session.update(payment);
                    session.getTransaction().commit();

                    // Обновляем данные в таблице
                    tableModel.setValueAt(paymentDate != null ? paymentDate.toString() : "N/A", selectedRow, 3);
                    tableModel.setValueAt(paymentMethod, selectedRow, 4);
                    tableModel.setValueAt(paymentAmount, selectedRow, 5);

                    JOptionPane.showMessageDialog(null, "Payment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    editPaymentDialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error updating payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> editPaymentDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        editPaymentDialog.add(buttonPanel, BorderLayout.SOUTH);

        editPaymentDialog.setLocationRelativeTo(null);
        editPaymentDialog.setModal(true);
        editPaymentDialog.setVisible(true);
    }
    private static void loadPaymentData(int clientId, DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            // Запрос с фильтрацией по clientId
            List<Payment> payments = session.createQuery(
                            "SELECT p " +
                                    "FROM Payment p " +
                                    "JOIN Client c ON c.id = p.client.id "+
                                    "WHERE c.id = :clientId", Payment.class)
                    .setParameter("clientId", clientId)
                    .list();

            for (Payment payment : payments) {
                tableModel.addRow(new Object[]{
                        payment.getId(),
                        payment.getInvoice().getId(), // Получаем ID счета из Invoice
                        payment.getPaymentDate(),
                        payment.getAmount(),
                        payment.getPaymentMethod()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
