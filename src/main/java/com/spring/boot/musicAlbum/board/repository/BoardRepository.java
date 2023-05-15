package com.spring.boot.musicAlbum.board.repository;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardDTO, Long> {
}
