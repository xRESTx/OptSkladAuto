package com.warehouse;

import com.warehouse.entity.Product;
import com.warehouse.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class WarehouseApp {

    private ProductService productService;

    // Main window components
    private JFrame frame;
    private JTable productTable;
    private DefaultTableModel tableModel;

    public WarehouseApp() {
        productService = new ProductService();
        frame = new JFrame("Wholesale Auto Parts Warehouse");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Buttons
        JButton btnAdd = new JButton("Add Product");
        JButton btnUpdate = new JButton("Update Product");
        JButton btnDelete = new JButton("Delete Product");
        JButton btnViewAll = new JButton("View All Products");

        // Add buttons to the panel
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnViewAll);

        // Add panel to the window
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        // Table to display products
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"SKU", "Name", "Price", "Category", "Description", "Stock"});
        productTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(productTable);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Button listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        btnViewAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllProducts();
            }
        });

        frame.setVisible(true);
    }

    // Method to load all products into the table
    private void loadAllProducts() {
        List<Product> products = productService.getAllProducts();
        tableModel.setRowCount(0); // Clear the table before loading new data

        for (Product product : products) {
            tableModel.addRow(new Object[]{product.getArticul(), product.getName(), product.getPrice(),
                    product.getCategory(), product.getDescription(), product.getNalichie()});
        }
    }

    // Method to add a new product
    private void addProduct() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField nalichieField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Stock:"));
        panel.add(nalichieField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String category = categoryField.getText();
            String description = descriptionField.getText();
            int nalichie = Integer.parseInt(nalichieField.getText());

            Product product = new Product(name, price, category, description, nalichie);
            productService.addProduct(product);
            loadAllProducts();
        }
    }

    // Method to update an existing product
    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row != -1) {
            Long articul = (Long) productTable.getValueAt(row, 0);
            Product product = productService.getProductByArticul(articul);

            JPanel panel = new JPanel(new GridLayout(0, 2));

            JTextField nameField = new JTextField(product.getName());
            JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
            JTextField categoryField = new JTextField(product.getCategory());
            JTextField descriptionField = new JTextField(product.getDescription());
            JTextField nalichieField = new JTextField(String.valueOf(product.getNalichie()));

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Category:"));
            panel.add(categoryField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionField);
            panel.add(new JLabel("Stock:"));
            panel.add(nalichieField);

            int option = JOptionPane.showConfirmDialog(frame, panel, "Update Product", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                product.setName(nameField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setCategory(categoryField.getText());
                product.setDescription(descriptionField.getText());
                product.setNalichie(Integer.parseInt(nalichieField.getText()));

                productService.updateProduct(product);
                loadAllProducts();
            }
        }
    }

    // Method to delete a product
    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row != -1) {
            Long articul = (Long) productTable.getValueAt(row, 0);
            productService.deleteProduct(articul);
            loadAllProducts();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WarehouseApp();
            }
        });
    }
}
