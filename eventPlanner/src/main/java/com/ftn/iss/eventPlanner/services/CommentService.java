package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.comment.CreateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.CreatedCommentDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Comment;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.CommentRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OfferingRepository offeringRepository;
    private ModelMapper modelMapper = new ModelMapper();
    @Transactional

    public CreatedCommentDTO create(CreateCommentDTO commentDTO, int offeringId){
        Offering offering = offeringRepository.findById(offeringId).get();
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        Optional<Account> accountOptional = accountRepository.findById(commentDTO.getAccount());
        accountOptional.ifPresent(account -> comment.setCommenter(account));
        comment.setStatus(Status.PENDING);
        offering.getComments().add(comment);
        commentRepository.save(comment);
        offeringRepository.save(offering);
        return modelMapper.map(comment, CreatedCommentDTO.class);
    }
}
