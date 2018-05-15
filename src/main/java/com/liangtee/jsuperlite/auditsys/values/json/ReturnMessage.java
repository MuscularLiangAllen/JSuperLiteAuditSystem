package com.liangtee.jsuperlite.auditsys.values.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/6/22.
 * All rights Reserved
 */
public class ReturnMessage {

    @JSONField(name="message")
    private String message;

    @JSONField(name="message2")
    private String message2;

    public ReturnMessage(String ...messages) {
        this.message = messages[0];
        if (messages.length > 1) this.message2 = messages[1];
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
