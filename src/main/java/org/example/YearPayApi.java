package org.example;

import Utils.SmartHttpUtil;

import java.util.HashMap;


public class YearPayApi {
    public static HashMap<String, String> requestParams() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", "13626");
        params.put("json", "true");
        params.put("year", "2023");
        return params;
    }
    public static String requestSend(HashMap<String, String> params) throws Exception {
        HashMap<String, String> headers = CustomHeaders.createHeaders();
        return SmartHttpUtil.sendPostForm("http://ecard.cwxu.edu.cn/report/EveryMonthPay", params, headers);
    }
}
