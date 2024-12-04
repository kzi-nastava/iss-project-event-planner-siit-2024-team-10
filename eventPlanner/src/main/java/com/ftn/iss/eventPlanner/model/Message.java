package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    private Account sender;
    @ManyToOne
    private Account receiver;
    @Column(nullable = false)
    private boolean isRead;

    public Message() {
    }
}
