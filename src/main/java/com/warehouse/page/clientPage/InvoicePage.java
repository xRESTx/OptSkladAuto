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
import java.time.LocalDate;
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

        JButton editButton = new JButton("Edit Invoice");
        editButton.addActionListener(e -> editInvoice(invoiceTable, tableModel));
        buttonPanel.add(editButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadInvoiceData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private static void editInvoice(JTable invoiceTable, DefaultTableModel tableModel) {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an invoice to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId = (int) tableModel.getValueAt(selectedRow, 0);

        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Invoice");
        editDialog.setSize(400, 350);
        editDialog.setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2));

        JComboBox<String> serviceComboBox = new JComboBox<>();
        JComboBox<String> tariffComboBox = new JComboBox<>();
        JComboBox<String> readingComboBox = new JComboBox<>();
        JTextField billingPeriodField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());
        JTextField statusField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());

        // Подгружаем данные в JComboBox
        try (Session session = factory.openSession()) {
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice == null) {
                JOptionPane.showMessageDialog(null, "Invoice not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Service> services = session.createQuery("from Service", Service.class).list();
            for (Service service : services) {
                serviceComboBox.addItem(service.getName());
            }
            serviceComboBox.setSelectedItem(invoice.getService().getName());

            List<Tariff> tariffs = session.createQuery("from Tariff", Tariff.class).list();
            for (Tariff tariff : tariffs) {
                tariffComboBox.addItem("ID: " + tariff.getId() + " - " + tariff.getRate());
            }
            tariffComboBox.setSelectedItem("ID: " + invoice.getTariff().getId() + " - " + invoice.getTariff().getRate());

            List<MeterReading> readings = session.createQuery("from MeterReading", MeterReading.class).list();
            for (MeterReading reading : readings) {
                readingComboBox.addItem("ID: " + reading.getId() + " - " + reading.getReadingValue());
            }
            readingComboBox.setSelectedItem("ID: " + invoice.getReading().getId() + " - " + invoice.getReading().getReadingValue());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        formPanel.add(new JLabel("Service: "));
        formPanel.add(serviceComboBox);
        formPanel.add(new JLabel("Tariff: "));
        formPanel.add(tariffComboBox);
        formPanel.add(new JLabel("Reading: "));
        formPanel.add(readingComboBox);
        formPanel.add(new JLabel("Billing Period: "));
        formPanel.add(billingPeriodField);
        formPanel.add(new JLabel("Status: "));
        formPanel.add(statusField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String serviceName = (String) serviceComboBox.getSelectedItem();
            String tariffInfo = (String) tariffComboBox.getSelectedItem();
            String readingInfo = (String) readingComboBox.getSelectedItem();
            LocalDate billingPeriod = LocalDate.parse(billingPeriodField.getText());
            String status = statusField.getText();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Invoice invoice = session.get(Invoice.class, invoiceId);
                if (invoice == null) {
                    JOptionPane.showMessageDialog(null, "Invoice not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Service service = session.createQuery("from Service where name = :name", Service.class)
                        .setParameter("name", serviceName)
                        .uniqueResult();

                int tariffId = Integer.parseInt(tariffInfo.split(" ")[1]);
                Tariff tariff = session.get(Tariff.class, tariffId);

                int readingId = Integer.parseInt(readingInfo.split(" ")[1]);
                MeterReading reading = session.get(MeterReading.class, readingId);

                if (service == null || tariff == null || reading == null) {
                    JOptionPane.showMessageDialog(null, "Invalid selection", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                invoice.setService(service);
                invoice.setTariff(tariff);
                invoice.setReading(reading);
                invoice.setBillingPeriod(billingPeriod);
                invoice.setStatus(status);

                session.update(invoice);
                session.getTransaction().commit();

                tableModel.setValueAt(service.getName(), selectedRow, 1);
                tableModel.setValueAt(tariff.getRate(), selectedRow, 2);
                tableModel.setValueAt(reading.getReadingValue(), selectedRow, 3);
                tableModel.setValueAt(billingPeriod, selectedRow, 4);
                tableModel.setValueAt(status, selectedRow, 5);

                editDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error updating invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> editDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(null);
        editDialog.setModal(true);
        editDialog.setVisible(true);
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
