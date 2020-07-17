package com.tomato.market.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomato.market.account.UserAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.location.Location;
import com.tomato.market.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NamedEntityGraph(name = "Product.withTagsAndLocations", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("locations")})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account writer;  //작성자

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();  //이 물품을 관심 추가한 회원들 (오류 발생.. 수정 필요)

    private int likeCount;

    private String title;

    private String category;

    private String price;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String images;  //등록한 이미지들의 html태그

    private String representativeImage;   //등록한 이미지 중 대표이미지(첫번째 이미지 html태그)

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Location> locations = new HashSet<>();

    private LocalDateTime writeTime;  //처음 작성한 시간

    private LocalDateTime updateTime;  //수정한 시간

    private LocalDateTime soldOutTime;  //판매완료 시간

    private boolean isSoldOut = false;

    private boolean isNotified = false;

    public boolean isWriter(UserAccount userAccount) {
        return this.writer.equals(userAccount.getAccount());  //userAccount.getUsername() 으로 바꿔서 디버그 해보기! username에 뭐가 들었나 궁금함
    }

    public void soldOut() {
        this.isSoldOut = true;
        this.soldOutTime = LocalDateTime.now();
    }

    public void makeWriterAndTime(Account account) {
        this.writer = account;
        this.writeTime = LocalDateTime.now();
    }

    public void makeRepresentativeImage() {
        if (this.images.contains("<img ") && this.images.contains("\">")) {
            this.representativeImage = this.images.substring(this.images.indexOf("<img "), this.images.indexOf("\">") + 2);
        } else {
            this.representativeImage = "<img src=/img/default_b72974fa-1b77-490a-9642-e6b2a443fff6.jpg>";
        }
    }

    public void addLikedAccount(Account account) {
            this.accounts.add(account);
            this.likeCount++;
    }

    public void removeLikedAccount(Account account) {
            this.accounts.remove(account);
            this.likeCount--;
    }
}
