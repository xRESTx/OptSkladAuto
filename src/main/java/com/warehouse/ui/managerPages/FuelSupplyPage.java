package com.warehouse.ui.managerPages;

import com.warehouse.dao.FuelSupplyDAO;
import com.warehouse.models.FuelSupply;
import com.warehouse.ui.dialog.FuelSupplyForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FuelSupplyPage extends JFrame {
    private JTable fuelSupplyTable;
    private DefaultTableModel tableModel;
    private FuelSupplyDAO fuelSupplyDAO;

    public FuelSupplyPage() {
        fuelSupplyDAO = new FuelSupplyDAO();

        setTitle("Управление поставками топлива");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаём таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Топливо", "Количество", "Цена", "Дата поставки", "Поставщик", "Станция"});
        fuelSupplyTable = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        fuelSupplyTable.setRowSorter(sorter);
        loadFuelSupplies();

        JScrollPane scrollPane = new JScrollPane(fuelSupplyTable);
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
        addButton.addActionListener(e -> openFuelSupplyForm(null)); // Добавление
        editButton.addActionListener(e -> editSelectedFuelSupply()); // Редактирование
        deleteButton.addActionListener(e -> deleteSelectedFuelSupply()); // Удаление
        backButton.addActionListener(e -> goBack()); // Закрыть окно

        setVisible(true);
    }

    private void goBack() {
        dispose();
        new ManagerMainPage(); // Возвращаемся на главную страницу админа
    }

    // Загрузка данных о поставках топлива в таблицу
    private void loadFuelSupplies() {
        tableModel.setRowCount(0);  // Очистить таблицу перед загрузкой данных
        List<FuelSupply> fuelSupplies = fuelSupplyDAO.getAllFuelSupplies();
        for (FuelSupply supply : fuelSupplies) {
            tableModel.addRow(new Object[]{
                    supply.getSupplyId(),
                    supply.getFuelType().getName(), // Название топлива
                    supply.getQuantityLiters(),
                    supply.getFuelType().getPricePerLiter(),
                    supply.getSupplyDate(),
                    supply.getSupplier().getName(), // Имя поставщика
                    supply.getStation().getName() // Название станции
            });
        }
    }

    // Открывает форму добавления/редактирования поставки
    private void openFuelSupplyForm(FuelSupply supply) {
        new FuelSupplyForm(this, supply);
        loadFuelSupplies();
    }

    // Редактирование выбранной поставки
    private void editSelectedFuelSupply() {
        int selectedRow = fuelSupplyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите поставку для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplyId = (int) tableModel.getValueAt(selectedRow, 0);
        FuelSupply supply = fuelSupplyDAO.getFuelSupplyById(supplyId);
        openFuelSupplyForm(supply);
    }

    // Удаление выбранной поставки
    private void deleteSelectedFuelSupply() {
        int selectedRow = fuelSupplyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите поставку для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplyId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить поставку?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            fuelSupplyDAO.deleteFuelSupply(supplyId);
            loadFuelSupplies(); // Обновить таблицу
        }
    }
}
