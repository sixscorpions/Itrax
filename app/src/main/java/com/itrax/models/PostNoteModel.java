package com.itrax.models;

/**
 * Created by shankar on 4/30/2017.
 */

public class PostNoteModel extends Model {

    private int ok;
    private int nModified;
    private int n;

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getNModified() {
        return nModified;
    }

    public void setNModified(int nModified) {
        this.nModified = nModified;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
