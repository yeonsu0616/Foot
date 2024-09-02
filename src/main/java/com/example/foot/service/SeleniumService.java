package com.example.foot.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeleniumService {

    public void scrapeData() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // 상품 목록 페이지 URL
            String baseUrl = "https://www.plabfootball.com";
            driver.get(baseUrl);

            Thread.sleep(10000); // 페이지 로드 대기

            // 상품 링크를 찾아서 리스트로 저장
            List<WebElement> productElements = driver.findElements(By.cssSelector(".list--match-schedule--container .list--match-schedule--item")); // 상품 링크의 적절한 CSS 셀렉터로 변경 필요
            System.out.println(productElements+"a");
            for (WebElement productElement : productElements) {
                // 상품 링크에서 상대 URL 추출
                String relativeUrl = productElement.getAttribute("href");

                // 상대 URL을 기반으로 세부 페이지 URL 생성
                String productUrl = relativeUrl.startsWith("/match/") ? baseUrl + relativeUrl : baseUrl + "/match/" + relativeUrl;

                // 세부 페이지로 이동
                driver.get(productUrl);

                Thread.sleep(10000); // 세부 페이지 로드 대기

                // 세부 페이지에서 데이터 추출
                extractProductDetails(driver);

                // 다시 목록 페이지로 돌아가기
                driver.navigate().back();
                Thread.sleep(5000); // 페이지 로드 대기
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private void extractProductDetails(WebDriver driver) {
        try {
            // 대표 이미지 추출 (여기서는 첫 번째 이미지를 대표 이미지로 가정)
            WebElement imageElement = driver.findElement(By.cssSelector(".modal--body img")); // 대표 이미지의 적절한 CSS 셀렉터로 변경 필요
            String imageUrl = imageElement.getAttribute("https://d31wz4d3hgve8q.cloudfront.net/media/KakaoTalk_Photo_2024-02-05-17-40-30_004.jpeg");
            System.out.println("Main Image URL: " + imageUrl);

            // 상품 이름
            WebElement nameElement = driver.findElement(By.cssSelector(".txtH w700h a")); // 상품 이름의 적절한 CSS 셀렉터로 변경 필요
            String productName = nameElement.getText();
            System.out.println("Product Name: " + productName);

            // 상품 가격
            WebElement priceElement = driver.findElement(By.cssSelector(".product-price-class")); // 가격의 적절한 CSS 셀렉터로 변경 필요
            String productPrice = priceElement.getText();
            System.out.println("Product Price: " + productPrice);

            // 기타 정보 (예: 설명)
            WebElement descriptionElement = driver.findElement(By.cssSelector(".product-description-class")); // 설명의 적절한 CSS 셀렉터로 변경 필요
            String productDescription = descriptionElement.getText();
            System.out.println("Product Description: " + productDescription);

            System.out.println("-------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
