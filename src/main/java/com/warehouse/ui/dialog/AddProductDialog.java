package com.warehouse.ui.dialog;

import com.warehouse.dao.*;
import com.warehouse.models.*;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddProductDialog extends JDialog {

    private JTextField nameField, descriptionField, costField, stockField, minimumField;
    private JComboBox<String> categoryBox;

    public AddProductDialog(JFrame parent) {
        super(parent, "Add Product", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(7, 2));
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{"Chemistry", "Electronics", "Wheels", "Accessories", "Lubricants", "Repair"});
        add(categoryBox);

        add(new JLabel("Cost:"));
        costField = new JTextField();
        add(costField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Minimum:"));
        minimumField = new JTextField();
        add(minimumField);


        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> {
            saveProduct();
//            saveCategoryProduct();
        });
        //saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());
    }
//    private void saveCategoryProduct() {
//        // Получаем максимальный articul из базы данных и увеличиваем его на 1
//        int nextArticul = getNextArticul();
//
//        // Получаем категорию
//        String category = categoryBox.getSelectedItem().toString();
//
//        // Создаем новый объект в зависимости от категории с только артикулом
//        Object newProduct = null;
//        switch (category) {
//            case "Сhemistry":
//                Chemistry chemistry = new Chemistry();
//                chemistry.setArticul(nextArticul);
//                ChemistryDAO.save(chemistry);
//                break;
//            case "Electronics":
//                newProduct = createEmptyElectronicsProduct(nextArticul);
//                break;
//            case "Wheels":
//                newProduct = createEmptyWheelsProduct(nextArticul);
//                break;
//            case "Accessories":
//                newProduct = createEmptyAccessoriesProduct(nextArticul);
//                break;
//            case "Lubricants":
//                newProduct = createEmptyLubricantsProduct(nextArticul);
//                break;
//            case "Repair":
//                newProduct = createEmptyRepairProduct(nextArticul);
//                break;
//        }
//
//        // Вызов DAO для добавления нового продукта (с минимальными данными)
//        if (newProduct != null) {
//            addProductToDatabase(newProduct, category);
//        }
//
//        // Уведомление и закрытие диалога
//        JOptionPane.showMessageDialog(this, "Empty product added successfully!");
//        dispose();
//    }
//
//    private Object createEmptyChemistryProduct(int articul) {
//        Chemistry chemistry = new Chemistry();
//        chemistry.setArticul(articul);
//        return chemistry;
//    }
//
//    private Object createEmptyElectronicsProduct(int articul) {
//        Electronics electronics = new Electronics();
//        electronics.setArticul(articul);
//        return electronics;
//    }
//
//    private Object createEmptyWheelsProduct(int articul) {
//        Wheels wheels = new Wheels();
//        wheels.setArticul(articul);
//        return wheels;
//    }
//
//    private Object createEmptyAccessoriesProduct(int articul) {
//        Accessories accessories = new Accessories();
//        accessories.setArticul(articul);
//        return accessories;
//    }
//
//    private Object createEmptyLubricantsProduct(int articul) {
//        Lubricants lubricants = new Lubricants();
//        lubricants.setArticul(articul);
//        return lubricants;
//    }
//
//    private Object createEmptyRepairProduct(int articul) {
//        Repair repair = new Repair();
//        repair.setArticul(articul);
//        return repair;
//    }
//
//    private void addProductToDatabase(Object product, String category) {
//        // Здесь будет вызов DAO для соответствующей категории
//        switch (category) {
//            case "Chemistry":
//                ChemistryDAO.save((Chemistry) product);
//                break;
//            case "Electronics":
//                ElectronicsDAO.save((Electronics) product);
//                break;
//            case "Wheels":
//                WheelsDAO.save((Wheels) product);
//                break;
//            case "Accessories":
//                AccessoriesDAO.save((Accessories) product);
//                break;
//            case "Lubricants":
//                LubricantsDAO.save((Lubricants) product);
//                break;
//            case "Repair":
//                RepairDAO.save((Repair) product);
//                break;
//        }
//    }

    private void saveProduct() {
        String name = nameField.getText();
        String category = categoryBox.getSelectedItem().toString();
        double cost = 0;
        int stock = 0;
        int minimum = 0;
        String description = descriptionField.getText();

        // Валидация данных
        try {
            cost = Double.parseDouble(costField.getText());
            stock = Integer.parseInt(stockField.getText());
            minimum = Integer.parseInt(minimumField.getText());
            if (minimum <= 0) {
                JOptionPane.showMessageDialog(this, "Minimum must be greater than zero.");
                return;
            }
            if (cost <= 0) {
                JOptionPane.showMessageDialog(this, "Cost must be greater than zero.");
                return;
            }
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock must be greater or equal than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for cost, stock, and minimum.");
            return;
        }

        // Получаем максимальный articul из базы данных и увеличиваем его на 1
        int nextArticul = getNextArticul();

        // Создаем новый объект Autotovar
        Autotovar newProduct = new Autotovar();
        newProduct.setArticul(nextArticul);  // Устанавливаем сгенерированное значение articul
        newProduct.setName(name);
        newProduct.setCategory(category);
        newProduct.setCost(cost);
        newProduct.setRemainingStock(stock);
        newProduct.setDescription(description);
        newProduct.setMinimum(minimum);  // Устанавливаем значение для минимального количества
        newProduct.setAvailable(true);  // Продукт доступен по умолчанию

        // Вызов DAO для добавления нового продукта
        AutotovarDAO.addProduct(newProduct);
        // Уведомление и закрытие диалога
        JOptionPane.showMessageDialog(this, "Product added successfully!");
        dispose();
    }

    private int getNextArticul() {
        int maxArticul = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Получаем максимальное значение articul из базы данных
            String hql = "SELECT MAX(a.articul) FROM Autotovar a";
            Query query = session.createQuery(hql);
            maxArticul = (Integer) ((org.hibernate.query.Query<?>) query).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxArticul + 1;  // Увеличиваем на 1
    }
}
