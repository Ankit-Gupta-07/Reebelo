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

public class Reebelo_mobile_phones {

    public static WebDriver driver;
    public static Properties locator;
    public static List<WebElement> allPhoneName, storageList, priceList, colorList;
    public static int totalPhones, totalStorage, totalPrice, totalColor, Sno = 0, countPrice = 0, phoneSearched = 0;
    public static String tempStorage, tempColor, tempPrice, tempPhone, tempCondition, tempProductDesc;
    public static BufferedWriter bw;
    public static String fileName;
    public static int inputValue;

    public static void main(String[] args) throws IOException, InterruptedException {
        launchReebeloTillAppleProduct();
        bw = new BufferedWriter(new FileWriter(fileName, false));
        bw.write("SNO,Product Name,Storage,Color,Condition,Price");
        bw.close();
        productLaptop();
    }

    public static WebElement mapValueToWebElement(int inputFromUser) {
        Map<Integer, WebElement> elements = new HashMap<>();
        elements.put(1, driver.findElement(By.xpath(locator.getProperty("appleFilter"))));
        elements.put(2, driver.findElement(By.xpath(locator.getProperty("samsungFilter"))));

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
        driver.findElement(By.xpath(locator.getProperty("Smartphones"))).click();

//Map
        System.out.println("Enter number accordingly: \n1 for Apple\n2 for Samsung");
        Scanner input = new Scanner(System.in);
        inputValue = input.nextInt();
        WebElement selected = mapValueToWebElement(inputValue);
//        Put Apple filter
        fileName = "src/main/java/Excel/" + selected.getText().trim() + "_ReebeloPhones.csv";
        selected.click();
    }

    public static void productLaptop() throws InterruptedException, IOException {
        allPhoneName = new ArrayList<>();
        allPhoneName = driver.findElements(By.xpath(locator.getProperty("allPhone")));
        totalPhones = allPhoneName.size();
        for (int i = 0; i < totalPhones; i++) {
            allPhoneName = driver.findElements(By.xpath(locator.getProperty("allPhone")));
            tempPhone = allPhoneName.get(i).getText();
            Thread.sleep(300);
            allPhoneName.get(i).click();
            phoneSearched++;
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
            phoneColor();
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,0);");
        WebDriverWait exWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        exWait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator.getProperty("Smartphones")))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.findElement(By.xpath(locator.getProperty("Smartphones"))).click();
        WebElement selected = mapValueToWebElement(inputValue);
        selected.click();
    }

    public static void phoneColor() throws InterruptedException, IOException {
        colorList = new ArrayList<>();
        colorList = driver.findElements(By.xpath(locator.getProperty("colorAvailable")));
        totalColor = colorList.size();
        for (int n = 0; n < totalColor; n++) {
            colorList = driver.findElements(By.xpath(locator.getProperty("colorAvailable")));
            String strColor = colorList.get(n).getAttribute("outerHTML");
            if (!strColor.contains("dashoffset")) {
                colorList.get(n).click();
                tempColor = driver.findElement(By.xpath(locator.getProperty("colorName"))).getText().substring(7);
                phonePrice();
            }
        }

    }


    public static void phonePrice() throws IOException {
        priceList = new ArrayList<>();
        priceList = driver.findElements(By.xpath(locator.getProperty("priceAvailable")));
        totalPrice = priceList.size();

        for (int m = 0; m < totalPrice; m++) {
            priceList = driver.findElements(By.xpath(locator.getProperty("priceAvailable")));
            String strPrice = priceList.get(m).getAttribute("outerHTML");

            if (!strPrice.contains("border-dashed") && !strPrice.contains("text-gray-700/50")) {
                Actions act = new Actions(driver);
                act.moveToElement(priceList.get(m)).click().perform();

                String op = priceList.get(m).getText().trim().replace("\n", " ");
                int ind = op.indexOf(" ");
                tempCondition = op.substring(0, ind);
                tempPrice = op.substring(ind + 1).replace(",", "").trim();

                Sno++;
                printData();
            }
        }
    }

    private static void printData() throws IOException {
        // Log to console
        System.out.println("📱 Total Data Fetched: " + Sno);
        System.out.println("Phone: " + tempPhone +
                " | Storage: " + tempStorage +
                " | Color: " + tempColor +
                " | Condition: " + tempCondition +
                " | Price: " + tempPrice);
        System.out.println("======================== Phone Count: " + phoneSearched +
                " out of: " + totalPhones + " ================================");

        // Write to CSV
        bw = new BufferedWriter(new FileWriter(fileName, true));
        bw.newLine();
        bw.write(Sno + "," +
                "\"" + tempPhone.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempStorage.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempColor.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempCondition.replaceAll("[\\r\\n]+", " ") + "\"," +
                "\"" + tempPrice.replaceAll("[\\r\\n]+", " ") + "\"");
        bw.close();
    }
}
