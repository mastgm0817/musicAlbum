package com.spring.boot.musicAlbum.comment.controller;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.service.BoardService;
import com.spring.boot.musicAlbum.comment.model.Comment;
import com.spring.boot.musicAlbum.comment.service.CommentService;
import com.spring.boot.musicAlbum.exception.WrongUserExceptionHandler;
import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private BoardService boardService;

    @PostMapping("/bDetail/{id}/comments")
    public String createComment(@PathVariable Long id, @ModelAttribute Comment comment) {
        comment.setBoardDTO(boardService.getBoardById(id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            comment.setCUserId(username);
        }
        commentService.saveComment(id, comment);
        return "redirect:/bDetail/" + id;
    }

    @GetMapping("/bDetail/{id}/comments")
    public String getCommentsByBoardId(@PathVariable("id") Long boardId, Model model) {
        List<Comment> comments = commentService.getCommentsByBoardId(boardId);
        model.addAttribute("comments", comments);
        return "comments";
    }

    @GetMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws WrongUserExceptionHandler {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long board_id = 0L;
        if (authentication != null) {
            String username = authentication.getName();
            Account account = accountService.findUserByUsername(username);
            if(account.getRole().equals("admin")){
                board_id = commentService.deleteComment(id);
            }
            else{
                board_id = commentService.deleteComment(id,username);
            }

        }

        return "redirect:/bDetail/" + board_id;
    }
    @PostMapping("/Update/{id}/comments")
    public String updateComment(@PathVariable("id") Long id,
                                @RequestParam("BId") Long BId,
                                @RequestParam("cId") Long cId,
                                @ModelAttribute("comment") Comment updatedComment) throws IOException, WrongUserExceptionHandler {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String username = authentication.getName();
            System.out.println("cId = " + cId);
            commentService.updateComment(cId, updatedComment, username);
        }

        return "redirect:/bDetail/" + BId;
    }
}
