package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentDTO {
    private String content;
    private Status status;

    public UpdateCommentDTO() {super();}
}
