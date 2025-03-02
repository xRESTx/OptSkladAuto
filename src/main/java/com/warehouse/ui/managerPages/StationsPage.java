package com.warehouse.ui.managerPages;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.Employee;
import com.warehouse.models.Station;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        stationsTable.setRowSorter(sorter);
        loadStations(); // Загружаем данные в таблицу

        panel.add(new JScrollPane(stationsTable), BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Назад");

        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
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

        // Получаем ID текущего менеджера (если есть)
        int managerId = stationDAO.getStationById(id).getManager() != null ?
                stationDAO.getStationById(id).getManager().getEmployeeId() : -1;

        JTextField nameField = new JTextField(currentName);
        JTextField locationField = new JTextField(currentLocation);

        // Выпадающий список менеджеров
        JComboBox<Employee> managerComboBox = new JComboBox<>();
        List<Employee> employees = new EmployeeDAO().getAllEmployees(); // Получаем всех сотрудников
        for (Employee emp : employees) {
            managerComboBox.addItem(emp);
            if (emp.getEmployeeId() == managerId) {
                managerComboBox.setSelectedItem(emp); // Устанавливаем текущего менеджера
            }
        }

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
        new ManagerMainPage();
    }
}
