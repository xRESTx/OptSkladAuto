package com.warehouse.ui.adminPages.categoryPages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.RepairDAO;
import com.warehouse.models.Repair;

import static java.lang.Integer.parseInt;

public class RepairPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public RepairPage() {
        setTitle("Edit Repair Category");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Названия столбцов для категории Repair
        String[] columnNames = {"Articul","Name", "Subcategory", "Vendor", "Weight", "Size", "OEM", "Material", "Compatibility"};

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
        List<Repair> categoryData = RepairDAO.findAll();
        tableModel.setRowCount(0); // Очищаем таблицу
        for (Repair repair : categoryData) {
            tableModel.addRow(new Object[]{
                    repair.getArticul(),
                    repair.getAutotovar().getName(),
                    repair.getSubcategory(),
                    repair.getVendor(),
                    repair.getWeight(),
                    repair.getSize(),
                    repair.getOem(),
                    repair.getMaterial(),
                    repair.getCompatibility()
            });
        }
    }

    // Метод для сохранения изменений
    private void saveChanges() {
        int rowCount = tableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0); // articul

            // Создаем новый объект Repair с обновленными данными
            Repair updatedRepair = new Repair();
            updatedRepair.setArticul(articul);
            updatedRepair.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedRepair.setVendor((String) tableModel.getValueAt(i, 2));
            updatedRepair.setWeight((String) tableModel.getValueAt(i, 3));
            updatedRepair.setSize((String) tableModel.getValueAt(i, 4));
            updatedRepair.setOem(parseInt((String) tableModel.getValueAt(i, 5)));
            updatedRepair.setMaterial((String) tableModel.getValueAt(i, 6));
            updatedRepair.setCompatibility((String) tableModel.getValueAt(i, 7));

            // Обновляем данные через DAO
            RepairDAO.updateRow(articul, updatedRepair);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
