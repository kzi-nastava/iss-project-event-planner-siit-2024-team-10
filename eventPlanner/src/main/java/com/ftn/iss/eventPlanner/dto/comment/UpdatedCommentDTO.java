package com.ftn.iss.eventPlanner.dto.comment;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedCommentDTO {
    private int id;
    private String content;
    private int rating;

    public UpdatedCommentDTO() {}
}
