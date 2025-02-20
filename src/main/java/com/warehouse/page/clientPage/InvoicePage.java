package com.warehouse.page.clientPage;

import com.warehouse.entities.Invoice;
import com.warehouse.entities.Service;
import com.warehouse.entities.Tariff;
import com.warehouse.entities.MeterReading;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoicePage {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Invoice.class)
            .addAnnotatedClass(Service.class)
            .addAnnotatedClass(Tariff.class)
            .addAnnotatedClass(MeterReading.class)
            .buildSessionFactory();

    public static void showInvoicePage(int clientId) {
        JFrame frame = new JFrame("Invoice Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Invoice List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Service", "Tariff", "Reading", "Billing Period", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable invoiceTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadInvoiceData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadInvoiceData(int clientId, DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            // Query that joins Invoices with Meters and Clients, filtering by clientId
            List<Invoice> invoices = session.createQuery(
                            "SELECT i " +
                                    "FROM Invoice i " +
                                    "JOIN Payment p ON p.invoice.id = i.id " +
                                    "JOIN Client c ON c.id = p.client.id " +
                                    "WHERE c.id = :clientId", Invoice.class)  // Используем id вместо customer_id
                    .setParameter("clientId", clientId)
                    .list();
            for (Invoice invoice : invoices) {
                tableModel.addRow(new Object[]{
                        invoice.getId(),
                        invoice.getService().getName(),
                        invoice.getTariff().getRate(),
                        invoice.getReading() != null ? invoice.getReading().getReadingValue() : "N/A",
                        invoice.getBillingPeriod(),
                        invoice.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading invoices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
