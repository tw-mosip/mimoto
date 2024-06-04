package pages;

import com.microsoft.playwright.Page;

import base.BasePage;

public class HomePage extends BasePage {
	
	public HomePage(Page page) {
		super(page);
	}
	
	public void clickOnHelp() {
		clickOnElement("//a[text()='Help']");
	}
	
	public Boolean isLogoDisplayed() {
		return isElementIsVisible("//img[@src='/static/media/inji-logo.3eee14d8592e46b14318.png']"); 
	}
	
	public Boolean isTextWordIsDisplayed() {
		return isElementIsVisible("//a[text()='Help']");
	}
}
