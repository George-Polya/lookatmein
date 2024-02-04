package com.ssafy.lam.freeboard.service;

import com.ssafy.lam.comment.domain.Comment;
import com.ssafy.lam.comment.dto.CommentRequestDto;
import com.ssafy.lam.comment.service.CommentService;
import com.ssafy.lam.exception.NoArticleExeption;
import com.ssafy.lam.freeboard.domain.Freeboard;
import com.ssafy.lam.freeboard.domain.FreeboardRepository;
import com.ssafy.lam.freeboard.dto.FreeboardRequestDto;
import com.ssafy.lam.freeboard.dto.FreeboardResponseDto;
import com.ssafy.lam.user.domain.User;
import com.ssafy.lam.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeboardServiceImpl implements FreeboardService {
    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;

    private Logger log = LoggerFactory.getLogger(FreeboardServiceImpl.class);


    @Override
    public Freeboard createFreeboard(FreeboardRequestDto freeboardRequestDto) {

        User user = userRepository.findById(freeboardRequestDto.getUser_seq()).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        System.out.println("user = " + user);
        log.info("글 등록 유저 정보: {}", user);
        Freeboard freeboard = Freeboard.builder()
                .user(user)
                .title(freeboardRequestDto.getFreeBoard_title())
                .content(freeboardRequestDto.getFreeBoard_content())
                .build();

        return freeboardRepository.save(freeboard);
    }

    @Override
    public List<Freeboard> getAllFreeboards() {
        List<Freeboard> freeboardList = freeboardRepository.findByIsDeletedFalse();
    
        return freeboardList;
    }

    @Override
    public FreeboardResponseDto getFreeboard(Long freeBoardSeq) {
        Freeboard freeboard = freeboardRepository.findByfreeboardSeqAndIsDeletedFalse(freeBoardSeq).orElseThrow(() -> new NoArticleExeption("해당 게시글이 없습니다."));
        FreeboardResponseDto freeboardResponseDto = FreeboardResponseDto.builder()
                .freeboardSeq(freeboard.getFreeboardSeq())
                .userId(freeboard.getUser().getUserId())
                .freeboardTitle(freeboard.getTitle())
                .freeboardContent(freeboard.getContent())
                .freeboardRegisterdate(freeboard.getRegisterDate())
                .build();


        List<Comment> commentList = commentService.getAllComments(freeBoardSeq);
        List<CommentRequestDto> commentRequestDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
//            System.out.println("comment = " + comment.getContent());
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .comment_seq(comment.getSeq())
                    .comment_content(comment.getContent())
                    .customer_name(comment.getUserId())
                    .customerId(comment.getUserId())
                    .freeboard_seq(comment.getFreeboard().getFreeboardSeq())
                    .regdate(comment.getRegdate())
                    .build();
            commentRequestDtoList.add(commentRequestDto);
        }

        freeboardResponseDto.setComments(commentRequestDtoList);

        return freeboardResponseDto;
    }

    @Override
    public Freeboard updateFreeboard(Long user_seq, FreeboardRequestDto updateFreeboardRequestDto) {
        Freeboard freeboard = freeboardRepository.findById(updateFreeboardRequestDto.getFreeBoard_seq())
                .orElseThrow(() -> new NoArticleExeption("해당 게시글이 없습니다."));

        if(freeboard.getUser().getUserSeq() != user_seq){
            throw new IllegalArgumentException("해당 게시글을 수정할 권한이 없습니다.");
        }

        Freeboard updatedFreeboard = Freeboard.builder()
                .freeboardSeq(freeboard.getFreeboardSeq())
                .title(updateFreeboardRequestDto.getFreeBoard_title())
                .content(updateFreeboardRequestDto.getFreeBoard_content())
                .build();


        return freeboardRepository.save(updatedFreeboard);
    }


    @Override
    public Freeboard deleteFreeboard(Long freeBoardSeq) throws NoArticleExeption {

        Freeboard freeboard = freeboardRepository.findById(freeBoardSeq).orElseThrow(() -> new NoArticleExeption("해당 게시글이 없습니다."));
        List<Comment> commentList = commentService.getAllComments(freeBoardSeq);


        log.info("게시글 삭제 전 정보: {}", freeboard);
        freeboard.setDeleted(true);
        freeboardRepository.save(freeboard);
        for(Comment comment : commentList){
            commentService.deleteComment(comment.getSeq());
        }

        log.info("게시글 삭제 후 정보: {}", freeboard);



        return freeboard;

    }

    @Override
    public List<Freeboard> getFreeboardByUserSeq(Long userSeq) {
        List<Freeboard> freeboards = freeboardRepository.findByUserUserSeqAndIsDeletedFalse(userSeq);
        return freeboards;
    }
}
