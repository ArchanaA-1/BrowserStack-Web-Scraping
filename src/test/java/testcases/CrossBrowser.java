package testcases;

import org.testng.annotations.Test;

import base.BaseClass;
import pages.ArticlePage;


public class CrossBrowser extends BaseClass {
	@Test(priority = 1)
	
	public void VerifySpanishLanguage() throws Throwable {
		try {
			ArticlePage ap = new ArticlePage(driver);
			ap.acceptCookie();
			ap.verifySpanishLanguage();
		}catch (Exception ex) {
			logger.error("TestFailed for Verify Spanish Language");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
    @Test(priority = 2)
	
	public void clickOpinion() throws Throwable {
		try {
			ArticlePage ap = new ArticlePage(driver);
			ap.clickOpinion();
		}catch (Exception ex) {
			logger.error("TestFailed for Opinion click");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}
    
    @Test(priority=3)
    public void scrapeArticles() throws Throwable {
		try {
			ArticlePage ap = new ArticlePage(driver);
			ap.scrapeArticles();
		}catch (Exception ex) {
			logger.error("TestFailed for scrape Articles");
			logger.error("Exception" + ex.getMessage());
			ex.printStackTrace();
		}
	}
    
    

}
