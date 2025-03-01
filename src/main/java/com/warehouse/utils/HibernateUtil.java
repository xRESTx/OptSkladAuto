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
                    .addAnnotatedClass(FuelType.class)
                    .addAnnotatedClass(Supplier.class)
                    .addAnnotatedClass(Station.class)
                    .addAnnotatedClass(FuelStock.class)
                    .addAnnotatedClass(FuelSupply.class)
                    .addAnnotatedClass(Transaction.class)
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
