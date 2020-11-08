package com.example.getsumfoot.data;

public class ReviewData {

    public String photo; // 게시글 사진
    public String title; //푸드트럭 이름
    public String content; // 게시글 내용

    public ReviewData(){}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String contents) {
        this.content = content;
    }


}
