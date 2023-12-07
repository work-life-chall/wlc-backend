package com.bonfire.challenge.Specification;

import com.bonfire.challenge.entity.Challenge;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ChallengeSpecification {

    public static Specification<Challenge> equalCategory(Integer category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Challenge> equalFrequency(String frequency) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("frequency"), frequency);
    }

    public static Specification<Challenge> equalTerm(String term) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("term"), term);
    }

    public static Specification<Challenge> likeTitle(String searchWord) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%"+searchWord+"%");
    }


}
