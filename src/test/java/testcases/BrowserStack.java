package testcases;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import scraper.WebScraping;

import java.net.URL;

public class BrowserStack {

    // BrowserStack credentials
    public static final String USERNAME = "archanaa_Qmoa7P";
    public static final String ACCESS_KEY = "QH9dF8jyKHAuwti3iXZ6";
    public static final String HUB_URL =
            "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    // Thread-safe driver for parallel execution
    public static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"os", "os_version", "browser", "browser_version", "device", "real_mobile"})
    public void setUp(
            @Optional("") String os,
            @Optional("") String os_version,
            @Optional("") String browser,
            @Optional("") String browser_version,
            @Optional("") String device,
            @Optional("false") String real_mobile
    ) throws Exception {

        MutableCapabilities capabilities = new MutableCapabilities();
        MutableCapabilities bstackOptions = new MutableCapabilities();

        // For Mobile Devices
        if (!device.isEmpty()) {
            // BrowserStack needs 'deviceName' for mobile
            bstackOptions.setCapability("deviceName", device);
            bstackOptions.setCapability("osVersion", os_version);
            bstackOptions.setCapability("realMobile", Boolean.parseBoolean(real_mobile));
            bstackOptions.setCapability("sessionName", device + " - Mobile Scraping Test");

            // Optional: Let BrowserStack decide default mobile browser
            // (Or set explicitly to Safari/Chrome if you prefer)
            if (device.toLowerCase().contains("iphone")) {
                capabilities.setCapability("browserName", "Safari");
            } else {
                capabilities.setCapability("browserName", "Chrome");
            }
        }

        // For Desktop Browsers
        else {
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("browserVersion", browser_version);
            bstackOptions.setCapability("os", os);
            bstackOptions.setCapability("osVersion", os_version);
            bstackOptions.setCapability("sessionName", browser + " - Desktop Scraping Test");
        }

        // Common BrowserStack options
        bstackOptions.setCapability("projectName", "El Pais Scraper");
        bstackOptions.setCapability("buildName", "TestNG Parallel Build");
        bstackOptions.setCapability("userName", USERNAME);
        bstackOptions.setCapability("accessKey", ACCESS_KEY);

        // Attach options
        capabilities.setCapability("bstack:options", bstackOptions);

        System.out.println("Launching session with capabilities: " + capabilities);

        // Initialize the RemoteWebDriver
        driver.set(new RemoteWebDriver(new URL(HUB_URL), capabilities));
    }

    @Test
    public void testElPaisScraping() throws Throwable {
        WebDriver webDriver = driver.get();
        try {
            // Run your web scraping logic
            WebScraping.scrapeArticles(webDriver);

            // Mark session as passed on BrowserStack
            ((JavascriptExecutor) webDriver).executeScript(
                    "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"passed\", \"reason\": \"Scraping executed successfully.\"}}"
            );
        } catch (Exception e) {
            e.printStackTrace();

            // Mark session as failed on BrowserStack
            ((JavascriptExecutor) webDriver).executeScript(
                    "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"failed\", \"reason\": \"" + e.getMessage() + "\"}}"
            );
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
