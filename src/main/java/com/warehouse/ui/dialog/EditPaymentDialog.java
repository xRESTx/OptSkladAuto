package com.warehouse.ui.dialog;

import com.toedter.calendar.JDateChooser;
import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.PaymentDAO;
import com.warehouse.models.Order;
import com.warehouse.models.Payment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EditPaymentDialog extends JDialog {
    private JTextField paymentIdField, orderField;
    private JComboBox<Order> orderComboBox;
    private JDateChooser datePaymentChooser;
    private JTextField statusPaymentField;
    private JButton saveButton, cancelButton;

    private Payment payment;

    // Конструктор EditPaymentDialog, который принимает объект Payment
    public EditPaymentDialog(JFrame parent, Payment payment) {
        super(parent, "Edit Payment", true);
        this.payment = payment;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создание панели для формы
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2));

        // ID платежа (только для чтения)
        formPanel.add(new JLabel("Payment ID:"));
        paymentIdField = new JTextField(String.valueOf(payment.getPaymentId()));
        paymentIdField.setEditable(false);
        formPanel.add(paymentIdField);

        // Заказ
        formPanel.add(new JLabel("Order:"));
        orderField = new JTextField();
        formPanel.add(orderField);
        int orderId = payment.getOrders().getNumberOrder(); // Получаем ID заказа из объекта Payment
        loadOrderDetails(orderField, orderId);
        orderField.setEditable(false);
//        orderComboBox = new JComboBox<>();
//        loadOrders();  // Загружаем заказы в ComboBox
//        orderComboBox.setSelectedItem(payment.getOrders());  // Устанавливаем текущий заказ
//        formPanel.add(orderField);

        // Дата платежа
        formPanel.add(new JLabel("Date Payment:"));
        datePaymentChooser = new JDateChooser();
        datePaymentChooser.setDate(payment.getDatePayment());  // Устанавливаем текущую дату платежа
        formPanel.add(datePaymentChooser);

        // Статус платежа
        formPanel.add(new JLabel("Status Payment:"));
        statusPaymentField = new JTextField(payment.getStatusPayment());  // Устанавливаем текущий статус
        formPanel.add(statusPaymentField);

        // Кнопки для сохранения или отмены
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем компоненты в окно
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик для кнопки "Сохранить"
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayment();  // Сохраняем изменения
            }
        });

        // Обработчик для кнопки "Отмена"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Закрыть окно без сохранения
            }
        });
    }

    private void loadOrderDetails(JTextField orderField, int orderId) {
        // Загружаем заказ из базы данных по его ID
        Order order = OrderDAO.findOrderById(orderId);

        // Если заказ найден, устанавливаем его данные в текстовое поле
        if (order != null) {
            orderField.setText("Order # " + order.getNumberOrder());
        } else {
            orderField.setText("Order not found");
        }

        // Делаем поле только для чтения
        orderField.setEditable(false);
    }

    // Загрузка всех заказов для ComboBox
    private void loadOrders() {
        List<Order> orders = OrderDAO.findAll();  // Получаем все заказы из базы
        DefaultComboBoxModel<Order> model = new DefaultComboBoxModel<>();

        for (Order order : orders) {
            model.addElement(order);  // Добавляем в модель сам объект заказа
        }

        orderComboBox.setModel(model);  // Устанавливаем модель в JComboBox

        // Создаем рендерер для отображения только номера ордера, но сохраняя ссылку на весь объект
        orderComboBox.setRenderer(new ListCellRenderer<Order>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Order> list, Order value, int index, boolean isSelected, boolean cellHasFocus) {
                // Создаем стандартную компоненту для отображения
                JLabel label = new JLabel();
                if (value != null) {
                    label.setText("Order # " + value.getNumberOrder());  // Отображаем только номер ордера
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                label.setOpaque(true);  // Включаем фоновое окрашивание
                return label;
            }
        });
    }

    // Сохранение изменений в базе данных
    private void savePayment() {
        try {
            // Получаем обновленные значения из формы
            Order selectedOrder = (Order) orderComboBox.getSelectedItem();
            java.util.Date selectedDate = datePaymentChooser.getDate();
            String status = statusPaymentField.getText();

            // Обновляем объект Payment
            payment.setOrders(selectedOrder);
            payment.setDatePayment(selectedDate);
            payment.setStatusPayment(status);

            // Сохраняем изменения в базе данных
            PaymentDAO.updatePayment(payment);

            JOptionPane.showMessageDialog(this, "Payment updated successfully!");
            dispose();  // Закрыть окно после успешного сохранения
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error occurred while updating payment: " + e.getMessage());
        }
    }
}
