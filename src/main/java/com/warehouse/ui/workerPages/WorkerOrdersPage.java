package com.warehouse.ui.workerPages;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderComponentDAO;
import com.warehouse.models.Order;
import com.warehouse.models.OrderComponent;
import com.warehouse.ui.managerPages.ManagerMainPage;
import com.warehouse.utils.SessionManager;
import com.warehouse.ui.dialog.OrderDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WorkerOrdersPage extends JFrame {
    private JTable ordersTable;
    private JButton backButton;

    public WorkerOrdersPage() {
        setTitle("My Orders");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new WorkerMainPage();// Здесь можно добавить переход на главную страницу работника
        });

        toolbar.add(backButton);

        // Таблица заказов
        String[] columnNames = {"Order Number", "Customer", "Date", "Status", "Total Sum", "Remark"};
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

        // Обработчик двойного клика для открытия деталей заказа
        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = ordersTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int orderNumber = (int) ordersTable.getValueAt(selectedRow, 0);
                        showOrderDetails(orderNumber);
                    }
                }
            }
        });

        loadOrders(); // Загружаем данные заказов при инициализации
    }

    // Метод для загрузки заказов, связанных с текущим пользователем
    private void loadOrders() {
        String currentLogin = SessionManager.getCurrentUserLogin();

        try {
            List<Order> workerOrders = OrderDAO.findOrdersByEmployeeLogin(currentLogin);
            updateOrderTable(workerOrders);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод для обновления таблицы заказов
    private void updateOrderTable(List<Order> orders) {
        String[] columnNames = {"Order Number", "Customer", "Date", "Status", "Total Sum", "Remark"};

        // Преобразуем List<Order> в двумерный массив для JTable
        Object[][] data = new Object[orders.size()][6];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            data[i][0] = order.getNumberOrder();
            data[i][1] = order.getAccount().getFullName();
            data[i][2] = order.getDateOrder();
            data[i][3] = order.getStatus();
            data[i][4] = order.getTotalSum();
            data[i][5] = order.getRemark();
        }

        // Обновляем модель таблицы
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещает редактирование всех ячеек
            }
        };
        ordersTable.setModel(model);

        // Настроить сортировку для таблицы
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        ordersTable.setRowSorter(sorter);
    }

    // Метод для отображения деталей заказа
    private void showOrderDetails(int orderNumber) {
        try {
            List<OrderComponent> components = OrderComponentDAO.findComponentsByOrderNumber(orderNumber);

            if (components == null || components.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No components found for this order.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            OrderDetailsDialog detailsDialog = new OrderDetailsDialog(this, components);
            detailsDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load order details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
