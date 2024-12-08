package com.warehouse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.ChemistryDAO;
import com.warehouse.models.Chemistry;

public class ChemistryPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public ChemistryPage() {
        setTitle("Edit Chemistry Category");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Названия столбцов для категории Chemistry
        String[] columnNames = {"Articul", "Name", "Subcategory", "Vendor", "Composition", "Concentration", "Target", "Volume", "Expiration Date"};

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
        List<Chemistry> categoryData = ChemistryDAO.findAll();
        tableModel.setRowCount(0); // Очищаем таблицу
        for (Chemistry chemistry : categoryData) {
            tableModel.addRow(new Object[]{
                    chemistry.getArticul(),
                    chemistry.getAutotovar().getName(),
                    chemistry.getSubcategory(),
                    chemistry.getVendor(),
                    chemistry.getCompositions(),
                    chemistry.getConcentration(),
                    chemistry.getTarget(),
                    chemistry.getVolume(),
                    chemistry.getExpirationDate()
            });
        }
    }

    // Метод для сохранения изменений
    private void saveChanges() {
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0); // articul

            // Создаем новый объект Chemistry с обновленными данными
            Chemistry updatedChemistry = new Chemistry();
            updatedChemistry.setArticul(articul);
            updatedChemistry.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedChemistry.setVendor((String) tableModel.getValueAt(i, 2));
            updatedChemistry.setCompositions((String) tableModel.getValueAt(i, 3));
            updatedChemistry.setConcentration((String) tableModel.getValueAt(i, 4));
            updatedChemistry.setTarget((String) tableModel.getValueAt(i, 5));
            updatedChemistry.setVolume((String) tableModel.getValueAt(i, 6));
            updatedChemistry.setExpirationDate((String) tableModel.getValueAt(i, 7));

            // Обновляем данные через DAO
            ChemistryDAO.updateRow(articul, updatedChemistry);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
