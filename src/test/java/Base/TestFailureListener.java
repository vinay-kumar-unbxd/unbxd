package Base;

import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtil;

public class TestFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();
        
        if (testInstance instanceof BaseTest) {
            BaseTest baseTest = (BaseTest) testInstance;
            
            try {
                String testName = result.getMethod().getMethodName();
                String className = result.getTestClass().getName();
                String screenshotName = className + "_" + testName + "_FAILED";
                
                // Take screenshot and attach to report using existing ScreenshotUtil method
                if (baseTest.getExtentTest() != null) {
                    ScreenshotUtil.takeScreenshotAndAttachToReportWithLog(
                        baseTest.getDriver(), 
                        baseTest.getExtentTest(), 
                        screenshotName, 
                        "❌ Test Failed - Screenshot captured for debugging"
                    );
                    System.out.println("✅ Screenshot captured and attached to report on test failure");
                } else {
                    // Fallback: just take screenshot without ExtentReports attachment
                    String screenshotPath = ScreenshotUtil.takeScreenshot(baseTest.getDriver(), screenshotName);
                    if (screenshotPath != null) {
                        System.out.println("✅ Screenshot captured on test failure: " + screenshotPath);
                    } else {
                        System.out.println("❌ Failed to capture screenshot on test failure");
                    }
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error in screenshot capture: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("🚀 Test Started: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test Passed: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test Skipped: " + result.getMethod().getMethodName());
    }
} 