import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class tradeIn {
    public static int SNo=0;
    public static String model,brand,variant,price,productDesc;
    public static void main(String[] args) throws IOException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://tradeinwith.asurion.com.au/");
        driver.manage().window().maximize();
        WebDriverWait exw = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        WebElement laptop=driver.findElement(By.xpath(".//*[text()=\"Laptop\"]"));
        laptop.click();

        WebElement apple = driver.findElement(By.xpath(".//*[text()=\"Apple\"]"));
        exw.until(ExpectedConditions.visibilityOf(apple));
        brand=apple.getText();
        apple.click();
        // excel
        BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/Excel/tradeIn.csv"));
        bw.write("SNO,Brand,Model,Variant,Product Description, Price");
        bw.newLine();
        //model name loop
        List<WebElement> modelName = new ArrayList<>();
        modelName = driver.findElements(By.xpath(".//div[@class=\"flex flex-row mx-6 md:mx-2 border justify-center items-center px-3 md:w-135 md:h-auto w-135 h-135 md:mt-m20 mt-12 cursor-pointer rounded-r4 bg-white  border-HN-Gray-Dark border-1\"]/div"));
        for (int i = 0; i < modelName.size(); i++) {
            model = modelName.get(i).getText().trim();
            modelName.get(i).click();
            // variant selector
            List<WebElement> variantName=new ArrayList<>();
            variantName=driver.findElements(By.xpath(".//*[@id=\"basediv\"]/div[2]/div[7]/div[3]/div/div/div"));
            for(int j=0;j<variantName.size();j++){
                variant=variantName.get(j).getText();
                variantName.get(j).click();

                //price fetch
                SNo++;
                String tempPrice=driver.findElement(By.xpath(".//p[@class=\"font-bold md:text-32 text-center text-64 leading-54\"]")).getText();
                int indStart=tempPrice.indexOf('$');
                int indEnd=tempPrice.indexOf('*');
                price=tempPrice.substring(indStart,indEnd);
                productDesc=driver.findElement(By.xpath(".//*[@id=\"basediv\"]/div[2]/div[8]/div[3]/div/div")).getText();
                bw.write(SNo+","+brand+","+model+","+variant+","+productDesc+","+price);
                bw.newLine();
            }
        }
        bw.close();
    }
}
