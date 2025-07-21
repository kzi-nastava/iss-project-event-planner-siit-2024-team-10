package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.comment.CreateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.CreatedCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.GetCommentDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<GetCommentDTO> getPendingComments() {
        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .filter(comment -> comment.getStatus() == Status.PENDING)
                .map(this::mapToGetCommentDTO)
                .collect(Collectors.toList());
    }

    public void approve(int commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Offering offering = offeringRepository.findByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Offering not found"));

        comment.setStatus(Status.ACCEPTED);
        commentRepository.save(comment);
    }

    public void delete(int commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Offering offering = offeringRepository.findByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Offering not found"));

        comment.setStatus(Status.DENIED);
        offering.getComments().remove(comment);
        offeringRepository.save(offering);
        commentRepository.save(comment);
    }

    private GetCommentDTO mapToGetCommentDTO(Comment comment) {
        return new GetCommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getStatus(),
                comment.getCommenter().getId(),
                comment.getRating(),
                comment.getCommenter().getUsername()
        );
    }
}
