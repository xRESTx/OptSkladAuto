package com.warehouse.page;

import com.warehouse.dao.RequestDAO;
import com.warehouse.dao.OrderDAO;
import com.warehouse.entities.Client;
import com.warehouse.entities.Request;
import com.warehouse.entities.Order;
import com.warehouse.dao.ClientDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class RequestPage {

    public static void showRequestPage(String clientFullName, int clientId) {
        JFrame frame = new JFrame("Client Requests");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Requests for " + clientFullName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Панель с двумя частями: список заказов слева и список обращений справа
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200); // Пропорция раздела
        frame.add(splitPane, BorderLayout.CENTER);

        // Панель для списка заказов
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BorderLayout());

        // Создаем таблицу для отображения заказов
        String[] orderColumnNames = {"Order ID"};
        DefaultTableModel ordersTableModel = new DefaultTableModel(orderColumnNames, 0);
        JTable ordersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);

        // Получаем все заказы клиента
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.findByClientId(clientId);
        for (Order order : orders) {
            ordersTableModel.addRow(new Object[]{"Order #" + order.getId()});
        }

        // Панель для списка обращений
        JPanel requestsPanel = new JPanel();
        requestsPanel.setLayout(new BorderLayout());

        // Создаем таблицу для отображения обращений
        String[] requestColumnNames = {"Status", "Creation Date", "Description"};
        DefaultTableModel requestsTableModel = new DefaultTableModel(requestColumnNames, 0);
        JTable requestsTable = new JTable(requestsTableModel);
        JScrollPane requestsScrollPane = new JScrollPane(requestsTable);
        requestsPanel.add(requestsScrollPane, BorderLayout.CENTER);

        // Панель для кнопки добавления нового обращения
        JPanel buttonPanel = new JPanel();
        JButton addRequestButton = new JButton("Add New Request");
        buttonPanel.add(addRequestButton);
        requestsPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(ordersPanel);
        splitPane.setRightComponent(requestsPanel);

        // Обработчик выбора строки в таблице заказов
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedOrderText = (String) ordersTableModel.getValueAt(selectedRow, 0);
                    int orderId = Integer.parseInt(selectedOrderText.split("#")[1]);

                    // Получаем обращения для выбранного заказа
                    List<Request> requests = new RequestDAO().findRequestsByOrderId(orderId);

                    // Очищаем таблицу перед добавлением новых данных
                    requestsTableModel.setRowCount(0);

                    for (Request request : requests) {
                        Object[] row = new Object[]{
                                request.getStatus(),
                                request.getCreationDate(),
                                request.getDescription()
                        };
                        requestsTableModel.addRow(row);
                    }

                    // Удаляем предыдущие обработчики и добавляем новый
                    for (ActionListener al : addRequestButton.getActionListeners()) {
                        addRequestButton.removeActionListener(al);
                    }
                    addRequestButton.addActionListener(evt -> showAddRequestDialog(clientId, requestsTableModel, orderId));
                }
            }
        });

        frame.setLocationRelativeTo(null); // Центрирование на экране
        frame.setVisible(true);
    }

    // Диалоговое окно для добавления нового обращения
    private static void showAddRequestDialog(int clientId, DefaultTableModel tableModel, int orderId) {
        // Получаем клиента по clientId
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findById(clientId);

        // Панель для ввода данных обращения
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        // Кнопки для сохранения и отмены
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Request");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Создаем новое окно для добавления обращения
        JFrame addRequestFrame = new JFrame("Add New Request");
        addRequestFrame.setSize(400, 250);
        addRequestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addRequestFrame.add(panel, BorderLayout.CENTER);
        addRequestFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик сохранения нового обращения
        saveButton.addActionListener(e -> {
            String description = descriptionArea.getText();

            // Проверка на пустое значение для обязательного поля description
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(addRequestFrame, "Please fill in the Description.");
                return;
            }

            // Создаем новое обращение
            Request request = new Request();
            request.setClient(client);
            request.setDescription(description);
            request.setStatus("Pending");
            request.setCreationDate(LocalDate.now());
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.findById(orderId);
            request.setOrder(order);

            // Сохраняем обращение в базе данных
            RequestDAO requestDAO = new RequestDAO();
            requestDAO.save(request);

            // Добавляем новое обращение в таблицу
            Object[] row = new Object[]{
                    request.getStatus(),
                    request.getCreationDate(),
                    request.getDescription()
            };
            tableModel.addRow(row);

            JOptionPane.showMessageDialog(addRequestFrame, "Request saved successfully.");

            // Закрываем окно после сохранения
            addRequestFrame.dispose();
        });

        // Обработчик для кнопки Cancel
        cancelButton.addActionListener(e -> addRequestFrame.dispose());

        addRequestFrame.setLocationRelativeTo(null); // Центрирование на экране
        addRequestFrame.setVisible(true);
    }
}
