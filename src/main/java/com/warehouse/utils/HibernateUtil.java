package com.warehouse.utils;

import com.warehouse.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure()
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(Position.class)
                    .addAnnotatedClass(Employee.class)
                    .addAnnotatedClass(Contract.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(Request.class)
                    .addAnnotatedClass(Department.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Payment.class)
                    .addAnnotatedClass(OrderItem.class)
                    .addAnnotatedClass(Supplier.class)
                    .addAnnotatedClass(Delivery.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
