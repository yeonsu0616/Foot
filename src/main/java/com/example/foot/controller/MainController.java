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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final HttpSession httpSession;

    private final ItemRepository itemRepository;
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
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

}
