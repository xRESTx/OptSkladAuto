package com.warehouse.ui.adminPages.categoryPages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.warehouse.dao.LubricantsDAO;
import com.warehouse.models.Lubricants;

public class LubricantsPage extends JFrame {
    private JTable categoryTable;
    private JButton backButton, saveButton;
    private DefaultTableModel tableModel;

    public LubricantsPage() {
        setTitle("Edit Lubricants Category");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Articul", "Name", "Subcategory", "Vendor", "Temperature", "Standards", "Expiration", "Volume", "Viscosity"};

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        categoryTable = new JTable(tableModel);

        loadCategoryData();

        JScrollPane scrollPane = new JScrollPane(categoryTable);

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

        add(scrollPane, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);
    }

    private void loadCategoryData() {
        List<Lubricants> categoryData = LubricantsDAO.findAll();
        tableModel.setRowCount(0);
        for (Lubricants lubricants : categoryData) {
            tableModel.addRow(new Object[]{
                    lubricants.getArticul(),
                    lubricants.getAutotovar().getName(),
                    lubricants.getSubcategory(),
                    lubricants.getVendor(),
                    lubricants.getTemperature(),
                    lubricants.getStandarts(),
                    lubricants.getExpiration(),
                    lubricants.getVolume(),
                    lubricants.getViscosity()
            });
        }
    }

    private void saveChanges() {
        int rowCount = tableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            int articul = (int) tableModel.getValueAt(i, 0);

            Lubricants updatedLubricants = new Lubricants();
            updatedLubricants.setArticul(articul);
            updatedLubricants.setSubcategory((String) tableModel.getValueAt(i, 1));
            updatedLubricants.setVendor((String) tableModel.getValueAt(i, 2));
            updatedLubricants.setTemperature((String) tableModel.getValueAt(i, 3));
            updatedLubricants.setStandarts((String) tableModel.getValueAt(i, 4));
            updatedLubricants.setExpiration((String) tableModel.getValueAt(i, 5));
            updatedLubricants.setVolume((String) tableModel.getValueAt(i, 6));
            updatedLubricants.setViscosity((String) tableModel.getValueAt(i, 7));

            LubricantsDAO.updateRow(articul, updatedLubricants);
        }

        JOptionPane.showMessageDialog(this, "Changes saved successfully!");
    }
}
