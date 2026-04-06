package com.saroz.vtiger.base;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.saroz.vtiger.utilities.ConfigReader;
import com.saroz.vtiger.utilities.ExtentManager;
import com.saroz.vtiger.utilities.WebUtils;

public class BaseTest {

	protected DriverFactory driverFactory;
	protected ConfigReader config;
	protected static ExtentReports reports;
	protected SoftAssert softAssert;

	// Added a getter for safety
	public WebDriver getDriver() {
		return driver.get();
	}

	// ThreadLocal for WebDriver to support Parallel execution on Jenkins/Local
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	// ThreadLocal ensures thread-safety during parallel execution
	protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
	protected static final Logger log = LogManager.getLogger(BaseTest.class);

	//=======================Extent Report ==========================//
	@BeforeSuite
	public void setupExtentReports() {

		try {
			// Initialize the report using static manager call
			reports = ExtentManager.setupExtentReports();
			log.info("Extent Report initialized successfully.");
		} catch (Exception e) {
			// Log error if reporting fails to start
			log.error("Critical: Failed to initialize Extent Report! " + e.getMessage());
		}
	}
	@AfterSuite
	public void flushExtentReports() {
		try {
			// Save and write all logs to the HTML file
			ExtentManager.flushExtentReports();
			log.info("Extent Report data saved successfully.");
		} catch (Exception e) {
			// Log error if report fails to save
			log.error("Error during flushing Extent Report: " + e.getMessage());
		}
	}
	//=======================Browser Setup ======================//

	@Parameters({ "browser", "headless" })
	@BeforeClass
	public void setup(@Optional("chrome") String browser, @Optional("false") String headless) {
		// Initializing Extent test for the class setup
		extentTest.set(reports.createTest("Setup: Browser Configuration (" + browser + ")"));

		boolean isHeadless = Boolean.parseBoolean(headless);
		try {
			driverFactory = new DriverFactory();
			//-- Using .set() to store driver in ThreadLocal
			driver.set(driverFactory.createDriver(browser, isHeadless));

			//--Initializes ConfigReader
			config = new ConfigReader(); 

			log.info(browser + " launched | Headless = " + isHeadless);
			extentTest.get().log(Status.INFO, browser + " launched | Headless = " + isHeadless);

		} catch (Exception e) {
			log.error("Browser launch failed", e);
			extentTest.get().log(Status.FAIL, "Browser launch failed: " + e.getMessage());
			throw e;
		}
	}

	@BeforeMethod
	public void startExtentTest(Method method) {
		// Create a new test entry for each @Test method
		extentTest.set(reports.createTest(method.getName()));
		softAssert = new SoftAssert();  
		log.info("Starting Execution of Test: " + method.getName());
		extentTest.get().log(Status.INFO, "Started Test: " + method.getName());
	}

	@AfterMethod
	public void screenResult(ITestResult result) {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				// Capture screenshot on test failure
				String screenshotPath = WebUtils.getScreenshot(getDriver(), extentTest.get(), result.getName());
				extentTest.get().addScreenCaptureFromPath(screenshotPath);
				log.info("Screenshot attached for failed test: " + result.getName());
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				extentTest.get().log(Status.PASS, "Test Case Passed");
			}
		} catch (Exception e) {
			log.error("Error processing test result for: " + result.getName(), e);
		} finally {
			// Assert all soft assertions at the end of the test
			if (softAssert != null) {
				softAssert.assertAll();
			}
		}
	}

	//====================Close Browser =================//
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		if (getDriver() != null) {
			getDriver().quit();
			driver.remove();
			log.info("Browser closed successfully.");
			if (extentTest.get() != null) {
				extentTest.get().log(Status.INFO, "Browser session ended.");
			}
		}
	}
}