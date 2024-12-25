package com.warehouse.ui.workerPages;

import com.warehouse.dao.AutotovarDAO;
import com.warehouse.models.Autotovar;
import com.warehouse.ui.dialog.EditProductDialog;
import com.warehouse.ui.managerPages.ManagerMainPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WorkerProductPage extends JFrame {
    private JTable productsTable;
    private JButton backButton, filterButton;
    private JTextField searchField;

    public WorkerProductPage() {
        setTitle("Products Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        filterButton = new JButton("Filter/Search");
        backButton = new JButton("Назад");

        // Элементы для сортировки
        JComboBox<String> sortColumnBox = new JComboBox<>(new String[]{
                "Articul", "Name", "Category", "Cost", "Available", "Remaining_stock", "Minimum"
        });
        sortColumnBox.setSelectedIndex(0);

        JRadioButton ascButton = new JRadioButton("Ascending");
        JRadioButton descButton = new JRadioButton("Descending");
        ButtonGroup sortDirectionGroup = new ButtonGroup();
        sortDirectionGroup.add(ascButton);
        sortDirectionGroup.add(descButton);
        ascButton.setSelected(true);

        JButton sortButton = new JButton("Sort");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new WorkerMainPage(); // Возвращаемся на главную страницу
        });

        toolbar.add(backButton);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(filterButton);
        toolbar.add(new JLabel("Sort by:"));
        toolbar.add(sortColumnBox);
        toolbar.add(ascButton);
        toolbar.add(descButton);
        toolbar.add(sortButton);

        // Таблица продуктов
        String[] columnNames = {"Articul", "Name", "Category", "Cost", "Available", "Remaining stock"};
        Object[][] data = {}; // Заглушка, данные будут загружаться из базы
        productsTable = new JTable(data, columnNames);

        JScrollPane tableScroll = new JScrollPane(productsTable);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Слушатели действий
        filterButton.addActionListener(e -> {
            filterProducts();
        });

        sortButton.addActionListener(e -> {
            String selectedColumn = sortColumnBox.getSelectedItem().toString();
            boolean ascending = ascButton.isSelected();

            List<Autotovar> sortedProducts = AutotovarDAO.sortProducts(selectedColumn, ascending);
            updateProductTable(sortedProducts);
        });

        loadProducts(); // Загружаем данные продуктов при инициализации
    }


    private void editProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int articul = Integer.parseInt(productsTable.getValueAt(selectedRow, 0).toString());
            new EditProductDialog(this, articul).setVisible(true);
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
        }
    }

    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Получаем articul выбранного продукта как целое число
            int articul = (int) productsTable.getValueAt(selectedRow, 0);

            // Подтверждаем удаление
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this product?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    // Вызов DAO для удаления товара
                    AutotovarDAO.deleteProduct(articul);
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    loadProducts(); // Перезагружаем список продуктов
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error occurred while deleting product: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
        }
    }






    private void filterProducts() {
        String searchTerm = searchField.getText().trim(); // Убираем лишние пробелы
        if (searchTerm.isEmpty()) {
            // Если поле поиска пустое, загружаем все продукты
            loadProducts();
        } else {
            // Вызов DAO для получения отфильтрованных данных
            List<Autotovar> filteredProducts = AutotovarDAO.filterProducts(searchTerm);
            updateProductTable(filteredProducts); // Обновляем таблицу с отфильтрованными данными
        }
    }


    // Метод для загрузки всех продуктов при инициализации страницы
    private void loadProducts() {
        List<Autotovar> allProducts = AutotovarDAO.findAll();
        updateProductTable(allProducts);
    }

    // Метод для обновления таблицы
    private void updateProductTable(List<Autotovar> products) {
        String[] columnNames = {"Articul", "Name", "Category", "Cost", "Available", "Stock", "Minimum", "Description"};

        // Преобразуем List<Autotovar> в двумерный массив для JTable
        Object[][] data = new Object[products.size()][8]; // Теперь 8 столбцов
        for (int i = 0; i < products.size(); i++) {
            Autotovar product = products.get(i);
            data[i][0] = product.getArticul();
            data[i][1] = product.getName();
            data[i][2] = product.getCategory();
            data[i][3] = product.getCost();
            data[i][4] = product.isAvailable();
            data[i][5] = product.getRemainingStock();
            data[i][6] = product.getMinimum(); // Добавляем minimum
            data[i][7] = product.getDescription(); // Добавляем description
        }

        // Устанавливаем модель таблицы с новыми данными
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещает редактирование всех ячеек
            }
        };
        productsTable.setModel(model);
    }

}
