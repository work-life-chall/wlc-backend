package com.bonfire.challenge.service;

import com.bonfire.challenge.Specification.ChallengeSpecification;
import com.bonfire.challenge.entity.Challenge;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ChallengeService {
    // 정렬
    public Sort sortList(String condition){
        Sort sortResult = switch (condition) {
            case "rewardDesc" -> Sort.by("reward").descending(); // 리워드 높은 순

            case "rewardAsc" -> Sort.by("reward").ascending(); //리워드 낮은 순

            case "entryDesc" -> Sort.by("entry").descending(); //참여자 많은 순

            default -> Sort.by(condition).descending(); //최신순
        };
         return sortResult;
    }

    // 검색조건
    public Specification<Challenge> searchSpec(Integer category, String frequency,String term, String searchWord){
        Specification<Challenge> spec = (root, query, criteriaBuilder) -> null;

        if (category != null) { // 카테고리
            spec = spec.and(ChallengeSpecification.equalCategory(category));
        }
        if (frequency != null) { // 빈도
            spec = spec.and(ChallengeSpecification.equalFrequency(frequency));
        }
        if (term != null) { // 기간
            spec = spec.and(ChallengeSpecification.equalTerm(term));
        }
        if (searchWord != null) { // 제목 키워드 검색
            spec = spec.and(ChallengeSpecification.likeTitle(searchWord));
        }
        return spec;
    }
}
