package com.spring.boot.musicAlbum.board.service;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public BoardDTO getBoard(Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll();
    }


    @Value("${upload.path}")
    private String uploadPath;

    public BoardDTO createBoard(BoardDTO boardDTO,
                             MultipartFile imageFile,
                             MultipartFile soundFile) throws IOException {
        if (imageFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
            File file = new File(uploadPath + "/" + newFileName);
            imageFile.transferTo(file); // 파일 전송
            // S3(R2)로 전달
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(newFileName)
//                    .build();
//            s3Client.putObject(request, RequestBody.fromBytes(imageFile.getBytes()));
            boardDTO.setBImage(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
        if (soundFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + soundFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
            File file = new File(uploadPath + "/" + newFileName);
            soundFile.transferTo(file); // 파일 전송
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(newFileName)
//                    .build();
//            s3Client.putObject(request, RequestBody.fromBytes(soundFile.getBytes()));
            boardDTO.setBSound(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
//        diary.setCreateAt(LocalDateTime.now()); // 생성시의 시간 입력
        return boardRepository.save(boardDTO);
    }

    public BoardDTO updateBoard(Long id, BoardDTO newBoard,
                                MultipartFile imageFile,
                                MultipartFile soundFile) throws IOException {
        BoardDTO boardDTO = boardRepository.findById(id).orElse(null);
        if (boardDTO == null) {
            return null;
        }
        boardDTO.setBTitle(newBoard.getBTitle());
        boardDTO.setBContent(newBoard.getBContent());
        if (imageFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
            File file = new File(uploadPath + "/" + newFileName);
            imageFile.transferTo(file); // 파일 전송
            // S3(R2)로 전달
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(newFileName)
//                    .build();
//            s3Client.putObject(request, RequestBody.fromBytes(imageFile.getBytes()));
            boardDTO.setBImage(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
        if (soundFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + soundFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
            File file = new File(uploadPath + "/" + newFileName);
            soundFile.transferTo(file); // 파일 전송
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(newFileName)
//                    .build();
//            s3Client.putObject(request, RequestBody.fromBytes(soundFile.getBytes()));
            boardDTO.setBSound(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
        return boardRepository.save(boardDTO);
    }
}
