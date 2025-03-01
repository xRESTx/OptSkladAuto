package com.warehouse.ui.adminPages;

import com.warehouse.dao.SupplierDAO;
import com.warehouse.models.Supplier;
import com.warehouse.ui.dialog.SupplierForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SuppliersPage extends JFrame {
    private JTable suppliersTable;
    private DefaultTableModel tableModel;
    private SupplierDAO supplierDAO;

    public SuppliersPage() {
        supplierDAO = new SupplierDAO();

        setTitle("Управление поставщиками");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаем таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Название", "Контактное лицо", "Телефон", "Email"});
        suppliersTable = new JTable(tableModel);
        loadSuppliers();

        JScrollPane scrollPane = new JScrollPane(suppliersTable);
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
        addButton.addActionListener(e -> openSupplierForm(null)); // Добавить новый поставщик
        editButton.addActionListener(e -> editSelectedSupplier()); // Редактировать выбранного поставщика
        deleteButton.addActionListener(e -> deleteSelectedSupplier()); // Удалить выбранного поставщика
        backButton.addActionListener(e -> dispose()); // Закрыть окно

        setVisible(true);
    }

    // Загрузка данных о поставщиках в таблицу
    private void loadSuppliers() {
        tableModel.setRowCount(0);
        List<Supplier> suppliersList = supplierDAO.getAllSuppliers();
        for (Supplier supplier : suppliersList) {
            tableModel.addRow(new Object[]{
                    supplier.getSupplierId(),
                    supplier.getName(),
                    supplier.getContactPerson(),
                    supplier.getPhoneNumber(),
                    supplier.getEmail()
            });
        }
    }

    // Открывает форму добавления/редактирования поставщика
    private void openSupplierForm(Supplier supplier) {
        new SupplierForm(this, supplier);
    }

    // Редактировать выбранного поставщика
    private void editSelectedSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        Supplier supplier = supplierDAO.getSupplierById(supplierId);
        openSupplierForm(supplier);
    }

    // Удалить выбранного поставщика
    private void deleteSelectedSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить этого поставщика?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            supplierDAO.deleteSupplier(supplierId);
            loadSuppliers(); // Обновить таблицу
        }
    }
}
