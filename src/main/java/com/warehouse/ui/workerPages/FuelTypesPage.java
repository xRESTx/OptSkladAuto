package com.warehouse.ui.workerPages;

import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.models.FuelType;
import com.warehouse.ui.dialog.FuelTypeForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        fuelTypesTable.setRowSorter(sorter);
        loadFuelTypes();

        JScrollPane scrollPane = new JScrollPane(fuelTypesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Назад");

        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Обработчики событий
        backButton.addActionListener(e -> goBack()); // Закрыть окно

        setVisible(true);
    }
    private void goBack() {
        dispose();
        new WorkerMainPage();
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
}
