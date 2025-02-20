package com.warehouse.page.adminPage;

import com.warehouse.entities.Meter;
import com.warehouse.entities.MeterReading;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MeterReadingPage {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(MeterReading.class)
            .addAnnotatedClass(Meter.class)
            .buildSessionFactory();

    public static void showMeterReadingPage() {
        JFrame frame = new JFrame("Meter Reading Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Meter Reading List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Meter", "Reading Date", "Reading Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable meterReadingTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(meterReadingTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Reading");
        addButton.addActionListener(e -> addMeterReading(tableModel));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Reading");
        editButton.addActionListener(e -> editMeterReading(meterReadingTable, tableModel));
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Reading");
        deleteButton.addActionListener(e -> deleteMeterReading(meterReadingTable, tableModel));
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadMeterReadingData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadMeterReadingData(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            List<MeterReading> meterReadings = session.createQuery("from MeterReading", MeterReading.class).list();
            for (MeterReading meterReading : meterReadings) {
                tableModel.addRow(new Object[]{
                        meterReading.getId(),
                        meterReading.getMeter().getId(),
                        meterReading.getReadingDate(),
                        meterReading.getReadingValue()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading meter readings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addMeterReading(DefaultTableModel tableModel) {
        // Create a dialog for adding a meter reading
        JDialog addMeterReadingDialog = new JDialog();
        addMeterReadingDialog.setTitle("Add Meter Reading");
        addMeterReadingDialog.setSize(400, 300);
        addMeterReadingDialog.setLayout(new BorderLayout());

        // Create a panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        addMeterReadingDialog.add(formPanel, BorderLayout.CENTER);

        JLabel meterLabel = new JLabel("Meter: ");
        JComboBox<String> meterComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            List<Meter> meters = session.createQuery("from Meter", Meter.class).list();
            for (Meter meter : meters) {
                meterComboBox.addItem("Meter " + meter.getId());
            }
        }
        formPanel.add(meterLabel);
        formPanel.add(meterComboBox);

        JLabel readingDateLabel = new JLabel("Reading Date: ");
        JTextField readingDateField = new JTextField(LocalDate.now().toString());
        formPanel.add(readingDateLabel);
        formPanel.add(readingDateField);

        JLabel readingValueLabel = new JLabel("Reading Value: ");
        JTextField readingValueField = new JTextField();
        formPanel.add(readingValueLabel);
        formPanel.add(readingValueField);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            int meterId = meterComboBox.getSelectedIndex() + 1;
            LocalDate readingDate = LocalDate.parse(readingDateField.getText());
            double readingValue = Double.parseDouble(readingValueField.getText());

            try (Session session = factory.openSession()) {
                session.beginTransaction();
                Meter meter = session.get(Meter.class, meterId);
                if (meter == null) {
                    JOptionPane.showMessageDialog(null, "Invalid meter selected", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MeterReading meterReading = new MeterReading(meter, readingDate, readingValue);
                session.save(meterReading);
                session.getTransaction().commit();

                tableModel.addRow(new Object[]{
                        meterReading.getId(),
                        meterReading.getMeter().getId(),
                        readingDate,
                        readingValue
                });

                addMeterReadingDialog.dispose();  // Close the dialog after saving
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding meter reading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> addMeterReadingDialog.dispose());  // Close the dialog without saving

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        addMeterReadingDialog.add(buttonPanel, BorderLayout.SOUTH);

        addMeterReadingDialog.setLocationRelativeTo(null);
        addMeterReadingDialog.setModal(true);
        addMeterReadingDialog.setVisible(true);
    }

    private static void editMeterReading(JTable meterReadingTable, DefaultTableModel tableModel) {
        int selectedRow = meterReadingTable.getSelectedRow();
        if (selectedRow == -1) return;

        int readingId = (int) tableModel.getValueAt(selectedRow, 0);

        // Create a dialog for editing the meter reading
        JDialog editMeterReadingDialog = new JDialog();
        editMeterReadingDialog.setTitle("Edit Meter Reading");
        editMeterReadingDialog.setSize(400, 300);
        editMeterReadingDialog.setLayout(new BorderLayout());

        // Create a panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        editMeterReadingDialog.add(formPanel, BorderLayout.CENTER);

        JLabel meterLabel = new JLabel("Meter: ");
        JComboBox<String> meterComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            MeterReading meterReading = session.get(MeterReading.class, readingId);
            if (meterReading != null) {
                List<Meter> meters = session.createQuery("from Meter", Meter.class).list();
                for (Meter meter : meters) {
                    meterComboBox.addItem("Meter " + meter.getId());
                }
                meterComboBox.setSelectedItem("Meter " + meterReading.getMeter().getId());
            }
        }
        formPanel.add(meterLabel);
        formPanel.add(meterComboBox);

        JLabel readingDateLabel = new JLabel("Reading Date: ");
        JTextField readingDateField = new JTextField();
        try (Session session = factory.openSession()) {
            MeterReading meterReading = session.get(MeterReading.class, readingId);
            if (meterReading != null) {
                readingDateField.setText(meterReading.getReadingDate().toString());
            }
        }
        formPanel.add(readingDateLabel);
        formPanel.add(readingDateField);

        JLabel readingValueLabel = new JLabel("Reading Value: ");
        JTextField readingValueField = new JTextField();
        try (Session session = factory.openSession()) {
            MeterReading meterReading = session.get(MeterReading.class, readingId);
            if (meterReading != null) {
                readingValueField.setText(String.valueOf(meterReading.getReadingValue()));
            }
        }
        formPanel.add(readingValueLabel);
        formPanel.add(readingValueField);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            int meterId = meterComboBox.getSelectedIndex() + 1;
            LocalDate readingDate = LocalDate.parse(readingDateField.getText());
            double readingValue = Double.parseDouble(readingValueField.getText());

            try (Session session = factory.openSession()) {
                session.beginTransaction();
                MeterReading meterReading = session.get(MeterReading.class, readingId);
                if (meterReading != null) {
                    Meter meter = session.get(Meter.class, meterId);
                    if (meter == null) {
                        JOptionPane.showMessageDialog(null, "Invalid meter selected", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    meterReading.setMeter(meter);
                    meterReading.setReadingDate(readingDate);
                    meterReading.setReadingValue(readingValue);
                    session.update(meterReading);
                    session.getTransaction().commit();

                    // Update the table
                    tableModel.setValueAt(meterReading.getMeter().getId(), selectedRow, 1);
                    tableModel.setValueAt(readingDate, selectedRow, 2);
                    tableModel.setValueAt(readingValue, selectedRow, 3);

                    editMeterReadingDialog.dispose();  // Close the dialog after saving
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error editing meter reading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> editMeterReadingDialog.dispose());  // Close the dialog without saving

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        editMeterReadingDialog.add(buttonPanel, BorderLayout.SOUTH);

        editMeterReadingDialog.setLocationRelativeTo(null);
        editMeterReadingDialog.setModal(true);
        editMeterReadingDialog.setVisible(true);
    }

    private static void deleteMeterReading(JTable meterReadingTable, DefaultTableModel tableModel) {
        int selectedRow = meterReadingTable.getSelectedRow();
        if (selectedRow == -1) return;

        int readingId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this meter reading?", "Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Session session = factory.openSession()) {
                session.beginTransaction();
                MeterReading meterReading = session.get(MeterReading.class, readingId);
                if (meterReading != null) {
                    session.delete(meterReading);
                    session.getTransaction().commit();

                    // Remove the row from the table
                    tableModel.removeRow(selectedRow);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error deleting meter reading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
