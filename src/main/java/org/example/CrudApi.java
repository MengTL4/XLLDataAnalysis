package org.example;

import Utils.JdbcUtils;
import com.alibaba.fastjson2.JSONObject;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CrudApi {
    public static int preInsertBillData(Connection conn, String occtime, String xq, String mercname, Double tranamt, String tranname) {
        try {
            // 检查数据库中是否已存在具有相同OCCTIME值的记录(记录产生的时间)
            String checkQuery = "SELECT OCCTIME FROM bill WHERE OCCTIME = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setString(1, occtime);
            ResultSet resultSet = checkStatement.executeQuery();
            if (!resultSet.next()) {
                // 如果没有找到相同OCCTIME值的记录，执行插入操作
                String insertQuery = "INSERT INTO bill (OCCTIME, XQ, MERCNAME, TRANAMT, TRANNAME) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                preparedStatement.setString(1, occtime);
                preparedStatement.setString(2, xq);
                preparedStatement.setString(3, mercname);
                preparedStatement.setDouble(4, tranamt);
                preparedStatement.setString(5, tranname);
                int insertCode = preparedStatement.executeUpdate();
                if (insertCode > 0) {
                    System.out.println("新数据插入成功");
                }
                return insertCode;
            } else {
                System.out.println("数据已经存在，跳过插入");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //根据TRANNAME字段为"持卡人消费"的记录，分类统计MERCNAME字段，算出哪一类消费的次数最多，然后按从高到低的顺序输出
    public static String countMerchantFrequency(Connection conn) {
        try {
            String query = "SELECT MERCNAME, COUNT(*) as frequency FROM bill WHERE TRANNAME IN ('持卡人消费', '电子账户消费','代扣代缴') GROUP BY MERCNAME ORDER BY frequency DESC";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> resmonthList = new ArrayList<>();
            List<String> amtList = new ArrayList<>();
            int maxamt = 0;

            while (resultSet.next()) {
                // Remove spaces
                String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
                int frequency = resultSet.getInt("frequency");

                resmonthList.add(mercname);
                amtList.add(String.valueOf(frequency));

                // Update maxamt if necessary
                if (frequency > maxamt) {
                    maxamt = frequency;
                }
            }

            // Create the result JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MERCNAME", resmonthList);
            jsonObject.put("frequency", amtList);
            jsonObject.put("maxfrequency", maxamt);

            return jsonObject.toJSONString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{\"error\": \"Error executing the query\", \"count\": 0}";
    }

    //本学期消费次数
    public static String count_term_frequency(Connection conn) {
        try {
            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();


            String query = "SELECT MERCNAME, COUNT(*) as frequency FROM bill WHERE TRANNAME IN ('持卡人消费', '电子账户消费','代扣代缴') " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY MERCNAME ORDER BY frequency DESC";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> resmonthList = new ArrayList<>();
            List<String> amtList = new ArrayList<>();
            int maxamt = 0;

            while (resultSet.next()) {
                // Remove spaces
                String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
                int frequency = resultSet.getInt("frequency");

                resmonthList.add(mercname);
                amtList.add(String.valueOf(frequency));

                // Update maxamt if necessary
                if (frequency > maxamt) {
                    maxamt = frequency;
                }
            }

            // Create the result JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resmonth", resmonthList);
            jsonObject.put("amt", amtList);
            jsonObject.put("maxamt", maxamt);

            return jsonObject.toJSONString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{\"error\": \"Error executing the query\", \"count\": 0}";
    }

    //本学期消费金额排行
    public static String count_term_pay(Connection conn) {
        try {

            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();

            String query = "SELECT MERCNAME, ABS(SUM(TRANAMT)) AS total_amount FROM bill " +
                    "WHERE TRANNAME IN ('持卡人消费', '电子账户消费','代扣代缴') " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY MERCNAME ORDER BY total_amount DESC";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> resmonthList = new ArrayList<>();
            List<String> amtList = new ArrayList<>();
            double maxamt = 0;

            DecimalFormat df = new DecimalFormat("#.##");

            while (resultSet.next()) {
                // Remove spaces
                String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
                double totalAmount = resultSet.getDouble("total_amount");

                resmonthList.add(mercname);
                amtList.add(df.format(totalAmount));

                // Update maxamt if necessary
                if (totalAmount > maxamt) {
                    maxamt = totalAmount;
                }
            }

            // Create the result JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resmonth", resmonthList);
            jsonObject.put("amt", amtList);
            jsonObject.put("maxamt", df.format(maxamt));


            return jsonObject.toJSONString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{\"error\": \"Error executing the query\", \"count\": 0}";
    }

    public static String billTable() {
        // 从上下文中获取数据库连接
        Connection connection = JdbcUtils.getConnect();

        // 执行查询，按照OCCTIME降序排列
        String query = "SELECT OCCTIME, XQ, MERCNAME, TRANAMT, TRANNAME FROM bill ORDER BY OCCTIME DESC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            // Build HTML table with styling
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<html><head><style>");
            htmlTable.append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
            htmlTable.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            htmlTable.append("th { background-color: #f2f2f2; }");
            htmlTable.append("tr:hover { background-color: #f5f5f5; }");
            htmlTable.append("</style></head><body>");
            htmlTable.append("<table><tr><th>OCCTIME</th><th>XQ</th><th>MERCNAME</th><th>TRANAMT</th><th>TRANNAME</th></tr>");

            // Process query results
            while (resultSet.next()) {
                htmlTable.append("<tr>");
                htmlTable.append("<td>").append(resultSet.getString("OCCTIME")).append("</td>");
                htmlTable.append("<td>").append(resultSet.getString("XQ")).append("</td>");
                htmlTable.append("<td>").append(resultSet.getString("MERCNAME")).append("</td>");
                htmlTable.append("<td>").append(resultSet.getString("TRANAMT")).append("</td>");
                htmlTable.append("<td>").append(resultSet.getString("TRANNAME")).append("</td>");
                htmlTable.append("</tr>");
            }

            htmlTable.append("</table></body></html>");
            return htmlTable.toString();
        } catch (SQLException e) {
            e.printStackTrace();
//            ctx.result("Error retrieving data from the database");
            return "Error retrieving data from the database";
        }
    }

    //最早时间
    public static String count_term_pay_2(Connection conn) {
        try {
            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();

            String query = "SELECT MERCNAME, MIN(TIME(OCCTIME)) AS earliest_time " +
                    "FROM bill " +
                    "WHERE TRANNAME = '持卡人消费' " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY MERCNAME ORDER BY earliest_time ASC";


            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1)));
            ResultSet resultSet = preparedStatement.executeQuery();

            // Adjust the lists to store time as a string or in a suitable format
            List<String> mercNameList = new ArrayList<>();
            List<String> earliestTimeList = new ArrayList<>();

            while (resultSet.next()) {
                String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
                Time earliestTime = resultSet.getTime("earliest_time");

                mercNameList.add(mercname);
                earliestTimeList.add(earliestTime.toString()); // You may need to format the time as per your requirement
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mercName", mercNameList);
            jsonObject.put("earliestTime", earliestTimeList);

            return jsonObject.toJSONString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{\"error\": \"Error executing the query\", \"count\": 0}";
    }

    public static String count_term_pay_3(Connection conn) {
        try {
            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();

            String query = "SELECT MERCNAME, MAX(TIME(OCCTIME)) AS latest_time " +
                    "FROM bill " +
                    "WHERE TRANNAME = '持卡人消费' " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY MERCNAME ORDER BY latest_time ASC";


            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1)));
            ResultSet resultSet = preparedStatement.executeQuery();

            // Adjust the lists to store time as a string or in a suitable format
            List<String> mercNameList = new ArrayList<>();
            List<String> latestTimeList = new ArrayList<>();

            while (resultSet.next()) {
                String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
                Time latestTime = resultSet.getTime("latest_time");

                mercNameList.add(mercname);
                latestTimeList.add(latestTime.toString()); // You may need to format the time as per your requirement
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mercName", mercNameList);
            jsonObject.put("latestTime", latestTimeList);

            return jsonObject.toJSONString();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{\"error\": \"Error executing the query\", \"count\": 0}";
    }

    public static int countDaysWithLessThanThreeRecords(Connection conn) {
        try {
            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();
            // Modify the query to count the number of records for each day
            String query = "SELECT COUNT(*) AS record_count " +
                    "FROM bill " +
                    "WHERE TRANNAME = '持卡人消费' " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY DATE(OCCTIME) " +
                    "HAVING record_count < 3";

            // Use the existing date range logic to set the parameters
            return preparedStatement(conn, startDate, endDate, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 or another suitable value to indicate an error
    }
    public static int countDaysWithLessThanThreeRecords2(Connection conn) {
        try {
            LocalDate startDate = CustomTerm.countTermStart();
            LocalDate endDate = CustomTerm.countTermEnd();
            // Modify the query to count the number of records for each day
            String query = "SELECT COUNT(*) AS record_count " +
                    "FROM bill " +
                    "WHERE TRANNAME = '持卡人消费' " +
                    "AND OCCTIME BETWEEN ? AND ? " +
                    "GROUP BY DATE(OCCTIME) " +
                    "HAVING record_count < 2";

            // Use the existing date range logic to set the parameters
            return preparedStatement(conn, startDate, endDate, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 or another suitable value to indicate an error
    }

    public static int preparedStatement(Connection conn, LocalDate startDate, LocalDate endDate, String query) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
        preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1)));
        ResultSet resultSet = preparedStatement.executeQuery();

        int daysWithLessThanThreeRecords = 0;

        while (resultSet.next()) {
            daysWithLessThanThreeRecords++;
        }

        return daysWithLessThanThreeRecords;
    }


}