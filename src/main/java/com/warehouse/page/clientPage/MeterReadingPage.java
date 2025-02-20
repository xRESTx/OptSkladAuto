package com.warehouse.page.clientPage;

import com.warehouse.entities.Meter;
import com.warehouse.entities.MeterReading;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MeterReadingPage {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(MeterReading.class)
            .addAnnotatedClass(Meter.class)
            .buildSessionFactory();

    public static void showMeterReadingPage(int clientId) {
        JFrame frame = new JFrame("Meter Reading Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Meter Reading List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Meter ID", "Reading Date", "Reading Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable meterReadingTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(meterReadingTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        // Кнопка "Close"
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadMeterReadingData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadMeterReadingData(int clientId, DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            // Query to get meter readings based on client ID
            List<MeterReading> meterReadings = session.createQuery(
                            "FROM MeterReading WHERE meter.id IN " +
                                    "(SELECT meter.id FROM Meter WHERE meter.client.id = :clientId)", MeterReading.class)
                    .setParameter("clientId", clientId)
                    .list();

            // Adding rows to the table based on the fetched meter readings
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
}
