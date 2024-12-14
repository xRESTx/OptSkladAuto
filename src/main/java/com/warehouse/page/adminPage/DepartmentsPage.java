package com.warehouse.page.adminPage;

import com.warehouse.entities.Department;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentsPage {

    private static SessionFactory factory;

    static {
        // Инициализация SessionFactory один раз при старте приложения
        factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Department.class)
                .buildSessionFactory();
    }

    public static void showDepartmentsPage() {
        JFrame frame = new JFrame("Department Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Department List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных департаментов
        String[] columnNames = {"ID", "Name", "Description"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable departmentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(departmentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления департамента
        JButton addButton = new JButton("Add Department");
        addButton.addActionListener(e -> addDepartment(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования департамента
        JButton editButton = new JButton("Edit Department");
        editButton.addActionListener(e -> editDepartment(departmentTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления департамента
        JButton deleteButton = new JButton("Delete Department");
        deleteButton.addActionListener(e -> deleteDepartment(departmentTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadDepartmentData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadDepartmentData(DefaultTableModel tableModel) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех департаментов из базы данных
            List<Department> departments = session.createQuery("from Department", Department.class).list();

            // Добавление данных в таблицу
            for (Department department : departments) {
                tableModel.addRow(new Object[] {
                        department.getId(),
                        department.getName(),
                        department.getDescription()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addDepartment(DefaultTableModel tableModel) {
        // Поля для ввода данных
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Department", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем департамент
                Department department = new Department();
                department.setName(name);
                department.setDescription(description);

                session.save(department);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[] {
                        department.getId(),
                        department.getName(),
                        department.getDescription()
                });

                JOptionPane.showMessageDialog(null, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding department: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void editDepartment(JTable departmentTable, DefaultTableModel tableModel) {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a department to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField descriptionField = new JTextField(currentDescription);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Department", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            String newDescription = descriptionField.getText();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Department department = session.get(Department.class, departmentId);
                if (department != null) {
                    department.setName(newName);
                    department.setDescription(newDescription);
                    session.update(department);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newName, selectedRow, 1);
                    tableModel.setValueAt(newDescription, selectedRow, 2);

                    JOptionPane.showMessageDialog(null, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Department not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating department: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteDepartment(JTable departmentTable, DefaultTableModel tableModel) {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a department to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);

        // Подтверждение удаления
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this department?", "Delete Department", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Department department = session.get(Department.class, departmentId);
                if (department != null) {
                    session.delete(department);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Department deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Department not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting department: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
