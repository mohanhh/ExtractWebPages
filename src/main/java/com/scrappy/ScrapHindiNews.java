package com.scrappy;

import com.google.common.base.Predicate;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.internal.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Mohan on 10/7/2014.
 */
public class ScrapHindiNews {
    WebDriver webDriver;

    String url = "http://rajasthanpatrika.patrika.com/rssnewsdemo.ashx?cid=18";
    WebDriverWait webDriverWait;
    String id;

    public ScrapHindiNews() {
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
        FileOutputStream stream = new FileOutputStream("HindiRssFeed.txt");
        List<WebElement> allTitles = webDriver.findElements(By.className("text"));
        List<WebElement> allLinks = webDriver.findElements(By.className("webkit-html-tag"));
        boolean isDescription = false;
        ArrayList<String> allText = new ArrayList<String>();
        for (WebElement elem : allTitles) {


                if (elem.getText().contains("CDATA")) {
                    isDescription = true;
                    continue;
                }
                else if(!elem.getText().trim().isEmpty() && !isDescription)
                {
                    allText.add(elem.getText());
                }
                if (isDescription) {
                    isDescription = false;
                    // get title
                    if(allText.size() - 3 >= 0) {
                        boolean result = false;
                        do {
                            try {
                                result = extractData(stream, allText);
                                if(result) {
                                    allText.clear();
                                    break;
                                }
                            } catch(Exception e) {
                                result = false;
                            }

                        } while(!result);
                    }
                    stream.write("\n\n\n\n".getBytes("UTF-8"));
                    stream.flush();

                }

            }
    }

    private boolean extractData(FileOutputStream stream, ArrayList<String> allText) throws IOException {

        WebDriver webDriver1 = new ChromeDriver();
        // find out which is the link
        String link = null;
        for(int i = 0; i < allText.size(); ++i) {
            try {
                link = new String(allText.get(i).getBytes("UTF-8"), "UTF-8");
                if(link.endsWith("html")) {
                    break;
                }
            } catch(Exception e) {

            }
        }
        if(link == null)
            return true;
        stream.write(link.getBytes("UTF-8"));
        stream.write("\n".getBytes("UTF-8"));
        webDriver1.get(link);

        try {
            webDriverWait = new WebDriverWait(webDriver1, 10);
            new WebDriverWait(webDriver1, 4)
                    .ignoring(StaleElementReferenceException.class)
                    .until(new Function<WebDriver, WebElement>() {
                        @Override
                        public WebElement apply(@Nullable WebDriver driver) {
                         try {
                            return driver.findElement(By.xpath("//div[@class=\"inner-left-heading\"]"));
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
        WebElement element = webDriver1.findElement(By.xpath("//div[@class=\"inner-left-heading\"]"));
        if(element != null) {
            element = element.findElement(By.xpath("//h1"));
            if(element != null)
            {
                stream.write(element.getText().getBytes("UTF-8"));
                stream.write("\n".getBytes("UTF-8"));

            }
        }
        stream.write("\n".getBytes("UTF-8"));
        element = webDriver1.findElement(By.xpath("//div[@class=\"news-date\"]"));
        if(element != null) {
            stream.write(element.getText().getBytes("UTF-8"));
            stream.write("\n".getBytes("UTF-8"));
        }
        element = webDriver1.findElement(By.xpath("//div[@class=\"detailed-news\"]"));
        if(element != null) {
            List<WebElement> elements = element.findElements(By.xpath("//div"));
            for(WebElement element1:elements) {
                if (!element1.getText().isEmpty()) {
                    stream.write(element.getText().getBytes("UTF-8"));
                    break;
                }
            }
        }
        webDriver1.close();
        webDriver1.quit();
        return true;
    }


    private HashMap<String, WebElement> getStringWebElementHashMap() {
        List<WebElement> titles = webDriver.findElements(By.xpath("//div[@class=\"rss-list\"]//li"));

        HashMap<String, WebElement> allLinks = new HashMap<String, WebElement>();

        for(WebElement title: titles) {
            try {
                String url = title.getText();
                WebElement link = title.findElement(By.xpath("//div[@class=\"rss-list\"]//li//a"));
                allLinks.put(url, link);
            } catch(Exception e){

            }
        }
        return allLinks;
    }

    static public void main(String args[]) {
        new ScrapHindiNews();
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
