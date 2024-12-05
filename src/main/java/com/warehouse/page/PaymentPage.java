package com.warehouse.page;

import com.warehouse.dao.PaymentDAO;
import com.warehouse.entities.Payment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentPage {

    public static void showPaymentPage(String clientFullName, int clientId) {
        JFrame frame = new JFrame("Payments");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Payments for " + clientFullName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Получение данных о платежах для клиента
        List<Payment> payments = new PaymentDAO().getPaymentsByClientId(clientId);

        // Создание модели данных для JTable
        String[] columnNames = {"Payment ID", "Order ID", "Amount", "Payment Date", "Method", "Status"};
        Object[][] tableData = new Object[payments.size()][columnNames.length];

        for (int i = 0; i < payments.size(); i++) {
            Payment payment = payments.get(i);
            tableData[i][0] = payment.getId();
            tableData[i][1] = payment.getOrder().getId();
            tableData[i][2] = payment.getAmount();
            tableData[i][3] = payment.getPaymentDate();
            tableData[i][4] = payment.getPaymentMethod();
            tableData[i][5] = payment.getStatus();
        }

        // Создание JTable
        JTable paymentTable = new JTable(tableData, columnNames);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Кнопка для изменения данных о платеже
        JButton editButton = new JButton("Edit Payment");
        frame.add(editButton, BorderLayout.SOUTH);
        editButton.addActionListener(e -> {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow != -1) {
                int paymentId = (int) tableData[selectedRow][0];
                Payment payment = new PaymentDAO().findById(paymentId);
                showEditPaymentDialog(payment, paymentTable, tableData, selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a payment to edit.");
            }
        });

        frame.setLocationRelativeTo(null); // Центрирование на экране
        frame.setVisible(true);
    }

    private static void showEditPaymentDialog(Payment payment, JTable paymentTable, Object[][] tableData, int selectedRow) {
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Payment");
        editDialog.setSize(400, 300);
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        editDialog.setLayout(new BorderLayout());

        // Создание панели для полей ввода и выпадающих списков
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Панель с полями и отступами
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы для панелей

        // Создание выпадающих списков для предустановленных значений
        String[] paymentMethods = {"Card", "Cash", "Online"};
        JComboBox<String> methodComboBox = new JComboBox<>(paymentMethods);
        methodComboBox.setSelectedItem(payment.getPaymentMethod());  // Устанавливаем текущий метод

        String[] statuses = {"Paid", "Unpaid"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(payment.getStatus());  // Устанавливаем текущий статус

        // Добавляем элементы на панель
        fieldsPanel.add(new JLabel("Payment Method"));
        fieldsPanel.add(methodComboBox);
        fieldsPanel.add(new JLabel("Status"));
        fieldsPanel.add(statusComboBox);

        // Добавление панели в диалог
        editDialog.add(fieldsPanel, BorderLayout.CENTER);

        // Создание панели для кнопок
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Центрируем кнопки с отступами

        // Кнопка для сохранения изменений
        JButton saveButton = new JButton("Save Changes");
        saveButton.setPreferredSize(new Dimension(150, 40));  // Устанавливаем фиксированный размер кнопки
        saveButton.addActionListener(e -> {
            // Получаем выбранные значения из выпадающих списков
            String newMethod = (String) methodComboBox.getSelectedItem();
            String newStatus = (String) statusComboBox.getSelectedItem();

            // Если статус изменяется на "Paid" и текущий статус не "Paid", установим текущую дату в paymentDate
            if ("Paid".equals(newStatus) && !"Paid".equals(payment.getStatus())) {
                payment.setPaymentDate(LocalDate.now());  // Устанавливаем текущую дату
            }

            // Обновление данных
            payment.setPaymentMethod(newMethod);
            payment.setStatus(newStatus);

            // Сохранение изменений в базе
            PaymentDAO paymentDAO = new PaymentDAO();
            paymentDAO.update(payment);

            // Обновляем строку в таблице после сохранения изменений
            tableData[selectedRow][4] = payment.getPaymentMethod();
            tableData[selectedRow][5] = payment.getStatus();
            tableData[selectedRow][3] = payment.getPaymentDate();  // Обновляем дату платежа

            // Обновляем модель таблицы
            paymentTable.repaint();

            JOptionPane.showMessageDialog(editDialog, "Payment updated successfully.");
            editDialog.dispose(); // Закрытие окна
        });

        // Кнопка для отмены изменений
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(150, 40)); // Устанавливаем фиксированный размер кнопки
        cancelButton.addActionListener(e -> editDialog.dispose()); // Закрываем диалог

        // Добавляем кнопки на панель
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        // Добавление панели с кнопками в диалог
        editDialog.add(buttonsPanel, BorderLayout.SOUTH);

        // Устанавливаем расположение и отображаем диалог
        editDialog.setLocationRelativeTo(null);
        editDialog.setVisible(true);
    }
}