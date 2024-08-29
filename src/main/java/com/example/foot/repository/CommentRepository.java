package com.example.foot.repository;

import com.example.foot.entity.CommentEntity;
import com.example.foot.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // select * from comment_table where item_id=? order by id desc;
    List<CommentEntity> findAllByItemOrderByIdDesc(Item item);
}
