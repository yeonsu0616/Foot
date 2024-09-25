package com.example.foot.dto;

import com.example.foot.constant.ItemSellStatus;
import com.querydsl.core.annotations.QueryProjection;
import com.example.foot.entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private String playDate;
    private LocalTime playTime;
    private String matchInfo;
    private ItemSellStatus itemSellStatus;
    private String level;
    private String gender;
    @QueryProjection //Querydsl 결과 조회 시 MainItemDto 객체로 바로 오도록  활용
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price, LocalTime playTime,
                       String matchInfo, ItemSellStatus itemSellStatus,String level, String gender, String playDate){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.playTime=playTime;
        this.matchInfo=matchInfo;
        this.itemSellStatus=itemSellStatus;
        this.level = level;
        this.gender = gender;
        this.playDate=playDate;
    }


    public String getFormattedPlayTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return playTime.format(formatter);
    }


    @Override
    public String toString() {
        return "MainItemDto{" +
                "id=" + id +
                ", itemNm='" + itemNm + '\'' +
                '}';
    }






    public static MainItemDto of(Item item) {
        String imgUrl = (item.getItemImgList() != null && !item.getItemImgList().isEmpty())
                ? item.getItemImgList().get(0).getImgUrl()
                : null; // 이미지가 없으면 null
        return new MainItemDto(item.getId(), item.getItemNm(), item.getItemDetail(), imgUrl, item.getPrice(),
                item.getPlayTime(),item.getMatchInfo(),item.getItemSellStatus(),item.getLevel(),item.getGender(), item.getPlayDate());
    }



}
