package com.warehouse.ui.adminPages;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Employee;
import com.warehouse.models.Station;
import com.warehouse.ui.dialog.EmployeeForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeesPage extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private EmployeeDAO employeeDAO;

    public EmployeesPage() {
        employeeDAO = new EmployeeDAO();

        setTitle("Управление сотрудниками");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Создаём таблицу
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Имя", "Должность", "Зарплата", "Дата найма", "Станция"});
        employeeTable = new JTable(tableModel);
        loadEmployees();

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton backButton = new JButton("Назад");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Обработчики событий
        addButton.addActionListener(e -> openEmployeeForm(null)); // Добавление
        editButton.addActionListener(e -> editSelectedEmployee()); // Редактирование
        deleteButton.addActionListener(e -> deleteSelectedEmployee()); // Удаление
        backButton.addActionListener(e -> dispose()); // Закрыть окно

        setVisible(true);
    }

    // Загрузка данных сотрудников в таблицу
    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getPosition(),
                    emp.getSalary(),
                    emp.getHireDate(),
                    (emp.getStation() != null) ? emp.getStation().getName() : "Нет"
            });
        }
    }

    // Открывает форму добавления/редактирования сотрудника
    private void openEmployeeForm(Employee employee) {
        new EmployeeForm(this, employee);
    }

    // Редактирование выбранного сотрудника
    private void editSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите сотрудника для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        openEmployeeForm(employee);
    }

    // Удаление выбранного сотрудника
    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите сотрудника для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить сотрудника?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            employeeDAO.deleteEmployee(employeeId);
            loadEmployees(); // Обновить таблицу
        }
    }
}
