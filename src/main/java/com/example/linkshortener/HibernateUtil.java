package com.example.linkshortener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateUtil {
    public HibernateUtil() {

    }
    @Bean
    public static SessionFactory getSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(getDataSource());
        sessionFactory.setPackagesToScan("com.example.linkshortener.entity");
        sessionFactory.setHibernateProperties(setHibernateProperties());
        try {
            sessionFactory.afterPropertiesSet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sessionFactory.getObject();
    }

    @Bean
    public static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/link_shortener");
        dataSource.setUsername("postgres");
        dataSource.setPassword(System.getenv("PGPASSWORD"));
        return dataSource;
    }

    private static Properties setHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.allow_update_outside_transaction", "true");
        return properties;
    }

    @Bean
    public static HibernateTransactionManager getHibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory());
        transactionManager.setDataSource(getDataSource());

        return transactionManager;
    }

}
