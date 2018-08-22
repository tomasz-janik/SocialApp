package pl.itomaszjanik.test;

import java.util.Date;

public class Note {

    private String content;
    private String author;
    private String hashes;
    private Date date;
    private int rating;

    public Note(String content, String author, String hashes, Date date){
        this.content = content;
        this.author = author;
        this.hashes = hashes;
        this.date = date;
        this.rating = 0;
    }

    public Note(String content, String author, String hashes){
        this.content = content;
        this.author = author;
        this.hashes = hashes;
        this.date = null;
        this.rating = 0;
    }

    public String getContent(){
        return content;
    }

    public String getAuthor(){
        return author;
    }

    public String getHashes(){ return hashes;}

    public int getRating(){
        return rating;
    }

}
