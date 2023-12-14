package org.example;

import java.util.HashMap;

public class CustomHeaders {
    public static HashMap<String, String> createHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Connection", "close");
        headers.put("Pragma", "no-cache");
        headers.put("Cache-Control", "no-cache");
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; 2203121C Build/PQ3A.190605.10261546; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh,zh-CN;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.put("Cookie", "xxx");
        return headers;
    }
}
