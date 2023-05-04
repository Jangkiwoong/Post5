package com.sprta.hanghae992.service;


import com.sprta.hanghae992.dto.*;
import com.sprta.hanghae992.entity.*;
import com.sprta.hanghae992.repository.CommentGoodRepository;
import com.sprta.hanghae992.repository.CommentRepository;
import com.sprta.hanghae992.repository.PostRepository;

import com.sprta.hanghae992.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentGoodRepository commentGoodRepository;



    //댓글 추가
    @Transactional
    public ResponseEntity addComment(CmtRequestDto cmtRequestDto, User user) {



                Post post = postRepository.findById(cmtRequestDto.getPostId()).orElseThrow(
                        () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
                );


                Comment comment = commentRepository.saveAndFlush(new Comment(cmtRequestDto, user.getUsername()));
                post.commentListadd(comment);
                Long temp = commentGoodRepository.count();
                CmtResponseDto cmtResponseDto = new CmtResponseDto(comment, temp);
                return ResponseEntity.status(200).body(cmtResponseDto);
            }




    //댓글 수정
    @Transactional
    public ResponseEntity updateComment(Long id, CmtRequestDto cmtRequestDto, User user) {


            Long temp = commentGoodRepository.count();
            //사용자 권환 확인 (ADMUN인지 아닌지)
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);
            if( userRoleEnum == UserRoleEnum.ADMIN) {
                Comment comment = commentRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
                );
                comment.update(cmtRequestDto,temp);
                CmtResponseDto cmtResponseDto = new CmtResponseDto(comment,temp);
                return ResponseEntity.status(200).body(cmtResponseDto);
            }else {


                Comment comment = commentRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                        () -> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
                );
                if (comment == null) {
                    MsgResponseDto msgResponseDto = new MsgResponseDto("작성자만 삭제/수정할 수 있습니다.", 400);
                    return ResponseEntity.status(400).body(msgResponseDto);
                } else {
                    comment.update(cmtRequestDto,temp);
                    CmtResponseDto cmtResponseDto = new CmtResponseDto(comment, temp);
                    return ResponseEntity.status(200).body(cmtResponseDto);
                }
            }

            }


//

    //댓글 삭제
    @Transactional
    public ResponseEntity deleteComment(Long id, User user) {


                //사용자 권환 확인 (ADMUN인지 아닌지)
                UserRoleEnum userRoleEnum = user.getRole();
                System.out.println("role = " + userRoleEnum);
                if( userRoleEnum == UserRoleEnum.ADMIN) {
                    Comment comment = commentRepository.findById(id).orElseThrow(
                            () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
                    );
                    commentGoodRepository.deleteByComment(comment);
                    commentRepository.delete(comment);
                    MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 삭제 성공", 200);
                    return ResponseEntity.status(200).body(msgResponseDto);
                }else {

                    Comment comment = commentRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                            () -> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
                    );


                    commentGoodRepository.deleteByComment(comment);
                    commentRepository.delete(comment);
                    MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 삭제 성공", 200);
                    return ResponseEntity.status(200).body(msgResponseDto);

            }
    }

    // 댓글 좋아요 API
    @Transactional
    public ResponseEntity CommentGood(Long commentId, UserDetailsImpl userDetails) {
        // 요청받은 commentId 값을 가진 댓글을 조회합니다.
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 현재 사용자가 해당 댓글에 대해 좋아요를 이미 눌렀는지 조회합니다.
        CommentGood commentGood = commentGoodRepository.findByUsernameAndComment(userDetails.getUsername(), comment);

        if (commentGood == null) { // 사용자가 해당 댓글에 대해 좋아요를 처음 요청하는 경우
            // CommentGood 테이블에 새로운 레코드를 생성하고, Comment의 좋아요 수를 1 증가시킵니다.
            commentGoodRepository.save(new CommentGood(userDetails.getUsername(), comment));
            comment.setGood(comment.getGood() + 1);
            commentRepository.save(comment);

            // 요청이 성공한 경우, 클라이언트에게 "좋아요 성공" 메시지와 상태코드 200을 반환합니다.
            MsgResponseDto msgResponseDto = new MsgResponseDto("좋아요 성공", 200);
            return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        } else { // 사용자가 이미 해당 댓글에 대해 좋아요를 누른 경우
            // CommentGood 테이블에서 해당 레코드를 삭제하고, Comment의 좋아요 수를 1 감소시킵니다.
            commentGoodRepository.delete(commentGood);
            comment.setGood(comment.getGood() - 1);
            commentRepository.save(comment);

            // 요청이 성공한 경우, 클라이언트에게 "좋아요 삭제 성공" 메시지와 상태코드 200을 반환합니다.
            MsgResponseDto msgResponseDto = new MsgResponseDto("좋아요 삭제 성공", 200);
            return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        }
    }
    //게시물의 댓글 좋아요 기능을 @OneToMany로 리스트를 만들어줬을 경우
//해당 게시물에 좋아요한 기록을 찾을 때, 결과가 하나 이상인 경우를 처리해야 합니다. 예를 들어,
// 반환된 PostGood 객체 중에서 가장 최근에 좋아요한 시간을 가진 객체를 삭제하거나,
// 모든 객체를 삭제하는 등의 처리를 할 수 있습니다.



}


