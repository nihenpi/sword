package com.skaz;

import com.skaz.utils.Dates;

import java.util.Date;

public class Test {
    public static void main(String[] args) {
        String month = "2018-06";
        Date sourceMonth = Dates.parse(month, "yyyy-MM");
        Date endMonth = Dates.addMonth(sourceMonth, 1);
        System.out.println(endMonth.getTime());
    }
}
