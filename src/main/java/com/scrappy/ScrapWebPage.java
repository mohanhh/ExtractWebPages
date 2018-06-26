package com.scrappy;

import com.google.common.base.Predicate;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.internal.Nullable;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Mohan on 9/26/2014.
 */

public class ScrapWebPage {
    WebDriver webDriver;

    String url = "http://tamilonline.com/thendral/Issue.aspx?id=";
    String userName = "mohanhh@yahoo.com";
    String password = "happyriley";
    WebDriverWait webDriverWait;
    String id;

    public ScrapWebPage(String id) {
        url = url + id;
        this.id = id;
        webDriver = new ChromeDriver();
        webDriver.get(url);
        webDriverWait= new WebDriverWait(webDriver, 10);
        login();
        try {
            startScraping();
        } catch(Exception e) {
            e.printStackTrace();
        }
        webDriver.close();
    }

    public void login() {
        // login input button
        List<WebElement> elementsInput = webDriver.findElements(By.tagName("input"));
        for (WebElement element : elementsInput) {
            if (element.getAttribute("id").equals("NewHeader1_Login1_txtUserName")) {
                element.sendKeys(userName);
            }
            if (element.getAttribute("id").equals("NewHeader1_Login1_txtPassword")) {
                element.sendKeys(password);
            }

        }
        // login link.
        WebElement element = webDriver.findElement(By.linkText("Login"));
        if (element != null) {
            element.click();
            new WebDriverWait(webDriver, 10)
                    .ignoring(StaleElementReferenceException.class)
                    .until(new Function<WebDriver, WebElement>() {
                        @Override
                        public WebElement apply(@Nullable WebDriver driver) {
                            try {
                                return driver.findElement(By.linkText("Logout"));
                            } catch (Exception e) {
                            }
                            return null;
                        }
                    });

        } else {
            System.out.println("Cannot find login link. Cannot login");
        }
    }

    public void startScraping() throws Exception
    {
        // look at all the a tags on the page
        // reading top part.
        HashMap<String , WebElement> allLinks = getStringWebElementHashMap();
        // now start clicking on each link. Get data when we find links like below.
        FileOutputStream outputFile =  new FileOutputStream("tamil" + id + ".txt");
        ArrayList<String> allStrings = new ArrayList<String>();
        allStrings.addAll(allLinks.keySet());
        for(String linkString:allStrings)
        {
            WebElement link1 = allLinks.get(linkString);

            try {
                String linkRef = link1.getAttribute("href");
                // ignore pdf link.
                if(linkRef.contains("ebook"))
                    continue;
                link1.click();
                new WebDriverWait(webDriver, 4)
                        .ignoring(StaleElementReferenceException.class)
                        .until(new Function<WebDriver, WebElement>() {
                            @Override
                            public WebElement apply(@Nullable WebDriver driver) {
                                try {
                                    return driver.findElement(By.xpath("//span[@id=\"lblChannelName\"]"));
                                } catch(Exception e) {

                                }
                                return null;
                            }
                        });
                try {
                    WebElement channel = webDriver.findElement(By.xpath("//span[@id=\"lblChannelName\"]"));
                    if (channel != null) {
                        outputFile.write(linkRef.getBytes("UTF-8"));
                        outputFile.write("\r\n".getBytes("UTF-8"));
                        outputFile.write(channel.getText().getBytes("UTF-8"));
                        outputFile.write("\r\n".getBytes("UTF-8"));
                    }
                    for(int i = 1; i < 100; ++i) {
                        try {
                            WebElement channel_content = webDriver.findElement(By.xpath("//span[@id=\"lblContent" + i + "\"]"));
                            if (channel_content != null) {
                                outputFile.write(channel_content.getText().getBytes("UTF-8"));
                                outputFile.write("\r\n".getBytes("UTF-8"));
                                outputFile.write("\r\n".getBytes("UTF-8"));
                                outputFile.write("\r\n".getBytes("UTF-8"));
                            }
                        } catch(Exception e) {
                            break;
                        }
                    }
                    outputFile.flush();

                    // now click prev.
                } catch (Exception e) {
                }
            } catch(ElementNotVisibleException Exception) {
                continue;
            }
            catch(Exception Exception1) {

            }


        webDriver.navigate().back();
                try {
                    new WebDriverWait(webDriver, 4)
                            .ignoring(StaleElementReferenceException.class)
                            .until(new Function<WebDriver, WebElement>() {
                                @Override
                                public WebElement apply(@Nullable WebDriver driver) {
                                    try {
                                        return driver.findElement(By.linkText("Logout"));
                                    } catch(Exception e) {}
                                        return null;
                                }
                            });
                } catch (Exception e) {
                    continue;
                }
                allLinks = getStringWebElementHashMap();
        }
         /*

            if channel_name:
        text = channel_name.get_text().encode("utf-8")
            t.write(text + "\n")
        while True:
    try:
        channel_content = self.driver.find_element_by_xpath('//span[@id="lblContent1"]')
        if channel_content:
    t.write(channel_content.get_text().encode("utf-8") + "\n")
        break
                except Exception:
    break
        break
        */
        // now read other part.


    }

    private HashMap<String, WebElement> getStringWebElementHashMap() {
        List<WebElement> links = webDriver.findElements(By.xpath("//a"));
        HashMap<String, WebElement> allLinks = new HashMap<String, WebElement>();

        for(WebElement link: links) {
            try {
                String url = link.getAttribute("href");
                String text = link.getAttribute("text");
                if (text.isEmpty())
                    continue;
                if (text.contains("Logout"))
                    continue;
                allLinks.put(text, link);
            } catch(Exception e){

            }
        }
        return allLinks;
    }

    static public void main(String args[]) {
        // read all URLS from a file.
        for(int i = 38; i < 100; ++i)
        new ScrapWebPage(Integer.toString(i));
        System.exit(0);
    }

}
