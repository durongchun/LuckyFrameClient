package luckyclient.execution.webdriver;

import java.io.File;
import java.io.IOException;
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
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class BaseWebDrive {

	/**
	 * �����Խ�����н�ͼ
	 * @param driver ����
	 * @param imgname ͼƬ����
	 */
	public static void webScreenShot(WebDriver driver, String imgname) {
		String relativelyPath = RunService.APPLICATION_HOME;
		String pngpath=relativelyPath +File.separator+ "log"+File.separator+"ScreenShot" +File.separator+ imgname + ".png";

		// ��Զ��ϵͳ���н�ͼ
		driver = new Augmenter().augment(driver);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(pngpath));
		} catch (IOException e) {
			LogUtil.APP.error("��ͼ����ʧ�ܣ��׳��쳣��鿴��־...", e);
		}
		scrFile.deleteOnExit();
		LogUtil.APP
				.info("�ѶԵ�ǰ������н�ͼ��������ͨ������ִ�н������־��ϸ�鿴��Ҳ����ǰ���ͻ����ϲ鿴...��{}��",pngpath);
	}

	/**
	 * ���Զ��������м�������ʾЧ��
	 * @param driver ����
	 * @param element ��λԪ��
	 * @author Seagull
	 * @date 2019��9��6��
	 */
    public static void highLightElement(WebDriver driver, WebElement element){
    	Properties properties = SysConfig.getConfiguration();
    	//boolean highLight = BooleanUtil.toBoolean(properties.getProperty("webdriver.highlight"));
    	boolean highLight=true;
    	
    	if(highLight){
            JavascriptExecutor js = (JavascriptExecutor) driver;
            /*����js�����������ҳ��Ԫ�ض���ı�����ɫ�ͱ߿���ɫ�ֱ��趨Ϊ��ɫ�ͺ�ɫ*/
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "background: yellow; border:2px solid red;");
    	}
    }
    
    
    public static void javascriptClick(WebDriver driver, String property, String propertyValue) {  
    	WebElement element = null;
    	switch (property) {
        case "id":
        	element=driver.findElement(By.id(propertyValue));            
            break;
        case "name":
        	element=driver.findElement(By.name(propertyValue));     
            break;
        case "xpath":
        	element=driver.findElement(By.xpath(propertyValue));     
            break;
        case "linktext":
        	element=driver.findElement(By.linkText(propertyValue));      
            break;
        case "tagname":
        	element=driver.findElement(By.tagName(propertyValue));       
            break;
        case "cssselector":
        	element=driver.findElement(By.cssSelector(propertyValue));       
            break;
        default:
            break;
    }
        clickElement(driver, element);
    }
   
    
    private static void clickElement(WebDriver driver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }
    
    public static void retryClick(WebDriver driver, String operationValue, String property, String propertyValue) { 			
    	JavascriptExecutor js = (JavascriptExecutor) driver; 			
    	WebElement element = null;
    	switch (property) {
    	case "id":
    	     element=driver.findElement(By.id(propertyValue));            
    	     break;
    	case "name":
        	element=driver.findElement(By.name(propertyValue));     
            break;
        case "xpath":
        	element=driver.findElement(By.xpath(propertyValue));     
            break;
        case "linktext":
        	element=driver.findElement(By.linkText(propertyValue));      
            break;
        case "tagname":
        	element=driver.findElement(By.tagName(propertyValue));       
            break;
        case "cssselector":
        	element=driver.findElement(By.cssSelector(propertyValue));       
            break;
        default:
            break;        
    	}
    	int attempts = 0;    			
		while (attempts < 3) {    			
			try {
				if (waitForElementToBeClickable(driver, operationValue, property, propertyValue)) {
					js.executeScript("arguments[0].click();",element);
					break;
				}
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}
	}		
    			
    
    public static boolean waitForElementToBeClickable(WebDriver driver, String operationValue, String property, String propertyValue) {
    	try {
    		isElementClickable(driver, operationValue, property, propertyValue);
    		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    
    }
    
    public static void isElementClickable(WebDriver driver, String operationValue, String property, String propertyValue) {
    	 WebDriverWait wait=new WebDriverWait(driver, Long.parseLong(operationValue));
         switch (property) {
             case "id":
                 wait.until(ExpectedConditions.elementToBeClickable(By.id(propertyValue)));
                 break;
             case "name":
                 wait.until(ExpectedConditions.elementToBeClickable(By.name(propertyValue)));
                 break;
             case "xpath":
                 wait.until(ExpectedConditions.elementToBeClickable(By.xpath(propertyValue)));
                 break;
             case "linktext":
                 wait.until(ExpectedConditions.elementToBeClickable(By.linkText(propertyValue)));
                 break;
             case "tagname":
                 wait.until(ExpectedConditions.elementToBeClickable(By.tagName(propertyValue)));
                 break;
             case "cssselector":
                 wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(propertyValue)));
                 break;
             default:
                 break;
         }
	}
    
    public static void waitvisibility(WebDriver driver, String operationValue, String property, String propertyValue) {
    	// ��ʽ�ȴ�Ԫ�ؿɼ�
        WebDriverWait wait=new WebDriverWait(driver,Long.parseLong(operationValue));
        switch (property) {
            case "id":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(propertyValue)));
                break;
            case "name":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(propertyValue)));
                break;
            case "xpath":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(propertyValue)));
                break;
            case "linktext":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(propertyValue)));
                break;
            case "tagname":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(propertyValue)));
                break;
            case "cssselector":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(propertyValue)));
                break;
            default:
                break;
        }
	}
    
    public static void waitnotvisibility(WebDriver driver, String operationValue, String property, String propertyValue) {
    	// ��ʽ�ȴ�Ԫ�ز��ɼ�
        WebDriverWait wait=new WebDriverWait(driver,Long.parseLong(operationValue));
        switch (property) {
        case "id":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(propertyValue))));
            break;
        case "name":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name(propertyValue))));
            break;
        case "xpath":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(propertyValue))));
            break;
        case "linktext":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.linkText(propertyValue))));
            break;
        case "tagname":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName(propertyValue))));
            break;
        case "cssselector":
            wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(propertyValue))));
            break;
        default:
            break;
        }
	}
    
    public static void sleep(String operationValue) {
    	try {
			Thread.sleep(Long.parseLong(operationValue));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}   
      
    public static void javaScriptInput(WebDriver driver, String operationValue, String property, String propertyValue) {
   	    sleep("1000");		
    	//WebElement element = findElement(driver, property, propertyValue);    	
    	JavascriptExecutor js = (JavascriptExecutor) driver;
    	js.executeScript("arguments[0].value='" + operationValue + "'", propertyValue);
	}  
    
    public static WebElement findElement(WebDriver driver, String property, String propertyValue) {
    	WebElement element = null;
    	switch (property) {
        case "id":
        	element=driver.findElement(By.id(propertyValue));            
            break;
        case "name":
        	element=driver.findElement(By.name(propertyValue));     
            break;
        case "xpath":
        	element=driver.findElement(By.xpath(propertyValue));     
            break;
        case "linktext":
        	element=driver.findElement(By.linkText(propertyValue));      
            break;
        case "tagname":
        	element=driver.findElement(By.tagName(propertyValue));       
            break;
        case "cssselector":
        	element=driver.findElement(By.cssSelector(propertyValue));       
            break;
        default:
            break;
    }
    	return element;
	}
    
    
}
