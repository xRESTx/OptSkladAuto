package com.warehouse.ui.adminPages;

import com.warehouse.dao.TransactionDAO;
import com.warehouse.models.Transaction;
import com.warehouse.ui.dialog.TransactionForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionsPage extends JFrame {
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private TransactionDAO transactionDAO;

    public TransactionsPage() {
        transactionDAO = new TransactionDAO();

        setTitle("Управление транзакциями");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаем таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Станция", "Тип топлива", "Количество литров", "Общая сумма", "Дата транзакции"});
        transactionsTable = new JTable(tableModel);
        loadTransactions();

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton backButton = new JButton("Назад");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Обработчики событий
        addButton.addActionListener(e -> openTransactionForm(null)); // Добавить новую транзакцию
        editButton.addActionListener(e -> editSelectedTransaction()); // Редактировать выбранную транзакцию
        deleteButton.addActionListener(e -> deleteSelectedTransaction()); // Удалить выбранную транзакцию
        backButton.addActionListener(e -> dispose()); // Закрыть окно

        setVisible(true);
    }

    // Загрузка данных о транзакциях в таблицу
    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactionsList = transactionDAO.getAllTransactions();
        for (Transaction transaction : transactionsList) {
            tableModel.addRow(new Object[]{
                    transaction.getTransactionId(),
                    transaction.getStation().getName(), // Предполагается, что транзакция связана с Station
                    transaction.getFuelType().getName(), // Предполагается, что транзакция связана с FuelType
                    transaction.getQuantityLiters(),
                    transaction.getTotalPrice(),
                    transaction.getTransactionDate()
            });
        }
    }

    // Открывает форму добавления/редактирования транзакции
    private void openTransactionForm(Transaction transaction) {
        new TransactionForm(this, transaction);
    }

    // Редактировать выбранную транзакцию
    private void editSelectedTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        Transaction transaction = transactionDAO.getTransactionById(transactionId);
        openTransactionForm(transaction);
    }

    // Удалить выбранную транзакцию
    private void deleteSelectedTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить эту транзакцию?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            transactionDAO.deleteTransaction(transactionId);
            loadTransactions(); // Обновить таблицу
        }
    }
}
