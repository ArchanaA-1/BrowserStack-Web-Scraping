package pages;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class ArticlePage extends BasePage {

	public ArticlePage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	private static final String API_KEY="3aa0fc5bc3msh31adb7f413ef0f7p14d7b3jsnaf73762828cb";
	private static final String API_HOST="google-translate113.p.rapidapi.com";
	private static final String TRANSLATE_URL="https://google-translate113.p.rapidapi.com/api/v1/translator/text";
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	@FindBy(xpath = "//time[contains(@id,'header_date')]/../span")
	WebElement SelectSpanishText;
	@FindBy(xpath = "//span[text()='Accept']")
	WebElement Accept;
	@FindBy(xpath = "(//a[text()='Opinión'])[1]")
	WebElement Opinion;
	@FindBy(xpath = "(//a[text()='Opinión'])[2]")
	WebElement OpinionPage;
	
	String language=driver.findElement(By.tagName("html")).getAttribute("lang");
	
	//Click on Accept button on the cookie pop up
	public void acceptCookie() throws Throwable{
		//Using Explicit wait to wait until the accept button is visible
		wait.until(ExpectedConditions.visibilityOf(Accept));
		clkAction(Accept, "Accept Button");
	}
	
	//Verify if the page is in Spanish language
	public void verifySpanishLanguage() throws Throwable {
		String SpanishText=SelectSpanishText.getText();
		try {
		Assert.assertEquals(language,"es-ES");
		Assert.assertEquals(SpanishText,"Seleccione:");
		logger.info("Page is in Spanish Language");
		System.out.println("Page is in Spanish Language");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		
		}catch (Exception ex) {
			logger.error("Article is not in Spanish");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	//Navigate to the Opinion section
	public void clickOpinion() throws Throwable{
		clkAction(Opinion, "Opinion Button");
	}
	
	public void closePopUp() throws Throwable{
		try {
	
	    WebElement closeButton = driver.findElement(By.cssSelector(".close-popup, .popup-close"));
	    closeButton.click();
	} catch (Exception e) {
	    // ignore if not present
	}}
	
	
	public void scrapeArticles() throws Throwable{
	   
		wait.until(ExpectedConditions.visibilityOf(OpinionPage));
		//closePopUp();
		List<String> englishTitles = new ArrayList<>();
		try {
		//Fetching all the articles in the page and store it in a list
		List<WebElement> articleTitles=driver.findElements(By.cssSelector("article h2"));
		List<WebElement> articleContent=driver.findElements(By.cssSelector("article p"));
		
		//To take the minimum value so that if there are articles less than 5 then there is no exception in for loop
		int count=Math.min(articleTitles.size(), 5);
		
		for(int i=0;i<count;i++) {
			
			//Print Article Title and Content
			WebElement article=articleTitles.get(i);
			WebElement paragraph=articleContent.get(i);
			
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article")));
			String Title=article.getText();
			String Content=paragraph.getText();
			System.out.println("Article Title"+ (i+1)+": "+Title);
			System.out.println("Article Content"+ (i+1)+": "+Content);
			
			//Translate to English
			String translatedTitle=translateText(Title,"es","en");
			englishTitles.add(translatedTitle);
			System.out.println("English Title of Article"+ (i+1)+": "+translatedTitle);	
			
			
			//Click Title
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("article h2")));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", article);
			Thread.sleep(500);
			article.click();
			
			//Image Download
			WebElement img=driver.findElement(By.cssSelector("figure img"));
			String imgURL=img.getAttribute("src");
			
			//To validate if imgURL is not Null and an Empty String
			if(imgURL!=null && !imgURL.isEmpty()) {
				saveImage(imgURL, "Article_Image_"+(i+1)+".jpg");
				System.out.println("Image of Article "+(i+1)+" saved successfully with file name Article_Image_"+(i+1)+".jpg");
				System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			}
			//Navigate back to Opinion Page
			driver.navigate().back();
			
			
		
		}
		//Count repeated Words
		findRepeatedWords(englishTitles);
		}catch (Exception ex) {
			logger.error("Error in Web Scraping");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
			}
		}
		
	
	//Save Image in local system
	public void saveImage(String imgURL, String fileName) throws Throwable {
		try {
			//Create a URL Object for the image
			URI uri = URI.create(imgURL);
			URL url = uri.toURL();
			//InputStream in= new URL(imgURL).openStream();
			//Create an Input stream to read the image data
			InputStream in=url.openStream();
			
			//Define where to save the image
			Path imagePath=Paths.get("image",fileName);	
			
			//Ensure target folder exists
			Files.createDirectories(imagePath.getParent());
			
			//Copy the image data from URL to local file
			Files.copy(in, imagePath,StandardCopyOption.REPLACE_EXISTING);
			
		} catch (Exception ex) {
			logger.error("Error in Article Image Saving");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	//Translate text from Spanish to English
	private static String translateText(String text, String sourceLang, String targetLang) {
		try {
			HttpClient client=HttpClient.newHttpClient();
			//API Request Body
			String requestBody=new JSONObject()
					.put("from",sourceLang).put("to",targetLang).put("text",text).toString();
			//Build Post request
			HttpRequest request=HttpRequest.newBuilder().uri(URI.create(TRANSLATE_URL)).header("content-type","application/json").header("X-RapidAPI-Key",API_KEY).header("X-RapidAPI-Host", API_HOST)
					.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
			//Send request and get get the server's response as a string
			HttpResponse<String> response=client.send(request, HttpResponse.BodyHandlers.ofString());
			//Pares JSON Response from the API
			JSONObject jsonResponse=new JSONObject(response.body());
			//Extract the translated text
			return jsonResponse.getString("trans");
			
		}catch (Exception ex) {
			logger.error("Error in Article Image Saving");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
			//Return original text if translation fails
			return text;
			
		}
		
	}
	
	//Print the repeated words in the translated titles
	private static void findRepeatedWords(List<String> titles) {
		// Combine all words into one list
        List<String> words = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            String header = titles.get(i);
            //Split all the translated titles into individual words
            String[] splitWords = header.toLowerCase().split("\\s+");
            for (int j = 0; j < splitWords.length; j++) {
                words.add(splitWords[j]);
            }
        }
        
     // List to keep track of already-checked words
        List<String> checked = new ArrayList<>();

        System.out.println("Words repeated more than twice:");
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            //skip words already counted
            if (!checked.contains(word)) {
                int count = 0;
             // Count how many times this word appears
                for (int j = 0; j < words.size(); j++) {
                    if (words.get(j).equals(word)) {
                        count++;
                    }
                }
             //Print if the word appears more than twice
                if (count > 2) {
                    System.out.println(word + " appears " + count + " times");
                }
                checked.add(word);
            }
        }
	}

}
