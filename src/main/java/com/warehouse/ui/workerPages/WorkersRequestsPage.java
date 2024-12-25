package com.warehouse.ui.workerPages;

import com.warehouse.dao.RequestDAO;
import com.warehouse.models.Request;
import com.warehouse.utils.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class WorkersRequestsPage extends JFrame {
    private JTable requestsTable;
    private JButton backButton;

    public WorkersRequestsPage() {
        setTitle("Worker's Requests");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new WorkerMainPage(); // Возвращаемся на главную страницу
        });

        // Добавляем кнопку на панель инструментов
        toolbar.add(backButton);

        // Таблица запросов
        String[] columnNames = {"Request Number", "Customer", "Date", "Status", "Text Request", "Number Order"};
        Object[][] data = {}; // Заглушка, данные будут загружаться из базы
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        requestsTable = new JTable(model);
        requestsTable.setFillsViewportHeight(true);
        requestsTable.setRowHeight(50);

        // Настройка сортировки для таблицы
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        requestsTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(requestsTable);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        loadWorkerRequests(); // Загружаем данные запросов при инициализации
    }

    // Метод для загрузки запросов текущего работника
    private void loadWorkerRequests() {
        String currentUserLogin = SessionManager.getCurrentUserLogin();
        List<Request> workerRequests = RequestDAO.findRequestsByEmployeeLogin(currentUserLogin);
        updateRequestTable(workerRequests);
    }

    // Метод для обновления таблицы запросов
    private void updateRequestTable(List<Request> requests) {
        String[] columnNames = {"Request Number", "Customer", "Date", "Status", "Text Request", "Number Order"};

        // Преобразуем List<Request> в двумерный массив для JTable
        Object[][] data = new Object[requests.size()][6];
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            data[i][0] = request.getRequestId();
            data[i][1] = request.getAccount().getFullName();
            data[i][2] = request.getDateRequest();
            data[i][3] = request.getStatusOrder();
            data[i][4] = request.getTextRequest();
            data[i][5] = request.getOrders().getNumberOrder();
        }

        // Обновляем модель таблицы
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещает редактирование всех ячеек
            }
        };
        requestsTable.setModel(model);

        // Настроить сортировку для таблицы
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        requestsTable.setRowSorter(sorter);
    }
}
