package com.ftn.iss.eventPlanner.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class CreatedMessageDTO {
    private int id;
    private int sender;
    private int receiver;
    private String content;
    private LocalDateTime timestamp;
}
