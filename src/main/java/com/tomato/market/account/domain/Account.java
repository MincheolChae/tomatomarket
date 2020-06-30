package com.tomato.market.account.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private String phone;  //휴대폰 번호

    private boolean emailVerified;  //이메일 인증 여부

    private String emailCheckToken;  //이메일 인증 토큰

    private LocalDateTime emailCheckTokenGeneratedAt;  //이메일 인증 토큰 발급시점

    private LocalDateTime joinedAt;  //가입날(인증된 날)

    //TODO 판매내역

    private String location;  //살고 있는 지역 (위치 API or 수동 입력)

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean newProductNotiByEmail;  //관심 키워드에 해당하는 제품 업로드 알림을 메일로 받을지

    private boolean newProductNotiByWeb = true;  //관심 키워드에 해당하는 제품 업로드 알림을 웹으로 받을지


    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();  //랜덤 UUID 로 토큰 생성
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();  //생성된 시간 저장
    }
}

