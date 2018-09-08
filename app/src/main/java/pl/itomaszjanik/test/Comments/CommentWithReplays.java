package pl.itomaszjanik.test.Comments;

import org.parceler.Parcel;
import pl.itomaszjanik.test.Comment;

@Parcel
public class CommentWithReplays extends Comment {

    public int noOfReplays;

    public CommentWithReplays(){ }

    public CommentWithReplays(String content, String username, String date, int noOfReplays){
        //super(content, username, date);
        this.noOfReplays = noOfReplays;
    }

    public int getNoOfReplays() {
        return noOfReplays;
    }
}
