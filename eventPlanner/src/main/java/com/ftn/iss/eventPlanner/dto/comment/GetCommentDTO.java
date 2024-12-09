package com.ftn.iss.eventPlanner.dto.comment;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCommentDTO {
    private int id;
    private String content;
    private Status status;
    private int accountId;
    public GetCommentDTO(){

    }
}
