package com.ftn.iss.eventPlanner.dto.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class CreateMessageDTO {
    @Valid
    @NotNull
    private int sender;
    @NotNull
    @Valid
    private int receiver;
    @NotNull(message = "Content is required")
    private String content;
}
