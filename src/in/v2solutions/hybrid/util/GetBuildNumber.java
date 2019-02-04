package in.v2solutions.hybrid.util;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GetBuildNumber {
	public WebDriver driver = new ChromeDriver();
	String rootPath = System.getProperty("user.dir");

	@BeforeClass // +++++++++++++++++++++++++++++++++++++++++++++++ BEFORE CLASS
					// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void setUp() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver",
				rootPath + "/sysfiles/" + "/browserdrivers/" + "/chromedriver/chromedriver.exe");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	@Test // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ TEST
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void buildNo() throws Exception {
		String twoDimentionArray[] = null;
		String sBuildNum[] = null;
		String filename;
		FileWriter out, fw;
		try {
			driver.get("https://dproto.hulftinc.com");
			Thread.sleep(20000);
			String sBuildNo = driver.findElement((By.xpath("//div[@class='col-md-4 text-muted text-right']")))
					.getText();
			System.out.println("----------------------------- 1: " + sBuildNo);
			twoDimentionArray = sBuildNo.split("build: ");
			sBuildNo = twoDimentionArray[1];
			System.out.println("----------------------------- 2: " + sBuildNo);
			sBuildNum = sBuildNo.split(",");
			sBuildNo = sBuildNum[0];
			System.out.println("----------------------------- 3: " + sBuildNo);

			File file = new File(rootPath + "/temp/Inter/tempfile.txt");
			if (file.exists()) {
				file.delete();
			}
			fw = new FileWriter(file, true);
			fw.write(sBuildNo + "\r\n");
			fw.close();
		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
		}
	}

	@AfterClass
	// STEP8: QUIT BROWSER
	public void tearDown() {
		if (driver != null) {
			System.out.println("Closing Chrome Browser");
			driver.quit();
		}
	}
}