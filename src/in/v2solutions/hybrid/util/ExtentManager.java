package in.v2solutions.hybrid.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.SystemAttributeContext;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;


public class ExtentManager extends Constants {

	private static final SystemAttributeContext systemAttributeContext = null;
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentReports extent;
	private static ExtentTest test;
	private static String filePath = System.getProperty("user.dir") + "/testresults/ExtentReport/";
	public static Boolean EXTENT = true;
	private static KlovReporter klovReporter = new KlovReporter();
	public static String todayAsString = new SimpleDateFormat("dd/MM/yy").format(new Date());

	public static ExtentReports GetExtent() {
		getConfigDetails();
		//System.out.println(": V2Centralized Reporting is: " + v2CentralizedReporting);
		if (v2CentralizedReporting.contains("On")) {
			try {
				klovReporter.initMongoDbConnection("localhost", 27017);
				// klovReporter.initMongoDbConnection("192.168.50.25", 27017);
				if (extent != null) {
					// System.out.println(": Extent Reporting System is already
					// Initialized");
					return extent;
				} else {
					System.out.println(": Initializing Extent Reporting System");
					extent = new ExtentReports();
					extent.setSystemInfo("SUT Url", SUTUrl);
					extent.setSystemInfo("SUT Server", SUTServer);
					extent.setSystemInfo("SUT Build", SUTBuildVersion);
					extent.setSystemInfo("TestBed", tBedType);
					extent.setSystemInfo("OS", osType);
					extent.setSystemInfo("Browser", bType);
					extent.setSystemInfo("Mobile Device", deviceName);
					extent.setSystemInfo("Test Suite", suitetype);
					extent.setSystemInfo("Test Module", module);

					// klovReporter.setKlovUrl("http://192.168.50.25");
					klovReporter.setKlovUrl("http://localhost");
					extent.attachReporter(getHtmlReporter(), klovReporter);
					return extent;
				}
			} catch (Exception e) {
				System.out.println("Following Error in ExtentReports Methods when V2Centralized Reporting is On: "
						+ e.getMessage());
			}
			return extent;
		} else {
			//System.out.println(": V2Centralized Reporting is: " + v2CentralizedReporting);
			try {
				if (extent != null) {
					//System.out.println(": Extent Reporting System is already Initialized");
					return extent;
				} else {
					System.out.println(": Initializing Extent Reporting System");
					extent = new ExtentReports();
					extent.setSystemInfo("SUT Url", SUTUrl);
					extent.setSystemInfo("SUT Server", SUTServer);
					extent.setSystemInfo("SUT Build", SUTBuildVersion);
					extent.setSystemInfo("TestBed", tBedType);
					extent.setSystemInfo("OS", osType);
					extent.setSystemInfo("Browser", bType);
					extent.setSystemInfo("Mobile Device", deviceName);
					extent.setSystemInfo("Test Suite", suitetype);
					extent.setSystemInfo("Test Module", module);
					extent.attachReporter(getHtmlReporter(), htmlReporter);
					return extent;
				}
			} catch (Exception e) {
				System.out.println("Following Error in ExtentReports Methods when V2Centralized Reporting is Off: "
						+ e.getMessage());
			}
			return extent;
		}
	}

	private static ExtentHtmlReporter getHtmlReporter() {

		getConfigDetails();
		if (v2CentralizedReporting.contains("On")) {
			htmlReporter = new ExtentHtmlReporter(
					filePath + "ExtentReport-Dashboard_" + suiteName + "_" + strActDate + ".html");
			htmlReporter.config().setChartVisibilityOnOpen(true);
			if (tBedType.equals("DESKTOP")) {
				
				htmlReporter.config().setDocumentTitle(projectName + "_Extend_Report");
				htmlReporter.config().setReportName(SUTServer + "-" + SUTBuildVersion + "-" + module + "-" + tBedType + "_" + bType);

				// **********For klov *************************************

				klovReporter.setProjectName(projectName + "-" + "(" + SUTServer + "-" + "b" + SUTBuildVersion + ")"
						+ "_" + suitetype + "_" + todayAsString);
				if (deviceName.contains("NA")) {
					klovReporter.setReportName(bType + "-" + tBedType);
				} else {
					klovReporter.setReportName(bType + "-" + tBedType + "-" + deviceName);
				}
				
				// ***********For klov *************************************
				
			}
			else {
				htmlReporter.config().setDocumentTitle(projectName + "_Extend_Report");
				htmlReporter.config().setReportName(SUTServer + "-" + SUTBuildVersion + "-" + module + "-" + tBedType + "_" + bType);

				// **********For klov *************************************

				klovReporter.setProjectName(projectName + "-" + "(" + SUTServer + "-" + "b" + SUTBuildVersion + ")"
						+ "_" + suitetype + "_" + todayAsString);
				if (deviceName.contains("NA")) {
					klovReporter.setReportName(bType + "-" + tBedType);
				} else {
					klovReporter.setReportName(bType + "-" + tBedType + "-" + deviceName);

				}
				
				// ***********For klov *************************************
			}
		} else {
			//System.out.println(": V2Centralized Reporting is: " + v2CentralizedReporting);
			htmlReporter = new ExtentHtmlReporter(
					filePath + "ExtentReport-Dashboard_" + suiteName + "_" + strActDate + ".html");
			htmlReporter.config().setChartVisibilityOnOpen(true);

			if (tBedType.equals("DESKTOP")) {
				htmlReporter.config().setDocumentTitle(projectName + "_Extend_Report");
				htmlReporter.config()
						.setReportName(SUTServer + "-" + SUTBuildVersion + "-" + module + "-" + tBedType + "_" + bType);
			} else {
				htmlReporter.config().setDocumentTitle(projectName + "_Extend_Report");
				htmlReporter.config()
						.setReportName(SUTServer + "-" + SUTBuildVersion + "-" + module + "-" + tBedType + "_" + bType);
			}
		}
		return htmlReporter;
	}

	public static ExtentTest createTest(String name, String description) {
		test = extent.createTest(name, description);
		return test;
	}
}
