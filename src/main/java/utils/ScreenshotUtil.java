package utils;

import com.aventstack.extentreports.ExtentTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotUtil {

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            // Create screenshots directory if it doesn't exist
            String screenshotDir = System.getProperty("user.dir") + "/test-output/screenshots/";
            new File(screenshotDir).mkdirs();
            
            String fileName = screenshotName + "_" + System.currentTimeMillis() + ".png";
            String dest = screenshotDir + fileName;
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);
            
            // Return relative path for ExtentReports (relative to test-output directory)
            return "screenshots/" + fileName;
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Takes a screenshot and attaches it to the ExtentTest report
     * @param driver WebDriver instance
     * @param test ExtentTest instance to attach screenshot to
     * @param screenshotName Name for the screenshot
     * @return Screenshot path if successful, null otherwise
     */
    public static String takeScreenshotAndAttachToReport(WebDriver driver, ExtentTest test, String screenshotName) {
        try {
            String screenshotPath = takeScreenshot(driver, screenshotName);
            if (screenshotPath != null && test != null) {
                test.addScreenCaptureFromPath(screenshotPath, screenshotName);
                System.out.println("📸 Screenshot captured and attached to report: " + screenshotName);
                return screenshotPath;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to capture screenshot: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Takes a screenshot with additional logging information and attaches it to the ExtentTest report
     * @param driver WebDriver instance
     * @param test ExtentTest instance to attach screenshot to
     * @param screenshotName Name for the screenshot
     * @param logMessage Additional message to log with the screenshot
     * @return Screenshot path if successful, null otherwise
     */
    public static String takeScreenshotAndAttachToReportWithLog(WebDriver driver, ExtentTest test, String screenshotName, String logMessage) {
        try {
            String screenshotPath = takeScreenshot(driver, screenshotName);
            if (screenshotPath != null && test != null) {
                test.addScreenCaptureFromPath(screenshotPath, screenshotName);
                if (logMessage != null && !logMessage.isEmpty()) {
                    test.info("📸 " + logMessage);
                }
                System.out.println("📸 Screenshot captured and attached to report: " + screenshotName);
                return screenshotPath;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to capture screenshot: " + e.getMessage());
            if (test != null) {
                test.warning("Failed to capture screenshot: " + e.getMessage());
            }
        }
        return null;
    }
} 