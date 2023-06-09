package com.a101.fakediary.card.repository;

import com.a101.fakediary.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value = "select c from Card c where c.member.memberId = :memberId order by c.cardId desc")
    Optional<List<Card>> findAllByMemberId(@Param("memberId")Long memberId);

    List<Card> findAllByMember_MemberIdAndCreatedAtBetween(Long memberId, LocalDateTime yesterday, LocalDateTime now);
}
