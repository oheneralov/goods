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

import com.google.common.io.Files;

import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Creds {
	String username = "";
	String pass = "";
}

public class Tests {
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	Logger log;

	@BeforeSuite(alwaysRun = true)
	public void SetUp() {
		System.setProperty("log4j.configurationFile", "C:\\alex\\training\\java\\goods\\configuration.xml");
		Logger log = LogManager.getRootLogger();
	}

	@DataProvider
	public Object[][] testData() throws IOException {
		Logger log = LogManager.getRootLogger();

		Creds creds = readCredsFromFile("creds.txt");
		log.debug(String.format("Generating  data, creds: %s , %s", creds.username, creds.pass));
		Pattern pattern = Pattern.compile("[^\\s]");
		Matcher matcher = pattern.matcher(creds.username);
		matcher.find();
		StringBuilder userName = new StringBuilder(matcher.group());
		log.debug("userName = %s", userName.toString());

		Pattern pattern2 = Pattern.compile("[^\\s]");
		Matcher matcher2 = pattern2.matcher(creds.pass);
		matcher2.find();
		StringBuilder password = new StringBuilder(matcher2.group());

		if ((userName.equals("")) || (password.equals(""))) {
			fail("Empty login/password!");
		}

		String url = "jdbc:mysql://192.168.56.101/test1";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, creds.username, creds.pass);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		StringBuilder sql = new StringBuilder("Select * from users");
		Statement statement = null;
		try {
			statement = conn.createStatement();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		ResultSet result = null;
		try {
			result = statement.executeQuery(sql.toString());
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Object[][] data = new Object[2][3];
		short row = 0;

		try {
			while (result.next()) {
				data[row][0] = result.getString("first_name");
				data[row][1] = result.getString("password");
				data[row][2] = result.getString("title");
				row++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}

	Creds readCredsFromFile(String filename) {
		Logger log = LogManager.getRootLogger();
		log.debug("Reading input data");
		List<String> alist = new ArrayList<String>();
		ArrayList<ArrayList<String>> alistOuter = new ArrayList<ArrayList<String>>();
		Charset charset = Charset.defaultCharset();
		File file = new File(filename);
		try {
			alist = Files.readLines(file, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String creds = alist.get(0);
		String[] credsarr = creds.split(",");
		log.debug("username = " + credsarr[0]);

		Creds creds1 = new Creds();

		creds1.username = credsarr[0];
		creds1.pass = credsarr[1];
		return creds1;

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
		WebDriverWait wait2 = new WebDriverWait(driver, 20);
		wait2.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//button[@tabindex='3'and @class='btn btn-primary btn-block btn-large login-button']")));
		WebElement heightField = driver.findElement(By.name("username"));
		heightField.clear();
		heightField.sendKeys(login);

		WebElement weightField = driver.findElement(By.name("passwd"));
		weightField.clear();
		weightField.sendKeys(pass);

		WebElement calculateButton = driver.findElement(
				By.xpath("//button[@tabindex='3'and @class='btn btn-primary btn-block btn-large login-button']"));
		if (calculateButton.isDisplayed()) {
			calculateButton.click();
		} else {
			fail("Error! No submit button on login page!!! ");
		}

		assertEquals(driver.getTitle(), expectedResult);

	}

	@AfterSuite
	public void tearDown() {
		// Close the browser
		driver.quit();
	}
}
