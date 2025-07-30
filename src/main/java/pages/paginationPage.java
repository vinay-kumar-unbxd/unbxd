package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class paginationPage {
    
    private final WebDriver driver;
    
    public paginationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    @FindAll({
        @FindBy(xpath = "//a[text()='Next'] | //button[text()='Next']"),
        @FindBy(xpath = "//li[contains(@class,'next')]/a"),
        @FindBy(xpath = "//a[@rel='next']"),
        @FindBy(xpath = "//button[@aria-label='Next']"),
        @FindBy(xpath = "//img[contains(@alt, 'right_arrow')]/parent::a"),
        @FindBy(xpath = "//img[contains(@alt, 'right_arrow')]/parent::button"),
        @FindBy(xpath = "//img[contains(@src, 'rightPagination-arrow')]/parent::*")
    })
    public List<WebElement> nextButtons;

    @FindAll({
        @FindBy(xpath = "//a[text()='Prev'] | //button[text()='Prev']"),
        @FindBy(xpath = "//li[contains(@class,'prev')]/a"),
        @FindBy(xpath = "//a[@rel='prev']"),
        @FindBy(xpath = "//img[contains(@alt, 'left_arrow')]/parent::a"),
        @FindBy(xpath = "//img[contains(@alt, 'left_arrow')]/parent::button"),
        @FindBy(xpath = "//img[contains(@src, 'leftPagination-arrow')]/parent::*")
    })
    public List<WebElement> prevButtons;
    
    public void clickNextButton() {
        for (WebElement btn : nextButtons) {
            if (btn.isDisplayed() && btn.isEnabled()) {
                btn.click(); // or JS click if needed
                break;
            }
        }
    }

    public void clickPreviousButton() {
        for (WebElement btn : prevButtons) {
            if (btn.isDisplayed() && btn.isEnabled()) {
                btn.click(); // or JS click if needed
                break;
            }
        }
    }
}
