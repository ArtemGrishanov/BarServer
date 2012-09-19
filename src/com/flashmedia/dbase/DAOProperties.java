/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flashmedia.dbase;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class immediately loads the DAO properties file 'dao.properties' once in memory and provides
 * a constructor which takes the specific key which is to be used as property key prefix of the DAO
 * properties file. There is a property getter which only returns the property prefixed with
 * 'specificKey.' and provides the option to indicate whether the property is mandatory or not.
 */
public class DAOProperties {

    // Constants ----------------------------------------------------------------------------------

    private static final String PROPERTIES_FILE = "/javabase.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
//		this code doesn't work on unix
//      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//      InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIES_FILE);
//
//      if (propertiesFile == null) {
//          throw new DAOConfigurationException(
//              "Properties file '" + PROPERTIES_FILE + "' is missing in classpath.");
//      }
//
//      try {
//          PROPERTIES.load(propertiesFile);
//      } catch (IOException e) {
//          throw new DAOConfigurationException(
//              "Cannot load properties file '" + PROPERTIES_FILE + "'.", e);
//      }

    	// set default properties
//    	DAOProperties.PROPERTIES.setProperty("javabase.driver", "com.mysql.jdbc.Driver");
//    	DAOProperties.PROPERTIES.setProperty("javabase.url", "jdbc:mysql://localhost:3306/barmandb?useUnicode=yes&characterEncoding=UTF-8");
//    	DAOProperties.PROPERTIES.setProperty("javabase.username", "root");
//    	DAOProperties.PROPERTIES.setProperty("javabase.password", "123456");
//    	DAOProperties.PROPERTIES.setProperty("javabase.useunicode", "yes");
//    	DAOProperties.PROPERTIES.setProperty("javabase.charset", "UTF-8");
    	// set production properties
    	DAOProperties.PROPERTIES.setProperty("javabase.driver", "com.mysql.jdbc.Driver");
    	DAOProperties.PROPERTIES.setProperty("javabase.url", "jdbc:mysql://mysql59.1gb.ru:3306/gb_barmandb?useUnicode=yes&characterEncoding=UTF-8");
    	DAOProperties.PROPERTIES.setProperty("javabase.username", "gb_barmandb");
    	DAOProperties.PROPERTIES.setProperty("javabase.password", "01378454rty");
    	DAOProperties.PROPERTIES.setProperty("javabase.useunicode", "yes");
    	DAOProperties.PROPERTIES.setProperty("javabase.charset", "UTF-8");
    	// try to load property file
    	String fullPath = /*new File(".").getAbsolutePath() + */DAOProperties.PROPERTIES_FILE;
		try
		{
			File file = new File(fullPath);
			FileInputStream in = new FileInputStream(file);
			DAOProperties.PROPERTIES.load(in);
			System.out.println("Loaded properties file '" + fullPath + "'");
		}
		catch (Exception e)
		{
			 System.out.println("Cannot load properties file '" + fullPath + "'");
		}
		System.out.println("USE DATABASE: " + DAOProperties.PROPERTIES.getProperty("javabase.url"));
    }

    // Vars ---------------------------------------------------------------------------------------

    private String specificKey;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct a DAOProperties instance for the given specific key which is to be used as property
     * key prefix of the DAO properties file.
     * @param specificKey The specific key which is to be used as property key prefix.
     * @throws DAOConfigurationException During class initialization if the DAO properties file is
     * missing in the classpath or cannot be loaded.
     */
    public DAOProperties(String specificKey) throws DAOConfigurationException {
        this.specificKey = specificKey;
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the DAOProperties instance specific property value associated with the given key with
     * the option to indicate whether the property is mandatory or not.
     * @param key The key to be associated with a DAOProperties instance specific value.
     * @param mandatory Sets whether the returned property value should not be null nor empty.
     * @return The DAOProperties instance specific property value associated with the given key.
     * @throws DAOConfigurationException If the returned property value is null or empty while
     * it is mandatory.
     */
    public String getProperty(String key, boolean mandatory) throws DAOConfigurationException {
        String fullKey = specificKey + "." + key;
        String property = DAOProperties.PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0) {
            if (mandatory) {
                throw new DAOConfigurationException("Required property '" + fullKey + "'"
                    + " is missing in properties file '" + DAOProperties.PROPERTIES_FILE + "'.");
            }
            // Make empty value null.
            property = null;
        }

        return property;
    }

}