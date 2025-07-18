package Base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import utils.ExtentManager;

import java.time.Duration;

public class BaseTest {
    public WebDriver driver;
    protected static ExtentReports extent;
    protected static ExtentTest test;


    @BeforeClass
    public void setup()
    {
        extent = ExtentManager.getInstance();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        driver.manage().window().maximize();
       // driver.get(si);
    }



    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            extent.flush();
        }
    }
}
