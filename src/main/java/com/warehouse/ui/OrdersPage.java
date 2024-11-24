package com.warehouse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import com.warehouse.dao.OrderDAO;
import com.warehouse.models.Order;
import com.warehouse.ui.dialog.EditOrderDialog;

public class OrdersPage extends JFrame {
    private JTable ordersTable;
    private JButton backButton;
    private JButton editButton;
    private JButton deleteButton;

    public OrdersPage() {
        setTitle("Orders Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");

        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new MainPage(); // Возвращаемся на главную страницу
        });

        // Добавляем кнопки на панель инструментов
        toolbar.add(backButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        // Таблица заказов
        String[] columnNames = {"Order Number", "Customer", "Date", "Status", "Total Sum", "Employee"};
        Object[][] data = {}; // Заглушка, данные будут загружаться из базы
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        ordersTable = new JTable(model);
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setRowHeight(50);

        // Настройка сортировки для таблицы
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        ordersTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(ordersTable);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Слушатели для кнопок
        editButton.addActionListener(e -> editOrder());
        deleteButton.addActionListener(e -> deleteOrder());

        loadOrders(); // Загружаем данные заказов при инициализации
    }

    // Метод для загрузки всех заказов
    private void loadOrders() {
        List<Order> allOrders = OrderDAO.findAll();
        updateOrderTable(allOrders);
    }

    // Метод для обновления таблицы заказов
    private void updateOrderTable(List<Order> orders) {
        String[] columnNames = {"Order Number", "Customer", "Date", "Status", "Total Sum", "Employee"};

        // Преобразуем List<Order> в двумерный массив для JTable
        Object[][] data = new Object[orders.size()][6];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            data[i][0] = order.getNumberOrder();
            data[i][1] = order.getAccount().getFullName();
            data[i][2] = order.getDateOrder();
            data[i][3] = order.getStatus();
            data[i][4] = order.getTotalSum();
            data[i][5] = order.getEmployee().getEmployeeLogin();
        }

        // Обновляем модель таблицы
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        ordersTable.setModel(model);

        // Настроить сортировку для таблицы
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        ordersTable.setRowSorter(sorter);
    }

    // Метод для редактирования заказа
    private void editOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to edit.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderNumber = (int) ordersTable.getValueAt(selectedRow, 0);
        EditOrderDialog dialog = new EditOrderDialog(this, orderNumber);
        dialog.setVisible(true);

        // Обновляем таблицу после возможных изменений
        loadOrders();
    }

    // Метод для удаления заказа
    private void deleteOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderNumber = (int) ordersTable.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete order #" + orderNumber + "?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            OrderDAO.deleteOrder(orderNumber); // Удаляем заказ
            loadOrders(); // Обновляем таблицу заказов
        }
    }
}
