package base;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BasePage {

    public void clickOnElement(WebDriver driver, By locator) {
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        element.click();
    }



//	public void clickOnElement(WebDriver driver, WebElement ele) {
//
//		Actions act = new Actions(driver);
//		act.moveToElement(ele).click().build().perform();
//
//	}


//	public Boolean isElementIsVisible(WebDriver driver, By locator) {
//		try {
//			WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(locator));
//			return element.isDisplayed();
//		} catch (NoSuchElementException | TimeoutException e) {
//			// Handle element not found or timeout exceptions here (e.g., log the error)
//			return false;
//		}
//	}

	public static boolean isElementIsVisible(WebDriver driver ,By by) {
		try {
			(new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.visibilityOfElementLocated(by));
			return driver.findElement(by).isDisplayed();
		}catch(Exception e) {
			return false;
		}
	}


	public void enterText(WebDriver driver, By locator, String text) {
		WebElement element = new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.presenceOfElementLocated(locator));
		element.clear();
		element.sendKeys(text);
	}


	public String getElementText(WebDriver driver, By locator) {
		WebElement element = new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.presenceOfElementLocated(locator));
		return element.getText();
	}


	public List<String> getElementTexts(WebDriver driver, By locator) throws TimeoutException {
		List<String> textContents = new ArrayList<>();
		List<WebElement> elements = new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
		for (WebElement element : elements) {
			textContents.add(element.getText());
		}
		return textContents;
	}




}
