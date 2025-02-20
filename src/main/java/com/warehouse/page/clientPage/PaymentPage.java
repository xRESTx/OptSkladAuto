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

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadPaymentData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
