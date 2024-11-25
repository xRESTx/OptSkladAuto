package com.warehouse.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.dao.AutotovarDAO;
import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderComponentDAO;
import com.warehouse.models.Autotovar;
import com.warehouse.models.Order;
import com.warehouse.models.OrderComponent;

public class UserMainPage extends JFrame {
    private JPanel autotovarPanel;
    private JButton backButton, viewOrdersButton, viewRequestsButton, viewPaymentsButton, viewAutotovarsButton, checkoutButton;
    private JTextField searchField;

    // Список для хранения артикулов выбранных товаров с их количеством
    private List<CartItem> cartItems;

    public UserMainPage() {
        setTitle("User Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Инициализация списка выбранных товаров
        cartItems = new ArrayList<>();

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        backButton = new JButton("LogOut");

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new LoginPage(); // Возвращаемся на главную страницу
        });

        toolbar.add(backButton);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);

        // Кнопки для навигации
        viewOrdersButton = new JButton("View Orders");
        viewRequestsButton = new JButton("View Requests");
        viewPaymentsButton = new JButton("View Payments");
        viewAutotovarsButton = new JButton("View Autotovars");
        checkoutButton = new JButton("Checkout");

        toolbar.add(viewOrdersButton);
        toolbar.add(viewRequestsButton);
        toolbar.add(viewPaymentsButton);
        toolbar.add(viewAutotovarsButton);
        toolbar.add(checkoutButton);

        // Панель для отображения автотоваров
        autotovarPanel = new JPanel();
        autotovarPanel.setLayout(new GridLayout(0, 3, 10, 10)); // Сетка из 3 столбцов

        JScrollPane autotovarScrollPane = new JScrollPane(autotovarPanel);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(autotovarScrollPane, BorderLayout.CENTER);

        // Слушатели действий
        viewOrdersButton.addActionListener(e -> showOrders());
        viewRequestsButton.addActionListener(e -> showRequests());
        viewPaymentsButton.addActionListener(e -> showPayments());
        viewAutotovarsButton.addActionListener(e -> showAutotovars());
        checkoutButton.addActionListener(e -> checkout());

        loadAutotovars(); // Загружаем автотовары по умолчанию
    }

    private void showOrders() {
        // Логика отображения заказов пользователя
    }

    private void showRequests() {
        // Логика отображения запросов пользователя
    }

    private void showPayments() {
        // Логика отображения оплат пользователя
    }

    private void showAutotovars() {
        List<Autotovar> autotovars = AutotovarDAO.findAll();
        updateAutotovarCards(autotovars);
    }

    private void updateAutotovarCards(List<Autotovar> autotovars) {
        autotovarPanel.removeAll(); // Удаляем старые карточки

        if (autotovars == null || autotovars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No autotovars available.");
            return;
        }

        for (Autotovar autotovar : autotovars) {
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Добавляем артикул
            JLabel articulLabel = new JLabel("Art: " + autotovar.getArticul());
            cardPanel.add(articulLabel);

            // Название автотовара
            JLabel nameLabel = new JLabel("Name: " + autotovar.getName());
            cardPanel.add(nameLabel);

            // Описание
            JLabel descriptionLabel = new JLabel("Description: " + autotovar.getDescription());
            cardPanel.add(descriptionLabel);

            // Цена
            JLabel priceLabel = new JLabel("Price: $" + autotovar.getCost());
            cardPanel.add(priceLabel);

            // Проверяем, есть ли товар в корзине
            CartItem cartItem = findCartItem(autotovar.getArticul());

            if (cartItem != null) {
                // Товар уже в корзине, показываем кнопки изменения количества
                JPanel quantityPanel = new JPanel(new FlowLayout());
                JLabel quantityLabel = new JLabel("Quantity: " + cartItem.getQuantity());
                quantityPanel.add(quantityLabel);

                // Кнопки для увеличения/уменьшения количества
                JButton decreaseButton = new JButton("-");
                decreaseButton.addActionListener(e -> updateQuantity(autotovar.getArticul(), -1));
                quantityPanel.add(decreaseButton);

                JButton increaseButton = new JButton("+");
                increaseButton.addActionListener(e -> updateQuantity(autotovar.getArticul(), 1));
                quantityPanel.add(increaseButton);

                cardPanel.add(quantityPanel);
            } else {
                // Товар еще не в корзине, показываем кнопку "Add to Cart"
                JButton orderButton = new JButton("Add to Cart");
                orderButton.addActionListener(e -> addToCart(autotovar.getArticul())); // Добавить в корзину
                cardPanel.add(orderButton);
            }

            // Добавляем карточку на панель
            autotovarPanel.add(cardPanel);
        }

        // Обновляем отображение панели
        autotovarPanel.revalidate();
        autotovarPanel.repaint();
    }

    private void addToCart(int autotovarId) {
        cartItems.add(new CartItem(autotovarId, 1)); // Добавляем товар в корзину с количеством 1
        JOptionPane.showMessageDialog(this, "Item added to cart.");
        loadAutotovars(); // Перезагружаем отображение товаров, чтобы обновить кнопку
    }

    private void updateQuantity(int autotovarId, int delta) {
        CartItem cartItem = findCartItem(autotovarId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + delta);
            if (cartItem.getQuantity() <= 0) {
                cartItems.remove(cartItem); // Если количество меньше или равно 0, удаляем товар из корзины
            }
        }
        loadAutotovars(); // Обновляем отображение товаров в корзине
    }

    private CartItem findCartItem(int autotovarId) {
        for (CartItem item : cartItems) {
            if (item.getAutotovarId() == autotovarId) {
                return item;
            }
        }
        return null; // Товар не найден в корзине
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Please add some items.");
            return;
        }

        // Создаем новый заказ
        Order newOrder = new Order(); // Создаем новый заказ
        newOrder.setStatus("Pending"); // Статус заказа

        // Добавляем заказ в базу данных
        int orderId = OrderDAO.createOrder(newOrder);

        // Добавляем компоненты заказа
        for (CartItem cartItem : cartItems) {
            // Получаем товар по его ID
            Autotovar autotovar = AutotovarDAO.findById(cartItem.getAutotovarId());

            // Создаем новый объект OrderComponent
            OrderComponent orderComponent = new OrderComponent();

            // Устанавливаем заказ (объект Order)
            orderComponent.setOrders(newOrder);  // order - это объект текущего заказа

            // Устанавливаем автотовар (объект Autotovar)
            orderComponent.setAutotovar(autotovar);

            // Устанавливаем количество
            orderComponent.setCount(cartItem.getQuantity());

            // Добавляем компонент заказа в базу данных
            OrderComponentDAO.addOrderComponent(orderComponent);
        }


        // Очистка корзины
        cartItems.clear();

        JOptionPane.showMessageDialog(this, "Order placed successfully! Your order ID is " + orderId);
    }

    private void loadAutotovars() {
        List<Autotovar> autotovars = AutotovarDAO.findAll();
        updateAutotovarCards(autotovars);
    }

    // Класс для хранения информации о товаре в корзине
    private static class CartItem {
        private int autotovarId;
        private int quantity;

        public CartItem(int autotovarId, int quantity) {
            this.autotovarId = autotovarId;
            this.quantity = quantity;
        }

        public int getAutotovarId() {
            return autotovarId;
        }

        public void setAutotovarId(int autotovarId) {
            this.autotovarId = autotovarId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
