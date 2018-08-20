package pl.itomaszjanik.test;

import java.util.Date;

public class Note {

    private String content;
    private String author;
    private Date date;
    private int rating;

    public Note(String content, String author, Date date){
        this.content = content;
        this.author = author;
        this.date = date;
        this.rating = 0;
    }

    public Note(String content, String author){
        this.content = content;
        this.author = author;
        this.date = null;
        this.rating = 0;
    }

    public String getContent(){
        return content;
    }

    public String getAuthor(){
        return author;
    }

    public int getRating(){
        return rating;
    }

}
