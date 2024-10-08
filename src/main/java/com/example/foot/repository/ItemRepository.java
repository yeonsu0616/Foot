package com.example.foot.repository;

import com.example.foot.dto.ItemSearchDto;
import com.example.foot.dto.MainItemDto;
import com.example.foot.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//경고에요.
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // select * from item where itemNm = ?(String itemNm)
    @Query("SELECT i FROM Item i WHERE i.itemNm LIKE %:itemNm%")
    List<Item> findByItemNm(@Param("itemNm")String itemNm);
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    List<Item> findByPriceLessThan(Integer price);
    //select * from item where price < Integer price order by desc;
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail")String itemDetail);
    @Query(value = "select * from item i where i.item_Detail like %:itemDetail% order by i.price desc"
            ,nativeQuery = true)
    List<Item> findByItemDetailNative(@Param("itemDetail")String itemDetail);
    @Query("SELECT i FROM Item i ORDER BY FUNCTION('RAND')")
    List<Item> findRandomItems();
    @Query("SELECT i FROM Item i ORDER BY i.playTime ASC")
   List<MainItemDto> findAllByOrderByPlayTimeAsc();

    List<Item> findByPlayDate(String playDate);
    List<Item> findByPlayDateOrderByPlayTime(String playDate);
    @Query("SELECT i FROM Item i WHERE i.gender = '여자만'")
    List<Item> findByGender(String gender);

    @Query("SELECT i FROM Item i WHERE i.level = '아마추어 1이하'")
    List<Item> findByLevel1(String level);

    @Query("SELECT i FROM Item i WHERE i.level = '아마추어 2이상'")
    List<Item> findByLevel2(String level);
}
