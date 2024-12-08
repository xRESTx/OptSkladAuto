package com.warehouse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.AccessoriesDAO;
import com.warehouse.models.Accessories;

public class AccessoriesPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public AccessoriesPage() {
        setTitle("Edit Accessories Category");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Названия столбцов для категории Accessories
        String[] columnNames = {"Articul", "Name", "Subcategory", "Vendor", "Material", "Colour", "Size", "Features", "Appointment"};

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
        List<Accessories> categoryData = AccessoriesDAO.findAll();
        tableModel.setRowCount(0); // Очищаем таблицу
        for (Accessories accessories : categoryData) {
            tableModel.addRow(new Object[]{
                    accessories.getArticul(),
                    accessories.getAutotovar().getName(),
                    accessories.getSubcategory(),
                    accessories.getVendor(),
                    accessories.getMaterial(),
                    accessories.getColour(),
                    accessories.getSize(),
                    accessories.getFeatures(),
                    accessories.getAppointment()
            });
        }
    }

    // Метод для сохранения изменений
    private void saveChanges() {
        int rowCount = tableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0); // articul

            // Создаем новый объект Accessories с обновленными данными
            Accessories updatedAccessories = new Accessories();
            updatedAccessories.setArticul(articul);
            updatedAccessories.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedAccessories.setVendor((String) tableModel.getValueAt(i, 2));
            updatedAccessories.setMaterial((String) tableModel.getValueAt(i, 3));
            updatedAccessories.setColour((String) tableModel.getValueAt(i, 4));
            updatedAccessories.setSize((String) tableModel.getValueAt(i, 5));
            updatedAccessories.setFeatures((String) tableModel.getValueAt(i, 6));
            updatedAccessories.setAppointment((String) tableModel.getValueAt(i, 7));

            // Обновляем данные через DAO
            AccessoriesDAO.updateRow(articul, updatedAccessories);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
