package pl.itomaszjanik.test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment02 {

    @SerializedName("POST_ID")
    @Expose
    private String pOSTID;
    @SerializedName("USER_ID")
    @Expose
    private String uSERID;
    @SerializedName("USERNAME")
    @Expose
    private Object uSERNAME;
    @SerializedName("DATE")
    @Expose
    private Object dATE;
    @SerializedName("CONTENT")
    @Expose
    private String cONTENT;
    @SerializedName("LIKES")
    @Expose
    private Integer lIKES;
    @SerializedName("REPLAYS")
    @Expose
    private Integer rEPLAYS;

    public String getPOSTID() {
        return pOSTID;
    }

    public void setPOSTID(String pOSTID) {
        this.pOSTID = pOSTID;
    }

    public String getUSERID() {
        return uSERID;
    }

    public void setUSERID(String uSERID) {
        this.uSERID = uSERID;
    }

    public Object getUSERNAME() {
        return uSERNAME;
    }

    public void setUSERNAME(Object uSERNAME) {
        this.uSERNAME = uSERNAME;
    }

    public Object getDATE() {
        return dATE;
    }

    public void setDATE(Object dATE) {
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