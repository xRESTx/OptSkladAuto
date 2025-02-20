package com.warehouse.page.adminPage;

import com.warehouse.entities.Client;
import com.warehouse.entities.Meter;
import com.warehouse.entities.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MeterPage {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Meter.class)
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(Service.class)
            .buildSessionFactory();

    public static void showMeterPage() {
        JFrame frame = new JFrame("Meter Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel titleLabel = new JLabel("Meter List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Добавлено поле для отображения Last Reading Value
        String[] columnNames = {"ID", "Client", "Service", "Installation Date", "Last Reading Date", "Last Reading Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable meterTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(meterTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Meter");
        addButton.addActionListener(e -> addMeter(tableModel));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Meter");
        editButton.addActionListener(e -> editMeter(meterTable, tableModel));
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Meter");
        deleteButton.addActionListener(e -> deleteMeter(meterTable, tableModel));
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadMeterData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadMeterData(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            List<Meter> meters = session.createQuery("from Meter", Meter.class).list();
            for (Meter meter : meters) {
                tableModel.addRow(new Object[]{
                        meter.getId(),
                        meter.getClient().getFullName(),
                        meter.getService().getName(),
                        meter.getInstallationDate(),
                        meter.getLastReadingDate(),
                        meter.getLastReadingValue() // Добавлен lastReadingValue в таблицу
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading meters: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addMeter(DefaultTableModel tableModel) {
        // Создаем диалоговое окно для добавления счетчика
        JDialog addMeterDialog = new JDialog();
        addMeterDialog.setTitle("Add Meter");
        addMeterDialog.setSize(400, 400); // Увеличенный размер окна для дополнительного поля
        addMeterDialog.setLayout(new BorderLayout());

        // Создаем панель для полей формы
        JPanel formPanel = new JPanel(new GridLayout(7, 2)); // Увеличена сетка для дополнительного поля
        addMeterDialog.add(formPanel, BorderLayout.CENTER);

        // Поля формы
        JLabel clientLabel = new JLabel("Client: ");
        JComboBox<String> clientComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            List<Client> clients = session.createQuery("from Client", Client.class).list();
            for (Client client : clients) {
                clientComboBox.addItem(client.getFullName());
            }
        }
        formPanel.add(clientLabel);
        formPanel.add(clientComboBox);

        JLabel serviceLabel = new JLabel("Service: ");
        JComboBox<String> serviceComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            List<Service> services = session.createQuery("from Service", Service.class).list();
            for (Service service : services) {
                serviceComboBox.addItem(service.getName());
            }
        }
        formPanel.add(serviceLabel);
        formPanel.add(serviceComboBox);

        JLabel installationDateLabel = new JLabel("Installation Date: ");
        JTextField installationDateField = new JTextField(LocalDate.now().toString());
        formPanel.add(installationDateLabel);
        formPanel.add(installationDateField);

        JLabel lastReadingDateLabel = new JLabel("Last Reading Date: ");
        JTextField lastReadingDateField = new JTextField(LocalDate.now().toString());
        formPanel.add(lastReadingDateLabel);
        formPanel.add(lastReadingDateField);

        // Новое поле для Last Reading Value
        JLabel lastReadingValueLabel = new JLabel("Last Reading Value: ");
        JTextField lastReadingValueField = new JTextField();
        formPanel.add(lastReadingValueLabel);
        formPanel.add(lastReadingValueField);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String clientName = (String) clientComboBox.getSelectedItem();
            String serviceName = (String) serviceComboBox.getSelectedItem();
            LocalDate installationDate = LocalDate.parse(installationDateField.getText());
            LocalDate lastReadingDate = LocalDate.parse(lastReadingDateField.getText());
            String lastReadingValueText = lastReadingValueField.getText();

            if (lastReadingValueText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Last Reading Value is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double lastReadingValue = Double.parseDouble(lastReadingValueText);

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Client client = session.createQuery("from Client where fullName = :name", Client.class)
                            .setParameter("name", clientName)
                            .uniqueResult();
                    Service service = session.createQuery("from Service where name = :name", Service.class)
                            .setParameter("name", serviceName)
                            .uniqueResult();

                    if (client == null || service == null) {
                        JOptionPane.showMessageDialog(null, "Invalid client or service selected", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Meter meter = new Meter(client, service, installationDate);
                    meter.setLastReadingDate(lastReadingDate);
                    meter.setLastReadingValue(lastReadingValue); // Устанавливаем lastReadingValue
                    session.save(meter);
                    session.getTransaction().commit();

                    tableModel.addRow(new Object[]{
                            meter.getId(),
                            client.getFullName(),
                            service.getName(),
                            installationDate,
                            lastReadingDate,
                            lastReadingValue  // Добавляем значение lastReadingValue в таблицу
                    });

                    addMeterDialog.dispose();  // Закрываем диалог после сохранения
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid value for last reading. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding meter: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> addMeterDialog.dispose());  // Закрытие диалога без сохранения

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        addMeterDialog.add(buttonPanel, BorderLayout.SOUTH);

        addMeterDialog.setLocationRelativeTo(null);
        addMeterDialog.setModal(true);
        addMeterDialog.setVisible(true);
    }

    private static void deleteMeter(JTable meterTable, DefaultTableModel tableModel) {
        int selectedRow = meterTable.getSelectedRow();
        if (selectedRow == -1) return;

        int meterId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                session.delete(meter);
                session.getTransaction().commit();
                tableModel.removeRow(selectedRow);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting meter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String selectClient() {
        try (Session session = factory.openSession()) {
            List<Client> clients = session.createQuery("from Client", Client.class).list();
            JComboBox<String> comboBox = new JComboBox<>(clients.stream().map(Client::getFullName).toArray(String[]::new));
            JOptionPane.showMessageDialog(null, comboBox, "Select Client", JOptionPane.QUESTION_MESSAGE);
            return (String) comboBox.getSelectedItem();
        }
    }

    private static String selectService() {
        try (Session session = factory.openSession()) {
            List<Service> services = session.createQuery("from Service", Service.class).list();
            JComboBox<String> comboBox = new JComboBox<>(services.stream().map(Service::getName).toArray(String[]::new));
            JOptionPane.showMessageDialog(null, comboBox, "Select Service", JOptionPane.QUESTION_MESSAGE);
            return (String) comboBox.getSelectedItem();
        }
    }

    private static void editMeter(JTable meterTable, DefaultTableModel tableModel) {
        int selectedRow = meterTable.getSelectedRow();
        if (selectedRow == -1) return;

        int meterId = (int) tableModel.getValueAt(selectedRow, 0);

        // Создаем диалоговое окно для редактирования счетчика
        JDialog editMeterDialog = new JDialog();
        editMeterDialog.setTitle("Edit Meter");
        editMeterDialog.setSize(400, 400);
        editMeterDialog.setLayout(new BorderLayout());

        // Создаем панель для полей формы
        JPanel formPanel = new JPanel(new GridLayout(7, 2));
        editMeterDialog.add(formPanel, BorderLayout.CENTER);

        // Поля формы
        JLabel clientLabel = new JLabel("Client: ");
        JComboBox<String> clientComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                List<Client> clients = session.createQuery("from Client", Client.class).list();
                for (Client client : clients) {
                    clientComboBox.addItem(client.getFullName());
                }
                clientComboBox.setSelectedItem(meter.getClient().getFullName());
            }
        }
        formPanel.add(clientLabel);
        formPanel.add(clientComboBox);

        JLabel serviceLabel = new JLabel("Service: ");
        JComboBox<String> serviceComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                List<Service> services = session.createQuery("from Service", Service.class).list();
                for (Service service : services) {
                    serviceComboBox.addItem(service.getName());
                }
                serviceComboBox.setSelectedItem(meter.getService().getName());
            }
        }
        formPanel.add(serviceLabel);
        formPanel.add(serviceComboBox);

        JLabel installationDateLabel = new JLabel("Installation Date: ");
        JTextField installationDateField = new JTextField();
        try (Session session = factory.openSession()) {
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                installationDateField.setText(meter.getInstallationDate().toString());
            }
        }
        formPanel.add(installationDateLabel);
        formPanel.add(installationDateField);

        JLabel lastReadingDateLabel = new JLabel("Last Reading Date: ");
        JTextField lastReadingDateField = new JTextField();
        try (Session session = factory.openSession()) {
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                lastReadingDateField.setText(meter.getLastReadingDate().toString());
            }
        }
        formPanel.add(lastReadingDateLabel);
        formPanel.add(lastReadingDateField);

        JLabel lastReadingValueLabel = new JLabel("Last Reading Value: ");
        JTextField lastReadingValueField = new JTextField();
        try (Session session = factory.openSession()) {
            Meter meter = session.get(Meter.class, meterId);
            if (meter != null) {
                lastReadingValueField.setText(String.valueOf(meter.getLastReadingValue()));
            }
        }
        formPanel.add(lastReadingValueLabel);
        formPanel.add(lastReadingValueField);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String clientName = (String) clientComboBox.getSelectedItem();
            String serviceName = (String) serviceComboBox.getSelectedItem();
            LocalDate installationDate = LocalDate.parse(installationDateField.getText());
            LocalDate lastReadingDate = LocalDate.parse(lastReadingDateField.getText());
            String lastReadingValueText = lastReadingValueField.getText();

            if (lastReadingValueText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Last Reading Value is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double lastReadingValue = Double.parseDouble(lastReadingValueText);

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Meter meter = session.get(Meter.class, meterId);
                    if (meter != null) {
                        Client client = session.createQuery("from Client where fullName = :name", Client.class)
                                .setParameter("name", clientName)
                                .uniqueResult();
                        Service service = session.createQuery("from Service where name = :name", Service.class)
                                .setParameter("name", serviceName)
                                .uniqueResult();

                        if (client == null || service == null) {
                            JOptionPane.showMessageDialog(null, "Invalid client or service selected", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        meter.setClient(client);
                        meter.setService(service);
                        meter.setInstallationDate(installationDate);
                        meter.setLastReadingDate(lastReadingDate);
                        meter.setLastReadingValue(lastReadingValue); // Обновление lastReadingValue
                        session.update(meter);
                        session.getTransaction().commit();

                        // Обновляем таблицу
                        tableModel.setValueAt(clientName, selectedRow, 1);
                        tableModel.setValueAt(serviceName, selectedRow, 2);
                        tableModel.setValueAt(installationDate, selectedRow, 3);
                        tableModel.setValueAt(lastReadingDate, selectedRow, 4);
                        tableModel.setValueAt(lastReadingValue, selectedRow, 5); // Обновляем lastReadingValue в таблице

                        editMeterDialog.dispose();  // Закрываем диалог после сохранения
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid value for last reading. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error editing meter: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> editMeterDialog.dispose());  // Закрытие диалога без сохранения

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        editMeterDialog.add(buttonPanel, BorderLayout.SOUTH);

        editMeterDialog.setLocationRelativeTo(null);
        editMeterDialog.setModal(true);
        editMeterDialog.setVisible(true);
    }

}
