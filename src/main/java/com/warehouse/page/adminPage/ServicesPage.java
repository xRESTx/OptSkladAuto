package com.warehouse.page.adminPage;

import com.warehouse.entities.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServicesPage {

    public static void showServicesPage() {
        JFrame frame = new JFrame("Service Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Service List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных сервисов
        String[] columnNames = {"ID", "Name", "Description", "Unit"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable serviceTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(serviceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления сервиса
        JButton addButton = new JButton("Add Service");
        addButton.addActionListener(e -> addService(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования сервиса
        JButton editButton = new JButton("Edit Service");
        editButton.addActionListener(e -> editService(serviceTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления сервиса
        JButton deleteButton = new JButton("Delete Service");
        deleteButton.addActionListener(e -> deleteService(serviceTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadServiceData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static Service getServiceById(int serviceId) {
        // Получаем сервис по ID из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Service.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            Service service = session.get(Service.class, serviceId);
            session.getTransaction().commit();
            return service;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving service: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private static void loadServiceData(DefaultTableModel tableModel) {
        // Создаем сессию Hibernate для получения данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Service.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех сервисов из базы данных
            List<Service> services = session.createQuery("from Service", Service.class).list();

            // Добавление данных в таблицу
            for (Service service : services) {
                tableModel.addRow(new Object[]{
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getUnit()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading services: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addService(DefaultTableModel tableModel) {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField unitField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String unit = unitField.getText().trim();

            // Проверка на пустые поля
            if (name.isEmpty() || description.isEmpty() || unit.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Создаем новый сервис
            Service service = new Service(name, description, unit);

            // Добавляем сервис в базу данных
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Service.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                session.save(service);
                session.getTransaction().commit();

                // Добавляем новый сервис в таблицу
                tableModel.addRow(new Object[]{
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getUnit()
                });

                JOptionPane.showMessageDialog(null, "Service added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding service: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteService(JTable serviceTable, DefaultTableModel tableModel) {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a service to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serviceId = (int) tableModel.getValueAt(selectedRow, 0);

        // Запрос подтверждения удаления
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this service?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Service.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Service service = session.get(Service.class, serviceId);
                if (service != null) {
                    session.delete(service);  // Удаление сервиса из базы данных
                    session.getTransaction().commit();

                    // Удаляем сервис из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Service deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Service not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting service: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void editService(JTable serviceTable, DefaultTableModel tableModel) {
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a service to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serviceId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 2);
        String currentUnit = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField descriptionField = new JTextField(currentDescription);
        JTextField unitField = new JTextField(currentUnit);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newDescription = descriptionField.getText().trim();
            String newUnit = unitField.getText().trim();

            // Обновляем данные сервиса в базе данных
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Service.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Service service = session.get(Service.class, serviceId);
                if (service != null) {
                    service.setName(newName);
                    service.setDescription(newDescription);
                    service.setUnit(newUnit);
                    session.update(service);

                    session.getTransaction().commit();

                    // Обновляем данные в таблице
                    tableModel.setValueAt(newName, selectedRow, 1);
                    tableModel.setValueAt(newDescription, selectedRow, 2);
                    tableModel.setValueAt(newUnit, selectedRow, 3);

                    JOptionPane.showMessageDialog(null, "Service updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Service not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating service: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
