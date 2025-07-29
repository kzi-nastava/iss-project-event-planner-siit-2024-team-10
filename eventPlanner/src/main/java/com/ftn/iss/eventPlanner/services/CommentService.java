package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.comment.CreateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.CreatedCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.GetCommentDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.CommentRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OfferingRepository offeringRepository;
    @Autowired
    private NotificationService notificationService;
    private ModelMapper modelMapper = new ModelMapper();
    @Transactional

    public CreatedCommentDTO create(CreateCommentDTO commentDTO, int offeringId) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new NotFoundException("Offering with ID " + offeringId + " not found"));

        Account account = accountRepository.findById(commentDTO.getAccount())
                .orElseThrow(() -> new NotFoundException("Account with ID " + commentDTO.getAccount() + " not found"));

        Comment comment = new Comment();
        comment.setCommenter(account);
        comment.setStatus(Status.PENDING);
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());

        offering.getComments().add(comment);

        commentRepository.save(comment);
        offeringRepository.save(offering);

        CreatedCommentDTO dto = modelMapper.map(comment, CreatedCommentDTO.class);
        dto.setAccount(account.getId());
        return dto;
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

        String offeringName = "";
        if (offering instanceof Product p){
            offeringName = p.getCurrentDetails().getName();
        } else if (offering instanceof com.ftn.iss.eventPlanner.model.Service s){
            offeringName = s.getCurrentDetails().getName();
        }

        notificationService.sendNotification(offering.getProvider().getAccount().getId(), "New Comment", "Your offering "+ offeringName +" has received a new comment with rating "+comment.getRating()+"/5.");
    }

    public void reject(int commentId){
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
