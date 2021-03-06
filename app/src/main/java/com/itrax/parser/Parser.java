package com.itrax.parser;

import android.content.Context;

import com.itrax.models.Model;

/**
 * Created by Shankar Rao on 3/28/2016.
 */
public interface Parser<T extends Model> {

    T parse(String s, Context context);
}