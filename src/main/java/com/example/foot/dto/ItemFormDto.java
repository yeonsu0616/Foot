package com.example.foot.dto;

import com.example.foot.constant.ItemSellStatus;
import com.example.foot.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class ItemFormDto {
    // Item
    private Long id;



    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private  String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    @NotNull(message = "날짜는 필수 입력 값입니다.")
    private String playDate;

    @NotNull(message = "시간은 필수 입력 값입니다.")
    private LocalTime playTime;

    @NotNull(message = "주소는 필수 입력 값입니다.")
    private String playAddress;

    @NotNull(message = "레벨은 필수 입력 값입니다.")
    private String level;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @NotNull(message = "정보는 필수 입력 값입니다.")
    private String matchInfo;

    @NotNull(message = "인원수는 필수 입력 값입니다.")
    private String memberNumber;

    @NotNull(message = "신발은 필수 입력 값입니다.")
    private String shoes;

    //----------------------------------------------------------------------------
    //ItemImg
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //상품 이미지 정보

    private List<Long> itemImgIds = new ArrayList<>(); //상품 이미지 아이디

    //--------------------------------------------------------------------------------------
    // ModelMapper
    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        // ItemFormDto -> Item 연결
        return modelMapper.map(this, Item.class);
    }
    public static ItemFormDto of(Item item){
        // Item -> ItemFormDto 연결
        return modelMapper.map(item, ItemFormDto.class);
    }
    public String getFormattedPlayDateWithDayOfWeek() {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM월 dd일");
        LocalDate date = LocalDate.parse(playDate, inputFormatter);

        // 요일 구하기
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        // 최종 포맷팅된 문자열
        return date.format(outputFormatter) + " (" + dayOfWeek + ")";
    }
}
