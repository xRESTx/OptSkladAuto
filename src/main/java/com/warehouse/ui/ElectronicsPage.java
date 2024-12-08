package com.warehouse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.ElectronicsDAO;
import com.warehouse.models.Electronics;

public class ElectronicsPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public ElectronicsPage() {
        setTitle("Edit Electronics Category");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Названия столбцов для категории Electronics
        String[] columnNames = {"Articul", "Name", "Subcategory", "Vendor", "Supported", "Warranty", "Permission", "Connect", "Screen"};

        // Создаем таблицу
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Столбец articul нельзя редактировать
            }
        };
        categoryTable = new JTable(tableModel);

        loadCategoryData(); // Загружаем данные категории

        JScrollPane scrollPane = new JScrollPane(categoryTable);

        // Панель действий
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back");
        saveButton = new JButton("Save Changes");

        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> {
            dispose();
            new CategoryPage().setVisible(true);
        });

        toolbar.add(backButton);
        toolbar.add(saveButton);

        // Добавляем компоненты в окно
        add(scrollPane, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);
    }

    // Метод для загрузки данных категории
    private void loadCategoryData() {
        List<Electronics> categoryData = ElectronicsDAO.findAll();
        tableModel.setRowCount(0); // Очищаем таблицу
        for (Electronics electronics : categoryData) {
            tableModel.addRow(new Object[]{
                    electronics.getArticul(),
                    electronics.getAutotovar().getName(),
                    electronics.getSubcategory(),
                    electronics.getVendor(),
                    electronics.getSupported(),
                    electronics.getWarranty(),
                    electronics.getPermission(),
                    electronics.getConnect(),
                    electronics.getScreen()
            });
        }
    }

    // Метод для сохранения изменений
    private void saveChanges() {
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0); // articul

            // Создаем новый объект Electronics с обновленными данными
            Electronics updatedElectronics = new Electronics();
            updatedElectronics.setArticul(articul);
            updatedElectronics.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedElectronics.setVendor((String) tableModel.getValueAt(i, 2));
            updatedElectronics.setSupported((String) tableModel.getValueAt(i, 3));
            updatedElectronics.setWarranty((String) tableModel.getValueAt(i, 4));
            updatedElectronics.setPermission((String) tableModel.getValueAt(i, 5));
            updatedElectronics.setConnect((String) tableModel.getValueAt(i, 6));
            updatedElectronics.setScreen((String) tableModel.getValueAt(i, 7));

            // Обновляем данные через DAO
            ElectronicsDAO.updateRow(articul, updatedElectronics);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
