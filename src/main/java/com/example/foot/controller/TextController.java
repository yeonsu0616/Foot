package com.example.foot.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class TextController {
    public static void main(String[] args) {
        // WebDriver 설정 및 초기화
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // 웹페이지 열기
            String url = "https://www.plabfootball.com";
            driver.get(url);

            // 일정 시간 대기 (페이지 로드 및 JavaScript 렌더링 대기)
            Thread.sleep(10000); // 10초 대기 (필요에 따라 조정)

            // match-list__title 클래스에 있는 모든 h3 태그 선택
            List<WebElement> titleElements = driver.findElements(By.cssSelector(".match-list__title h3"));

            // list--match-schedule__time 클래스에 있는 모든 p 태그 선택
            List<WebElement> timeElements = driver.findElements(By.cssSelector(".list--match-schedule__time p"));

            List<WebElement> infoElements = driver.findElements(By.cssSelector(".label--match-option"));

//            List<WebElement> beginElements = driver.findElements(By.cssSelector(".match--option span"));

//            List<WebElement> midElements = driver.findElements(By.cssSelector(".match--option is_mid"));

            // 각 요소의 텍스트 출력 (두 리스트의 크기가 같다고 가정)
            for (int i = 0; i < titleElements.size(); i++) {
                String title = titleElements.get(i).getText();
                String time = timeElements.get(i).getText();
                String info = infoElements.get(i).getText();
//                String level1 = beginElements.get(i).getText();
//                String level2 = midElements.get(i).getText();

                System.out.println("Title: " + title);
                System.out.println("Time: " + time);
                System.out.println("info: " + info);
//                System.out.println("level1: " + level1);
//                System.out.println("level2: " + level2);
                System.out.println("-------------------------");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 드라이버 종료
            driver.quit();
        }
    }
}
