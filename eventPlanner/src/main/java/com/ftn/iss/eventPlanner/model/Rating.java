package com.ftn.iss.eventPlanner.model;

public class Rating {
    private int id;
    private int score;
    private Account rater;

    public Rating() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Account getRater() {
        return rater;
    }

    public void setRater(Account rater) {
        this.rater = rater;
    }
}
