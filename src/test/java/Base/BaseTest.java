package Base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ExtentManager;
import java.time.Duration;
import java.lang.reflect.Method;

public class BaseTest {
    public WebDriver driver;
    protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void beforeSuite() {
        extent = ExtentManager.getInstance();
        System.out.println("📊 ExtentReports initialized");
        System.out.println("📁 Report will be saved at: " + ExtentManager.getReportPath());
    }

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        driver.manage().window().maximize();
        System.out.println("🚀 Browser setup completed");
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        String testName = method.getName();
        String className = this.getClass().getSimpleName();
        test = extent.createTest(className + " - " + testName);
        test.info("🧪 Starting test: " + testName);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());
            test.fail("Test failed with exception: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "⏭️ Test Skipped: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "✅ Test Passed Successfully");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🔚 Browser closed");
        }
    }

    @AfterSuite
    public void afterSuite() {
        if (extent != null) {
            extent.flush();
            System.out.println("📋 ExtentReports flushed");
            System.out.println("📄 Report saved at: " + ExtentManager.getReportPath());
        }
    }

    // Helper methods for logging in tests
    public void logInfo(String message) {
        if (test != null) {
            test.info(message);
        }
        System.out.println(message);
    }

    public void logPass(String message) {
        if (test != null) {
            test.pass(message);
        }
        System.out.println("✅ " + message);
    }

    public void logFail(String message) {
        if (test != null) {
            test.fail(message);
        }
        System.out.println("❌ " + message);
    }

    public void logWarning(String message) {
        if (test != null) {
            test.warning(message);
        }
        System.out.println("⚠️ " + message);
    }
}
