package com.itrax.parser;

import android.content.Context;

import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;

import org.json.JSONObject;

/**
 * Created by shankar on 4/30/2017.
 */

public class PostNoteParser implements Parser<Model> {
    @Override
    public Model parse(String s, Context context) {
        PostNoteModel mPostNoteModel = new PostNoteModel();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.has("ok"))
                mPostNoteModel.setOk(jsonObject.optInt("ok"));
            if (jsonObject.has("nModified"))
                mPostNoteModel.setNModified(jsonObject.optInt("nModified"));
            if (jsonObject.has("n"))
                mPostNoteModel.setN(jsonObject.optInt("n"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mPostNoteModel;
    }
}
