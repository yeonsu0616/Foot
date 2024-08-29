package com.example.foot.service;


import com.example.foot.dto.CommentDTO;
import com.example.foot.entity.CommentEntity;
import com.example.foot.entity.Item;
import com.example.foot.repository.CommentRepository;
import com.example.foot.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    public Long save(CommentDTO commentDTO) {
        //부모 엔티티(Item) 조회
        Optional<Item> optionalItem = itemRepository.findById(commentDTO.getItemId());
        //optionalItem가 존재하면
        if (optionalItem.isPresent()) {
            //item id 추출
            Item item = optionalItem.get();
            //dto객체에서 엔티티 객체로 변환

            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, item);
            //엔티티 객체를 데이터베이스에 저장하고 id를 리턴한다.

            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }

    }

    public List<CommentDTO> findAll(Long itemId) {
        // select * from comment_table where item_id=? order by id desc;
        Item item = itemRepository.findById(itemId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByItemOrderByIdDesc(item);
        //EntityList -> DTOList
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, itemId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    // 댓글 삭제 메서드
    public void deleteComment(Long id) throws Exception {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new Exception("댓글을 찾을 수 없습니다.");
        }
    }
}
