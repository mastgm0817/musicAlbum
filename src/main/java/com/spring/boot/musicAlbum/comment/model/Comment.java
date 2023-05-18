package com.spring.boot.musicAlbum.comment.model;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.login.model.Account;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cId;
    private String cContent;
    // 게시글과 댓글 간의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BId")
    private BoardDTO boardDTO;
    private String cUserId;

}