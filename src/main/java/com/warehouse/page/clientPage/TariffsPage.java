package com.warehouse.page.clientPage;

import com.warehouse.entities.Tariff;
import com.warehouse.entities.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TariffsPage {

    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Tariff.class)
            .buildSessionFactory();

    public static void showTariffsPage() {
        JFrame frame = new JFrame("Tariff List");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Tariff List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных тарифов
        String[] columnNames = {"ID", "Service", "Effective Date", "Rate"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tariffTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tariffTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Кнопка для закрытия окна
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadTariffData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadTariffData(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех тарифов из базы данных
            List<Tariff> tariffs = session.createQuery("from Tariff", Tariff.class).list();

            // Добавление данных в таблицу
            for (Tariff tariff : tariffs) {
                tableModel.addRow(new Object[]{
                        tariff.getId(),
                        tariff.getService().getName(),
                        tariff.getEffectiveDate(),
                        tariff.getRate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading tariffs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
