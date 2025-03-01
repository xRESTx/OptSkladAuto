package com.warehouse.ui.adminPages;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.Employee;
import com.warehouse.models.Station;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StationsPage extends JFrame {
    private JTable stationsTable;
    private DefaultTableModel tableModel;
    private StationDAO stationDAO;

    public StationsPage() {
        stationDAO = new StationDAO();

        setTitle("Управление заправками");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Создание таблицы
        tableModel = new DefaultTableModel(new String[]{"ID", "Название", "Локация", "ID Менеджера"}, 0);
        stationsTable = new JTable(tableModel);
        loadStations(); // Загружаем данные в таблицу

        panel.add(new JScrollPane(stationsTable), BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton backButton = new JButton("Назад");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        addButton.addActionListener(e -> addStation());
        editButton.addActionListener(e -> editStation());
        deleteButton.addActionListener(e -> deleteStation());
        backButton.addActionListener(e -> goBack());

        add(panel);
        setVisible(true);
    }

    private void loadStations() {
        tableModel.setRowCount(0); // Очищаем таблицу перед загрузкой новых данных
        List<Station> stations = stationDAO.getAllStations(); // Загружаем данные из БД

        if (stations.isEmpty()) {
            System.out.println("NODATA!"); // Отладка
        }

        for (Station station : stations) {
            Object[] row = {
                    station.getStationId(),
                    station.getName(),
                    station.getLocation(),
                    (station.getManager() != null) ? station.getManager().getFullName() : "Нет менеджера"
            };
            tableModel.addRow(row);
        }
    }


    private void addStation() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();

        // Выпадающий список менеджеров
        JComboBox<Employee> managerComboBox = new JComboBox<>();
        List<Employee> employees = new EmployeeDAO().getAllEmployees(); // Получаем всех сотрудников
        for (Employee emp : employees) {
            managerComboBox.addItem(emp);
        }

        Object[] message = {
                "Название:", nameField,
                "Локация:", locationField,
                "Менеджер:", managerComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить станцию", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String location = locationField.getText();
            Employee selectedManager = (Employee) managerComboBox.getSelectedItem(); // Получаем объект Employee

            Station station = new Station();
            station.setName(name);
            station.setLocation(location);
            station.setManager(selectedManager); // Передаем объект Employee

            stationDAO.saveStation(station);
            loadStations(); // Обновляем таблицу
        }
    }


    private void editStation() {
        int selectedRow = stationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите станцию для редактирования.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentLocation = (String) tableModel.getValueAt(selectedRow, 2);

        // Получаем текущего менеджера (объект Employee)
        Employee currentManager = (Employee) tableModel.getValueAt(selectedRow, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField locationField = new JTextField(currentLocation);

        // Выпадающий список менеджеров
        JComboBox<Employee> managerComboBox = new JComboBox<>();
        List<Employee> employees = new EmployeeDAO().getAllEmployees(); // Получаем всех сотрудников
        for (Employee emp : employees) {
            managerComboBox.addItem(emp);
        }

        managerComboBox.setSelectedItem(currentManager);

        Object[] message = {
                "Название:", nameField,
                "Локация:", locationField,
                "Менеджер:", managerComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Редактировать станцию", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Station station = new Station();
            station.setStationId(id);
            station.setName(nameField.getText());
            station.setLocation(locationField.getText());

            // Получаем выбранного менеджера
            Employee selectedManager = (Employee) managerComboBox.getSelectedItem();
            station.setManager(selectedManager);

            stationDAO.updateStation(station);
            loadStations();
        }
    }


    private void deleteStation() {
        int selectedRow = stationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите станцию для удаления.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Удалить станцию ID " + id + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            stationDAO.deleteStation(id);
            loadStations(); // Обновляем таблицу
        }
    }

    private void goBack() {
        dispose();
        new AdminMainPage();
    }
}
