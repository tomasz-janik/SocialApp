package pl.itomaszjanik.test;

import org.parceler.Parcel;

@Parcel
public class Comment {

    public String content;
    public String username;
    public String date;
    public int noOfReplays;
    public int likes;

    public Comment(){ }

    public Comment(String content, String username, String date){
        this.content = content;
        this.username = username;
        this.date = date;
        this.noOfReplays = 0;
        this.likes = 0;
    }

    public Comment(String content, String username, String date, int noOfReplays, int likes){
        this.content = content;
        this.username = username;
        this.date = date;
        this.noOfReplays = noOfReplays;
        this.likes = likes;
    }


    public String getContent(){
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public int getNoOfReplays() {
        return noOfReplays;
    }

    public int getLikes() {
        return likes;
    }
}
