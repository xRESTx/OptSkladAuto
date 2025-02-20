package com.warehouse.page.adminPage;

import com.warehouse.entities.Service;
import com.warehouse.entities.Tariff;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TariffsPage {

    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Tariff.class)
            .buildSessionFactory();

    public static void showTariffsPage() {
        JFrame frame = new JFrame("Tariff Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Tariff List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных тарифов
        String[] columnNames = {"ID", "Service", "Effective Date", "Rate"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tariffTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tariffTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления тарифа
        JButton addButton = new JButton("Add Tariff");
        addButton.addActionListener(e -> addTariff(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования тарифа
        JButton editButton = new JButton("Edit Tariff");
        editButton.addActionListener(e -> editTariff(tariffTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления тарифа
        JButton deleteButton = new JButton("Delete Tariff");
        deleteButton.addActionListener(e -> deleteTariff(tariffTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadTariffData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadTariffData(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех тарифов из базы данных
            List<Tariff> tariffs = session.createQuery("from Tariff", Tariff.class).list();

            // Добавление данных в таблицу
            for (Tariff tariff : tariffs) {
                tableModel.addRow(new Object[]{
                        tariff.getId(),
                        tariff.getService().getName(),
                        tariff.getEffectiveDate(),
                        tariff.getRate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading tariffs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addTariff(DefaultTableModel tableModel) {
        // Create a dialog for adding a tariff
        JDialog addTariffDialog = new JDialog();
        addTariffDialog.setTitle("Add Tariff");
        addTariffDialog.setSize(400, 350);
        addTariffDialog.setLayout(new BorderLayout());

        // Create a panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        addTariffDialog.add(formPanel, BorderLayout.CENTER);

        // Add a dropdown for selecting the Service
        JLabel serviceLabel = new JLabel("Service: ");
        JComboBox<String> serviceComboBox = new JComboBox<>();
        try (Session session = factory.openSession()) {
            List<Service> services = session.createQuery("from Service", Service.class).list();
            for (Service service : services) {
                serviceComboBox.addItem(service.getName());
            }
        }
        formPanel.add(serviceLabel);
        formPanel.add(serviceComboBox);

        // Add a field for the Effective Date
        JLabel effectiveDateLabel = new JLabel("Effective Date: ");
        JTextField effectiveDateField = new JTextField(LocalDate.now().toString());  // Default to current date
        formPanel.add(effectiveDateLabel);
        formPanel.add(effectiveDateField);

        // Add a field for the Rate
        JLabel rateLabel = new JLabel("Rate: ");
        JTextField rateField = new JTextField();
        formPanel.add(rateLabel);
        formPanel.add(rateField);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String serviceName = (String) serviceComboBox.getSelectedItem();
            String effectiveDateString = effectiveDateField.getText().trim();
            String rateString = rateField.getText().trim();

            if (serviceName.isEmpty() || effectiveDateString.isEmpty() || rateString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate effectiveDate = LocalDate.parse(effectiveDateString);
                double rate = Double.parseDouble(rateString);

                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    // Fetch the service object by name
                    Service service = session.createQuery("from Service where name = :name", Service.class)
                            .setParameter("name", serviceName)
                            .uniqueResult();

                    if (service == null) {
                        JOptionPane.showMessageDialog(null, "Service not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create and save the new Tariff
                    Tariff tariff = new Tariff(service, effectiveDate, rate);
                    session.save(tariff);
                    session.getTransaction().commit();

                    // Add the new tariff to the table
                    tableModel.addRow(new Object[]{
                            tariff.getId(),
                            tariff.getService().getName(),
                            tariff.getEffectiveDate(),
                            tariff.getRate()
                    });

                    addTariffDialog.dispose();  // Close the dialog after saving
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid rate format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> addTariffDialog.dispose());  // Close the dialog without saving

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        addTariffDialog.add(buttonPanel, BorderLayout.SOUTH);

        addTariffDialog.setLocationRelativeTo(null);
        addTariffDialog.setModal(true);
        addTariffDialog.setVisible(true);
    }


    private static void deleteTariff(JTable tariffTable, DefaultTableModel tableModel) {
        int selectedRow = tariffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a tariff to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tariffId = (int) tableModel.getValueAt(selectedRow, 0);

        // Запрос подтверждения удаления
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this tariff?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Tariff tariff = session.get(Tariff.class, tariffId);
                if (tariff != null) {
                    session.delete(tariff);  // Удаление тарифа из базы данных
                    session.getTransaction().commit();

                    // Удаляем тариф из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Tariff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Tariff not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting tariff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void editTariff(JTable tariffTable, DefaultTableModel tableModel) {
        int selectedRow = tariffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a tariff to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tariffId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentService = (String) tableModel.getValueAt(selectedRow, 1);
        LocalDate currentEffectiveDate = (LocalDate) tableModel.getValueAt(selectedRow, 2); // Дата как LocalDate
        Double currentRate = (Double) tableModel.getValueAt(selectedRow, 3); // Ставка как Double

        JTextField serviceField = new JTextField(currentService);
        JTextField effectiveDateField = new JTextField(currentEffectiveDate.toString()); // Преобразуем LocalDate в строку для отображения
        JTextField rateField = new JTextField(String.valueOf(currentRate)); // Ставка как строка

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Service:"));
        panel.add(serviceField);
        panel.add(new JLabel("Effective Date:"));
        panel.add(effectiveDateField);
        panel.add(new JLabel("Rate:"));
        panel.add(rateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Tariff", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newServiceName = serviceField.getText().trim();
            String newEffectiveDateString = effectiveDateField.getText().trim();
            String newRateString = rateField.getText().trim();

            // Проверка на пустые поля
            if (newServiceName.isEmpty() || newEffectiveDateString.isEmpty() || newRateString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Преобразуем строку в LocalDate
                LocalDate newEffectiveDate = LocalDate.parse(newEffectiveDateString);

                // Преобразуем строку в Double
                Double newRate = Double.parseDouble(newRateString);

                // Обновление тарифа в базе данных
                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    Tariff tariff = session.get(Tariff.class, tariffId);
                    if (tariff != null) {
                        // Ищем Service в базе данных по имени
                        Service service = (Service) session.createQuery("from Service where name = :name")
                                .setParameter("name", newServiceName)
                                .uniqueResult();

                        if (service == null) {
                            JOptionPane.showMessageDialog(null, "Service not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Обновляем поля тарифа
                        tariff.setService(service);
                        tariff.setEffectiveDate(newEffectiveDate); // Присваиваем LocalDate
                        tariff.setRate(newRate); // Присваиваем Double

                        session.update(tariff);
                        session.getTransaction().commit();

                        // Обновляем данные в таблице
                        tableModel.setValueAt(newServiceName, selectedRow, 1);
                        tableModel.setValueAt(newEffectiveDate, selectedRow, 2); // Отображаем LocalDate
                        tableModel.setValueAt(newRate, selectedRow, 3); // Отображаем ставку как Double

                        JOptionPane.showMessageDialog(null, "Tariff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Tariff not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use the format YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid rate format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating tariff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private static String selectService() {
        try (Session session = factory.openSession()) {
            List<Service> services = session.createQuery("from Service", Service.class).list();

            // Показать список сервисов в диалоговом окне
            String[] serviceNames = new String[services.size()];
            for (int i = 0; i < services.size(); i++) {
                serviceNames[i] = services.get(i).getName();
            }

            String selectedService = (String) JOptionPane.showInputDialog(null, "Select Service", "Service Selection",
                    JOptionPane.QUESTION_MESSAGE, null, serviceNames, serviceNames[0]);

            return selectedService;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
