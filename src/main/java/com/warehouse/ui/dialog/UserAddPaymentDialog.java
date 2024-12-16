package com.warehouse.ui.dialog;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.PaymentDAO;
import com.warehouse.models.Order;
import com.warehouse.models.Payment;
import com.warehouse.utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserAddPaymentDialog extends JDialog {

    private JTextField statusField;
    private JComboBox<Order> orderComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    public UserAddPaymentDialog(Frame parent) {
        super(parent, "Add Payment", true);

        // Инициализация компонентов
        orderComboBox = new JComboBox<>();
        JTextField dateField = new JTextField(20);
        dateField.setEditable(false); // Запрещаем редактирование даты
        statusField = new JTextField(20);
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Устанавливаем текущую дату
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(new Date()));

        // Заполнение ComboBox заказами
        loadOrders();

        // Расположение компонентов в окне
        setLayout(new FlowLayout());
        add(new JLabel("Select Order:"));
        add(orderComboBox);
        add(new JLabel("Date of Payment:"));
        add(dateField);
        add(new JLabel("Payment Status:"));
        add(statusField);
        add(saveButton);
        add(cancelButton);

        // Обработчики событий
        saveButton.addActionListener(new SaveButtonListener(dateField));
        cancelButton.addActionListener(e -> dispose()); // Закрытие окна без сохранения

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }

    // Метод для загрузки заказов в ComboBox
    private void loadOrders() {
        String currentLogin = SessionManager.getCurrentUserLogin(); // Получаем логин текущего пользователя
        List<Order> userOrders = OrderDAO.findOrdersByUserLogin(currentLogin); // Получаем заказы текущего пользователя

        DefaultComboBoxModel<Order> model = new DefaultComboBoxModel<>();
        for (Order order : userOrders) {
            model.addElement(order); // Добавляем заказы в модель
        }

        orderComboBox.setModel(model);

        // Создаем рендерер для отображения только номера ордера, но сохраняя ссылку на весь объект
        orderComboBox.setRenderer(new ListCellRenderer<Order>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Order> list, Order value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel();
                if (value != null) {
                    label.setText("Order # " + value.getNumberOrder());
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                label.setOpaque(true);
                return label;
            }
        });
    }

    // Метод для сохранения платежа
    private class SaveButtonListener implements ActionListener {
        private final JTextField dateField;

        public SaveButtonListener(JTextField dateField) {
            this.dateField = dateField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Получаем данные из полей
                Order selectedOrder = (Order) orderComboBox.getSelectedItem();
                String dateStr = dateField.getText(); // Берем дату из уже заполненного поля
                String status = statusField.getText();

                // Проверка на корректность данных
                if (selectedOrder == null || status.isEmpty()) {
                    JOptionPane.showMessageDialog(UserAddPaymentDialog.this, "Please fill all fields.");
                    return;
                }

                // Преобразуем строку даты в объект Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(dateStr);

                // Создаем объект Payment
                Payment payment = new Payment();
                payment.setOrders(selectedOrder);  // Устанавливаем заказ
                payment.setDatePayment(date);      // Устанавливаем дату платежа
                payment.setStatusPayment(status); // Устанавливаем статус платежа

                // Сохраняем платеж в базе данных
                PaymentDAO.savePayment(payment);

                JOptionPane.showMessageDialog(UserAddPaymentDialog.this, "Payment saved successfully!");
                dispose(); // Закрываем диалоговое окно

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UserAddPaymentDialog.this, "Error saving payment: " + ex.getMessage());
            }
        }
    }
}
