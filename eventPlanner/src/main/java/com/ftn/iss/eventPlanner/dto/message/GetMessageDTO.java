package com.ftn.iss.eventPlanner.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetMessageDTO {
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime timestamp;
}
