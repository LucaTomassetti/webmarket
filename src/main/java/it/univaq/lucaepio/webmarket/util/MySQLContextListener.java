package it.univaq.lucaepio.webmarket.util;

import it.univaq.lucaepio.webmarket.dao.PersistentManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class MySQLContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(MySQLContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Inizializzazione, se necessaria
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Chiudi la SessionFactory di Hibernate
        PersistentManager.getInstance().closeSessionFactory();

        // Deregistra i driver JDBC
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                LOGGER.log(Level.INFO, "Deregistering jdbc driver: {0}", driver);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deregistering driver " + driver, e);
            }
        }
    }
}