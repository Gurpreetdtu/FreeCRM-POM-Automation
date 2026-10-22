
package com.crm.qa.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
		try {
			prop = new Properties();

			String configPath = System.getProperty("user.dir")
					+ "/src/main/java/com/crm/qa/config/config.properties";

			FileInputStream ip = new FileInputStream(configPath);
			prop.load(ip);
			ip.close();

		} catch (FileNotFoundException e) {
			throw new RuntimeException("config.properties file not found.", e);
		} catch (IOException e) {
			throw new RuntimeException("Unable to load config.properties.", e);
		}
	}

	public static void initialization() {

		String browserName = prop.getProperty("browser");

		if (browserName == null || browserName.trim().isEmpty()) {
			throw new RuntimeException(
					"Browser is not configured in config.properties");
		}

		if (browserName.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();

		} else if (browserName.equalsIgnoreCase("FF")
				|| browserName.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();

		} else {
			throw new RuntimeException(
					"Unsupported browser: " + browserName);
		}

		e_driver = new EventFiringWebDriver(driver);
		eventListener = new WebEventListener();
		e_driver.register(eventListener);
		driver = e_driver;

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();

		driver.manage().timeouts().pageLoadTimeout(
				TestUtil.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);

		driver.manage().timeouts().implicitlyWait(
				TestUtil.IMPLICIT_WAIT, TimeUnit.SECONDS);

		driver.get(prop.getProperty("url"));
	}
}
