package pl.itomaszjanik.test;

import org.parceler.Parcel;
import pl.itomaszjanik.test.Comment;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Note {
    String content;
    String author;
    List<String> tags;
    List<Comment> comments;
    int noOfComments;
    int rating;

    public Note() {}

    public Note(String content, String author, List<String> tags, int rating) {
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.comments = new ArrayList<>();
        this.noOfComments = 0;
        this.rating = rating;
    }

    public Note(String content, String author, List<String> tags, List<Comment> comments, int rating) {
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.comments = comments;
        this.noOfComments = comments.size();
        this.rating = rating;
    }

    public String getContent() { return content; }

    public String getAuthor() { return author; }

    public List<String> getHashes() { return tags; }

    public List<Comment> getComments() {
        return comments;
    }

    public int getNoOfComments() {
        return noOfComments;
    }

    public int getRating(){
        return rating;
    }
}
