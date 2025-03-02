package com.warehouse.ui.adminPages;

import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.models.FuelType;
import com.warehouse.ui.dialog.FuelTypeForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FuelTypesPage extends JFrame {
    private JTable fuelTypesTable;
    private DefaultTableModel tableModel;
    private FuelTypeDAO fuelTypeDAO;

    public FuelTypesPage() {
        fuelTypeDAO = new FuelTypeDAO();

        setTitle("Управление типами топлива");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаем таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Название", "Цена за литр"});
        fuelTypesTable = new JTable(tableModel);
        loadFuelTypes();

        JScrollPane scrollPane = new JScrollPane(fuelTypesTable);
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
        addButton.addActionListener(e -> openFuelTypeForm(null)); // Добавить новый тип топлива
        editButton.addActionListener(e -> editSelectedFuelType()); // Редактировать выбранный тип топлива
        deleteButton.addActionListener(e -> deleteSelectedFuelType()); // Удалить выбранный тип топлива
        backButton.addActionListener(e -> goBack()); // Закрыть окно

        setVisible(true);
    }
    private void goBack() {
        dispose();
        new AdminMainPage();
    }
    // Загрузка данных о типах топлива в таблицу
    private void loadFuelTypes() {
        tableModel.setRowCount(0);
        List<FuelType> fuelTypesList = fuelTypeDAO.getAllFuelTypes();
        for (FuelType fuelType : fuelTypesList) {
            tableModel.addRow(new Object[]{
                    fuelType.getFuelTypeId(),
                    fuelType.getName(),
                    fuelType.getPricePerLiter()
            });
        }
    }

    // Открывает форму добавления/редактирования типа топлива
    private void openFuelTypeForm(FuelType fuelType) {
        new FuelTypeForm(this, fuelType);
    }

    // Редактировать выбранный тип топлива
    private void editSelectedFuelType() {
        int selectedRow = fuelTypesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fuelTypeId = (int) tableModel.getValueAt(selectedRow, 0);
        FuelType fuelType = fuelTypeDAO.getFuelTypeById(fuelTypeId);
        openFuelTypeForm(fuelType);
    }

    // Удалить выбранный тип топлива
    private void deleteSelectedFuelType() {
        int selectedRow = fuelTypesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fuelTypeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить этот тип топлива?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            fuelTypeDAO.deleteFuelType(fuelTypeId);
            loadFuelTypes(); // Обновить таблицу
        }
    }
}
