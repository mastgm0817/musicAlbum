package com.spring.boot.musicAlbum.board.controller;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;


    @GetMapping("/bList/{id}")
    public String getBoard(@PathVariable Long id, Model model) {
        BoardDTO board = boardService.getBoard(id);
        if (board == null) {
            return "redirect:/";
        }
        model.addAttribute("board", board);
        return "bDetail";
    }

    @GetMapping("/bList")
    public String getAllbList(Model model) {
        List<BoardDTO> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "bList";
    }

    @GetMapping("/bUpdate/{id}")
    public String updateBoard(@PathVariable Long id, Model model) {
        BoardDTO board = boardService.getBoard(id);
        if (board == null) {
            return "redirect:/";
        }
        model.addAttribute("board", board);
        return "bDetail";
    }

    @PostMapping("/bUpdate/{id}")
    public String DetailBoard(@PathVariable Long id,
                              @ModelAttribute("board") BoardDTO newBoard,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam("soundFile") MultipartFile soundFile) throws IOException {
        BoardDTO Board = boardService.getBoard(id);
        if (Board != null) {
            Board.setBTitle(newBoard.getBTitle());
            Board.setBContent(newBoard.getBContent());
            Board.setBImage(newBoard.getBImage());
            Board.setBSound(newBoard.getBSound());
            Board.setCreatedAt(newBoard.getCreatedAt());
            boardService.updateBoard(id, Board, imageFile, soundFile);
        }
        return "redirect:/bList";
    }

    @GetMapping("/bAdd")
    public String addBoard() {
        return "bAdd";
    }


    @PostMapping("/bList")
    public String createBoard(
            @ModelAttribute("diary") BoardDTO boardDTO,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("soundFile") MultipartFile soundFile) throws IOException {
//        diaryService.createDiary(diary);
        boardService.createBoard(boardDTO, imageFile, soundFile);
        return "redirect:/bList";
    }


    // Update
//    @PostMapping("/{id}")
//    public String updateBoard(@PathVariable Long id,
//                              @ModelAttribute("diary") Diary newDiary,
//                              @RequestParam("imageFile") MultipartFile imageFile,
//                              @RequestParam("soundFile") MultipartFile soundFile) throws IOException {
//        Diary diary = diaryService.getDiary(id);
//        if (diary != null) {
//            diary.setTitle(newDiary.getTitle());
//            diary.setContent(newDiary.getContent());
////            diary.setImage(newDiary.getImage());
////            diary.setSound(newDiary.getSound());
////            diary.setCreateAt(newDiary.getCreateAt());
//            diaryService.updateDiary(id, diary, imageFile, soundFile);
//        }
//        return "redirect:/";
//    }


}
