package com.saroz.vtiger.base;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.ExtentTest;
import com.saroz.vtiger.utilities.ExcelUtil;
import com.saroz.vtiger.utilities.WebUtils;

public class BasePage {
	protected WebDriver driver;
	protected WebUtils utils;
	protected ExtentTest extentTest;
	protected ExcelUtil excel;


	public BasePage(WebDriver driver, ExtentTest extentTest) {

		// Assign WebDriver instance
		this.driver = driver;

		// Assign ExtentTest instance for reporting
		this.extentTest = extentTest;

		// Initialize PageFactory elements
		PageFactory.initElements(driver, this);

		// Initialize WebUtils for Selenium actions
		this.utils = new WebUtils(driver, extentTest);
		
		// Initialize ExcelUtils For Read Excel Data
		this.excel = new ExcelUtil(System.getProperty("user.dir") + "/src/test/resources/TestData/Credentials.xlsx","LoginTestData");

	}
	public void pressKey(WebElement element, Keys key, int timeout,String elementName) {
		utils.pressKey( element, key, timeout, elementName);
	}
	// Clicks on the specified web element
	protected void click(WebElement element, String elementName) {
		utils.click(element, elementName);
	}

	// Types the given value into the specified web element
	protected void type(WebElement element, String value, String elementName) {
		utils.sendKeys(element, value, elementName);
	}

	// Retrieves the visible text from the specified web element
	protected String getText(WebElement element, String elementName) {
		return utils.getText(element, elementName);
	}

	// Retrieves the value of a specified attribute from the web element
	protected String getAttribute(WebElement element, String attributeName, String elementName) {
		return	utils.getAttribute(element, attributeName, elementName);
	}

	// Checks whether the specified web element is displayed
	protected boolean isDisplayed(WebElement element, String elementName) {
		return	utils.isDisplayed(element, elementName);
	}

	// Checks whether the specified web element is enabled
	protected boolean isEnabled(WebElement element, String elementName) {
		return	utils.isEnabled(element, elementName);
	}

	// Checks whether the specified web element is selected
	protected boolean isSelected(WebElement element, String elementName) {
		return	utils.isSelected(element, elementName);
	}

	// Selects a dropdown option by visible text
	protected void selectByVisibleText(WebElement element, String text, String elementName) {
		utils.selectByVisibleText(element, text, elementName);
	}

	// Selects a dropdown option by value attribute
	protected void selectByValue(WebElement element, String value, String elementName) {
		utils.selectByValue(element, value, elementName);
	}

	// Selects a dropdown option by index
	protected void selectByIndex(WebElement element, int index, String elementName){
		utils.selectByIndex(element, index, elementName);
	}

	// Retrieves the currently selected dropdown option text
	protected String getSelectedDropdownText(WebElement element, String elementName) {
		return	utils.getSelectedDropdownOption(element, elementName);
	}

	// Scrolls the page until the specified web element is in view
	protected void scrollToElement(WebElement element, String elementName) {
		utils.scrollToElementByJs(element, elementName);
	}

}
