package com.tomato.market.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository keywordRepository;

    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = keywordRepository.findByTitle(tagTitle);
        if (tag == null) {
            tag = keywordRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }

}
