package com.ftn.iss.eventPlanner.dto.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDTO {
    @NotNull(message = "Content is required")
    private String content;
    @Valid
    @NotNull
    private int account;
    @NotNull(message = "Rating is required")

    private int rating;
    public CreateCommentDTO(){

    }
}
