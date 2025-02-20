package com.warehouse.page.adminPage;

import com.warehouse.entities.Client;
import com.warehouse.entities.Invoice;
import com.warehouse.entities.Payment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentPage {
    private static final SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Payment.class)
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(Invoice.class)
            .buildSessionFactory();

    public static void showPaymentPage() {
        JFrame frame = new JFrame("Payment Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Payment List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Client", "Invoice", "Date", "Method", "Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable paymentTable = new JTable(tableModel);
        mainPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Payment");
        addButton.addActionListener(e -> addPayment(tableModel));
        JButton editButton = new JButton("Edit Payment");
        editButton.addActionListener(e -> editPayment(paymentTable, tableModel));
        JButton deleteButton = new JButton("Delete Payment");
        deleteButton.addActionListener(e -> deletePayment(paymentTable, tableModel));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadPayments(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadPayments(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            List<Payment> payments = session.createQuery("from Payment", Payment.class).list();
            for (Payment payment : payments) {
                tableModel.addRow(new Object[]{
                        payment.getId(),
                        payment.getClient().getFullName(),
                        "Invoice #" + payment.getInvoice().getId(),
                        payment.getPaymentDate(),
                        payment.getPaymentMethod(),
                        payment.getAmount()
                });
            }
        }
    }

    private static void addPayment(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Payment");
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2));

        JComboBox<String> clientComboBox = new JComboBox<>();
        JComboBox<String> invoiceComboBox = new JComboBox<>();
        JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"Card", "Cash", "Online"});
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField amountField = new JTextField();

        try (Session session = factory.openSession()) {
            for (Client client : session.createQuery("from Client", Client.class).list()) {
                clientComboBox.addItem(client.getFullName());
            }
            List<Invoice> invoices = session.createQuery("from Invoice i where not exists (from Payment p where p.invoice = i)", Invoice.class).list();
            for (Invoice invoice : invoices) {
                invoiceComboBox.addItem("Invoice #" + invoice.getId());
            }
        }

        dialog.add(new JLabel("Client:"));
        dialog.add(clientComboBox);
        dialog.add(new JLabel("Invoice:"));
        dialog.add(invoiceComboBox);
        dialog.add(new JLabel("Date:"));
        dialog.add(dateField);
        dialog.add(new JLabel("Method:"));
        dialog.add(methodComboBox);
        dialog.add(new JLabel("Amount:"));
        dialog.add(amountField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try (Session session = factory.openSession()) {
                session.beginTransaction();
                Client selectedClient = session.createQuery("from Client where fullName = :name", Client.class)
                        .setParameter("name", clientComboBox.getSelectedItem()).uniqueResult();
                Invoice selectedInvoice = session.createQuery("from Invoice where id = :id", Invoice.class)
                        .setParameter("id", Integer.parseInt(invoiceComboBox.getSelectedItem().toString().split("#")[1])).uniqueResult();

                if (selectedClient != null && selectedInvoice != null) {
                    Payment payment = new Payment(
                            selectedClient,
                            selectedInvoice,
                            LocalDate.parse(dateField.getText()),
                            methodComboBox.getSelectedItem().toString(),
                            Double.parseDouble(amountField.getText())
                    );
                    session.save(payment);
                    session.getTransaction().commit();
                    tableModel.addRow(new Object[]{
                            payment.getId(),
                            payment.getClient().getFullName(),
                            "Invoice #" + payment.getInvoice().getId(),
                            payment.getPaymentDate(),
                            payment.getPaymentMethod(),
                            payment.getAmount()
                    });
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Invalid selection.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private static void editPayment(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        // Выводим диалог с текущими значениями
        String client = model.getValueAt(row, 1).toString();
        String invoice = model.getValueAt(row, 2).toString();
        String date = model.getValueAt(row, 3).toString();
        String method = model.getValueAt(row, 4).toString();
        String amount = model.getValueAt(row, 5).toString();

        JTextField dateField = new JTextField(date);
        JTextField amountField = new JTextField(amount);
        JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"Card", "Cash", "Online"});
        methodComboBox.setSelectedItem(method);

        int result = JOptionPane.showConfirmDialog(null, new Object[]{
                "Date:", dateField, "Method:", methodComboBox, "Amount:", amountField
        }, "Edit Payment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            model.setValueAt(dateField.getText(), row, 3);
            model.setValueAt(methodComboBox.getSelectedItem(), row, 4);
            model.setValueAt(amountField.getText(), row, 5);
        }
    }

    private static void deletePayment(JTable table, DefaultTableModel tableModel) {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int paymentId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Delete this payment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Session session = factory.openSession()) {
                session.beginTransaction();
                Payment payment = session.get(Payment.class, paymentId);
                if (payment != null) {
                    session.delete(payment);
                    session.getTransaction().commit();
                    tableModel.removeRow(row);
                }
            }
        }
    }
}
