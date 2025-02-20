package com.warehouse.page.clientPage;

import com.warehouse.entities.Client;
import com.warehouse.entities.Meter;
import com.warehouse.entities.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MeterPage {
    // Убедитесь, что factory определен как глобальная переменная
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Meter.class)
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(Service.class)
            .buildSessionFactory();

    public static void showMeterPage(int clientId) {
        JFrame frame = new JFrame("Meter Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Meter List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Колонки для таблицы
        String[] columnNames = {"ID", "Client", "Service", "Installation Date", "Last Reading Date", "Last Reading Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable meterTable = new JTable(tableModel);
        meterTable.setEnabled(false); // Отключаем редактирование таблицы

        JScrollPane scrollPane = new JScrollPane(meterTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загружаем данные счетчиков для текущего пользователя
        loadMeterData(clientId, tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadMeterData(int clientId, DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            // Запрос с фильтрацией по clientId
            List<Meter> meters = session.createQuery(
                            "Select m FROM Meter m " +
                                    "JOIN Client c ON c.id = m.client.id "+
                                    "WHERE c.id = :clientId", Meter.class)
                    .setParameter("clientId", clientId)
                    .list();

            for (Meter meter : meters) {
                tableModel.addRow(new Object[]{
                        meter.getId(),
                        meter.getClient().getFullName(),
                        meter.getService().getName(),
                        meter.getInstallationDate(),
                        meter.getLastReadingDate(),
                        meter.getLastReadingValue() // Добавляем lastReadingValue в таблицу
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading meters: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
