package com.spring.boot.musicAlbum.comment.service;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.repository.BoardRepository;
import com.spring.boot.musicAlbum.comment.model.Comment;
import com.spring.boot.musicAlbum.comment.repository.CommentRepository;
import com.spring.boot.musicAlbum.exception.WrongUserExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    public List<Comment> getCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardDTOBId(boardId);
        return comments;
    }

    public Comment findCommentByCID(Long id){
        return commentRepository.findCommentBycId(id);
    }
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment saveComment(Long boardId, Comment comment) {
        BoardDTO board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        comment.setBoardDTO(board);
        return commentRepository.save(comment);
    }

    public Long deleteComment(Long commentId) throws WrongUserExceptionHandler {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 댓글을 찾을 수 없습니다: " + commentId));
        Long board_id = comment.getBoardDTO().getBId();

        commentRepository.delete(comment);
        return board_id;
    }
    public Long deleteComment(Long commentId, String cUserId) throws WrongUserExceptionHandler {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 댓글을 찾을 수 없습니다: " + commentId));
        Long board_id = comment.getBoardDTO().getBId();
        if (!comment.getCUserId().equals(cUserId)) {
            throw new WrongUserExceptionHandler("당신이 작성한 코멘트가 아닙니다.");
        }

        commentRepository.delete(comment);
        return board_id;
    }

    public void updateComment(Long commentId, Comment updatedComment, String username) throws IOException, WrongUserExceptionHandler {
        // Retrieve the existing comment with the given commentId from the database
        System.out.println("commentId = " + commentId);
        Comment existingComment = findCommentByCID(commentId);
        // Check if the comment exists
        if (existingComment != null) {
            String existUsername = existingComment.getCUserId();
            System.out.println("existUsername = " + existUsername);
            System.out.println("username = " + username);;

            if(!existUsername.equals(username)){
                throw new WrongUserExceptionHandler("당신이 작성한 코멘트가 아닙니다.");
            }
            // Apply the updated content to the existing comment
            existingComment.setCContent(updatedComment.getCContent());

            // Perform any additional modifications if necessary

            // Save the updated comment back to the database
            commentRepository.save(existingComment);

            // Return the updated comment
        }

    }

//    public boolean checkUsername(Long commentId, String username){
//        Comment existingComment = commentRepository.findById(commentId).orElse(null);
//        boolean result = existingComment.getCUserId().equals(username);
//        return result;
//    }
}

