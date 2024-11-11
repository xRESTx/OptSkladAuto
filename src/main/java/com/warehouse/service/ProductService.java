package com.warehouse.service;

import com.warehouse.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ProductService {

    private SessionFactory sessionFactory;

    public ProductService() {
        sessionFactory = new Configuration().configure().addAnnotatedClass(Product.class).buildSessionFactory();
    }

    public void addProduct(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
    }

    public Product getProductByArticul(Long articul) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Product product = session.get(Product.class, articul);
        session.getTransaction().commit();
        return product;
    }

    public List<Product> getAllProducts() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        List<Product> products = session.createQuery("from Product", Product.class).getResultList();
        session.getTransaction().commit();
        return products;
    }

    public void updateProduct(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(product);
        session.getTransaction().commit();
    }

    public void deleteProduct(Long articul) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Product product = session.get(Product.class, articul);
        if (product != null) {
            session.delete(product);
        }
        session.getTransaction().commit();
    }
}
