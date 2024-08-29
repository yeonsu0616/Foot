package com.example.foot.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
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



}
