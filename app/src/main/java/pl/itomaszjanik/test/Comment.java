package pl.itomaszjanik.test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

@Parcel
public class Comment {

    @SerializedName("POST_ID")
    @Expose
    Integer pOSTID;
    @SerializedName("USER_ID")
    @Expose
    Integer uSERID;
    @SerializedName("USERNAME")
    @Expose
    String uSERNAME;
    @SerializedName("DATE")
    @Expose
    String dATE;
    @SerializedName("CONTENT")
    @Expose
    String cONTENT;
    @SerializedName("LIKES")
    @Expose
    Integer lIKES;
    @SerializedName("REPLAYS")
    @Expose
    Integer rEPLAYS;

    public Comment(){}

    public Comment(int postID, int userID, String username, String date, String content, int likes, int replays){
        this.pOSTID = postID;
        this.uSERID = userID;
        this.uSERNAME = username;
        this.dATE = date;
        this.cONTENT = content;
        this.lIKES = likes;
        this.rEPLAYS = replays;
    }

    public Integer getPOSTID() {
        return pOSTID;
    }

    public void setPOSTID(Integer pOSTID) {
        this.pOSTID = pOSTID;
    }

    public Integer getUSERID() {
        return uSERID;
    }

    public void setUSERID(Integer uSERID) {
        this.uSERID = uSERID;
    }

    public String getUSERNAME() {
        return uSERNAME;
    }

    public void setUSERNAME(String uSERNAME) {
        this.uSERNAME = uSERNAME;
    }

    public String getDATE() {
        return dATE;
    }

    public void setDATE(String dATE) {
        this.dATE = dATE;
    }

    public String getCONTENT() {
        return cONTENT;
    }

    public void setCONTENT(String cONTENT) {
        this.cONTENT = cONTENT;
    }

    public Integer getLIKES() {
        return lIKES;
    }

    public void setLIKES(Integer lIKES) {
        this.lIKES = lIKES;
    }

    public Integer getREPLAYS() {
        return rEPLAYS;
    }

    public void setREPLAYS(Integer rEPLAYS) {
        this.rEPLAYS = rEPLAYS;
    }

}