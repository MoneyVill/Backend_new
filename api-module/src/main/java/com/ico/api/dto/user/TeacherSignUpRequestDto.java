package com.ico.api.dto.user;

import com.ico.core.entity.Teacher;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 교사 회원가입 시 입력받을 필드
 */
@Getter
@NoArgsConstructor
public class TeacherSignUpRequestDto {

    @NotBlank(message = "100")
    private String identity;

    @NotBlank(message = "104")
    private String password;

    @NotBlank(message = "104")
    private String checkedPassword;

    @NotBlank(message = "112")
    private String name;

    // `role`과 `phoneNum` 필드를 제거했습니다.

    // 모든 필드를 초기화하는 생성자 추가
    public TeacherSignUpRequestDto(String identity, String password, String checkedPassword, String name) {
        this.identity = identity;
        this.password = password;
        this.checkedPassword = checkedPassword;
        this.name = name;
    }

    public TeacherSignUpRequestDto(Teacher teacher) {
        this.identity = teacher.getIdentity();
        this.password = teacher.getPassword();
        this.name = teacher.getName();
    }
}
