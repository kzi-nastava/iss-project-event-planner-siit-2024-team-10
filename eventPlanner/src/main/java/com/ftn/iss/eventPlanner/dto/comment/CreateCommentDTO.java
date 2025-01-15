package com.ftn.iss.eventPlanner.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDTO {
    private String content;
    private int account;
    private int rating;
    public CreateCommentDTO(){

    }
}
