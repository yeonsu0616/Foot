package com.example.foot.repository;

import com.example.foot.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);
    @Query("select o from Order o where o.id = :id")
    Order findOrder(@Param("id") Long id); // 결제완료창에서 쓸거임

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);
    ///////////////////////////////////////////////////////////////////////////////////
//    @Query("select o from Order o" +
//            " left join fetch o.payment p" +
//            " left join fetch o.member m" +
//            " where o.id = :id")
//    Optional<Order> findOrderAndPaymentAndMember(Long id);
//
//    @Query("select o from Order o" +
//            " left join fetch o.payment p" +
//            " where o.orderUid = :orderUid")
//    Optional<Order> findOrderAndPayment(String orderUid);
    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch p.item i" +  // Payment와 Item을 조인
            " left join fetch o.member m" +
            " where o.id = :id")
    Optional<Order> findOrderAndPaymentAndMember(Long id);


    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch p.item i" +  // Payment와 Item을 조인
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPayment(String orderUid);



}
