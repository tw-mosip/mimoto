package utils;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class BaseTest {
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver driver;

    public JavascriptExecutor jse;

    public static final String AUTOMATE_USERNAME = "anupnehe_w1PZQx";
    public static final String AUTOMATE_KEY = "Zenzg8a3RikxvTUmELFm";
    public static final String URL = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @Before
    public void beforeAll() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
        browserstackOptions.put("os", "Windows");
        browserstackOptions.put("osVersion", "10");
        browserstackOptions.put("projectName", "Bstack-[Java] Sample file download");
        capabilities.setCapability("bstack:options", browserstackOptions);

         driver = new RemoteWebDriver(new URL(URL), capabilities);
         jse = (JavascriptExecutor) driver;
        driver.manage().window().maximize();
        driver.get("https://inji.qa-inji.mosip.net/");
    }

    @After
    public void afterAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public JavascriptExecutor getJse() {
        return jse;
    }
}