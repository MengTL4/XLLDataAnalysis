import Utils.JdbcUtils;
import com.alibaba.fastjson2.JSONException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class YourJavalinApp {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/api/data", ctx -> {
            try (Connection connection = JdbcUtils.getConnect()) {
                String tableName = "bill"; // 替换成您的表名
                JSONArray jsonData = fetchDataFromTable(connection, tableName);
                ctx.json(jsonData);
            } catch (SQLException e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });
    }

    private static JSONArray fetchDataFromTable(Connection connection, String tableName) throws SQLException, JSONException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            JSONArray jsonArray = new JSONArray();

            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                row.put("RO", resultSet.getInt("RO"));
                row.put("OCCTIME", resultSet.getString("OCCTIME"));
                row.put("XQ", resultSet.getString("XQ"));
                row.put("MERCNAME", resultSet.getString("MERCNAME"));
                row.put("TRANAMT", resultSet.getDouble("TRANAMT"));
                row.put("TRANNAME", resultSet.getString("TRANNAME"));
                jsonArray.add(row);
            }

            return jsonArray;
        }
    }
}
