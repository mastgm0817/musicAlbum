package com.spring.boot.musicAlbum.board.model;

import com.spring.boot.musicAlbum.comment.model.Comment;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name="board")
public class BoardDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BId;
    private String bTitle;
    private String bContent;
    private String bUserID;
    private String bPassword;
    private String bImage;
    private String bSound;

    @OneToMany(mappedBy = "boardDTO", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
