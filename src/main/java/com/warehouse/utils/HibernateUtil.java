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
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(Invoice.class)
                    .addAnnotatedClass(Meter.class)
                    .addAnnotatedClass(MeterReading.class)
                    .addAnnotatedClass(Payment.class)
                    .addAnnotatedClass(Service.class)
                    .addAnnotatedClass(Tariff.class)
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
