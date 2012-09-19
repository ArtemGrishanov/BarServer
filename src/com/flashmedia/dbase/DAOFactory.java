/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flashmedia.dbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class represents a DAO factory for a SQL database. You can use {@link #getInstance(String)}
 * to obtain an instance for the given database name. You can obtain DAO's for the database instance
 * using the DAO getters.
 * <p>
 * This class requires a properties file named 'dao.properties' in the classpath with under each
 * the following properties:
 * <pre>
 * name.url *
 * name.driver
 * name.username
 * name.password
 * </pre>
 * Those marked with * are required, others are optional and can be left away or empty. Only the
 * username is required when any password is specified.
 * <ul>
 * <li>The 'name' must represent the database name in {@link #getInstance(String)}.</li>
 * <li>The 'name.url' must represent either the JDBC URL or JNDI name of the database.</li>
 * <li>The 'name.driver' must represent the full qualified class name of the JDBC driver.</li>
 * <li>The 'name.username' must represent the username of the database login.</li>
 * <li>The 'name.password' must represent the password of the database login.</li>
 * </ul>
 * If you specify the driver property, then the url property will be assumed as JDBC URL. If you
 * omit the driver property, then the url property will be assumed as JNDI name. When using JNDI
 * with username/password preconfigured, you can omit the username and password properties as well.
 * <p>
 * Here are basic examples of valid properties for a database with the name 'javabase':
 * <pre>
 * javabase.url = jdbc:mysql://localhost:3306/javabase
 * javabase.driver = com.mysql.jdbc.Driver
 * javabase.username = java
 * javabase.password = d$7hF_r!9Y
 * </pre>
 * <pre>
 * javabase.url = java:comp/env/jdbc/javabase
 * </pre>
 * Here is a basic use example:
 * <pre>
 * DAOFactory javabase = DAOFactory.getInstance("javabase");
 * UserDAO userDAO = javabase.getUserDAO();
 * </pre>
 *
 */
public final class DAOFactory {

    // Constants ----------------------------------------------------------------------------------

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_DRIVER = "driver";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_CHARSET = "charset";
    private static final String PROPERTY_USE_UNICODE = "useunicode";
    private static final Map<String, DAOFactory> INSTANCES = new HashMap<String, DAOFactory>();

    // Vars ---------------------------------------------------------------------------------------

    private String url;
    private String username;
    private String password;
    private DataSource dataSource;
    private String	characterEncoding;
    private String useUnicode;
    // Constructors -------------------------------------------------------------------------------


    /**
     * Construct a DAOFactory instance for the given database name.
     * @param name The database name to construct a DAOFactory instance for.
     * @throws DAOConfigurationException If the properties file is missing in the classpath or
     * cannot be loaded, or if a required property is missing in the properties file, or if either
     * the driver cannot be loaded or the datasource cannot be found.
     */
    private DAOFactory(String name) throws DAOConfigurationException {
        DAOProperties properties = new DAOProperties(name);
        url = properties.getProperty(DAOFactory.PROPERTY_URL, true);
        String driverClassName = properties.getProperty(DAOFactory.PROPERTY_DRIVER, false);
        password = properties.getProperty(DAOFactory.PROPERTY_PASSWORD, false);
        username = properties.getProperty(DAOFactory.PROPERTY_USERNAME, password != null);
        characterEncoding = properties.getProperty(DAOFactory.PROPERTY_CHARSET, true);
        useUnicode = properties.getProperty(DAOFactory.PROPERTY_USE_UNICODE, false);
        if (driverClassName != null) {
            // If driver is specified, then load it and let it register itself with DriverManager.
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new DAOConfigurationException(
                    "Driver class '" + driverClassName + "' is missing in classpath.", e);
            }
        } else {
            // Else assume URL as DataSource URL and lookup it in the JNDI.
            try {
                dataSource = (DataSource) new InitialContext().lookup(url);
            } catch (NamingException e) {
                throw new DAOConfigurationException(
                    "DataSource '" + url + "' is missing in JNDI.", e);
            }
        }
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns a DAOFactory instance for the given database name.
     * @param name The database name to return a DAOFactory instance for.
     * @return A DAOFactory instance for the given database name.
     * @throws DAOConfigurationException If the database name is null, or if the properties file is
     * missing in the classpath or cannot be loaded, or if a required property is missing in the
     * properties file, or if either the driver cannot be loaded or the datasource cannot be found.
     */
    public static DAOFactory getInstance(String name) throws DAOConfigurationException {
        if (name == null) {
            throw new DAOConfigurationException("Database name is null.");
        }
        DAOFactory instance = DAOFactory.INSTANCES.get(name);
        if (instance == null) {
            instance = new DAOFactory(name);
            DAOFactory.INSTANCES.put(name, instance);
        }
        return instance;
    }

    /**
     * Returns a connection to the database. Package private so that it can be used inside the DAO
     * package only.
     * @return A connection to the database.
     * @throws SQLException If acquiring the connection fails.
     */
    Connection getConnection() throws SQLException {
        if (dataSource != null) {
            if (username != null) {
                return dataSource.getConnection(username, password);
            }
            return dataSource.getConnection();
        }
    	//--------------------------
    	Properties info = new Properties();
    	//---------------------------
    	info.put("user", username);
    	info.put("password", password);
    	info.put("useUnicode", useUnicode);
    	info.put("characterEncoding", characterEncoding);
    	//---------------------------
//            return DriverManager.getConnection(url, username, password);
    	return DriverManager.getConnection(url, info);
    }

    // DAO getters --------------------------------------------------------------------------------

    /**
     * Returns the User DAO associated with the current DAOFactory.
     * @return The User DAO associated with the current DAOFactory.
     */
    public UserDAO getUserDAO() {
        return new UserDAO(this);
    }

    // You can add more DAO getters here.

}
