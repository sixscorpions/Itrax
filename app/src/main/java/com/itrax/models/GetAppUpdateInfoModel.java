package com.itrax.models;

/**
 * Created by Admin on 14/06/2018.
 */

public class GetAppUpdateInfoModel extends Model {
    boolean isUpdate;
    boolean isForceToUpdate;

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isForceToUpdate() {
        return isForceToUpdate;
    }

    public void setForceToUpdate(boolean forceToUpdate) {
        isForceToUpdate = forceToUpdate;
    }
}
