package org.example;

import Utils.JdbcUtils;
import Utils.SmartHttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class Bill {
    //获取总页数
    public static int getTotalValue() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", "13626");
        params.put("json", "true");
        params.put("page", "1");
        String response = requestSend(params);
        // 使用Fastjson解析JSON字符串
        JSONObject jsonResponse = JSON.parseObject(response);
        // 返回total字段的值
        return jsonResponse.getIntValue("total");
    }
    //获取每一页的数据
    public static String requestParams(int page) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", "13626");
        params.put("json", "true");
        params.put("page", String.valueOf(page));
        return requestSend(params);
    }
    //统一的请求体
    public static String requestSend(HashMap<String, String> params) throws Exception {
        HashMap<String, String> headers = CustomHeaders.createHeaders();
        return SmartHttpUtil.sendPostForm("http://ecard.cwxu.edu.cn/Report/GetMyBill", params, headers);
    }
    //这里进行json解析，将数据插入到数据库中,insetCode为0时，表示插入成功，否则插入失败，并且引入exitCode，用来处理上层循环
    public static void doInsertMyBillData(String response) {
        try (Connection conn = JdbcUtils.getConnect()) {
            JSONObject jsonResponse = JSON.parseObject(response);
            JSONArray rows = jsonResponse.getJSONArray("rows");
            for (int i = 0; i < rows.size(); i++) {
                JSONObject row = rows.getJSONObject(i);
                String occtime = row.getString("OCCTIME");
                String xq = row.getString("XQ");
                String mercname = row.getString("MERCNAME");
                String tranamt = row.getString("TRANAMT");
                String tranname = row.getString("TRANNAME");
                // 使用预处理语句插入数据
                int insertCode = CrudApi.preInsertBillData(conn, occtime, xq, mercname, Double.valueOf(tranamt), tranname);
                if (insertCode == 0) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
