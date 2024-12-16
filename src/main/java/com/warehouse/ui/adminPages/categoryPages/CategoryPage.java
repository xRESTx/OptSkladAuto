package com.warehouse.ui.adminPages.categoryPages;

import com.warehouse.ui.adminPages.AdminMainPage;

import javax.swing.*;
import java.awt.*;

public class CategoryPage extends JFrame {
    private JButton chemistryButton;
    private JButton electronicsButton;
    private JButton wheelsButton;
    private JButton lubricantsButton;
    private JButton accessoriesButton;
    private JButton repairButton;
    private JButton backButton;

    public CategoryPage() {
        setTitle("Categories");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создание кнопок для каждой категории
        chemistryButton = new JButton("Chemistry");
        electronicsButton = new JButton("Electronics");
        wheelsButton = new JButton("Wheels");
        lubricantsButton = new JButton("Lubricants");
        accessoriesButton = new JButton("Accessories");
        repairButton = new JButton("Repair");
        backButton = new JButton("Back");

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 10, 10));
        buttonPanel.add(chemistryButton);
        buttonPanel.add(electronicsButton);
        buttonPanel.add(wheelsButton);
        buttonPanel.add(lubricantsButton);
        buttonPanel.add(accessoriesButton);
        buttonPanel.add(repairButton);
        buttonPanel.add(backButton);

        add(buttonPanel);

        // Слушатели кнопок
        chemistryButton.addActionListener(e -> openChemistryPage("Chemistry"));
        electronicsButton.addActionListener(e -> openElectronicsPage("Electronics"));
        wheelsButton.addActionListener(e -> openWheelsPage("Wheels"));
        lubricantsButton.addActionListener(e -> openLubricantsPage("Lubricants"));
        accessoriesButton.addActionListener(e -> openAccessoriesPage("Accessories"));
        repairButton.addActionListener(e -> openRepairPage("Repair"));
        backButton.addActionListener(e -> {
            dispose();
            new AdminMainPage(); // Возврат на главную страницу администратора
        });
    }

    private void openChemistryPage(String category) {
        dispose();
        new ChemistryPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
    private void openElectronicsPage(String category) {
        dispose();
        new ElectronicsPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
    private void openWheelsPage(String category) {
        dispose();
        new WheelsPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
    private void openLubricantsPage(String category) {
        dispose();
        new LubricantsPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
    private void openAccessoriesPage(String category) {
        dispose();
        new AccessoriesPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
    private void openRepairPage(String category) {
        dispose();
        new RepairPage().setVisible(true); // Открываем страницу товаров для выбранной категории
    }
}
