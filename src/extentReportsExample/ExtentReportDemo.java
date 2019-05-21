package extentReportsExample;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


public class ExtentReportDemo {

	public WebDriver driver;
	public ExtentHtmlReporter htmlReporter; //responsible for the look and feel of the extent reports
	public ExtentReports extent; //Responsible for adding System related information
	public ExtentTest test; //Responsible for logging and adding screenshots to the reports

	@BeforeTest
	public void createReport() throws UnknownHostException {
		htmlReporter =new ExtentHtmlReporter(System.getProperty("user.dir")+"/test-output/my-report.html"); //you need to specify the path where you wanted to store your reports
		htmlReporter.config().setTheme(Theme.DARK);
		htmlReporter.config().setDocumentTitle("My Extent Reports");
		htmlReporter.config().setReportName("Functional Testing Reports");

		extent=new ExtentReports();
		extent.attachReporter(htmlReporter);

		//Passing General/System related information
		InetAddress addr = InetAddress.getLocalHost();//this is to get the system related information using java methods
		extent.setSystemInfo("HostName", addr.getHostName());
		extent.setSystemInfo("OS", "Windows10");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("IP", addr.getHostAddress());
	}

	@AfterTest
	public void endReport() {
		extent.flush();
	}

	@BeforeMethod
	public void initialiseWebDriver() {
		driver=new ChromeDriver();
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\pravinutala\\Downloads\\Jars\\Selenium Jars\\chromedriver_win32\\chromedriver.exe");
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://opensource-demo.orangehrmlive.com/");
	}

	@Test
	public void loginTest() {
		test= extent.createTest("Login Test");
		driver.findElement(By.id("txtUsername")).sendKeys("Admin");
		driver.findElement(By.id("txtPassword")).sendKeys("admin123");
		driver.findElement(By.id("btnLogin")).click();
	}

	@Test
	public void verifyLogo() {
		test= extent.createTest("Verify Logo");
		Assert.assertTrue(driver.findElement(By.xpath(".//div[@id='divLogo']/img")).isDisplayed());
	}

	@Test
	public void OrangeHRMcreateNodeTest() {
		test = extent.createTest("OrangeHRMcreateNodeTest");

		test.createNode("Login with Valid input");
		Assert.assertTrue(true);

		test.createNode("Login with In-valid input");
		Assert.assertTrue(false);
	}

	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {
		if(result.getStatus()==ITestResult.SUCCESS) { //when test case got passed, we are logging this into report
			test.log(Status.PASS,result.getName() + "test case is passed");
		}

		if(result.getStatus()==ITestResult.FAILURE) {
			test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getName()); // to add name in extent report
			test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getThrowable()); // to add error/exception in extent report
			 String screenshotPath = ExtentReportDemo.getScreenshot(driver, result.getName());
			   test.addScreenCaptureFromPath(screenshotPath);// adding screen shot
			  } else if (result.getStatus() == ITestResult.SKIP) {
			   test.log(Status.SKIP, "Test Case SKIPPED IS " + result.getName());
			  }
		driver.quit();
	}

	private static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		  TakesScreenshot ts = (TakesScreenshot) driver;
		  File source = ts.getScreenshotAs(OutputType.FILE);
		  
		  // after execution, you could see a folder "FailedTestsScreenshots" under src folder
		  String destination = System.getProperty("user.dir") + "/Screenshots/" + screenshotName + dateName + ".png";
		  File finalDestination = new File(destination);
		  FileUtils.copyFile(source, finalDestination);
		  return destination;
	}

}
