package com.warehouse.page.adminPage;

import com.google.gson.Gson;
import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Coordinate;

import com.warehouse.entities.Department;
import com.warehouse.jsonModel.Root;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpCookie;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApiRequestPage {
    private static SessionFactory factory;

    static {
        // Инициализация SessionFactory один раз при старте приложения
        factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Coordinate.class)
                .buildSessionFactory();
    }
    public static void showApiRequestPage() {
        JFrame frame = new JFrame("Api Request Page");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Whether List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных поставок
        String[] columnNames = {"ID", "latitude", "longitude", "Date", "city", "temperature"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable supplyTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(supplyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления поставки
        JButton addButton = new JButton("Add Coordinate");
        addButton.addActionListener(e -> addCoordinate(tableModel));
        buttonPanel.add(addButton);

        // Кнопка удаления поставки
        JButton deleteButton = new JButton("Delete Coordinate");
        deleteButton.addActionListener(e -> deleteCoordinate(supplyTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadSupplyData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private static void loadSupplyData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Coordinate.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех координат из базы данных
            java.util.List<Coordinate> supplies = session.createQuery("from Coordinate", Coordinate.class).list();

            for (Coordinate supply : supplies) {
                try{
                    double x = supply.getLatitude();
                    double y = supply.getLongitude();
                    String page = "https://api.openweathermap.org/data/2.5/weather?lat=" + x + "&lon=" + y + "&appid=814d790e81c46d8706d3c77ba695679b";
                    Connection connectionPage = Jsoup.connect(page)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0")
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .timeout(10_000);

                    Connection.Response responsePage = connectionPage.execute();

                    String jsons = responsePage.body();

                    System.out.println(jsons);

                    Gson gson = new Gson();
                    Root root = gson.fromJson(jsons,Root.class);



                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    String date = now.format(fmt);
                    String city = root.getName().isEmpty() ? "none" : root.getName();
                    tableModel.addRow(new Object[]{
                            supply.getId(),
                            supply.getLatitude(),
                            supply.getLongitude(),
                            date,
                            city,
                            String.format("%.2f", root.getMain().getTemp()- 273.15)
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading supplies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void deleteCoordinate(JTable coordTable, DefaultTableModel tableModel) {
        int selectedRow = coordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null,
                    "Please select a coordinate to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int coordId = (int) tableModel.getValueAt(selectedRow, 0); // колонка 0 = id
        int ans = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this coordinate?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;

        /* factory лучше держать в поле класса, но оставим как у вас */
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Coordinate.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            Coordinate coord = session.get(Coordinate.class, coordId);
            if (coord != null) {
                session.delete(coord);
                session.getTransaction().commit();

                tableModel.removeRow(selectedRow);   // убираем строку из JTable
                JOptionPane.showMessageDialog(null,
                        "Coordinate deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Coordinate not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error deleting coordinate: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addCoordinate(DefaultTableModel tableModel) {
        JTextField latField = new JTextField();
        JTextField lonField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Latitude:"));
        panel.add(latField);
        panel.add(new JLabel("Longitude:"));
        panel.add(lonField);

        int ans = JOptionPane.showConfirmDialog(
                null, panel, "Add Coordinate", JOptionPane.OK_CANCEL_OPTION);
        if (ans != JOptionPane.OK_OPTION) return;

        double lat, lon;
        try {
            lat = Double.parseDouble(latField.getText().trim());
            lon = Double.parseDouble(lonField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    null, "Enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            Coordinate c = new Coordinate();
            c.setLatitude(lat);
            c.setLongitude(lon);

            session.save(c);
            session.getTransaction().commit();
            try{
                double x = c.getLatitude();
                double y = c.getLongitude();
                String page = "https://api.openweathermap.org/data/2.5/weather?lat=" + x + "&lon=" + y + "&appid=814d790e81c46d8706d3c77ba695679b";
                Connection connectionPage = Jsoup.connect(page)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .timeout(10_000);

                Connection.Response responsePage = connectionPage.execute();

                String jsons = responsePage.body();

                System.out.println(jsons);

                Gson gson = new Gson();
                Root root = gson.fromJson(jsons,Root.class);



                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                String date = now.format(fmt);
                String city = root.getName().isEmpty() ? "none" : root.getName();
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getLatitude(),
                        c.getLongitude(),
                        date,
                        city,
                        String.format("%.2f", root.getMain().getTemp()- 273.15)
                });
            }catch (Exception e){
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                    null, "Coordinate added", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
