package org.example;

import Utils.SmartHttpUtil;

import java.util.HashMap;

public class Electricity {
    public static String getElectricity(String jsondata) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("jsondata", jsondata);
        params.put("funname", "synjones.onecard.query.elec.roominfo");
        params.put("json", "true");
        return requestHeaders(params);
    }
    public static String requestHeaders(HashMap<String, String> params) throws Exception {
        HashMap<String, String> headers = CustomHeaders.createHeaders();
        return SmartHttpUtil.sendPostForm("http://120.195.201.200:8088/web/Common/Tsm.html", params, headers);
    }
}
