package com.warehouse.ui.dialog;

import javax.swing.*;
import java.awt.*;
import com.warehouse.models.Order;
import com.warehouse.dao.OrderDAO;
import com.warehouse.models.Employee;

public class UserEditOrderDialog extends JDialog {
    private JTextField customerField, dateField, totalSumField, statusField, employeeField, descriptionField;
    private int orderNumber;

    public UserEditOrderDialog(JFrame parent, int orderNumber) {
        super(parent, "Edit Order", true);
        this.orderNumber = orderNumber;

        // Получаем данные заказа
        Order order = OrderDAO.findOrderByNumber(orderNumber);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found!");
            dispose();
            return;
        }

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2));

        // Поля редактирования
        add(new JLabel("Customer:"));
        customerField = new JTextField(order.getAccount().getFullName());
        customerField.setEditable(false); // Поле не редактируется
        add(customerField);

        add(new JLabel("Date:"));
        dateField = new JTextField(String.valueOf(order.getDateOrder()));
        dateField.setEditable(false);
        add(dateField);

        add(new JLabel("Total Sum:"));
        totalSumField = new JTextField(String.valueOf(order.getTotalSum()));
        totalSumField.setEditable(false); // Сумма не редактируется напрямую
        add(totalSumField);

        add(new JLabel("Status:"));
        statusField = new JTextField(order.getStatus());
        statusField.setEditable(false);
        add(statusField);

        add(new JLabel("Remark:"));
        descriptionField = new JTextField(order.getRemark());
        add(descriptionField);

        // Кнопки
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveOrder());
        cancelButton.addActionListener(e -> dispose());
    }

    private String[] getEmployeeList() {
        // Получаем список сотрудников для выбора
        return OrderDAO.getAllEmployeeLogins(); // Метод для получения логинов сотрудников
    }

    private void saveOrder() {
        try {
            String description = descriptionField.getText();

            // Обновляем данные заказа
            Order updatedOrder = OrderDAO.findOrderByNumber(orderNumber);
            if (updatedOrder == null) {
                JOptionPane.showMessageDialog(this, "Order not found!");
                return;
            }


            updatedOrder.setRemark(description);

            // Сохраняем обновление через DAO
            OrderDAO.updateOrder(updatedOrder);

            JOptionPane.showMessageDialog(this, "Order updated successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage());
        }
    }
}
