package luckyclient.execution.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import luckyclient.utils.LogUtil;
import luckyclient.utils.config.SysConfig;
import springboot.RunService;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class BaseWebDrive {

	/**
	 * 进测试结果进行截图
	 * 
	 * @param driver  驱动
	 * @param imgname 图片名称
	 */
	public static void webScreenShot(WebDriver driver, String imgname) {
		String relativelyPath = RunService.APPLICATION_HOME;
		String pngpath = relativelyPath + File.separator + "log" + File.separator + "ScreenShot" + File.separator
				+ imgname + ".png";

		// 对远程系统进行截图
		driver = new Augmenter().augment(driver);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(pngpath));
		} catch (IOException e) {
			LogUtil.APP.error("截图操作失败，抛出异常请查看日志...", e);
		}
		scrFile.deleteOnExit();
		LogUtil.APP.info("已对当前界面进行截图操作，可通过用例执行界面的日志明细查看，也可以前往客户端上查看...【{}】", pngpath);
	}

	/**
	 * 在自动化过程中加入点击显示效果
	 * 
	 * @param driver  驱动
	 * @param element 定位元素
	 * @author Seagull
	 * @date 2019年9月6日
	 */
	public static void highLightElement(WebDriver driver, WebElement element) {		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String originalStyle = element.getAttribute("style");
		js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);

		// Wait for a brief moment to see the highlighted element
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Reset the element's original style
		js.executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);		
	}

	public static void javascriptClick(WebDriver driver, String property, String propertyValue) {
		WebElement element = null;
		element = driver.findElement(by(property, propertyValue));
		clickElement(driver, element);
	}

	private static void clickElement(WebDriver driver, WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
	}

	public static void retryClick(WebDriver driver, String operationValue, String property, String propertyValue) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement element = null;
		element = driver.findElement(by(property, propertyValue));

		int attempts = 0;
		while (attempts < 3) {
			try {
				if (waitForElementToBeClickable(driver, operationValue, property, propertyValue)) {
					js.executeScript("arguments[0].click();", element);
					break;
				}
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}
	}

	public static boolean waitForElementToBeClickable(WebDriver driver, String operationValue, String property,
			String propertyValue) {
		try {
			isElementClickable(driver, operationValue, property, propertyValue);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static void isElementClickable(WebDriver driver, String operationValue, String property,
			String propertyValue) {
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(operationValue));
		wait.until(ExpectedConditions.elementToBeClickable(by(property, propertyValue)));
	}

	public static void waitvisibility(WebDriver driver, String operationValue, String property, String propertyValue) {
		// 显式等待元素可见
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(operationValue));
		wait.until(ExpectedConditions.visibilityOfElementLocated(by(property, propertyValue)));
	}

	public static void waitnotvisibility(WebDriver driver, String operationValue, String property,
			String propertyValue) {
		// 显式等待元素不可见
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(operationValue));
		wait.until(ExpectedConditions
				.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(by(property, propertyValue))));
	}

	public static void sleep(String operationValue) {
		int seconds = Integer.valueOf(operationValue);
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void javaScriptInput(WebDriver driver, String operationValue, String property, String propertyValue) {
		sleep("1000");
		// WebElement element = findElement(driver, property, propertyValue);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].value='" + operationValue + "'", propertyValue);
	}

	public static void selectOption(WebDriver driver, String property, String propertyValue, String operationValue) {
		List<WebElement> options = findElements(driver, property, propertyValue);
		for (WebElement option : options) {
			if (option.getText().equals(operationValue)) {
				option.click(); // click the desired option
				break;
			}
		}
	}

	public static List<WebElement> findElements(WebDriver driver, String property, String propertyValue) {
		List<WebElement> elements = null;
		elements = driver.findElements(by(property, propertyValue));
		return elements;
	}

	public static WebElement findElement(WebDriver driver, String property, String propertyValue) {
		WebElement element = null;
		element = driver.findElement(by(property, propertyValue));
		return element;
	}

	public static By by(String property, String propertyValue) {
		By by = null;
		switch (property) {
		case "id":
			by = By.id(propertyValue);
			break;
		case "name":
			by = By.name(propertyValue);
			break;
		case "xpath":
			by = By.xpath(propertyValue);
			break;
		case "linktext":
			by = By.linkText(propertyValue);
			break;
		case "tagname":
			by = By.tagName(propertyValue);
			break;
		case "cssselector":
			by = By.cssSelector(propertyValue);
			break;
		default:
			break;
		}

		return by;
	}

}
