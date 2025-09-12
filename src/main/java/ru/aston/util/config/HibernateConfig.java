package ru.aston.util.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.entity.UserEntity;

import java.io.InputStream;
import java.util.Properties;

public final class HibernateConfig {

    private static final String PATH_TEST_PROPERTIES = "hibernate-test.properties";
    private static final String PATH_PRODUCTION_PROPERTIES = "hibernate.properties";

    private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);

    private static final SessionFactory sessionFactory = createSessionFactory();

    private static SessionFactory createSessionFactory() {

        Properties properties = new Properties();

        String environment = System.getProperty("app.environment", "production");

        String pathProperties = environment.equals("test") ? PATH_TEST_PROPERTIES : PATH_PRODUCTION_PROPERTIES;

        try (InputStream input = HibernateConfig.class.getClassLoader()
                .getResourceAsStream(pathProperties)) {
            properties.load(input);

            if (environment.equals("test")) {
                properties.setProperty("hibernate.connection.url", System.getProperty("hibernate.connection.url"));
                properties.setProperty("hibernate.connection.username", System.getProperty("hibernate.connection.username"));
                properties.setProperty("hibernate.connection.password", System.getProperty("hibernate.connection.password"));
            }

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build();

            Metadata metadata = new MetadataSources(registry)
                    .addAnnotatedClass(UserEntity.class)
                    .getMetadataBuilder()
                    .build();

            return metadata.getSessionFactoryBuilder().build();

        } catch (Exception e) {
            shutdown();
            logger.error("SessionFactory isn't create");
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("SessionFactory close");
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            logger.warn("SessionFactory isn't available or closed");
            throw new IllegalStateException("SessionFactory is not available");
        }
        return sessionFactory;
    }
}
