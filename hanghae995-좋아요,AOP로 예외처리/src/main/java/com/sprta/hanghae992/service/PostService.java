package com.sprta.hanghae992.service;

import com.sprta.hanghae992.dto.MsgResponseDto;
import com.sprta.hanghae992.dto.PostRequestDto;
import com.sprta.hanghae992.dto.PostResponseDto;
import com.sprta.hanghae992.entity.*;
import com.sprta.hanghae992.jwt.JwtUtil;
import com.sprta.hanghae992.repository.CommentRepository;
import com.sprta.hanghae992.repository.PostGoodRepository;
import com.sprta.hanghae992.repository.PostRepository;
import com.sprta.hanghae992.repository.UserRepository;

import com.sprta.hanghae992.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final PostGoodRepository postGoodRepository;

    //게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {



        // Request에서 Token 가져오기
            Post post = postRepository.saveAndFlush(new Post(postRequestDto, user.getId(),user.getUsername()));
            return new PostResponseDto(post);
        }




    //게시글 전체 조회
    @Transactional
    public List<PostResponseDto> getPost() {
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> postResponseDtoList = new ArrayList();
        for(Post post : postList){
            postResponseDtoList.add(new PostResponseDto(post));
        }


//        List<Post> posts = postRepository.findAllFetchJoin();
//        return posts.stream().map(PostResponseDto::new).collect(Collectors.toList());
        return postResponseDtoList;
    }

    //게시글 수정
    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(Long id, PostRequestDto postRequestDto, User user) {

        //사용자 권환 확인 (ADMUN인지 아닌지)
        UserRoleEnum userRoleEnum = user.getRole();
        System.out.println("role = " + userRoleEnum);
        if (userRoleEnum == UserRoleEnum.ADMIN) {
            Post post = postRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
            );
            post.update(postRequestDto);
            PostResponseDto postResponseDto = new PostResponseDto(post);
            return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
        } else {

            Post post= postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
            );

                post.update(postRequestDto);
            PostResponseDto postResponseDto = new PostResponseDto(post);
            return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
            }
        }




    //게시글 삭제
    @Transactional
    public ResponseEntity deleteMemo(Long id, User user) {

            //사용자 권환 확인 (ADMUN인지 아닌지)
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);
            if (userRoleEnum == UserRoleEnum.ADMIN) {
                Post post = postRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
                );
                postRepository.deleteById(id);
                MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 삭제 성공", 200);
                return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
            } else {
                Post post = postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                        () -> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
                );
                postRepository.deleteById(id);
                MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 삭제 성공", 200);
                return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
            }
        }





    //게시글 상세조회
    @Transactional
    public PostResponseDto oneGetPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }




    // 포스트 좋아요 API
    @Transactional
    public ResponseEntity goodPost(Long postId, UserDetailsImpl userDetails) {
        // 요청받은 postId 값을 가진 포스트를 조회합니다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("포스트가 존재하지 않습니다."));

        // 현재 사용자가 해당 포스트에 대해 좋아요를 이미 눌렀는지 조회합니다.
        PostGood postGood = postGoodRepository.findByUsernameAndPost(userDetails.getUsername(), post);

        if (postGood == null) { // 사용자가 해당 포스트에 대해 좋아요를 처음 요청하는 경우
            // PostGood 테이블에 새로운 레코드를 생성하고, Post의 좋아요 수를 1 증가시킵니다.
            postGoodRepository.save(new PostGood(userDetails.getUsername(), post));
            post.setGood(post.getGood() + 1);
            postRepository.save(post);

            // 요청이 성공한 경우, 클라이언트에게 "좋아요 성공" 메시지와 상태코드 200을 반환합니다.
            MsgResponseDto msgResponseDto = new MsgResponseDto("좋아요 성공", 200);
            return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        } else { // 사용자가 이미 해당 포스트에 대해 좋아요를 누른 경우
            // PostGood 테이블에서 해당 레코드를 삭제하고, Post의 좋아요 수를 1 감소시킵니다.
            postGoodRepository.delete(postGood);
            post.setGood(post.getGood() - 1);
            postRepository.save(post);

            // 요청이 성공한 경우, 클라이언트에게 "좋아요 삭제 성공" 메시지와 상태코드 200을 반환합니다.
            MsgResponseDto msgResponseDto = new MsgResponseDto("좋아요 삭제 성공", 200);
            return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        }
    }


}
