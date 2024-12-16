package com.warehouse.ui.adminPages.categoryPages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.WheelsDAO;
import com.warehouse.models.Wheels;

public class WheelsPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public WheelsPage() {
        setTitle("Edit Wheels Category");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Названия столбцов для категории Wheels
        String[] columnNames = {"Articul", "Name", "Subcategory", "Vendor", "Size", "Protector", "Material", "Colour", "Seasonality"};

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
        List<Wheels> categoryData = WheelsDAO.findAll();
        tableModel.setRowCount(0); // Очищаем таблицу
        for (Wheels wheels : categoryData) {
            tableModel.addRow(new Object[]{
                    wheels.getArticul(),
                    wheels.getAutotovar().getName(),
                    wheels.getSubcategory(),
                    wheels.getVendor(),
                    wheels.getSize(),
                    wheels.getProtector(),
                    wheels.getMaterial(),
                    wheels.getColour(),
                    wheels.getSeasonality()
            });
        }
    }

    // Метод для сохранения изменений
    private void saveChanges() {
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0); // articul

            // Создаем новый объект Wheels с обновленными данными
            Wheels updatedWheels = new Wheels();
            updatedWheels.setArticul(articul);
            updatedWheels.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedWheels.setVendor((String) tableModel.getValueAt(i, 2));
            updatedWheels.setSize((String) tableModel.getValueAt(i, 3));
            updatedWheels.setProtector((String) tableModel.getValueAt(i, 4));
            updatedWheels.setMaterial((String) tableModel.getValueAt(i, 5));
            updatedWheels.setColour((String) tableModel.getValueAt(i, 6));
            updatedWheels.setSeasonality((String) tableModel.getValueAt(i, 7));

            // Обновляем данные через DAO
            WheelsDAO.updateRow(articul, updatedWheels);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
