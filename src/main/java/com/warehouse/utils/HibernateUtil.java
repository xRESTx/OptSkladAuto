package com.warehouse.utils;

import com.warehouse.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure()
                    .addAnnotatedClass(Employee.class)
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(Contract.class)
                    .addAnnotatedClass(Department.class)
                    .addAnnotatedClass(Accessories.class)
                    .addAnnotatedClass(Autotovar.class)
                    .addAnnotatedClass(Chemistry.class)
                    .addAnnotatedClass(Electronics.class)
                    .addAnnotatedClass(Lubricants.class)
                    .addAnnotatedClass(OrderComponent.class)
                    .addAnnotatedClass(Orders.class)
                    .addAnnotatedClass(Payment.class)
                    .addAnnotatedClass(Repair.class)
                    .addAnnotatedClass(Request.class)
                    .addAnnotatedClass(Wheels.class)
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
