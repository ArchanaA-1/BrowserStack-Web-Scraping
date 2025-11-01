package scraper;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

public class WebScraping {

    private static final String API_KEY = "3aa0fc5bc3msh31adb7f413ef0f7p14d7b3jsnaf73762828cb";
    private static final String API_HOST = "google-translate113.p.rapidapi.com";
    private static final String TRANSLATE_URL = "https://google-translate113.p.rapidapi.com/api/v1/translator/text";

    public static void scrapeArticles(WebDriver driver) throws Throwable {
    	driver.get("https://elpais.com/opinion/");
        
    	//Maximixe window for web browsers
    	String platform = ((RemoteWebDriver) driver).getCapabilities().getCapability("deviceName") != null ?
                ((RemoteWebDriver) driver).getCapabilities().getCapability("deviceName").toString() : "Desktop";
        if (platform.equalsIgnoreCase("Desktop")) {
            driver.manage().window().maximize();
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        System.out.println("Navigated to El País");

        //Accept cookies if present
        handleCookies(driver);

        // Verify Spanish
        String lang = driver.findElement(By.tagName("html")).getAttribute("lang");
        if (!lang.startsWith("es")) {
            throw new RuntimeException("Page not in Spanish!");
        }
        System.out.println("Page is in Spanish (lang=" + lang + ")");

        // Navigate to Opinión section
        driver.get("https://elpais.com/opinion/");
        System.out.println("Opened 'Opinión' section");
        
        // Wait for articles to load
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article h2 a")));
        
        //Fetching all the articles in the page and store it in a list
        List<WebElement> articleLinks = driver.findElements(By.cssSelector("article h2 a"));
        List<WebElement> articleTitles = driver.findElements(By.cssSelector("article h2"));
        
        List<String> englishTitles = new ArrayList<>();
        
        //To take the minimum value so that if there are articles less than 5 then there is no exception in for loop
        int count = Math.min(articleTitles.size(), 5);

        for (int i = 0; i < count; i++) {
            // Re-locate elements inside loop to avoid stale references
            WebElement article = driver.findElements(By.cssSelector("article h2")).get(i);
            WebElement paragraph = driver.findElements(By.cssSelector("article p")).get(i);

            wait.until(ExpectedConditions.visibilityOf(article));
            String title = article.getText();
            String content = paragraph.getText();
            String articleURL=articleLinks.get(i).getAttribute("href");

            System.out.println("Article Title " + (i + 1) + ": " + title);
            System.out.println("Article Content " + (i + 1) + ": " + content);

            // Translate to English
            String translatedTitle = translateText(title, "es", "en");
            englishTitles.add(translatedTitle);
            System.out.println("English Title of Article " + (i + 1) + ": " + translatedTitle);
            
            // Navigate to articles
            driver.navigate().to(articleURL);

            // Wait for image to load and download
            WebElement img = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("figure img")));
            String imgURL = img.getAttribute("src");

            //Download if image is present
            if (imgURL != null && !imgURL.isEmpty()) {
                saveImage(imgURL, "Article_Image_" + (i + 1) + ".jpg");
                System.out.println("Image of Article " + (i + 1) + " saved successfully.");
                System.out.println("--------------------------------------------------------------------");
            }

            // Navigate back to opinion page
            driver.get("https://elpais.com/opinion/");

            //Fetch article links
            articleLinks = driver.findElements(By.cssSelector("article h2 a"));
        }
        // Find repeated words
        findRepeatedWords(englishTitles);
    }

    //save file
    private static void saveImage(String imgURL, String fileName) throws Throwable {
    	//Create URL and Input stream to read the image data
        try (InputStream in = URI.create(imgURL).toURL().openStream()) {
        	// Define where to save the image
            Path imagePath = Paths.get("images", fileName);
            // Ensure target folder exists
            Files.createDirectories(imagePath.getParent());
            // Copy the image data from URL to local file
            Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println("Error saving image: " + ex.getMessage());
        }
    }
    
    //Handle cookies
    public static void handleCookies(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Try all possible cookie accept buttons (desktop + mobile)
        List<By> possibleCookieButtons = Arrays.asList(
            By.xpath("//button[contains(translate(., 'ACEPTAR', 'aceptar'), 'aceptar')]"),
            By.xpath("//button[contains(., 'Accept')]"),
            By.xpath("//input[@value='Accept']"),
            By.xpath("//button[contains(., 'Aceptar y cerrar')]"),
            By.xpath("//button[contains(., 'Aceptar todas')]"),
            By.xpath("//button[contains(., 'Continue')]"),
            By.xpath("//button[contains(., 'Continue with essential')]"),
            By.xpath("//div[contains(@class, 'cookie')]//button"),
            By.xpath("//div[@role='dialog']//button"),
            By.cssSelector("button#onetrust-accept-btn-handler"), // common cookie tool
            By.xpath("//button[contains(@class,'accept')]"), // generic accept class
            By.xpath("//span[contains(text(),'Accept')]")     // iOS often wraps text inside span
        );

        for (By locator : possibleCookieButtons) {
            try {
                WebElement cookieBtn = wait.until(ExpectedConditions.elementToBeClickable(locator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cookieBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cookieBtn);
                System.out.println("Cookie popup handled");
                return;
            } catch (Exception ignored) {
                // Try next locator
            }
        }

        System.out.println("No cookie popup found or already accepted.");
    }
    
    //Translate text from Spanish to English
    private static String translateText(String text, String sourceLang, String targetLang) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            //API Request Body
            String requestBody = new JSONObject().put("from", sourceLang).put("to", targetLang).put("text", text).toString();
            //Build Post request
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(TRANSLATE_URL)).header("content-type", "application/json")
                    .header("X-RapidAPI-Key", API_KEY).header("X-RapidAPI-Host", API_HOST).POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            //Send request and get get the server's response as a string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //Parse JSON Response from the API
            JSONObject json = new JSONObject(response.body());
            //Extract the translated text
            return json.getString("trans");

        } catch (Exception e) {
            System.out.println("Translation failed: " + e.getMessage());
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