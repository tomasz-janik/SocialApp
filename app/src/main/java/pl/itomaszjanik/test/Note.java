package pl.itomaszjanik.test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

@Parcel
public class Note {

    @SerializedName("ID")
    @Expose
    Integer id;
    @SerializedName("USERNAME")
    @Expose
    String username;
    @SerializedName("DATE")
    @Expose
    String date;
    @SerializedName("CONTENT")
    @Expose
    String content;
    @SerializedName("HASHESH")
    @Expose
    String hashesh;
    @SerializedName("LIKES")
    @Expose
    Integer likes;
    @SerializedName("COMMENTS")
    @Expose
    Integer comments;
    @SerializedName("COMMENT")
    @Expose
    Integer comment;
    @SerializedName("LIKED")
    @Expose
    Boolean liked;

    public Note(){}

    public Note(int id, String username, String date, String content, String hashesh, int likes, int comments, int comment, boolean liked){
        this.id = id;
        this.username = username;
        this.date = date;
        this.content = content;
        this.hashesh = hashesh;
        this.likes = likes;
        this.comments = comments;
        this.comment = comment;
        this.liked = liked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashesh() {
        return hashesh;
    }

    public void setHashesh(String hashesh) {
        this.hashesh = hashesh;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public void incrementComments(){
        this.comments++;
    }

    public void incrementLikes(){
        this.likes++;
    }

    public void decrementLikes(){
        this.likes--;
    }

    public void changeLiked(){
        if (liked){
            liked = false;
        }
        else{
            liked = true;
        }
    }
}