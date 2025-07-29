package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
}
