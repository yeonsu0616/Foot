package com.example.foot.service;

import com.example.foot.constant.ItemSellStatus;
import com.example.foot.dto.ItemFormDto;
import com.example.foot.dto.ItemImgDto;
import com.example.foot.dto.ItemSearchDto;
import com.example.foot.dto.MainItemDto;
import com.example.foot.entity.Item;
import com.example.foot.entity.ItemImg;
import com.example.foot.repository.ItemImgRepository;
import com.example.foot.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    public Long saveItem(@Valid ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        //이미지 등록
        for(int i =0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        //Entity
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        //DB에서 데이터를 가지고 옵니다.
        //DTO
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //왜 DTO 만들었나요??

        for(ItemImg itemimg : itemImgList){
            // Entity -> DTO
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // Item -> ItemFormDto modelMapper
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품 변경
        Item item = itemRepository.findById(itemFormDto.getId()).
                orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        //상품 이미지 변경
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i =0; i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }
    @Transactional(readOnly = true) // 쿼리문 실행 읽기만 한다.
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
//    @Transactional(readOnly = true)
//    public List<MainItemDto> getRandomItems(int count) {
//        List<Item> items = itemRepository.findRandomItems();
//        List<MainItemDto> mainItemDtos = new ArrayList<>();
//
//        for (Item item : items) {
//            MainItemDto mainItemDto = MainItemDto.of(item); // Item을 MainItemDto로 변환
//            mainItemDtos.add(mainItemDto); // 리스트에 추가
//        }
//
//        return mainItemDtos.stream().limit(count).toList(); // 최대 count 개수로 제한
//    }

    @Transactional(readOnly = true)
    public List<Item> getItemAll(){
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MainItemDto> getItemsByDate(String playDate) {
        List<Item> items = itemRepository.findByPlayDate(playDate);

        // Entity를 DTO로 변환하여 반환
        return items.stream()
                .map(MainItemDto::of) // Item -> MainItemDto 변환
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MainItemDto> getItemsByDateSortedByTime(String playDate) {
        return itemRepository.findByPlayDateOrderByPlayTime(playDate)
                .stream()
                .map(MainItemDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Item> getItemByGender(String gender){
        return itemRepository.findByGender(gender);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemByLevel1(String level){
        return itemRepository.findByLevel1(level);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemByLevel2(String level){
        return itemRepository.findByLevel2(level);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemByInside(String itemNm){
        return itemRepository.findByItemNm(itemNm);
    }

    public void saveCrawledData(String title, String formattedDate, String formattedTime, String firstImageUrl,
                                String level, String gender, String matchType, String numberOfPlayers, String shoesType,
                                String address, String detail) {

        Item item = new Item();
        ItemImg itemImg = new ItemImg();
        item.setItemNm(title);
        item.setPlayDate(formattedDate);
        item.setPlayTime(LocalTime.parse(formattedTime)); // assuming formattedTime is in HH:mm format
        itemImg.setImgUrl(firstImageUrl);
        item.setLevel(level);
        item.setGender(gender);
        item.setMatchInfo(matchType);
        item.setMemberNumber(numberOfPlayers);
        item.setShoes(shoesType);
        item.setPlayAddress(address);
        item.setItemDetail(detail);

        // You can set price, stockNumber, and other fields as needed
        item.setPrice(10000); // Example price
        item.setStockNumber(18); // Example stock number
        item.setItemSellStatus(ItemSellStatus.SELL); // Example sell status

        // Save the item to the database
        itemRepository.save(item);
    }

//    public List<MainItemDto> getRecentItemsFromSession(List<MainItemDto> recentItems, MainItemDto mainItemDto) {
//        if (recentItems == null) {
//            recentItems = new ArrayList<>();
//        }
//
//        // 기존 리스트에서 중복된 항목 제거
//        recentItems.removeIf(item -> item.getId().equals(mainItemDto.getId()));
//
//        // 리스트에 새 항목 추가
//        recentItems.add(0, mainItemDto);
//
//        // 리스트 크기 제한
//        if (recentItems.size() > 5) {
//            recentItems = recentItems.subList(0, 5);
//        }
//
//        return recentItems;
//    }
//
//    public MainItemDto findRItemById(Long id) {
//        return itemRepository.findById(id)
//                .map(item -> new MainItemDto(item.getId(), item.getItemNm(), item.getItemDetail(), item.getGender(), item.getPrice(), item.getPlayTime(),
//                        item.getMatchInfo(),item.getItemSellStatus(),item.getLevel(),item.getPlayDate(),item.getPlayAddress()))
//                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + id));
//    }
}
