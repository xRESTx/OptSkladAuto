package com.warehouse.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.warehouse.dao.PaymentDAO;
import com.warehouse.models.Payment;
import com.warehouse.ui.dialog.AddPaymentDialog;
import com.warehouse.ui.dialog.EditPaymentDialog;

public class PaymentsPage extends JFrame {
    private JTable paymentsTable;
    private JButton backButton, addButton, editButton, deleteButton, filterButton;
    private JTextField searchField;

    public PaymentsPage() {
        setTitle("Payments Management");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        filterButton = new JButton("Filter/Search");
        addButton = new JButton("Add Payment");
        editButton = new JButton("Edit Payment");
        deleteButton = new JButton("Delete Payment");
        backButton = new JButton("Назад");

//        // Элементы для сортировки
//        JComboBox<String> sortColumnBox = new JComboBox<>(new String[]{
//                "Payment ID", "Order Number", "Amount", "Date", "Status"
//        });
//        sortColumnBox.setSelectedIndex(0);
//
//        JRadioButton ascButton = new JRadioButton("Ascending");
//        JRadioButton descButton = new JRadioButton("Descending");
//        ButtonGroup sortDirectionGroup = new ButtonGroup();
//        sortDirectionGroup.add(ascButton);
//        sortDirectionGroup.add(descButton);
//        ascButton.setSelected(true);

//        JButton sortButton = new JButton("Sort");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new MainPage(); // Возвращаемся на главную страницу
        });

        toolbar.add(backButton);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(filterButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
//        toolbar.add(new JLabel("Sort by:"));
//        toolbar.add(sortColumnBox);
//        toolbar.add(ascButton);
//        toolbar.add(descButton);
//        toolbar.add(sortButton);

        // Таблица платежей
        String[] columnNames = {"Payment ID", "Order Number", "Amount", "Date", "Status"};
        Object[][] data = {}; // Заглушка, данные будут загружаться из базы
        paymentsTable = new JTable(data, columnNames);

        JScrollPane tableScroll = new JScrollPane(paymentsTable);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Слушатели действий
        addButton.addActionListener(e -> {
            new AddPaymentDialog(PaymentsPage.this).setVisible(true);
            loadPayments();
        });

        editButton.addActionListener(e -> {
            editPayment();
        });

        deleteButton.addActionListener(e -> {
            deletePayment();
        });

        filterButton.addActionListener(e -> {
            filterPayments();
        });

//        sortButton.addActionListener(e -> {
//            String selectedColumn = sortColumnBox.getSelectedItem().toString();
//            boolean ascending = ascButton.isSelected();
//
//            List<Payment> sortedPayments = PaymentDAO.sortPayments(selectedColumn, ascending);
//            updatePaymentTable(sortedPayments);
//        });

        loadPayments(); // Загружаем данные платежей при инициализации
    }


    private void editPayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int paymentId = Integer.parseInt(paymentsTable.getValueAt(selectedRow, 0).toString());
            Payment payment = PaymentDAO.findById(paymentId);

// Открываем диалоговое окно для редактирования этого платежа
            new EditPaymentDialog(this, payment).setVisible(true);loadPayments();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a payment to edit.");
        }
    }

    private void deletePayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Получаем paymentId выбранного платежа как целое число
            int paymentId = (int) paymentsTable.getValueAt(selectedRow, 0);

            // Подтверждаем удаление
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this payment?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    // Вызов DAO для удаления платежа
                    PaymentDAO.deletePayment(paymentId);
                    JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
                    loadPayments(); // Перезагружаем список платежей
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error occurred while deleting payment: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a payment to delete.");
        }
    }

    private void filterPayments() {
        String searchTerm = searchField.getText().trim(); // Убираем лишние пробелы
        if (searchTerm.isEmpty()) {
            // Если поле поиска пустое, загружаем все платежи
            loadPayments();
        } else {
            // Вызов DAO для получения отфильтрованных данных
            List<Payment> filteredPayments = PaymentDAO.filterPayments(searchTerm);
            updatePaymentTable(filteredPayments); // Обновляем таблицу с отфильтрованными данными
        }
    }


    // Метод для загрузки всех платежей при инициализации страницы
    private void loadPayments() {
        List<Payment> allPayments = PaymentDAO.findAll();
        updatePaymentTable(allPayments);
    }

    // Метод для обновления таблицы
    private void updatePaymentTable(List<Payment> payments) {
        String[] columnNames = {"Payment ID", "Order Number", "Date", "Status"};

        // Преобразуем List<Payment> в двумерный массив для JTable
        Object[][] data = new Object[payments.size()][4];
        for (int i = 0; i < payments.size(); i++) {
            Payment payment = payments.get(i);
            data[i][0] = payment.getPaymentId();
            data[i][1] = payment.getOrders().getNumberOrder();  // Номер ордера
            data[i][2] = payment.getDatePayment();               // Дата платежа
            data[i][3] = payment.getStatusPayment();             // Статус платежа
        }

        // Устанавливаем модель таблицы с новыми данными
        paymentsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }



}
