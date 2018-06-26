package com.scrappy;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.internal.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Mohan on 1/29/2015.
 */
public class ScrapMarathiNews {
    WebDriver webDriver;

    String url = "http://mr.wikisource.org/wiki/%E0%A4%AE%E0%A5%81%E0%A4%96%E0%A4%AA%E0%A5%83%E0%A4%B7%E0%A5%8D%E0%A4%A0";
    WebDriverWait webDriverWait;
    String id;

    public ScrapMarathiNews() {
        webDriver = new ChromeDriver();
        webDriver.get(url);
        webDriverWait= new WebDriverWait(webDriver, 10);
        try {
            startScraping();
        } catch(Exception e) {
            e.printStackTrace();
        }
        webDriver.close();
        webDriver.quit();
    }

    public void startScraping() throws Exception {
        FileOutputStream stream = new FileOutputStream("MarathiText.txt");
        WebElement mainTitle = webDriver.findElement(By.className("firstHeading"));
        WebElement subTitle = webDriver.findElement(By.className("mw-body-content"));
        if(mainTitle != null) {
            stream.write(mainTitle.getText().getBytes("UTF-8"));
        }
        if(subTitle != null) {
            stream.write(subTitle.getText().getBytes("UTF-8"));
            // get all links

            List<WebElement> allElements = subTitle.findElements(By.tagName("a"));
            for (WebElement element : allElements) {
                extractData(stream, element);
            }
        }

    }

    private boolean extractData(FileOutputStream stream, WebElement link) throws IOException {

        if(link == null)
            return true;
        stream.write("\n".getBytes("UTF-8"));
        stream.write(link.getText().getBytes("UTF-8"));
        WebDriver webDriver1 = new ChromeDriver();
        webDriver1.get(link.getAttribute("href"));

        try {
            webDriverWait = new WebDriverWait(webDriver1, 10);
            new WebDriverWait(webDriver1, 4)
                    .ignoring(StaleElementReferenceException.class)
                    .until(new Function<WebDriver, WebElement>() {
                        @Override
                        public WebElement apply(@Nullable WebDriver driver) {

                            try {
                                return driver.findElement(By.className("firstHeading"));
                            } catch (Exception e) {

                            }
                            return null;
                        }
                    });
        } catch(Exception e) {
            webDriver1.close();
            webDriver1.quit();
            return true;
        }
        WebElement element = webDriver1.findElement(By.className("mw-body-content"));
        if(element != null) {
                stream.write(element.getText().getBytes("UTF-8"));
                stream.write("\n".getBytes("UTF-8"));

        }
        webDriver1.close();
        webDriver1.quit();
        return true;
    }



    static public void main(String args[]) {
        new ScrapMarathiNews();
        System.exit(0);
    }


}

        /*
        // look at all links giving us RSS feeds.
        HashMap<String, WebElement> allLinks = getStringWebElementHashMap();
        Set<String> tmpLinks = allLinks.keySet();
        for(String s:tmpLinks)
        {
            WebElement element = allLinks.get(s);

            JavascriptExecutor jse = (JavascriptExecutor)webDriver;

            //jse.executeScript("arguments[0].click();", element); // this will scroll up
            //String x = "scroll (" + (element.getLocation().x + 100) +  ", " +  (element.getLocation().y + 200) + ")";
            //jse.executeScript(x); // this will scroll up

                try {
                    ((JavascriptExecutor) webDriver).executeScript(
                            "arguments[0].scrollIntoView(true);", element);
                } catch (Exception e) {
                    continue;
                }
            try {
              Thread.sleep(10 * 1000);
            } catch(Exception e) {

            }
            if(element.isDisplayed()) {
                try {
                    element.click();
                } catch(Exception e) {
                    continue;
                }
            }
            webDriverWait= new WebDriverWait(webDriver, 10);
            /*new WebDriverWait(webDriver, 4)
                    .ignoring(StaleElementReferenceException.class)
                    .until(new Predicate<WebDriver>() {
                        @Override
                        public boolean apply(@Nullable WebDriver driver) {
                            driver.findElement(By.xpath("//div[@class=\"collapsible\"]"));
                            return true;
                        }
                    });
            */
