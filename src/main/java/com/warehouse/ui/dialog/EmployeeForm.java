package com.warehouse.ui.dialog;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.Employee;
import com.warehouse.models.Station;
import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

public class EmployeeForm extends JDialog {
    private JTextField nameField, positionField, salaryField;
    private JComboBox<Station> stationComboBox;
    private JDatePickerImpl datePicker; // Календарь
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

        add(new JLabel("Дата найма:"));
        datePicker = createDatePicker();
        add(datePicker);

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

            // Устанавливаем текущую дату найма
            if (employee.getHireDate() != null) {
                ((UtilDateModel) datePicker.getModel()).setValue(new java.util.Date(employee.getHireDate().getTime()));
            }

            stationComboBox.setSelectedItem(employee.getStation());
        }

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Сегодня");
        p.put("text.month", "Месяц");
        p.put("text.year", "Год");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
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

        // Получаем дату из JDatePicker
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        Date sqlDate = (selectedDate != null) ? new Date(selectedDate.getTime()) : null;

        Station selectedStation = (Station) stationComboBox.getSelectedItem();

        if (employee == null) {
            employee = new Employee();
        }

        employee.setFullName(name);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setHireDate(sqlDate);
        employee.setStation(selectedStation);

        if (employee.getEmployeeId() == 0) {
            employeeDAO.saveEmployee(employee);
        } else {
            employeeDAO.updateEmployee(employee);
        }

        dispose();
    }

    // Форматтер даты для JDatePicker
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormat.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                return dateFormat.format(((java.util.Date) value));
            }
            return "";
        }
    }
}
