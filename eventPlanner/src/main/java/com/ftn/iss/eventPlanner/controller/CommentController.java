package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.UpdateCommentDTO;
import com.ftn.iss.eventPlanner.dto.UpdatedCommentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCommentDTO> updateComment(@RequestBody UpdateCommentDTO comment, @PathVariable Long id)
            throws Exception {
        UpdatedCommentDTO updatedComment = new UpdatedCommentDTO();

        updatedComment.setId(id);
        updatedComment.setContent(comment.getContent());
        updatedComment.setStatus(comment.getStatus());

        return new ResponseEntity<UpdatedCommentDTO>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
