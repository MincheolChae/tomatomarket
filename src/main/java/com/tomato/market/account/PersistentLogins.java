package com.tomato.market.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "persistent_logins")
@Entity
@Getter
@Setter @AllArgsConstructor @NoArgsConstructor
public class PersistentLogins {

    @Id
    @Column(length = 128)
    private String series;

    @Column(nullable = false, length = 128)
    private String username;

    @Column(nullable = false, length = 128)
    private String token;

    @Column(name = "last_used", nullable = false, length = 128)
    private LocalDateTime lastUsed;

}
