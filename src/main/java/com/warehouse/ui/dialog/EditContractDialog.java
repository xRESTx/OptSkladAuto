package com.warehouse.ui.dialog;

import com.warehouse.dao.ContractDAO;
import com.warehouse.models.Contract;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class EditContractDialog extends JDialog {

    private JTextField startDateField, endDateField, positionField, salaryField;
    private Contract contract;

    public EditContractDialog(Frame parent, Contract contract) {
        super(parent, "Edit Contract", true);
        this.contract = contract;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2));

        // Поля формы
        add(new JLabel("Start Date (yyyy-MM-dd):"));
        startDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(contract.getStartDate()));
        add(startDateField);

        add(new JLabel("End Date (yyyy-MM-dd):"));
        endDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(contract.getEndDate()));
        add(endDateField);

        add(new JLabel("Position:"));
        positionField = new JTextField(contract.getPosition());
        add(positionField);

        add(new JLabel("Salary:"));
        salaryField = new JTextField(contract.getPosition());
        add(salaryField);

        // Кнопки
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveContract(contract));
        cancelButton.addActionListener(e -> dispose());

        add(saveButton);
        add(cancelButton);
        setupContractFields(contract);
    }

    // Этот метод заполняет поля формы значениями из контракта
    private void setupContractFields(Contract contract) {
        // Проверяем, что startDate не является null
        if (contract.getStartDate() != null) {
            startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(contract.getStartDate()));
        } else {
            startDateField.setText(""); // если startDate == null, оставляем поле пустым
        }

        // Проверяем, что endDate не является null
        if (contract.getEndDate() != null) {
            endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(contract.getEndDate()));
        } else {
            endDateField.setText(""); // если endDate == null, оставляем поле пустым
        }

        positionField.setText(contract.getPosition());
        salaryField.setText(String.valueOf(contract.getSalary()));
    }

    // Метод сохранения изменений контракта
    private void saveContract(Contract contract) {
        try {
            // Проверка startDate
            String startDateText = startDateField.getText().trim();
            if (startDateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Start Date must not be empty.");
                return;
            }

            contract.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDateText));

            // Проверка endDate
            String endDateText = endDateField.getText().trim();
            if (endDateText.isEmpty()) {
                contract.setEndDate(null); // Устанавливаем null для пустой даты
            } else {
                contract.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(endDateText));
            }

            contract.setPosition(positionField.getText().trim());
            contract.setSalary(Double.parseDouble(salaryField.getText().trim()));

            // Сохранение контракта
            ContractDAO.updateContract(contract);

            JOptionPane.showMessageDialog(this, "Contract updated successfully!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace(); // Печать ошибки в консоль
            JOptionPane.showMessageDialog(this, "Error updating contract: " + e.getMessage());
        }
    }

}
