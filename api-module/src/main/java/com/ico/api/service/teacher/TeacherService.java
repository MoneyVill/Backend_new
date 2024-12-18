package com.ico.api.service.teacher;

import com.ico.api.dto.user.TeacherSignUpRequestDto;
import com.ico.core.entity.Teacher;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Teacher Service
 *
 * @author 강교철
 */
public interface TeacherService {

    /**
     * TeacherSignUpRequestDto 를 받아 교사 회원가입
     *
     * @param requestDto
     * @return id
     */
    Long signUp(TeacherSignUpRequestDto requestDto) throws IOException;


    /**
     * 회원가입 후 교사인증서 반려 당했을 때 or 다시 교사인증서를 보낼 때
     * @param request
     * @param file
     */
    void certifiedImage(HttpServletRequest request, MultipartFile file);
}
