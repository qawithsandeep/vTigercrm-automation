package com.saroz.vtiger.utilities;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	    protected static ExtentReports reports;
	    protected static ExtentSparkReporter extentReporter;
	    private static final Logger log = LogManager.getLogger(ExtentManager.class);
	 
	    public static ExtentReports setupExtentReports() {
	        if (reports == null) {
	        	String reportPath = Paths.get(System.getProperty("user.dir"), "Reports", "ExtentReports", "ExtentReport.html")
                        .toString();
	            ExtentSparkReporter extentReporter = new ExtentSparkReporter(reportPath);
	            reports = new ExtentReports();
	            reports.attachReporter(extentReporter);

	            // Report configuration
	            extentReporter.config().setTheme(Theme.STANDARD);
	            extentReporter.config().setDocumentTitle("Hotstar Automation Report");
	            extentReporter.config().setReportName("Automation Results");

	            // System information for report
	            reports.setSystemInfo("OS", System.getProperty("os.name"));
	            reports.setSystemInfo("Environment", "QA");
	        }
	        log.info("Extent Reports setup completed.");
	        return reports;
	    }


	    public static void flushExtentReports() {
	        if (reports != null) {
	            reports.flush();
	            log.info("Extent Reports flushed successfully.");
	        }
	    }
}