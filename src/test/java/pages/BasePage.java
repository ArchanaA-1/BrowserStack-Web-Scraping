package pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utils.FileUtility;

public class BasePage {
	WebDriver driver;
	public static Logger logger;
	public FileUtility flip = new FileUtility();
	WebDriverWait lowwait = new WebDriverWait(driver, Duration.ofSeconds(20));
	WebDriverWait mediumwait = new WebDriverWait(driver, Duration.ofSeconds(30));
	WebDriverWait longwait = new WebDriverWait(driver, Duration.ofSeconds(40));

	public BasePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logger = LogManager.getLogger(this.getClass());

	}

	public void verifyElement(WebElement element, String name) {

		try {
			
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			boolean as = element.isDisplayed();

			if (as == true) {
				Assert.assertTrue(as);
				logger.info(name + " icon is visible " + as);
			} else {
				Assert.assertFalse(as);
				logger.error(name + " icon is not visible " + as);
			}

		} catch (Exception ex) {
			logger.error("TestFailed for " + name);
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public void clkAction(WebElement element, String name) throws Throwable {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			mediumwait.until(ExpectedConditions.elementToBeClickable(element));
			boolean as = element.isDisplayed();
			if (as == true) {
				Assert.assertTrue(as);
				logger.info(name + " icon is visible " + as);
				element.click();
				logger.info(name + " is clicked");

			} else {
				Assert.assertFalse(as);
				logger.error(name + " icon is not visible " + as);
			}
		} catch (Exception ex) {
			logger.error("TestFailed for " + name);
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
		Thread.sleep(3000);

	}

	public void highlight(WebElement element) {
		lowwait.until(ExpectedConditions.elementToBeClickable(element));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].style.border='0.1px solid red'", element);
	}

	public void Highlight(WebElement element) {
		lowwait.until(ExpectedConditions.elementToBeClickable(element));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
	}

	public void PassValue(WebElement element, String name, String value) throws Throwable {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			mediumwait.until(ExpectedConditions.elementToBeClickable(element));
			boolean as = element.isDisplayed();
			if (as == true) {
				Assert.assertTrue(as);
				logger.info(name + " icon is visible " + as);
				element.sendKeys(value);
				logger.info("Passed the value successfully in +" + name + "textbox");

			} else {
				Assert.assertFalse(as);
				logger.error(name + " icon is not visible " + as);
			}
		} catch (Exception ex) {
			logger.error("TestFailed for " + "SendKey" + name);
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
		Thread.sleep(3000);

	}
	
	public void clearValue(WebElement element, String name) throws Throwable {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			mediumwait.until(ExpectedConditions.elementToBeClickable(element));
			boolean as = element.isDisplayed();
			if (as == true) {
				Assert.assertTrue(as);
				logger.info(name + " icon is visible " + as);
				element.clear();
				logger.info("Cleard the value successfully in +" + name + "textbox");

			} else {
				Assert.assertFalse(as);
				logger.error(name + " icon is not visible " + as);
			}
		} catch (Exception ex) {
			logger.error("TestFailed for " + "TextBox" + name);
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
		Thread.sleep(3000);

	}

	public void UrlVerification(String name) throws Throwable {
		try {

			String Actualurl = driver.getCurrentUrl();
			String Expectedurl = flip.getPropertyKeyValue(name);
			boolean uv = Actualurl.contentEquals(Expectedurl);
			if (uv == true) {
				logger.info(name + " page is loaded ");
			} else {
				logger.info("page is not loaded ");
			}

		} catch (Exception ex) {
			logger.error("TestFailed for UrlValidation");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public void SwitchWindow() throws Throwable {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
			String parent = driver.getWindowHandle();
			Set<String> ChildrenWindowHandle = driver.getWindowHandles();
			Iterator<String> ChildrenWindowHandleIterator = ChildrenWindowHandle.iterator();
			while (ChildrenWindowHandleIterator.hasNext()) {
				String windowhandle = ChildrenWindowHandleIterator.next();
				if (!windowhandle.equals(parent)) {
					driver.switchTo().window(windowhandle);
					Thread.sleep(5000);
				}
			}
		} catch (Exception ex) {
			logger.error("TestFailed for SwitchWindow_PlanetStore");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public void SwitchWindow_Close() {
		try {
			driver.close();
			Set<String> allWindowId = driver.getWindowHandles();
			Thread.sleep(5000);
			Iterator<String> windowHandleIterator = allWindowId.iterator();
			while (windowHandleIterator.hasNext()) {
				String windowhandle = windowHandleIterator.next();
				driver.switchTo().window(windowhandle);

			}

		} catch (Exception ex) {
			logger.error("TestFailed for SwitchWindow_PlanetStore");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public String captureScreen(String name) throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot takescreenshot = (TakesScreenshot) driver;
		File sourceFile = takescreenshot.getScreenshotAs(OutputType.FILE);
		String targetFilePath = System.getProperty("user.dir") + "\\Screenshots\\\\" + name + "\\" + timeStamp + ".png";
		File targetFile = new File(targetFilePath);
		sourceFile.renameTo(targetFile);
		return targetFilePath;

	}

	public void MouseHover(WebElement element) throws Throwable {
		Thread.sleep(3000);
		Actions act = new Actions(driver);
		act.moveToElement(element).perform();

	}

	public void ScrollDown(WebElement element) throws Throwable {
		try {
			//lowwait.until(ExpectedConditions.elementToBeClickable(element));
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			//lowwait.until(ExpectedConditions.elementToBeClickable(element));
			JavascriptExecutor j = (JavascriptExecutor) driver;
			j.executeScript("arguments[0].scrollIntoView(true)", element);
			Thread.sleep(5000);
		} catch (Exception ex) {
			logger.error("TestFailed for scrollDown");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void ScrollUp(WebElement element) throws Throwable {
		try {
			//mediumwait.until(ExpectedConditions.elementToBeClickable(element));
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='0.3px solid red'", element);
			//mediumwait.until(ExpectedConditions.elementToBeClickable(element));
			JavascriptExecutor j = (JavascriptExecutor) driver;
			j.executeScript("arguments[0].scrollIntoView(false)", element);
			Thread.sleep(5000);
		} catch (Exception ex) {
			logger.error("TestFailed for scrollUp");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void ScrollDown() throws Throwable {
		Robot r = new Robot();
		for (int i = 0; i < 8; i++) {

			r.keyPress(KeyEvent.VK_PAGE_DOWN);
		}
		r.keyRelease(KeyEvent.VK_PAGE_DOWN);

	}

	public void ScrollUp() throws Throwable {
		Robot r = new Robot();
		for (int i = 0; i < 2; i++) {

			r.keyPress(KeyEvent.VK_PAGE_UP);
			Thread.sleep(2000);
		}
		r.keyRelease(KeyEvent.VK_PAGE_UP);
		Thread.sleep(3000);
	}
	
	public void Save() throws Throwable {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ALT);
		r.keyPress(KeyEvent.VK_S);
		Thread.sleep(500);
		r.keyRelease(KeyEvent.VK_S);
		r.keyRelease(KeyEvent.VK_ALT);
		Thread.sleep(3000);
		
	}
	
	
	
	public void Exit() throws Throwable {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ALT);
		r.keyPress(KeyEvent.VK_X);
		r.keyRelease(KeyEvent.VK_X);
		r.keyRelease(KeyEvent.VK_ALT);
		
		Thread.sleep(3000);
	}
	
	public void ENTER() throws Throwable {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(1000);
	}
	
	public void TAB() throws Throwable {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_TAB);
		r.keyRelease(KeyEvent.VK_TAB);
		Thread.sleep(1000);
	}
	
	public void exitdriverwindow() throws Throwable {

		Set<String> allWindowId = driver.getWindowHandles();
		Thread.sleep(5000);
		Iterator<String> windowHandleIterator = allWindowId.iterator();
		while (windowHandleIterator.hasNext()) {
			String windowhandle = windowHandleIterator.next();
			driver.switchTo().window(windowhandle);
			System.out.println("print exit driver window" + windowhandle);
		}

	}

	public void frame() {
		try {
			driver.switchTo().frame("frameForwardToApp");
			Thread.sleep(2000);
		} catch (Exception ex) {
			logger.error("TestFailed for thirdwindowSwtich_DealingBank");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
