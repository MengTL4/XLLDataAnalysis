package org.example;

public class DealBill {
    public static void updateData() throws Exception {
        int totalValue = Bill.getTotalValue();
        int round = (int) Math.ceil((double) totalValue / 15);
        for (int page = 1; page <= round; page++) {
            String response = Bill.requestParams(page);
            Bill.doInsertMyBillData(response);
            }
        }
    }
