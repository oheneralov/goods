package goods;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Reporter;
import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Tests {
    private WebDriver driver;
    private StringBuffer verificationErrors = new StringBuffer();

    @DataProvider
        public Object[][] testData() throws IOException {
    	Object[][] data = null;
    	System.setProperty("log4j.configurationFile",
				"C:\\alex\\training\\java\\goods\\configuration.xml");
    	Logger log = LogManager.getRootLogger();
    	

		try {
			int row = 0;
			BufferedReader in = new BufferedReader(new FileReader("input.txt"));
			data = new Object[in.lines().toArray().length][3];
			String line;
			
			try {
				while ((line = in.readLine()) != null) {
					String[] lineData = line.split(",");
					
					for (int i = 0; i < lineData.length; i++) {
						data[row][i] = lineData[i];
						log.debug("data " + lineData[i]);

					}
 					row++;

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
            return data;
    }

    @BeforeTest
    public void setUp() {
    	verificationErrors.append("");
        // Create a new instance of the Firefox driver
    	System.setProperty("webdriver.gecko.driver", "C://alex//selenium//geckodriver-v0.18.0-win64//geckodriver.exe");

		// set something on the profile...
    	FirefoxProfile fp = new FirefoxProfile();
		DesiredCapabilities dc = DesiredCapabilities.firefox();
		dc.setCapability(FirefoxDriver.PROFILE, fp);
		dc.acceptInsecureCerts();
		driver = new FirefoxDriver(dc);
    
    }

    @Test(dataProvider = "testData")
    public void testAdministratorLogin(String login, String pass, String expectedResult) {
    	driver.get("https://192.168.56.101/administrator/");
    	if (driver == null) {
    		System.out.println("Error! driver is null!!!");
    	}
    	    WebDriverWait wait = new WebDriverWait(driver, 20);
    	    wait.until(ExpectedConditions.titleContains("Administration"));
            WebElement heightField = driver.findElement(By.name("username"));
            heightField.clear();
            heightField.sendKeys(login);

            WebElement weightField = driver.findElement(By.name("passwd"));
            weightField.clear();
            weightField.sendKeys(pass);
          
            WebElement calculateButton = driver.findElement(By.xpath("//button[@tabindex='3'and @class='btn btn-primary btn-block btn-large login-button']"));
            if (calculateButton.isDisplayed()) {
            	calculateButton.click();
            }
            else {
            	fail("Error! No submit button on login page!!! ");
            }
            
            assertEquals(driver.getTitle(), expectedResult);

    }

    @AfterTest
    public void tearDown() {
        //Close the browser
        driver.quit();
    }
}
