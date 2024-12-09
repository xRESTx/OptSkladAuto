package com.warehouse.ui.dialog;

import com.warehouse.models.*;
import com.warehouse.dao.*;

import javax.swing.*;
import java.awt.*;

public class ProductDetailsDialog extends JDialog {
    private Autotovar autotovar;

    public ProductDetailsDialog(JFrame parent, Autotovar autotovar) {
        this.autotovar = autotovar;
        setTitle("Product Details");
        setSize(600, 800);
        setLocationRelativeTo(parent);

        // Создаём таблицу данных
        String[][] allData = collectData();
        String[] columnNames = {"Attribute", "Value"};
        JTable table = new JTable(allData, columnNames);
        table.setEnabled(false);
        table.setTableHeader(null);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Добавляем таблицу и фото
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Панель с фото
        // Панель для изображения с фоном и рамкой
        JPanel photoPanel = new JPanel();
        photoPanel.setPreferredSize(new Dimension(150, 150));
        photoPanel.setBackground(Color.LIGHT_GRAY); // Устанавливаем фон
        photoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        String photoPath = "C:\\Users\\REST\\IdeaProjects\\OptSkladAuto\\src\\main\\resources\\photo\\" + autotovar.getArticul() + ".jpg";
        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon imageIcon = new ImageIcon(photoPath);
            if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(image));
            } else {
                throw new Exception("Image not found or invalid");
            }
        } catch (Exception e) {
            photoLabel.setText("No Image");
        }

        photoPanel.add(photoLabel);
        mainPanel.add(photoPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Кнопка закрытия
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Метод для сбора всех данных в одну таблицу
    private String[][] collectData() {
        // Основные данные
        java.util.List<String[]> data = new java.util.ArrayList<>();
        data.add(new String[]{"Артикул товара", String.valueOf(autotovar.getArticul())});
        data.add(new String[]{"Название товара", autotovar.getName()});
        data.add(new String[]{"Описание товара", autotovar.getDescription()});
        data.add(new String[]{"Категория товара", autotovar.getCategory()});
        data.add(new String[]{"Цена", String.valueOf(autotovar.getCost())});
        data.add(new String[]{"Минимальное количество к заказу", String.valueOf(autotovar.getMinimum())});
        data.add(new String[]{"Наличие", autotovar.isAvailable() ? "Да" : "Нет"});

        // Данные категории
        switch (autotovar.getCategory()) {
            case "Химия": {
                Chemistry chemistry = ChemistryDAO.findByArticul(autotovar.getArticul());
                if (chemistry != null) {
                    data.add(new String[]{"Subcategory", chemistry.getSubcategory()});
                    data.add(new String[]{"Vendor", chemistry.getVendor()});
                    data.add(new String[]{"Volume", chemistry.getVolume()});
                    data.add(new String[]{"Compositions", chemistry.getCompositions()});
                    data.add(new String[]{"Target", chemistry.getTarget()});
                    data.add(new String[]{"Concentration", chemistry.getConcentration()});
                    data.add(new String[]{"Expiration Date", chemistry.getExpirationDate()});
                }
                break;
            }
            case "Электроника": {
                Electronics electronics = ElectronicsDAO.findByArticul(autotovar.getArticul());
                if (electronics != null) {
                    data.add(new String[]{"Subcategory", electronics.getSubcategory()});
                    data.add(new String[]{"Vendor", electronics.getVendor()});
                    data.add(new String[]{"Screen", electronics.getScreen()});
                    data.add(new String[]{"Connect", electronics.getConnect()});
                    data.add(new String[]{"Supported", electronics.getSupported()});
                    data.add(new String[]{"Permission", electronics.getPermission()});
                    data.add(new String[]{"Warranty", electronics.getWarranty()});
                }
                break;
            }
            case "Шины": {
                Wheels wheels = WheelsDAO.findByArticul(autotovar.getArticul());
                if (wheels != null) {
                    data.add(new String[]{"Vendor", wheels.getVendor()});
                    data.add(new String[]{"Subcategory", wheels.getSubcategory()});
                    data.add(new String[]{"Colour", wheels.getColour()});
                    data.add(new String[]{"Size", wheels.getSize()});
                    data.add(new String[]{"Material", wheels.getMaterial()});
                    data.add(new String[]{"Protector", wheels.getProtector()});
                    data.add(new String[]{"Seasonality", wheels.getSeasonality()});
                }
                break;
            }
            case "Аксессуары": {
                Accessories accessories = AccessoriesDAO.findByArticul(autotovar.getArticul());
                if (accessories != null) {
                    data.add(new String[]{"Vendor", accessories.getVendor()});
                    data.add(new String[]{"Subcategory", accessories.getSubcategory()});
                    data.add(new String[]{"Features", accessories.getFeatures()});
                    data.add(new String[]{"Size", accessories.getSize()});
                    data.add(new String[]{"Material", accessories.getMaterial()});
                    data.add(new String[]{"Appointment", accessories.getAppointment()});
                    data.add(new String[]{"Colour", accessories.getColour()});
                }
                break;
            }
            case "Смазочные материалы": {
                Lubricants lubricants = LubricantsDAO.findByArticul(autotovar.getArticul());
                if (lubricants != null) {
                    data.add(new String[]{"Vendor", lubricants.getVendor()});
                    data.add(new String[]{"Subcategory", lubricants.getSubcategory()});
                    data.add(new String[]{"Standarts", lubricants.getStandarts()});
                    data.add(new String[]{"Expiration", lubricants.getExpiration()});
                    data.add(new String[]{"Volume", lubricants.getVolume()});
                    data.add(new String[]{"Temperature", lubricants.getTemperature()});
                    data.add(new String[]{"Viscosity", lubricants.getViscosity()});
                }
                break;
            }
            case "Ремонт": {
                Repair repair = RepairDAO.findByArticul(autotovar.getArticul());
                if (repair != null) {
                    data.add(new String[]{"Vendor", repair.getVendor()});
                    data.add(new String[]{"Subcategory", repair.getSubcategory()});
                    data.add(new String[]{"Material", repair.getMaterial()});
                    data.add(new String[]{"Weight", repair.getWeight()});
                    data.add(new String[]{"Size", repair.getSize()});
                    data.add(new String[]{"Compatibility", repair.getCompatibility()});
                    data.add(new String[]{"OEM", String.valueOf(repair.getOem())});
                }
                break;
            }
            default:
                data.add(new String[]{"Unknown Category", autotovar.getCategory()});
        }

        // Преобразуем список в массив
        return data.toArray(new String[0][0]);
    }
}
