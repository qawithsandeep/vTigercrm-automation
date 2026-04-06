package com.saroz.vtiger.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private Properties prop;
    private String path = System.getProperty("user.dir") + "/src/main/resources/config.properties";

    public ConfigReader() {
        try {
            FileInputStream fis = new FileInputStream(path);
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            System.out.println(" Unable to load config file.");
            e.printStackTrace();
        }
    }

  //--------- vTigercrm Login Config Methods ---------//

    /**
     * Returns valid username from config
     * Key: vt.username
     */
    public String getValidUserName() {
        return prop.getProperty("vt.username");
    }

    /**
     * Returns valid user password from config
     * Key: vt.userpasword
     */
    public String getValidUserPass() {
        return prop.getProperty("vt.userpasword");
    }

    /**
     * Returns invalid username from config
     * Key: vt.invalidUserName
     */
    public String getInvalidUserName() {
        return prop.getProperty("vt.invalidUserName");
    }

    /**
     * Returns invalid user password from config
     * Key: vt.invalidUserPassword
     */
    public String getInvalidUserPass() {
        return prop.getProperty("vt.invalidUserPassword");
    }

    /**
     * Returns login URL from config
     * Key: vt.loginurl
     */
    public String getLoginUrl() {
        return prop.getProperty("vt.loginurl");
    }
}