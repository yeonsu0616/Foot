package com.example.foot.controller;

import com.example.foot.dto.ItemFormDto;
import com.example.foot.dto.ItemSearchDto;
import com.example.foot.dto.MainItemDto;
import com.example.foot.dto.SessionUser;
import com.example.foot.entity.Item;
import com.example.foot.repository.ItemRepository;
import com.example.foot.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.foot.entity.QItem.item;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final HttpSession httpSession;

    private final ItemRepository itemRepository;
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model,
                       HttpSession session,Item item,MainItemDto mainItemDto,
                       @RequestParam Optional<Long>itemId) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

//        //최근 본 상품 목록 관리
//        List<MainItemDto> recentItems = (List<MainItemDto>)session.getAttribute("recentItems");
//        recentItems = itemService.getRecentItemsFromSession(recentItems, mainItemDto);
//
//        if (itemId.isPresent()) {
//            // 현재 조회 중인 상품 ID
//            Long currentItemId = itemId.get();
//
//            // 상품 정보를 조회하여 DTO로 변환
//            MainItemDto currentItem = itemService.findRItemById(currentItemId);
//
//            // 최근 본 상품 목록 업데이트
//            recentItems = itemService.getRecentItemsFromSession(recentItems, currentItem);
//
//            // 업데이트된 최근 본 상품 목록을 세션에 저장
//            session.setAttribute("recentItems", recentItems);
//        }
//
//        // 최근 본 상품 목록을 모델에 추가
//        model.addAttribute("recentItems", recentItems);
//        System.out.println("Recent Items: " + recentItems);



        //등록된 상품의 시간으로 오름차순 정렬
        List<MainItemDto> timeitems = itemRepository.findAllByOrderByPlayTimeAsc();
        // 오늘 날짜에 해당하는 상품을 시간 오름차순으로 정렬하여 가져오기
        LocalDate today = LocalDate.now();
        List<MainItemDto> todayItems = itemService.getItemsByDateSortedByTime(today.toString());

        System.out.println(items.getNumber()+"!!!!!!!!!!");
        System.out.println(items.getTotalPages()+"#########");
//        List<MainItemDto> randomItems = itemService.getRandomItems(5);
        model.addAttribute("items", items);
//        model.addAttribute("TimeItem",timeitems);
        model.addAttribute("TimeItem",todayItems);
//        for (MainItemDto item : todayItems) {
//            System.out.println("Item ID: " + item.getId() + ", PlayTime: " + item.getFormattedPlayTime());
//        }

//        model.addAttribute("randomItems",randomItems);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "main";
    }

    // Ajax 요청을 처리하여 특정 날짜에 해당하는 상품을 반환하는 메서드
//    @GetMapping("/getItemsByDate")
//    @ResponseBody
//    public List<MainItemDto> getItemsByDate(@RequestParam("playDate") String playDate) {
//        // 서비스에서 playDate에 해당하는 아이템을 가져옴
//        return itemService.getItemsByDate(playDate);
//    }
    @RequestMapping(value = "/getItemsByDate", method = RequestMethod.GET)
    @ResponseBody
    public List<MainItemDto> getItemsByDate(@RequestParam("playDate") String playDate) {

        System.out.println("Requested playDate1: " + playDate);
        return itemService.getItemsByDateSortedByTime(playDate);
    }

    @GetMapping(value = "/inside")
    public String inside(Model model){
        List<Item> inside = itemService.getItemByInside("실내");

        Map<String, List<Item>> itemsByDate = inside.stream()
                .collect(Collectors.groupingBy(Item::getPlayDate));
        model.addAttribute("itemsByDate",itemsByDate);
        return "/icons/inside";
    }
    @GetMapping(value = "/beginner")
    public String beginner(Model model){
        List<Item> beginnerItems = itemService.getItemByLevel1("아마추어 1이하");

        Map<String, List<Item>> itemsByDate = beginnerItems.stream()
                .collect(Collectors.groupingBy(Item::getPlayDate));

        model.addAttribute("itemsByDate",itemsByDate);
        return "/icons/beginner";
    }
    @GetMapping(value = "/woman")
    public String woman(Model model) {
        List<Item> womanItems = itemService.getItemByGender("여자만");

        // 날짜별로 아이템을 그룹화
        Map<String, List<Item>> itemsByDate = womanItems.stream()
                .collect(Collectors.groupingBy(Item::getPlayDate)); // Item 클래스에 getDate() 메소드가 있다고 가정

        // 모델에 추가
        model.addAttribute("itemsByDate", itemsByDate);
        System.out.println(itemsByDate);
        return "/icons/woman";
    }
    @GetMapping(value = "/semi")
    public String semi(Model model){
        List<Item> semiItems = itemService.getItemByLevel2("아마추어 2이상");

        // 날짜별로 아이템을 그룹화
        Map<String, List<Item>> itemsByDate = semiItems.stream()
                .collect(Collectors.groupingBy(Item::getPlayDate)); // Item 클래스에 getDate() 메소드가 있다고 가정

        // 모델에 추가
        model.addAttribute("itemsByDate", itemsByDate);
        return "/icons/semi";
    }


}
