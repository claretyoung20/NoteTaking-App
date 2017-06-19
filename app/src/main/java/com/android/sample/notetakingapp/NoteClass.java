package com.android.sample.notetakingapp;

/**
 * Created by Young Claret on 6/14/2017.
 */

public class NoteClass {

    private String title;
    private String body;
    private String date;

    public NoteClass(String title, String body, String date) {
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
