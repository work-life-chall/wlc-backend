package com.bonfire.challenge.repository;

import com.bonfire.challenge.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge,Long>, JpaSpecificationExecutor<Challenge> {
    Page<Challenge> findByCategory(int category, Pageable pageable);

}
