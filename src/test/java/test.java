import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class test {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("file:///C:/Users/unbxd/Desktop/vinay.html");
        driver.findElement(By.xpath("//input[@type='TEXT']")).sendKeys("hsfg");
    }
}
