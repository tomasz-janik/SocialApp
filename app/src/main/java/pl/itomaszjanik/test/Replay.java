package pl.itomaszjanik.test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

@Parcel
public class Replay {

    @SerializedName("REPLAY_ID")
    @Expose
    Integer replayID;
    @SerializedName("COMMENT_ID")
    @Expose
    Integer commentID;
    @SerializedName("USER_ID")
    @Expose
    Integer userID;
    @SerializedName("USERNAME")
    @Expose
    String username;
    @SerializedName("DATE")
    @Expose
    String date;
    @SerializedName("CONTENT")
    @Expose
    String content;
    @SerializedName("LIKES")
    @Expose
    Integer likes;
    @SerializedName("REPLAYS")
    @Expose
    Integer replays;
    @SerializedName("LIKED")
    @Expose
    Boolean liked;

    public Replay(){}

    public Replay(int replayID, int commentID, int userID, String username, String date, String content, int likes, int replays, boolean liked){
        this.replayID = replayID;
        this.commentID = commentID;
        this.userID = userID;
        this.username = username;
        this.date = date;
        this.content = content;
        this.likes = likes;
        this.replays = replays;
        this.liked = liked;
    }

    public Integer getReplayID(){
        return replayID;
    }

    public void setReplayID(Integer replayID){
        this.replayID = replayID;
    }

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getReplays() {
        return replays;
    }

    public void setReplays(Integer replays) {
        this.replays = replays;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public void changeLiked(){
        if (liked){
            liked = false;
        }
        else{
            liked = true;
        }
    }

    public void incrementLikes(){
        likes++;
    }

    public void decrementLikes(){
        likes--;
    }

    public void incrementReplays(){
        replays++;
    }

    public void decrementReplays(){
        replays--;
    }

}