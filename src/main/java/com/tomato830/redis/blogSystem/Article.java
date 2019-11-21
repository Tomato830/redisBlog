package com.tomato830.redis.blogSystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Article {
    //主键
    long id;
    //点击量
    long view;
    //标题
    String title;
    //作者
    String author;
    //标签
    ArrayList<String> tag;
    //正文
    String content;
    //评论
    ArrayList<String> comment;
    //发布日期
    Date postTime;

    //tostring()方法

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", view=" + view +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", tag=" + tag +
                ", content='" + content + '\'' +
                ", comment=" + comment +
                ", postTime=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(postTime) +
                '}';
    }

    //getter和setter方法
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getView() {
        return view;
    }

    public void setView(long view) {
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getComment() {
        return comment;
    }

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }
}
