package com.nmt.education.commmons.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class RequestBodyUtil {

    public static JSONObject readFromRequestBody(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (Objects.nonNull(line)) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return JSONObject.parseObject(sb.toString());
    }
}
