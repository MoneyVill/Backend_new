package com.ico.api.dto.rule;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 학급규칙 추가 dto
 *
 * @author 서재건
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RuleReqDto {


    @NotBlank(message = "402")
    private String title;

    @NotBlank(message = "403")
    private String detail;

    private Boolean answer;

    @Builder
    public RuleReqDto(String title, String detail, Boolean answer) {
        this.title = title;
        this.detail = detail;
        this.answer = answer;
    }
}
