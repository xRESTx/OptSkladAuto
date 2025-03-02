package com.warehouse.ui.workerPages;

import com.warehouse.dao.FuelStockDAO;
import com.warehouse.models.FuelStock;
import com.warehouse.ui.dialog.FuelStockForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FuelStockPage extends JFrame {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private FuelStockDAO fuelStockDAO;

    public FuelStockPage() {
        fuelStockDAO = new FuelStockDAO();

        setTitle("Управление запасами топлива");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаём таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Станция", "Тип топлива", "Количество (л)"});
        stockTable = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        stockTable.setRowSorter(sorter);
        loadFuelStock();

        JScrollPane scrollPane = new JScrollPane(stockTable);
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
    // Загрузка данных о запасах топлива в таблицу
    private void loadFuelStock() {
        tableModel.setRowCount(0);
        List<FuelStock> stockList = fuelStockDAO.getAllFuelStocks();
        for (FuelStock stock : stockList) {
            tableModel.addRow(new Object[]{
                    stock.getStockId(),
                    stock.getStation().getName(),
                    stock.getFuelType().getName(),
                    stock.getQuantityLiters()
            });
        }
    }
}
