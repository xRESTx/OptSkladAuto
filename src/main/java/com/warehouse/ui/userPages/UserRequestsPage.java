package com.warehouse.ui.userPages;

import com.warehouse.dao.*;
import com.warehouse.dao.RequestDAO;
import com.warehouse.models.*;
import com.warehouse.models.Account;
import com.warehouse.utils.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserRequestsPage extends JFrame {
    private JTable requestsTable;
    private JButton backButton, addRequestButton;

    public UserRequestsPage() {
        setTitle("Requests Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");
        addRequestButton = new JButton("Добавить запрос");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new UserMainPage().setVisible(true); // Возвращаемся на главную страницу
        });

        // Кнопка "Добавить запрос"
        addRequestButton.addActionListener(e -> openAddRequestDialog());

        // Добавляем кнопки на панель инструментов
        toolbar.add(backButton);
        toolbar.add(addRequestButton);

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

    private void loadRequests() {
        String currentLogin = SessionManager.getCurrentUserLogin();
        List<Request> allRequests = RequestDAO.findAll();

        // Фильтруем заказы по текущему логину пользователя
        List<Request> userRequests = allRequests.stream()
                .filter(order -> order.getAccount().getLogin().equals(currentLogin))
                .collect(Collectors.toList());

        updateRequestTable(userRequests);
    }

    private void updateRequestTable(List<Request> requests) {
        String[] columnNames = {"Request Number", "Customer", "Date", "Status", "Text Request", "Employee", "Number Order"};

        Object[][] data = new Object[requests.size()][7];
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            data[i][0] = request.getRequestId();
            data[i][1] = request.getAccount().getFullName();
            data[i][2] = request.getDateRequest();
            data[i][3] = request.getStatusOrder();
            data[i][4] = request.getTextRequest();
            data[i][5] = request.getEmployee() != null ? request.getEmployee().getEmployeeLogin() : "N/A";
            data[i][6] = request.getOrders() != null ? request.getOrders().getNumberOrder() : "N/A";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestsTable.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        requestsTable.setRowSorter(sorter);
    }
    private void openAddRequestDialog() {
        JDialog addRequestDialog = new JDialog(this, "Добавить запрос", true);
        addRequestDialog.setSize(400, 300);
        addRequestDialog.setLayout(new GridLayout(3, 2, 10, 10));
        addRequestDialog.setLocationRelativeTo(this);

        JLabel textRequestLabel = new JLabel("Текст запроса:");
        JTextField textRequestField = new JTextField();

        JLabel orderNumberLabel = new JLabel("Номер заказа:");
        JComboBox<String> orderNumberComboBox = new JComboBox<>();

        // Загружаем список заказов текущего пользователя
        List<Order> orderNumbers = OrderDAO.findOrdersByUserLogin(SessionManager.getCurrentUserLogin());
        //orderNumbers.add("N/A"); // Добавляем опцию "N/A" для запросов без заказа
        for (Order orderNumber : orderNumbers) {
            orderNumberComboBox.addItem(String.valueOf(orderNumber.getNumberOrder()));
        }

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            String textRequest = textRequestField.getText().trim();
            String selectedOrderNumber = (String) orderNumberComboBox.getSelectedItem();

            if (textRequest.isEmpty()) {
                JOptionPane.showMessageDialog(addRequestDialog, "Текст запроса не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Создаем новый запрос
            Request newRequest = new Request();
            newRequest.setTextRequest(textRequest);
            newRequest.setDateRequest(new Date());
            newRequest.setStatusOrder("Новый");

            // Устанавливаем текущего пользователя
            String currentLogin = SessionManager.getCurrentUserLogin();
            Account currentAccount = AccountDAO.findById(currentLogin);
            newRequest.setAccount(currentAccount);

            // Устанавливаем номер заказа, если он выбран
            if (!"N/A".equals(selectedOrderNumber)) {
                newRequest.setOrders(OrderDAO.findOrderByNumber(Integer.parseInt(selectedOrderNumber)));
            } else {
                newRequest.setOrders(null); // Если "N/A", заказа нет
            }

            // Сохраняем запрос
            if (RequestDAO.saveRequest(newRequest)) {
                JOptionPane.showMessageDialog(addRequestDialog, "Запрос успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                addRequestDialog.dispose();
                loadRequests(); // Обновляем таблицу запросов
            } else {
                JOptionPane.showMessageDialog(addRequestDialog, "Ошибка при добавлении запроса. Попробуйте снова.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addRequestDialog.dispose());

        // Добавляем компоненты в диалог
        addRequestDialog.add(textRequestLabel);
        addRequestDialog.add(textRequestField);
        addRequestDialog.add(orderNumberLabel);
        addRequestDialog.add(orderNumberComboBox);
        addRequestDialog.add(saveButton);
        addRequestDialog.add(cancelButton);

        addRequestDialog.setVisible(true);
    }

}
