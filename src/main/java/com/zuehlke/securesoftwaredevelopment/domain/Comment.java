package com.zuehlke.securesoftwaredevelopment.domain;

public class Comment {
    private int bookId;
    private Integer userId;
    private String comment;

    public Comment() {
    }

    public Comment(int bookId, Integer userId, String comment) {
        this.bookId = bookId;
        this.userId = userId;
        this.comment = comment;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
