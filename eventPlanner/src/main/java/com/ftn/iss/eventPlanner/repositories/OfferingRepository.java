package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferingRepository extends JpaRepository<Offering,Integer>, JpaSpecificationExecutor<Offering> {
    List<Offering> findByProvider_Id(int providerId);

    @Query("SELECT o FROM Offering o JOIN o.comments c WHERE c.id = :commentId")
    Optional<Offering> findByCommentId(@Param("commentId") int commentId);
}
