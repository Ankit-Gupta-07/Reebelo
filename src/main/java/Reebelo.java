import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Reebelo {
    public static WebDriver driver;
    public static Properties locator;
    public static List<WebElement> allLaptopName, storageList, ramList, cpuList, priceList, colorList;
    public static int totalLaptop, totalStorage, totalCpu, totalRam, totalPrice, totalColor, Sno = 0, countPrice = 0, laptopSearched = 0;
    public static String tempStorage, tempColor, tempCpu, tempRam, tempPrice, tempLaptop, tempCondition,tempProductDesc;
    public static BufferedWriter bw;


    public static void main(String[] args) throws IOException, InterruptedException {
        launchReebeloTillAppleProduct();
        bw = new BufferedWriter(new FileWriter("src/main/java/Excel/Reebelo.csv"));
        bw.write("SNO,Product Name,Product Description,Storage,Color,Processor,RAM,Condition,Price");
        bw.close();
        productLaptop();
    }

    public static void launchReebeloTillAppleProduct() throws IOException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
//load locator file
        locator = new Properties();
        FileInputStream fis = new FileInputStream("src/locator.properties");
        locator.load(fis);
        driver.get(locator.getProperty("url"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.findElements(By.xpath(locator.getProperty("WelcomePopUp"))).get(2).click();
        driver.findElement(By.xpath(locator.getProperty("laptops"))).click();
//        Put Apple filter
        driver.findElement(By.xpath(locator.getProperty("appleFilter"))).click();

    }

    public static void productLaptop() throws InterruptedException, IOException {
        allLaptopName = new ArrayList<>();
        allLaptopName = driver.findElements(By.xpath(locator.getProperty("allLaptop")));
        totalLaptop = allLaptopName.size();
        for (int i = 0; i < totalLaptop; i++) {
            allLaptopName = driver.findElements(By.xpath(locator.getProperty("allLaptop")));
            tempLaptop = allLaptopName.get(i).getText();
            allLaptopName.get(i).click();
            laptopSearched++;
            countPrice = 0;
            storage();
        }

    }

    public static void storage() throws InterruptedException, IOException {
        storageList = new ArrayList<>();
        storageList = driver.findElements(By.xpath(locator.getProperty("storageAvailable")));
        totalStorage = storageList.size();
        for (int j = 0; j < totalStorage; j++) {
            storageList = driver.findElements(By.xpath(locator.getProperty("storageAvailable")));
            storageList.get(j).click();
            tempStorage = storageList.get(j).getText();
            color();
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,0);");
        WebDriverWait exWait=new WebDriverWait(driver,Duration.ofSeconds(15));
        exWait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator.getProperty("laptops")))));
        driver.findElement(By.xpath(locator.getProperty("laptops"))).click();
//        Put Apple filter
        driver.findElement(By.xpath(locator.getProperty("appleFilter"))).click();
    }

    public static void color() throws InterruptedException, IOException {
        colorList = new ArrayList<>();
        colorList = driver.findElements(By.xpath(locator.getProperty("colorAvailable")));
        totalColor = colorList.size();
        for (int n = 0; n < totalColor; n++) {
            colorList = driver.findElements(By.xpath(locator.getProperty("colorAvailable")));
            String strColor = colorList.get(n).getAttribute("outerHTML");
            if (!strColor.contains("dashoffset")) {
                colorList.get(n).click();
                tempColor = driver.findElement(By.xpath(locator.getProperty("colorName"))).getText().substring(7);
                cpu();
            }
        }

    }

    public static void cpu() throws InterruptedException, IOException {
        cpuList = new ArrayList<>();
        cpuList = driver.findElements(By.xpath(locator.getProperty("cpuAvailable")));
        totalCpu = cpuList.size();
        for (int k = 0; k < totalCpu; k++) {
            cpuList = driver.findElements(By.xpath(locator.getProperty("cpuAvailable")));
            String strCpu = cpuList.get(k).getAttribute("outerHTML");
            if (!strCpu.contains("border-dashed border-1")) {
                cpuList.get(k).click();
                tempCpu = cpuList.get(k).getText().replaceAll("'"," ");
                ram();
            }

        }

    }

    public static void ram() throws InterruptedException, IOException {
        ramList = new ArrayList<>();
        ramList = driver.findElements(By.xpath(locator.getProperty("rampAvailable")));
        totalRam = ramList.size();
        for (int l = 0; l < totalRam; l++) {
            ramList = driver.findElements(By.xpath(locator.getProperty("rampAvailable")));
            String strRam = ramList.get(l).getAttribute("outerHTML");
            if (!strRam.contains("border-dashed border-1")) {
                ramList.get(l).click();
                tempRam = ramList.get(l).getText();
                price();
            }
        }

    }

    public static void price() throws InterruptedException, IOException {
        priceList = new ArrayList<>();
        priceList = driver.findElements(By.xpath(locator.getProperty("priceAvailable")));
        totalPrice = priceList.size();
        for (int m = 0; m < totalPrice; m++) {
            priceList = driver.findElements(By.xpath(locator.getProperty("priceAvailable")));
            String strPrice = priceList.get(m).getAttribute("outerHTML");
            if (!strPrice.contains("border-dashed border-1")) {
                priceList.get(m).click();
                String op = priceList.get(m).getText().trim().replace("\n", " ");
                int ind = op.indexOf(" ");
                tempPrice = op.substring((ind + 1)).replace(",","");
                tempCondition = op.substring(0, ind);
                tempProductDesc = driver.findElement(By.xpath(locator.getProperty("productDescription"))).getText().trim();
                countPrice++;
                Sno++;
                printData();
            }

        }
    }

    private static void printData() throws IOException {
        System.out.println(" Total Data Fetched: " + Sno);
        bw = new BufferedWriter(new FileWriter("src/main/java/Excel/Reebelo.csv", true));
        bw.newLine();
        bw.write(Sno + "," + tempLaptop + "," + tempProductDesc + "," + tempStorage + "," + tempColor + "," + tempCpu + "," + tempRam + "," +tempCondition+","+tempPrice);
        bw.close();
        System.out.println("Description: "+tempProductDesc+"\nLaptop-" + tempLaptop + "\n--Storage - " + tempStorage + "--" + tempColor + "\n--CPU - " + tempCpu + "--RAM - " + tempRam + "--Price - " + countPrice + ": " + tempPrice + " " + tempCondition);
        System.out.println("========================" + "Laptop Count: " + laptopSearched + " out of: " + totalLaptop + "========================================");

    }
}
