package pl.itomaszjanik.test;

import org.parceler.Parcel;

@Parcel
public class Comment {

    public String content;
    public String username;
    public String date;

    public Comment(){ }

    public Comment(String content, String username, String date){
        this.content = content;
        this.username = username;
        this.date = date;
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
}
