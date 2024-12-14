package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Request;
import com.warehouse.entities.Order;
import com.warehouse.entities.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class RequestsPage {

    public static void showRequestsPage() {
        JFrame frame = new JFrame("Request Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Request List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных запросов
        String[] columnNames = {"ID", "Client ID", "Order ID", "Description", "Status", "Creation Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable requestTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления запроса
        JButton addButton = new JButton("Add Request");
        addButton.addActionListener(e -> addRequest(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования запроса
        JButton editButton = new JButton("Edit Request");
        editButton.addActionListener(e -> editRequest(requestTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления запроса
        JButton deleteButton = new JButton("Delete Request");
        deleteButton.addActionListener(e -> deleteRequest(requestTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadRequestData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadRequestData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Request.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех запросов из базы данных
            List<Request> requests = session.createQuery("from Request", Request.class).list();

            // Добавление данных в таблицу
            for (Request request : requests) {
                tableModel.addRow(new Object[]{
                        request.getId(),
                        request.getClient().getClientId(),
                        request.getOrder().getId(),
                        request.getDescription(),
                        request.getStatus(),
                        request.getCreationDate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addRequest(DefaultTableModel tableModel) {
        // Создаем выпадающий список для клиентов
        JComboBox<Client> clientComboBox = new JComboBox<>();

        // Создаем выпадающий список для заказов
        JComboBox<Order> orderComboBox = new JComboBox<>();

        // Загружаем существующих клиентов из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(Request.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            // Загружаем клиентов
            List<Client> clients = session.createQuery("from Client", Client.class).list();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }

            // Загружаем заказы
            List<Order> orders = session.createQuery("from Order", Order.class).list();
            for (Order order : orders) {
                orderComboBox.addItem(order);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading clients or orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Поля для других данных
        JTextField descriptionField = new JTextField();
        JTextField statusField = new JTextField();

        // Создаем JDateChooser для выбора даты
        JDateChooser creationDateChooser = new JDateChooser();
        creationDateChooser.setDateFormatString("yyyy-MM-dd"); // Формат отображаемой даты

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Client:"));
        panel.add(clientComboBox);
        panel.add(new JLabel("Order ID:"));
        panel.add(orderComboBox);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Creation Date:"));
        panel.add(creationDateChooser); // Добавляем JDateChooser в панель

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Request", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Client selectedClient = (Client) clientComboBox.getSelectedItem();
            Order selectedOrder = (Order) orderComboBox.getSelectedItem();
            String description = descriptionField.getText();
            String status = statusField.getText();

            // Получаем выбранную дату из JDateChooser
            java.util.Date utilDate = creationDateChooser.getDate();
            if (utilDate != null) {
                LocalDate creationDate = LocalDate.ofInstant(utilDate.toInstant(), java.time.ZoneId.systemDefault());

                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    // Создаем и сохраняем запрос
                    Request request = new Request();
                    request.setClient(selectedClient);
                    request.setOrder(selectedOrder);
                    request.setDescription(description);
                    request.setStatus(status);
                    request.setCreationDate(creationDate);

                    session.save(request);
                    session.getTransaction().commit();

                    // Обновляем таблицу
                    tableModel.addRow(new Object[]{
                            request.getId(),
                            selectedClient.getFullName(),
                            selectedOrder.getId(),
                            request.getDescription(),
                            request.getStatus(),
                            request.getCreationDate()
                    });

                    JOptionPane.showMessageDialog(null, "Request added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a creation date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        factory.close();
    }

    private static void editRequest(JTable requestTable, DefaultTableModel tableModel) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 3);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        LocalDate currentCreationDate = (LocalDate) tableModel.getValueAt(selectedRow, 5);

        JTextField descriptionField = new JTextField(currentDescription);
        JTextField statusField = new JTextField(currentStatus);
        JTextField creationDateField = new JTextField(currentCreationDate.toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Creation Date (YYYY-MM-DD):"));
        panel.add(creationDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Request", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newDescription = descriptionField.getText();
            String newStatus = statusField.getText();
            LocalDate newCreationDate = LocalDate.parse(creationDateField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Request.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Request request = session.get(Request.class, requestId);
                if (request != null) {
                    request.setDescription(newDescription);
                    request.setStatus(newStatus);
                    request.setCreationDate(newCreationDate);
                    session.update(request);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newDescription, selectedRow, 3);
                    tableModel.setValueAt(newStatus, selectedRow, 4);
                    tableModel.setValueAt(newCreationDate, selectedRow, 5);

                    JOptionPane.showMessageDialog(null, "Request updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Request not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deleteRequest(JTable requestTable, DefaultTableModel tableModel) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this request?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Request.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Находим запрос по ID и удаляем его
                Request request = session.get(Request.class, requestId);
                if (request != null) {
                    session.delete(request);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Request deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Request not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
