package com.warehouse.ui.dialog;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.PaymentDAO;
import com.warehouse.models.Order;
import com.warehouse.models.Payment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class AddPaymentDialog extends JDialog {

    private JTextField orderIdField;
    private JTextField dateField;
    private JTextField statusField;
    private JComboBox<Order> orderComboBox;  // Для выбора заказа
    private JButton saveButton;
    private JButton cancelButton;

    public AddPaymentDialog(Frame parent) {
        super(parent, "Add Payment", true);

        // Инициализация компонентов
        orderComboBox = new JComboBox<>();
        dateField = new JTextField(20);
        statusField = new JTextField(20);
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Заполнение ComboBox заказами
        loadOrders();

        // Расположение компонентов в окне
        setLayout(new FlowLayout());
        add(new JLabel("Select Order:"));
        add(orderComboBox);
        add(new JLabel("Date of Payment (yyyy-MM-dd):"));
        add(dateField);
        add(new JLabel("Payment Status:"));
        add(statusField);
        add(saveButton);
        add(cancelButton);

        // Обработчики событий
        saveButton.addActionListener(new SaveButtonListener());
        cancelButton.addActionListener(e -> dispose());  // Закрытие окна без сохранения

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }

    // Метод для загрузки заказов в ComboBox
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


    // Метод для сохранения платежа
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Получаем данные из полей
                Order selectedOrder = (Order) orderComboBox.getSelectedItem();
                String dateStr = dateField.getText();
                String status = statusField.getText();

                // Проверка на корректность данных
                if (selectedOrder == null || dateStr.isEmpty() || status.isEmpty()) {
                    JOptionPane.showMessageDialog(AddPaymentDialog.this, "Please fill all fields.");
                    return;
                }

                // Преобразуем строку даты в объект Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = dateFormat.parse(dateStr);

                // Создаем объект Payment
                Payment payment = new Payment();
                payment.setOrders(selectedOrder);  // Устанавливаем заказ
                payment.setDatePayment(date);      // Устанавливаем дату платежа
                payment.setStatusPayment(status); // Устанавливаем статус платежа

                // Сохраняем платеж в базе данных
                PaymentDAO.savePayment(payment);

                JOptionPane.showMessageDialog(AddPaymentDialog.this, "Payment saved successfully!");
                dispose();  // Закрываем диалоговое окно

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AddPaymentDialog.this, "Error saving payment: " + ex.getMessage());
            }
        }
    }
}
