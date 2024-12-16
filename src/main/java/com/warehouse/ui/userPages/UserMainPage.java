package com.warehouse.ui.userPages;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.dao.*;
import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderComponentDAO;
import com.warehouse.models.Account;
import com.warehouse.models.Autotovar;
import com.warehouse.models.Order;
import com.warehouse.models.OrderComponent;
import com.warehouse.ui.mainPages.LoginPage;
import com.warehouse.ui.dialog.ProductDetailsDialog;
import com.warehouse.utils.*;

public class UserMainPage extends JFrame {
    private JPanel autotovarPanel;
    private JButton backButton, viewOrdersButton, viewRequestsButton, viewPaymentsButton, viewAutotovarsButton, checkoutButton;
    private JTextField searchField;

    // Список для хранения артикулов выбранных товаров с их количеством
    private List<CartItem> cartItems;

    public UserMainPage() {
        setTitle("User Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        toolbar.add(viewAutotovarsButton);
        toolbar.add(viewOrdersButton);
        toolbar.add(viewRequestsButton);
        toolbar.add(viewPaymentsButton);

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
        viewAutotovarsButton.addActionListener(e -> filterAutotovars());
        checkoutButton.addActionListener(e -> checkout());

        loadAutotovars(); // Загружаем автотовары по умолчанию
    }
    private void filterAutotovars() {
        String searchText = searchField.getText().toLowerCase(); // Получаем текст из поля поиска
        List<Autotovar> autotovars = AutotovarDAO.findAll();
        if (searchText.isEmpty()) {
            updateAutotovarCards(autotovars); // Если поиск пустой, показываем все товары
        } else {
            // Фильтруем автотовары по имени или описанию
            List<Autotovar> filteredAutotovars = new ArrayList<>();
            for (Autotovar autotovar : autotovars) {
                String description = autotovar.getDescription(); // Получаем описание товара

                // Проверяем, содержится ли поисковая строка в имени или описании товара (с учетом null)
                if ((autotovar.getName().toLowerCase().contains(searchText)) ||
                        (description != null && description.toLowerCase().contains(searchText))) {
                    filteredAutotovars.add(autotovar);
                }
            }
            updateAutotovarCards(filteredAutotovars); // Обновляем карточки с отфильтрованными товарами
        }
    }
    private void showOrders() {
        dispose();
        new UserOrdersPage().setVisible(true);
    }

    private void showRequests() {
        dispose();
        new UserRequestsPage().setVisible(true);
    }

    private void showPayments() {
        dispose();
        new UserPaymentsPage().setVisible(true);
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
            cardPanel.setLayout(new BorderLayout()); // Используем BorderLayout для размещения элементов
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Устанавливаем фиксированный размер карточки
            cardPanel.setPreferredSize(new Dimension(300, 120));

            // Путь к фотографии
            String photoPath = "src/main/resources/photo/" + autotovar.getArticul() + ".jpg";
            String noImagePath = "src/main/resources/photo/noImagePhoto.jpg";

            // Создаем компонент для фото
            JLabel photoLabel = new JLabel();
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Попытка загрузить изображение
            try {
                ImageIcon imageIcon = new ImageIcon(photoPath); // Загружаем изображение
                if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) { // Проверяем, успешно ли загрузилось изображение
                    Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING); // Масштабируем изображение
                    photoLabel.setIcon(new ImageIcon(image)); // Устанавливаем масштабированное изображение
                } else {
                    throw new Exception("Image not found or invalid");
                }
            } catch (Exception e) {
                ImageIcon imageIcon = new ImageIcon(noImagePath);
                Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING);
                photoLabel.setIcon(new ImageIcon(image));
            }

            // Добавляем фото в левую часть карточки
            cardPanel.add(photoLabel, BorderLayout.WEST);

            // Панель для описания и кнопки
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Добавляем отступы

            // Добавляем артикул
            JLabel articulLabel = new JLabel("Art: " + autotovar.getArticul());
            infoPanel.add(articulLabel);

            // Название автотовара
            JLabel nameLabel = new JLabel("Name: " + autotovar.getName());
            infoPanel.add(nameLabel);

            // Описание
            JLabel descriptionLabel = new JLabel("Description: " + autotovar.getDescription());
            infoPanel.add(descriptionLabel);

            // Цена
            JLabel priceLabel = new JLabel("Price: $" + autotovar.getCost());
            infoPanel.add(priceLabel);

            // Резервируем место для кнопки или управления количеством
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            actionPanel.setMaximumSize(new Dimension(250, 50)); // Фиксируем высоту панели
            actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(actionPanel);

            // Проверяем, есть ли товар в корзине
            CartItem cartItem = findCartItem(autotovar.getArticul());

            if (cartItem != null) {
                // Товар уже в корзине, показываем кнопки изменения количества
                JLabel quantityLabel = new JLabel("Quantity: " + cartItem.getQuantity());

                JButton decreaseButton = new JButton("-");
                decreaseButton.addActionListener(e -> updateQuantity(autotovar.getArticul(), -1));

                JButton increaseButton = new JButton("+");
                increaseButton.addActionListener(e -> updateQuantity(autotovar.getArticul(), 1));

                actionPanel.add(quantityLabel);
                actionPanel.add(decreaseButton);
                actionPanel.add(increaseButton);
            } else {
                // Товар еще не в корзине, показываем кнопку "Add to Cart"
                JButton orderButton = new JButton("Add to Cart");
                orderButton.addActionListener(e -> {
                    addToCart(autotovar.getArticul());
                    updateAutotovarCards(autotovars); // Перерисовываем панель после добавления в корзину
                });
                actionPanel.add(orderButton);
            }

            // Добавляем infoPanel в правую часть карточки
            cardPanel.add(infoPanel, BorderLayout.CENTER);

            cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) { // Проверяем, что клик двойной
                        // Открываем ProductDetailsDialog
                        showProductDetails(autotovar);
                    }
                }
            });
            // Добавляем карточку на панель
            autotovarPanel.add(cardPanel);
        }

        // Обновляем отображение панели
        autotovarPanel.revalidate();
        autotovarPanel.repaint();
    }




    private void showProductDetails(Autotovar autotovar) {
        ProductDetailsDialog dialog = new ProductDetailsDialog(this, autotovar);
        dialog.setVisible(true);
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

        // Запрашиваем комментарий к заказу у пользователя
        String comment = JOptionPane.showInputDialog(this, "Leave a comment for your order:", "Order Comment", JOptionPane.PLAIN_MESSAGE);

        // Устанавливаем логин текущего пользователя
        String currentLogin = SessionManager.getCurrentUserLogin();
        Account account = AccountDAO.findById(currentLogin);

        // Создаем новый заказ
        Order newOrder = new Order();
        newOrder.setStatus("Pending");
        newOrder.setAccount(account);
        newOrder.setRemark(comment); // Устанавливаем комментарий



        // Добавляем заказ в базу данных
        int orderId = OrderDAO.createOrder(newOrder);

        // Добавляем компоненты заказа
        for (CartItem cartItem : cartItems) {
            // Получаем товар по его ID
            Autotovar autotovar = AutotovarDAO.findById(cartItem.getAutotovarId());

            // Создаем новый объект OrderComponent
            OrderComponent orderComponent = new OrderComponent();
            orderComponent.setOrders(newOrder); // Устанавливаем текущий заказ
            orderComponent.setAutotovar(autotovar); // Устанавливаем автотовар
            orderComponent.setCount(cartItem.getQuantity()); // Устанавливаем количество

            // Добавляем компонент заказа в базу данных
            OrderComponentDAO.addOrderComponent(orderComponent);
        }

        // Очистка корзины
        cartItems.clear();

        JOptionPane.showMessageDialog(this, "Order placed successfully! Your order ID is " + orderId);
        loadAutotovars();
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
