package com.tomato830.redis;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import redis.clients.jedis.Client;

public class createArticle {
    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务器正在运行: " + jedis.ping());

        //id生成器
        long idCreator=jedis.incr("id");
        String title="Redis入门指南";
        String author="李子骅";
        String content="本书是一本Redis的入门指导书籍，以通俗易懂的方式介绍了Redis基础与实践方面的知识，包括历史与特性、在开发和生产环境中部署运行Redis、数据类型与命令、使用Redis实现队列、事务、复制、管道、持久化、优化Redis存储空间等内容，并采用任务驱动的方式介绍了PHP、Ruby、Python和Node.js这4种语言的Redis客户端库的使用方法。\n" +
                "本书的目标读者不仅包括Redis的新手，还包括那些已经掌握Redis使用方法的人。对于新手而言，本书的内容由浅入深且紧贴实践，旨在让读者真正能够即学即用；对于已经了解Redis的读者，通过本书的大量实例以及细节介绍，也能发现很多新的技巧。";
        ArrayList<String> tag=new ArrayList<>();
        tag.add("入门");
        tag.add("简介");
        ArrayList<String> comment=new ArrayList<>();
        comment.add("谢谢楼主!");
        comment.add("写得很好!");
        Date date=new Date();

        //hset()的map只接受<String,String>
        Map<String,String> article=new HashMap<>();
        article.put("id",String.valueOf(idCreator));
        article.put("view","120");
        article.put("title",title);
        article.put("author",author);
        article.put("content",content);
        article.put("tag",tag.toString());
        article.put("comment",comment.toString());
        article.put("postTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

        if (article.isEmpty())
            System.out.println("空");
        //hset中的map只能有一对键值对
        //hmset中的map可以有多对
        String res=jedis.hmset("article:"+Long.toString(idCreator),article);
        //long res=jedis.hset("article:"+Long.toString(idCreator),"id",Long.toString(idCreator));
        System.out.println("return code:"+res);
    }
}
