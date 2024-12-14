package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Supplier;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

public class SuppliersPage {

    public static void showSuppliersPage() {
        JFrame frame = new JFrame("Supplier Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Supplier List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных поставщиков
        String[] columnNames = {"ID", "Name", "Contact Person", "Phone", "Email", "Start Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable supplierTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления поставщика
        JButton addButton = new JButton("Add Supplier");
        addButton.addActionListener(e -> addSupplier(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования поставщика
        JButton editButton = new JButton("Edit Supplier");
        editButton.addActionListener(e -> editSupplier(supplierTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления поставщика
        JButton deleteButton = new JButton("Delete Supplier");
        deleteButton.addActionListener(e -> deleteSupplier(supplierTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadSupplierData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadSupplierData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Supplier.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех поставщиков из базы данных
            List<Supplier> suppliers = session.createQuery("from Supplier", Supplier.class).list();

            // Добавление данных в таблицу
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContactPerson(),
                        supplier.getContactPhone(),
                        supplier.getEmail(),
                        supplier.getStartDate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading suppliers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addSupplier(DefaultTableModel tableModel) {
        JTextField nameField = new JTextField();
        JTextField contactPersonField = new JTextField();
        JTextField contactPhoneField = new JTextField();
        JTextField emailField = new JTextField();

        // Используем JDateChooser для выбора даты
        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");  // Формат отображения даты

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Contact Person:"));
        panel.add(contactPersonField);
        panel.add(new JLabel("Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Partnership Start Date:"));  // Подсказка для формата даты
        panel.add(startDateChooser);  // Добавляем JDateChooser

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String contactPerson = contactPersonField.getText();
            String contactPhone = contactPhoneField.getText();
            String email = emailField.getText();

            // Получаем выбранную дату (если дата не выбрана, будет null)
            Date startDateUtil = startDateChooser.getDate();
            if (startDateUtil == null) {
                JOptionPane.showMessageDialog(null, "Please select a valid start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate startDate = startDateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Проверка на обязательность полей
            if (name.isEmpty() || contactPerson.isEmpty() || contactPhone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Supplier.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем нового поставщика с датой начала сотрудничества
                Supplier supplier = new Supplier(name, contactPhone, email, contactPerson, startDate);
                session.save(supplier);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[]{
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContactPerson(),
                        supplier.getContactPhone(),
                        supplier.getEmail(),
                        supplier.getStartDate()
                });

                JOptionPane.showMessageDialog(null, "Supplier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deleteSupplier(JTable supplierTable, DefaultTableModel tableModel) {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a supplier to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this supplier?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Supplier.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Supplier supplier = session.get(Supplier.class, supplierId);
                if (supplier != null) {
                    session.delete(supplier);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Supplier deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Supplier not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void editSupplier(JTable supplierTable, DefaultTableModel tableModel) {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a supplier to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentContactPerson = (String) tableModel.getValueAt(selectedRow, 2);
        String currentContactPhone = (String) tableModel.getValueAt(selectedRow, 3);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 4);
        LocalDate currentStartDate = (LocalDate) tableModel.getValueAt(selectedRow, 5);

        JTextField nameField = new JTextField(currentName);
        JTextField contactPersonField = new JTextField(currentContactPerson);
        JTextField contactPhoneField = new JTextField(currentContactPhone);
        JTextField emailField = new JTextField(currentEmail);
        JTextField startDateField = new JTextField(currentStartDate.toString());

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Contact Person:"));
        panel.add(contactPersonField);
        panel.add(new JLabel("Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            String newContactPerson = contactPersonField.getText();
            String newContactPhone = contactPhoneField.getText();
            String newEmail = emailField.getText();
            LocalDate newStartDate = LocalDate.parse(startDateField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Supplier.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Supplier supplier = session.get(Supplier.class, supplierId);
                if (supplier != null) {
                    supplier.setName(newName);
                    supplier.setContactPerson(newContactPerson);
                    supplier.setContactPhone(newContactPhone);
                    supplier.setEmail(newEmail);
                    supplier.setStartDate(newStartDate);
                    session.update(supplier);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newName, selectedRow, 1);
                    tableModel.setValueAt(newContactPerson, selectedRow, 2);
                    tableModel.setValueAt(newContactPhone, selectedRow, 3);
                    tableModel.setValueAt(newEmail, selectedRow, 4);
                    tableModel.setValueAt(newStartDate, selectedRow, 5);

                    JOptionPane.showMessageDialog(null, "Supplier updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Supplier not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
