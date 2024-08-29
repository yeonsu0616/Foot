package com.example.foot.entity;

import com.example.foot.constant.ItemSellStatus;
import com.example.foot.dto.ItemFormDto;
import com.example.foot.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 상품코드

    @Column(nullable = false,length = 50)
    private String itemNm; // 상품명


    @Column(name = "price", nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 수량

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매 상태

    @Column(name="playDate")
    private String playDate;

    @Column(name="playTime")
    private LocalTime playTime;

    @Column(name="address")
    private String playAddress;

    @Column(name="level")
    private String level;

    @Column(name="gender")
    private String gender;

    @Column(name="matchInfo")
    private String matchInfo;

    @Column(name="memberNumber")
    private String memberNumber;

    @Column(name="shoes")
    private String shoes;

    //private LocalDateTime regTime; // 등록 시간

    //private LocalDateTime updateTime; // 수정 시간

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="member_item",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> member;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.playDate=itemFormDto.getPlayDate();
        this.playTime = itemFormDto.getPlayTime();
        this.playAddress=itemFormDto.getPlayAddress();
        this.level=itemFormDto.getLevel();
        this.gender=itemFormDto.getGender();
        this.matchInfo=itemFormDto.getMatchInfo();
        this.memberNumber=itemFormDto.getMemberNumber();
        this.shoes=itemFormDto.getShoes();


    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber; // 10,  5 / 10, 20
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: "+this.stockNumber+")");
        }
        this.stockNumber = restStock; // 5
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

    @Getter
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemImg> itemImgList;

    // 연관 관계 설정
    @OneToMany(mappedBy = "item")
    private List<Payment> payments;
}
