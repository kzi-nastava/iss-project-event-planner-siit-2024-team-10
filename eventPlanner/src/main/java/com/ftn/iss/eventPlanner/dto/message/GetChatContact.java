package com.ftn.iss.eventPlanner.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetChatContact {
    private int user;
    private String name;
    private String content;
    private LocalDateTime dateTime;
}
