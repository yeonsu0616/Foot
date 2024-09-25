package com.example.foot.service;

import com.example.foot.constant.ItemSellStatus;
import com.example.foot.entity.Item;
import com.example.foot.entity.ItemImg;
import com.example.foot.repository.ItemRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class SeleniumService {
   private final ItemService itemService;
   private final ItemRepository itemRepository;
   private final ItemImgService itemImgService;

    public void startCrawling() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            String url = "https://www.plabfootball.com";
            driver.get(url);
            driver.manage().window().maximize();


            Thread.sleep(10000); // 10초 대기 (필요에 따라 조정)

            // 상품 제목, 시간, 정보 가져오기
            List<WebElement> titleElements = driver.findElements(By.cssSelector(".match-list__title h3"));
            List<WebElement> timeElements = driver.findElements(By.cssSelector(".list--match-schedule__time p"));
            List<WebElement> infoElements = driver.findElements(By.cssSelector(".label--match-option"));
//            List<WebElement> gElements = driver.findElements(By.cssSelector("match--label match--label_guest"));
            // 상품 링크 가져오기
            List<WebElement> productLinks = driver.findElements(By.cssSelector("li.list--match-schedule--item "));

            for (int i = 0; i < 5; i++) {
                String title = titleElements.get(i).getText();
                String time = timeElements.get(i).getText();
                String info = infoElements.get(i).getText();
//                String guest = gElements.get(i).getText();
                System.out.println("Title: " + title);
//                System.out.println("Time: " + time);
//                System.out.println("Info: " + info);

                // 링크 클릭
                WebElement productLink = productLinks.get(i);
                productLink.click();

                //날짜 및 시간 추출
                WebElement dateTimeElements = driver.findElement(By.xpath("//*[@id='matchDetailApp']/div[1]/div[2]/div[2]/div[1]/div[1]"));
                String matchTimeText = dateTimeElements.getText();
                System.out.println(matchTimeText);

                //날짜 부분만 추출
                String dateText = matchTimeText.split(" ")[0] +""+  matchTimeText.split(" ")[1];
                //시간 부분 추출
//                String timeText = matchTimeText.split(" ")[3]; //"13:00"
//                System.out.println("matchTime: " + matchTimeText);

                String[] dateParts = matchTimeText.split(" ");
                System.out.println("Array length: " + dateParts.length);
                if (dateParts.length >= 3) {
                    dateText = dateParts[0] + " " + dateParts[1];
                    System.out.println(dateParts.length); //4
                    System.out.println("Date Text: " + dateText);

                    // 시간 부분 추출 (예: "13:00")
                    String timeText = dateParts[3]; // 여기서 날짜와 시간이 결합된 형태일 수 있으므로 적절히 분리
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time1 = LocalTime.parse(timeText, timeFormatter);
                    System.out.println("Time Text: " + timeText);
                    System.out.println("Formatted Time: " + time1);
                } else {
                    // 에러 처리 혹은 기본 값 설정
                    System.out.println("Date text format is incorrect.");
                }

                //현재 연도 구하기
                Calendar now = Calendar.getInstance();
                int currentYear = now.get(Calendar.YEAR);

                //연도를 추가하여 최종 문자열 생성
                String dateTextWithYear = currentYear + " " + dateText; // "2024 9 3"

                //기존 형식을 SimpleDateFormat으로 파싱
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy MM월 dd일", Locale.KOREAN);
                Date date = originalFormat.parse(dateTextWithYear);

                // 날짜를 원하는 형식으로 포맷
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = targetFormat.format(date);

                //시간 부분 포맷
//                SimpleDateFormat originalTimeFormat = new SimpleDateFormat("HH:mm",Locale.KOREAN);
//                SimpleDateFormat targetTimeFormat = new SimpleDateFormat("HH:mm");
//                String formattedTime = targetTimeFormat.format(originalTimeFormat.parse(timeText));

                // 시간 포맷팅
//                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//                LocalTime time1 = LocalTime.parse(timeText, timeFormatter);

                System.out.println("Formatted Date: " + formattedDate);
//                System.out.println("Formatted Time: " + time1);

//                WebElement priceElements = driver.findElement(By.xpath("//*[@id=\"matchDetailApp\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]/div/span[1]"));

                // 추가 정보 추출 (이미지, 상세 설명 등)
                // 예시: 이미지 URL 추출
//                List<WebElement> images = driver.findElements(By.cssSelector(".slick-track img"));
//                for (WebElement img : images) {
//                    String imageUrl = img.getAttribute("src");
//                    System.out.println("Image URL: " + imageUrl);
//                }
                // 첫 번째 이미지 URL 추출
                WebElement firstImage = driver.findElement(By.cssSelector(".slick-track img"));
//                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // 10초 대기
//                WebElement firstImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".slick-track img")));
                String imageUrl = firstImage.getAttribute("src");
                System.out.println("First Image URL: " + imageUrl);


                // 예시: 상세 설명 추출
                WebElement descriptionElement = driver.findElement(By.cssSelector("div#mnRule.info__list__wrapper.double"));
                String description = descriptionElement.getText();
                //System.out.println("Description: " + description);
                // 줄바꿈(\n)을 기준으로 텍스트를 분리
                String[] descriptionItems = description.split("\n");

                // 각 항목별로 출력
                if (descriptionItems.length >= 5) {
                    String level = descriptionItems[0];          // "모든 레벨"
                    String gender = descriptionItems[1];         // "남녀 모두"
                    String matchType = descriptionItems[2];      // "6vs6 3파전"
                    String numberOfPlayers = descriptionItems[3];// "10~18명"
                    String shoesType = descriptionItems[4];      // "풋살화/운동화"

                    System.out.println("Level: " + level);
                    System.out.println("Gender: " + gender);
                    System.out.println("Match Type: " + matchType);
                    System.out.println("Number of Players: " + numberOfPlayers);
                    System.out.println("Shoes Type: " + shoesType);

                }


                //주소
                WebElement addressElement = driver.findElement(By.xpath("//*[@id=\"matchDetailApp\"]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/span[1]"));
                String Address = addressElement.getText();
                System.out.println("Address: " + Address);

                WebElement detailElement = driver.findElement(By.xpath("//*[@id=\"mnFeature\"]/div[2]/div[2]/div/pre"));
                String details = detailElement.getText();
                System.out.println("detail: " + details);

                String level = descriptionItems[0];          // "모든 레벨"
                String gender = descriptionItems[1];         // "남녀 모두"
                String matchType = descriptionItems[2];      // "6vs6 3파전"
                String numberOfPlayers = descriptionItems[3];// "10~18명"
                String shoesType = descriptionItems[4];      // "풋살화/운동화"

                String timeText = dateParts[3];
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime time1 = LocalTime.parse(timeText, timeFormatter);

                Item item = new Item();
                item.setItemNm(title);
                item.setPlayDate(formattedDate);
                item.setPlayTime(time1);
                item.setLevel(level);
                item.setGender(gender);
                item.setMatchInfo(matchType);
                item.setMemberNumber(numberOfPlayers);
                item.setShoes(shoesType);
                item.setPlayAddress(Address);
                item.setItemDetail(details);

                // 필요 시, 가격이나 기타 정보를 설정할 수 있음
                item.setPrice(10000); // Example price
                item.setStockNumber(18); // Example stock number
                item.setItemSellStatus(ItemSellStatus.SELL); // Example sell status

                // Repository를 통해 DB에 저장
                itemRepository.save(item);

                //이미지 저장(ItemImg)
                if(!"이미지 없음.".equals(imageUrl)){
                    ItemImg itemImg = new ItemImg();
                    itemImg.setItem(item);
                    itemImg.setRepImgYn("Y");

                    itemImgService.savedCrawlingImg(itemImg,imageUrl);
                }


                System.out.println("-------------------------");

                // 이전 페이지로 돌아가기
                driver.navigate().back();

                // 페이지가 완전히 로드되도록 대기 (필요에 따라 조정)
                Thread.sleep(10000);

                // 다시 요소를 가져오기 (이전 페이지로 돌아온 후)
                titleElements = driver.findElements(By.cssSelector(".match-list__title h3"));
                timeElements = driver.findElements(By.cssSelector(".list--match-schedule__time p"));
                infoElements = driver.findElements(By.cssSelector(".label--match-option"));
                productLinks = driver.findElements(By.cssSelector("li.list--match-schedule--item "));
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }
}
