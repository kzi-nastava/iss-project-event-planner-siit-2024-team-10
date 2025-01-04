package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.comment.CreateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.CreatedCommentDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Comment;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public CreatedCommentDTO create(CreateCommentDTO commentDTO){
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        Optional<Account> accountOptional = accountRepository.findById(commentDTO.getAccountId());
        accountOptional.ifPresent(account -> comment.setCommenter(account));
        comment.setStatus(Status.PENDING);
        commentRepository.save(comment);
        return modelMapper.map(comment, CreatedCommentDTO.class);
    }
}
