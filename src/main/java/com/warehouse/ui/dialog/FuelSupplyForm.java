package com.warehouse.ui.dialog;

import com.warehouse.dao.FuelSupplyDAO;
import com.warehouse.dao.FuelTypeDAO;
import com.warehouse.dao.SupplierDAO;
import com.warehouse.dao.StationDAO;
import com.warehouse.models.FuelSupply;
import com.warehouse.models.FuelType;
import com.warehouse.models.Supplier;
import com.warehouse.models.Station;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class FuelSupplyForm extends JDialog {
    private JComboBox<FuelType> fuelTypeComboBox;
    private JComboBox<Supplier> supplierComboBox;
    private JComboBox<Station> stationComboBox;
    private JTextField quantityField;
    private JSpinner dateSpinner;
    private FuelSupplyDAO fuelSupplyDAO;
    private FuelSupply fuelSupply;
    private FuelTypeDAO fuelTypeDAO;
    private SupplierDAO supplierDAO;
    private StationDAO stationDAO;

    public FuelSupplyForm(JFrame parent, FuelSupply fuelSupply) {
        super(parent, true);
        this.fuelSupplyDAO = new FuelSupplyDAO();
        this.fuelTypeDAO = new FuelTypeDAO();
        this.supplierDAO = new SupplierDAO();
        this.stationDAO = new StationDAO();
        this.fuelSupply = fuelSupply;

        setTitle((fuelSupply == null) ? "Добавить поставку" : "Редактировать поставку");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(parent);

        add(new JLabel("Топливо:"));
        fuelTypeComboBox = new JComboBox<>();
        loadFuelTypes();
        add(fuelTypeComboBox);

        add(new JLabel("Количество:"));
        quantityField = new JTextField();
        add(quantityField);


        add(new JLabel("Поставщик:"));
        supplierComboBox = new JComboBox<>();
        loadSuppliers();
        add(supplierComboBox);

        add(new JLabel("Станция:"));
        stationComboBox = new JComboBox<>();
        loadStations();
        add(stationComboBox);

        add(new JLabel("Дата поставки:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        add(dateSpinner);
        dateSpinner.setEnabled(false);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (fuelSupply != null) {
            fuelTypeComboBox.setSelectedItem(fuelSupply.getFuelType());
            quantityField.setText(String.valueOf(fuelSupply.getQuantityLiters()));
            supplierComboBox.setSelectedItem(fuelSupply.getSupplier());
            stationComboBox.setSelectedItem(fuelSupply.getStation());
            dateSpinner.setValue(fuelSupply.getSupplyDate());
        }

        saveButton.addActionListener(e -> saveFuelSupply());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadFuelTypes() {
        for (FuelType fuelType : fuelTypeDAO.getAllFuelTypes()) {
            fuelTypeComboBox.addItem(fuelType);
        }
    }

    private void loadSuppliers() {
        for (Supplier supplier : supplierDAO.getAllSuppliers()) {
            supplierComboBox.addItem(supplier);
        }
    }

    private void loadStations() {
        for (Station station : stationDAO.getAllStations()) {
            stationComboBox.addItem(station);
        }
    }

    private void saveFuelSupply() {
        FuelType selectedFuelType = (FuelType) fuelTypeComboBox.getSelectedItem();
        double quantity = Double.parseDouble(quantityField.getText());
        Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
        Station selectedStation = (Station) stationComboBox.getSelectedItem();
        Date supplyDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());

        if (fuelSupply == null) {
            fuelSupply = new FuelSupply();
        }

        fuelSupply.setFuelType(selectedFuelType);
        fuelSupply.setQuantityLiters(quantity);
        fuelSupply.setFuelType(selectedFuelType);
        fuelSupply.setSupplier(selectedSupplier);
        fuelSupply.setStation(selectedStation);
        fuelSupply.setSupplyDate(supplyDate);

        if (fuelSupply.getSupplyId() == 0) {
            fuelSupplyDAO.saveFuelSupply(fuelSupply);
        } else {
            fuelSupplyDAO.updateFuelSupply(fuelSupply);
        }

        dispose();
    }
}
