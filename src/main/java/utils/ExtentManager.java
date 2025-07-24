package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {

    private static ExtentReports extent;
    private static String reportPath;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    public static ExtentReports createInstance() {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Configure the HTML report with enhanced settings
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("🛍️ Unbxd E-commerce Test Automation Report");
        sparkReporter.config().setReportName("🔍 Product Search & Validation Results");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        sparkReporter.config().setEncoding("utf-8");
        
        // Add custom CSS for better appearance
        sparkReporter.config().setCss(
            ".test-item { margin-bottom: 10px; } " +
            ".info { color: #17a2b8; } " +
            ".pass { color: #28a745; } " +
            ".fail { color: #dc3545; } " +
            ".warning { color: #ffc107; }"
        );
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // Set comprehensive system information
        extent.setSystemInfo("🖥️ Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        extent.setSystemInfo("☕ Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("👤 User", System.getProperty("user.name"));
        extent.setSystemInfo("🌐 Browser", "Google Chrome (Latest)");
        extent.setSystemInfo("🔧 Selenium Version", "4.19.1");
        extent.setSystemInfo("📱 Test Framework", "TestNG + REST Assured");
        extent.setSystemInfo("🏷️ Environment", "QA Test Environment");
        extent.setSystemInfo("📊 Report Generated", new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm:ss").format(new Date()));
        extent.setSystemInfo("🎯 Test Scope", "API + UI Validation");
        extent.setSystemInfo("🔍 Test Categories", "Product Search, Autosuggest, Image Validation");
        
        return extent;
    }

    public static String getReportPath() {
        return reportPath;
    }
}
