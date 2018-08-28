package pl.itomaszjanik.test;

import org.parceler.Parcel;

@Parcel
public class CommentWithReplays extends Comment{

    public int noOfReplays;

    public CommentWithReplays(){ }

    public CommentWithReplays(String content, String username, String date, int noOfReplays){
        super(content, username, date);
        this.noOfReplays = noOfReplays;
    }

    public int getNoOfReplays() {
        return noOfReplays;
    }
}
