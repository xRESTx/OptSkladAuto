package com.warehouse.ui.dialog;

import com.warehouse.dao.TransactionDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.models.Transaction;
import com.warehouse.models.Station;
import com.warehouse.models.FuelType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransactionForm extends JDialog {
    private JTextField quantityField;
    private JTextField totalPriceField;
    private JComboBox<Station> stationComboBox;
    private JComboBox<FuelType> fuelTypeComboBox;
    private TransactionDAO transactionDAO;
    private StationDAO stationDAO;
    private FuelTypeDAO fuelTypeDAO;
    private Transaction transaction;

    public TransactionForm(JFrame parent, Transaction transaction) {
        super(parent, true);
        this.transactionDAO = new TransactionDAO();
        this.stationDAO = new StationDAO();
        this.fuelTypeDAO = new FuelTypeDAO();
        this.transaction = transaction;

        setTitle((transaction == null) ? "Добавить транзакцию" : "Редактировать транзакцию");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Станция:"));
        stationComboBox = new JComboBox<>();
        loadStations();
        add(stationComboBox);

        add(new JLabel("Тип топлива:"));
        fuelTypeComboBox = new JComboBox<>();
        loadFuelTypes();
        add(fuelTypeComboBox);

        add(new JLabel("Количество литров:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Общая сумма:"));
        totalPriceField = new JTextField();
        add(totalPriceField);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (transaction != null) {
            stationComboBox.setSelectedItem(transaction.getStation());
            fuelTypeComboBox.setSelectedItem(transaction.getFuelType());
            quantityField.setText(String.valueOf(transaction.getQuantityLiters()));
            totalPriceField.setText(String.valueOf(transaction.getTotalPrice()));
        }

        saveButton.addActionListener(e -> saveTransaction());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadStations() {
        List<Station> stations = stationDAO.getAllStations();
        for (Station station : stations) {
            stationComboBox.addItem(station);
        }
    }

    private void loadFuelTypes() {
        List<FuelType> fuelTypes = fuelTypeDAO.getAllFuelTypes();
        for (FuelType fuelType : fuelTypes) {
            fuelTypeComboBox.addItem(fuelType);
        }
    }

    private void saveTransaction() {
        Station station = (Station) stationComboBox.getSelectedItem();
        FuelType fuelType = (FuelType) fuelTypeComboBox.getSelectedItem();
        double quantityLiters = Double.parseDouble(quantityField.getText());
        double totalPrice = Double.parseDouble(totalPriceField.getText());

        if (station == null || fuelType == null || quantityLiters <= 0 || totalPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены корректно", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (transaction == null) {
            transaction = new Transaction();
        }

        transaction.setStation(station);
        transaction.setFuelType(fuelType);
        transaction.setQuantityLiters(quantityLiters);
        transaction.setTotalPrice(totalPrice);

        if (transaction.getTransactionId() == 0) {
            transactionDAO.saveTransaction(transaction);
        } else {
            transactionDAO.updateTransaction(transaction);
        }

        dispose();
    }
}
