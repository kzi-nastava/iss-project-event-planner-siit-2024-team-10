package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedCommentDTO {
    private int id;
    private String content;
    private Status status;
    private int accountId;
}
