package com.warehouse.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.warehouse.dao.OrderComponentDAO;
import com.warehouse.models.OrderComponent;

public class OrderDetailsDialog extends JDialog {
    public OrderDetailsDialog(JFrame parent, List<OrderComponent> components) {
        super(parent, "Order Details", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        String[] columnNames = {"ID", "Autotovar", "Count", "Summa"};
        Object[][] data = new Object[components.size()][4];

        for (int i = 0; i < components.size(); i++) {
            OrderComponent component = components.get(i);
            data[i][0] = component.getId();
            data[i][1] = component.getAutotovar().getName();
            data[i][2] = component.getCount();
            data[i][3] = component.getSumma();
        }

        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }
}
