package com.ftn.iss.eventPlanner.dto.comment;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentDTO {
    private String content;
    private int rating;

    public UpdateCommentDTO() {super();}
}
