package com.saroz.vtiger.utilities;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class WebUtils {

	private WebDriver driver;
	private Actions actions;
	private Robot robot;
	private File file;
	private Alert alert;
	private ExtentTest extentTest;
	private SoftAssert softAssert;
	protected WebDriverWait wait;
	private static final Logger log = LogManager.getLogger(WebUtils.class);


	//==================== Constructor Injection ====================//

	public WebUtils(WebDriver driver,ExtentTest extentTest) {
		this.driver = driver;
		this.extentTest = extentTest;

	}
	//====================Get Element==================//

	//	private WebElement getElement(String locator) {
	//	    try {
	//	        //--Find element
	//	        WebElement element = driver.findElement(By.xpath(locator));
	//
	//	        //--Log success
	//	        log.info("Element found: " + locator);
	//	        extentTest.log(Status.PASS, "Element found: " + locator);
	//
	//	        return element;
	//
	//	    } catch (InvalidSelectorException e) {
	//	        //--Invalid XPath
	//	        String errorMsg = "Invalid locator syntax: " + locator;
	//	        log.error(errorMsg, e);
	//	        extentTest.log(Status.FAIL, errorMsg);
	//	        throw new RuntimeException(errorMsg, e);
	//
	//	    } catch (NoSuchElementException e) {
	//	        //--Element not found
	//	        String errorMsg = "Element not found with locator: " + locator;
	//	        log.warn(errorMsg, e);
	//	        extentTest.log(Status.FAIL, errorMsg);
	//	        throw new RuntimeException(errorMsg, e);
	//
	//	    } catch (Exception e) {
	//	        //--Other exceptions
	//	        String errorMsg = "Unexpected exception while finding element: " + locator + " | Reason: " + e.getMessage();
	//	        log.error(errorMsg, e);
	//	        extentTest.log(Status.FAIL, errorMsg);
	//	        throw new RuntimeException(errorMsg, e);
	//	    }
	//	}
	//

	//====================Get Elements==================//

	public List<WebElement> getElements(By locator) {
		List<WebElement> elements = new ArrayList<>();
		try {
			//--Attempt to find elements
			elements = driver.findElements(locator);

			if (elements.isEmpty()) {
				//--No elements found
				String warningMsg = "No elements found for locator: " + locator.toString();
				log.warn(warningMsg);
				extentTest.log(Status.WARNING, warningMsg);
			} else {
				//--Elements found
				String infoMsg = "Found " + elements.size() + " elements for locator: " + locator.toString();
				log.info(infoMsg);
				extentTest.log(Status.PASS, infoMsg);
			}

		} catch (InvalidSelectorException e) {
			//--Invalid locator
			String errorMsg = "Invalid locator syntax: " + locator.toString();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);

		} catch (Exception e) {
			//--Other exceptions
			String errorMsg = "Unexpected exception while finding elements: " + locator.toString() + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
		return elements;
	}

	//====================Open URL====================//

	public void openPageUrl(String url, String urlName) {

		try {
			// Driver Null Check
			if (driver == null) {
				extentTest.log(Status.FAIL, "Driver instance is NULL. Cannot open URL!");
				log.error("Driver is NULL. Unable to open URL: " + url + " | Name: " + urlName);
				throw new RuntimeException("Driver instance is NULL. Cannot open URL.");
			}

			// URL Validation
			if (url == null || url.trim().isEmpty()) {
				extentTest.log(Status.FAIL, "Provided URL is EMPTY or NULL.");
				log.error("URL is EMPTY or NULL for name: " + urlName);
				throw new IllegalArgumentException("URL cannot be NULL or EMPTY.");
			}
			//--Wait for Page Load
			waitForPageToLoad(10);

			// Open URL
			driver.get(url);

			// Log Success
			extentTest.log(Status.INFO, "Opened URL successfully: " + urlName + " -> " + url);
			log.info("Opened URL successfully: " + urlName + " | " + url);

		} catch (Exception e) {

			// Log Failure
			extentTest.log(Status.FAIL, "Failed to open URL: " + urlName + " -> " + url);
			log.error("Failed to open URL: " + urlName + " | " + url, e);

			throw new RuntimeException("Unable to open URL: " + url, e);
		}
	}

	//====================Click====================//

	public void click(WebElement element, String elementName) {
		try {

			//--Check Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL");
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Check Displayed
			if (element.isDisplayed()) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");

				//--Check Enabled
				if (element.isEnabled()) {
					log.info(elementName + " Element is Enabled [WebElement :- '" + element + "']");

					//--Wait For Clickable 
					waitForClickable(element, 10);

					//--Finally Click 
					element.click();

					//--Success Logs
					log.info("Element clicked successfully :- " + elementName);
					extentTest.log(Status.PASS, "Element clicked successfully :- " + elementName);
					return;  // prevent JS logging

				} else {
					log.error(elementName + " Element is Disabled [WebElement :- '" + element + "']");
					extentTest.log(Status.WARNING, elementName + " Element is Disabled. Using JavaScript click.");
					clickByJs(element, elementName);
					return;
				}

			} else { 
				log.error(elementName + " Element is Invisible [WebElement :- '" + element + "']");
				extentTest.log(Status.WARNING, elementName + " Element is Invisible. Using JavaScript click.");
				clickByJs(element, elementName);
				return;
			}

		} catch (ElementClickInterceptedException e) {
			extentTest.log(Status.WARNING, "Click intercepted, attempting JavaScript click... " + elementName);
			log.warn("Click intercepted for: " + elementName, e);
			clickByJs(element, elementName);

		} catch (ElementNotInteractableException e) {
			extentTest.log(Status.WARNING, "Element not interactable, attempting JavaScript click... " + elementName);
			log.warn("Element not interactable: " + elementName, e);
			clickByJs(element, elementName);

		} catch (StaleElementReferenceException e) {
			extentTest.log(Status.WARNING, "Stale element, retrying click... " + elementName);
			log.warn("Stale element detected on: " + elementName, e);

			waitForMillis(500);
			click(element, elementName);

		} catch (Exception e) {
			extentTest.log(Status.FAIL, "Unexpected exception during click: " + e.getMessage());
			log.error("Unexpected error on click: " + elementName, e);
			clickByJs(element, elementName);
		}
	}

	//====================Click By JavaScript====================//

	public void clickByJs(WebElement element, String elementName) {
		try {

			//--Check Element Not Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform JavaScript click.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. JS Click failed.");
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Wait For Clickable 
			waitForClickable(element, 10);

			//--Scroll & Highlight (Optional but useful)
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);", element);
			js.executeScript("arguments[0].style.border='3px solid yellow'", element);

			//--Perform JavaScript Click
			js.executeScript("arguments[0].click();", element);

			//--Success Logging
			log.info("JavaScript click performed successfully on: " + elementName);
			extentTest.log(Status.PASS, "Clicked by JavaScript successfully: " + elementName);

		} catch (StaleElementReferenceException e) {

			//--Handle Stale Element
			log.warn("Stale element detected. Retrying JavaScript click on: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element detected. Retrying JS Click: " + elementName);

			waitForMillis(500);
			clickByJs(element, elementName);

		} catch (Exception e) {

			//--Unexpected Errors
			log.error("JavaScript click failed on: " + elementName, e);
			extentTest.log(Status.FAIL, "JS Click failed for: " + elementName + " | Exception: " + e.getMessage());
			throw new RuntimeException("JS Click failed on: " + elementName, e);
		}
	}

	//====================SendKeys====================//

	public void sendKeys(WebElement element, String value, String elementName) {
		try {

			//--Check Element Not Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform SendKeys.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. SendKeys failed.");
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Check Displayed
			if (element.isDisplayed()) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");

				//--Check Enabled
				if (element.isEnabled()) {
					log.info(elementName + " Element is Enabled [WebElement :- '" + element + "']");

					//--Wait For Visibility
					waitForVisibility(element, 10);

					//--Clear Existing Value
					element.clear();
					log.info("Cleared existing text for: " + elementName);

					//--Send Keys
					element.sendKeys(value);

					//--Success Logging
					log.info("Sent text '" + value + "' successfully to: " + elementName);
					extentTest.log(Status.PASS, "Sent text '" + value + "' successfully to element: " + elementName);
					return;

				} else {
					log.error(elementName + " Element is Disabled [WebElement :- '" + element + "']");
					extentTest.log(Status.WARNING, elementName + " Element is Disabled. Using JavaScript SendKeys.");
					sendKeysByJS(element, value, elementName);
					return;
				}

			} else {
				log.error(elementName + " Element is Invisible [WebElement :- '" + element + "']");
				extentTest.log(Status.WARNING, elementName + " Element is Invisible. Using JavaScript SendKeys.");
				sendKeysByJS(element, value, elementName);
				return;
			}

		} catch (ElementNotInteractableException e) {

			//--Fallback to JS
			log.warn("Element not interactable. Using JavaScript SendKeys for: " + elementName, e);
			extentTest.log(Status.WARNING, "Element not interactable. Using JS SendKeys: " + elementName);
			sendKeysByJS(element, value, elementName);

		} catch (StaleElementReferenceException e) {

			//--Retry Logic
			log.warn("Stale element detected. Retrying SendKeys for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element detected. Retrying SendKeys: " + elementName);

			waitForMillis(500);
			sendKeys(element, value, elementName);

		} catch (Exception e) {

			//--Unexpected Error
			log.error("Unexpected error during SendKeys for: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to SendKeys to element: " + elementName + " | " + e.getMessage());

			sendKeysByJS(element, value, elementName);
		}
	}

	//====================SendKeys By JavaScript====================//

	public void sendKeysByJS(WebElement element, String value, String elementName) {
		try {

			//--Check Element Not Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform JS SendKeys.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. JS SendKeys failed.");
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Check Displayed
			if (element.isDisplayed()) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");
			} else {
				log.warn(elementName + " Element is NOT Displayed. Proceeding with JS SendKeys.");
				extentTest.log(Status.WARNING, elementName + " Element not displayed. Still performing JS SendKeys.");
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Perform JS SendKeys
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].value = arguments[1];", element, value);

			//--Success Logging
			log.info("JavaScript SendKeys executed successfully for: " + elementName + " | Value: " + value);
			extentTest.log(Status.PASS, "JavaScript SendKeys successful for: " + elementName + " | Value: " + value);

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale
			log.warn("Stale element detected in JS SendKeys. Retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element in JS SendKeys. Retrying: " + elementName);

			waitForMillis(500);
			sendKeysByJS(element, value, elementName);

		} catch (Exception e) {

			//--Unexpected Error
			log.error("JavaScript SendKeys failed for: " + elementName, e);
			extentTest.log(Status.FAIL, "JS SendKeys failed for: " + elementName + " | " + e.getMessage());

			throw new RuntimeException("JS SendKeys failed for element: " + elementName, e);
		}
	}


	//====================Get Text====================//

	public String getText(WebElement element, String elementName) {
		try {

			//--Check Element Not Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot retrieve text.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Text retrieval failed.");
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Check Displayed
			if (element.isDisplayed()) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");
			} else {
				log.warn(elementName + " Element is NOT Displayed. Attempting to get text anyway.");
				extentTest.log(Status.WARNING, elementName + " Element not displayed. Attempting to fetch text.");
			}

			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Actually get Text
			String text = element.getText().trim();

			//--Success Logs
			log.info("Text retrieved from " + elementName + " : '" + text + "'");
			extentTest.log(Status.PASS, "Retrieved text from '" + elementName + "' : '" + text + "'");

			return text;

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element detected while getting text for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while getting text. Retrying: " + elementName);

			waitForMillis(500);
			return getText(element, elementName);

		} catch (Exception e) {

			//--Unexpected Error
			log.error("Failed to get text from: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to get text from: " + elementName + " | Exception: " + e.getMessage());

			throw new RuntimeException("Unable to get text from: " + elementName, e);
		}
	}


	//==================== Get Attribute ===========================//

	public String getAttribute(WebElement element, String attributeName, String elementName) {
		try {

			//--Check Element Not Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot retrieve attribute: " + attributeName);
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Failed to get attribute: " + attributeName);
				throw new RuntimeException("WebElement is NULL for: " + elementName);
			}

			//--Check Displayed
			if (element.isDisplayed()) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");
			} else {
				log.warn(elementName + " Element is NOT Displayed. Attempting to get attribute anyway.");
				extentTest.log(Status.WARNING, elementName + " Element not displayed. Trying to fetch attribute.");
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);


			//--Actually get Attribute Value
			String value = element.getAttribute(attributeName);

			//--Success Logs
			log.info("Retrieved attribute [" + attributeName + "] from " + elementName + " : '" + value + "'");
			extentTest.log(Status.PASS, "Retrieved attribute [" + attributeName + "] from '" + elementName + "' : '" + value + "'");

			return value;

		} catch (StaleElementReferenceException e) {

			//--Retry Stale Element
			log.warn("Stale element while getting attribute [" + attributeName + "] from: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while getting attribute. Retrying: " + elementName);

			waitForMillis(500);
			return getAttribute(element, attributeName, elementName);

		} catch (Exception e) {

			//--Unexpected Error
			log.error("Failed to get attribute [" + attributeName + "] from: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to get attribute [" + attributeName + "] from: " + elementName +
					" | Exception: " + e.getMessage());

			return null;
		}
	}


	//==================== Is Displayed ===========================//

	public boolean isDisplayed(WebElement element, String elementName) {
		try {

			//--Check Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot check display status.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Display check failed.");
				return false;
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Check Displayed
			boolean displayed = element.isDisplayed();

			if (displayed) {
				log.info(elementName + " Element is Displayed [WebElement :- '" + element + "']");
				extentTest.log(Status.PASS, "Element is displayed: " + elementName);
			} else {
				log.warn(elementName + " Element is NOT Displayed [WebElement :- '" + element + "']");
				extentTest.log(Status.WARNING, "Element is NOT displayed: " + elementName);
			}

			return displayed;

		} catch (StaleElementReferenceException e) {

			//--Retry Stale Element
			log.warn("Stale element while checking display status for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while checking display status. Retrying: " + elementName);

			waitForMillis(500);
			return isDisplayed(element, elementName);

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Failed to verify display status for: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to verify display status for: " + elementName +
					" | Exception: " + e.getMessage());

			return false;
		}
	}



	//==================== Is Enabled ===========================//

	public boolean isEnabled(WebElement element, String elementName) {
		try {

			//--Check Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot check enabled status.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Enabled check failed.");
				return false;
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Check Enabled
			boolean enabled = element.isEnabled();

			if (enabled) {
				log.info(elementName + " Element is Enabled [WebElement :- '" + element + "']");
				extentTest.log(Status.PASS, "Element is enabled: " + elementName);
			} else {
				log.warn(elementName + " Element is NOT Enabled [WebElement :- '" + element + "']");
				extentTest.log(Status.WARNING, "Element is NOT enabled: " + elementName);
			}
			return enabled;

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element while checking enabled status for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while checking enabled status. Retrying: " + elementName);

			waitForMillis(500);
			return isEnabled(element, elementName);

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Failed to verify enabled status for: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to verify enabled status for: " + elementName +
					" | Exception: " + e.getMessage());
			return false;
		}
	}



	//==================== Is Selected ===========================//

	public boolean isSelected(WebElement element, String elementName) {
		try {

			//--Check Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot check selected status.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Selected check failed.");
				return false;
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Check Selected
			boolean selected = element.isSelected();

			if (selected) {
				log.info(elementName + " Element is Selected [WebElement :- '" + element + "']");
				extentTest.log(Status.PASS, "Element is selected: " + elementName);
			} else {
				log.warn(elementName + " Element is NOT Selected [WebElement :- '" + element + "']");
				extentTest.log(Status.WARNING, "Element is NOT selected: " + elementName);
			}

			return selected;

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element while checking selected status for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while checking selected status. Retrying: " + elementName);

			waitForMillis(500);
			return isSelected(element, elementName);

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Failed to verify selected status for: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to verify selected status for: " + elementName +
					" | Exception: " + e.getMessage());

			return false;
		}
	}


	//==================== Scroll To Element By JavaScript ===========================//

	public void scrollToElementByJs(WebElement element, String elementName) {
		try {

			//--Check Null
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot scroll.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Scroll failed.");
				return;
			}
			//--Wait For Visibility
			waitForVisibility(element, 10);

			//--Scroll Using JavaScript
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);", element);

			log.info("Scrolled to element successfully: " + elementName);
			extentTest.log(Status.PASS, "Scrolled to element successfully: " + elementName);

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element in scrollToElementByJs, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element while scrolling. Retrying: " + elementName);

			waitForMillis(500);
			scrollToElementByJs(element, elementName);

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Failed to scroll to element: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to scroll to element: " + elementName +
					" | Exception: " + e.getMessage());

			throw new RuntimeException("JavaScript scroll failed for: " + elementName, e);
		}
	}


	//====================Scroll To Top (Keyboard)====================//

	public void scrollToTop() {
		try {

			if (driver == null) {
				log.error("Driver is NULL. Cannot scroll to top.");
				extentTest.log(Status.FAIL, "Driver is NULL. Scroll to top failed.");
				return;
			}

			Actions actions = new Actions(driver);
			actions.keyDown(Keys.CONTROL)
			.sendKeys(Keys.HOME)
			.keyUp(Keys.CONTROL)
			.perform();

			log.info("Scrolled to top using keyboard successfully.");
			extentTest.log(Status.PASS, "Scrolled to top using keyboard.");

		} catch (Exception e) {
			log.error("Scroll to top using keyboard failed.", e);
			extentTest.log(Status.FAIL, "Scroll to top failed | " + e.getMessage());
			throw new RuntimeException("Scroll to top failed", e);
		}
	}




	//==================== Scroll To Top By JavaScript ===========================//

	public void scrollToTopByJs() {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform scroll to top.");
				extentTest.log(Status.FAIL, "Driver is NULL. Scroll to top failed.");
				return;
			}

			//--Scroll To Top
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, 0);");

			log.info("Scrolled to top of the page successfully.");
			extentTest.log(Status.PASS, "Scrolled to top of the page successfully.");

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("JavaScript scroll to top failed.", e);
			extentTest.log(Status.FAIL, "JavaScript scroll to top failed | Exception: " + e.getMessage());

			throw new RuntimeException("JavaScript scroll to top failed", e);
		}
	}


	//==================== Scroll To Bottom ===========================//

	public void scrollToBottom() {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform scroll to bottom.");
				extentTest.log(Status.FAIL, "Driver is NULL. Scroll to bottom failed.");
				return;
			}
			//--Wait for page load
			waitForPageToLoad(10);

			//--Scroll To Bottom Using Keyboard
			Actions actions = new Actions(driver);
			actions.keyDown(Keys.CONTROL)
			.sendKeys(Keys.END)
			.keyUp(Keys.CONTROL)
			.perform();

			log.info("Scrolled to bottom of the page successfully (Without JavaScript).");
			extentTest.log(Status.PASS, "Scrolled to bottom of the page successfully (Without JavaScript).");

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Scroll to bottom without JavaScript failed.", e);
			extentTest.log(
					Status.FAIL,
					"Scroll to bottom without JavaScript failed | Exception: " + e.getMessage()
					);

			throw new RuntimeException("Scroll to bottom without JavaScript failed", e);
		}
	}



	//==================== Scroll To Bottom By JavaScript ===========================//

	public void scrollToBottomByJs() {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform scroll to bottom.");
				extentTest.log(Status.FAIL, "Driver is NULL. Scroll to bottom failed.");
				return;
			}
			//--Wait For PAge Load
			waitForPageToLoad(10);

			//--Scroll To Bottom
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

			log.info("Scrolled to bottom of the page successfully.");
			extentTest.log(Status.PASS, "Scrolled to bottom of the page successfully.");

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("JavaScript scroll to bottom failed.", e);
			extentTest.log(Status.FAIL, "JavaScript scroll to bottom failed | Exception: " + e.getMessage());

			throw new RuntimeException("JavaScript scroll to bottom failed", e);
		}
	}


	//==================== Click By Actions Class ===========================//

	public void clickByActions(WebElement element, String elementName) {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform Actions click on: " + elementName);
				extentTest.log(Status.FAIL, "Driver is NULL. Actions click failed for: " + elementName);
				return;
			}

			//--Check Null Element
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform Actions click.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Actions click failed.");
				return;
			}
			//--Waith For Clickable
			waitForClickable(element, 10);
			//--Perform Actions Click
			Actions actions = new Actions(driver);
			actions.click(element).perform();

			log.info("Actions click performed successfully on: " + elementName);
			extentTest.log(Status.PASS, "Actions click performed successfully on: " + elementName);

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element detected in clickByActions, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element in Actions click. Retrying: " + elementName);

			waitForMillis(500);
			clickByActions(element, elementName);

		} catch (Exception e) {

			//--Unexpected Error
			log.error("Actions click failed on: " + elementName, e);
			extentTest.log(Status.FAIL, "Actions click failed on: " + elementName +
					" | Reason: " + e.getMessage());

			throw new RuntimeException("Actions click failed on: " + elementName, e);
		}
	}


	//==================== Double Click By Actions ==========================//

	public void doubleClickByActions(WebElement element, String elementName) {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform double-click on: " + elementName);
				extentTest.log(Status.FAIL, "Driver is NULL. Double-click failed for: " + elementName);
				return;
			}

			//--Check Element NULL
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform double-click.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Double-click failed.");
				return;
			}

			//--Waith For Clickable
			waitForClickable(element, 10);

			//--Perform Double Click
			Actions actions = new Actions(driver);
			actions.doubleClick(element).perform();

			log.info("Double-click performed successfully on: " + elementName);
			extentTest.log(Status.PASS, "Double-clicked successfully on: " + elementName);

		} catch (StaleElementReferenceException e) {

			//--Retry for Stale Element
			log.warn("Stale element in doubleClickByActions, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element in doubleClickByActions. Retrying: " + elementName);

			waitForMillis(500);
			new Actions(driver).doubleClick(element).perform();

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Double-click failed on: " + elementName, e);
			extentTest.log(Status.FAIL, "Double-click failed on: " + elementName +
					" | Reason: " + e.getMessage());

			throw new RuntimeException("Double-click failed on: " + elementName, e);
		}
	}

	//==================== Context Click (Right-Click) By Actions ====================//

	public void contextClick(WebElement element, String elementName) {
		try {

			//--Check Driver
			if (driver == null) {
				log.error("Driver is NULL. Cannot perform right-click on: " + elementName);
				extentTest.log(Status.FAIL, "Driver is NULL. Right-click failed on: " + elementName);
				return;
			}

			//--Check Element
			if (element == null) {
				log.error(elementName + " WebElement is NULL. Cannot perform right-click.");
				extentTest.log(Status.FAIL, elementName + " WebElement is NULL. Right-click failed.");
				return;
			}

			//--Waith For Clickable
			waitForClickable(element, 10);

			//--Perform Right-Click
			new Actions(driver).contextClick(element).perform();

			log.info("Right-click performed successfully on: " + elementName);
			extentTest.log(Status.PASS, "Right-click performed successfully on: " + elementName);

		} catch (StaleElementReferenceException e) {

			//--Retry Stale Element
			log.warn("Stale element detected while right-clicking, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element detected, retrying right-click for: " + elementName);

			waitForMillis(500);
			new Actions(driver).contextClick(element).perform();

		} catch (Exception e) {

			//--Unexpected Failure
			log.error("Right-click failed on: " + elementName, e);
			extentTest.log(Status.FAIL, "Right-click failed on: " + elementName +
					" | Reason: " + e.getMessage());

			throw new RuntimeException("Right-click failed on: " + elementName, e);
		}
	}



	//==================== Drag And Drop By Actions ====================//

	public void dragAndDropByActions(WebElement source, WebElement target, String sourceName, String targetName) {
		try {

			//-- Check NULL Elements
			if (source == null) {
				log.error("Source element is NULL → " + sourceName);
				extentTest.log(Status.FAIL, "Source element is NULL: " + sourceName);
				return;
			}

			if (target == null) {
				log.error("Target element is NULL → " + targetName);
				extentTest.log(Status.FAIL, "Target element is NULL: " + targetName);
				return;
			}

			//-- Wait for both elements to be visible (required for drag & drop)
			waitForVisibility(source, 10);
			waitForVisibility(target, 10);

			//-- Optional: wait until both are clickable (safer for complex UIs)
			waitForClickable(source, 10);
			waitForClickable(target, 10);

			//-- Perform Drag And Drop
			new Actions(driver).dragAndDrop(source, target).perform();

			log.info("Drag and drop performed successfully → " + sourceName + " → " + targetName);
			extentTest.log(Status.PASS,
					"Drag and drop performed successfully from: [' " +
							sourceName + " '] → [' " + targetName + " ']");

		} catch (StaleElementReferenceException e) {

			//-- Retry Stale Element
			log.warn("Stale element detected during drag and drop, retrying...", e);
			extentTest.log(Status.WARNING, "Stale element detected, retrying drag and drop...");

			waitForMillis(500);

			new Actions(driver).dragAndDrop(source, target).perform();

		} catch (Exception e) {

			//-- Unexpected Failure
			log.error("Drag and drop failed from: " + sourceName + " to: " + targetName, e);
			extentTest.log(Status.FAIL,
					"Drag and drop failed from: " + sourceName + " → " + targetName +
					" | Reason: " + e.getMessage());

			throw new RuntimeException("Drag and drop failed from: " + sourceName +
					" to: " + targetName, e);
		}
	}


	//==================== SendKeys By Actions ====================//

	public void sendKeysByActions(WebElement element, String value, String elementName) {
		try {

			if (element == null) {
				throw new IllegalArgumentException("Element is NULL for: " + elementName);
			}

			waitForVisibility(element, 10);
			waitForClickable(element, 10);

			 actions = new Actions(driver);

			actions.moveToElement(element).click().sendKeys(value).build().perform();
			log.info("Text '" + value + "' entered successfully using Actions on: " + elementName);
			extentTest.log(Status.PASS,"Text '" + value + "' entered successfully using Actions on: " + elementName);

		} catch (StaleElementReferenceException e) {

			log.error("Stale element encountered while sending keys on: " + elementName, e);
			extentTest.log(Status.FAIL,"Stale element encountered on: " + elementName);

			throw e;

		} catch (Exception e) {

			log.error("Actions sendKeys failed on: " + elementName, e);
			extentTest.log(Status.FAIL,"Actions sendKeys failed on: " + elementName + " | Reason: " + e.getMessage());
			throw new RuntimeException("Actions sendKeys failed on: " + elementName, e);
		}
	}

	//==================== Select By Visible Text ====================//

	public void selectByVisibleText(WebElement element, String text, String elementName) {
		try {

			// Null Safety
			if (element == null) {
				log.error("Element is NULL for: " + elementName);
				extentTest.log(Status.FAIL, "Element is NULL for: " + elementName);
				return;
			}

			//--Wait For Visibility 
			waitForVisibility(element, 10);

			// Perform Select Action
			Select dropdown = new Select(element);
			dropdown.selectByVisibleText(text);

			log.info("Selected option by visible text '" + text + "' on: " + elementName);
			extentTest.log(Status.PASS,
					"Selected option by visible text '" + text + "' on " + elementName);

		} catch (StaleElementReferenceException e) {

			// Handle Stale Element
			log.warn("Stale element detected in selectByVisibleText, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING,
					"Stale element detected, retrying selectByVisibleText for: " + elementName);

			waitForMillis(500);

			// Retry
			Select dropdown = new Select(element);
			dropdown.selectByVisibleText(text);

			log.info("Retry successful: selected '" + text + "' on: " + elementName);
			extentTest.log(Status.PASS,
					"Retry successful: selected '" + text + "' on " + elementName);

		} catch (NoSuchElementException e) {

			// Option not available
			log.warn("Option '" + text + "' not found for dropdown: " + elementName);
			extentTest.log(Status.WARNING,
					"Option '" + text + "' not found in dropdown: " + elementName);

		} catch (Exception e) {

			// Unexpected Failure
			log.error("Failed to select by visible text '" + text + "' on: " + elementName, e);
			extentTest.log(Status.FAIL,
					"Failed to select by visible text '" + text + "' on " + elementName
					+ " | Reason: " + e.getMessage());

			throw new RuntimeException(
					"selectByVisibleText failed on: " + elementName, e);
		}
	}

	//========================Press Keys =============================//

	public void pressKey(WebElement element, Keys key, int timeout, String elementName) {

		try {

			// ===== Null Check =====
			if (element == null) {
				log.error("Element is NULL: " + elementName);
				extentTest.log(Status.FAIL, "Element is NULL: " + elementName);
				throw new IllegalArgumentException("Element is NULL: " + elementName);
			}

			// ===== Wait Until Clickable =====
			waitForClickable(element, timeout);


			// ===== Display Check =====
			if (!element.isDisplayed()) {
				log.error("Element is NOT displayed: " + elementName);
				extentTest.log(Status.FAIL, "Element is NOT displayed: " + elementName);
				throw new ElementNotInteractableException("Element not displayed: " + elementName);
			}


			// ===== Enabled Check =====
			if (!element.isEnabled()) {
				log.error("Element is NOT enabled: " + elementName);
				extentTest.log(Status.FAIL, "Element is NOT enabled: " + elementName);
				throw new ElementNotInteractableException("Element not enabled: " + elementName);
			}

			// ===== Perform Key Press =====
			element.sendKeys(key);

			log.info("Successfully pressed key '" + key.name() + "' on: " + elementName);
			extentTest.log(Status.PASS,
					"Successfully pressed key '" + key.name() + "' on: " + elementName);

		} catch (TimeoutException e) {

			log.error("Timeout while waiting for element: " + elementName, e);
			extentTest.log(Status.FAIL,
					"Timeout: Element not clickable within " + timeout + " seconds: " + elementName);
			throw e;

		} catch (ElementNotInteractableException e) {

			log.error("Element not interactable: " + elementName, e);
			extentTest.log(Status.FAIL,
					"Element not interactable: " + elementName + " | " + e.getMessage());
			throw e;

		} catch (Exception e) {

			log.error("Unexpected error while pressing key on: " + elementName, e);
			extentTest.log(Status.FAIL,
					"Unexpected error while pressing key on: " + elementName +
					" | Reason: " + e.getMessage());
			throw new RuntimeException(
					"Press key failed on: " + elementName, e);
		}
	}

	//================== Select By Value ==================//

	public void selectByValue(WebElement element, String value, String elementName) {
		try {
			if (element == null) {
				log.error("Element is NULL for: " + elementName);
				extentTest.log(Status.FAIL, "Element is NULL for: " + elementName);
				return;
			}
			//--Wait For Visibility 
			waitForVisibility(element, 10);

			Select dropdown = new Select(element);
			dropdown.selectByValue(value);

			log.info("Selected option by value '" + value + "' on: " + elementName);
			extentTest.log(Status.PASS, "Selected option by value '" + value + "' on: " + elementName);

		} catch (StaleElementReferenceException e) {

			extentTest.log(Status.WARNING, "Stale element detected, retrying selectByValue for: " + elementName);
			log.warn("Stale element detected in selectByValue for: " + elementName, e);

			waitForMillis(500);

			Select dropdown = new Select(element);
			dropdown.selectByValue(value);

			log.info("Retry successful: selected by value '" + value + "' on: " + elementName);
			extentTest.log(Status.PASS, "Retry successful: selected by value '" + value + "' on: " + elementName);

		} catch (NoSuchElementException e) {

			log.warn("Option with value '" + value + "' not found for: " + elementName);
			extentTest.log(Status.WARNING, "Option with value '" + value + "' not found for: " + elementName);

		} catch (Exception e) {

			log.error("Failed to select option by value '" + value + "' on: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to select by value '" + value + "' on: " + elementName);

			throw new RuntimeException("selectByValue failed on: " + elementName, e);
		}
	}


	//================== Select By Index ==================//

	public void selectByIndex(WebElement element, int index, String elementName) {
		try {
			if (element == null) {
				log.error("Element is NULL for: " + elementName);
				extentTest.log(Status.FAIL, "Element is NULL for: " + elementName);
				return;
			}
			//--Wait For Visibility 
			waitForVisibility(element, 10);

			Select dropdown = new Select(element);
			dropdown.selectByIndex(index);

			log.info("Selected index '" + index + "' on: " + elementName);
			extentTest.log(Status.PASS, "Selected index '" + index + "' on: " + elementName);

		} catch (StaleElementReferenceException e) {
			log.warn("Stale element detected in selectByIndex, retrying for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale element detected, retrying selectByIndex for: " + elementName);

			waitForMillis(500);

			Select dropdown = new Select(element);
			dropdown.selectByIndex(index);

			log.info("Retry successful: selected index '" + index + "' on: " + elementName);
			extentTest.log(Status.PASS, "Retry successful: selected index '" + index + "' on: " + elementName);

		} catch (Exception e) {
			log.error("Failed to select index '" + index + "' on: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to select index '" + index + "' on: " + elementName);

			throw new RuntimeException("selectByIndex failed on: " + elementName, e);
		}
	}

	//================== Get Selected Option Text ==================//

	public String getSelectedOptionText(WebElement element, String elementName) {
		try {
			if (element == null) {
				log.error("Element is NULL for: " + elementName);
				extentTest.log(Status.FAIL, "Element is NULL for: " + elementName);
				return null;
			}
			//--Wait For Visibility 
			waitForVisibility(element, 10);

			Select dropdown = new Select(element);
			String selectedText = dropdown.getFirstSelectedOption().getText();

			log.info("Selected option text retrieved: '" + selectedText + "' from: " + elementName);
			extentTest.log(Status.INFO, "Selected option text: '" + selectedText + "'");

			return selectedText;

		} catch (Exception e) {
			log.error("Failed to get selected option text from: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to get selected option text from: " + elementName);

			throw new RuntimeException("getSelectedOptionText failed on: " + elementName, e);
		}
	}

	//==================== Switch To Frame ====================//

	public void switchToFrame(WebElement element, String elementName) {
		try {

			//-- Null Safety
			if (element == null) {
				log.error("Frame WebElement is NULL for: " + elementName);
				extentTest.log(Status.FAIL, "Frame WebElement is NULL for: " + elementName);
				throw new RuntimeException("Frame WebElement is NULL for: " + elementName);
			}
			//--Wait For Visibility 
			waitForVisibility(element, 10);

			//-- Switch to Frame
			driver.switchTo().frame(element);

			log.info("Switched to frame successfully: " + elementName);
			extentTest.log(Status.PASS, "Switched to frame successfully: " + elementName);

		} catch (StaleElementReferenceException e) {

			//-- Stale Handling
			log.warn("Stale frame element detected, retrying switchToFrame for: " + elementName, e);
			extentTest.log(Status.WARNING, "Stale frame detected, retrying for: " + elementName);

			waitForMillis(500);

			driver.switchTo().frame(element);

			log.info("Retry successful: switched to frame: " + elementName);
			extentTest.log(Status.PASS, "Retry successful: switched to frame: " + elementName);

		} catch (NoSuchFrameException e) {

			//-- Frame Not Found
			log.error("Frame not found: " + elementName, e);
			extentTest.log(Status.FAIL, "Frame not found: " + elementName);

		} catch (Exception e) {

			//-- Unexpected Failure
			log.error("Failed to switch to frame: " + elementName, e);
			extentTest.log(Status.FAIL, "Failed to switch to frame: " + elementName + " | Reason: " + e.getMessage());

			throw new RuntimeException("Failed to switch to frame: " + elementName, e);
		}
	}


	//===================== Switch To Default Content =====================//

	public void switchToDefaultContent() {
		try {

			//-- Switch to Default Content
			driver.switchTo().defaultContent();

			log.info("Switched to default content successfully");
			extentTest.log(Status.PASS, "Switched to default content successfully");

		} catch (StaleElementReferenceException e) {

			//-- Stale Handling (rare but safe)
			log.warn("Stale element detected while switching to default content", e);
			extentTest.log(Status.WARNING, "Stale element detected, retrying to switch to default content");

			waitForMillis(500);
			driver.switchTo().defaultContent();

			log.info("Retry successful: switched to default content");
			extentTest.log(Status.PASS, "Retry successful: switched to default content");

		} catch (Exception e) {

			//-- Unexpected Errors
			log.error("Failed to switch to default content", e);
			extentTest.log(Status.FAIL, "Failed to switch to default content | Reason: " + e.getMessage());

			throw new RuntimeException("Failed to switch to default content", e);
		}
	}

	//====================Switch To Window Handle==================//

	public void switchToWindow(String title) {

		try {
			// ---------------- NULL CHECK ----------------
			if (title == null || title.trim().isEmpty()) {
				log.error("Provided window title is NULL or EMPTY.");
				throw new IllegalArgumentException("Window title cannot be NULL or EMPTY.");
			}

			log.info("Attempting to switch to window with title: " + title);

			// Fetch all window handles
			Set<String> windowHandles = driver.getWindowHandles();

			if (windowHandles == null || windowHandles.isEmpty()) {
				log.warn("No window handles available.");
				extentTest.log(Status.FAIL, "No browser windows found to switch.");
				return;
			}

			// Loop through each window
			for (String handle : windowHandles) {
				driver.switchTo().window(handle);
				String currentTitle = driver.getTitle();

				log.debug("Checking window: Handle = " + handle + ", Title = " + currentTitle);

				if (currentTitle != null && currentTitle.equalsIgnoreCase(title)) {
					log.info("Successfully switched to window with title: " + title);
					extentTest.log(Status.PASS, "Switched to window with title: " + title);
					return;
				}
			}

			// If matching title was not found
			log.warn("No window found with the title: " + title);
			extentTest.log(Status.FAIL, "No window found with title: " + title);

		} catch (Exception e) {
			log.error("Failed to switch to window: " + title + " | Reason: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Failed to switch to window: " + title + " | Reason: " + e.getMessage());
			throw new RuntimeException("Failed to switch to window: " + title, e);
		}
	}

	// ---------------- Switch To Windows By URL ----------------//

	public void SwitchToWindowByUrl(String url) {
		try {
			// ---------------- NULL CHECK ----------------
			if (url == null || url.trim().isEmpty()) {
				log.error("Provided window URL is NULL or EMPTY.");
				throw new IllegalArgumentException("Window URL cannot be NULL or EMPTY.");
			}

			log.info("Attempting to switch to window with URL: " + url);

			// Fetch all window handles
			Set<String> windowHandles = driver.getWindowHandles();

			if (windowHandles == null || windowHandles.isEmpty()) {
				log.warn("No window handles available.");
				extentTest.log(Status.FAIL, "No browser windows found to switch.");
				return;
			}

			// Loop through each window
			for (String handle : windowHandles) {
				driver.switchTo().window(handle);
				String currentUrl = driver.getCurrentUrl();

				log.debug("Checking window: Handle = " + handle + ", URL = " + currentUrl);

				if (currentUrl != null && currentUrl.equals(url)) {
					log.info("Successfully switched to window with URL: " + url);
					extentTest.log(Status.PASS, "Switched to window with URL: " + url);
					return; // Exit method if window found
				}
			}

			// If no window matches the URL
			log.warn("Window with URL '" + url + "' not found");
			extentTest.log(Status.WARNING, "Window with URL '" + url + "' not found");

		} catch (Exception e) {
			log.error("Failed to switch to window by URL: " + url + " | Reason: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Failed to switch to window by URL: " + url + " | Reason: " + e.getMessage());
		}
	}


	//=====================Upload Files========================//

	public void uploadFile(WebElement element, String filePath, String elementName) {
		try {
			//--Null Check
			if (element == null) {
				log.error("WebElement '" + elementName + "' is NULL.");
				throw new IllegalArgumentException("WebElement cannot be NULL.");
			}
			if (filePath == null || filePath.trim().isEmpty()) {
				log.error("File path is NULL or EMPTY for element '" + elementName + "'.");
				throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
			}

			log.info("Uploading file to element: " + elementName + " | File: " + filePath);

			// Perform file upload
			element.sendKeys(filePath);

			log.info("File uploaded successfully to " + elementName + ": " + filePath);
			extentTest.log(Status.PASS, "File uploaded successfully to " + elementName + ": " + filePath);

		} catch (Exception e) {
			String errorMsg = "Failed to upload file to " + elementName + " - " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//====================Upload File By Robot Class==================//

	public void uploadFileByRobot(String filePath) {
		try {
			// ---------------- NULL CHECK ----------------
			if (filePath == null || filePath.trim().isEmpty()) {
				log.error("File path is NULL or EMPTY.");
				throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
			}

			log.info("Uploading file using Robot: " + filePath);

			// Copy file path to clipboard
			StringSelection selection = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			robot.delay(500);

			// Simulate Ctrl+V to paste file path
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.delay(500);

			// Press Enter to confirm file selection
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			log.info("File uploaded successfully: " + filePath);
			extentTest.log(Status.PASS, "File uploaded successfully: " + filePath);

		} catch (Exception e) {
			String errorMsg = "Error while uploading file using Robot: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Create Folder Or Directory==================//

	public void createFolder(String directoryPath) {
		try {
			//--Null/Empty Check
			if (directoryPath == null || directoryPath.trim().isEmpty()) {
				log.error("Directory path is NULL or EMPTY.");
				throw new IllegalArgumentException("Directory path cannot be NULL or EMPTY.");
			}

			log.info("Attempting to create folder: " + directoryPath);

			//--Create Folder
			file = new File(directoryPath);
			boolean created = file.mkdir();

			//--Check Success
			if (created) {
				log.info("Folder created successfully: " + directoryPath);
			} else {
				log.warn("Folder already exists or could not be created: " + directoryPath);
			}

			extentTest.log(Status.PASS, "Folder created: " + directoryPath + " | Success: " + created);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to create folder: " + directoryPath + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Create File==================//

	public void createFile(String filePath) {
		try {
			//--Null/Empty Check
			if (filePath == null || filePath.trim().isEmpty()) {
				log.error("File path is NULL or EMPTY.");
				throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
			}

			log.info("Attempting to create file: " + filePath);

			//--Create File
			file = new File(filePath);
			boolean created = file.createNewFile(); // create new file if it does not exist

			//--Check Success
			if (created) {
				log.info("File created successfully: " + filePath);
			} else {
				log.warn("File already exists or could not be created: " + filePath);
			}

			extentTest.log(Status.PASS, "File created successfully: " + filePath + " | Created: " + created);

		} catch (IOException e) {
			//--Exception Handling
			String errorMsg = "Failed to create file: " + filePath + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Validate File==================//

	public boolean validateCreateFile(String filePath) {
		//--Null/Empty Check
		if (filePath == null || filePath.trim().isEmpty()) {
			log.error("File path is NULL or EMPTY.");
			throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
		}

		log.info("Validating file existence: " + filePath);

		//--Check File Existence
		file = new File(filePath);
		boolean exists = file.exists();

		//--Log Result
		if (exists) {
			log.info("Validation Passed: File exists at " + file.getAbsolutePath());
			extentTest.log(Status.PASS, "Validation Passed: File exists at " + file.getAbsolutePath());
		} else {
			log.warn("Validation Failed: File not found at " + file.getAbsolutePath());
			extentTest.log(Status.FAIL, "Validation Failed: File not found at " + file.getAbsolutePath());
		}

		return exists;
	}



	//===============Delete File==================//

	public void deleteFile(String filePath) {
		try {
			//--Null/Empty Check
			if (filePath == null || filePath.trim().isEmpty()) {
				log.error("File path is NULL or EMPTY.");
				throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
			}

			log.info("Attempting to delete file: " + filePath);

			//--Delete File
			file = new File(filePath);
			boolean deleted = file.delete();

			//--Log Result
			if (deleted) {
				log.info("File deleted successfully: " + filePath);
				extentTest.log(Status.PASS, "File deleted successfully: " + filePath);
			} else {
				log.warn("File could not be deleted (may not exist): " + filePath);
				extentTest.log(Status.WARNING, "File could not be deleted (may not exist): " + filePath);
			}

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Error while deleting file: " + filePath + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Validate & Delete File==================//

	public void validateDeleteFile(String filePath) {
		try {
			//--Null/Empty Check
			if (filePath == null || filePath.trim().isEmpty()) {
				log.error("File path is NULL or EMPTY.");
				throw new IllegalArgumentException("File path cannot be NULL or EMPTY.");
			}

			log.info("Validating and attempting to delete file: " + filePath);

			//--Check File Existence
			file = new File(filePath);
			if (file.exists()) {
				//--Delete File
				boolean deleted = file.delete();

				//--Validate Deletion
				if (deleted && !file.exists()) {
					log.info("File deleted successfully: " + filePath);
					extentTest.log(Status.PASS, "File deleted successfully: " + filePath);
				} else {
					log.warn("File could not be deleted: " + filePath);
					extentTest.log(Status.WARNING, "File could not be deleted: " + filePath);
				}
			} else {
				log.warn("File does not exist: " + filePath);
				extentTest.log(Status.WARNING, "File does not exist: " + filePath);
			}

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Error while deleting file: " + filePath + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}



	//===============Get Selected Dropdown Option==================//

	public String getSelectedDropdownOption(WebElement element, String elementName) {
		try {
			//--Null Check
			if (element == null) {
				log.error("Dropdown element '" + elementName + "' is NULL.");
				throw new IllegalArgumentException("Dropdown element cannot be NULL.");
			}

			log.info("Fetching selected option from dropdown: " + elementName);

			//--Get Selected Option
			Select dropdown = new Select(element);
			String selectedText = dropdown.getFirstSelectedOption().getText();

			log.info("Selected option text in " + elementName + ": '" + selectedText + "'");
			extentTest.log(Status.PASS, "Selected option text in " + elementName + ": '" + selectedText + "'");

			return selectedText;

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to get selected option from " + elementName + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Get All Dropdown Options==================//

	public List<String> getAllDropdownOptions(WebElement element, String elementName) {
		try {
			//--Null Check
			if (element == null) {
				log.error("Dropdown element '" + elementName + "' is NULL.");
				throw new IllegalArgumentException("Dropdown element cannot be NULL.");
			}

			log.info("Fetching all options from dropdown: " + elementName);

			//--Get All Options
			Select dropdown = new Select(element);
			List<WebElement> options = dropdown.getOptions();
			List<String> optionTexts = options.stream().map(WebElement::getText).toList();

			log.info("Found " + optionTexts.size() + " options in dropdown: " + elementName);
			extentTest.log(Status.INFO, "Found " + optionTexts.size() + " options in dropdown: " + elementName);

			return optionTexts;

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to get dropdown options from " + elementName + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Is Dropdown Option Present==================//

	public boolean isDropdownOptionPresent(WebElement element, String optionText, String elementName) {
		try {
			//--Null/Empty Check
			if (element == null) {
				log.error("Dropdown element '" + elementName + "' is NULL.");
				throw new IllegalArgumentException("Dropdown element cannot be NULL.");
			}
			if (optionText == null || optionText.trim().isEmpty()) {
				log.error("Option text is NULL or EMPTY for element '" + elementName + "'.");
				throw new IllegalArgumentException("Option text cannot be NULL or EMPTY.");
			}

			log.info("Checking if option '" + optionText + "' is present in dropdown: " + elementName);

			//--Check Option Presence
			List<String> options = getAllDropdownOptions(element, elementName);
			boolean exists = options.contains(optionText);

			log.info("Option '" + optionText + "' exists in " + elementName + ": " + exists);
			extentTest.log(Status.INFO, "Option '" + optionText + "' exists in " + elementName + ": " + exists);

			return exists;

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to check dropdown option '" + optionText + "' in " + elementName + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			return false;
		}
	}


	//===============Mouse Over==================//

	public void mouseOver(WebElement element, String elementName) {
		try {
			//--Null Check
			if (element == null) {
				log.error("Element '" + elementName + "' is NULL.");
				throw new IllegalArgumentException("Element cannot be NULL.");
			}

			log.info("Hovering over element: " + elementName);

			//--Mouse Over Action
			actions = new Actions(driver);
			actions.moveToElement(element).perform();

			log.info("Hovered over element: " + elementName);
			extentTest.log(Status.PASS, "Hovered over element: " + elementName);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to hover over element: " + elementName + " | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Switch To New Window==================//

	public void switchToNewWindow() {
		try {
			log.info("Attempting to switch to a new window.");

			//--Get Current and All Windows
			String mainWindow = driver.getWindowHandle();
			Set<String> allWindows = driver.getWindowHandles();

			//--Switch to New Window
			for (String window : allWindows) {
				if (!window.equals(mainWindow)) {
					driver.switchTo().window(window);
					log.info("Switched to new window: " + window);
					extentTest.log(Status.PASS, "Switched to new window: " + window);
					return;
				}
			}

			//--If No New Window Found
			String noWindowMsg = "No new window found to switch.";
			log.warn(noWindowMsg);
			throw new RuntimeException(noWindowMsg);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to switch to new window | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Close Current Window==================//

	public void closeCurrentWindow() {
		try {
			log.info("Attempting to close current window.");

			//--Get Current and All Windows
			String currentWindow = driver.getWindowHandle();
			Set<String> allWindows = driver.getWindowHandles();

			//--Close Current Window if Multiple Windows Open
			if (allWindows.size() > 1) {
				driver.close();
				for (String window : allWindows) {
					if (!window.equals(currentWindow)) {
						driver.switchTo().window(window);
						log.info("Closed current window and switched to: " + window);
						extentTest.log(Status.PASS, "Closed current window and switched to: " + window);
						return;
					}
				}
			} else {
				String warningMsg = "Only one window open, cannot close current window.";
				log.warn(warningMsg);
				extentTest.log(Status.WARNING, warningMsg);
			}

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to close current window | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Refresh Page==================//

	public void refreshPage(String message) {
		try {
			//--Refresh Page
			log.info("Refreshing page: " + message);
			driver.navigate().refresh();

			log.info("Page refreshed successfully: " + message);
			extentTest.log(Status.PASS, "Page refreshed successfully: " + message);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to refresh page: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
		}
	}


	//===============Navigate Back==================//

	public void navigateBack(String message) {
		try {
			//--Navigate Back
			log.info("Navigating back: " + message);
			driver.navigate().back();

			log.info("Navigated back successfully: " + message);
			extentTest.log(Status.PASS, "Navigated back successfully: " + message);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to navigate back: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
		}
	}


	//===============Navigate Forward==================//

	public void navigateForward(String message) {
		try {
			//--Navigate Forward
			log.info("Navigating forward: " + message);
			driver.navigate().forward();

			log.info("Navigated forward successfully: " + message);
			extentTest.log(Status.PASS, "Navigated forward successfully: " + message);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to navigate forward: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
		}
	}


	//===============Get Current URL==================//

	public String getCurrentUrl() {
		try {
			//--Get Current URL
			String url = driver.getCurrentUrl();
			log.info("Current URL: " + url);
			extentTest.log(Status.INFO, "Current URL: " + url);

			return url;

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to get current URL | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Get Page Title==================//

	public String getPageTitle() {
		try {
			// Wait for page to fully load using your existing method
			waitForPageToLoad(10);

			// Get page title
			String title = driver.getTitle();
			log.info("Page title: " + title);
			extentTest.log(Status.INFO, "Page title: " + title);

			return title;

		} catch (TimeoutException e) {
			String errorMsg = "Page title did not load within timeout";
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);

		} catch (Exception e) {
			String errorMsg = "Failed to get page title | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}


	//===============Accept Alert==================//

	public void acceptAlert() {
		try {
			//--Switch to Alert
			alert = driver.switchTo().alert();
			alert.accept();

			//--Success Log
			log.info("Alert accepted successfully");
			extentTest.log(Status.PASS, "Alert accepted successfully");

		} catch (NoAlertPresentException e) {
			//--No Alert Present
			String warningMsg = "No alert present to accept";
			log.warn(warningMsg, e);
			extentTest.log(Status.WARNING, warningMsg);
			throw new RuntimeException(warningMsg, e);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to accept alert | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Dismiss Alert==================//

	public void dismissAlert() {
		try {
			//--Switch to Alert
			alert = driver.switchTo().alert();
			alert.dismiss();

			//--Success Log
			log.info("Alert dismissed successfully");
			extentTest.log(Status.PASS, "Alert dismissed successfully");

		} catch (NoAlertPresentException e) {
			//--No Alert Present
			String warningMsg = "No alert present to dismiss";
			log.warn(warningMsg, e);
			extentTest.log(Status.WARNING, warningMsg);
			throw new RuntimeException(warningMsg, e);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to dismiss alert | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Get Single Element Text With Hover==================//

	public String getElementTextWithHover(WebElement element) {
		String text = "";
		Actions action = new Actions(driver);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		try {
			//--Hover on the element
			action.moveToElement(element).perform();
			Thread.sleep(500);

			//--Get text via JavaScript (works even if hidden or dynamic)
			text = (String) js.executeScript("return arguments[0].textContent.trim();", element);

			//--Log result
			if (text != null && !text.isEmpty()) {
				log.info("Captured text: [" + text + "]");
				extentTest.log(Status.PASS, "Captured text: [" + text + "]");
			} else {
				log.warn("No visible text found for element: " + element);
				extentTest.log(Status.WARNING, "No visible text found for element: " + element);
			}

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Error while capturing text: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
		}

		return text;
	}

	//===============Get All Element Texts With Hover==================//

	public List<String> getAllElementTextsWithHover(List<WebElement> elements) {
		List<String> texts = new ArrayList<>();
		Actions action = new Actions(driver);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		//--Log number of elements to process
		log.info("Found " + elements.size() + " elements to extract text.");
		extentTest.log(Status.INFO, "Found " + elements.size() + " elements to extract text.");

		for (WebElement element : elements) {
			try {
				//--Hover on element
				action.moveToElement(element).perform();
				Thread.sleep(800);

				//--Get text via JS
				String text = (String) js.executeScript("return arguments[0].textContent.trim();", element);

				//--Log result
				if (text != null && !text.isEmpty()) {
					texts.add(text);
					log.info("Captured text: [" + text + "]");
					extentTest.log(Status.PASS, "Captured text: <b>" + text + "</b>");
				} else {
					log.warn("No visible text for element: " + element);
					extentTest.log(Status.WARNING, "No visible text for element: " + element);
				}

			} catch (Exception e) {
				//--Exception handling
				String errorMsg = "Error capturing text: " + e.getMessage();
				log.error(errorMsg, e);
				extentTest.log(Status.FAIL, errorMsg);
			}
		}

		//--Log all captured texts
		log.info("All captured texts: " + texts);
		extentTest.log(Status.INFO, "All captured texts: " + texts);

		return texts;
	}

	//===============Get Alert Text==================//

	public String getAlertText() {
		try {
			//--Switch to Alert
			alert = driver.switchTo().alert();
			String text = alert.getText();

			//--Log captured text
			log.info("Alert text: " + text);
			extentTest.log(Status.INFO, "Alert text: " + text);

			//--Accept the alert
			alert.accept();

			return text;

		} catch (NoAlertPresentException e) {
			//--No Alert Present
			String warningMsg = "No alert present to get text from";
			log.warn(warningMsg, e);
			extentTest.log(Status.WARNING, warningMsg);
			throw new RuntimeException(warningMsg, e);

		} catch (Exception e) {
			//--Exception Handling
			String errorMsg = "Failed to get alert text | Reason: " + e.getMessage();
			log.error(errorMsg, e);
			extentTest.log(Status.FAIL, errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}

	//===============Wait For Milliseconds==================//

	private void waitForMillis(long millis) {
		try {
			//--Pause execution
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			//--Handle interruption
			Thread.currentThread().interrupt();
			String warningMsg = "Wait interrupted: " + e.getMessage();
			log.warn(warningMsg, e);
			extentTest.log(Status.WARNING, warningMsg);
		}
	}


	//===============Get All Element Texts==================//

	public List<String> getAllElementTexts(List<WebElement> elements) {
		List<String> texts = new ArrayList<>();

		for (WebElement element : elements) {
			String text = element.getText().trim();
			if (!text.isEmpty()) {
				texts.add(text);
			}
		}

		//--Log captured texts
		log.info("Captured element texts: " + texts);
		extentTest.log(Status.PASS, "Captured element texts: " + texts);

		return texts;
	}

	//============Take ScreenShot========================//

	 public static String getScreenshot(WebDriver driver, ExtentTest extentTest, String fileName) {
	        //  Create a unique timestamp for the file name
	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        
	        //-- Define the path 
	        String screenshotName = fileName + "_" + timestamp + ".png";
	        String directory = Paths.get(System.getProperty("user.dir"), "Reports", "ScreenShots") + File.separator;
	        String fullPath = directory + screenshotName;

	        try {
	            //--Ensure the directory exists
	            File folder = new File(directory);
	            if (!folder.exists()) {
	                folder.mkdirs(); // Creates the folder if it doesn't exist
	            }

	            // 4. Capture screenshot using the passed driver
	            TakesScreenshot ts = (TakesScreenshot) driver;
	            File source = ts.getScreenshotAs(OutputType.FILE);
	            File destination = new File(fullPath);
	            
	            // 5. Copy file to destination
	            FileUtils.copyFile(source, destination);

	            log.info("Screenshot successfully saved at: " + fullPath);
	            
	            // 6. Log to Extent Report if instance is provided
	            if (extentTest != null) {
	                extentTest.log(Status.INFO, "Screenshot captured for failure: " + screenshotName);
	            }

	            return fullPath; // Returning path to attach in BaseTest

	        } catch (IOException e) {
	            log.error("Exception while taking screenshot: " + e.getMessage());
	            return null;
	        }
	 }

	//==================== Validation All Methods ==================================//

	//====================Validate Text Contains==================//
	public void validateTextContainsHard(String actualText, String expectedText, String message) {

		Assert.assertTrue(actualText != null && actualText.contains(expectedText),"Validation Failed: " + message +
				" | Expected to contain: '" + expectedText +
				"', Actual: '" + actualText + "'"
				);
		log.info("Validation Passed: " + message +
				" | Actual: " + actualText + " | Text Matched");
		extentTest.log(Status.PASS,
				"Validation Passed: " + message +
				" | Actual: " + actualText + " | Text Matched");
	}


	//====================Validate Text Ignore Case==================//
	public void validateTextIgnoreCaseHard(String actualText, String expectedText, String message) {

		Assert.assertTrue(actualText != null && actualText.equalsIgnoreCase(expectedText),
				"Validation Failed: " + message +
				" | Expected (ignore case): '" + expectedText +
				"', Actual: '" + actualText + "'");
		log.info("Validation Passed: " + message +" | Actual: " + actualText +" | Text Matched (Ignore Case)");
		extentTest.log(Status.PASS,"Validation Passed: " + message +" | Actual: " + actualText +" | Text Matched (Ignore Case)");
	}


	//====================Validate Text Equals==================//
	public void validateTextEqualsHard(String actualText, String expectedText, String message) {

		Assert.assertEquals(actualText != null ? actualText.trim() : null,
				expectedText != null ? expectedText.trim() : null,
						"Validation Failed: " + message +
						" | Expected: '" + expectedText +
						"', Actual: '" + actualText + "'"
				);

		log.info("Validation Passed: " + message +
				" | Actual: " + actualText + " | Text Matched");

		extentTest.log(Status.PASS,
				"Validation Passed: " + message +
				" | Actual: " + actualText + " | Text Matched");
	}


	//====================Validate List Text Equals==================//
	public void validateTextEqualsHard(List<String> actualTexts, List<String> expectedTexts, String message) {

		// Null-safe normalization (trim each value)
		List<String> normalizedActual = actualTexts == null ? null :
			actualTexts.stream()
			.map(text -> text == null ? null : text.trim())
			.toList();

		List<String> normalizedExpected = expectedTexts == null ? null :
			expectedTexts.stream()
			.map(text -> text == null ? null : text.trim())
			.toList();

		Assert.assertEquals(
				normalizedActual,
				normalizedExpected,
				"Validation Failed: " + message +
				" | Expected: " + normalizedExpected +
				" | Actual: " + normalizedActual
				);

		log.info("Validation Passed: " + message +
				" | Expected: " + normalizedExpected +
				" | Actual: " + normalizedActual);

		extentTest.log(Status.PASS,
				"Validation Passed: " + message +
				" | Expected: " + normalizedExpected +
				" | Actual: " + normalizedActual);
	}

	// Overloaded version for varargs
	public void validateTextEqualsHard(List<String> actualTexts, String message, String... expectedTexts) {
		validateTextEqualsHard(actualTexts, Arrays.asList(expectedTexts), message);
	}

	//====================Validate List Text Equals Ignore Case==================//

	public void validateTextEqualsIgnoreCaseHard(List<String> actualTexts,List<String> expectedTexts,String message) {

		List<String> normalizedActual = actualTexts == null ? null :
			actualTexts.stream()
			.map(text -> text == null ? null : text.trim().toLowerCase())
			.toList();

		List<String> normalizedExpected = expectedTexts == null ? null :
			expectedTexts.stream()
			.map(text -> text == null ? null : text.trim().toLowerCase())
			.toList();

		Assert.assertEquals(normalizedActual,normalizedExpected,"Validation Failed: " + message +" | Expected: " + normalizedExpected +" | Actual: " + normalizedActual);
		log.info("Validation Passed: " + message +" | Expected: " + normalizedExpected +" | Actual: " + normalizedActual);
		extentTest.log(Status.PASS,"Validation Passed: " + message +" | Expected: " + normalizedExpected +" | Actual: " + normalizedActual);
	}

	// Overloaded version for Varargs
	public void validateTextEqualsIgnoreCaseHard(List<String> actualTexts,String message,String... expectedTexts) {
		validateTextEqualsIgnoreCaseHard(actualTexts,Arrays.asList(expectedTexts),message);
	}

	// ==================== validate All Elements Contain Keyword ==========================//
	public void validateAllElementsContainKeyword(List<WebElement> elements,String keyword,String message) {


		Assert.assertNotNull(elements, "Validation Failed: Element list is null!");
		Assert.assertFalse(elements.isEmpty(), "Validation Failed: Element list is empty!");
		Assert.assertNotNull(keyword, "Validation Failed: Keyword is null!");

		String normalizedKeyword = keyword.trim().toLowerCase();

		List<String> failedItems = elements.stream()
				.map(WebElement::getText)
				.map(text -> text != null ? text.trim().toLowerCase() : "")
				.filter(text -> !text.contains(normalizedKeyword))
				.toList();

		Assert.assertTrue(failedItems.isEmpty(),
				"Validation Failed: Some elements do not contain keyword '" +keyword + "'. Failed items: " + failedItems);
		log.info("Validation Passed: " + message +" | Total Elements: " + elements.size() +" | Keyword: " + keyword);
		extentTest.log(Status.PASS,message + " | All " + elements.size() +" elements contain keyword: " + keyword);}

	//====================Validate List Text Contains==================//

	public void validateTextsContainsHard(List<String> actualTexts,List<String> expectedTexts,String message) {

		Assert.assertNotNull(actualTexts, "Actual text list is null!");
		Assert.assertNotNull(expectedTexts, "Expected text list is null!");
		Assert.assertFalse(actualTexts.isEmpty(), "Actual text list is empty!");

		List<String> missingTexts = expectedTexts.stream()
				.filter(Objects::nonNull)
				.filter(expected -> actualTexts.stream()
						.filter(Objects::nonNull)
						.map(s -> s.trim().toLowerCase())
						.noneMatch(actual -> actual.contains(expected.trim().toLowerCase()))).toList();
		Assert.assertTrue(missingTexts.isEmpty(),"Validation Failed: Some expected texts not found. Missing: " + missingTexts +" | Actual List: " + actualTexts);
		log.info("Validation Passed: " + message + " | Expected: " + expectedTexts + " | Actual: " + actualTexts);
		extentTest.log(Status.PASS, message + " | All expected texts are present in actual list.");
	}
	// Overloaded version for Varargs
	public void validateTextsContainsHard(List<String> actualTexts,String message,String... expectedTexts) {
		validateTextsContainsHard(actualTexts, Arrays.asList(expectedTexts), message);
	}


	//====================Validate Alert Text==================//

	public void validateAlertText(String actualText,String expectedText,String message) {

		String normalizedActual = actualText == null ? null : actualText.trim();
		String normalizedExpected = expectedText == null ? null : expectedText.trim();

		Assert.assertTrue(normalizedActual != null &&normalizedActual.equalsIgnoreCase(normalizedExpected),"Validation Failed: " + message +" | Expected alert: '" + expectedText +"' | Actual alert: '" + actualText + "'");
		log.info("Validation Passed: " + message +" | Alert text: " + actualText);
		extentTest.log(Status.PASS,"Validation Passed: " + message +" | Alert text: " + actualText);
	}

	//================ Validate Attribute Equals =================//

	public void validateAttributeEqualsHard(WebElement element,String attributeName,String expectedValue,String message) {

		Assert.assertNotNull(element, "Validation Failed: WebElement is null!");
		Assert.assertNotNull(attributeName, "Validation Failed: Attribute name is null!");

		String actualValue = element.getAttribute(attributeName);
		String normalizedActual = actualValue == null ? null : actualValue.trim();
		String normalizedExpected = expectedValue == null ? null : expectedValue.trim();

		Assert.assertEquals(normalizedActual,	normalizedExpected,"Validation Failed: " + message +" | Attribute: '" + attributeName +"' | Expected: '" + expectedValue +"' | Actual: '" + actualValue + "'");
		log.info("Validation Passed: " + message +" | Attribute: " + attributeName +" | Value: " + actualValue);
		extentTest.log(Status.PASS,"Validation Passed: " + message +" | Attribute: " + attributeName +" | Value: " + actualValue);
	}

	//====================Validate Element Present==================//
	public void validateElementPresent(WebElement element, String message) {

		Assert.assertNotNull(element, "Validation Failed: " + message + " | Element reference is null!");
		Assert.assertTrue(element.isDisplayed(),"Validation Failed: " + message + " | Element is not displayed!");
		log.info("Validation Passed: " + message + " | Element is present and displayed");
		extentTest.log(Status.PASS,"Validation Passed: " + message + " | Element is present and displayed");
	}

	//====================Validate Element Not Present==================//

	public void validateElementNotPresentHard(WebElement element,String elementName,String message) {

		Assert.assertFalse(element.isDisplayed(),"Validation Failed: " + message +" | Element is visible: " + elementName);
		log.info("Validation Passed: " + message +" | Element not visible: " + elementName);
		extentTest.pass("Validation Passed: " + message +" | Element not visible: " + elementName);
	}

	//====================Validate Element Visible==================//
	public void validateElementVisibleHard(WebElement element,String elementName,String message) {

		Assert.assertTrue(element.isDisplayed(),"Validation Failed: " + message +" | Element not visible: " + elementName);
		log.info("Validation Passed: " + message +" | Element visible: " + elementName);
		extentTest.pass("Validation Passed: " + message +" | Element visible: " + elementName);
	}

	//====================Validate Element Not Visible==================//

	public void validateElementNotVisibleHard(WebElement element,String elementName,String message) {

		Assert.assertFalse(element.isDisplayed(),"Validation Failed: " + message +" | Element is visible: " + elementName);
		log.info("Validation Passed: " + message +" | Element not visible: " + elementName);
		extentTest.pass("Validation Passed: " + message +" | Element not visible: " + elementName);
	}

	//====================Validate Element Enabled==================//

	public void validateElementEnabled(WebElement element,String elementName,String message) {

		Assert.assertTrue(element.isEnabled(),"Validation Failed: " + message +" | Element not enabled: " + elementName);
		log.info("Validation Passed: " + message +" | Element enabled: " + elementName);
		extentTest.pass("Validation Passed: " + message +" | Element enabled: " + elementName);}

	//====================Validate Element Disabled==================//

	public void validateElementDisabled(WebElement element,String elementName, String message) {

		Assert.assertFalse(element.isEnabled(),"Validation Failed: " + message +" | Element is enabled: " + elementName);
		log.info("Validation Passed: " + message +" | Element disabled: " + elementName);
		extentTest.pass("Validation Passed: " + message +" | Element disabled: " + elementName);}


	//====================Validate URL Equals==================//

	public void validateUrlEquals(String actualUrl, String expectedUrl,String message) {

		Assert.assertEquals(actualUrl,expectedUrl,"Validation Failed: " + message +" | Expected URL: '" + expectedUrl +"', Actual URL: '" + actualUrl + "'");
		log.info("Validation Passed: " + message + " | URL matches.");
		extentTest.pass("Validation Passed: " + message + " | URL matches.");
	}

	//====================Validate URL Contains==================//

	public void validateUrlContains(String actualUrl, String expectedText,String message) {

		Assert.assertTrue(actualUrl.contains(expectedText),"Validation Failed: " + message +" | Expected URL to contain: '" + expectedText +"', Actual URL: '" + actualUrl + "'");
		log.info("Validation Passed: " + message +" | URL contains expected text.");
		extentTest.pass("Validation Passed: " + message +" | URL contains expected text.");
	}


	//====================Validate Title Equals==================//

	public void validateTitleEquals(String actualTitle, String expectedTitle, String message) {

		Assert.assertEquals(actualTitle,expectedTitle,"Validation Failed: " + message +" | Expected Title: '" + expectedTitle +"', Actual Title: '" + actualTitle + "'");
		log.info("Validation Passed: " + message + " | Title matches.");
		extentTest.pass("Validation Passed: " + message + " | Title matches.");
	}

	//====================Validate Title Contains==================//

	public void validateTitleContains(String actualTitle,String expectedTitle,String message) {

		Assert.assertTrue(actualTitle != null && actualTitle.contains(expectedTitle),"Validation Failed: " + message +" | Expected Title to contain: '" + expectedTitle +"', Actual Title: '" + actualTitle + "'");
		log.info("Validation Passed: " + message +" | Title contains expected text.");
		extentTest.pass("Validation Passed: " + message +" | Title contains expected text.");
	}


	//====================Validate Text Equals (Soft Assert)==================//

	public void validateTextEqualsSoft(String actualText,String expectedText, String message) {

		softAssert.assertEquals(actualText != null ? actualText.trim() : null,expectedText != null ? expectedText.trim() : null,"Validation Failed: " + message +" | Expected: '" + expectedText +"', Actual: '" + actualText + "'");
		log.info("Validation Executed (Soft): " + message +" | Expected: '" + expectedText +"', Actual: '" + actualText + "'");
		extentTest.info("Validation Executed (Soft): " + message +" | Expected: '" + expectedText +"', Actual: '" + actualText + "'");
	}


	//====================Validate Text Ignore Case==================//

	public void validateTextIgnoreCaseSoft(String actualText,String expectedText, String message) {

		boolean isMatch = actualText != null && expectedText != null && actualText.equalsIgnoreCase(expectedText);
		softAssert.assertTrue(isMatch,"Validation Failed: " + message +" | Expected: '" + expectedText + "', Actual: '" + actualText + "'");
		log.info("Validation Executed: " + message +" | Expected: '" + expectedText +"', Actual: '" + actualText +"' | Matched (Ignore Case): " + isMatch);
		extentTest.log(isMatch ? Status.PASS : Status.FAIL,"Validation Executed: " + message +" | Expected: '" + expectedText +"', Actual: '" + actualText +"' | Matched (Ignore Case): " + isMatch);
	}
	//====================Validate Text Contains==================//

	public void validateTextContainsSoft(String actualText,String expectedText, String message) {

		boolean isMatch = actualText != null &&expectedText != null &&actualText.contains(expectedText);

		// Soft assertion
		softAssert.assertTrue(isMatch,"Validation Failed: " + message +" | Expected to contain: '" + expectedText + "', Actual: '" + actualText + "'");
		String logMsg = message +" | Expected to contain: '" + expectedText +"', Actual: '" + actualText +"' | Matched: " + isMatch;
		log.info("Validation Executed: " + logMsg);
		extentTest.log(isMatch ? Status.PASS : Status.FAIL, "Validation Executed: " + logMsg);
	}

	//====================Validate List Text Equals==================//

	public void validateTextEqualsSoft(List<String> actualTexts,List<String> expectedTexts) {

		boolean isMatch = actualTexts != null && expectedTexts != null && actualTexts.equals(expectedTexts);

		// Soft assertion
		softAssert.assertEquals(actualTexts,expectedTexts,"Text validation failed (Exact Match)");
		String logMsg = "Text validation executed (Exact Match) | Expected: " + expectedTexts +" | Actual: " + actualTexts +" | Matched: " + isMatch;
		log.info(logMsg);
		extentTest.log(isMatch ? Status.PASS : Status.FAIL, logMsg);
	}

	// Overloaded version for varargs (Soft Assert)
	public void validateTextEqualsSoft(List<String> actualTexts, String... expectedTexts) {
		validateTextEqualsSoft(actualTexts, Arrays.asList(expectedTexts));
	}

	//====================Validate List Text Equals Ignore Case==================//

	public void validateTextEqualsIgnoreCaseSoft(List<String> actualTexts, List<String> expectedTexts) {

		List<String> actualLower = actualTexts != null? actualTexts.stream().map(s -> s != null ? s.toLowerCase() : null).toList(): List.of();
		List<String> expectedLower = expectedTexts != null? expectedTexts.stream().map(s -> s != null ? s.toLowerCase() : null).toList(): List.of();

		// Soft assertion
		softAssert.assertEquals(actualLower,expectedLower,"Text lists do not match (Ignore Case)");
		boolean isMatch = actualLower.equals(expectedLower);
		String logMsg = "Text validation executed (Ignore Case) | Expected: " +expectedTexts + " | Actual: " + actualTexts +" | Matched: " + isMatch;
		log.info(logMsg);
		extentTest.log(isMatch ? Status.PASS : Status.FAIL, logMsg);
	}

	// Overloaded version for varargs (Soft Assert)
	public void validateTextEqualsIgnoreCaseSoft(List<String> actualTexts, String... expectedTexts) {
		validateTextEqualsIgnoreCaseSoft(actualTexts, Arrays.asList(expectedTexts));
	}


	//====================Validate List Text Contains==================//

	public void validateTextContainsSoft(List<String> actualTexts, List<String> expectedTexts) {

		List<String> actual = actualTexts != null ? actualTexts : List.of();
		List<String> expected = expectedTexts != null ? expectedTexts : List.of();
		boolean allFound = expected.stream().allMatch(actual::contains);
		softAssert.assertTrue(allFound,"Text validation failed (Contains Check) | Expected to contain: " +expected + " | Actual: " + actual);
		String logMsg = "Text validation executed (Contains Check) | Expected to contain: " +expected + " | Actual: " + actual + " | Matched: " + allFound;
		log.info(logMsg);
		extentTest.log(allFound ? Status.PASS : Status.FAIL, logMsg);
	}

	// Overloaded version for varargs (Soft Assert)
	public void validateTextContainsSoft(List<String> actualTexts, String... expectedTexts) {
		validateTextContainsSoft(actualTexts, Arrays.asList(expectedTexts));
	}


	//====================Validate Element Visible==================//

	public void validateElementVisibleSoft(WebElement element, String elementName, String message) {

		boolean isVisible = element != null && element.isDisplayed();
		softAssert.assertTrue(isVisible,"Element not visible: " + elementName + " | " + message);
		String logMsg = message + " | Element: " + elementName + " | Visible: " + isVisible;
		log.info("Validation Executed: " + logMsg);
		extentTest.log(isVisible ? Status.PASS : Status.FAIL, "Validation Executed: " + logMsg);
	}

	//====================Validate Element Not Visible==================//

	public void validateElementNotVisibleSoft(WebElement element, String elementName, String message) {

		boolean isVisible = element != null && element.isDisplayed();
		softAssert.assertFalse(isVisible,"Element should not be visible: " + elementName + " | " + message);
		String logMsg = message + " | Element: " + elementName + " | Visible: " + isVisible;
		log.info("Validation Executed: " + logMsg);
		extentTest.log(isVisible ? Status.FAIL : Status.PASS, "Validation Executed: " + logMsg);
	}
	//====================Validate Element Not Present==================//

	public void validateElementNotPresentSoft(WebElement element, String elementName,String message) {

		boolean isPresent = element != null && element.isDisplayed();
		softAssert.assertFalse(isPresent,"Element should not be present: " + elementName + " | " + message);
		String logMsg = message + " | Element: " + elementName + " | Present: " + isPresent;
		log.info("Validation Executed: " + logMsg);
		extentTest.log(isPresent ? Status.FAIL : Status.PASS, "Validation Executed: " + logMsg);
	}

	//===================== All Waits Method ==========================//

	//====================Wait for Element Visibility==================//

	public WebElement waitForVisibility(WebElement element, int seconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
			wait.until(ExpectedConditions.visibilityOf(element));
			extentTest.info("Element is visible.");
			return element;
		} catch (Exception e) {
			extentTest.fail("Element not visible within " + seconds + " seconds.");
			throw e;
		}
	}

	//====================Wait for Element Clickable==================//

	public WebElement waitForClickable(WebElement element, int seconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			extentTest.info("Element is clickable.");
			return element;
		} catch (Exception e) {
			extentTest.fail("Element not clickable within " + seconds + " seconds.");
			throw e;
		}
	}



	//====================Wait for URL to Contain Text==================//

	public boolean waitForUrlContains(String fraction, int seconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
			boolean result = wait.until(ExpectedConditions.urlContains(fraction));
			extentTest.info(" URL contains expected text: " + fraction);
			return result;
		} catch (Exception e) {
			extentTest.fail(" URL did not contain expected text within " + seconds + " seconds: " + fraction);
			throw e;
		}
	}



	//====================Wait for Title to Contain Text==================//

	public void waitForTitleContains(String titlePart, int seconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
			wait.until(ExpectedConditions.titleContains(titlePart));
			extentTest.info("Page title contains expected text: " + titlePart);

		} catch (TimeoutException e) {
			String errorMsg = "Page title did not contain '" + titlePart +
					"' within " + seconds + " seconds";
			extentTest.fail(errorMsg);
			throw new TimeoutException(errorMsg, e);
		}
	}



	//====================Fluent Wait for Element Visibility==================//

	public WebElement fluentWaitForVisibility(WebElement element, int timeoutSeconds, int pollingSeconds) {
		try {
			getFluentWait(timeoutSeconds, pollingSeconds)
			.until(ExpectedConditions.visibilityOf(element));
			extentTest.info(" Fluent wait successful for element visibility.");
			return element;
		} catch (Exception e) {
			extentTest.fail("Fluent wait failed for element visibility.");
			throw e;
		}
	}



	//====================Generic FluentWait==================//

	private FluentWait<WebDriver> getFluentWait(int timeoutSeconds, int pollingSeconds) {
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofSeconds(pollingSeconds))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}


	//====================Wait for Page to Load Completely==================//

	public void waitForPageToLoad(int timeoutSeconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			wait.until(webDriver ->
			((String) ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState")).equals("complete"));
			extentTest.info(" Page loaded completely.");
		} catch (Exception e) {
			extentTest.fail("Page did not load completely within " + timeoutSeconds + " seconds.");
			throw e;
		}
	}



}
