package base;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import utils.ExtentReportManager;
import utils.FileUtility;

public class BaseClass {
	public static WebDriver driver;
	public static Properties prop;
	public Logger logger;
	public FileUtility flip = new FileUtility();

	@Parameters({"browser"})
	//@BeforeMethod
	//@BeforeSuite
	@BeforeClass
	//@BeforeClass(groups={"Sanity","Regression"})

	public void setup(String br) throws Throwable {
		logger = LogManager.getLogger(this.getClass());
		
		
		
		if(flip.getPropertyKeyValue("ExecutionEnv").equalsIgnoreCase("remote")) {
			String huburl = "http://10.163.248.16:4444/wd/hub";
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setPlatform(Platform.WIN11);
			
			switch(br.toLowerCase())
			 {
				case "chrome":
					capabilities.setBrowserName("chrome"); break;

				case "edge":
					capabilities.setBrowserName("MicrosoftEdge"); break;
					
				case "firefox":
					capabilities.setBrowserName("firefox"); break;
					
				default:
					System.out.println("Invalid browser name...");
					return;
				}
			driver= new RemoteWebDriver(new URL(huburl),capabilities);
				
		}
		else {
		switch (br.toLowerCase()) {
		case "chrome":
			/*ChromeOptions options = new ChromeOptions();
			options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);*/
			driver = new ChromeDriver();
			
			break;
		case "edge":
			/*EdgeOptions option1= new EdgeOptions();
			option1.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);*/
			driver = new EdgeDriver();
			break;
		case "firefox":
			/*FirefoxOptions option2= new FirefoxOptions();
			option2.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);*/
			driver = new FirefoxDriver();
			break;
		default:
			System.out.println("Invalid browser name...");
			return;
		} }
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		driver.manage().window().maximize();
		driver.get(flip.getPropertyKeyValue("url"));
		/*Alert alert = driver.switchTo().alert();
		alert.accept();*/

		// ExtentReportManager.
		Assert.assertEquals(driver.getCurrentUrl(), flip.getPropertyKeyValue("url"));
		Thread.sleep(3000);

	}
  
    @AfterSuite

	public void teardown() {
	  //if (driver != null) {
      driver.quit();

	}

	
	
	public String captureScreen(String tname) throws IOException{
		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot takescreenshot = (TakesScreenshot) driver;
		File sourceFile = takescreenshot.getScreenshotAs(OutputType.FILE);
		String targetFilePath = System.getProperty("user.dir")+"\\Screenshots\\"+ tname +"_"+timeStamp + ".png";
		File targetFile = new File(targetFilePath);
		sourceFile.renameTo(targetFile);
		return targetFilePath;
		
	}

}
