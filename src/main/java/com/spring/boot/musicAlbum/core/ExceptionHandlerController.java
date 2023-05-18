package com.spring.boot.musicAlbum.core;

import com.spring.boot.musicAlbum.exception.WrongBoardExceptionHandler;
import com.spring.boot.musicAlbum.exception.WrongUserExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice //보조형 컨트롤러 -> Exception 받아주는 역할
public class ExceptionHandlerController {
    @ExceptionHandler(WrongUserExceptionHandler.class)
    public String handleException(WrongUserExceptionHandler ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("msg","당신이 작성한거 맞아?");
        return "redirect:/bList";
    }
    @ExceptionHandler(WrongBoardExceptionHandler.class)
    public String handleException(WrongBoardExceptionHandler ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("msg","당신이 작성한 게시글이 아닌뒤?");
        return "redirect:/bList";
    }
}