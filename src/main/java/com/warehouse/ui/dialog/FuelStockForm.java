package com.warehouse.ui.dialog;

import com.warehouse.dao.FuelStockDAO;
import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.FuelStock;
import com.warehouse.models.FuelType;
import com.warehouse.models.Station;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FuelStockForm extends JDialog {
    private JComboBox<Station> stationComboBox;
    private JComboBox<FuelType> fuelTypeComboBox;
    private JTextField quantityField;
    private FuelStockDAO fuelStockDAO;
    private StationDAO stationDAO;
    private FuelTypeDAO fuelTypeDAO;
    private FuelStock fuelStock;

    public FuelStockForm(JFrame parent, FuelStock fuelStock) {
        super(parent, true);
        this.fuelStockDAO = new FuelStockDAO();
        this.stationDAO = new StationDAO();
        this.fuelTypeDAO = new FuelTypeDAO();
        this.fuelStock = fuelStock;

        setTitle((fuelStock == null) ? "Добавить запас топлива" : "Редактировать запас топлива");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2, 10, 10));
        setLocationRelativeTo(parent);

        add(new JLabel("Станция:"));
        stationComboBox = new JComboBox<>();
        loadStations();
        add(stationComboBox);

        add(new JLabel("Тип топлива:"));
        fuelTypeComboBox = new JComboBox<>();
        loadFuelTypes();
        add(fuelTypeComboBox);

        add(new JLabel("Количество (л):"));
        quantityField = new JTextField();
        add(quantityField);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (fuelStock != null) {
            stationComboBox.setSelectedItem(fuelStock.getStation());
            fuelTypeComboBox.setSelectedItem(fuelStock.getFuelType());
            quantityField.setText(String.valueOf(fuelStock.getQuantityLiters()));
        }

        saveButton.addActionListener(e -> saveFuelStock());
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

    private void saveFuelStock() {
        Station selectedStation = (Station) stationComboBox.getSelectedItem();
        FuelType selectedFuelType = (FuelType) fuelTypeComboBox.getSelectedItem();
        double quantity = Double.parseDouble(quantityField.getText());

        if (fuelStock == null) {
            fuelStock = new FuelStock();
        }

        fuelStock.setStation(selectedStation);
        fuelStock.setFuelType(selectedFuelType);
        fuelStock.setQuantityLiters(quantity);

        if (fuelStock.getStockId() == 0) {
            fuelStockDAO.saveFuelStock(fuelStock);
        } else {
            fuelStockDAO.updateFuelStock(fuelStock);
        }

        dispose();
    }
}
