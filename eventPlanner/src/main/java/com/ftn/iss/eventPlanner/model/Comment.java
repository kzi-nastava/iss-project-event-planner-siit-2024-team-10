package com.ftn.iss.eventPlanner.model;

public class Comment {
    private int id;
    private String content;
    private Status status;
    private Account commenter;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Account getCommenter() {
        return commenter;
    }

    public void setCommenter(Account commenter) {
        this.commenter = commenter;
    }
}
