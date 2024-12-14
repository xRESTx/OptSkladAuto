package com.warehouse.page.adminPage;

import com.warehouse.entities.Position;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PositionsPage {

    public static void showPositionsPage() {
        JFrame frame = new JFrame("Positions Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Positions List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных должностей
        String[] columnNames = {"ID", "Name", "Description"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable positionsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(positionsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления должности
        JButton addButton = new JButton("Add Position");
        addButton.addActionListener(e -> addPosition(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования должности
        JButton editButton = new JButton("Edit Position");
        editButton.addActionListener(e -> editPosition(positionsTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления должности
        JButton deleteButton = new JButton("Delete Position");
        deleteButton.addActionListener(e -> deletePosition(positionsTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadPositionsData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadPositionsData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Position.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех должностей из базы данных
            List<Position> positions = session.createQuery("from Position", Position.class).list();

            // Добавление данных в таблицу
            for (Position position : positions) {
                tableModel.addRow(new Object[]{
                        position.getId(),
                        position.getName(),
                        position.getDescription()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading positions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addPosition(DefaultTableModel tableModel) {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Position", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Position.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Position position = new Position();
                position.setName(name);
                position.setDescription(description);
                session.save(position);

                session.getTransaction().commit();

                // Обновление таблицы
                tableModel.addRow(new Object[]{
                        position.getId(),
                        position.getName(),
                        position.getDescription()
                });

                JOptionPane.showMessageDialog(null, "Position added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding position: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void editPosition(JTable positionsTable, DefaultTableModel tableModel) {
        int selectedRow = positionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a position to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int positionId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField descriptionField = new JTextField(currentDescription);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Position", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            String newDescription = descriptionField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Position.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Position position = session.get(Position.class, positionId);
                if (position != null) {
                    position.setName(newName);
                    position.setDescription(newDescription);
                    session.update(position);

                    session.getTransaction().commit();

                    // Обновление таблицы
                    tableModel.setValueAt(newName, selectedRow, 1);
                    tableModel.setValueAt(newDescription, selectedRow, 2);

                    JOptionPane.showMessageDialog(null, "Position updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Position not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating position: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deletePosition(JTable positionsTable, DefaultTableModel tableModel) {
        int selectedRow = positionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a position to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int positionId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this position?", "Delete Position", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Position.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Position position = session.get(Position.class, positionId);
                if (position != null) {
                    session.delete(position);

                    session.getTransaction().commit();

                    // Обновление таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Position deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Position not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting position: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
