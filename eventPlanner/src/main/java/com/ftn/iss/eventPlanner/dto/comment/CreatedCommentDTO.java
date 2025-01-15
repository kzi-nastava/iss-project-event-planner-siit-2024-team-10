package com.ftn.iss.eventPlanner.dto.comment;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatedCommentDTO {
    private int id;
    private String content;
    private Status status;
    private int accountId;
    private int rating;
}
