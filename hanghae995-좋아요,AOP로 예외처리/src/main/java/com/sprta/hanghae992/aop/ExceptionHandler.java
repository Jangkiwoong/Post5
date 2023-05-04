package com.sprta.hanghae992.aop;

import com.sprta.hanghae992.dto.MsgResponseDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
@Aspect
public class ExceptionHandler {

    @Around("execution(* com.sprta.hanghae992.controller..*(..)) || execution(* com.sprta.hanghae992.dto..*(..))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 메서드 실행
            return joinPoint.proceed();
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 발생 시, 400 에러코드와 함께 에러메시지 반환
            return ResponseEntity.badRequest().body(new MsgResponseDto(e.getMessage(), 400));
        } catch (MethodArgumentNotValidException e) {
            // 그 외 예외 발생 시, 500 에러코드와 함께 에러메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MsgResponseDto("아이디 비번 다시 입력.", 400));
        }
//        catch (Exception e) {
//            // 그 외 예외 발생 시, 500 에러코드와 함께 에러메시지 반환
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MsgResponseDto("예기치 않은 오류가 발생했습니다.", 500));
//        }
    }



}


