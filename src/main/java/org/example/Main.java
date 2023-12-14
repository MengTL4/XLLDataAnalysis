package org.example;

import Utils.JdbcUtils;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import static org.example.Electricity.getElectricity;

public class Main {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(7070);
        app.get("/", ctx -> {
            // 执行重定向到 "/index.html"
            ctx.redirect("/index.html");
        });
        //更新消费记录
        app.get("/update",ctx->{
            DealBill.updateData();
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.result("更新成功");
        });
        //年度账单
        app.get("/year", ctx -> {
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json(YearPayApi.requestSend(YearPayApi.requestParams()));
        });
        //总的消费次数
        app.get("/total_frequency",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json(CrudApi.countMerchantFrequency(JdbcUtils.getConnect()));
        });
        //本学期消费次数(智能判断学期)
        app.get("/term_frequency",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json(CrudApi.count_term_frequency(JdbcUtils.getConnect()));
        });
        //本学期消费金额排行
        app.get("/term_pay",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json( CrudApi.count_term_pay(JdbcUtils.getConnect()));
        });
        //最早时间
        app.get("/time_early",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json( CrudApi.count_term_pay_2(JdbcUtils.getConnect()));
        });
        //最晚时间
        app.get("/time_latest",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.json( CrudApi.count_term_pay_3(JdbcUtils.getConnect()));
        });
        //吃饭少于3次
        app.get("/launch_three",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.result(String.valueOf(CrudApi.countDaysWithLessThanThreeRecords(JdbcUtils.getConnect())));
        });
        //吃饭少于2次
        app.get("/launch_two",ctx->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.result(String.valueOf(CrudApi.countDaysWithLessThanThreeRecords2(JdbcUtils.getConnect())));
        });
        //消费记录
        app.get("/bill", ctx -> {
            ctx.contentType("text/html; charset=UTF-8");
            ctx.html(CrudApi.billTable());
        });
        app.post("/buildingelec",ctx ->{
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.result(getElectricity(ctx.formParam("jsondata")));
        });
    }
}