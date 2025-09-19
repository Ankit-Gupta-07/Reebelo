import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Reebelo {
    public static WebDriver driver;
    public static Properties locator;
    public static List<WebElement> allLaptopName, storageList, ramList, cpuList, priceList, colorList;
    public static int totalLaptop, totalStorage, totalCpu, totalRam, totalPrice, totalColor, Sno = 0, countPrice = 0, laptopSearched = 0;
    public static String tempStorage, tempColor, tempCpu, tempRam, tempPrice, tempLaptop, tempCondition, tempProductDesc;
    public static BufferedWriter bw;
    public static String fileName;
    public static int inputValue;

    public static void main(String[] args) throws IOException, InterruptedException {
        launchReebeloTillAppleProduct();
        bw = new BufferedWriter(new FileWriter(fileName, false));
        bw.write("SNO,Product Name,Product Description,Storage,Color,Processor,RAM,Condition,Price");
        bw.close();
        productLaptop();
    }
    public static WebElement mapValueToWebElement(int inputFromUser){
        Map<Integer, WebElement> elements = new HashMap<>();
        elements.put(1, driver.findElement(By.xpath(locator.getProperty("appleFilter"))));
        elements.put(2, driver.findElement(By.xpath(locator.getProperty("dellFilter"))));
        elements.put(3, driver.findElement(By.xpath(locator.getProperty("hpFilter"))));
        elements.put(4, driver.findElement(By.xpath(locator.getProperty("microsoftFilter"))));
        elements.put(5, driver.findElement(By.xpath(locator.getProperty("asusFilter"))));
        elements.put(6, driver.findElement(By.xpath(locator.getProperty("acerFilter"))));
        elements.put(7, driver.findElement(By.xpath(locator.getProperty("lenovoFilter"))));
        return elements.get(inputFromUser);
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

//Map
        System.out.println("Enter number accordingly: \n1 for Apple\n2 for Dell\n3 for HP\n4 for Microsoft" +
                "\n5 for Asus\n6 for Acer\n7 for Lenovo");
        Scanner input = new Scanner(System.in);
        inputValue = input.nextInt();
        WebElement selected=mapValueToWebElement(inputValue);
//        Put Apple filter
        fileName = "src/main/java/Excel/" + selected.getText().trim() + "_Reebelo.csv";
        selected.click();
    }

    public static void productLaptop() throws InterruptedException, IOException {
        allLaptopName = new ArrayList<>();
        allLaptopName = driver.findElements(By.xpath(locator.getProperty("allLaptop")));
        totalLaptop = allLaptopName.size();
        for (int i = 0; i < totalLaptop; i++) {
            allLaptopName = driver.findElements(By.xpath(locator.getProperty("allLaptop")));
            tempLaptop = allLaptopName.get(i).getText();
            Thread.sleep(300);
            allLaptopName.get(i).click();
            laptopSearched++;
            countPrice = 0;
            storage();
        }

    }

    public static void storage() throws InterruptedException, IOException {
        storageList = new ArrayList<>();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        storageList = driver.findElements(By.xpath(locator.getProperty("storageAvailable")));
        if (storageList.size() == 0) {
            storageList = driver.findElements(By.xpath(locator.getProperty("catchStorageAvailable")));
        }
        totalStorage = storageList.size();
        for (int j = 0; j < totalStorage; j++) {
            storageList = driver.findElements(By.xpath(locator.getProperty("storageAvailable")));
            if (storageList.size() == 0) {
                storageList = driver.findElements(By.xpath(locator.getProperty("catchStorageAvailable")));
            }
            Thread.sleep(200);
            storageList.get(j).click();
            tempStorage = storageList.get(j).getText();
            color();
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,0);");
        WebDriverWait exWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        exWait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator.getProperty("laptops")))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.findElement(By.xpath(locator.getProperty("laptops"))).click();
        WebElement selected = mapValueToWebElement(inputValue);
        selected.click();
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
        if (cpuList.size() == 0) {
            cpuList = driver.findElements(By.xpath(locator.getProperty("catchCpuAvailable")));
        }
        totalCpu = cpuList.size();
        for (int k = 0; k < totalCpu; k++) {
            cpuList = driver.findElements(By.xpath(locator.getProperty("cpuAvailable")));
            if (cpuList.size() == 0) {
                cpuList = driver.findElements(By.xpath(locator.getProperty("catchCpuAvailable")));
            }
            String strCpu = cpuList.get(k).getAttribute("outerHTML");
            if (!strCpu.contains("border-dashed border-1")) {
                Actions act = new Actions(driver);
                act.moveToElement(cpuList.get(k)).click().perform();
//                cpuList.get(k).click();
                tempCpu = cpuList.get(k).getText().replaceAll("'", " ");
                ram();
            }

        }

    }

    public static void ram() throws InterruptedException, IOException {
        ramList = new ArrayList<>();
        ramList = driver.findElements(By.xpath(locator.getProperty("rampAvailable")));
        if (ramList.size() == 0) {
            ramList = driver.findElements(By.xpath(locator.getProperty("catchRamAvailable")));
        }
        totalRam = ramList.size();
        for (int l = 0; l < totalRam; l++) {
            ramList = driver.findElements(By.xpath(locator.getProperty("rampAvailable")));
            if (ramList.size() == 0) {
                ramList = driver.findElements(By.xpath(locator.getProperty("catchRamAvailable")));
            }
            String strRam = ramList.get(l).getAttribute("outerHTML");
            if (!strRam.contains("border-dashed border-1")) {
                Actions act = new Actions(driver);
                act.moveToElement(ramList.get(l)).click().perform();
//                ramList.get(l).click();
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
            if (!strPrice.contains("border-dashed") && !strPrice.contains("text-gray-700/50")) {
                Actions act = new Actions(driver);
                act.moveToElement(priceList.get(m)).click().perform();
                priceList.get(m).click();
                String op = priceList.get(m).getText().trim().replace("\n", " ");
                int ind = op.indexOf(" ");
                tempPrice = op.substring((ind + 1)).replace(",", "");
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
        bw = new BufferedWriter(new FileWriter(fileName, true));
        bw.newLine();
        bw.write(Sno + "," +
                "\"" + tempLaptop.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempProductDesc.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempStorage.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempColor.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempCpu.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempRam + "\"," +
                "\"" + tempCondition + "\"," +
                "\"" + tempPrice + "\"");

        bw.close();
        System.out.println("Description: " + tempProductDesc.replaceAll("[\\r\\n]+", " ") + "\nLaptop-" + tempLaptop.replaceAll("[\\r\\n]+", " ") + "\n--Storage - " + tempStorage.replaceAll("[\\r\\n]+", " ") + "--" + tempColor + "\n--CPU - " + tempCpu + "--RAM - " + tempRam + "--Price - " + countPrice + ": " + tempPrice + " " + tempCondition);
        System.out.println("========================" + "Laptop Count: " + laptopSearched + " out of: " + totalLaptop + "========================================");

    }
}
