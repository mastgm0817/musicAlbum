package com.spring.boot.musicAlbum.board.controller;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.service.BoardService;
import com.spring.boot.musicAlbum.comment.model.Comment;
import com.spring.boot.musicAlbum.comment.service.CommentService;
import com.spring.boot.musicAlbum.exception.WrongBoardExceptionHandler;
import com.spring.boot.musicAlbum.exception.WrongUserExceptionHandler;
import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private S3Client s3Client; // @Bean

    @GetMapping("/bAdd")
    public String addBoard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardDTO board = new BoardDTO();
        if (authentication != null) {
            String username = authentication.getName();
            board.setBUserID(username);
            model.addAttribute("board", board);
        }
        return "bAdd";
    }


    @PostMapping("/bAdd")
    public String add(@ModelAttribute BoardDTO board,
                      @RequestParam("imageFile") MultipartFile imageFile,
                      @RequestParam("soundFile") MultipartFile soundFile,
                      RedirectAttributes redirectAttributes) throws IOException {
        boardService.createBoard(board, imageFile, soundFile);
        redirectAttributes.addFlashAttribute("msg","게시글이 작성되었습니다.");
        return "redirect:/bList";
    }


    @GetMapping("/bList")
    public String getAllbList(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        int pageSize = 5; // 페이지당 게시물 수
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<BoardDTO> boardPage = boardService.getAllBoards(pageable);
        List<BoardDTO> boards = boardPage.getContent();
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardPage.getTotalPages());
        return "bList";
    }



    @GetMapping("/bDetail/{id}")
    public String getBoardDetail(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.getBoardById(id);
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        model.addAttribute("board", boardDTO);
        model.addAttribute("comment", new Comment());
        model.addAttribute("comments", comments);
        return "bDetail";
    }

    @GetMapping("/bUpdate/{id}")
    public String updateBoard(@PathVariable Long id, Model model) {
        BoardDTO board = boardService.getBoardById(id);
        if (board == null) {
            return "redirect:/";
        }
        model.addAttribute("board", board);
        return "bUpdate";
    }

    @PostMapping("/edit")
    public String UpdateBoard(@ModelAttribute("board") BoardDTO newBoard,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam("soundFile") MultipartFile soundFile,
                              RedirectAttributes redirectAttributes) throws IOException, WrongUserExceptionHandler {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            boardService.updateBoard(newBoard,username, imageFile,soundFile);
            redirectAttributes.addFlashAttribute("msg", "정상적으로 수정되었습니다.");
        }
        return "redirect:/bList";
    }


    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id) throws WrongBoardExceptionHandler {
        // 게시글 삭제 전에 파일도 함께 삭제
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long board_id = 0L;
        if (authentication != null) {
            String username = authentication.getName();
            Account account = accountService.findUserByUsername(username);
            if(account.getRole().equals("admin")){
                boardService.deleteBoard(id);
            }
            else{
                boardService.deleteBoard(id,username);
            }

            BoardDTO board = boardService.getBoardById(id);
            if (board != null) {
                // R2에서 파일 삭제
                if (board.getBImage() != null) {
                    boardService.deleteFileFromR2(board.getBImage());
                }

                if (board.getBSound() != null) {
                    boardService.deleteFileFromR2(board.getBSound());
                }
            }
            // 게시글 삭제
        }


        return "redirect:/bList";
    }

    @GetMapping("/upload/{filename}")
    public ResponseEntity<?> upload(@PathVariable String filename) throws IOException {
        byte[] byteArray = boardService.loadFile(filename);
        return new ResponseEntity<>(byteArray, HttpStatus.OK);
    }


}
