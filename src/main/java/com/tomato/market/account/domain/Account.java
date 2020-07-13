package com.tomato.market.account.domain;

import com.tomato.market.product.Product;
import com.tomato.market.tag.Tag;
import com.tomato.market.location.Location;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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

    @Column(unique = true)
    private String nickname;

    private String phone;  //휴대폰 번호(폰번호는 바뀔수 있으므로 unique한 값으로 하지않음)

    private boolean emailVerified = false;  //이메일 인증 여부

    private String emailCheckToken;  //이메일 인증 토큰

    private LocalDateTime emailCheckTokenGeneratedAt;  //이메일 인증 토큰 발급시점

    private LocalDateTime joinedAt;  //가입날(인증된 날)

    @OneToMany(mappedBy = "writer", fetch = FetchType.EAGER)
    @OrderBy("writeTime")
    private List<Product> products = new ArrayList<>();   //판매내역

    @ManyToMany
    private List<Product> productsLiked = new ArrayList<>();  //관심 표시한 물품들

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();   //관심 태그

    @ManyToMany
    private Set<Location> locations = new HashSet<>();  //활동 지역

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean newProductNotiByEmail;  //관심 키워드에 해당하는 제품 업로드 알림을 메일로 받을지

    private boolean newProductNotiByWeb = true;  //관심 키워드에 해당하는 제품 업로드 알림을 웹으로 받을지

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();  //랜덤 UUID 로 토큰 생성
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();  //생성된 시간 저장
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public void addProduct(Product newProduct) {
        this.products.add(newProduct);
    }

    public void deleteProduct(Product product) {
        this.products.remove(product);
    }
}

