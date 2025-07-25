package utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ActionUtils {

    /**
     * Press Enter key using Actions
     * @param driver WebDriver instance
     */
    public static void pressEnterWithActions(WebDriver driver) {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ENTER).build().perform();
    }
    
    /**
     * Press any key using Actions
     * @param driver WebDriver instance
     * @param key Keys to press
     */
    public static void pressKeyWithActions(WebDriver driver, Keys key) {
        Actions actions = new Actions(driver);
        actions.sendKeys(key).build().perform();
    }
    
    /**
     * Click on element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to click
     */
    public static void clickWithActions(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.click(element).build().perform();
    }
    
    /**
     * Double click on element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to double click
     */
    public static void doubleClickWithActions(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.doubleClick(element).build().perform();
    }
    
    /**
     * Right click on element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to right click
     */
    public static void rightClickWithActions(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.contextClick(element).build().perform();
    }
    
    /**
     * Hover over element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to hover over
     */
    public static void hoverWithActions(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }
    
    /**
     * Drag and drop from source to target element
     * @param driver WebDriver instance
     * @param sourceElement Source element to drag
     * @param targetElement Target element to drop
     */
    public static void dragAndDropWithActions(WebDriver driver, WebElement sourceElement, WebElement targetElement) {
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceElement, targetElement).build().perform();
    }
    
    /**
     * Send text to element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to send text to
     * @param text Text to send
     */
    public static void sendTextWithActions(WebDriver driver, WebElement element, String text) {
        Actions actions = new Actions(driver);
        actions.click(element).sendKeys(text).build().perform();
    }
    
    /**
     * Clear text field and enter new text using Actions
     * @param driver WebDriver instance
     * @param element WebElement text field
     * @param text New text to enter
     */
    public static void clearAndSendTextWithActions(WebDriver driver, WebElement element, String text) {
        Actions actions = new Actions(driver);
        actions.click(element)
               .keyDown(Keys.CONTROL)
               .sendKeys("a")
               .keyUp(Keys.CONTROL)
               .sendKeys(text)
               .build()
               .perform();
    }
    
    /**
     * Scroll to element using Actions
     * @param driver WebDriver instance
     * @param element WebElement to scroll to
     */
    public static void scrollToElementWithActions(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.scrollToElement(element).build().perform();
    }
} 