package com.ico.api.dto.rule;

import com.ico.core.entity.Rule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학급규칙 조회 res dto
 *
 * @author 서재건
 */
@Getter
@NoArgsConstructor
public class RuleResDto {

    private Long id;

    private String title;

    private String detail;

    private String dateTime;

    private Boolean answer;

    @Builder
    public RuleResDto(Long id, String title, String detail, Boolean answer, String dateTime) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.answer = answer;
        this.dateTime = dateTime;
    }

    /**
     *
     * @param rule
     * @return
     */
    public RuleResDto of(Rule rule, String dateTime) {
        return RuleResDto.builder()
                .id(rule.getId())
                .title(rule.getTitle())
                .detail(rule.getDetail())
                .answer(rule.getAnswer())
                .dateTime(dateTime)
                .build();
    }
}
