package com.tomato830.redis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dateTest {
    public static void main(String[] args) throws ParseException {
        Date date=new Date();
        String s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        System.out.println(s);
        //DateFormat dateFormat=new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        System.out.println("date:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s));
    }
}
