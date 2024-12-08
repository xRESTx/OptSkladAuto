package com.warehouse.ui;

import com.warehouse.dao.DepartmentDAO;
import com.warehouse.models.Department;
import com.warehouse.ui.dialog.EditDepartmentDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentPage extends JFrame {

    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;

    public DepartmentPage() {
        setTitle("Department Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Инициализация модели таблицы
        String[] columnNames = {"Department ID", "Department Name", "Employee Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки таблицы не редактируемы
            }
        };

        departmentTable = new JTable(tableModel);

        // Кнопки
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add Department");
        editButton = new JButton("Edit Department");
        deleteButton = new JButton("Delete Department");
        backButton = new JButton("Back");
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(backButton);

        // Добавляем таблицу и кнопки
        add(new JScrollPane(departmentTable), BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);

        // Логика кнопок
        addButton.addActionListener(e -> addDepartment());
        editButton.addActionListener(e -> editDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new AdminMainPage(); // Возвращаемся на главную страницу
        });

        // Заполнение таблицы
        refreshTable();
    }

    private void refreshTable() {
        // Очистка таблицы
        tableModel.setRowCount(0);

        // Получение департаментов из базы данных
        List<Department> departments = DepartmentDAO.findAll();

        // Добавление строк в таблицу
        for (Department department : departments) {
            tableModel.addRow(new Object[]{
                    department.getDepartmentId(),
                    department.getName(),
                    department.getEmployeeCount()
            });
        }
    }

    private void addDepartment() {
        // Открытие диалога для добавления нового департамента
        new EditDepartmentDialog(this, null).setVisible(true);
        refreshTable(); // Обновление таблицы после добавления
    }

    private void editDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to edit.");
            return;
        }

        // Получение данных выбранного департамента
        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Department department = DepartmentDAO.findById(departmentId);

        // Открытие диалога для редактирования департамента
        new EditDepartmentDialog(this, department).setVisible(true);
        refreshTable(); // Обновление таблицы после редактирования
    }

    private void deleteDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to delete.");
            return;
        }

        // Получаем ID департамента и сам департамент
        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Department department = DepartmentDAO.findById(departmentId);

        // Подтверждение удаления
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the department: " + department.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Удаляем департамент передав сам объект
                DepartmentDAO.delete(department);
                JOptionPane.showMessageDialog(this, "Department deleted successfully.");
                refreshTable(); // Обновляем таблицу
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error while deleting department: " + e.getMessage());
            }
        }
    }


}
