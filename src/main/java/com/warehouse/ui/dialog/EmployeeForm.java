package com.warehouse.ui.dialog;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.Employee;
import com.warehouse.models.Station;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeeForm extends JDialog {
    private JTextField nameField, positionField, salaryField, hireDateField;
    private JComboBox<Station> stationComboBox;
    private EmployeeDAO employeeDAO;
    private StationDAO stationDAO;
    private Employee employee;

    public EmployeeForm(JFrame parent, Employee employee) {
        super(parent, true);
        this.employeeDAO = new EmployeeDAO();
        this.stationDAO = new StationDAO();
        this.employee = employee;

        setTitle((employee == null) ? "Добавить сотрудника" : "Редактировать сотрудника");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(parent);

        add(new JLabel("ФИО:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Должность:"));
        positionField = new JTextField();
        add(positionField);

        add(new JLabel("Зарплата:"));
        salaryField = new JTextField();
        add(salaryField);

        add(new JLabel("Дата найма (YYYY-MM-DD):"));
        hireDateField = new JTextField();
        add(hireDateField);

        add(new JLabel("Станция:"));
        stationComboBox = new JComboBox<>();
        loadStations();
        add(stationComboBox);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (employee != null) {
            nameField.setText(employee.getFullName());
            positionField.setText(employee.getPosition());
            salaryField.setText(String.valueOf(employee.getSalary()));
            hireDateField.setText(employee.getHireDate().toString());
            stationComboBox.setSelectedItem(employee.getStation());
        }

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadStations() {
        List<Station> stations = stationDAO.getAllStations();
        for (Station station : stations) {
            stationComboBox.addItem(station);
        }
    }

    private void saveEmployee() {
        String name = nameField.getText();
        String position = positionField.getText();
        double salary = Double.parseDouble(salaryField.getText());
        String hireDate = hireDateField.getText();
        Station selectedStation = (Station) stationComboBox.getSelectedItem();

        if (employee == null) {
            employee = new Employee();
        }

        employee.setFullName(name);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setHireDate(java.sql.Date.valueOf(hireDate));
        employee.setStation(selectedStation);

        if (employee.getEmployeeId() == 0) {
            employeeDAO.saveEmployee(employee);
        } else {
            employeeDAO.updateEmployee(employee);
        }

        dispose();
    }
}
