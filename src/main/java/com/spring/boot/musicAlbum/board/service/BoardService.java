package com.spring.boot.musicAlbum.board.service;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private S3Client s3Client; // @Bean

    public BoardDTO getBoardById(Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll();
    }


//    @Value("${upload.path}")
//    private String uploadPath;

    public void createBoard(BoardDTO boardDTO,
                             MultipartFile imageFile,
                             MultipartFile soundFile) throws IOException {
        String bucketName = "project-file";
        if (imageFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
//            File file = new File(uploadPath + "/" + newFileName);
//            imageFile.transferTo(file); // 파일 전송
            // S3(R2)로 전달
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(imageFile.getBytes()));
            boardDTO.setBImage(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
        if (soundFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + soundFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
//            File file = new File(uploadPath + "/" + newFileName);
//            soundFile.transferTo(file); // 파일 전송
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(soundFile.getBytes()));
            boardDTO.setBSound(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
//        diary.setCreateAt(LocalDateTime.now()); // 생성시의 시간 입력
        boardRepository.save(boardDTO);
    }

    public void updateBoard(BoardDTO newBoard,
                                MultipartFile imageFile,
                                MultipartFile soundFile) throws IOException {
        String bucketName = "project-file";
        BoardDTO existedBoard = getBoardById(newBoard.getId());
        if (existedBoard == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        // 업데이트할 속성들을 갱신
        existedBoard.setBTitle(newBoard.getBTitle());
        existedBoard.setBContent(newBoard.getBContent());
        existedBoard.setCreatedAt(newBoard.getCreatedAt());


        if (imageFile != null && !imageFile.isEmpty()) {
            String newFileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(imageFile.getBytes()));

            if (existedBoard.getBImage() != null) {
                deleteFileFromR2(existedBoard.getBImage());
            }

            existedBoard.setBImage(newFileName);
        }

        if (soundFile != null && !soundFile.isEmpty()) {
            String newFileName = System.currentTimeMillis() + "-" + soundFile.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(soundFile.getBytes()));

            if (existedBoard.getBSound() != null) {
                deleteFileFromR2(existedBoard.getBSound());
            }

            existedBoard.setBSound(newFileName);
        }

        boardRepository.save(existedBoard);
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    public byte[] loadFile(String key) throws IOException {
        String bucketName = "project-file";
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3Client.getObject(request).readAllBytes();
    }

    public void deleteFileFromR2(String fileName) {
        String bucketName = "project-file";
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(request);
    }

}
