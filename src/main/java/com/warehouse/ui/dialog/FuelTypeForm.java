package com.warehouse.ui.dialog;

import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.models.FuelType;

import javax.swing.*;
import java.awt.*;

public class FuelTypeForm extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private FuelTypeDAO fuelTypeDAO;
    private FuelType fuelType;

    public FuelTypeForm(JFrame parent, FuelType fuelType) {
        super(parent, true);
        this.fuelTypeDAO = new FuelTypeDAO();
        this.fuelType = fuelType;

        setTitle((fuelType == null) ? "Добавить тип топлива" : "Редактировать тип топлива");
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Название:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Цена за литр:"));
        priceField = new JTextField();
        add(priceField);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (fuelType != null) {
            nameField.setText(fuelType.getName());
            priceField.setText(String.valueOf(fuelType.getPricePerLiter()));
        }

        saveButton.addActionListener(e -> saveFuelType());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveFuelType() {
        String name = nameField.getText();
        String priceText = priceField.getText();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price = Double.parseDouble(priceText);

        if (fuelType == null) {
            fuelType = new FuelType();
        }

        fuelType.setName(name);
        fuelType.setPricePerLiter(price);

        if (fuelType.getFuelTypeId() == 0) {
            fuelTypeDAO.saveFuelType(fuelType);
        } else {
            fuelTypeDAO.updateFuelType(fuelType);
        }

        dispose();
    }
}
