package com.warehouse.page;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderItemDAO;
import com.warehouse.dao.ProductDAO;
import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;
import com.warehouse.entities.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPage {

    private static Order currentOrder = null;

    public static void showOrdersPage(String clientFullName, int clientId) {

        JFrame frame = createFrame("Orders", 800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Orders for " + clientFullName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.getOrdersByClientId(clientId);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Order ID", "Status", "Total Amount", "Order Date"}, 0
        );
        orders.forEach(order -> model.addRow(new Object[]{
                order.getId(),
                order.getStatus(),
                order.getAmount(),
                order.getOrderDate()
        }));

        JTable ordersTable = new JTable(model);
        frame.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createOrderButton = new JButton("Create Order");
        JButton viewOrderButton = new JButton("View Order");

        buttonPanel.add(createOrderButton);
        buttonPanel.add(viewOrderButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик для создания нового заказа
        createOrderButton.addActionListener(e -> {
            currentOrder = null; // Сбрасываем текущий заказ, чтобы создать новый
            showCreateOrderPage(clientId, model);
        });

        // Обработчик для просмотра и редактирования существующего заказа
        viewOrderButton.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow != -1) {
                int orderId = (int) model.getValueAt(selectedRow, 0);
                OrderDAO orderDAO1 = new OrderDAO();
                currentOrder = orderDAO1.findById(orderId); // Загружаем выбранный заказ
                if (currentOrder != null) {
                    showOrderDetails(currentOrder); // Открываем детали существующего заказа
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to load order.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order to view.");
            }
        });

        frame.setVisible(true);
    }

    private static void showCreateOrderPage(int clientId, DefaultTableModel ordersTableModel) {
        JFrame frame = createFrame("Create Order", 800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Create New Order");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Таблица для выбора товаров
        DefaultTableModel productsModel = new DefaultTableModel(
                new String[]{"Product ID", "Product Name", "Price", "Stock Quantity"}, 0
        );
        JTable productsTable = new JTable(productsModel);
        frame.add(new JScrollPane(productsTable), BorderLayout.WEST);

        // Получение списка продуктов
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.findAll();
        for (Product product : products) {
            productsModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStockQuantity()
            });
        }


        // Таблица для добавленных в заказ товаров
        DefaultTableModel orderItemsModel = new DefaultTableModel(
                new String[]{"Product ID", "Product Name", "Quantity", "Price"}, 0
        );
        JTable orderItemsTable = new JTable(orderItemsModel);
        frame.add(new JScrollPane(orderItemsTable), BorderLayout.CENTER);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        JButton removeItemButton = new JButton("Remove Item");
        JButton confirmOrderButton = new JButton("Confirm Order");
        JButton closeButton = new JButton("Cancel");

        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(confirmOrderButton);
        buttonPanel.add(closeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик для добавления товара в список
        addItemButton.addActionListener(e -> {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow != -1) {
                int productId = (int) productsModel.getValueAt(selectedRow, 0);
                String productName = (String) productsModel.getValueAt(selectedRow, 1);
                Object stockQuantityObj = productsModel.getValueAt(selectedRow, 2);
                int stockQuantity = (stockQuantityObj instanceof Number) ? ((Number) stockQuantityObj).intValue() : 0;


                String quantityStr = JOptionPane.showInputDialog(frame, "Enter quantity (available: " + stockQuantity + "):");
                if (quantityStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0 && quantity <= stockQuantity) {
                            double price = (double) productsModel.getValueAt(selectedRow, 2); // Получаем цену из productsModel
                            orderItemsModel.addRow(new Object[]{productId, productName, quantity, price});
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid quantity. Please try again.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid quantity entered.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a product to add.");
            }
        });

        // Обработчик для удаления товара из списка
        removeItemButton.addActionListener(e -> {
            int selectedRow = orderItemsTable.getSelectedRow();
            if (selectedRow != -1) {
                orderItemsModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an item to remove.");
            }
        });

        // Обработчик для подтверждения заказа
        confirmOrderButton.addActionListener(e -> {
            if (orderItemsModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "Order is empty. Please add items.");
                return;
            }

            // Создаем заказ
            OrderDAO orderDAO = new OrderDAO();
            Order newOrder = orderDAO.createOrder(clientId);

            if (newOrder == null) {
                JOptionPane.showMessageDialog(frame, "Failed to create order.");
                return;
            }

            // Добавляем элементы в заказ
            OrderItemDAO orderItemDAO = new OrderItemDAO();
            boolean allItemsAdded = true;
            double totalAmount = 0.0;
            for (int i = 0; i < orderItemsModel.getRowCount(); i++) {
                int quantity = Integer.parseInt(orderItemsModel.getValueAt(i, 2).toString());
                double price = Double.parseDouble(orderItemsModel.getValueAt(i, 3).toString());
                totalAmount += quantity * price;

                int productId = (int) orderItemsModel.getValueAt(i, 0);
                OrderItem newItem = orderItemDAO.addItemToOrder(newOrder, productId, quantity);
                if (newItem == null) {
                    allItemsAdded = false;
                }
            }
            newOrder.setAmount(totalAmount); // Устанавливаем общую стоимость
            orderDAO.update(newOrder); // Обновляем заказ в базе данных


            if (allItemsAdded) {
                JOptionPane.showMessageDialog(frame, "Order successfully created!");
                // Обновляем таблицу заказов на главной странице
                ordersTableModel.addRow(new Object[]{newOrder.getId(), newOrder.getStatus(), newOrder.getAmount(), newOrder.getOrderDate()});
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Some items could not be added to the order.");
            }
        });

        // Обработчик для закрытия окна
        closeButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }


    private static void showOrderDetails(Order order) {
        JFrame frame = new JFrame("Order Details");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Order #" + order.getId() + " Details");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Таблица с элементами заказа
        DefaultTableModel orderItemsModel = new DefaultTableModel(
                new String[]{"Product ID", "Product Name", "Quantity", "Price", "Total Price"}, 0
        );
        JTable orderItemsTable = new JTable(orderItemsModel);
        frame.add(new JScrollPane(orderItemsTable), BorderLayout.CENTER);

        // Загрузка элементов заказа
        loadOrderItems(order.getId(), orderItemsModel);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        JButton confirmOrderButton = new JButton("Confirm Order");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addItemButton);
        buttonPanel.add(confirmOrderButton);
        buttonPanel.add(closeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Добавление нового товара в заказ
        addItemButton.addActionListener(e -> {
            DefaultTableModel newItemsModel = new DefaultTableModel(
                    new String[]{"Product ID", "Product Name", "Quantity", "Price"}, 0
            );
            JTable newItemsTable = new JTable(newItemsModel);

            // Показ окна для выбора новых элементов
            JFrame addItemsFrame = new JFrame("Add Items");
            addItemsFrame.setSize(600, 400);
            addItemsFrame.add(new JScrollPane(newItemsTable), BorderLayout.CENTER);

            // Загрузка доступных продуктов
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.findAll();
            for (Product product : products) {
                newItemsModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getStockQuantity(),
                        product.getPrice()
                });
            }

            JPanel addItemPanel = new JPanel();
            JButton addButton = new JButton("Add");
            addItemPanel.add(addButton);
            addItemsFrame.add(addItemPanel, BorderLayout.SOUTH);

            addButton.addActionListener(ae -> {
                int selectedRow = newItemsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int productId = (int) newItemsModel.getValueAt(selectedRow, 0);
                    String productName = (String) newItemsModel.getValueAt(selectedRow, 1);
                    double price = (double) newItemsModel.getValueAt(selectedRow, 3);

                    String quantityStr = JOptionPane.showInputDialog("Enter quantity:");
                    if (quantityStr != null) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            if (quantity > 0) {
                                orderItemsModel.addRow(new Object[]{
                                        productId,
                                        productName,
                                        quantity,
                                        price,
                                        quantity * price
                                });
                            } else {
                                JOptionPane.showMessageDialog(addItemsFrame, "Invalid quantity.");
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(addItemsFrame, "Invalid input.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(addItemsFrame, "Please select a product.");
                }
            });

            addItemsFrame.setVisible(true);
        });

        // Подтверждение изменений в заказе
        confirmOrderButton.addActionListener(e -> {
            if (orderItemsModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "No items to add.");
                return;
            }

            OrderItemDAO orderItemDAO = new OrderItemDAO();
            boolean allItemsAdded = true;
            for (int i = 0; i < orderItemsModel.getRowCount(); i++) {
                int productId = (int) orderItemsModel.getValueAt(i, 0);
                int quantity = (int) orderItemsModel.getValueAt(i, 2);

                OrderItem addedItem = orderItemDAO.addItemToOrder(order, productId, quantity);
                if (addedItem == null) {
                    allItemsAdded = false;
                }
            }

            if (allItemsAdded) {
                JOptionPane.showMessageDialog(frame, "Order updated successfully.");
                loadOrderItems(order.getId(), orderItemsModel); // Перезагружаем таблицу элементов заказа
            } else {
                JOptionPane.showMessageDialog(frame, "Some items could not be added.");
            }
        });

        // Закрытие окна
        closeButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private static void loadOrderItems(int orderId, DefaultTableModel model) {
        model.setRowCount(0); // Очищаем текущие данные
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            model.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    product.getPrice(),
                    item.getQuantity() * product.getPrice()
            });
        }
    }


    private static JFrame createFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Изменено на DO_NOTHING_ON_CLOSE
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
