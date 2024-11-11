package com.warehouse.repository;

import com.warehouse.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.warehouse.util.HibernateUtil;

import java.util.List;

public class ProductRepository {

    private SessionFactory sessionFactory;

    public ProductRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    // ????? ??? ?????????? ????????
    public void save(Product product) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(product);  // ????? saveOrUpdate, ????? ???????? ???????????? ??????
        transaction.commit();
        session.close();
    }

    // ????? ???????? ?? ????????
    public Product findByArticul(Long articul) {
        Session session = sessionFactory.openSession();
        Product product = session.get(Product.class, articul);  // ?????????? get ??? ?????? ?? ID
        session.close();
        return product;
    }

    // ???????? ??? ??????
    public List<Product> findAll() {
        Session session = sessionFactory.openSession();
        Query<Product> query = session.createQuery("FROM Product", Product.class);
        List<Product> products = query.getResultList();
        session.close();
        return products;
    }

    // ?????????? ??????
    public void update(Product product) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.update(product);
        transaction.commit();
        session.close();
    }

    // ???????? ??????
    public void delete(Long articul) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Product product = session.get(Product.class, articul);
        if (product != null) {
            session.delete(product);
        }
        transaction.commit();
        session.close();
    }
}
