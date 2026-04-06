package com.saroz.vtiger.base;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.edge.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverFactory {

    // Logger for DriverFactory class
    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    // Creates and returns WebDriver instance based on browser and headless flag
    protected WebDriver createDriver(String browser, boolean headless) {

        WebDriver driver;

        try {
            // Use Chrome as default browser when browser value is null or empty
            if (browser == null || browser.trim().isEmpty()) {
                log.warn("Browser not provided. Defaulting to Chrome.");
                browser = "chrome";
            }

            // Initialize Chrome browser
            if ("chrome".equalsIgnoreCase(browser)) {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                }
                driver = new ChromeDriver(options);

            // Initialize Firefox browser
            } else if ("firefox".equalsIgnoreCase(browser)) {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);

            // Initialize Edge browser
            } else if ("edge".equalsIgnoreCase(browser)) {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new EdgeDriver(options);

            // Throw exception if browser is not supported
            } else {
                throw new IllegalArgumentException("Browser Not Supported: " + browser);
            }

            // Browser configuration
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            log.info(browser + " driver created | Headless = " + headless);
            return driver;

        // Handle any driver initialization failure
        } catch (Exception e) {
            log.error("Failed to create driver for browser: " + browser, e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
}
