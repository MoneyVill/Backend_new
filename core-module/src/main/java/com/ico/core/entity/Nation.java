package com.ico.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ico.core.dto.StockReqDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

/**
 * 나라 Entity
 *
 * @author 강교철
 * @author 변윤경
 * @author 서재건
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String school;

    private byte grade;

    private byte room;

    private String title;

    @Column(unique = true)
    private String code;

    /**
     * 화폐 이름
     */
    private String currency;

    /**
     * 국고
     */
    @ColumnDefault("0")
    private int treasury;

    @OneToMany(mappedBy = "nation", cascade = CascadeType.ALL)
    private List<StockItem> stockItems;

    @ColumnDefault("50")
    private byte credit_up;

    @ColumnDefault("20")
    private byte credit_down;


    /**
     * 신용점수 등락폭 수정
     *
     * @param creditUp
     * @param creditDown
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void updateCredit(int creditUp, int creditDown) {
        this.credit_up = (byte) creditUp;
        this.credit_down = (byte) creditDown;
    }
}
