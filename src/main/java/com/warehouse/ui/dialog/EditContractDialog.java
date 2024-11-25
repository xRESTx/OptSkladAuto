package com.warehouse.ui.dialog;

import com.warehouse.dao.ContractDAO;
import com.warehouse.models.Contract;
import com.warehouse.ui.EmployeesPage;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditContractDialog extends JDialog {
//
//    private JTextField contractNumberField, startDateField, endDateField;
//    private Contract contract;
//
//    public EditContractDialog(EmployeesPage parent) {
//        super(parent, "Edit Contract", true);
//        setSize(400, 300);
//        setLayout(new BorderLayout());
//        setLocationRelativeTo(parent);
//
//        this.contract = contract;
//
//        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
//
//        // Поля формы
//        formPanel.add(new JLabel("Contract Number:"));
//        contractNumberField = new JTextField(String.valueOf(contract.getContractNumber()));
//        formPanel.add(contractNumberField);
//
//        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"));
//        startDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(contract.getStartDate()));
//        formPanel.add(startDateField);
//
//        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"));
//        endDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(contract.getEndDate()));
//        formPanel.add(endDateField);
//
//        add(formPanel, BorderLayout.CENTER);
//
//        // Кнопки
//        JPanel buttonPanel = new JPanel();
//        JButton saveButton = new JButton("Save");
//        JButton cancelButton = new JButton("Cancel");
//
//        saveButton.addActionListener(e -> saveContract());
//        cancelButton.addActionListener(e -> dispose());
//
//        buttonPanel.add(saveButton);
//        buttonPanel.add(cancelButton);
//
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private void saveContract() {
//        try {
//            contract.setContractNumber(Integer.parseInt(contractNumberField.getText().trim()));
//
//            String startDateText = startDateField.getText().trim();
//            if (!startDateText.isEmpty() && !startDateText.equals("Not set")) {
//                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateText);
//                contract.setStartDate(startDate);
//            } else {
//                contract.setStartDate(null);  // Если дата не указана, можно оставить null
//            }
//
//            String endDateText = endDateField.getText().trim();
//            if (!endDateText.isEmpty() && !endDateText.equals("Not set")) {
//                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateText);
//                contract.setEndDate(endDate);
//            } else {
//                contract.setEndDate(null);  // Если дата не указана, можно оставить null
//            }
//
//            // Сохраняем изменения
//            ContractDAO.saveOrUpdate(contract);
//            JOptionPane.showMessageDialog(this, "Contract updated successfully!");
//            dispose();
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Error updating contract: " + ex.getMessage());
//            ex.printStackTrace(); // Для диагностики
//        }
//    }


}
