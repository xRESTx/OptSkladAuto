package com.warehouse.ui.dialog;

import javax.swing.*;
import java.awt.*;
import com.warehouse.models.Request;
import com.warehouse.models.Employee;
import com.warehouse.dao.RequestDAO;
import com.warehouse.dao.EmployeeDAO;

public class EditRequestDialog extends JDialog {
    private JTextField customerField, dateField, statusField;
    private JTextArea textRequestArea;
    private JComboBox<String> employeeBox;
    private int requestId;

    public EditRequestDialog(JFrame parent, int requestId) {
        super(parent, "Edit Request", true);
        this.requestId = requestId;

        // Получаем данные запроса
        Request request = RequestDAO.findById(requestId);
        if (request == null) {
            JOptionPane.showMessageDialog(this, "Request not found!");
            dispose();
            return;
        }

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2));

        // Поля редактирования
        add(new JLabel("Customer:"));
        customerField = new JTextField(request.getAccount().getFullName());
        customerField.setEditable(false); // Поле не редактируется
        add(customerField);

        add(new JLabel("Date:"));
        dateField = new JTextField(String.valueOf(request.getDateRequest()));
        dateField.setEditable(false);
        add(dateField);

        add(new JLabel("Status:"));
        statusField = new JTextField(request.getStatusOrder());
        add(statusField);

        add(new JLabel("Request Text:"));
        textRequestArea = new JTextArea(request.getTextRequest());
        add(new JScrollPane(textRequestArea));

        add(new JLabel("Employee:"));
        employeeBox = new JComboBox<>(getEmployeeList());
        employeeBox.setSelectedItem(request.getEmployee().getEmployeeLogin());
        add(employeeBox);

        // Кнопки
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveRequest());
        cancelButton.addActionListener(e -> dispose());
    }

    private String[] getEmployeeList() {
        // Получаем список логинов сотрудников
        return EmployeeDAO.getAllEmployeeLogins();
    }

    private void saveRequest() {
        try {
            String status = statusField.getText();
            String textRequest = textRequestArea.getText();
            String employeeLogin = employeeBox.getSelectedItem().toString();

            // Обновляем данные запроса
            Request updatedRequest = RequestDAO.findById(requestId);
            if (updatedRequest == null) {
                JOptionPane.showMessageDialog(this, "Request not found!");
                return;
            }

            updatedRequest.setStatusOrder(status);
            updatedRequest.setTextRequest(textRequest);

            Employee employee = EmployeeDAO.getEmployeeByLogin(employeeLogin);
            if (employee != null) {
                updatedRequest.setEmployee(employee);
            }

            // Сохраняем обновление через DAO
            RequestDAO.update(updatedRequest);

            JOptionPane.showMessageDialog(this, "Request updated successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating request: " + e.getMessage());
        }
    }
}
