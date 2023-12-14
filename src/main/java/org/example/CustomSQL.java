package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomSQL {
    public static List<String> resmonthList(ResultSet resultSet, List<String> resmonthList) throws SQLException {
        while (resultSet.next()) {
            // Remove spaces
            String mercname = resultSet.getString("MERCNAME").replaceAll("\\s", "");
            int frequency = resultSet.getInt("frequency");

            resmonthList.add(mercname);
        }
        return resmonthList;
    }
    public static List<String> amtList(ResultSet resultSet) throws SQLException {
        List<String> amtList = new ArrayList<>();
        while (resultSet.next()) {
            int frequency = resultSet.getInt("frequency");
            amtList.add(String.valueOf(frequency));
        }
        return amtList;
    }
    public static int maxAmt(ResultSet resultSet) throws SQLException {
        int maxamt = 0;
        while (resultSet.next()) {
            int frequency = resultSet.getInt("frequency");
            if (frequency > maxamt) {
                maxamt = frequency;
            }
        }
        return maxamt;
    }

}
