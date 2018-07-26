package com.skaz.web;

import com.skaz.bean.Result;
import com.skaz.utils.Results;

/**
 * @author jungle
 */
public class BaseController {

    public Result failure(String msg) {
        return Results.failure(msg);
    }

}
