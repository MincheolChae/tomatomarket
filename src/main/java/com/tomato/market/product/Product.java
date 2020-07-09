package com.tomato.market.product;

import com.tomato.market.account.UserAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.location.Location;
import com.tomato.market.tag.Tag;
import lombok.*;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account writer;  //작성자

    private boolean isWriter;  //필요없을듯

    private String title;

    private String category;

    private String price;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String description;

    @Lob @Basic(fetch = FetchType.EAGER)
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


    public boolean isWriter(UserAccount userAccount) {
        return this.writer.equals(userAccount.getAccount());  //userAccount.getUsername() 으로 바꿔서 디버그 해보기! username에 뭐가 들었나 궁금함
    }

    public void soldOut() {
        this.isSoldOut = true;
        this.soldOutTime = LocalDateTime.now();
    }
}
