package com.warehouse.ui.userPages;

import com.warehouse.dao.RequestDAO;
import com.warehouse.models.Request;
import com.warehouse.utils.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserRequestsPage extends JFrame {
    private JTable requestsTable;
    private JButton backButton;

    public UserRequestsPage() {
        setTitle("Requests Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new UserMainPage().setVisible(true); // Возвращаемся на главную страницу
        });

        // Добавляем кнопки на панель инструментов
        toolbar.add(backButton);

        // Таблица запросов
        String[] columnNames = {"Request Number", "Customer", "Date", "Status", "Text Request", "Employee", "Number Order"};
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

        loadRequests(); // Загружаем данные запросов при инициализации
    }

    // Метод для загрузки всех запросов
//    private void loadRequests() {
//        List<Request> allRequests = RequestDAO.findAll();
//        updateRequestTable(allRequests);
//    }
    private void loadRequests() {
        String currentLogin = SessionManager.getCurrentUserLogin();
        List<Request> allRequests = RequestDAO.findAll();

        // Фильтруем заказы по текущему логину пользователя
        List<Request> userRequests = allRequests.stream()
                .filter(order -> order.getAccount().getLogin().equals(currentLogin))
                .collect(Collectors.toList());

        updateRequestTable(userRequests);
    }
    // Метод для обновления таблицы запросов
    private void updateRequestTable(List<Request> requests) {
        String[] columnNames = {"Request Number", "Customer", "Date", "Status", "Text Request", "Employee", "Number Order"};

        // Преобразуем List<Request> в двумерный массив для JTable
        Object[][] data = new Object[requests.size()][7];
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            data[i][0] = request.getRequestId();
            data[i][1] = request.getAccount().getFullName();
            data[i][2] = request.getDateRequest();
            data[i][3] = request.getStatusOrder();
            data[i][4] = request.getTextRequest();
            data[i][5] = request.getEmployee().getEmployeeLogin();
            data[i][6] = request.getOrders().getNumberOrder();
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

    // Метод для удаления запроса
    private void deleteRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) requestsTable.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete request #" + requestId + "?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            RequestDAO.deleteRequestById(requestId); // Удаляем запрос по id
            loadRequests(); // Обновляем таблицу запросов
        }
    }
}
