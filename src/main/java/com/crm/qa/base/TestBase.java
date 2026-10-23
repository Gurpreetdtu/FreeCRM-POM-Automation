
		package com.crm.qa.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import com.crm.qa.util.TestUtil;
import com.crm.qa.util.WebEventListener;

public class TestBase {

	public static WebDriver driver;
	public static Properties prop;
	public static EventFiringWebDriver e_driver;
	public static WebEventListener eventListener;

	public TestBase() {
		prop = new Properties();

		String configPath = System.getProperty("user.dir")
				+ "/src/main/java/com/crm/qa/config/config.properties";

		try (FileInputStream ip = new FileInputStream(configPath)) {
			prop.load(ip);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"config.properties not found: " + configPath, e);
		} catch (IOException e) {
			throw new RuntimeException(
					"Unable to load config.properties.", e);
		}
	}

	public static void initialization() {

		String browserName = prop.getProperty("browser");

		if (browserName == null || browserName.trim().isEmpty()) {
			throw new RuntimeException(
					"Browser is not configured in config.properties");
		}

		if (browserName.equalsIgnoreCase("chrome")) {

			ChromeOptions options = new ChromeOptions();

			if ("true".equalsIgnoreCase(System.getenv("CI"))) {
				options.addArguments(
						"--headless=new",
						"--no-sandbox",
						"--disable-dev-shm-usage",
						"--window-size=1920,1080"
				);
			}

			driver = new ChromeDriver(options);

		} else if (browserName.equalsIgnoreCase("firefox")
				|| browserName.equalsIgnoreCase("FF")) {

			driver = new FirefoxDriver();

		} else {
			throw new RuntimeException(
					"Unsupported browser: " + browserName);
		}

		e_driver = new EventFiringWebDriver(driver);
		eventListener = new WebEventListener();
		e_driver.register(eventListener);
		driver = e_driver;

		driver.manage().deleteAllCookies();

		driver.manage().timeouts().pageLoadTimeout(
				Duration.ofSeconds(TestUtil.PAGE_LOAD_TIMEOUT));

		driver.manage().timeouts().implicitlyWait(
				Duration.ofSeconds(TestUtil.IMPLICIT_WAIT));

		String url = prop.getProperty("url");

		if (url == null || url.trim().isEmpty()) {
			throw new RuntimeException(
					"Application URL is missing in config.properties");
		}

		driver.get(url);
		driver.manage().window().maximize();
	}
}
