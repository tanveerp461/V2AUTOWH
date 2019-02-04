package in.v2solutions.hybrid.util;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.paulhammant.ngwebdriver.NgWebDriver;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;
import net.lightbody.bmp.BrowserMobProxy;

public class Keywords extends Constants {
	/*
	 * @HELP
	 * 
	 * @class: Keywords
	 * 
	 * @Singleton Class: Keywords getKeywordsInstance()
	 * 
	 * @ constructor: Keywords()
	 * 
	 * @methods: OpenBrowser(), Navigate(), NavigateTo(), ResizeBrowser(), Login(), Input(), InputDDTdata(), Click(), SelectValueFromDropDownWithAnchorTags(), SelectValueFromDropDown(), SelectUnselectCheckbox(), GetText(), GetDollarPrice(), GetCountOfAllWebElements(),
	 * GetCountOfDisplayedWebElements(), GetCountOfImagesDisplayed(), GetSizeOfImages(), GetPositionOfImages(), Wait(), VerifyText(), VerifyTextDDTdata(), VerifyDollarPrice(), VerifyTitle(), VerifyUrl(), VerifyTotalPrice(), VerifyTotalPriceForDDT(), VerifyListOfStrings(),
	 * VerifyCountOfAllWebElements(), VerifyCountOfDisplayedWebElements(), VerifyImageCounts() VerifyListOfImageDimensions(), VerifyListOfImagePositions(), HighlightNewWindowOrPopup(), HandlingJSAlerts(), Flash_LoadFlashMovie(), Flash_SetPlaybackQuality(), Flash_SetVolume(),
	 * Flash_SeekTo(), Flash_VerifyValue(), Flash_StopVideo(), CloseBrowser(), QuitBrowser().
	 * 
	 * @parameter: Different parameters are passed as per the method declaration
	 * 
	 * @notes: Keyword Drives and Executes the framework interacting with the MasterTSModule xlsx file
	 * 
	 * @returns: All respective methods have there return types
	 * 
	 * @END
	 */

	@SuppressWarnings("rawtypes")
	public static Map<String, String> getTextOrValues = new HashMap<String, String>();
	// Generating Dynamic Log File
	public String FILE_NAME = System.setProperty("filename", tsName + tcName + " - " + getCurrentTime());
	public static long start;
	static Keywords keywords = null;
	public boolean Fail = false;
	public boolean highlight = false;
	public boolean captureScreenShot = false;
	public String failedResult = "";
	public static int count = 0;
	public static String scriptTableFirstRowData = "";
	static Properties props;
	public static Connection connection = null;
	public static Statement statement = null;
	public String parentWindowID;
	public String GTestName = null;
	String StrGet = null;
	String StrPost = null;
	public BrowserMobProxy proxy;
	public String interGlobal;
	StringBuilder sb = new StringBuilder(100);
	Pattern patternDigit = Pattern.compile("([0-9]+)");
	Matcher matcher;
	int allOfferCount = 0;
	BufferedWriter bw = null;
	FileWriter fw = null;
	BufferedReader br = null;
	FileReader fr = null;
	LogEntries logEntries;
	NgWebDriver ngdriver;
	public String ActualText = null;
	public static boolean flag = false;
	public List<String> filename = new ArrayList<String>();
	public String fileName = null;
	public String GExpectedValue = null;
	public String GActualValue = null;

	private Keywords() throws IOException {

		ngdriver = new NgWebDriver((JavascriptExecutor) driver);
		props = new Properties();
		props.load(new FileInputStream(new File(orPath + "OR.properties/")));

		System.out.println("INFO:=> Initializing keywords");
		APP_LOGS.debug("INFO:=> Initializing keywords");
		// Initialize properties file
		try {
			// Config
			getConfigDetails();
			// OR
			OR = new Properties();
			fs = new FileInputStream(orPath + "OR.properties/");
			OR.load(fs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void executeKeywords(String testName, Hashtable<String, String> data) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: executeKeywords()
		 * 
		 * @parameter: String testName, Hashtable<String, String> data
		 * 
		 * @notes: Executes the Keywords as defined in the Master Xslx "Test Steps" Sheet and takes screenshots for any Test Step failure. The test case execution is asserted for any failure in actions and the script execution continues of at all there are some failures in
		 * verifications.
		 * 
		 * @returns: No return type
		 * 
		 * @END
		 */
		System.out.println(": =========================================================");
		APP_LOGS.debug(": =========================================================");
		System.out.println(": Executing---" + testName + " Test Case");
		APP_LOGS.debug(": Executing---" + testName + " Test Case");

		String keyword = null;
		String objectKeyFirst = null;
		String objectKeySecond = null;
		String dataColVal = null;
		GTestName = testName;
		String links_highlight_true = null;
		String links_highlight_false = null;
		String links_on_action = null;

		for (int rNum = 2; rNum <= xls.getRowCount("Test Steps"); rNum++) {
			if (testName.equals(xls.getCellData("Test Steps", "TCID", rNum))) {

				keyword = xls.getCellData("Test Steps", "Keyword", rNum);
				objectKeyFirst = xls.getCellData("Test Steps", "FirstObject", rNum);
				objectKeySecond = xls.getCellData("Test Steps", "SecondObject", rNum);
				dataColVal = xls.getCellData("Test Steps", "Data", rNum);
				String result = null;

				if (keyword.equals("OpenBrowser"))// It is not a keyword, it is
													// a supportive method
					result = OpenBrowser(dataColVal);

				else if (keyword.equals("Navigate"))
					result = Navigate(dataColVal);

				else if (keyword.equals("NavigateTo"))
					result = NavigateTo(dataColVal);

				else if (keyword.equals("Login"))
					result = Login();

				else if (keyword.equals("InputText"))
					result = InputText(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("InputTextDirectly"))
					result = InputTextDirectly(objectKeyFirst, dataColVal);

				else if (keyword.equals("InputNumber"))
					result = InputNumber(objectKeyFirst, dataColVal);

				else if (keyword.equals("InputTextWithAutoAddress"))
					result = InputTextWithAutoAddress(objectKeyFirst, dataColVal);

				else if (keyword.equals("Click"))
					result = Click(objectKeyFirst);

				else if (keyword.equals("ClickOnElementIfPresent"))
					result = ClickOnElementIfPresent(objectKeyFirst);

				else if (keyword.equals("SelectValueFromDropDownWithAnchorTags"))
					result = SelectValueFromDropDownWithAnchorTags(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("SelectHostStatusAndVerifyColumnData"))
					result = SelectHostStatusAndVerifyColumnData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("SelectValueFromDropDown"))
					result = SelectValueFromDropDown(objectKeyFirst, dataColVal);

				else if (keyword.equals("SelectMonitorStatusAndVerifyColumnData"))
					result = SelectMonitorStatusAndVerifyColumnData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("SelectManageTransferStatusAndVerifyColumnData"))
					result = SelectManageTransferStatusAndVerifyColumnData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("SelectTransferHIstoryExecutionStatusAndVerifyColumnData"))
					result = SelectTransferHIstoryExecutionStatusAndVerifyColumnData(objectKeyFirst, objectKeySecond,
							dataColVal);

				else if (keyword.equals("SendingDatatoInputAndVerifyColumnData"))
					result = SendingDatatoInputAndVerifyColumnData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("SelectRadioButton"))
					result = SelectRadioButton(objectKeyFirst);

				else if (keyword.equals("SelectUnselectCheckbox"))
					result = SelectUnselectCheckbox(objectKeyFirst, dataColVal);

				else if (keyword.equals("Wait"))
					result = Wait(dataColVal);

				else if (keyword.equals("GetText"))
					result = GetText(objectKeyFirst);

				else if (keyword.equals("GetSelectedValueFromDropdown"))
					result = getSelectedValueFromDropdown(objectKeyFirst);

				else if (keyword.equals("VerifyText"))
					result = VerifyText(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyTextDDTdata"))
					result = VerifyTextDDTdata(objectKeyFirst, objectKeySecond, data.get(dataColVal));

				else if (keyword.equals("VerifyTitle"))
					result = VerifyTitle(actTitle, dataColVal);

				else if (keyword.equals("CloseBrowser"))
					result = CloseBrowser();

				else if (keyword.equals("QuitBrowser"))
					result = QuitBrowser();

				else if (keyword.equals("MouseHover"))
					result = MouseHover(objectKeyFirst);

				else if (keyword.equals("MouseHoverAndClick"))
					result = MouseHoverAndClick(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("TestCaseEnds"))
					result = TestCaseEnds();

				else if (keyword.equals("ClearTextField"))
					result = clearTextField(objectKeyFirst);

				else if (keyword.equals("ScrollPageToEnd"))
					result = ScrollPageToEnd(objectKeyFirst);

				else if (keyword.equals("VerifyColumnData"))
					result = VerifyColumnData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyElementPresent"))
					result = VerifyElementPresent(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyTextContains"))
					result = VerifyTextContains(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyTextAttributeValue"))
					result = VerifyTextAttributeValue(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyToolTip"))
					result = VerifyToolTip(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyTextDDTdataContains"))
					result = VerifyTextDDTdataContains(objectKeyFirst, objectKeySecond, data.get(dataColVal));

				else if (keyword.equals("VerifyTitleContains"))
					result = VerifyTitleContains(dataColVal);

				else if (keyword.equals("UploadThroughAutoIT"))
					result = uploadThroughAutoIT();

				else if (keyword.equals("CloseTheChildWindow"))
					result = CloseTheChildWindow();

				else if (keyword.equals("VerifyTableData"))
					result = VerifyTableData(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("ScrollPageToBottom"))
					result = ScrollPageToBottom();

				else if (keyword.equals("UploadFile"))
					result = UploadFile(dataColVal);

				else if (keyword.equals("VerifyFileDownload"))
					result = VerifyFileDownload(dataColVal);

				else if (keyword.equals("DeleteFile"))
					result = DeleteFile(dataColVal);

				else if (keyword.equals("VerifyFileIsDownloaded"))
					result = VerifyFileIsDownloaded();

				else if (keyword.equals("deleteFilesFromFolder"))
					result = deleteFilesFromFolder(dataColVal);

				else if (keyword.equals("GetColumnDataLink"))
					result = GetColumnDataLink(objectKeyFirst, dataColVal);

				else if (keyword.equals("ScrollPageToUp"))
					result = ScrollPageToUp();

				else if (keyword.equals("ScrollElementIntoView"))
					result = ScrollElementIntoView(objectKeyFirst);

				else if (keyword.equals("WaitTillTransferStatusIsReady"))
					result = WaitTillTransferStatusIsReady(objectKeyFirst);

				else if (keyword.equals("VerifyButtonIsDisable"))
					result = VerifyButtonIsDisable(objectKeyFirst);

				else if (keyword.equals("VerifyButtonIsEnable"))
					result = VerifyButtonIsEnable(objectKeyFirst);

				else if (keyword.equals("VerifyCheckBoxIsEnabled"))
					result = VerifyCheckBoxIsEnabled(objectKeyFirst);

				else if (keyword.equals("WaitWhileElementPresent"))
					result = WaitWhileElementPresent(objectKeyFirst);

				else if (keyword.equals("WaitTillElementAppears"))
					result = WaitTillElementAppears(objectKeyFirst);

				else if (keyword.equals("VerifyPrefilledDataFromInputField"))
					result = VerifyPrefilledDataFromInputField(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyFileIsExportedAndSizeIsNotZero"))
					result = VerifyFileIsExportedAndSizeIsNotZero(objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyTableDataResult"))
					result = VerifyTableDataResult(objectKeyFirst, dataColVal);

				else if (keyword.equals("SelectTomorrowDate"))
					result = SelectTomorrowDate(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("DeleteTransfer"))
					result = DeleteTransfer(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyHostActions"))
					result = VerifyHostActions(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyAdminModules"))
					result = VerifyAdminModules(objectKeyFirst);

				else if (keyword.equals("VerifyUserModules"))
					result = VerifyUserModules(objectKeyFirst);

				else if (keyword.equals("VerifySuperUserModules"))
					result = VerifySuperUserModules(objectKeyFirst);

				else if (keyword.equals("VerifySuperUserCheckboxPresentInAdminRole"))
					result = VerifySuperUserCheckboxPresentInAdminRole(objectKeyFirst, dataColVal);

				else if (keyword.equals("TestCaseFail"))
					result = TestCaseFail();

				else if (keyword.equals("VerifySuperUserDefultWidgets"))
					result = VerifySuperUserDefultWidgets(objectKeyFirst);

				else if (keyword.equals("VerifyDefultWidgets"))
					result = VerifyDefultWidgets(objectKeyFirst);

				else if (keyword.equals("UnSelectUserRoles"))
					result = UnSelectUserRoles(objectKeyFirst);

				else if (keyword.equals("SelectUnSelectSftpServeronHosts"))
					result = SelectUnSelectSftpServeronHosts(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("CheckSFTPServerIsPresentonHost"))
					result = CheckSFTPServerIsPresentonHost(objectKeyFirst, dataColVal);

				else if (keyword.equals("SendingInputtoDatabaseLastEditedDateSearchBox"))
					result = SendingInputtoDatabaseLastEditedDateSearchBox(objectKeyFirst);

				else if (keyword.equals("CheckProductLicensesStatusShouldNotNegative"))
					result = CheckProductLicensesStatusShouldNotNegative(objectKeyFirst);

				else if (keyword.equals("VerifyColumData"))
					result = VerifyColumData(objectKeyFirst, dataColVal);

				else if (keyword.equals("IsEmpty"))
					result = IsEmpty(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("ButtonisPresent"))
					result = ButtonisPresent(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifySubmitButtonOnNewProductsPage"))
					result = VerifySubmitButtonOnNewProductsPage(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("GetFileNameFromDownloadFolder"))
					result = GetFileNameFromDownloadFolder(dataColVal);

				else if (keyword.equals("GetProductNamesInstalledonHosts"))
					result = GetProductNamesInstalledonHosts(objectKeyFirst);

				else if (keyword.equals("VerifyNumberofHostsAvailableonAllHostsPage"))
					result = VerifyNumberofHostsAvailableonAllHostsPage(objectKeyFirst);

				else if (keyword.equals("CheckHostsValidatedStatusasYes"))
					result = CheckHostsValidatedStatusasYes(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyProductsCount"))
					result = VerifyProductsCount(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyAlertisPresence"))
					result = VerifyAlertisPresence(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("PageRefresh"))
					result = PageRefresh(dataColVal);

				else if (keyword.equals("SelectTodayDateFromDropDown"))
					result = SelectTodayDateFromDropDown(objectKeyFirst);

				else if (keyword.equals("SelectCurrentMonthFromDropDown"))
					result = SelectCurrentMonthFromDropDown(objectKeyFirst);

				else if (keyword.equals("AddMinutestoCurrentTime"))
					result = AddMinutestoCurrentTime(objectKeyFirst);

				else if (keyword.equals("CaptureCurrentTimeandConverttoTimeZone"))
					result = CaptureCurrentTimeandConverttoTimeZone(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyHostStatusisUP"))
					result = VerifyHostStatusisUP(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyStatus"))
					result = VerifyStatus(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("GetAttributeValue"))
					result = GetAttributeValue(objectKeyFirst);

				else if (keyword.equals("VerifyAccountByEmail"))
					result = VerifyAccountByEmail(objectKeyFirst);

				else if (keyword.equals("VerifyScheduleTime"))
					result = VerifyScheduleTime(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifyURLonNewWindowPage"))
					result = VerifyURLonNewWindowPage(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyLinkisExist"))
					result = VerifyLinkisExist(objectKeyFirst);

				else if (keyword.equals("VerifyMigrateTransferActionNotAvailableForSubjob"))
					result = VerifyMigrateTransferActionNotAvailableForSubjob(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyTransferJobIsDeleted"))
					result = VerifyTransferJobIsDeleted(objectKeyFirst);

				else if (keyword.equals("VerifyMultipleEnginesInstalledonHost"))
					result = VerifyMultipleEnginesInstalledonHost(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("VerifySubJobsStatus"))
					result = VerifySubJobsStatus(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyTextOnNewWindow"))
					result = VerifyTextOnNewWindow(objectKeyFirst, dataColVal);

				else if (keyword.equals("VerifyTransferLogs"))
					result = VerifyTransferLogs(objectKeyFirst);

				else if (keyword.equals("VerifySubJobScheduleTime"))
					result = VerifySubJobScheduleTime(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("SaveTransferNameToNotePad"))
					result = SaveTransferNameToNotePad(objectKeyFirst);

				else if (keyword.equals("GetTransferNameFromNotePad"))
					result = GetTransferNameFromNotePad(objectKeyFirst);

				else if (keyword.equals("VerifyDataAfterClearingGlobalSearchBox"))
					result = VerifyDataAfterClearingGlobalSearchBox(objectKeyFirst);

				else if (keyword.equals("SFTPFTPSCredentials"))
					result = SFTPFTPSCredentials(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("AESEncryptionKeyIsTRUE"))
					result = AESEncryptionKeyIsTRUE(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("SelectUnSelectForcedEncryptionKey"))
					result = SelectUnSelectForcedEncryptionKey(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyLastExectutionTimeAndLogs"))
					result = VerifyLastExectutionTimeAndLogs(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyAlerts"))
					result = VerifyAlerts(objectKeyFirst, objectKeySecond, dataColVal);

				else if (keyword.equals("RemoveHostActionShouldNotDisplayForUserRole"))
					result = RemoveHostActionShouldNotDisplayForUserRole(objectKeyFirst, objectKeySecond);

				else if (keyword.equals("VerifyServerConfigurationOnHost"))
					result = VerifyServerConfigurationOnHost(objectKeyFirst, objectKeySecond, dataColVal);

				System.out.println(": " + result);
				APP_LOGS.debug(": " + result);
				File scrFile = null;
				String screeshotNameArray1[] = testName.split("_");
				String shortTcName = screeshotNameArray1[0];
				String screeshotNameArray2[] = screeshotNameArray1[1].split("_");
				shortTcName = shortTcName + "_" + screeshotNameArray2[0];

				//// ========================== FOR VERIFY
				//// KEYWORDS=======================
				if (keyword.contains("Verify")) {
					//// ============================ IF RESULT IS
					//// FAIL=======================
					if (!result.equals("PASS")) {
						System.out.println("MANISH IN VERIFY..................................................");
						if (highlight == true && captureScreenShot == true) // For UI
																			// Test
																			// cases
																			// Verification
																			// Fail
						{
							try {
								System.out.println(
										"MANISH IN VERIFY WITH H AS T, C AS T..................................................");
								highlightElement(returnElementIfPresent(objectKeyFirst));
								scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
								scrFileName = shortTcName + "--Failed_AT-" + keyword + "-" + objectKeyFirst + "-"
										+ getCurrentTimeForScreenShot() + ".png";
								links_highlight_true = " , For Error Screenshot please refer to this link  : "
										+ "<a href=" + "'" + scrFileName + "'" + ">" + scrFileName + "</a>";
								String filename = SRC_FOLDER2 + Forwardslash + failedDataInText;
								FileWriter fw = new FileWriter(filename, true);
								String tempStr;
								tempStr = shortTcName + "__" + objectKeyFirst + "__" + actText + "__" + globalExpText
										+ "__" + scrFileName;
								fw.write(tempStr + "\r\n");
								fw.close();
								unhighlightElement(returnElementIfPresent(objectKeyFirst));
							} catch (Exception e) {
								Fail = true;
								failedResult = failedResult.concat(result + links_highlight_true + " && ");
							}
							try {
								FileUtils.copyFile(scrFile, new File(screenshotPath + scrFileName));
								test.log(Status.FAIL,
										xls.getCellData("Test Steps", "Keyword", rNum)
												+ " - 1 - keyowrd got failed. -> Actual value : " + GActualValue
												+ " and Expected value :" + GExpectedValue
												+ ", For Error Screenshot please refer to this link  : " + "<a href="
												+ "'" + System.getProperty("user.dir") + "/temp/screenshots/"
												+ scrFileName + "'" + ">" + scrFileName + "</a>")
										.addScreenCaptureFromPath(screenshotPath + scrFileName);
								System.out.println(": Verification failed. Please refer " + scrFileName);
								APP_LOGS.debug(": Verification failed. Please refer " + scrFileName);
								Fail = true;
								failedResult = failedResult.concat(result + links_highlight_true + " && ");
								System.out.println(": On Verification when highlight is True Failed");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						else if (highlight == false && captureScreenShot == true) // For UI Test cases Verification Fail because of Element Not found
						{
							System.out.println(
									"MANISH IN VERIFY WITH H AS F, C AS T..................................................");
							scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); // error
																									// when
																									// highlight
																									// is
																									// not
																									// set
																									// in
																									// VerifyCompleteGetResponse
							scrFileName = shortTcName + "--Failed_AT-" + keyword + "-" + objectKeyFirst + "-"
									+ getCurrentTimeForScreenShot() + ".png";
							links_highlight_false = " , For Error Screenshot please refer to this link  : " + "<a href="
									+ "'" + scrFileName + "'" + ">" + scrFileName + "</a>";
							String filename = SRC_FOLDER2 + Forwardslash + failedDataInText;
							FileWriter fw = new FileWriter(filename, true);
							String tempStr;
							tempStr = testName + "__" + objectKeyFirst + "__" + objectKeyFirst
									+ " Not able to read text. Please check and modify Object Repository or  wait time"
									+ "__" + "" + "__" + scrFileName;
							fw.write(tempStr + "\r\n");
							fw.close();
							Thread.sleep(500);
							FileUtils.copyFile(scrFile, new File(screenshotPath + scrFileName));
							test.log(Status.ERROR,
									"ERROR: " + xls.getCellData("Test Steps", "Keyword", rNum)
											+ "-  2 - keyowrd got failed. For Error Screenshot please refer to this link  : "
											+ "<a href=" + "'" + System.getProperty("user.dir") + "/temp/screenshots/"
											+ scrFileName + "'" + ">" + scrFileName + "</a>")
									.addScreenCaptureFromPath(screenshotPath + scrFileName);
							System.out.println(
									": Unable to Verify, as Web Element Not Found. Please refer " + scrFileName);
							APP_LOGS.debug(": Unable to Verify, as Web Element Not Found. Please refer " + scrFileName);
							Fail = true;
							System.out.println("failedResult BEFORE *********************= " + failedResult);
							System.out.println("result*********************= " + result);
							failedResult = failedResult.concat(result + links_highlight_false + " && ");

							System.out.println("failedResult*********************= " + failedResult);
						}

						/*
						 * For HAR DB and API Test cases. We don't need to highlight and take screenshot
						 */
						else if (highlight == false && captureScreenShot == false) {
							System.out.println(
									"MANISH IN VERIFY WITH H AS F, C AS F..................................................");
							String filename = SRC_FOLDER2 + Forwardslash + failedDataInText;
							FileWriter fw = new FileWriter(filename, true);
							String tempStr;
							tempStr = testName + "__" + objectKeyFirst + "__" + actText + "__" + globalExpText;
							test.log(Status.FAIL,
									xls.getCellData("Test Steps", "Keyword", rNum) + " -  3-  keyowrd got failed");
							fw.write(tempStr + "\r\n");
							fw.close();
							System.out.println(": VERIFICATION failed for HAR, DB or API call .");
							APP_LOGS.debug(": VERIFICATION failed for HAR, DB or API call.");
							Fail = true;
							failedResult = failedResult.concat(result + " && ");

						}
					}
					///// =============================== Creating HTML
					///// VERIFICATION NOTEPAD
					String filename = SRC_FOLDER2 + Forwardslash + verificationSummaryText;
					try {
						FileWriter fw = new FileWriter(filename, true);
						String tempStr = GTestName;
						if (result.equals("PASS")) {
							tempStr += " " + "__" + objectKeyFirst + "__" + keyword + "__" + "Y" + "__" + "-";
						} else {
							tempStr += " " + "__" + objectKeyFirst + "__" + keyword + "__" + "-" + "__" + "Y";
						}
						count++;

						fw.write(tempStr + "\r\n");
						fw.close();
					} catch (Exception e) {
						System.out.println("Error in count of the verification points..");
						e.printStackTrace();
					}
				}

				/////// ================================= FOR
				/////// ACTION=========================
				else {
					if (!result.equals("PASS")) {
						System.out.println("MANISH IN ACTION..................................................");
						if (highlight == false && captureScreenShot == true) // UI
																				// Action
																				// Fail
						{
							scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
							System.out.println(
									"MANISH IN ACTION WITH H AS F, C AS T..................................................");
							scrFileName = shortTcName + "--Failed_AT-" + keyword + "-" + objectKeyFirst + "-"
									+ getCurrentTimeForScreenShot() + ".png";
							links_on_action = " , For Error Screenshot please refer to this link  : " + "<a href=" + "'"
									+ scrFileName + "'" + ">" + scrFileName + "</a>";
							String filename = SRC_FOLDER2 + Forwardslash + failedDataInText;
							FileWriter fw = new FileWriter(filename, true);
							String tempStr;
							tempStr = shortTcName + "__" + objectKeyFirst + "__" + objectKeyFirst
									+ " Did not appeared after waiting " + waitTime
									+ " seconds. Please check the application status or modify Object Repository, Wait time."
									+ "__" + "" + "__" + scrFileName;
							fw.write(tempStr + "\r\n");
							fw.close();
							try {
								FileUtils.copyFile(scrFile, new File(screenshotPath + scrFileName));
								test.log(Status.ERROR,
										"ERROR: " + xls.getCellData("Test Steps", "Keyword", rNum)
												+ "-  4 - keyowrd got failed. For Error Screenshot please refer to this link  : "
												+ "<a href=" + "'" + System.getProperty("user.dir")
												+ "/temp/screenshots/" + scrFileName + "'" + ">" + scrFileName + "</a>")
										.addScreenCaptureFromPath(screenshotPath + scrFileName);
								System.out.println(
										": ACATION failed for UI because of Object Not Found Issue. Please refer "
												+ scrFileName);
								APP_LOGS.debug(": ACTION failed for UI because of Object Not Found Issue. Please refer "
										+ scrFileName);
								Fail = true;
								failedResult = failedResult.concat(result + links_on_action + " && ");
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
							APP_LOGS.debug(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
							Fail = false;
							QuitBrowser();
							driver = null;
							String failedResult1 = failedResult;
							failedResult = "";
							Assert.assertTrue(false, failedResult1);
						} else if (highlight == false && captureScreenShot == false) // DB,
																						// API
																						// Action
																						// Fail
						{
							System.out.println(
									"MANISH IN ACTION WITH H AS F, C AS F..................................................");
							String filename = SRC_FOLDER2 + Forwardslash + failedDataInText;
							test.log(Status.FAIL,
									xls.getCellData("Test Steps", "Keyword", rNum) + " -  5 -  keyowrd got failed");
							FileWriter fw = new FileWriter(filename, true);
							String tempStr;
							tempStr = shortTcName + "__" + objectKeyFirst + "__" + actText + "__" + globalExpText;
							fw.write(tempStr + "\r\n");
							fw.close();
							System.out.println(": ACTION failed for DB or API call .");
							APP_LOGS.debug(": ACTION failed for DB or API call.");
							Fail = true;
							failedResult = failedResult.concat(result + " && ");
						}
					} // last if is closing
				} // first Else is closing. it is of inner IF's
			} // outer If loop is closing
		} // outer For loop is closing t
	}

	// **************************************************************************************************
	// Keywords Definitions
	// ******************************************************************************************************************************

	public String VerifyServerConfigurationOnHost(String firstXpathKey, String secondXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyMigrateTransferActionNotAvailableForSubjob ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Migrate Transfer action is not available for subjob, If it is available fail the testcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bFlag = false;

		try {

			bFlag = returnElementIfPresent(firstXpathKey).isSelected();

			if (bFlag == true) {

				returnElementIfPresent("HOSTS_ALLHOSTS_LINK").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ACTIONS_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(20000);
				String actText = returnElementIfPresent("HOSTS_FTPS_TESTPASSED_MATCH").getText();
				expText = expText.trim();
				if (actText.compareTo(expText) == 0) {
					System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
					APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
					test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
				} else {
					globalExpText = expText;
					highlight = true;
					captureScreenShot = true;
					System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
					test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
					return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
				}
			} else {
				System.out.println("server is not configured on Host");
				APP_LOGS.debug("server is not configured on Host");
				test.pass("server is not configured on Host");
			}
		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to verify server configured on host";
		}

		test.pass("PASS");
		return "PASS";
	}

	public String RemoveHostActionShouldNotDisplayForUserRole(String firstXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyMigrateTransferActionNotAvailableForSubjob ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Migrate Transfer action is not available for subjob, If it is available fail the testcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bFlag = false;
		System.out.println(
				": Verifying " + inputData + " Role, Remove Host Action is availble in the List of Host Actions");
		APP_LOGS.debug(
				": Verifying " + inputData + " Role, Remove Host Action is availble in the List of Host Actions");
		test.info(": Verifying " + inputData + " Role, Remove Host Action is availble in the List of Host Actions");
		try {
			List<WebElement> ListofActions = returnElementsIfPresent("SUBJOB_MENU_LISTOFACTIONS");
			int count = ListofActions.size();
			for (int i = 0; i < count - 1; i++) {
				String text = ListofActions.get(i).getText();
				Thread.sleep(2000);
				if (inputData.equals("User")) {
					if (!text.equals("Remove Host")) {
						System.out.println(": Remove host action is not avialable for user, hulft and guest roles");
						bFlag = true;
					} else {
						captureScreenShot = true;
						bFlag = false;
						System.out.println(
								": " + inputData + " role, Remove Host Action is avialble in the list of actions");
						APP_LOGS.debug(
								": " + inputData + " role, Remove Host Action is avialble in the list of actions");
						test.info(": " + inputData + " role, Remove Host Action is avialble in the list of actions");
						return "FAIL- : " + inputData + " role, Remove Host Action is avialble in the list of actions";

					}
				} else if (inputData.equals("SuperUser") || inputData.equals("Admin")) {
					if (text.equals("Remove Host")) {
						System.out.println(": Remove host action is avialable for " + inputData + " roles");
						bFlag = true;
					} else {
						captureScreenShot = true;
						bFlag = false;
						System.out.println(
								": " + inputData + " role, Remove Action is not avialble in the list of actions");
						APP_LOGS.debug(
								": " + inputData + " role, Remove Action is not avialble in the list of actions");
						test.info(": " + inputData + " role, Remove Action is not avialble in the list of actions");
						return "FAIL- : " + inputData + " role, Remove Action is not avialble in the list of actions";

					}
				}
			}
		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Verify MigrateTransferAction not avialable for subjob";
		}
		test.pass("PASS");
		return "PASS";
	}

	public String Navigate(String URLKey) throws ATUTestRecorderException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Navigate ()
		 * 
		 * @parameter: String URLKey
		 * 
		 * @notes: Navigate opened Browser to specific URL as metioned in the config details.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		getConfigDetails();
		failedResult = "";
		System.out.println(": Navigating to (" + SUTUrl + ") Site");
		APP_LOGS.debug(": Navigating to (" + SUTUrl + ") Site");
		test.info(" Navigating to (" + SUTUrl + ") Site");
		try {
			if (captureVideoRecording.equals("Yes")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
				Date date = new Date();
				recorder = new ATUTestRecorder(suitrunvideoPath,
						"RECVideo-" + Keywords.tcName + dateFormat.format(date), false);
				System.out.println(": Video Recording Started ");
				APP_LOGS.debug(": Video Recording Started ");
				test.info(" Video Recording Started ");
				recorder.start();
			}
			if (driver != null) {
				Constants.driver.quit();
				OpenBrowser(bType);
				System.out.println(": Driver Handle: " + driver);
				System.out.println(": No Opened Browser Available, Opening New one");
				APP_LOGS.debug(": No Opened Browser Available, Opening New one");
				// OpenBrowser(bType);
				driver.get(SUTUrl);

				/*
				 * String Windowhd = driver.getWindowHandle(); System.out. println(": Browser is Already Opened and same will be used for this TestScript execution" ); APP_LOGS. debug(": Browser is Already Opened and same will be used for this TestScript execution" ); test.
				 * info(" Browser is Already Opened and same will be used for this TestScript execution" ); driver.get(SUTUrl); Thread.sleep(2000); WebDriverWait wait = new WebDriverWait(driver, 3); if (wait.until(ExpectedConditions.alertIsPresent()) != null) {
				 * System.out.println(": Alert Popup is persent"); APP_LOGS.debug(": Alert Popup is persent"); Alert alt = driver.switchTo().alert(); alt.accept(); System.out.println(": Alert Popup is Accepted"); APP_LOGS.debug(": Alert Popup is Accepted");
				 * driver.switchTo().window(Windowhd); }
				 */
			} else {
				System.out.println(": Driver Handle: " + driver);
				System.out.println(": No Opened Browser Available, Opening New one");
				APP_LOGS.debug(": No Opened Browser Available, Opening New one");
				OpenBrowser(bType);
				driver.get(SUTUrl);
			}

		} catch (Exception e) {
			System.out.println(": Alert Exception getMessage: " + e.getMessage());
			if (e.getMessage().contains("Expected condition failed")) {
				System.out.println(": Alert hasn't Appeared");
				return "PASS";
			} else {
				System.out.println(": Alert Exception: " + e.getLocalizedMessage());
				test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
				return "FAIL - Not able to Navigate " + SUTUrl + " Site" + e.getMessage();
			}
		}
		test.pass("Pass");
		return "PASS";
	}

	public String NavigateTo(String URLKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: NavigateTo ()
		 * 
		 * @parameter: String URLKey
		 * 
		 * @notes: Navigate to specific URL as metioned in the Data Coulmn in "Test Steps" Sheet.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		getConfigDetails();

		failedResult = "";
		System.out.println(": Navigating to (" + URLKey + ") Site");
		APP_LOGS.debug(": Navigating to (" + URLKey + ") Site");
		test.info(": Navigating to (" + URLKey + ") Site");
		try {
			if (captureVideoRecording.equals("Yes")) {
				DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh-mm-ss");
				Date date = new Date();
				recorder = new ATUTestRecorder(suitrunvideoPath,
						"RECVideo-" + Keywords.tcName + dateFormat.format(date), false);
				System.out.println(": Video Recording Started ");
				APP_LOGS.debug(": Video Recording Started ");
				recorder.start();
			}
			if (driver != null) {
				System.out.println(": Driver Handle: " + driver);
				System.out.println(": Browser is Already Opened and same will be used for this TestScript execution");
				APP_LOGS.debug(": Browser is Already Opened and same will be used for this TestScript execution");
				driver.get(URLKey);
			} else {
				System.out.println(": Driver Handle: " + driver);
				System.out.println(": No Opened Browser Available, Opening New one");
				APP_LOGS.debug(": No Opened Browser Available, Opening New one");
				OpenBrowser(bType);
				driver.get(URLKey);

			}
		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Navigate " + URLKey + " Site";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String AddMinutestoCurrentTime(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: AddMinutestoCurrentTime ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Add minutes to Current System time
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String CurrentTime = null;
		try {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
			CurrentTime = formatter.format(date);
			System.out.println(CurrentTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, 1);
			String newTime = formatter.format(cal.getTime());
			System.out.println(newTime);
			System.out.println(": Entering " + newTime + " Time in text field");
			APP_LOGS.debug(": Entering " + newTime + " Time in text field");
			test.info(": Entering " + newTime + " Time in text field");
			returnElementIfPresent(firstXpathKey).sendKeys(newTime);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Add  Minutes to Curent Time");
			return "FAIL - Not able to Add  Minutes to Curent Time";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String AESEncryptionKeyIsTRUE(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: AESEncryptionKeyIsTRUE ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verifying AESEncryptionKey for hosts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying AESENCRYPTION Key is TRUE");

		try {
			String EncryptionKey = returnElementIfPresent(firstXpathKey).getText();
			if (EncryptionKey.equals("true")) {
				System.out.println(": AESENCRYPTIONKEY Status is : " + EncryptionKey);
				test.info(": AESENCRYPTIONKEY Status is : " + EncryptionKey);
			} else {
				System.out.println(": AESENCRYPTIONKEY Status is : " + EncryptionKey);
				returnElementIfPresent("HOSTS_EDITHOST_DETAILS").click();
				Thread.sleep(1000);
				returnElementIfPresent("HOSTPROTOCOLOPTIONS_HFTTRNSOPT").click();
				Thread.sleep(1000);
				returnElementIfPresent("EDIT_AESENCRYPTION_YESRADIOBUTTON").click();
				Thread.sleep(3000);
				returnElementIfPresent("TRANSFERREPORTS_REALLYSURE_OK_BUTTON").click();
				Thread.sleep(3000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("PASS");
		return "PASS";
	}

	public String ButtonisPresent(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verifies Accept Button is present on Alert Popup, if Present click on Button, else Accept Button Not present displays already accepted agreement message
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String expText = "Hulft Inc End User License Agreement";
		System.out.println(": Verifying Accept Button is Present on Alert PopUp");
		APP_LOGS.debug(": Verifying Accept Button is Present on Alert PopUp ");
		try {
			List<WebElement> alertWindowPresence = returnElementsIfPresent(firstXpathKey);
			int count = alertWindowPresence.size();
			if (count > 0) {
				System.out.println(": Accept Button is Present on Alert PopUp: ");
				APP_LOGS.debug(": Accept Button is Present on Alert PopUp: ");
				String actText = returnElementIfPresent("ENDUSERLICENSE_AGREEMENT").getText();
				if (actText.compareTo(expText) == 0) {
					System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
					APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				} else {
					globalExpText = expText;
					highlight = true;
					captureScreenShot = true;
					System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
					return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
				}
				Thread.sleep(5000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(5000);
				returnElementIfPresent("ENDTUTORIAL_BUTTON").click();
				Thread.sleep(5000);
			} else {
				System.out.println(": Already accepted agreement");
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Button not present in Alert PopUp";
		}
		return "PASS";
	}

	public String CaptureCurrentTimeandConverttoTimeZone(String firstXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: AddMinutestoCurrentTime ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Capture current time and convert to timezone according to given inputData
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String CurrentTime = null;
		try {
			Thread.sleep(5000);
			Date today = new Date();
			DateFormat df1 = new SimpleDateFormat("ss");
			CurrentTime = df1.format(today);
			int result = Integer.parseInt(CurrentTime);
			if (result >= 50) {
				System.out.println(": Waiting for Next Minute");
				Thread.sleep(10000);
			}
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
			today = new Date();
			String CurrentTime1 = df.format(today);
			System.out.println("Current System Time is : " + CurrentTime1);
			if (inputData.equals("Asia/Colombo")) {
				df.setTimeZone(TimeZone.getTimeZone("Asia/Colombo"));
				String IST = df.format(today);
				System.out.println("Asia/Colombo time is :" + IST);
			} else if (inputData.equals("US/Pacific-New")) {
				df.setTimeZone(TimeZone.getTimeZone("US/Pacific-New"));
				String PacificNew = df.format(today);
				System.out.println("Date in US/Pacific-New : " + PacificNew);
				APP_LOGS.debug("Date in US/Pacific-New : " + PacificNew);
				test.info("Date in US/Pacific-New : " + PacificNew);
			} else if (inputData.equals("America/New_York")) {
				df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				String AmericaNewYork = df.format(today);
				System.out.println("Date in America/newyork Time is: " + AmericaNewYork);

			} else if (inputData.equals("UTC")) {
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				String AmericaNewYork = df.format(today);
				System.out.println("Date in UTC Time is: " + AmericaNewYork);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.add(Calendar.MINUTE, 1);
			String newTime = df.format(cal.getTime());
			System.out.println("Schedule Time is : " + newTime);
			System.out.println(": Entering " + newTime + " Time in text field");
			APP_LOGS.debug(": Entering " + newTime + " Time in text field");
			test.info(": Entering " + newTime + " Time in text field");
			returnElementIfPresent(firstXpathKey).sendKeys(newTime);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Convert Curent Time to given TimeZone");
			return "FAIL - Not able to Convert Curent Time to given TimeZone";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String clearTextField(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: clearTextField ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Clearing Text Field.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			System.out.println(": Clearing Text Field");
			APP_LOGS.debug(": Clearing Text Field");
			test.info(": Clearing Text Field");
			Thread.sleep(1000);
			returnElementIfPresent(firstXpathKey).clear();
		} catch (InterruptedException e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to perform clearTextField: " + e.getLocalizedMessage());
			System.out.println("Not Able to perform clearTextField");
		}
		test.pass("Pass");
		return "PASS";

	}

	public String Click(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Click ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Performs Click action on link, Hyperlink, selections or buttons of a web page.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		System.out.println(": Performing Click action on " + firstXpathKey);
		APP_LOGS.debug(": Performing Click action on " + firstXpathKey);
		test.info(" Performing Click action on " + firstXpathKey);
		highlight = false;
		captureScreenShot = false;
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		try {
			Thread.sleep(1000);
			if (GTestName.contains("HLT")) {
				if (bType.equals("Edge")) {
					System.out.println(": In Edge if for Click ");
					returnElementIfPresent(firstXpathKey).click();
					Thread.sleep(1000);
				} else {
					if (firstXpathKey.equals("HOSTS_CHOOSEFILE_BUTTON")
							|| firstXpathKey.equals("HOSTS_IMPORTHOST_LOCALFILE")
							|| firstXpathKey.equals("LICENSES_CHOOSEFILE_BUTTON")) {
						returnElementIfPresent(firstXpathKey).click();
					} else {
						wait = new WebDriverWait(driver, 20);
						wait.until(ExpectedConditions.elementToBeClickable(returnElementIfPresent(firstXpathKey)));
						executor.executeScript("arguments[0].click();", returnElementIfPresent(firstXpathKey));
						Thread.sleep(1000);
					}
				}
			} else {
				returnElementIfPresent(firstXpathKey).click();
				Thread.sleep(1000);
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Unable to locate " + firstXpathKey
					+ " element, This can be because new code/content deployemnt on AUT. Please check and update OR file");
			return "FAIL - Not able to click on -- " + firstXpathKey + e.getMessage();
		}
		test.pass("Pass");
		return "PASS";
	}

	public String CheckHostsValidatedStatusasYes(String firstXpathKey, String secondXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: CheckHostsValidatedStatusasYes ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String expText
		 * 
		 * @notes: Verify host validated status is yes, if is yes print yes, else perform the steps(click on action button, Yes radio button & Update button
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String ExpectedText = "Host saved successfully!";
		String actText = returnElementIfPresent(firstXpathKey).getText();
		try {
			if (actText.equals(expText)) {
				System.out.println("Host Validated Status is: " + expText);
			} else {
				System.out.println("Host Validated Status is: " + actText);
				returnElementIfPresent("HOSTS_ACTIONS_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ACTIONS_EDITHOST_LIST").click();
				Thread.sleep(2000);
				Actions action = new Actions(driver);
				action.moveToElement(returnElementIfPresent("HOSTS_ISVALIDATED_YES_RADIOBUTTON")).click().build()
						.perform();
				Thread.sleep(2000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(3000);
				String ActualText = returnElementIfPresent("HOSTSAVEDSUCCESSFULLY").getText();
				System.out.println(ActualText);
				if (ActualText.compareTo(ExpectedText) == 0) {
					System.out.println(": Actual is-> " + ActualText + " AND Expected is-> " + ExpectedText);
					APP_LOGS.debug(": Actual is-> " + ActualText + " AND Expected is-> " + ExpectedText);
					returnElementIfPresent("HOSTSAVEDSUCCESSFULLY").click();
				} else {
					globalExpText = ExpectedText;
					highlight = true;
					captureScreenShot = true;
					System.out.println(": Actual is-> " + ActualText + " AND Expected is-> " + ExpectedText);
					APP_LOGS.debug(": Actual is-> " + ActualText + " AND Expected is-> " + ExpectedText);
					return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to get Filname from Folder";
		}
		return "PASS";
	}

	public String CheckSFTPServerIsPresentonHost(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: CheckSFTPServerIsPresentonHost ()
		 * 
		 * @parameter: String firstXpathKey, String expText
		 * 
		 * @notes: Checking SFTP Client and Server, If present pass the method, Otherwise fail the testcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		flag = false;
		System.out.println(": Checking " + expText + " is Present on Host");
		APP_LOGS.debug(": Checking " + expText + " is Present on Host");
		test.info(": Checking " + expText + " is Present on Host");
		try {
			List<WebElement> protocol = returnElementsIfPresent(firstXpathKey);
			int length = protocol.size();
			Thread.sleep(5000);
			for (int i = 0; i <= length - 1; i++) {
				String text = protocol.get(i).getText();
				if (text.equals(expText)) {
					flag = true;
				}
			}
			if (flag == true) {
				System.out.println(expText + " is Avialable");
			} else {
				captureScreenShot = true;
				TestCaseFail();
			}

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println("Not Able to Checking SFTP Server/Client is Present on Host");
			test.fail(" Not Able to Checking SFTP Server/Client is Present on Host");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String CheckProductLicensesStatusShouldNotNegative(String firstXpathKey) throws ATUTestRecorderException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: CheckProductLicensesStatusShouldNotNegative ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Checking All Products License Status should be positive, if it is negative testcase will fail
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			System.out.println(": Checking Products Licenses Available Status as Positive");
			APP_LOGS.debug(": Checking ProductLicenses Available Status as Positive");
			test.info(" Checking ProductLicenses Available Status as Positive");
			Thread.sleep(MID_WAIT);
			List<WebElement> licenses = returnElementsIfPresent(firstXpathKey);
			count = licenses.size();
			for (int i = 0; i <= count - 1; i++) {
				String text = licenses.get(i).getText();
				int status = Integer.parseInt(text);
				if (status >= 0) {
					flag = true;
				}
			}
			if (flag == true) {
				System.out.println(": All Products Licenses Status as Positive");
				APP_LOGS.debug(": All Products Licenses Status as Positive");
				test.pass(" All Products Licenses Status as Positive");
			} else {
				System.out.println(": Product License Status as Negative is Displayed");
				test.pass(" FAIL : Product License Status as Negative is Displayed");
				captureScreenShot = true;
				TestCaseFail();
			}
		} catch (InterruptedException e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to perform CheckLicensesStatusShouldNotNegative method");
			System.out.println("Not Able to perform CheckLicensesStatusShouldNotNegative method");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String ClickOnElementIfPresent(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Click ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Performs Click action on link, Hyperlink, selections or buttons of a web page.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Performing Click action on " + firstXpathKey + " Element if it is Present in WebPage");
		APP_LOGS.debug(": Performing Click action on " + firstXpathKey + " Element if it is Present in WebPage");
		highlight = false;
		captureScreenShot = false;
		try {
			if (isElementPresent(firstXpathKey)) {
				System.out.println(": " + firstXpathKey + " Element is present. Performing Click Action on it.");
				APP_LOGS.debug(": " + firstXpathKey + " Element is present. Performing Click Action on it.");
				returnElementIfPresent(firstXpathKey).click();
			} else {
				System.out.println(": " + firstXpathKey + " Element is Not present in WebPage");
				APP_LOGS.debug(": " + firstXpathKey + " Element is Not present in WebPage");
				captureScreenShot = true;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to click on -- " + firstXpathKey;
		}
		return "PASS";
	}

	public String CloseTheChildWindow() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: dragAndDropByCoordinates (data)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Makes POST request with attached data in form of file saved on HDD( in XMLForLT folder of the framework which contains JSON file) using apache apache library supported HttpRequest and HttpResponse. In dataColValue user must pass file path followed by URL e.g
		 * https://offers.dev.lendingtree.com/formstore/submit-lead.ashx,/ XMLForLT/LT_02_Verify_POST_API_JSON.json
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		System.out.println(": Closing Child Window");
		APP_LOGS.debug(": Closing Child Window");
		highlight = false;
		captureScreenShot = false;
		try {
			String ParentWindow;
			String ChildWindow1;
			Set<String> set = driver.getWindowHandles();
			Iterator<String> it = set.iterator();
			ParentWindow = it.next();
			ChildWindow1 = it.next();
			driver.switchTo().window(ChildWindow1);
			Thread.sleep(2000);
			driver.close();
			driver.switchTo().window(ParentWindow);

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			return "FAIL - Not Able to close Child Window";
		}
		return "PASS";
	}

	public String CloseBrowser() throws ATUTestRecorderException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: CloseBrowser ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Closing the opened Browser after the Test Case Execution.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		// getTextOrValues.clear();
		scriptTableFirstRowData = "";
		System.out.println(": Closing the Browser");
		APP_LOGS.debug(": Closing the Browser");
		test.info(" Closing the Browser");
		try {
			driver.close();
			driver = null;
			if (captureVideoRecording.equals("Yes")) {
				System.out.println(": Video Recording Stopped ");
				APP_LOGS.debug(": Video Recording Stopped ");
				test.info(" Video Recording Stopped ");
				recorder.stop();
				Thread.sleep(SYNC_WAIT);
			}
		} catch (Exception e) {
			test.log(Status.ERROR, "ERROR : Not able to Close Browser");
			return "FAIL - Not able to Close Browser";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String DeleteFile(String path) throws Exception {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: DeleteFile (path)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Deletes file mentions in parameter.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			File file = new File(path);

			if (file.exists()) {
				file.delete();
			}
			Thread.sleep(2000);
			System.out.println(": Deleted a file ");
			APP_LOGS.debug(": Deleted a file ");
			test.info(" Deleted a file ");

		} catch (RuntimeException exception) {
			captureScreenShot = true;
			System.out.println("Error in deleting a file: " + exception.getMessage());
			test.log(Status.ERROR, "ERROR : Error in deleting a file");
			return "FAIL - Error in deleting a file";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String DeleteTransfer(String firstXpathkey, String secondXpathkey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Delete Transfer ()
		 * 
		 * @parameter: String firstXpathKey,String secondXpathkey
		 * 
		 * @notes: Deleting the Transfer on Manage Transfers Page
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;

		try {
			returnElementIfPresent(firstXpathkey).click();
			Thread.sleep(3000);
			returnElementIfPresent("MT_DELETETRANSFER_LIST").click();
			Thread.sleep(3000);
			returnElementIfPresent(secondXpathkey).click();
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Delete the Transfer");
			return "FAIL - Not able to Delete the Transfer";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String deleteFilesFromFolder(String filePath) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: deleteFilesFromFolder (filePath)
		 * 
		 * @parameter: String filePath
		 * 
		 * @notes: Verifying able to delete files from mentioned folder
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Deleting Files from Folder");
		APP_LOGS.debug(": Deleting Files from Folder");
		test.info(" Deleting Files from Folder");
		try {
			String user = System.getProperty("user.name");
			if (filePath.contains("C:\\Users\\{username}\\Downloads")) {
				filePath = filePath.replace("{username}", user);
				System.out.println(filePath);
			}
			File directory = new File(filePath);
			if (directory.isDirectory()) {

				for (int i = 0; i < directory.list().length; i++) {
					File file = new File(directory + "\\" + directory.list()[i]);
					file.delete();
				}

			} else {
				System.out.println("Parent Directory has not anything.");
			}
			System.out.println("Successfully deleted directory : " + filePath);
			APP_LOGS.debug("Successfully deleted directory : " + filePath);
		} catch (Exception ex) {
			System.out.println(
					"Error in deleting contents of the directory : " + filePath + " with exception " + ex.getMessage());
			test.log(Status.ERROR, "ERROR : Error in deleting contents of the directory : " + filePath);
			return "FAIL - Error in deleting contents of the directory : " + filePath;
		}
		test.pass("Pass");
		return "PASS";
	}

	// **************************************************************************************************Keywords
	// Definitions******************************************************************************************************************************

	public String GetAttributeValue(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: GetAttributeValue ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Get the attribute value of the web element of the passed "firstXpathKey" and stores it into a global Hash map "getTextOrValues".
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Getting " + firstXpathKey + " Attribute Value from the Page");
		APP_LOGS.debug(": Getting " + firstXpathKey + " Attribute Value from the Page");
		test.info(" Getting " + firstXpathKey + " Attribute Value from the Page");
		highlight = false;
		captureScreenShot = false;
		try {
			getTextOrValues.put(firstXpathKey, returnElementIfPresent(firstXpathKey).getAttribute("value").trim());
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";

	}

	public String GetText(String firstXpathKey) throws IOException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: GetText ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Get the text of the web element of the passed "firstXpathKey" and stores it into a global Hash map "getTextOrValues".
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Getting " + firstXpathKey + " Text from the Page");
		APP_LOGS.debug(": Getting " + firstXpathKey + " Text from the Page");
		test.info(" Getting " + firstXpathKey + " Text from the Page");
		highlight = false;
		captureScreenShot = false;
		try {
			getTextOrValues.put(firstXpathKey, returnElementIfPresent(firstXpathKey).getText().trim());
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	public String GetTransferNameFromNotePad(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: GetTransferNameFromNotePad ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: getting TransferName from NotePad
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String data = "";
		System.out.println(": Getting Transfer Name from NotePad");
		APP_LOGS.debug(": Getting Transfer Name from NotePad");
		test.info(": Getting Transfer Name from NotePad");
		try {
			br = new BufferedReader(new FileReader(rootPath + "/temp/Inter/tempfile.txt"));
			while ((data = br.readLine()) != null) {
				System.out.println(" Transfer name from NotePad : " + data);
				getTextOrValues.put(firstXpathKey, data);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("PASS");
		return "PASS";
	}

	public String getColumnData(String firstXpathKey, String secondXpathKey) {

		highlight = false;
		captureScreenShot = false;
		String data = "";
		WebElement table = returnElementIfPresent(firstXpathKey);
		List<WebElement> th = table.findElements(By.tagName("th"));
		System.out.println(th.get(1).getText());
		int col_position = 0;
		System.out.println(": Getting " + secondXpathKey + " Column Data from the Table");
		APP_LOGS.debug(": Getting " + secondXpathKey + " Column Data from the Table");
		for (int i = 0; i < th.size(); i++) {
			if ((returnElementIfPresent(secondXpathKey).getText()).equalsIgnoreCase(th.get(i).getText())) {
				col_position = i + 1;
				break;
			}
		}
		List<WebElement> FirstColumns = table.findElements(By.xpath("//tr/td[" + col_position + "]"));
		for (WebElement e : FirstColumns) {
			data = data + e.getText() + ",";
		}
		return data;
	}

	public String GetColumnDataLink(String firstXpathKey, String secondXpathKey) {

		highlight = false;
		captureScreenShot = false;
		String data = "";
		WebElement table = returnElementIfPresent(firstXpathKey);
		System.out.println("Table Tag Name : " + table.getTagName());
		List<WebElement> ul = table.findElements(By.tagName("th"));
		int col_position = 0;
		for (int i = 0; i < ul.size(); i++) {
			System.out.println(": Getting " + secondXpathKey + " Column Data from the Table");
			APP_LOGS.debug(": Getting " + firstXpathKey + " Column Data from the Table");

			if ((returnElementIfPresent(secondXpathKey).getText()).equalsIgnoreCase(ul.get(i).getText())) {
				col_position = i + 1;
				break;
			}
		}

		List<WebElement> FirstColumns = table.findElements(By.xpath("//tr/td[" + col_position + "]"));
		for (WebElement e : FirstColumns) {
			data = data + e.getText() + ",";
		}

		return data;
	}

	public String GetFileNameFromDownloadFolder(String filePath) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: GetFileNameFromDownloadFolder ()
		 * 
		 * @parameter: String filePath
		 * 
		 * @notes: Get the filenames from Folder
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;

		System.out.println(": Getting FileName from Folder");
		APP_LOGS.debug(": Getting FileName from Folder");
		try {
			String user = System.getProperty("user.name");
			if (filePath.contains("C:\\Users\\{username}\\Downloads\\")) {
				filePath = filePath.replace("{username}", user);
				System.out.println(filePath);
			}
			File[] files = new File(filePath).listFiles();
			for (File file : files) {
				if (file.isFile()) {
					filename.add(file.getName());
					System.out.println(filename);
					fileName = filename.toString();
					fileName = fileName.replaceAll("[\\[\\]]", "");
					System.out.println(fileName);
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to get Filname from Folder";
		}
		return "PASS";
	}

	public static Keywords getKeywordsInstance() throws IOException {
		if (keywords == null) {
			keywords = new Keywords();
		}
		return keywords;
	}

	public String getLastTestCaseName() {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: getLastTestCaseName ()
		 * 
		 * @returns: returns last test case name from Master.xlsx file which have runmode Y into any combination
		 * 
		 * @END
		 */
		Xls_Reader x = new Xls_Reader(mastertsmodulePath + "/MasterTSModule.xlsx");
		String suiteType = suitetype;

		if (!suiteType.contains("_") && !suiteType.equalsIgnoreCase("Regression")) {
			int totalRows = x.getRowCount("Test Cases");
			String lastTestCaseName = null;
			String tcType = null;
			String runMode = null;
			for (int i = 1; i <= totalRows; i++) {
				tcType = x.getCellData("Test Cases", 1, i);
				if (tcType.contains(suiteType)) {
					runMode = x.getCellData("Test Cases", 2, i);
					if (runMode.contains("Y")) {
						lastTestCaseName = x.getCellData("Test Cases", 0, i);
					}
				}
			}
			System.out.println("INFO:=> Last Test Case Name is: " + lastTestCaseName);
			return lastTestCaseName;

		} else if (suiteType.contains("_")) {
			String splitArray[] = suiteType.split("_");
			int totalRows = x.getRowCount("Test Cases");
			String lastTestCaseName = null;
			String tcType = null;
			String runMode = null;
			for (int i = 1; i <= totalRows; i++) {
				tcType = x.getCellData("Test Cases", 1, i);
				if (tcType.contains(splitArray[0]) || tcType.contains(splitArray[1])) {
					runMode = x.getCellData("Test Cases", 2, i);
					if (runMode.contains("Y")) {
						lastTestCaseName = x.getCellData("Test Cases", 0, i);
					}
				}
			}
			System.out.println("INFO:=> Last Test Case Name is: " + lastTestCaseName);
			return lastTestCaseName;

		} else {
			System.out.println("INFO:=> This suiteType is " + suiteType);
			int totalRows = x.getRowCount("Test Cases");
			String lastTestCaseName = null;
			String runMode = null;
			for (int i = 1; i <= totalRows; i++) {
				runMode = x.getCellData("Test Cases", 2, i);
				if (runMode.equalsIgnoreCase("Y")) {
					lastTestCaseName = x.getCellData("Test Cases", 0, i);
				}
			}
			System.out.println("INFO:=> Last Test Case Name is: " + lastTestCaseName);
			return lastTestCaseName;
		}
	}

	public String GetProductNamesInstalledonHosts(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: GetFileNameFromDownloadFolder ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Get the List of the Products installed on Hosts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Getting ProductNames installed on Hosts");
		APP_LOGS.debug(": Getting ProductNames installed on Hosts");
		try {
			List<WebElement> table = returnElementsIfPresent(firstXpathKey);
			int length = table.size();
			System.out.println(": List of the products installed on host are ");
			APP_LOGS.debug(": List of the products installed on host are ");
			for (int i = 0; i < length; i++) {
				Thread.sleep(3000);
				String product = table.get(i).getText();
				System.out.println(": " + product);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to get ProductName";
		}
		return "PASS";
	}

	public String getSelectedValueFromDropdown(String firstXpathKey) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: getSelectedValueFromDropdown ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: WebDriver Object focus should move to JavaScript Alerts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": getting value from drop down");
		APP_LOGS.debug(": getting value from drop down");
		highlight = false;
		captureScreenShot = false;
		String value = "";
		try {

			Select sel = new Select(returnElementIfPresent(firstXpathKey));
			value = sel.getFirstSelectedOption().getText();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			captureScreenShot = true;
			return "FAIL - Not able to getting value from drop down";
		}

		return value;
	}

	public String InputText(String firstXpathKey, String secondXpathKey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Input ()
		 * 
		 * @parameter: String firstXpathKey & String inputData
		 * 
		 * @notes: Inputs the value in any edit box. Value is defined in the master xlsx file and is assigned to "inputData" local variable. We cannot perform a data driven testing using the input keyword.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		if (inputData.isEmpty()) {
			System.out.println(": Test Data is Empty, taking this value from Hashmap");
			APP_LOGS.debug(": Test Data is Empty, taking this value from Hashmap");
			test.info(" Test Data is Empty, taking this value from Hashmap");
			inputData = (String) getTextOrValues.get(secondXpathKey);
			System.out.println(": expText " + inputData);
			APP_LOGS.debug(": expText " + inputData);
			if (inputData == null) {
				System.out.println(
						": No Test Data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				APP_LOGS.debug(
						": No Test data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				inputData = returnElementIfPresent(secondXpathKey).getText().trim();
			}
		}

		else {
			if (inputData.equals("ATSNAME") || inputData.equals("AUFName") || inputData.equals("AULName")
					|| inputData.equals("AUName") || inputData.equals("AExUName") || inputData.equals("AExUID")
					|| inputData.equals("AutoTransferReportName") || inputData.equals("AutoHostReportName")
					|| inputData.equals("AutoUserReportName") || inputData.equals("test")
					|| inputData.equals("DemoGroup") || inputData.equals("hulft") || inputData.equals("EditedAExUName")
					|| inputData.equals("AutoUserGroup") || inputData.equals("UserGroupDescription")
					|| inputData.equals("AutoSystemStatusReportName") || inputData.equals("Schedule")
					|| inputData.equals("AutoTaskName") || inputData.equals("AutoNotification")
					|| inputData.equals("username") || inputData.equals("identifier")
					|| inputData.equals("collection")) {
				SimpleDateFormat tsdf = new SimpleDateFormat("ddMMMyyyyHHmmssz");
				java.util.Date tcurDate = new java.util.Date();
				String tstrDate = tsdf.format(tcurDate);
				String tstrActDate = tstrDate.toString();
				if (inputData.equals("ATSNAME")) {
					inputData = inputData + tstrActDate;
					inputData = inputData.toUpperCase();
					System.out.println(": INPUTDATA FOR ATSNAME: " + inputData);
				} else {
					inputData = inputData + tstrActDate;
					System.out.println(": INPUTDATA EXCEPT ATSNAME: " + inputData);
				}
				getTextOrValues.put(firstXpathKey, inputData.trim());
			} else if (inputData.equals("hulft@v2solutions.com") || inputData.equals("EditedEmail@v2solutions.com")
					|| inputData.equals("hulft@mailinator.com")) {
				String searchableString = inputData;
				String keyword = "@";

				int index = searchableString.indexOf(keyword);
				String first = searchableString.substring(0, index);
				String second = searchableString.substring(index, searchableString.length());

				SimpleDateFormat tsdf = new SimpleDateFormat("ddMMMyyyyHHmmssz");
				java.util.Date tcurDate = new java.util.Date();
				String tstrDate = tsdf.format(tcurDate);
				String tstrActDate = tstrDate.toString();
				inputData = first + tstrActDate + second;
				getTextOrValues.put(firstXpathKey, inputData.trim());
			} else if (inputData.equals("SFTP Transfer v1.0 Linux Standard")) {
				inputData = "SFTP Transfer " + "[" + "v1.0" + "]" + " Linux Standard";
			} else if (inputData.equals("Transfer v8.4 Linux Standard")) {
				inputData = "Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
			} else if (inputData.equals("HULFT Transfer v8.4 Linux Standard")) {
				inputData = "HULFT Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
			}
		}

		System.out.println(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		APP_LOGS.debug(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		test.info(" Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		highlight = false;
		captureScreenShot = false;
		try {
			if (GTestName.contains("HLT")) {
				Actions actions = new Actions(driver);
				if (bType.equals("Edge")) {
					System.out.println(": For edge browser");
					returnElementIfPresent(firstXpathKey).click();
					returnElementIfPresent(firstXpathKey).clear();
					for (int i = 0; i < inputData.length(); i++) {
						char eachInvidualCharacter = inputData.charAt(i);
						String indivualLetter = new StringBuilder().append(eachInvidualCharacter).toString();
						actions.sendKeys(indivualLetter);
						actions.build().perform();
						Thread.sleep(50);
					}

					Thread.sleep(100);
					actions.moveByOffset(300, 0).doubleClick();
					actions.build().perform();
					Thread.sleep(1000);
				} else {
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					Thread.sleep(500);
					// System.out.println("Hello");
					actions.doubleClick();
					Thread.sleep(500);
					if (firstXpathKey.equals("TASK_SCRIPTDATA_INPUTFIELD") == false) {
						returnElementIfPresent(firstXpathKey).clear();
					}
					for (int i = 0; i < inputData.length(); i++) {
						char eachInvidualCharacter = inputData.charAt(i);
						String indivualLetter = new StringBuilder().append(eachInvidualCharacter).toString();
						actions.sendKeys(indivualLetter);
						actions.build().perform();
						Thread.sleep(50);
					}

					// Thread.sleep(100);
					actions.moveByOffset(300, 0).doubleClick();
					// actions.build().perform();
					Thread.sleep(500);
				}
			} else {
				returnElementIfPresent(firstXpathKey).sendKeys(inputData);

			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR,
					"ERROR : Not able to enter -- " + inputData + " in " + firstXpathKey + "Field" + e.getMessage());
			return "FAIL - Not able to enter " + inputData + " in " + firstXpathKey + " Field";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String InputTextDirectly(String firstXpathKey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Input ()
		 * 
		 * @parameter: String firstXpathKey & String inputData
		 * 
		 * @notes: Inputs the value in any edit box. Value is defined in the master xlsx file and is assigned to "inputData" local variable. We cannot perform a data driven testing using the input keyword.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		APP_LOGS.debug(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		highlight = false;
		captureScreenShot = false;
		try {

			if (GTestName.contains("HLT")) {
				Actions actions = new Actions(driver);
				actions.moveToElement(returnElementIfPresent(firstXpathKey));
				Thread.sleep(500);
				actions.doubleClick();
				Thread.sleep(500);
				returnElementIfPresent(firstXpathKey).clear();
				actions.sendKeys(inputData);
				actions.build().perform();
				Thread.sleep(100);
				actions.moveByOffset(200, 0).doubleClick();
				actions.build().perform();
				Thread.sleep(500);
			} else {
				returnElementIfPresent(firstXpathKey).sendKeys(inputData);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to enter " + inputData + " in " + firstXpathKey + " Field";
		}
		return "PASS";
	}

	public String InputTextWithAutoAddress(String firstXpathKey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Input ()
		 * 
		 * @parameter: String firstXpathKey & String inputData
		 * 
		 * @notes: Inputs the value in any edit box. Value is defined in the master xlsx file and is assigned to "inputData" local variable. We cannot perform a data driven testing using the input keyword.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		APP_LOGS.debug(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
		highlight = false;
		captureScreenShot = false;
		try {
			if (firstXpathKey.equals("ZIP_CODE_INPUT_FIELD")) {
				Actions actions = new Actions(driver);
				if (bType.equals("Edge")) {
					System.out.println(": For edge browser");
					returnElementIfPresent(firstXpathKey).click();
					returnElementIfPresent(firstXpathKey).clear();
					returnElementIfPresent(firstXpathKey).sendKeys(inputData);
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					Thread.sleep(3000);
					actions.build().perform();
					Thread.sleep(1000);
				} else {
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					System.out.println(": For Normal browser");
					actions.click();
					returnElementIfPresent(firstXpathKey).clear();
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					actions.click();
					actions.sendKeys(inputData);
					actions.build().perform();
					actions.sendKeys(" ");
					actions.doubleClick();
					Thread.sleep(1500);
					actions.build().perform();
					Thread.sleep(1000);
				}
			} else {
				Actions actions = new Actions(driver);
				if (bType.equals("Edge")) {
					System.out.println(": For edge browser");
					returnElementIfPresent(firstXpathKey).click();
					returnElementIfPresent(firstXpathKey).clear();
					returnElementIfPresent(firstXpathKey).sendKeys(inputData);
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					Thread.sleep(5000);
					actions.sendKeys(Keys.ARROW_DOWN);
					actions.sendKeys(Keys.ENTER);
					Thread.sleep(1000);
					actions.moveByOffset(300, 0).doubleClick();
					actions.moveByOffset(300, 0).doubleClick();
					actions.moveByOffset(300, 0).doubleClick();
					actions.build().perform();
					Thread.sleep(2000);
				} else {
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					System.out.println(": For Normal browser");
					actions.click();
					returnElementIfPresent(firstXpathKey).clear();
					actions.moveToElement(returnElementIfPresent(firstXpathKey));
					actions.click();
					actions.sendKeys(inputData);
					actions.build().perform();
					actions.sendKeys(" ");
					actions.doubleClick();
					Thread.sleep(3000);
					actions.sendKeys(Keys.ARROW_DOWN);
					actions.sendKeys(Keys.ENTER);
					actions.build().perform();
					Thread.sleep(2000);
				}

			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to enter " + inputData + " in " + firstXpathKey + " Field";
		}
		return "PASS";
	}

	public String InputNumber(String firstXpathKey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Input ()
		 * 
		 * @parameter: String firstXpathKey & String inputData
		 * 
		 * @notes: Inputs the value in any edit box. Value is defined in the master xlsx file and is assigned to "inputData" local variable. We cannot perform a data driven testing using the input keyword.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			String regex = ".*\\d.*";
			if (inputData.matches(regex)) {
				NumberFormat nf = NumberFormat.getInstance();
				Number number = nf.parse(inputData);
				long lnputValue = number.longValue();
				inputData = String.valueOf(lnputValue);

				System.out.println(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
				APP_LOGS.debug(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
				returnElementIfPresent(firstXpathKey).clear();
				returnElementIfPresent(firstXpathKey).sendKeys(inputData);
			} else {
				System.out.println(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
				APP_LOGS.debug(": Entering: " + '"' + inputData + '"' + " text in " + firstXpathKey + " Field");
				returnElementIfPresent(firstXpathKey).sendKeys(inputData);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to enter " + inputData + " in " + firstXpathKey + " Field";
		}
		return "PASS";
	}

	// =====================================================================================================================================

	public boolean isElementPresentBy(By by) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: isElementPresent ()
		 * 
		 * @parameter: By by
		 * 
		 * @notes: Supported method for finding an element.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			captureScreenShot = true;
			return false;
		}
	}

	public String IsEmpty(String firstXpathKey, String secondXpathkey, String expText) throws InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: IsEmpty ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathkey, String expText
		 * 
		 * @notes: Verify Hulft Agent Insatlled details should be displayed as empty or not on Manage products page
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			List<WebElement> space = returnElementsIfPresent(firstXpathKey);
			int count = space.size();
			System.out.println(count);
			for (int i = 0; i <= count; i++) {
				Thread.sleep(5000);
				String text = space.get(i).getText();
				System.out.println(text);
				if (text.contains(expText)) {
					String ts = returnElementIfPresent(secondXpathkey).getText();
					System.out.println("Action is" + ts);
					Thread.sleep(5000);
					if (ts.isEmpty()) {
						System.out.println(
								"Hulft Agent Insatlled details displayed as empty on Manage products page: " + ts);
						APP_LOGS.debug(
								"Hulft Agent Insatlled details displayed as empty on Manage products page: " + ts);
						return "PASS";
					} else {
						captureScreenShot = true;
						System.out.println(
								"Hulft Agent Insatlled details displayed as not empty on Manage products page: " + ts);
						APP_LOGS.debug(
								"Hulft Agent Insatlled details displayed as not empty on Manage products page: " + ts);
						return "FAIL";
					}
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to perform isEmpty() method";
		}
		return "PASS";
	}

	public String Login() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Login ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Inputs the default login details as mentioned in the "Config  Details" sheet of the master xlsx and performs click action on the login button.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			getConfigDetails();
			System.out.println(": Entering: " + username + " in USERNAME Field");
			APP_LOGS.debug(": Entering: " + username + " in USERNAME Field");
			returnElementIfPresent(GUSER_XPATH).sendKeys(username);
			System.out.println(": PASS");
			APP_LOGS.debug(": PASS");

			System.out.println(": Entering: " + password + " in PASSWORD Field");
			APP_LOGS.debug(": Entering: " + password + " in PASSWORD Field");
			returnElementIfPresent(GPASS_XPATH).sendKeys(password);
			System.out.println(": PASS");
			APP_LOGS.debug(": PASS");

			System.out.println(": Performing Click action on LOGIN");
			APP_LOGS.debug(": Performing Click action on LOGIN");
			returnElementIfPresent(GLOGIN).click();
		} catch (Exception e) {
			captureScreenShot = true;
			APP_LOGS.debug(
					": FAIL - Not able to Loging with " + username + " : Username and " + password + ": Password");
			return ("FAIL - Not able to Loging with " + username + " : Username and " + password + ": Password");
		}
		return "PASS";
	}

	public String MouseHover(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: MouseHoverAndClick ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Performing Mouse hover on " + firstXpathKey);
		APP_LOGS.debug(": Performing Mouse hover on " + firstXpathKey);
		test.info(" Performing Mouse hover on " + firstXpathKey);
		highlight = false;
		captureScreenShot = false;
		try {
			Thread.sleep(2000);
			Actions act = new Actions(driver);
			WebElement root = returnElementIfPresent(firstXpathKey);
			act.moveToElement(root).build().perform();
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to do mouse hover on -- " + firstXpathKey);
			return "FAIL - Not able to do mouse hover on -- " + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	public String MouseHoverAndClick(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: MouseHoverAndClick ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Performs Click action on link, Hyperlink, selections or buttons of a web page.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Performing Mouse hover and Click action on " + firstXpathKey);
		APP_LOGS.debug(": Performing Mouse hover and Click action on " + firstXpathKey);
		highlight = false;
		captureScreenShot = false;
		try {
			Thread.sleep(2000);
			Actions act = new Actions(driver);
			WebElement root = returnElementIfPresent(firstXpathKey);
			act.moveToElement(root).build().perform();
			Thread.sleep(1000);
			returnElementIfPresent(secondXpathKey).click();
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to do mouse hover and click on -- " + firstXpathKey;
		}
		return "PASS";
	}

	public String OpenBrowser(String browserType) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: OpenBrowser ()
		 * 
		 * @parameter: String browserType
		 * 
		 * @notes: Opens Browsers, Sets Timeout parameter and Maximize the Browser
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		getConfigDetails();
		int WaitTime;
		NumberFormat nf = NumberFormat.getInstance();
		Number number = nf.parse(waitTime);
		WaitTime = number.intValue();
		CONFIG_IMPLICIT_WAIT_TIME = WaitTime;
		failedResult = "";
		System.out.println(": Opening: " + bType + " Browser");
		try {

			if (tBedType.equals("DESKTOP")) {
				// ***************** 1. For Desktop Browsers****************/
				if (bType.equals("Chrome")) {
					System.setProperty("webdriver.chrome.driver", chromedriverPath);
					if (GTestName.contains("HLT")) {
						DesiredCapabilities caps = DesiredCapabilities.chrome();
						ChromeOptions options = new ChromeOptions();
						options.addArguments("--allow-running-insecure-content");
						LoggingPreferences logPrefs = new LoggingPreferences();
						logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
						caps.setCapability(ChromeOptions.CAPABILITY, options);
						caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
						driver = new ChromeDriver(caps);
					} else {
						driver = new ChromeDriver();
					}
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();

					
					// please note : below code is for integrating this framework with zap (security testing tool) as localhost port 8090 is configured in zap tool for the same.
					
					// ***************** 1. For Desktop Browsers***************/
					/*
					 * if (bType.equals("Chrome")) { 
					 * System.setProperty("webdriver.chrome.driver", chromedriverPath); 
					 * if (GTestName.contains("HLT")) { 
					 * Proxy proxy = new Proxy(); 
					 * proxy.setHttpProxy("localhost:8090"); 
					 * proxy.setFtpProxy("localhost:8090");
					 * proxy.setSslProxy("localhost:8090"); 
					 * DesiredCapabilities caps = DesiredCapabilities.chrome(); 
					 * ChromeOptions options = new ChromeOptions(); 
					 * options.addArguments("--allow-running-insecure-content"); 
					 * LoggingPreferences logPrefs = new LoggingPreferences();
					 * logPrefs.enable(LogType.PERFORMANCE, Level.ALL); 
					 * caps.setCapability(ChromeOptions.CAPABILITY, options); 
					 * caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs); 
					 * caps.setCapability(CapabilityType.PROXY, proxy); 
					 * driver = new ChromeDriver(caps); } 
					 * else {
					 * driver = new ChromeDriver(); } 
					
					 * 
					 * getBrowserVersion(); 
					 * APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser"); 
					 * driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS); 
					 * driver.manage().window().maximize();
					 */

				} else if (bType.equals("Edge")) {
					System.setProperty("webdriver.edge.driver", edgedriverPath);

					driver = new EdgeDriver();
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				} else if (bType.equals("Edge")) {
					System.setProperty("webdriver.edge.driver", edgedriverPath);

					driver = new EdgeDriver();
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();

				} else if (bType.equals("Mozilla")) {
					System.setProperty("webdriver.gecko.driver", geckodriverPath);

					driver = new FirefoxDriver();
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				} else if (bType.equals("Safari")) {

					driver = new SafariDriver();
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				} else if (bType.equals("IE")) {
					System.setProperty("webdriver.ie.driver", iedriverPath);

					driver = new InternetExplorerDriver();
					getBrowserVersion();
					APP_LOGS.debug(": Opening: " + bTypeVersion + " Browser");
					driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				} else if (bType.equals("Opera")) {
					driver = new OperaDriver();
				} else if (bType.equals("HtmlUnit")) {
					driver = new HtmlUnitDriver(true);
				}
				driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS);

			} else if (tBedType.equals("MOBILE_EMULATION")) {

				if (bType.equals("Chrome")) {
					System.setProperty("webdriver.chrome.driver", chromedriverPath);
					if (GTestName.contains("HLT")) {
						mobileEmulation = new HashMap<String, String>();
						mobileEmulation.put("deviceName", deviceName);
						Map<String, Object> chromeOptions = new HashMap<String, Object>();
						chromeOptions.put("mobileEmulation", mobileEmulation);
						DesiredCapabilities capabilities = DesiredCapabilities.chrome();
						capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
						getBrowserVersion();
						System.out.println(": Opening Mobile Emulator : " + bTypeVersion + " in Chrome Browser");
						APP_LOGS.debug(": Opening Mobile Emulator : " + bTypeVersion + " in Chrome Browser");

						System.out.println(": Launching : " + capabilities.getCapability("chromeOptions"));
						APP_LOGS.debug(": Launching : " + capabilities.getCapability("chromeOptions"));

						driver = new ChromeDriver(capabilities);
					}
				} else {
					System.out.println(
							": The Browser Type: " + bType + " is not valid. Please Enter a valid Browser Type");
					APP_LOGS.debug(": The Browser Type: " + bType + " is not valid. Please Enter a valid Browser Type");
					return "FAIL - The Browser Type: " + bType + " is not valid. Please Enter a valid Browser Type";
				}
			} else {
				System.out.println(": The Browser Type: " + bType + " is not valid. Please Enter a valid Browser Type");
				return "FAIL - The Browser Type: " + bType + " is not valid. Please Enter a valid Browser Type";
			}

		} catch (Exception e) {
			return "FAIL - Not able to Open Browser";
		}
		return "PASS";
	}

	/*
	 * public String VerifyProductsInstalledOnManageProductspage(String firstXpathKey) { highlight=false; captureScreenShot=false; List<WebElement> rows=returnElementsIfPresent(firstXpathKey); try { for(int i=1;i<=3;i++) { String text=rows.get(i).getText();
	 * System.out.println(text); if(text.contains("HULFT Transfer SFTP") && text.contains("Linux")&&text.contains("Installed")&&text.contains( "Remove")) { System.out.println("Product is Installed"); } else if(text.contains("HULFT Transfer") &&
	 * text.contains("Linux")&&text.contains("Installed")&&text.contains( "Remove")) { System.out.println("Product is Installed"); } else if(text.contains("HULFT Integrate") && text.contains("Linux")&&text.contains("Installed")&&text.contains( "Remove")) {
	 * System.out.println("Product is Installed on"); } else { System.out.println("Product is not Installed"); } } } catch (Exception e) { captureScreenShot=true; return "FAIL - Not able to verify products installed on host"; }
	 * 
	 * return "PASS"; }
	 */
	public String PageRefresh(String URLKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: PageRefresh
		 * 
		 * @parameter: no parameter
		 * 
		 * @notes: Refresh the Webpage
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Refreshing to (" + SUTUrl + ") Site ");
		APP_LOGS.debug(": Refreshing to (" + SUTUrl + ") Site ");
		test.info(" Refreshing to (" + SUTUrl + ") Site ");
		try {

			driver.navigate().refresh();
		}

		catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Refresh " + SUTUrl + " Site" + e.getMessage());
			return "FAIL - Not able to Refresh " + SUTUrl + " Site" + e.getMessage();

		}
		test.pass("Pass");
		return "PASS";
	}

	public String QuitBrowser() throws ATUTestRecorderException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: QuitBrowser ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Quits all opened Browsers or Brower instances after the test case Execution.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		getTextOrValues.clear();
		scriptTableFirstRowData = "";
		System.out.println(": Quiting all opened Browsers");
		APP_LOGS.debug(": Quiting all opened Browsers");
		try {
			driver.close();
			driver = null;
			if (captureVideoRecording.equals("Yes")) {
				recorder.stop();
				System.out.println(": Video Recording Stopped ");
				APP_LOGS.debug(": Video Recording Stopped ");
				Thread.sleep(SYNC_WAIT);
			}

		} catch (Exception e) {
			return "FAIL - Not able to Quit all opened Browsers";
		}
		return "PASS";
	}

	private StringBuffer readFile(String filePath) {

		BufferedReader br = null;
		StringBuffer stringBuffer = new StringBuffer();

		try {

			String lineString = null;
			br = new BufferedReader(new java.io.FileReader(filePath));

			while ((lineString = br.readLine()) != null) {
				stringBuffer.append(lineString);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return stringBuffer;
	}

	public String SaveTransferNameToNotePad(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SaveTransferNameToNotePad ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Saving TransferName to NotePad
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		BufferedReader br = null;
		try {
			String transferName = returnElementIfPresent(firstXpathKey).getText();
			System.out.println(": Saving " + transferName + " to NotePad");
			APP_LOGS.debug(": Saving " + transferName + " to NotePad");
			test.info(": Saving " + transferName + " to NotePad");

			File file = new File(rootPath + "/temp/Inter/tempfile.txt");
			if (file.exists()) {
				file.delete();
			}
			fw = new FileWriter(file, true);
			fw.write(transferName + "\r\n");
			fw.close();

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}

		test.pass("PASS");
		return "PASS";
	}

	public String SelectCurrentMonthFromDropDown(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectCurrentMonthFromDropDown ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Selecting Current month From Drop Down
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String CurrentMonth = null;
		try {
			Select sel = new Select(returnElementIfPresent(firstXpathKey));

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("M");
			CurrentMonth = formatter.format(date);
			System.out.println(": Selecting " + CurrentMonth + " From DropDown");
			APP_LOGS.debug(": Selecting " + CurrentMonth + " From DropDown");
			test.info(" Selecting " + CurrentMonth + " From DropDown");
			sel.selectByVisibleText(CurrentMonth);
			Thread.sleep(2000);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Select Today Date from DropDown");
			return "FAIL - Not able to Select Today Date from DropDown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectUnSelectForcedEncryptionKey(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectUnSelectForcedEncryptionKey ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Select or Unselect EncryptionKey for Hosts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			String EncryptionKey = returnElementIfPresent(firstXpathKey).getText();
			if (EncryptionKey.equals("true")) {
				System.out.println(": AESENCRYPTIONKEY Status is : " + EncryptionKey);
				returnElementIfPresent("HOSTS_EDITHOST_DETAILS").click();
				Thread.sleep(1000);
				returnElementIfPresent("HOSTPROTOCOLOPTIONS_HFTTRNSOPT").click();
				Thread.sleep(1000);
				returnElementIfPresent("EDIT_AESENCRYPTION_NORADIOBUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("TRANSFERREPORTS_REALLYSURE_OK_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(5000);
			} else {
				System.out.println(": AESENCRYPTIONKEY Status is : " + EncryptionKey);
				returnElementIfPresent("HOSTS_EDITHOST_DETAILS").click();
				Thread.sleep(1000);
				returnElementIfPresent("HOSTPROTOCOLOPTIONS_HFTTRNSOPT").click();
				Thread.sleep(1000);
				returnElementIfPresent("EDIT_AESENCRYPTION_YESRADIOBUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("TRANSFERREPORTS_REALLYSURE_OK_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(5000);
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}

		test.pass("PASS");
		return "PASS";
	}

	public String SFTPFTPSCredentials(String firstXpathKey, String secondXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SFTPFTPSCredentials ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Displaying only SFTP/FTPS Server Credentials on Choose Credentials
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		ArrayList<String> one = null;
		ArrayList<String> two = null;
		try {
			List<WebElement> transferName = returnElementsIfPresent(firstXpathKey);
			int length = transferName.size();
			for (int i = 0; i <= length - 1; i++) {
				String FTPUname = transferName.get(i).getText();
				one = new ArrayList<String>();
				one.add(FTPUname);
				// System.out.println(one);
			}
			returnElementIfPresent("MANAGETRANSFER_LINK").click();
			Thread.sleep(1000);
			returnElementIfPresent("ADDTRANSFER_BUTTON").click();
			Thread.sleep(1000);
			returnElementIfPresent("TRANSFER_RECEIVE_BUTTON").click();
			Thread.sleep(1000);
			if (inputData.equals("SFTP")) {
				returnElementIfPresent("SFTP_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("SEARCH_SENDHOST_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("SFTP_SENDHOSTSELECT_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("SEARCH_RECEIVEHOST_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("SFTP_RECEIVEHOSTSELECT_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("SFTP_EXTERNALUSERNAME_BUTTON").click();
				Thread.sleep(1000);
			} else if (inputData.equals("FTPS")) {
				returnElementIfPresent("FTPS_PROTOCOL").click();
				Thread.sleep(1000);
				returnElementIfPresent("FTPS_SEARCH_SENDHOST_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("FTPS_SENDHOSTSELECT_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("FTPS_SEARCH_RECEIVEHOST_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("FTPS_RECEIVEHOSTSELECT_BUTTON").click();
				Thread.sleep(1000);
				returnElementIfPresent("FTPS_EXTERNALUSERNAME_BUTTON").click();
				Thread.sleep(1000);
			}

			List<WebElement> Credentials = returnElementsIfPresent(secondXpathKey);
			int length1 = Credentials.size();
			for (int i = 0; i <= length1 - 1; i++) {
				String Name = Credentials.get(i).getText();
				two = new ArrayList<String>();
				two.add(Name);
				// System.out.println(two);
			}

			boolean b = one.equals(two);

			System.out.println(b);

			if (b == true) {
				System.out.println(": Displaying only " + inputData + " Credentials on Choose Credentials");
				APP_LOGS.debug(": Displaying only " + inputData + " Credentials on Choose Credentials");
				test.info(": Displaying only " + inputData + " Credentials on Choose Credentials");
			} else {
				captureScreenShot = true;
				System.out.println(": Not displaying " + inputData + " Credentials on Choose Credentials");
				return "FAIL - Not displaying " + inputData + " Credentials on Choose Credentials";
			}

			// returnElementIfPresent("SEARCH_RECEIVEHOST_BUTTON").click();

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}

		test.pass("PASS");
		return "PASS";
	}

	public String SelectRadioButton(String firstXpathKey) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectRadioButton ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Select Radio Button
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Selecting Radio Button " + firstXpathKey);
		APP_LOGS.debug(": Selecting Radio Button " + firstXpathKey);
		test.info(" Selecting Radio Button " + firstXpathKey + " Button");

		highlight = false;
		captureScreenShot = false;
		try {
			Actions action = new Actions(driver);
			action.moveToElement(returnElementIfPresent(firstXpathKey)).click().build().perform();
			Thread.sleep(2000);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR,
					"ERROR : Not able to click on -- " + firstXpathKey + " Radio button" + e.getLocalizedMessage());
			return "FAIL - Not able to select " + firstXpathKey + " Radio button";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectTomorrowDate(String firstXpathkey, String secondXpathkey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Delete Transfer ()
		 * 
		 * @parameter: String firstXpathKey,String secondXpathkey
		 * 
		 * @notes: Select Tommorow's Date
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String objectIdentifierValue = "";
		String objectArray[] = null;
		Calendar cal = null;
		String date = null;
		try {
			String object = OR.getProperty(secondXpathkey);
			objectArray = object.split("__");
			objectIdentifierValue = objectArray[1].trim();
			if (inputData.equals("Tommorow")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd");
				cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 1);
				date = sdf.format(cal.getTime());
				if (date.equals("01")) {
					returnElementIfPresent(firstXpathkey).click();
					objectIdentifierValue = objectIdentifierValue + "'" + date + "')]";
				} else {
					objectIdentifierValue = objectIdentifierValue + "'" + date + "')]";
				}
			} else if (inputData.equals("DayAfterTommorow")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd");
				cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 2);
				date = sdf.format(cal.getTime());
				if (date.equals("01")) {
					returnElementIfPresent(firstXpathkey).click();
					objectIdentifierValue = objectIdentifierValue + "'" + date + "')]";
				} else {
					objectIdentifierValue = objectIdentifierValue + "'" + date + "')]";
				}
			} else if (inputData.equals("PreviousDays")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd");
				cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, -7);
				date = sdf.format(cal.getTime());
				System.out.println(date);

				int i = Integer.valueOf(date);

				if (i >= 24) {
					returnElementIfPresent(firstXpathkey).click();
					objectIdentifierValue = objectIdentifierValue + "'" + i + "')]";
					System.out.println(objectIdentifierValue);
				} else {
					objectIdentifierValue = objectIdentifierValue + "'" + date + "')]";
				}
			}
			driver.findElement(By.xpath(objectIdentifierValue)).click();
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to selecte Tomorrow Date");
			return "FAIL - Not able to selecte Tomorrow Date";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectUnselectCheckbox(String firstXpathKey, String checkBoxVal) throws InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectUnselectCheckbox ()
		 * 
		 * @parameter: String firstXpathKey, String checkBoxVal
		 * 
		 * @notes: Select or Unselect the checkbox of a webpage as per the value of local variable "chechBoxVal" mentioned in the "Test Steps" sheet in module excel.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Performing Select Unselect action on " + firstXpathKey);
		APP_LOGS.debug(": Setting " + firstXpathKey + " Checkbox Value As " + checkBoxVal);
		test.info(" Setting " + firstXpathKey + " Checkbox Value As " + checkBoxVal);
		highlight = false;
		captureScreenShot = false;
		Thread.sleep(5000);
		try {
			if (checkBoxVal.equals("TRUE")) {
				if (returnElementIfPresent(firstXpathKey).isSelected()) {

				} else {
					returnElementIfPresent(firstXpathKey).click();
				}
			} else {
				if (returnElementIfPresent(firstXpathKey).isSelected()) {
					returnElementIfPresent(firstXpathKey).click();
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR :  Not able to Select Unselect Checkbox-- " + firstXpathKey);
			return "FAIL - Not able to Select Unselect Checkbox-- " + firstXpathKey + " Exception " + e.getMessage();
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectUnSelectSftpServeronHosts(String firstXpathKey, String secondXpathkey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Navigate ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathkey, String expText
		 * 
		 * @notes: Select or Unselect SFTP Server on Hosts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		flag = false;
		System.out.println(": Performing SelectUnSelect SFTP Server");
		APP_LOGS.debug(": Performing SelectUnSelect SFTP Server");
		test.info(" Performing SelectUnSelect SFTP Server");
		try {
			List<WebElement> protocol = returnElementsIfPresent(firstXpathKey);
			int length = protocol.size();
			for (int i = 0; i <= length - 1; i++) {
				String text = protocol.get(i).getText();
				if (text.equals(expText)) {
					flag = true;
				}
			}
			if (flag == true) {

				Thread.sleep(3000);
				System.out.println(": SFTP Server is Present then UnSelect SFTP Server");
				APP_LOGS.debug(": SFTP Server is Present then UnSelect SFTP Server");
				test.info(" SFTP Server is Present then UnSelect SFTP Server");
				returnElementIfPresent(secondXpathkey).click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ACTIONS_EDITHOST_LIST").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_SUPPORTEDPROTOCOLS_CHECKBOX").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_UPDATE_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ALLHOSTS_LINK").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_HOSTNAME_X_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTNAME_TEXTSEARCHBOX").sendKeys("TestHost");
				Thread.sleep(5000);
				Actions act = new Actions(driver);
				WebElement root = returnElementIfPresent("HOSTS_HOSTNAME_MATCHES");
				act.moveToElement(root).build().perform();
				Thread.sleep(5000);
				List<WebElement> protocol1 = returnElementsIfPresent(firstXpathKey);
				int length1 = protocol1.size();
				for (int i = 0; i <= length1 - 1; i++) {
					String text1 = protocol1.get(i).getText();
					if (!(text1.equals(expText))) {
						flag = true;
					}
				}
				if (flag == true) {
					captureScreenShot = true;
					System.out.println(": SFTP Server is Not Available");
					APP_LOGS.debug(": SFTP Server is Not Available");
					test.info(" SFTP Server is Not Available");
				}

			} else {
				System.out.println(": SFTP Server is NOT Present then Select SFTP Server");
				APP_LOGS.debug(": SFTP Server is NOT Present then Select SFTP Server");
				test.info(" SFTP Server is NOT Present then Select SFTP Server");
				Thread.sleep(2000);
				returnElementIfPresent(secondXpathkey).click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ACTIONS_EDITHOST_LIST").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_SUPPORTEDPROTOCOLS_CHECKBOX").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_SFTPSERVEROPTIONS_LINK").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_PORTNUMBER_TEXTFIELD").sendKeys("2222");
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ROOTFOLDER_TEXTFIELD").sendKeys("/home/app");
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_FTPSERVERCREDENTIALS_PLUS_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_SEARCHUSERS_CHECKBOX").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_SEARCHUSERS_SELECT_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_UPDATE_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_ALLHOSTS_LINK").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTS_HOSTNAME_X_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("HOSTNAME_TEXTSEARCHBOX").sendKeys("TestHost");
				Thread.sleep(5000);
				Actions act = new Actions(driver);
				WebElement root = returnElementIfPresent("HOSTS_HOSTNAME_MATCHES");
				act.moveToElement(root).build().perform();
				Thread.sleep(5000);
				List<WebElement> protocol1 = returnElementsIfPresent(firstXpathKey);
				int length1 = protocol1.size();
				for (int i = 0; i <= length1 - 1; i++) {
					String text1 = protocol1.get(i).getText();
					if (text1.equals(expText)) {
						flag = true;
					}
				}
				if (flag == true) {
					captureScreenShot = true;
					System.out.println(": SFTP Server is Available");
					APP_LOGS.debug(": SFTP Server is Available");
					test.info(" SFTP Server is Available");
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to Select or Unselect SFTP Server");
			System.out.println("Not Able to Select or Unselect SFTP Server");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectValueFromDropDown(String firstXpathKey, String inputData) throws Exception {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectValueFromDropDown ()
		 * 
		 * @parameter: String firstXpathKey, String inputData
		 * 
		 * @notes: Selects the "inputData" as mentioned in the module xlsx from the DropDown in a webpage.firstXpathKey would be location of the Dropdown on webpage and dataColVal would be visible text of the dropdown.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Selecting : " + inputData + " from the Dropdown");
		APP_LOGS.debug(": Selecting : " + inputData + " from the Dropdown");
		test.info(" Selecting : " + inputData + " from the Dropdown");
		highlight = false;
		captureScreenShot = false;
		try {
			Select sel = new Select(returnElementIfPresent(firstXpathKey));
			sel.selectByVisibleText(inputData);
			Thread.sleep(2000);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR :  Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectMonitorStatusAndVerifyColumnData(String firstXpathKey, String secondXpathKey,
			String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectValueFromMonitorStatusAndVerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Selects the "inputData" as mentioned in the module xlsx from the DropDown in a webpage.firstXpathKey would be location of the Dropdown on webpage , secondXpathkey is Transfer Monitor Status and dataColVal would be visible text of the dropdown.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			Thread.sleep(5000);
			SelectValueFromDropDown(firstXpathKey, inputData);
			Thread.sleep(MID_WAIT);
			List<WebElement> status = returnElementsIfPresent(secondXpathKey);
			int count1 = status.size();
			List<WebElement> status1 = returnElementsIfPresent("MT_MONITORSTATUS_MATCH_DISABLE");
			int count2 = status1.size();
			if (inputData.equals("Yes")) {
				if (count1 > 0) {
					System.out.println(": Monitor Status is Enabled");
					APP_LOGS.debug(": Monitor Status is Enabled");
					test.info(" Monitor Status is Enabled");
				} else {
					System.out.println(
							": After Selecting " + inputData + " filter option, No search result is displayed ");
					APP_LOGS.debug(": After Selecting " + inputData + " filter option, No search result is displayed ");
					test.info(" After Selecting " + inputData + " filter option, No search result is displayed ");
				}
			} else if (inputData.equals("No")) {
				if (count2 > 0 && count1 == 0) {
					System.out.println(": Monitor Status is Disabled");
					APP_LOGS.debug(": Monitor Status is Disabled");
					test.info(" Monitor Status is Disabled");

				} else {
					System.out.println(
							": After Selecting " + inputData + " filter option, No search result is displayed ");
					APP_LOGS.debug(": After Selecting " + inputData + " filter option, No search result is displayed ");
					test.info(" After Selecting " + inputData + " filter option, No search result is displayed ");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR :  Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectManageTransferStatusAndVerifyColumnData(String firstXpathKey, String secondXpathKey,
			String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectValueFromMonitorStatusAndVerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Selects the "inputData" as mentioned in the module xlsx from the DropDown in a webpage.firstXpathKey would be location of the Dropdown on webpage , secondXpathkey is Manage Transfer Status and dataColVal would be visible text of the dropdown.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		try {
			Thread.sleep(MID_WAIT);
			SelectValueFromDropDown(firstXpathKey, inputData);
			Thread.sleep(MID_WAIT);
			List<WebElement> status = returnElementsIfPresent(secondXpathKey);
			int count1 = status.size();
			if (count1 == 0) {
				Thread.sleep(3000);
				System.out.println("After Selecting " + inputData + " filter option, No search result is displayed");
				APP_LOGS.debug("After Selecting " + inputData + " filter option, No search result is displayed");
				test.info(" After Selecting " + inputData + " filter option, No search result is displayed");
			} else {
				Thread.sleep(3000);
				for (int i = 0; i <= count1 - 1; i++) {
					String sText = status.get(i).getText();
					if (sText.equals("Ready")) {
						bflag = true;
					} else if (sText.contains("Error") || sText.contains("Invalid")) {
						bflag = true;
					} else if (sText.contains("Ready-Trigger")) {
						bflag = true;
					} else if (sText.contains("Ready-Schedule") || sText.contains("Ready-Partial")) {
						bflag = true;
					} else if (sText.contains("Running")) {
						bflag = true;
					} else if (sText.contains("Invalid")) {
						bflag = true;
					} else if (sText.contains("Pending") || sText.contains("Error")) {
						bflag = true;
					} else if (sText.contains("Ready-Partial") || sText.contains("Invalid")) {
						bflag = true;
					} else if (sText.contains("loading")) {
						bflag = true;
					}
				}
				if (bflag == true) {
					System.out.println(
							":  After Selecting " + inputData + " filter option, " + count1 + " results are displayed");
					APP_LOGS.debug(
							":  After Selecting " + inputData + " filter option, " + count1 + " results are displayed");
					test.pass(" After Selecting " + inputData + " filter option, " + count1 + " results are displayed");
				} else {
					System.out.println(
							": Fail because of Other than " + inputData + " Status is present in All Transfers Page ");
					APP_LOGS.debug(
							": Fail because of Other than " + inputData + " Status is present in All Transfers Page ");
					test.fail(" Fail because of Other than " + inputData + " Status is present in All Transfers Page ");
					captureScreenShot = true;
					TestCaseFail();
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectTransferHIstoryExecutionStatusAndVerifyColumnData(String firstXpathKey, String secondXpathKey,
			String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectTransferHIstoryExecutionStatusAndVerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Selects the "inputData" as mentioned in the module xlsx from the DropDown in a webpage.firstXpathKey would be location of the Dropdown on webpage , secondXpathkey is Transfer History Execution Status and dataColVal would be visible text of the dropdown.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		try {
			Thread.sleep(SYNC_WAIT);
			SelectValueFromDropDown(firstXpathKey, inputData);
			Thread.sleep(SYNC_WAIT);
			List<WebElement> status = returnElementsIfPresent(secondXpathKey);
			int count = status.size();
			if (count == 0) {
				Thread.sleep(SYNC_WAIT);
				System.out.println(": sFAIL: After Selecting " + inputData + " filter option, No search result is displayed");
				APP_LOGS.debug(": sFAIL: After Selecting " + inputData + " filter option, No search result is displayed");
				test.info(" sFAIL: After Selecting " + inputData + " filter option, No search result is displayed");
				return "sFAIL: After Selecting " + inputData + " filter option, No search result is displayed";
			} 
			else 
			{
				System.out.println(":  After Selecting " + inputData + " filter option, " + count + " results are displayed");
				APP_LOGS.debug(":  After Selecting " + inputData + " filter option, " + count + " results are displayed");
				test.pass("  After Selecting " + inputData + " filter option, " + count + " results are displayed");
				Thread.sleep(SYNC_WAIT);
				for (int i = 0; i <= count - 1; i++)
				{
					String sText = status.get(i).getText();
					if (sText.equals(inputData)) 
					{
						bflag = true;
					} 
					else 
					{
					System.out.println(": Fail because of Other than " + inputData + " Status is present ");
					APP_LOGS.debug(": Fail because of Other than " + inputData + " Status is present ");
					test.fail(" FAIL: Fail because of Other than " + inputData+ " Status is present ");
					captureScreenShot = true;
					return "FAIL: Fail because of Other than " + inputData+ " Status is present in All Transfers Page ";
					}
				}
			}
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, " ERROR : Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectHostStatusAndVerifyColumnData(String firstXpathKey, String secondXpathKey, String inputData)
			throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectValueFromDropDown ()
		 * 
		 * @parameter: String firstXpathKey, String inputData
		 * 
		 * @notes: Selects the "inputData" as mentioned in the module xlsx from the DropDown in a webpage.firstXpathKey would be location of the Dropdown on webpage and dataColVal would be visible text of the dropdown.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			Thread.sleep(MID_WAIT);
			SelectValueFromDropDown(firstXpathKey, inputData);
			Thread.sleep(MID_WAIT);
			List<WebElement> enable = returnElementsIfPresent(secondXpathKey);
			int count = enable.size();
			if (count == 0) {
				System.out.println("After Selecting " + inputData + " filter option, No search result is displayed");
				APP_LOGS.debug("After Selecting " + inputData + " filter option, No search result is displayed");
				test.info(" After Selecting " + inputData + " filter option, No search result is displayed");
			} else {
				System.out.println(
						":  After Selecting " + inputData + " filter option, " + count + " search result is displayed");
				APP_LOGS.debug(
						":  After Selecting " + inputData + " filter option, " + count + " search result is displayed");
				test.info(" After Selecting " + inputData + " filter option, " + count + " search result is displayed");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, "Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectValueFromDropDownWithAnchorTags(String firstXpathKey, String secondXpathKey) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectValueFromDropDownWithAnchorTags ()
		 * 
		 * @parameter: String firstXpathKey, String inputData
		 * 
		 * @notes: Click the dropdown and click the value from the List(Which contains anchor tags).
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Selecting : " + secondXpathKey + " from the Dropdown");
		APP_LOGS.debug(": Selecting : " + secondXpathKey + " from the Dropdown");
		highlight = false;
		captureScreenShot = false;
		try {
			returnElementIfPresent(firstXpathKey).click();
			returnElementIfPresent(secondXpathKey).click();
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to select " + secondXpathKey + " from the Dropdown";
		}
		return "PASS";
	}

	public String SendingDatatoInputAndVerifyColumnData(String firstXpathKey, String secondXpathKey, String inputData)
			throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SendingDatatoInputAndVerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Enter iputdata in textfield and verify the column data
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		try {
			
			Thread.sleep(SYNC_WAIT);
			InputText(firstXpathKey, secondXpathKey, inputData);
			Thread.sleep(SYNC_WAIT);
			List<WebElement> status = returnElementsIfPresent(secondXpathKey);
			int count = status.size();
			if (count == 0) {
				captureScreenShot = true;
				System.out.println(": After Selecting " + inputData + " filter option, No search result is displayed");
				APP_LOGS.debug(": After Selecting " + inputData + " filter option, No search result is displayed");
				test.info(": After Selecting " + inputData + " filter option, No search result is displayed");
				return "FAIL - After Selecting " + inputData + " filter option, No search result is displayed";
			} else {
				System.out.println(":  After Selecting " + inputData + " filter option, " + count + " results are displayed");
				APP_LOGS.debug(":  After Selecting " + inputData + " filter option, " + count + " results are displayed");
				test.info("After Selecting " + inputData + " filter option, " + count + " results are displayed");
				for (int i = 0; i < count; i++) {
					String data = status.get(i).getText();
					if (data.contains(inputData)) {
						bflag = true;
					} else {
						captureScreenShot = true;
						System.out.println(": Fail because of Other than " + inputData + " search result is displayed ");
						APP_LOGS.debug(": Fail because of Other than " + inputData + " search result is displayed ");
						test.info(": Fail because of Other than " + inputData + " search result is displayed ");
						return "Fail because of Other than " + inputData + " search result is displayed ";
					}
				}
			}

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println("Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String ScrollPageToBottom() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: ScrollPageToBottom ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Scroll The Page to END in terms of what element is passed in firstXpathKey.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Scrolling The Page to END Using END key");
		APP_LOGS.debug(": Scrolling The Page to END Using END key");
		test.info(" Scrolling The Page to END Using END key");
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to Scroll The Page to END Using END key");
			return "FAIL - Not Able to Scroll The Page to END Using END key";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String ScrollPageToEnd(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: ScrollPageToEnd ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Scroll The Page to END in terms of what element is passed in firstXpathKey.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Scrolling The Page to END Using END key");
		APP_LOGS.debug(": Scrolling The Page to END Using END key");
		test.info(" Scrolling The Page to END Using END key");
		highlight = false;
		captureScreenShot = false;
		try {
			WebElement element = returnElementIfPresent(firstXpathKey);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to Scrol The Page to END Using END key");
			return "FAIL - Not Able to Scrol The Page to END Using END key";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String ScrollPageToUp() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: ScrollPageToUp ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Scroll The Page to UP in terms of what element is passed in firstXpathKey.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Scrolling The Page UP ");
		APP_LOGS.debug(": Scrolling The Page UP ");
		test.info(" Scrolling The Page UP ");
		try {
			((JavascriptExecutor) driver).executeScript("scroll(120, 0)");
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not Able to Scroll The Page Up");
			return "FAIL - Not Able to Scroll The Page Up";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String ScrollElementIntoView(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: ScrollElementIntoView ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Scroll page until element is visible on the page where element is passed in firstXpathKey.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Scrolling the page until element visible on the page ");
		APP_LOGS.debug(": Scrolling the page until element visible on the page ");
		test.info(" Scrolling the page until element visible on the page ");
		try {
			WebElement element = returnElementIfPresent(firstXpathKey);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			Thread.sleep(500);
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-250)", "");
			Thread.sleep(500);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR :  Not Able to Scroll The Page: " + e.getMessage());
			return "FAIL - Not Able to Scrol The Page to END Using END key";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SelectTodayDateFromDropDown(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SelectTodayDateFromDropDown ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Selecting Todays date From Drop Down
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String TodayDate = null;
		try {
			Select sel = new Select(returnElementIfPresent(firstXpathKey));

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("d");
			TodayDate = formatter.format(date);
			System.out.println(": Selecting " + TodayDate + " From DropDown");
			APP_LOGS.debug(": Selecting " + TodayDate + " From DropDown");
			test.info(" Selecting " + TodayDate + " From DropDown");
			sel.selectByVisibleText(TodayDate);
			Thread.sleep(2000);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Select Today Date from DropDown");
			return "FAIL - Not able to Select Today Date from DropDown";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String SendingInputtoDatabaseLastEditedDateSearchBox(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: SendinginputtoDatabaseLastEditedDateSearchBox ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Entering Today Date to Database LastEditedSearchBox
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String TodayDate = null;
		try {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			TodayDate = formatter.format(date);
			System.out.println(": Entering " + TodayDate + " in TextField");
			APP_LOGS.debug(": Entering " + TodayDate + " in TextField");
			test.info(" Entering " + TodayDate + " in TextField");
			returnElementIfPresent(firstXpathKey).sendKeys(TodayDate);
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println("Not Able to Enter input to LastEditedDate Database Searchbox");
			test.log(Status.ERROR, "ERROR : Not Able to Enter input to LastEditedDate Database Searchbox");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String TestCaseEnds() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: TestCaseEnds ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Performs necessary actions before concluding the testcase like if testcase has anything fail it will declare by Assert.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": TestCase is Ending");
		APP_LOGS.debug(": TestCase is Ending");
		getTextOrValues.clear();
		scriptTableFirstRowData = "";
		try {
			if (Fail == true) {
				System.out.println(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
				APP_LOGS.debug(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
				test.info(" TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
				highlight = false;
				Fail = false;
				failedResult = "";
				String failedResult1 = failedResult;
				if (captureVideoRecording.equals("Yes")) {
					recorder.stop();
					System.out.println(": Video Recording Stopped As test case completed");
					APP_LOGS.debug(": Video Recording Stopped As test case completed");
					test.info(" Video Recording Stopped As test case completed");
				}
				Thread.sleep(5000);
				Assert.assertTrue(false, failedResult1);
			} else {
				System.out.println(": TEST SCRIPT:=> " + GTestName + " Has PASSED************");
				APP_LOGS.debug(": TEST SCRIPT:=> " + GTestName + " Has PASSED************");
				test.info(" TEST SCRIPT:=> " + GTestName + " Has PASSED************");
				Fail = true;
				failedResult = "";
				String failedResult1 = failedResult;
				if (captureVideoRecording.equals("Yes")) {
					recorder.stop();
					System.out.println(": Video Recording Stopped As test case completed");
					APP_LOGS.debug(": Video Recording Stopped As test case completed");
					test.info(" Video Recording Stopped As test case completed");
				}
				Thread.sleep(5000);
				Assert.assertTrue(true, failedResult1);
				Fail = false;
			}
		} catch (Exception e) {
			test.log(Status.ERROR, "ERROR : Not able to end TC");
			return "FAIL - Not able to end TC";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String TestCaseFail() throws InterruptedException, ATUTestRecorderException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: TestCaseFail
		 * 
		 * @parameter: None
		 * 
		 * @notes: Failing the TestCase if Expected behavior is not Matched
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		System.out.println(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
		APP_LOGS.debug(": TEST SCRIPT:=> " + GTestName + " Has FAILED!!!!!!!!!!!!");
		highlight = false;
		Fail = false;
		failedResult = "";
		String failedResult1 = failedResult;
		if (captureVideoRecording.equals("Yes")) {
			recorder.stop();
			System.out.println(": Video Recording Stopped As test case completed");
			APP_LOGS.debug(": Video Recording Stopped As test case completed");
			Thread.sleep(SYNC_WAIT);
		}
		Assert.assertTrue(false, failedResult1);
		return "PASS";
	}

	public String UnSelectUserRoles(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: UnSelectUserRoles()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Unselcting the user roles checkboxes, if any checkboxes are selected
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		try {
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				if (Role.get(i).isSelected()) {
					Thread.sleep(2000);
					Role.get(i).click();
				}
			}
		}

		catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to VerifySuperUserCheckboxPresentInAdminRole of Any user - ");
			return "FAIL - Not able to VerifySuperUserCheckboxPresentInAdminRole of Any user - ";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String uploadThroughAutoIT() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: dragAndDropByCoordinates (data)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Requires .exe file which is generated by AutoIT.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		System.out.println(": Performing uploading :-> ");
		APP_LOGS.debug(": Performing uploading :-> ");
		highlight = false;
		captureScreenShot = false;
		try {
			Runtime.getRuntime().exec("D://test.exe");
			Thread.sleep(3000);

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			return "FAIL - Not Able to perform uploading :->  ";
		}
		return "PASS";
	}

	public String UploadFile(String fileLocation) throws Exception {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: uploadFile (fileLocation)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Requires filelocation as parameter.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			String user = System.getProperty("user.name");
			/*
			 * if (fileLocation.contains("C:\\Users\\{username}\\Downloads\\")) { fileLocation = fileLocation.replace("{username}", user); fileLocation = fileLocation + fileName; }
			 */
			System.out.println(": Uploading a file from Specified location " + fileLocation);
			APP_LOGS.debug(": Uploading a file from Specified location " + fileLocation);
			test.info(" Uploading a file from Specified location " + fileLocation);
			StringSelection stringSelection = new StringSelection(fileLocation);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_V);
			Thread.sleep(2000);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(2000);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (RuntimeException localRuntimeException) {
			captureScreenShot = true;
			System.out.println("Error in uploading a file from location: " + localRuntimeException.getMessage());
			test.log(Status.ERROR, "ERROR : Error in uploading a file");
			return "FAIL - Error in uploading a file";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyAdminModules(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyAdminModules()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Google Docs Administrator Modules in Direct Component if it matches Continue the rest of the Test Steps otherwise it Fails the execution
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		System.out.println(": Verifying Admin Modules are Matched or not");
		APP_LOGS.debug(": Verifying Admin Modules are Matched or not");
		test.info(" Verifying Admin Modules are Matched or not");
		try {
			String[] admin = { "Hosts", "Users", "User Groups", "External Users" };
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String list1 = Role.get(i).getText();
				for (int k = 0; k < admin.length; k++) {
					if (list1.equals(admin[k])) {
						bflag = true;
					}
				}
			}
			if (bflag == true) {
				System.out.println(": Matched");
				APP_LOGS.debug(": Matched");
				test.pass(" Matched");
			} else {
				captureScreenShot = true;
				System.out.println(": Fail because of new module was added");
				APP_LOGS.debug(": Fail because of new module was added");
				test.fail(" Fail : because of new module was added");
				TestCaseFail();
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Admin Modules");
			return "FAIL - Not able to Verify Admin Modules";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyAlerts(String firstXpathKey, String secondXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyAlerts ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String iputData
		 * 
		 * @notes: Verify the Alerts
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String actText = null;
		String Transfer = null;
		String expText = null;

		try {

			if (inputData.equals("Transfer")) {
				Thread.sleep(2000);
				int count = returnElementsIfPresent(firstXpathKey).size();
				if (count > 0) {
					actText = returnElementIfPresent(firstXpathKey).getText().trim();
					System.out.println(actText);
					returnElementIfPresent(firstXpathKey).click();
					Thread.sleep(2000);
					String Text = returnElementIfPresent("MANAGEALERTS_COUNT").getText().trim();
					String[] parts = Text.split(" ");
					expText = parts[1].trim();
					System.out.println(": Verifying both Alert and bubble count on Transfer history");
					if (actText.compareTo(expText) == 0) {
						System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
						APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
						test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
					} else {
						captureScreenShot = true;
						System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
						test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
						return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
					}
					Thread.sleep(2000);
					returnElementIfPresent("ALERTS_CANCEL_BUTTON").click();
					Transfer = returnElementIfPresent(secondXpathKey).getText().trim();
					System.out.println(": Verifying both Alert and bubble count on Transfer");
					if (Transfer.compareTo(expText) == 0) {
						System.out.println(": Actual is-> " + Transfer + " AND Expected is-> " + expText);
						APP_LOGS.debug(": Actual is-> " + Transfer + " AND Expected is-> " + expText);
						test.pass(" Actual is-> " + Transfer + " AND Expected is-> " + expText);
					} else {
						captureScreenShot = true;
						System.out.println(": Actual is-> " + Transfer + " AND Expected is-> " + expText);
						test.fail(" FAIL : Actual is-> " + Transfer + " AND Expected is-> " + expText);
						return "FAIL - Actual is-> " + Transfer + " AND Expected is-> " + expText;
					}
				} else {
					System.out.println(": Alert is not Present on Webpage");
					APP_LOGS.debug(": Alert is not Present on Webpage");
					test.pass(": Alert is not Present on Webpage");
				}
			} else if (inputData.equals("Hosts")) {
				int count = returnElementsIfPresent(firstXpathKey).size();
				if (count > 0) {
					actText = returnElementIfPresent(firstXpathKey).getText().trim();
					System.out.println(actText);
					Thread.sleep(2000);
					returnElementIfPresent(firstXpathKey).click();
					Thread.sleep(2000);
					int length = returnElementsIfPresent("PRODUCTSDETAIL_COUNT").size();
					System.out.println("length=+length");
					String Text = returnElementIfPresent(secondXpathKey).getText().trim();
					String[] parts = Text.split(" ");
					expText = parts[1].trim();
					System.out.println(expText);
					System.out.println(": Verifying both Alert and bubble count on Hosts");
					if (actText.compareTo(expText) == 0) {
						System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
						APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
						test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
					} else {
						captureScreenShot = true;
						System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
						test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
						return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
					}
					Thread.sleep(2000);
					returnElementIfPresent("ALERTS_CANCEL_BUTTON").click();
				} else {
					System.out.println(": Alert is not Present on Webpage");
					APP_LOGS.debug(": Alert is not Present on Webpage");
					test.pass(": Alert is not Present on Webpage");
				}
			} else if (inputData.equals("Product")) {
				int count = returnElementsIfPresent(firstXpathKey).size();
				if (count > 0) {
					actText = returnElementIfPresent(firstXpathKey).getText().trim();
					System.out.println(actText);

					int pdalcount = Integer.parseInt(actText);
					Thread.sleep(2000);
					returnElementIfPresent(firstXpathKey).click();
					Thread.sleep(2000);
					int logdetails = returnElementsIfPresent(secondXpathKey).size();
					if (logdetails == 1) {
						if (pdalcount == logdetails) {
							System.out.println(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							APP_LOGS.debug(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							test.pass(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
						} else {
							captureScreenShot = true;
							System.out.println(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							test.fail(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							return "FAIL - : Actual is-> " + pdalcount + " AND Expected is-> " + logdetails;
						}
						Thread.sleep(2000);
						returnElementIfPresent("CLOSE_BUTTON").click();
					} else if (logdetails > 1) {
						if (pdalcount == logdetails) {
							System.out.println(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							APP_LOGS.debug(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							test.pass(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
						} else {
							captureScreenShot = true;
							System.out.println(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							test.fail(": Actual is-> " + pdalcount + " AND Expected is-> " + logdetails);
							return "FAIL - : Actual is-> " + pdalcount + " AND Expected is-> " + logdetails;
						}
						Thread.sleep(2000);
						returnElementIfPresent("ALERTS_CANCEL_BUTTON").click();
					}
				} else {
					System.out.println(": Alert is not Present on " + inputData);
				}
			} else if (inputData.equals("Direct")) {

				String pd = returnElementIfPresent(firstXpathKey).getText().trim();
				int pdcount = Integer.parseInt(pd);
				String hc = returnElementIfPresent(secondXpathKey).getText().trim();
				int hccount = Integer.parseInt(hc);

				int dbcount = returnElementsIfPresent("DATABASE_ALERTCOUNT").size();
				System.out.println(dbcount);
				if (dbcount == 0) {
					dbcount = dbcount;
				} else {
					String db = returnElementIfPresent("DATABASE_ALERTCOUNT").getText().trim();
					dbcount = Integer.parseInt(db);
				}
				int temp = pdcount + hccount + dbcount;
				String dirc = returnElementIfPresent("DIRECT_ALERTCOUNT").getText().trim();
				int dircount = Integer.parseInt(dirc);
				if (temp == dircount) {
					System.out.println(": Actual is-> " + temp + " AND Expected is-> " + dircount);
					APP_LOGS.debug(": Actual is-> " + temp + " AND Expected is-> " + dircount);
					test.pass(": Actual is-> " + temp + " AND Expected is-> " + dircount);
				} else {
					captureScreenShot = true;
					System.out.println(": Actual is-> " + temp + " AND Expected is-> " + dircount);
					test.fail(": Actual is-> " + temp + " AND Expected is-> " + dircount);
					return "FAIL - : Actual is-> " + temp + " AND Expected is-> " + dircount;
				}

			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify Alerts");
			return "FAIL - Verify Alerts";
		}

		test.pass("PASS");
		return "PASS";
	}

	public String VerifyAlertisPresence(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyAlertisPresence ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verify Alert is present or not, If present verify view alert notification is present or not on page, otherwise pass the message alert is not displayed
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		List<WebElement> Alert = returnElementsIfPresent(firstXpathKey);
		int count = Alert.size();
		System.out.println("Alert Size= " + count);
		try {
			if (count > 0) {
				System.out.println(": Alert is Displayed");
				APP_LOGS.debug(": Alert is Displayed");
				test.info(" Alert is Displayed");

				String AlertNotificationCount = returnElementIfPresent(firstXpathKey).getText();
				int result = Integer.parseInt(AlertNotificationCount);
				System.out.println("Alert Count on Product=" + result);
				returnElementIfPresent(secondXpathKey).click();

				returnElementIfPresent("HOSTS_VIEWALERT_NOTIFICATON").click();

				/*
				 * List<WebElement> viewAlertNotification = returnElementsIfPresent("PRODUCT_FORM_COUNT"); int length = viewAlertNotification.size(); System.out.println("Length="+length);
				 */
				int viewAlertNotification;
				if (isElementPresentBy(By.xpath("//div[@class='list-group well']"))) {
					viewAlertNotification = returnElementsIfPresent("PRODUCT_UPGRADE_COUNT").size();
					System.out.println("viewAlertNotification: " + viewAlertNotification);
				} else {
					viewAlertNotification = 0;
				}

				int updateProductCount;
				if (isElementPresentBy(By.xpath("//div[@class='list-group well']"))) {
					updateProductCount = returnElementsIfPresent("PRODUCT_UPGRADE_COUNT").size();
					System.out.println("updateProductCount: " + updateProductCount);
				} else {
					updateProductCount = 0;
				}

				int count1 = viewAlertNotification + updateProductCount;
				System.out.println("Total count=" + count1);

				if (result == count1) {
					System.out.println("Both Alert Count and View Notification alert count are same");
					APP_LOGS.debug("Both Alert Count and View Notification alert count are same");
					test.pass(" Both Alert Count and View Notification alert count are same");
					Thread.sleep(5000);
					returnElementIfPresent("PRODUCT_CLOSE_BUTTON").click();
				} else {
					System.out.println("Both Alert Count and View Notification alert count are not same");
					APP_LOGS.debug("Both Alert Count and View Notification alert count are not same");
					test.fail(" FAIL : Both Alert Count and View Notification alert count are not same");
					captureScreenShot = true;
					returnElementIfPresent("PRODUCT_CLOSE_BUTTON").click();
					return "FAIL- Both Alert Count and View Notification alert count are not same";
				}

			} else {
				System.out.println("Alert is not Displayed");

			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to verify Alert is Presence on Webpage");
			return "FAIL - Not able to verify Alert is Presence on Webpage";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyButtonIsDisable(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page and get the tooltip from the element and verifies it with expText.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Button is Disable " + firstXpathKey);
		APP_LOGS.debug(": Verifying Button is Disable  " + firstXpathKey);
		try {
			Thread.sleep(2000);
			Actions act = new Actions(driver);
			WebElement root = returnElementIfPresent(firstXpathKey);
			act.moveToElement(root).build().perform();
			Thread.sleep(2000);

			if (!root.isEnabled()) {
				System.out.println(": Button present in disabled mode : " + root.getText());
				APP_LOGS.debug(": Button present in disabled mode : " + root.getText());
			} else {
				highlight = true;
				captureScreenShot = true;
				System.out.println(
						": Button present in enable mode but expected it should be disbale : " + root.getText());
				return "FAIL - Expected -> Button " + root.getText() + " is disable mode AND Actual it is enable mode ";
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Button not present in disable mode";
		}
		return "PASS";

	}

	public String VerifyButtonIsEnable(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page and get the tooltip from the element and verifies it with expText.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Button is Enable " + firstXpathKey);
		APP_LOGS.debug(": Verifying Button is Enable  " + firstXpathKey);
		try {
			Thread.sleep(2000);
			Actions act = new Actions(driver);
			WebElement root = returnElementIfPresent(firstXpathKey);
			act.moveToElement(root).build().perform();
			Thread.sleep(2000);

			if (root.isEnabled()) {
				System.out.println(": Button present in enable mode : " + root.getText());
				APP_LOGS.debug(": Button present in enable mode : " + root.getText());
			} else {
				highlight = true;
				captureScreenShot = true;
				System.out
						.println(": Button present in disable mode but expected it should be enable" + root.getText());
				return "FAIL - Expected -> Button " + root.getText() + " is enable mode AND Actual it is disable mode ";
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Button not present in enable mode";
		}
		return "PASS";

	}

	public String VerifyColumnData(String firstXpathKey, String secondXpathKey, String expText) {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String expText
		 * 
		 * @notes: Performs the verification of the table data by getting column data from firstXpathKey and secondXpathKey and verify it against the expText or dataColVal.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		try {
			highlight = false;
			captureScreenShot = false;
			String actText = getColumnData(firstXpathKey, secondXpathKey);
			System.out.println(": Verifying Table Data:");
			APP_LOGS.debug(": Verifying Table Data:");

			if (expText.equalsIgnoreCase(actText)) {

				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);

			} else {

				System.out.println("FAIL - Not Able to verify " + actText + " is present or Not-- with " + expText);
				APP_LOGS.debug("FAIL - Not Able to verify " + actText + " is present or Not-- with " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;

			}
		} catch (Exception e) {
			captureScreenShot = true;

			System.out.println("FAIL - Not Able to verify " + actText + " is present or Not-- with " + expText);
			APP_LOGS.debug("FAIL - Not Able to verify " + actText + " is present or Not-- with " + expText);

			return "FAIL - Not Able to verify Element is present or Not--" + firstXpathKey;
		}
		return "PASS";
	}

	public String VerifyColumData(String firstXpathkey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyColumnData ()
		 * 
		 * @parameter: String firstXpathkey
		 * 
		 * @notes: Verify the column data
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Verifying " + inputData + " Column Data");
		APP_LOGS.debug(": Verifying Column Data");
		test.info(" Verifying Column Data");
		highlight = false;
		captureScreenShot = false;
		try {
			Thread.sleep(5000);
			List<WebElement> data = returnElementsIfPresent(firstXpathkey);
			int count = data.size();
			Thread.sleep(5000);
			// System.out.println(count);
			if (count == 0) {
				System.out.println(": After Verifying " + inputData + " Column data, No search result is displayed");
				APP_LOGS.debug(": After Verifying " + inputData + " Column data, No search result is displayed");
				test.info(" After Verifying " + inputData + " Column data, No search result is displayed");
			} else {
				System.out.println(": Verify " + inputData + " Column Data, " + count + " search result is displayed");
				APP_LOGS.debug(": Verify " + inputData + " Column Data, " + count + " search result is displayed");
				test.info(" Verify " + inputData + " Column Data, " + count + " search result is displayed");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify " + inputData + " ColumnData");
			return "FAIL - Not able to Verify " + inputData + " ColumnData";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyDataAfterClearingGlobalSearchBox(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyDataAfterClearingGlobalSearchBox ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verifying once cleared search values on global search then Transfer jobs page should displayed with log details
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying the logs after clearing global serachbox on TransferHistory page");
		APP_LOGS.debug(": Verifying the logs after clearing global serachbox on TransferHistory page");
		test.info(": Verifying the logs after clearing global serachbox on TransferHistory page");
		try {
			int transferName = returnElementsIfPresent(firstXpathKey).size();
			// System.out.println(transferName);
			if (transferName == 0) {
				captureScreenShot = true;
				System.out.println(
						": None of the logs are present after clearing global searchbox on TransferHistory page ");
				return "FAIL- None of the logs are present after clearing global searchbox on TransferHistory page ";
			} else {
				System.out.println(": logs are present after clearing global searchbox on TransferHistory page");
				APP_LOGS.debug(": logs are present after clearing global searchbox on TransferHistory page");
				test.info(": logs are present after clearing global searchbox on TransferHistory page");
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("PASS");
		return "PASS";
	}

	// **************************************************************************************************Keywords
	// Definitions******************************************************************************************************************************
	public String VerifyDefultWidgets(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify the default widgets present on Home Page, if not present testcase will fail
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		try {
			String[] admin = { "Host Resources", "Product Installations", "Transfer Bandwidth", "Bytes Transferred",
					"Error Rates", "Transfer Errors", "Transfer Not Initiated" };
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String list1 = Role.get(i).getText();
				for (int j = 0; j < admin.length; j++) {
					if (list1.equals(admin[j])) {
						flag = true;
						if (flag == true) {
							System.out.println("Matched");
						} else {
							captureScreenShot = true;
							System.out.println(": Default Widgets Are Not present in Home Page");
							APP_LOGS.debug(": Default Widgets Are Not present in Home Page");
							test.info(" Default Widgets Are Not present in Home Page");
							TestCaseFail();

						}
					}

				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Default Widgets");
			return "FAIL - Not able to Verify Default Widgets";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyElementPresent(String firstXpathKey, String expTEXT) throws ParseException {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyElementPresent ()
		 * 
		 * @parameter: String firstXpathKey, String expText
		 * 
		 * @notes: Performs the verification of the table data by getting column data from firstXpathKey and secondXpathKey and verify it against the expText or dataColVal.User can perform negative testing by passing boolean value in dataColVal.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		System.out.println(": Verifying " + firstXpathKey + " Element is Present on the page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Element is Present on the page");
		highlight = false;
		captureScreenShot = false;
		String sElementText = null;
		try {
			String sFlag = "";
			if (isElementPresent(firstXpathKey)) {
				sFlag = "TRUE";
				sElementText = returnElementIfPresent(firstXpathKey).getText();
				if (expTEXT.equals(sFlag)) {
					System.out.println(
							": " + firstXpathKey + " Element is Present on the page and its Value is: " + sElementText);
					APP_LOGS.debug(
							": " + firstXpathKey + " Element is Present on the page and its Value is: " + sElementText);
				}
			} else {
				System.out.println(": " + firstXpathKey + " Element is NOT Present on Page");
				APP_LOGS.debug(": " + firstXpathKey + " Element is NOT Present on Page");
				captureScreenShot = true;
				return "FAIL -  " + firstXpathKey + " Element is NOT Present on the page";
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not Able to verify " + firstXpathKey + " Element is Present on Page or Not--";
		}
		return "PASS";
	}

	public String VerifyFileIsDownloaded() {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyFileIsDownloaded ()
		 * 
		 * @parameter: None
		 * 
		 * @notes: Verifying file is downloaded
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */ highlight = false;
		captureScreenShot = false;
		String directoryName = "C:\\Users\\{username}\\Downloads";
		try {
			File directory = new File(directoryName);
			if (directory.isDirectory()) {
				for (int i = 0; i < directory.list().length; i++) {
					File file = new File(directory + "\\" + directory.list()[i]);
					if (file.getName().contains(".pdf") || file.getName().contains(".xml")
							|| file.getName().contains(".csv")) {
						System.out.println("Successfully executed with file name of : " + file.getName());
					} else {
						System.out.println("directory has files with different file extention or folders.");
					}
				}
			} else {
				System.out.println("It is not a directory.");
			}
			System.out.println("Successfully verified files : " + directoryName);
			APP_LOGS.debug("Successfully verified files : " + directoryName);
		} catch (Exception ex) {
			System.out.println("Error in verifing contents of the directory : " + directoryName + " with exception "
					+ ex.getMessage());
			return "FAIL - Error in verifing contents of the directory : " + directoryName;
		}
		return "PASS";
	}

	public String VerifyFileDownload(String dataColValue) throws Exception {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyFileDownload (dataColValue)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Verifies file mentions in parameter.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		try {
			dataColValue.replaceAll("\\\\", "/");
			File file = new File(dataColValue);
			highlight = false;
			captureScreenShot = false;

			double bytes = file.length();
			double kilobytes = (bytes / 1024);
			String str = String.format("%1.2f", kilobytes);
			kilobytes = Double.valueOf(str);

			if (file.exists() && bytes != 0) {
				System.out.println(": File is downloaded successfull at:-> " + dataColValue + " path");
				APP_LOGS.debug(": File is downloaded successfull at:-> " + dataColValue + " path");
			} else {
				highlight = true;
				System.out.println(": File is not present at the location ");
				APP_LOGS.debug(":  File is not present at the location ");
				return "FAIL - File is not present at the location ";
			}

		} catch (Exception exception) {
			captureScreenShot = true;
			System.out.println("Error in saving a file: " + exception.getMessage());
			return "FAIL - Error in saving a file";
		}

		return "PASS";
	}

	public String VerifyFileIsExportedAndSizeIsNotZero(String secondXpathKey, String dataColValue) throws Exception {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyFileDownload (dataColValue)
		 * 
		 * @parameter: None
		 * 
		 * @notes: Verifies file mentions in parameter.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Entetred in Verify File is Exported and Size is Not zero");
		String tempFileName = "";
		String tempFileDownloadDirectory = "";
		// String objectIdentifierType="";
		String objectIdentifierValue = "";
		String objectArray[] = null;
		try {

			String user = System.getProperty("user.name");
			if (dataColValue.contains("C:\\Users\\{username}\\Downloads\\")) {
				dataColValue = dataColValue.replace("{username}", user);
			}
			String object = OR.getProperty(secondXpathKey);
			objectArray = object.split("__");
			objectIdentifierValue = objectArray[1].trim();
			Thread.sleep(5000);
			if (objectIdentifierValue.contains("ATSNAME")) {
				objectIdentifierValue = fileName;
				// tempFileName = objectIdentifierValue;
			}

			tempFileName = objectIdentifierValue;
			System.out.println(": Verifing '" + tempFileName + "' Exported File is Downloaded properly.");
			APP_LOGS.debug(": Verifing '" + tempFileName + "' Exported File is Downloaded properly.");
			test.info(" Verifying '" + tempFileName + "' Exported File is Downloaded properly.");
			tempFileDownloadDirectory = dataColValue;
			dataColValue = dataColValue + tempFileName;
			dataColValue.replaceAll("\\\\", "/");
			File file = new File(dataColValue);
			double bytes = file.length();
			double kilobytes = (bytes / 1024);
			String str = String.format("%1.2f", kilobytes);
			kilobytes = Double.valueOf(str);

			if (file.exists() && bytes != 0) {
				System.out.println(": " + tempFileName + " file is Exported successfull at:-> '"
						+ tempFileDownloadDirectory + "' Directory and its Size is " + kilobytes + "KB");
				APP_LOGS.debug(": " + tempFileName + " file is Exported successfull at:-> '" + tempFileDownloadDirectory
						+ "' Directoryand and its Size is " + kilobytes + "KB");
				test.pass(" " + tempFileName + " file is Exported successfully at:-> '" + tempFileDownloadDirectory
						+ "' Directoryand and its Size is " + kilobytes + "KB");
			} else {
				System.out.println(": " + tempFileName + " file is NOT Exported at:-> '" + tempFileDownloadDirectory
						+ "' Directory and its Size is " + kilobytes + "KB");
				APP_LOGS.debug(": " + tempFileName + " file is NOT Exported at:-> '" + tempFileDownloadDirectory
						+ "' Directory and its Size is " + kilobytes + "KB");
				test.fail("FAIL : " + tempFileName + " file is NOT Exported as it is not present at:-> '"
						+ tempFileDownloadDirectory + "' Directory.");
				return "FAIL - " + tempFileName + " file is NOT Exported at:-> '" + tempFileDownloadDirectory
						+ "' Directory and its Size is " + kilobytes + "KB";
			}

			if (file.delete()) {
				System.out.println(": After Verification: " + tempFileName + " file is deleted successfully from :-> '"
						+ tempFileDownloadDirectory + "' Directory");
				APP_LOGS.debug(": After Verification: " + tempFileName + " file is deleted successfully from :-> '"
						+ tempFileDownloadDirectory + "' Directory");
				test.info(" After Verification: " + tempFileName + " file is deleted successfully from :-> '"
						+ tempFileDownloadDirectory + "' Directory");
			} else {
				System.out.println("After Verification: " + tempFileName + " file is NOT deleted from :-> '"
						+ tempFileDownloadDirectory + "' Directory. Please check Manually");
				APP_LOGS.debug("After Verification: " + tempFileName + " file is NOT deleted from :-> '"
						+ tempFileDownloadDirectory + "' Directory. Please check Manually");
				test.info("After Verification: " + tempFileName + " file is NOT deleted from :-> '"
						+ tempFileDownloadDirectory + "' Directory. Please check Manually");
			}

		} catch (Exception exception) {
			System.out.println(": Error in saving a file: " + exception.getMessage());
			test.log(Status.ERROR,
					"ERROR : " + tempFileName + " file is NOT Exported at:-> '" + tempFileDownloadDirectory
							+ "' Directory and got following error message=> " + exception.getMessage());
			return "FAIL - " + tempFileName + " file is NOT Exported at:-> '" + tempFileDownloadDirectory
					+ "' Directory and got following error message=> " + exception.getMessage();
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyHostActions(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyHostActions(String,String)
		 * 
		 * @parameter: String firstXpathKey,String expText
		 * 
		 * @notes: Verify Host Actions with Expected Text(ex: View Host, Edit Host) if it Matches then go and click on that Action(ex: View Host, Edit Host)
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifing '" + firstXpathKey + "' on Host Action");
		APP_LOGS.debug(": Verifing '" + firstXpathKey + "' on Host Action");
		test.info(" Verifing '" + firstXpathKey + "' on Host Action");
		try {
			List<WebElement> list = returnElementsIfPresent("HOSTS_LISTS");
			for (int i = 0; i <= list.size() - 1; i++) {
				String action = list.get(i).getText();
				if (action.equals(expText)) {
					System.out.println("Matched");
					returnElementIfPresent(firstXpathKey).click();
					break;
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Host Actions");
			return "FAIL - Not able to Verify Host Actions";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyHostStatusisUP(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyHostStatusisUP ()
		 * 
		 * @parameter: String firstXpathKey, String expText
		 * 
		 * @notes: Verifying the Host Status is up or down, if it is up continue the execution, otherwise fail the teatcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String actText = returnElementIfPresent(firstXpathKey).getText();
		try {
			if (actText.equals(expText)) {
				System.out.println(": Host Status is : " + actText);
				APP_LOGS.debug(": Host Status is : " + actText);
				test.info(": Host Status is : " + actText);
			} else {
				captureScreenShot = true;
				System.out.println(": Host Status is : " + actText);
				APP_LOGS.debug(": Host Status is : " + actText);
				test.info(": Host Status is : " + actText);
				// TestCaseFail();
				return "FAIL - Host Status is : " + actText;
			}

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": Not able to verify Host Status ");
			APP_LOGS.debug(": Not able to verify Host Status ");
			test.log(Status.ERROR, "ERROR : Not able to verify Host Status");
			return "FAIL - Not able to Verify Host Status";
		}
		test.pass("PASS");
		return "PASS";
	}

	public String VerifyLastExectutionTimeAndLogs(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyLastExectutionTimeAndLogs ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verify LastExecution Time and Logs for FTPS job
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			int time = returnElementsIfPresent(secondXpathKey).size();
			if (time >= 1) {
				System.out.println(": Job is run and verify the logs");
				returnElementIfPresent("ACTIONS_BUTTON").click();
				Thread.sleep(2000);
				returnElementIfPresent("MT_VIEWLOGS_LIST").click();
				Thread.sleep(2000);
				returnElementIfPresent("TRANSFERLOGS_STATUS_MATCH").click();
				Thread.sleep(2000);
				returnElementIfPresent("SENDERLOG_TASKDETAILS_DATE_CLICK").click();
				Thread.sleep(2000);
				VerifyTransferLogs(firstXpathKey);
				Thread.sleep(3000);
				returnElementIfPresent("MANAGETRANSFER_LINK").click();
				Thread.sleep(2000);
			} else {
				System.out.println(": FTPS job is not run");
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + secondXpathKey);
			return "FAIL - Not able to read text from " + secondXpathKey;
		}

		test.pass("PASS");
		return "PASS";
	}

	public String VerifyLinkisExist(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyLinkisExist ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify link is exists on WebPage
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		System.out.println(": Verifying " + firstXpathKey + " link is Exist on a Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " link is Exist on a Page");
		test.info(": Verifying " + firstXpathKey + " link is Exist on a Page");
		try {
			if (returnElementsIfPresent(firstXpathKey).size() > 0) {
				System.out.println(" : " + firstXpathKey + " link is found");
				APP_LOGS.debug(" : " + firstXpathKey + " link is found");
				test.pass(" : " + firstXpathKey + " link is found");
			} else {
				System.out.println(": Submit a request link is not found");
				APP_LOGS.debug(": Submit a request link is not found");
				test.pass(": Submit a request link is not found");

			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify Link is Exist");
			return "FAIL - Verify Link is Exist";
		}

		test.pass("Pass");
		return "PASS";
	}

	public String VerifyMigrateTransferActionNotAvailableForSubjob(String firstXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyMigrateTransferActionNotAvailableForSubjob ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Migrate Transfer action is not available for subjob, If it is available fail the testcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bFlag = false;
		System.out.println(": Verifying " + inputData + " Action is availble in the List of Transfer actions");
		APP_LOGS.debug(": Verifying " + inputData + " Action is availble in the List of Transfer actions");
		test.info(": Verifying " + inputData + " Action is availble in the List of Transfer actions");
		try {
			List<WebElement> ListofActions = returnElementsIfPresent(firstXpathKey);
			int count = ListofActions.size();
			for (int i = 0; i < count - 1; i++) {
				String text = ListofActions.get(i).getText();
				if (inputData.equals("Migrate")) {
					if (!text.equals("Migrate Transfer")) {
						bFlag = true;
					}
				} else if (inputData.equals("Run")) {
					if (!text.equals("Run Transfer")) {
						bFlag = true;
					}
				} else {
					captureScreenShot = true;
					bFlag = false;
					System.out.println(": " + inputData + " Action is avialble in the list of actions");
					APP_LOGS.debug(": " + inputData + " Action is avialble in the list of actions");
					test.info(": " + inputData + " Action is avialble in the list of actions");
					return "FAIL- :" + inputData + " Action is avialble in the list of actions";
				}
			}
		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Verify MigrateTransferAction not avialable for subjob";
		}
		test.pass("PASS");
		return "PASS";
	}

	public String VerifyMultipleEnginesInstalledonHost(String firstXpathKey, String secondXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyMultipleEnginesInstalledonHost ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Verify transfer job is deleted or not.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Multiple Engines Install on Host " + inputData);
		APP_LOGS.debug(": Verifying Multiple Engines Install on Host " + inputData);
		test.info(": Verifying Multiple Engines Install on Host " + inputData);
		try {
			Thread.sleep(5000);
			List<WebElement> installatedproducts = returnElementsIfPresent(firstXpathKey);
			int count = installatedproducts.size();
			Thread.sleep(3000);
			returnElementIfPresent("HOST_TRANSFERHOST_LINK").click();
			Thread.sleep(25000);
			List<WebElement> transferoptions = returnElementsIfPresent(secondXpathKey);
			int length = transferoptions.size();
			if (length == count) {
				System.out.println(": Multiple Engines are Installed on Host " + inputData);
				APP_LOGS.debug(": Multiple Engines are Installed on Host " + inputData);
				test.info(": Multiple Engines are Installed on Host " + inputData);
			} else {
				captureScreenShot = true;
				System.out.println(": Multiple Engines aren't Installed on Same Host " + inputData);
				APP_LOGS.debug(": Multiple Engines are Installed on Host " + inputData);
				return "FAIL- Multiple Engines aren't Installed on Same Host " + inputData;
			}

		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Verify MultipleEnginesInstalled on Host";
		}

		test.pass("PASS");
		return "PASS";
	}

	public String VerifyNumberofHostsAvailableonAllHostsPage(String firstXpathKey) throws InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyNumberofHostsAvailableonAllHostsPage ()
		 * 
		 * @parameter: String firstXpathKey,String expText
		 * 
		 * @notes: Verifying hosts column data, if host count is 3 pass the testcase, otherwise fail the testcase.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		int expText = 3;
		System.out.println(": Verifying Number of Hosts Available on All Hosts Page");
		APP_LOGS.debug(": Verifying Number of Hosts Available on All Hosts Page");
		List<WebElement> table = returnElementsIfPresent(firstXpathKey);
		int size = table.size();
		Thread.sleep(5000);
		try {
			if (expText == size) {
				System.out.println(": " + expText + " Hosts Are Available ");
				APP_LOGS.debug(": " + expText + " Hosts Are Available ");
			} else {
				captureScreenShot = true;
				System.out.println(": " + expText + " Hosts Are Not Available, Create new instance");
				APP_LOGS.debug(": " + expText + " Hosts Are Not Available, Create new instance");
				TestCaseFail();

			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to get Filname from Folder";
		}

		return "PASS";
	}

	public String VerifyProductsCount(String firstXpathKey, String secondXpathKey) {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyProductsCount ()
		 * 
		 * @parameter: String firstXpathKey,String secondXpathKey
		 * 
		 * @notes: Verify the Host Product count and Products Sku count same or not
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String actText = returnElementIfPresent(firstXpathKey).getAttribute("value");
		List<WebElement> Products = returnElementsIfPresent(secondXpathKey);
		int size = Products.size();
		String expText = Integer.toString(size);
		try {
			if (expText.compareTo(actText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL: Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to verify products count");
			return "FAIL - Not able to verify products count";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyPrefilledDataFromInputField(String firstXpathKey, String expText) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyPrefilledDataFromInputField ()
		 * 
		 * @parameter: None
		 * 
		 * @notes:
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		try {
			expText = expText.trim();
			returnElementIfPresent(firstXpathKey).sendKeys(Keys.chord(Keys.CONTROL, "a"));
			returnElementIfPresent(firstXpathKey).sendKeys(Keys.chord(Keys.CONTROL, "c"));
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			String str = (String) transferable.getTransferData(DataFlavor.stringFlavor);
			actText = str;

			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to get value from Input Field");
			return "FAIL - Not able to get value from Input Field";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyScheduleTime(String firstXpathKey, String secondXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyScheduleTime ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verifying the both Scheduled time and Last Execution Time
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String ExecutionTime = null;
		System.out.println(": Verify Scheduling Time With " + inputData + " Timezone");
		APP_LOGS.debug(": Verify Scheduling Time With " + inputData + " Timezone");
		test.info(": Verify Scheduling Time With " + inputData + " Timezone");

		try {
			Thread.sleep(3000);
			String dateString = returnElementIfPresent("MT_SCH_LET_DATE").getText();
			String timeString = returnElementIfPresent(firstXpathKey).getText();
			String dateTimeString = dateString + " " + timeString;
			DateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
			Date date = formatter.parse(dateTimeString);
			DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			Date date1 = formatter.parse(dateTimeString);

			// String date1=formatter1.format(dateTimeString);
			// System.out.println(date1);
			// DateFormat formatter1 = new SimpleDateFormat("hh:mm a");

			if (inputData.equals("Asia/Colombo")) {
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("Asia/Colombo time : " + ExecutionTime);
			} else if (inputData.equals("US/Pacific-New")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("US/Pacific-New"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("US/Pacific-New Time : " + ExecutionTime);
			} else if (inputData.equals("America/New_York")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("Pacific time : " + ExecutionTime);
			} else if (inputData.equals("UTC")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("UTC time : " + ExecutionTime);
			}

			String actText = ExecutionTime.trim();
			Thread.sleep(2000);
			returnElementIfPresent("ACTIONS_BUTTON").click();
			Thread.sleep(2000);
			returnElementIfPresent("MANAGETRANSFERS_VIEWTRANSFER").click();
			Thread.sleep(2000);
			returnElementIfPresent("SENDPARAMS_TAB").click();
			Thread.sleep(3000);
			String expText = returnElementIfPresent(secondXpathKey).getText().trim();
			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				// test.fail(" FAIL : Actual is-> " + actText + " AND Expected
				// is-> " + expText);
				// return "FAIL - Actual is-> " + actText + " AND Expected is->
				// " + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("PASS");
		return "PASS";
	}

	public String VerifyStatus(String firstXpathKey, String secondXpathKey, String inputData)
			throws InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyStatus ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String inputData
		 * 
		 * @notes: Selecting Transfer jobs status in dropdowns and verify column data
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		// System.out.println(": Verifying Transfer Jobs Status");
		// APP_LOGS.debug(": Verifying Transfer Jobs Status");
		// test.info(": Verifying Transfer Jobs Status");
		try {
			String[] Status = inputData.split(",");
			Select sel = new Select(returnElementIfPresent(firstXpathKey));
			sel.selectByVisibleText(Status[0]);
			for (int i = 1; i <= Status.length - 1; i++) {
				Select sel1 = new Select(returnElementIfPresent(secondXpathKey));
				Thread.sleep(2000);
				sel1.selectByVisibleText(Status[i]);
				Thread.sleep(3000);
				System.out.println(": Selecting " + Status[0] + " - " + Status[i] + " from Job status dropdown");
				APP_LOGS.debug(": Selecting " + Status[0] + " - " + Status[i] + " from Job status dropdown");
				test.info(": Selecting " + Status[0] + " - " + Status[i] + " from Job status dropdown");
				List<WebElement> mt = returnElementsIfPresent("MT_STATUS_ROW");
				int count = mt.size();
				Thread.sleep(3000);
				if (count == 0) {
					System.out.println("After Selecting " + Status[0] + " - " + Status[i]
							+ " filter option, No search result is displayed");
					APP_LOGS.debug("After Selecting " + Status[0] + " - " + Status[i]
							+ " filter option, No search result is displayed");
					test.info(" After Selecting " + Status[0] + " - " + Status[i]
							+ " filter option, No search result is displayed");
				} else {

					System.out.println("After Selecting " + Status[0] + " - " + Status[i] + " filter option, " + count
							+ " search results are displayed");
					APP_LOGS.debug("After Selecting " + Status[0] + " - " + Status[i] + " filter option, " + count
							+ " search results are displayed");
					test.info("After Selecting " + Status[0] + " - " + Status[i] + " filter option, " + count
							+ " search results are displayed");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to select " + inputData + " from the Dropdown");
			return "FAIL - Not able to select " + inputData + " from the Dropdown";
		}

		test.pass("PASS");
		return "PASS";
	}

	// **************************************************************************************************Keywords
	// Definitions******************************************************************************************************************************

	public String VerifySubJobsStatus(String firstXpathKey, String secondXpathKey) {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyMultipleEnginesInstalledonHost ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verify the Subjob Status on all transfers page
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String actText = null;
		String expText = null;
		int readyCount = 0;
		int pendingCount = 0;
		int errorCount = 0;
		System.out.println(": Verifying Sub Jobs status on All Transfers Page");
		APP_LOGS.debug(": Verifying Sub Jobs status on All Transfers Page");
		test.pass(": Verifying Sub Jobs status on All Transfers Page");
		try {
			List<WebElement> SubJobStatus = returnElementsIfPresent(firstXpathKey);
			int count = SubJobStatus.size();
			System.out.println(count);
			for (int i = 0; i <= count - 1; i++) {
				Thread.sleep(2000);
				actText = SubJobStatus.get(i).getText();
				if (actText.equals("Ready")) {
					readyCount++;
				} else if (actText.equals("Pending")) {
					pendingCount++;
				} else if (actText.equals("Error")) {
					errorCount++;
				}

			}

			if (readyCount >= 2) {
				actText = "Ready";
				returnElementIfPresent("MANAGETRANSFER_LINK").click();
			} else if (pendingCount >= 2) {
				actText = "Pending";
				returnElementIfPresent("MANAGETRANSFER_LINK").click();
			} else if (errorCount >= 2) {
				actText = "Error";
				returnElementIfPresent("MANAGETRANSFER_LINK").click();
			} else if (readyCount == 1 && pendingCount == 1 && errorCount == 1) {
				actText = "Ready";
				returnElementIfPresent("MANAGETRANSFER_LINK").click();
			}

			Thread.sleep(5000);
			expText = returnElementIfPresent(secondXpathKey).getText().trim();
			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
			}

		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Verify MultipleEnginesInstalled on Host";
		}

		test.pass("PASS");
		return "PASS";
	}

	public String VerifySubmitButtonOnNewProductsPage(String firstXpathKey, String secondXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verifies Submit Button is present on Add New Product Modal Dialog Box, if Present click on Submit Button, else click on Close button.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Submit Button is Present on Add New Product Modal Dialog Box");
		APP_LOGS.debug(": Verifying Submit Button is Present on Add New Product Modal Dialog Box");
		test.info(" Verifying Submit Button is Present on Add New Product Modal Dialog Box");
		try {

			List<WebElement> products = returnElementsIfPresent(firstXpathKey);
			int count = products.size();
			if (count == 0) {
				Thread.sleep(3000);
				returnElementIfPresent("PRODUCTS_CLOSE_BUTTON").click();

			} else {
				Thread.sleep(3000);
				returnElementIfPresent("PRODUCTS_CHECKALL").click();
				Thread.sleep(3000);
				returnElementIfPresent(secondXpathKey).click();
				Thread.sleep(15000);
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Button not present in Add New Product Modal Dialog Box");
			return "FAIL - Button not present in Add New Product Modal Dialog Box";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifySubJobScheduleTime(String firstXpathKey, String secondXpathKey, String inputData) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyScheduleTime ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey
		 * 
		 * @notes: Verifying the both Scheduled time and Last Execution Time
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		String ExecutionTime = null;
		System.out.println(": Verify Scheduling Time With " + inputData + " Timezone");
		APP_LOGS.debug(": Verify Scheduling Time With " + inputData + " Timezone");
		test.info(": Verify Scheduling Time With " + inputData + " Timezone");

		try {
			Thread.sleep(3000);
			String dateString = returnElementIfPresent("MT_SCH_LET_DATE").getText();
			String timeString = returnElementIfPresent(firstXpathKey).getText();
			String dateTimeString = dateString + " " + timeString;
			DateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
			Date date = formatter.parse(dateTimeString);
			DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			Date date1 = formatter.parse(dateTimeString);

			// String date1=formatter1.format(dateTimeString);
			// System.out.println(date1);
			// DateFormat formatter1 = new SimpleDateFormat("hh:mm a");

			if (inputData.equals("Asia/Colombo")) {
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("Asia/Colombo time : " + ExecutionTime);
			} else if (inputData.equals("US/Pacific-New")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("US/Pacific-New"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("US/Pacific-New Time : " + ExecutionTime);
			} else if (inputData.equals("America/New_York")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("Pacific time : " + ExecutionTime);
			} else if (inputData.equals("UTC")) {
				formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
				ExecutionTime = formatter1.format(date1).toLowerCase();
				System.out.println("UTC time : " + ExecutionTime);
			}

			String actText = ExecutionTime.trim();
			Thread.sleep(2000);
			returnElementIfPresent("ACTIONS_BUTTON").click();
			Thread.sleep(2000);
			returnElementIfPresent("MANAGETRANSFERS_VIEWTRANSFER").click();
			Thread.sleep(2000);
			returnElementIfPresent("JOBS_VIEW_BUTTON").click();
			Thread.sleep(2000);
			returnElementIfPresent("SENDPARAMS_TAB").click();
			Thread.sleep(3000);
			String expText = returnElementIfPresent(secondXpathKey).getText().trim();
			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				// test.fail(" FAIL : Actual is-> " + actText + " AND Expected
				// is-> " + expText);
				// return "FAIL - Actual is-> " + actText + " AND Expected is->
				// " + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR: Not able to read text from " + firstXpathKey);
			return "FAIL - Not able to read text from " + firstXpathKey;
		}
		test.pass("PASS");
		return "PASS";
	}

	public String VerifySuperUserDefultWidgets(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyColumnData ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify the default widgets present on Home Page for superuser role of any user, if not present testcase will fail
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying SuperUser Default Widgets");
		APP_LOGS.debug(": Verifying SuperUser Default Widgets");
		test.info(" Verifying SuperUser Default Widgets");

		try {
			String[] admin = { "Getting Started", "Configuration Status", "Host Resources", "Product Installations",
					"Transfer Bandwidth", "Bytes Transferred", "Error Rates", "Transfer Errors",
					"Transfer Not Initiated" };
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String list1 = Role.get(i).getText();
				for (int j = 0; j < admin.length; j++) {
					if (list1.equals(admin[j])) {
						flag = true;
						if (flag == true) {
							System.out.println("Matched");
						} else {
							captureScreenShot = true;
							System.out.println(": Default Widgets Are Not present in Home Page");
							APP_LOGS.debug(": Default Widgets Are Not present in Home Page");
							test.fail(" FAIL: Default Widgets Are Not present in Home Page");
							TestCaseFail();
						}
					}
				}
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify SuperUser Default Widgets");
			return "FAIL - Not able to Verify SuperUser Default Widgets";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifySuperUserModules(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifySuperUserModules()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Google Docs SuperUser Modules in Direct Component if it matches Continue the rest of the Test Steps otherwise it Fails the execution
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		try {
			System.out.println(": Verifying SuperUser Modules are Matched or not");
			APP_LOGS.debug(": Verifying SuperUser Modules are Matched or not");
			test.info(" Verifying SuperUser Modules are Matched or not");

			String[] SuperUser = { "External Users", "Hosts", "Licenses", "Products", "Users", "User Groups",
					"Configure", "Archive Logs", "Database" };
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String list1 = Role.get(i).getText();
				for (int k = 0; k < SuperUser.length; k++) {
					if (list1.equals(SuperUser[k])) {
						bflag = true;
					}
				}
			}
			if (bflag == true) {
				System.out.println(": Matched");
				APP_LOGS.debug(": Matched");
				test.pass(" Matched");
			} else {
				captureScreenShot = true;
				System.out.println(": Fail because of new module was added");
				APP_LOGS.debug(": Fail because of new module was added");
				test.fail(" Fail: because of new module was added");
				TestCaseFail();
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Admin Modules");
			return "FAIL - Not able to Verify Admin Modules";
		}
		test.pass("Pass");
		return "PASS";
	}

	// **************************************************************************************************Keywords
	// Definitions******************************************************************************************************************************

	public String VerifySuperUserCheckboxPresentInAdminRole(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifySuperUserModules()
		 * 
		 * @parameter: String firstXpathKey, String expText
		 * 
		 * @notes: Verify Google Docs SuperUser Modules, Superuser checkbox was present or not.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying SuperUserCheckBox is Present in Any user of Admin Role");
		APP_LOGS.debug(": Verifying SuperUserCheckBox is Present in Any user of Admin Role");
		test.info(" Verifying SuperUserCheckBox is Present in Any user of Admin Role");
		try {
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String text = Role.get(i).getText();
				if (text.equals(expText)) {
					captureScreenShot = true;
					System.out.println(": Fail because of SuperUser Checkbox was Present in User Roles");
					APP_LOGS.debug(": Fail because of SuperUser Checkbox was Present in User Roles");
					test.fail(" FAIL :Fail because of SuperUser Checkbox was Present in User Roles");
					TestCaseFail();
				} else {
					System.out.println(
							": PASS because of SuperUser Checkbox was not Present in User Roles, Continue the Rest of the TestSteps");
					APP_LOGS.debug(
							": PASS because of SuperUser Checkbox was not Present in User Roles, Continue the Rest of the TestSteps");
					test.pass(
							" PASS : because of SuperUser Checkbox was not Present in User Roles, Continue the Rest of the TestSteps");
				}

			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to VerifySuperUserCheckboxPresentInAdminRole of Any user - ");
			return "FAIL - Not able to VerifySuperUserCheckboxPresentInAdminRole of Any user - ";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyTableData(String firstXpathKey, String secondXpathKey, String expText) {

		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTableData ()
		 * 
		 * @parameter: String firstXpathKey, String secondXpathKey, String expText
		 * 
		 * @notes: Performs the verification of the table data by getting column data from firstXpathKey and secondXpathKey and verify it against the expText or dataColVal.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {

			String actText = getColumnData(firstXpathKey, secondXpathKey);
			String[] inter = actText.split(",");
			for (int i = 0; i < inter.length; i++) {
				System.out.println("Values : " + inter[i]);
			}
			System.out.println("Total " + inter.length + " records found.");
			for (int i = 0; i < inter.length; i++) {
				if (inter[i].contains(expText)) {
					System.out.println(": Actual is-> " + inter[i] + " AND Expected is-> " + expText);
					APP_LOGS.debug(": Actual is-> " + inter[i] + " AND Expected is->" + expText);

				} else {
					highlight = true;
					System.out.println(": Actual is-> " + inter[i] + " AND Expected is-> " + expText);
					return "FAIL - Actual is-> " + inter[i] + " AND Expected is->" + expText;
				}
			}

		} catch (Exception ex) {
			System.out.println("Unable to match data with table data.");
			APP_LOGS.debug("Unable to match data with table data ");
			return "FAIL - Not able to match data with table data.";
		}
		return "PASS";
	}

	public String VerifyTableDataResult(String firstXpathkey, String inputData) throws Exception {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTableDataResult ()
		 * 
		 * @parameter: String firstXpathkey
		 * 
		 * @notes: Count the Number of Users in a Page
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		try {
			List<WebElement> NumberOfUsers = returnElementsIfPresent(firstXpathkey);
			count = NumberOfUsers.size();
			if (count > 1) {
				System.out.println(
						": Number Of Results displayed on the first page of " + inputData + " table is : " + (count));
				APP_LOGS.debug(
						": Number Of Results displayed on the first page of " + inputData + " table is : " + (count));
				test.pass(" Number Of Results displayed on the first page of " + inputData + " table is : " + (count));
				Thread.sleep(5000);
			} else if (inputData.equals("transfer-b")) {
				if (count <= 0) {
					System.out.println(": Number Of Results displayed on the first page of " + inputData
							+ " table is : " + (count));
					APP_LOGS.debug(": Number Of Results displayed on the first page of " + inputData + " table is : "
							+ (count));
					test.pass(" Number Of Results displayed on the first page of " + inputData + " table is : "
							+ (count));
					return "FAIL - Number Of Results displayed on the first page of " + inputData + " table is : "
							+ count;

				} else {
					System.out.println(": Number Of Results displayed on the first page of " + inputData
							+ " table is : " + (count));
					APP_LOGS.debug(": Number Of Results displayed on the first page of " + inputData + " table is : "
							+ (count));
					test.pass(" Number Of Results displayed on the first page of " + inputData + " table is : "
							+ (count));
					Thread.sleep(5000);
				}
			}

			else {
				captureScreenShot = true;
				System.out.println(": 0 Results displayed in " + inputData + " table");
				APP_LOGS.debug(": 0 Results displayed in " + inputData + " table");
				// test.fail(" FAIL : 0 Results displayed in " + inputData + "
				// table");
				// return "FAIL -: 0 Results displayed in " + inputData + "
				// table";
			}
		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Table Column Data");
			return "FAIL - Not able to Verify Table Column Data";
		}
		test.pass("Pass");
		return "PASS";
	}

	@SuppressWarnings("unchecked")
	public String VerifyText(String firstXpathKey, String secondXpathKey, String expText)
			throws ParseException, InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyText ()
		 * 
		 * @parameter: String firstXpathKey, Optional=>String secondXpathKey, Optional=> String expText
		 * 
		 * @notes: Verifies the Actual Text as compared to the Expected Text. Verification can be performed on the same page or on different pages. User can perform two different webelement's text comparison by passing argument as objectKeySecond.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying " + firstXpathKey + " Text on the Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Text on the Page");
		test.info(" Verifying " + firstXpathKey + " Text on the Page");
		Thread.sleep(SYNC_WAIT);
		if (expText.equals("Transfer v8.4 Linux Standard")) {
			expText = "Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
		} else if (expText.equals("HULFT Transfer v8.4 Linux Standard")) {
			expText = "HULFT Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
		} else if (expText.equals("Description: Transfer v8.4 Linux Standard")) {
			expText = "Description: Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
		} else if (expText.equals("Description: HULFT Transfer v8.4 Linux Standard")) {
			expText = "Description: HULFT Transfer " + "[" + "v8.4" + "]" + " Linux Standard";
		} else if (expText.equals("HULFT v8.1 Windows Standard")) {
			expText = "HULFT " + "[" + "v8.1" + "]" + " Windows Standard";
		} else if (expText.equals("HULFT v8.1 Linux Standard")) {
			expText = "HULFT " + "[" + "v8.1" + "]" + " Linux Standard";
		} else if (expText.contains("Error: You are not authorized to access this resource")) {
			String searchableString = expText;
			String[] parts = searchableString.split(":");
			String part1 = parts[0];
			String part2 = parts[1].trim();
			expText = part2;
		} else {
			String regex = "[0-9].[0-9]";
			if (expText.matches(regex)) {
				NumberFormat nf = NumberFormat.getInstance();
				Number number = nf.parse(expText);
				long lnputValue = number.longValue();
				expText = String.valueOf(lnputValue);
			}
		}
		if (expText.isEmpty()) {
			System.out.println(": Expected Data is Empty, taking this value from Hashmap");
			APP_LOGS.debug(": Expected Data is Empty, taking this value from Hashmap");
			test.info(" Expected Data is Empty, taking this value from Hashmap");
			expText = (String) getTextOrValues.get(secondXpathKey);
			if (expText == null) {
				System.out.println(
						": No Expected Data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				APP_LOGS.debug(
						": No Expected data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				expText = returnElementIfPresent(secondXpathKey).getText().trim();
			}
			if (expText.contains("Transfer History : transfer-b -> NewAWSWin : ")
					|| expText.contains("Transfer History : transfer-b -> transfer-c : ")) {
				String[] parts = expText.split("->");
				String part1 = parts[0];
				String part2 = parts[1].trim();
				String[] part3 = part2.split(":");
				String part4 = part3[0];
				String part5 = part3[1].trim();
				expText = part5;
			}
		}
		try {
			actText = returnElementIfPresent(firstXpathKey).getText().trim();
			if (actText.contains("Transfer History : transfer-b -> NewAWSWin : ")
					|| actText.contains("Transfer History : transfer-b -> transfer-c : ")) {
				String[] parts = actText.split("->");
				String part1 = parts[0];
				String part2 = parts[1].trim();
				String[] part3 = part2.split(":");
				String part4 = part3[0];
				String part5 = part3[1].trim();
				actText = part5;
			} else if (actText.equals("HULFT Transfer options(third)")) {
				String text = actText.substring(22, 29).trim();
				actText = text;

			} else if (actText.equals("HULFT Transfer options(8.4main)")) {
				String text = actText.substring(22, 31).trim();
				actText = text;
			} else if (actText.contains("You are not authorized to access this resource")) {
				String searchableString = actText;
				String[] parts = searchableString.split(":");
				String part1 = parts[0];
				String part2 = parts[1].trim();
				actText = part2;
			}
			expText = expText.trim();
			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, " ERROR : Not able to read text--" + firstXpathKey);
			return "FAIL - Not able to read text--" + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	@SuppressWarnings("unchecked")
	public String VerifyTextDDTdata(String firstXpathKey, String secondXpathKey, String expText)
			throws ParseException, InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTextDDTdata ()
		 * 
		 * @parameter: String firstXpathKey, Optional=>String secondXpathKey, Optional=> String expText
		 * 
		 * @notes: Verifies the Actual Text as compared to the Expected Text. Verification can be performed on the same page or on different pages for DDT.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying " + firstXpathKey + " Text on the Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Text on the Page");

		try {
			getTextOrValues.put(firstXpathKey, returnElementIfPresent(firstXpathKey).getText());
			actText = getTextOrValues.get(firstXpathKey).toString();

			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to read text--" + firstXpathKey;
		}
		return "PASS";
	}

	public String VerifyTitle(String actTitle, String expTitle) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTitle ()
		 * 
		 * @parameter: String actTitle & String expTitle
		 * 
		 * @notes: Verifies the Actual Web Page Title as compared to the Expected Web Page title. Verification is performed on the same Web page.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Page Title");
		APP_LOGS.debug(": Verifying Page Title");
		test.info(" Verifying Page Title");
		try {
			expTitle = expTitle.replace("_", ",");
			actTitle = driver.getTitle();
			if (actTitle.compareTo(expTitle) == 0) {
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				APP_LOGS.debug(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				test.pass(" Actual is-> " + actTitle + " AND Expected is->" + expTitle);
			} else {
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				test.fail(" FAIL : Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				return "FAIL - Actual is-> " + actTitle + " AND Expected is->" + expTitle;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to get title");
			return "FAIL - Not able to get title";
		}
		test.pass("Pass");
		return "PASS";
	}

	@SuppressWarnings("unchecked")
	public String VerifyTextContains(String firstXpathKey, String secondXpathKey, String expText)
			throws ParseException, InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTextContains ()
		 * 
		 * @parameter: String firstXpathKey, Optional=>String secondXpathKey, Optional=> String expText
		 * 
		 * @notes: Verifies the Actual Text as compared to the Expected Text. Verification can be performed on the same page or on different pages. User can perform two different webelement's text comparision by passing argument as objectKeySecond. In this it is not necessary
		 * expText should have as a whole it uses contains function.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying " + firstXpathKey + " Text on the Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Text on the Page");
		test.info(" Verifying " + firstXpathKey + " Text on the Page");
		Thread.sleep(SYNC_WAIT);

		String regex = "[0-9].[0-9]";
		if (expText.matches(regex)) {
			NumberFormat nf = NumberFormat.getInstance();
			Number number = nf.parse(expText);
			long lnputValue = number.longValue();
			expText = String.valueOf(lnputValue);
		}
		if (expText.isEmpty()) {
			System.out.println(": Expected Data is Empty, taking this value from Hashmap");
			APP_LOGS.debug(": Expected Data is Empty, taking this value from Hashmap");
			test.info(" Expected Data is Empty, taking this value from Hashmap");
			expText = (String) getTextOrValues.get(secondXpathKey);
			if (expText == null) {
				System.out.println(
						": No Expected Data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				APP_LOGS.debug(
						": No Expected data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				test.info(
						" No Expected data present in Hashmap, taking this value from secondXpathKey object of Webpage");
				expText = returnElementIfPresent(secondXpathKey).getText().trim();
			} else if (expText.equals("HULFT Integrate [v4.1 SP3] Linux Standard")) {
				String text = expText.substring(6, 15);
				expText = text;
			} else if (expText.equals("SFTP Server")) {
				String text = expText.substring(0, 4);
				expText = text;
			} else if (expText.equals("develop 2402. 1.0.4")) {
				String text = expText.substring(8, 12).trim();
				expText = text;
			}
		}
		try {
			actText = returnElementIfPresent(firstXpathKey).getText().trim();
			if (actText.equals("Protocol: HULFT") || actText.equals("Description: AutoTransferDescription")
					|| actText.contains("Name: AutoTransferReportName")
					|| actText.equals("Description: AutoTransferReportDescriptionRenamed")
					|| actText.contains("Name: AutoHostReportName")
					|| actText.equals("Description: AutoHostReportDescriptionRenamed")
					|| actText.contains("Name: AutoSystemStatusReportName")
					|| actText.equals("Description: AutoSystemStatusReportDescriptionRenamed")
					|| actText.contains("Name: AutoUserReportName")
					|| actText.equals("Description: AutoUserReportDescriptionRenamed")) {
				String searchableString = actText;
				String[] parts = searchableString.split(":");
				String part1 = parts[0];
				String part2 = parts[1].trim();
				actText = part2;
			} else if (actText.equals("SFTP Transfer Ports")) {
				String text = actText.substring(0, 4).trim();
				actText = text;
			} else if (actText.equals("HULFT Transfer Ports")) {
				String text = actText.substring(0, 5).trim();
				actText = text;
			} else if (actText.equals("Integrate Transfer Ports")) {
				String text = actText.substring(0, 9).trim();
				actText = text;
			} else if (actText.equals("HULFT Integrate [v4.1 SP3] Linux Standard")) {
				String text = actText.substring(6, 15).trim();
				actText = text;
			} else if (actText.equals("1.0.4 ( build 2402 )")) {
				String text = actText.substring(14, 18).trim();
				actText = text;
			}
			expText = expText.trim();
			if (actText.contains(expText) == true) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to read text--" + firstXpathKey);
			return "FAIL - Not able to read text--" + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	@SuppressWarnings("unchecked")
	public String VerifyTextAttributeValue(String firstXpathKey, String secondXpathKey, String expText)
			throws ParseException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTextContains ()
		 * 
		 * @parameter: String firstXpathKey, Optional=>String secondXpathKey, Optional=> String expText
		 * 
		 * @notes: Verifies the Actual Text as compared to the Expected Text. Verification can be performed on the same page or on different pages. User can perform two different webelement's text comparision by passing argument as objectKeySecond. In this it is not necessary
		 * expText should have as a whole it uses contains function.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying " + firstXpathKey + " Text on the Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Text on the Page");
		test.info(" Verifying " + firstXpathKey + " Text on the Page");

		try {
			getTextOrValues.put(firstXpathKey, returnElementIfPresent(firstXpathKey).getAttribute("value"));
			actText = getTextOrValues.get(firstXpathKey).toString();
			actText = actText.trim();
			expText = expText.trim();

			if (actText.contains(expText) == true) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to read text--" + firstXpathKey);
			return "FAIL - Not able to read text--" + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyToolTip(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page and get the tooltip from the element and verifies it with expText.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Performing Mouse hover on " + firstXpathKey);
		APP_LOGS.debug(": Performing Mouse hover on " + firstXpathKey);
		test.info(" Performing Mouse hover on " + firstXpathKey);
		try {
			Thread.sleep(2000);
			Actions act = new Actions(driver);
			WebElement root = returnElementIfPresent(firstXpathKey);
			act.moveToElement(root).build().perform();
			Thread.sleep(2000);
			String actText = root.getText();

			if (actText.contains(expText)) {
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				APP_LOGS.debug(": Actual is-> " + actTitle + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actTitle + " AND Expected is->" + expText);
			} else {
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is->" + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to get tool tip text");
			return "FAIL - Not able to get tool tip text";
		}
		test.pass("Pass");
		return "PASS";

	}

	@SuppressWarnings("unchecked")
	public String VerifyTextDDTdataContains(String firstXpathKey, String secondXpathKey, String expText)
			throws ParseException, InterruptedException {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTextDDTdata ()
		 * 
		 * @parameter: String firstXpathKey, Optional=>String secondXpathKey, Optional=> String expText
		 * 
		 * @notes: Verifies the Actual Text as compared to the Expected Text. Verification can be performed on the same page or on different pages for DDT.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying " + firstXpathKey + " Text on the Page");
		APP_LOGS.debug(": Verifying " + firstXpathKey + " Text on the Page");
		test.info(" Verifying " + firstXpathKey + " Text on the Page");

		try {
			getTextOrValues.put(firstXpathKey, returnElementIfPresent(firstXpathKey).getText());
			actText = getTextOrValues.get(firstXpathKey).toString();
			actText = actText.trim();
			expText = expText.trim();

			if (actText.contains(expText)) {
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is->" + expText);
			} else {
				globalExpText = expText;
				highlight = true;
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is->" + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is->" + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is->" + expText;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to read text--" + firstXpathKey);
			return "FAIL - Not able to read text--" + firstXpathKey;
		}
		test.pass("Pass");
		return "PASS";
	}

	// **************************************************************************************************Keywords
	// Definitions******************************************************************************************************************************

	public String VerifyTextOnNewWindow(String firstXpathKey, String expText) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTextOnNewWindow ()
		 * 
		 * @parameter: String firstXpathKey, String expText
		 * 
		 * @notes: Verify text on New Window
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		String actText = null;
		System.out.println(": Verifying Text on New Window");
		APP_LOGS.debug(": Verifying Text on New Window");
		test.info(" Verifying Text on New Window");
		try {
			String winHandleBefore = driver.getWindowHandle();
			for (String winHandle : driver.getWindowHandles()) {
				driver.switchTo().window(winHandle);
			}
			Thread.sleep(5000);
			expText = expText.trim();
			actText = returnElementIfPresent(firstXpathKey).getText().trim();
			if (actText.compareTo(expText) == 0) {
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				APP_LOGS.debug(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.pass(" Actual is-> " + actText + " AND Expected is-> " + expText);
			} else {
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actText + " AND Expected is-> " + expText);
				test.fail(" FAIL : Actual is-> " + actText + " AND Expected is-> " + expText);
				return "FAIL - Actual is-> " + actText + " AND Expected is-> " + expText;
			}
			driver.close();
			Thread.sleep(3000);
			driver.switchTo().window(winHandleBefore);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify Text on New Window");
			return "FAIL - Verify Text on New Window";
		}

		test.pass("PASS");
		return "PASS";

	}

	public String VerifyTitleContains(String expTitle) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTitle ()
		 * 
		 * @parameter: String actTitle & String expTitle
		 * 
		 * @notes: Verifies the Actual Web Page Title as compared to the Expected Web Page title. Verification is performed on the same Web page. It is not necessary to have full page title as expTitle.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Page Title");
		APP_LOGS.debug(": Verifying Page Title");
		try {
			expTitle = expTitle.replace("_", ",");
			expTitle.trim();
			actTitle = driver.getTitle();
			if (actTitle.contains(expTitle)) {
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				APP_LOGS.debug(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
			} else {
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				return "FAIL - Actual is-> " + actTitle + " AND Expected is->" + expTitle;
			}
		} catch (Exception e) {
			captureScreenShot = true;
			return "FAIL - Not able to get title";
		}
		return "PASS";
	}

	public String VerifyTransferJobIsDeleted(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTransferJobIsDeleted ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify transfer job is deleted or not.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying Transfer Job is Deleted on All Trnafers Page");
		APP_LOGS.debug(": Verifying Transfer Job is Deleted on All Trnafers Page");
		test.info(": Verifying Transfer Job is Deleted on All Trnafers Page");
		try {
			List<WebElement> transfer = returnElementsIfPresent(firstXpathKey);
			int count = transfer.size();
			System.out.println(count);
			Thread.sleep(5000);
			if (count > 0) {
				captureScreenShot = true;
				System.out.println(": Transfer Job is Still Displayed in All Transfers Page");
				APP_LOGS.debug(": Transfer Job is Still Displayed in All Transfers Page");
				test.info(": Transfer Job is Still Displayed in All Transfers Page");
				return "FAIL- : Transfer Job is Still Displayed in All Transfers Page";
			} else {
				System.out.println(": Transfer is Deleted Successfully");
				APP_LOGS.debug(": Transfer is Deleted Successfully");
				test.info(": Transfer is Deleted Successfully");
			}

		} catch (Exception e) {
			System.out.println(": Exception: " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Alert Exception: " + e.getLocalizedMessage());
			return "FAIL - Not able to Verify Transfer is Deleted";
		}

		test.pass("PASS");
		return "PASS";
	}

	public String VerifyTransferLogs(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyTransferLogs ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify the logs status for transfer
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;

		try {
			Thread.sleep(3000);
			List<WebElement> status = returnElementsIfPresent(firstXpathKey);
			int count = status.size();
			// System.out.println(count);
			Thread.sleep(3000);
			if (count > 0) {
				System.out.println(": Logs Are generated ");
				APP_LOGS.debug(": Logs Are generated ");
				test.info(": Logs Are generated ");
			} else {
				captureScreenShot = true;
				System.out.println(": Logs Are not generated ");
				test.fail(": Logs Are not generated ");
				return "FAIL- Logs are not generated";

			}

		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify Transfer Logs");
			return "FAIL - Verify Transfer logs";
		}
		test.pass("PASS");
		return "PASS";
	}

	public String VerifyURLonNewWindowPage(String firstXpathKey, String expTitle) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyURLonNewWindowPage ()
		 * 
		 * @parameter: String firstXpathKey, String expTitle
		 * 
		 * @notes: Verify URL on New Window Page
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying URL on New Window");
		APP_LOGS.debug(": Verifying URL on New Window");
		test.info(" Verifying URL on New Window");
		try {
			String winHandleBefore = driver.getWindowHandle();
			for (String winHandle : driver.getWindowHandles()) {
				driver.switchTo().window(winHandle);
			}
			expTitle = expTitle.replace("_", ",");
			actTitle = driver.getCurrentUrl();
			if (actTitle.compareTo(expTitle) == 0) {
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				APP_LOGS.debug(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				test.pass(" Actual is-> " + actTitle + " AND Expected is->" + expTitle);
			} else {
				captureScreenShot = true;
				System.out.println(": Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				test.fail(" FAIL : Actual is-> " + actTitle + " AND Expected is->" + expTitle);
				return "FAIL - Actual is-> " + actTitle + " AND Expected is->" + expTitle;
			}
			Thread.sleep(5000);
			VerifyLinkisExist(firstXpathKey);
			driver.close();
			Thread.sleep(3000);
			driver.switchTo().window(winHandleBefore);
		} catch (Exception e) {
			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : Not able to Verify URL on New Window");
			return "FAIL - Verify URL on New Window";
		}

		test.pass("Pass");
		return "PASS";
	}

	public String VerifyUserModules(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyUserModules()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Verify Google Docs User Modules in Direct Component if it matches Continue the rest of the Test Steps otherwise it Fails the execution
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		boolean bflag = false;
		try {
			System.out.println(": Verifying User Modules are Matched or not");
			APP_LOGS.debug(": Verifying User Modules are Matched or not");
			test.info(" Verifying User Modules are Matched or not");
			String[] User = { "Hosts" };
			List<WebElement> Role = returnElementsIfPresent(firstXpathKey);
			int length = Role.size();
			for (int i = 0; i <= length - 1; i++) {
				String list1 = Role.get(i).getText();
				for (int k = 0; k < User.length; k++) {
					if (list1.equals(User[k])) {
						bflag = true;
					}
				}
			}
			if (bflag == true) {
				System.out.println(": Matched");
				APP_LOGS.debug(": Matched");
				test.pass(" Matched");
			} else {
				captureScreenShot = true;
				System.out.println(": Fail because of new module was added");
				APP_LOGS.debug(": Fail because of new module was added");
				test.fail(" Fail: because of new module was added");
				TestCaseFail();
			}

		} catch (Exception e) {
			captureScreenShot = true;
			System.out.println(": " + e.getMessage());
			test.log(Status.ERROR, "ERROR : Not able to Verify Admin Modules");
			return "FAIL - Not able to Verify Admin Modules";
		}
		test.pass("Pass");
		return "PASS";
	}

	public String Wait(String stepWaitTime) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Wait ()
		 * 
		 * @parameter: String WaitTime
		 * 
		 * @notes: Wait for a user defined specific time to load the page for ex: 20 seconds. String "WaitTime" captures the value from the module xlsx file.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		try {

			System.out.println(": Waiting for Page to load.");
			APP_LOGS.debug(": Waiting for Page to load.");
			test.info(" Waiting for Page to load.");
			stepWaitTime = stepWaitTime.trim();
			if (stepWaitTime.equals("SYNC_WAIT") || stepWaitTime.equals("SMALL_WAIT") || stepWaitTime.equals("MID_WAIT")
					|| stepWaitTime.equals("LONG_WAIT")) {
				if (stepWaitTime.equals("SYNC_WAIT")) {
					Thread.sleep(Constants.SYNC_WAIT);
				} else if (stepWaitTime.equals("SMALL_WAIT")) {
					Thread.sleep(Constants.SMALL_WAIT);
				} else if (stepWaitTime.equals("MID_WAIT")) {
					Thread.sleep(Constants.MID_WAIT);
				} else if (stepWaitTime.equals("LONG_WAIT")) {
					Thread.sleep(Constants.LONG_WAIT);
				}
			} else {
				APP_LOGS.debug(
						": FAIL - Please check the Wait data in Test Case sheet. It can be SYNC_WAIT,SMALL_WAIT,MID_WAIT or LONG_WAIT BUT written as: "
								+ stepWaitTime);
				test.fail(
						" FAIL : - Please check the Wait data in Test Case sheet. It can be SYNC_WAIT,SMALL_WAIT,MID_WAIT or LONG_WAIT BUT written as: "
								+ stepWaitTime);
				return (": FAIL - Please check the Wait data in Test Case sheet. It can be SYNC_WAIT,SMALL_WAIT,MID_WAIT or LONG_WAIT BUT written as: "
						+ stepWaitTime);
			}

		} catch (Exception e) {
			APP_LOGS.debug(": FAIL - Not able to wait for " + stepWaitTime + " Seconds to load the page");
			test.log(Status.ERROR, "ERROR : Not able to wait for " + stepWaitTime + " Seconds to load the page");
			return ("FAIL - Not able to wait for " + stepWaitTime + " Seconds to load the page");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String WaitTillElementAppears(String ObjectIdentifier) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Wait ()
		 * 
		 * @parameter: String WaitTime
		 * 
		 * @notes: Wait for a user defined specific time to load the page for ex: 20 seconds. String "WaitTime" captures the value from the module xlsx file.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		try {
			highlight = false;
			captureScreenShot = false;
			int i = 0;
			int expWaitTime = 120;
			String objectIdentifierValue = "";
			String objectArray[] = null;
			String object = OR.getProperty(ObjectIdentifier);
			objectArray = object.split("__");
			objectIdentifierValue = objectArray[1].trim();
			System.out.println(": Waiting for Max " + expWaitTime + " seconds to Appear " + ObjectIdentifier
					+ " Element which may NOT Present on Page");
			APP_LOGS.debug(": Waiting for Max " + expWaitTime + " seconds to Disappear " + ObjectIdentifier
					+ " Element which may NOT Present on Page");
			test.info(" Waiting for Max " + expWaitTime + " seconds to Disappear " + ObjectIdentifier
					+ " Element which may NOT Present on Page");
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			while (isElementPresentBy(By.xpath(objectIdentifierValue)) == false) {
				if (i <= expWaitTime) {
					System.out.println(": " + ObjectIdentifier
							+ " Element is currently NOT Present on Page. Going to check again after 1 second.");
					APP_LOGS.debug(": " + ObjectIdentifier
							+ " Element is currently NOT Present on Page. Going to check again after 1 second.");
					test.info(" " + ObjectIdentifier
							+ " Element is currently NOT Present on Page. Going to check again after 1 second.");
					Thread.sleep(1000);
					i++;
				} else {
					System.out.println(": Element not loaded in " + expWaitTime
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					APP_LOGS.debug(": Element not loaded in " + expWaitTime
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					test.fail(" FAIL : Element not loaded in " + expWaitTime
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					captureScreenShot = true;
					return ("FAIL - Element not loaded in " + expWaitTime
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
				}
			}
			System.out.println(": " + ObjectIdentifier + " Element is Now Present on Page, Moving Ahead.");
			APP_LOGS.debug(": " + ObjectIdentifier + " Element is Now Present on Page, Moving Ahead");
			test.info(" " + ObjectIdentifier + " Element is Now Present on Page, Moving Ahead");
			driver.manage().timeouts().implicitlyWait(CONFIG_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
		} catch (Exception e) {
			captureScreenShot = true;
			APP_LOGS.debug(": FAIL - Not able to wait Till " + ObjectIdentifier
					+ " Element Appears on Page. Please see the screenshot for more details.");
			test.log(Status.ERROR, "ERROR : Not able to wait Till " + ObjectIdentifier
					+ " Element Appears on Page. Please see the screenshot for more details.");
			return ("FAIL - Not able to wait Till " + ObjectIdentifier
					+ " Element Appears on Page. Please see the screenshot for more details.");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String WaitTillTransferStatusIsReady(String firstXpathKey) {
		/*
		 * @HELP) { /* @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Wait ()
		 * 
		 * @parameter: String WaitTime
		 * 
		 * @notes: Wait for a user defined specific time to load the page for ex: 20 seconds. String "WaitTime" captures the value from the module xlsx file.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		try {
			highlight = false;
			captureScreenShot = false;
			int i = 0;
			int expWaitTime = 40;
			System.out.println(": Inside WaitTillTransferStatusIsReady Method");
			APP_LOGS.debug(": Inside WaitTillTransferStatusIsReady Method");
			test.info(" Inside WaitTillTransferStatusIsReady Method");
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

			System.out.println(": Transfer Status: " + returnElementIfPresent(firstXpathKey).getText().trim());
			// String
			// Status=returnElementIfPresent(firstXpathKey).getText().trim();

			if (returnElementIfPresent(firstXpathKey).getText().trim().equals("Ready")) {
				System.out.println(": Transfer status is Ready now so oveing ahead");
				APP_LOGS.debug(": Transfer status is Ready now so oveing ahead");
				test.info(" Transfer status is Ready now so oveing ahead");
			} else if (returnElementIfPresent(firstXpathKey).getText().trim().equals("Ready-Schedule")) {
				System.out.println(": Transfer status is Ready-Schedule now so oveing ahead");
				APP_LOGS.debug(": Transfer status is Ready-Schedule now so oveing ahead");
				test.info(" Transfer status is Ready-Schedule now so oveing ahead");
			} else if (returnElementIfPresent(firstXpathKey).getText().trim().equals("Pending")) {
				System.out.println(": Transfer status is Pending, so going to wait for some time");
				APP_LOGS.debug(": Transfer status is Pending, so going to wait for some time");
				test.info(" Transfer status is Pending, so going to wait for some time");
				while (returnElementIfPresent(firstXpathKey).getText().trim() == "Pending") {
					if (i <= expWaitTime) {
						System.out
								.println(": Transfer status is still Pending, going to wait 10 seconds and try again");
						APP_LOGS.debug(": Transfer status is still Pending, going to wait 10 seconds and try again");
						test.info(" Transfer status is still Pending, going to wait 10 seconds and try again");
						Thread.sleep(1000);
						i++;
					} else {
						System.out.println(": Transfer Status is not Ready in " + expWaitTime
								+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
						APP_LOGS.debug(": Transfer Status is not Ready in " + expWaitTime
								+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
						test.fail(" FAIL - Transfer Status is not Ready in " + expWaitTime
								+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
						captureScreenShot = true;
						return ("FAIL - Transfer Status is not Ready in " + expWaitTime
								+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					}
				}
				System.out.println(": Transfer Status is Now, Moving Ahead");
				APP_LOGS.debug(": Transfer Status is Now, Moving Ahead");
				test.info(": Transfer Status is Now, Moving Ahead");
				driver.manage().timeouts().implicitlyWait(CONFIG_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			captureScreenShot = true;
			APP_LOGS.debug(
					": FAIL - Not able to wait Till Transfer Status is Ready on Page. Please see the screenshot for more details.");
			test.log(Status.ERROR,
					"ERROR : Not able to wait Till Transfer Status Status is on Page. Please see the screenshot for more details.");
			return ("FAIL - Not able to wait Till Transfer Status Status is on Page. Please see the screenshot for more details.");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String WaitWhileElementPresent(String ObjectIdentifier) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: Wait ()
		 * 
		 * @parameter: String WaitTime
		 * 
		 * @notes: Wait for a user defined specific time to load the page for ex: 20 seconds. String "WaitTime" captures the value from the module xlsx file.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */

		highlight = false;
		captureScreenShot = false;
		int i = 0;
		System.out.println(": Waiting for Page to load.");
		APP_LOGS.debug(": Waiting for Page to load.");
		test.info(" Waiting for Page to load.");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		String objectIdentifierValue = "";
		String objectArray[] = null;
		try {
			String object = OR.getProperty(ObjectIdentifier);
			objectArray = object.split("__");
			objectIdentifierValue = objectArray[1].trim();
			while (isElementPresentBy(By.xpath(objectIdentifierValue)) == true) {
				if (i <= 120) {
					System.out.println(": Element present. Checking again after 1 Second");
					APP_LOGS.debug(": Element present. Checking again after 1 Second");
					test.info(" Element present. Checking again after 1 Second");
					Thread.sleep(1000);
					i++;
				} else {
					System.out.println(": Page not loaded in " + i
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					APP_LOGS.debug(": Page not loaded in " + i
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					test.fail(" FAIL : Page not loaded in " + i
							+ " Seconds. So stopping this test script execution. Please see the screenshot for more details.");
					captureScreenShot = true;
					return ("FAIL - Page not loaded in expected time. Please see the screenshot for more details.");
				}
			}
			Thread.sleep(3000);
			driver.manage().timeouts().implicitlyWait(CONFIG_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
			System.out.println(": CONFIG_IMPLICIT_WAIT_TIME: " + CONFIG_IMPLICIT_WAIT_TIME);

		} catch (Exception e) {
			captureScreenShot = true;
			APP_LOGS.debug(": FAIL - Not able to wait for Seconds to load the page");
			test.log(Status.ERROR, "ERROR : Not able to wait for Seconds to load the page");
			return ("FAIL - Not able to wait for Seconds to load the page");
		}
		test.pass("Pass");
		return "PASS";
	}

	public String VerifyCheckBoxIsEnabled(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page and get the tooltip from the element and verifies it with expText.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying if " + firstXpathKey + "  is Enabled ");
		APP_LOGS.debug(": Verifying if " + firstXpathKey + "  is Enabled ");
		try {
			boolean isChecked;
			Thread.sleep(2000);
			isChecked = returnElementIfPresent(firstXpathKey).getAttribute("checked").equals("true");
			if (isChecked == true) {
				System.out.println(": " + firstXpathKey + " CheckBox is in checked status");
				APP_LOGS.debug(": " + firstXpathKey + " CheckBox ist in checked status");
			} else {
				highlight = true;
				captureScreenShot = true;
				System.out.println(": " + firstXpathKey + " CheckBox ist in unchecked status");
				APP_LOGS.debug(": " + firstXpathKey + " CheckBox ist in unchecked status");
				return "FAIL - :" + firstXpathKey + " CheckBox ist in unchecked status";
			}

		} catch (Exception e) {

			captureScreenShot = true;
			return "FAIL - Unable to Verify the Checkbox status";
		}
		return "PASS";

	}

	public String VerifyAccountByEmail(String firstXpathKey) {
		/*
		 * @HELP
		 * 
		 * @class: Keywords
		 * 
		 * @method: VerifyToolTip ()
		 * 
		 * @parameter: String firstXpathKey
		 * 
		 * @notes: Hover mouse over given Object, link, Hyperlink, selections or buttons of a web page and get the tooltip from the element and verifies it with expText.
		 * 
		 * @returns: ("PASS" or "FAIL" with Exception in case if method not got executed because of some runtime exception) to executeKeywords method
		 * 
		 * @END
		 */
		highlight = false;
		captureScreenShot = false;
		System.out.println(": Verifying User's Account By Email ");
		APP_LOGS.debug(": Verifying User's Account By Email ");
		test.info(": Verifying User's Account By Email ");

		try {
			driver.switchTo().frame("msg_body");
			String sMailLink = returnElementIfPresent(firstXpathKey).getText().trim();
			String sMailLinkArray[] = null;
			String sMailLinkArray1[] = null;
			String sMailLinkSecondPart;
			String sMailLinkMidPart;
			String sFirstSplitText = "http";
			String sSecondSplitText = "The";
			sMailLinkArray = sMailLink.split(sFirstSplitText);
			sMailLinkSecondPart = sMailLinkArray[1].toString();
			sMailLinkArray1 = sMailLinkSecondPart.split(sSecondSplitText);
			sMailLinkMidPart = sMailLinkArray1[0].toString();
			sMailLinkMidPart = sMailLinkMidPart.trim();
			sMailLinkMidPart = sFirstSplitText + sMailLinkMidPart;
			driver.switchTo().defaultContent();
			driver.get(sMailLinkMidPart);
			Thread.sleep(10000);
			String SuccessfulMsg = driver.findElement(By.xpath("//h2[text()='Your account is successfully verified.']"))
					.getText();
			System.out.println(": " + SuccessfulMsg);

			Thread.sleep(10000);

		} catch (Exception e) {

			captureScreenShot = true;
			test.log(Status.ERROR, "ERROR : User's Account By Email");
			return "FAIL - User's Account By Email";
		}
		test.pass("Pass");
		return "PASS";
	}

}