package com.spring.boot.musicAlbum.comment.repository;

import com.spring.boot.musicAlbum.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardDTOBId(Long boardId);
    Comment findCommentBycId(Long id);
}
