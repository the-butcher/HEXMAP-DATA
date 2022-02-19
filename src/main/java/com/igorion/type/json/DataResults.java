package com.igorion.type.json;

import com.igorion.type.json.impl.JsonTypeImplDataResult;

public class DataResults {

    private DataResults() {
        //no public instance
    }

    public static JsonTypeImplDataResult create(String src, String dst, boolean success) {
        JsonTypeImplDataResult result = new JsonTypeImplDataResult();
        result.setSrc(src);
        result.setDst(dst);
        result.setSuccess(success);
        return result;
    }

    public static JsonTypeImplDataResult success(String src, String dst) {
        return create(src, dst, true);
    }

    public static JsonTypeImplDataResult failure(String src, String dst) {
        return create(src, dst, false);
    }

}
