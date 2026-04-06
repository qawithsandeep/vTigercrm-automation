package com.saroz.vtiger.listeners;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.saroz.vtiger.base.BaseTest;

//Custom listener to manage test execution events and integrate with Extent Reports and Log4j.
 
public class TestListener extends BaseTest implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        // Logs the start of the entire test suite execution
        log.info("=== Test Suite Execution Started: " + context.getName() + " ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Logs the beginning of each individual test method
        log.info("--- Starting Test: " + result.getMethod().getMethodName() + " ---");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Records a PASS status in Extent Report and logs success to the console
        extentTest.get().log(Status.PASS, "Test PASSED: " + result.getMethod().getMethodName());
        log.info("Test Success: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Records a FAIL status with the exception details and logs the error
        extentTest.get().log(Status.FAIL, "Test FAILED: " + result.getMethod().getMethodName());
        extentTest.get().log(Status.FAIL, result.getThrowable()); 
        log.error("Test Failed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Records a SKIP status in the report for tests that are not executed
        extentTest.get().log(Status.SKIP, "Test Case SKIPPED: " + result.getMethod().getMethodName());
        log.warn("Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Logs the completion of the entire test suite execution
        log.info("=== Test Suite Execution Finished: " + context.getName() + " ===");
    }
}