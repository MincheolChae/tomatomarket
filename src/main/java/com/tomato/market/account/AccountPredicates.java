package com.tomato.market.account;

import com.querydsl.core.types.Predicate;
import com.tomato.market.account.domain.QAccount;
import com.tomato.market.location.Location;
import com.tomato.market.tag.Tag;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndLocations(Set<Tag> tags, Set<Location> locations) {
        QAccount account = QAccount.account;
        return account.locations.any().in(locations).and(account.tags.any().in(tags)); //account의 locations 중 아무거나 매칭이 되고, account의 tags 중 아무거나 매칭이 되는걸 리턴함
    }
}
