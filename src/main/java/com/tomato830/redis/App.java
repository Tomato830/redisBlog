package com.tomato830.redis;

import com.tomato830.redis.blogSystem.Article;
import redis.clients.jedis.Jedis;

import java.io.BufferedInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class App
{
    public static void main( String[] args ) throws ParseException {
        //连接redis
        Jedis jedis=new Jedis("localhost");
        if (jedis.ping().equals("PONG")){
            boolean b=true;
            while (b){
                System.out.println( "欢迎来到博客管理系统!");
                System.out.println( "请选择你的操作:");
                System.out.println( "0.创建文章\n" +
                        "1.显示所有文章\n" +
                        "2.搜索文章的标签\n"+
                        "3.显示热门文章\n"+
                        "4.退出\n");
                System.out.print("请输入你的选择:");
                Scanner in=new Scanner(new BufferedInputStream(System.in));
                int input=in.nextInt();

                switch (input){
                    case 0:
                        createArticle(jedis);
                        break;
                    case 1:
                        int p=1;
                        while (true){
                            showArticleOfPage(jedis,p);
                            System.out.print("请输入要看的页数(输入0退出):");
                            Scanner pageIn=new Scanner(new BufferedInputStream(System.in));
                            p=pageIn.nextInt();
                            if (p==0) break;
                        }
                        break;
                    case 2:
                        searchTags(jedis);
                        break;
                    case 3:
                        displayHot(jedis);
                        break;
                    case 4:
                        b=false;
                        break;
                    default:
                }
            }
        } else {
            System.out.println("redis连接失败!");
        }
    }

    static void createArticle(Jedis jedis){
        String title,author,content;
        ArrayList<String> tags;
        System.out.println("目前正在发布文章.....");
        System.out.print("请输入标题:");
        Scanner input=new Scanner(new BufferedInputStream(System.in));
        title=input.next();
        System.out.print("请输入文章作者:");
        author=input.next();
        System.out.print("请输入文章内容:");
        content=input.next();
        System.out.print("请输入文章的标签(用英文逗号隔开):");
        tags= new ArrayList<String>(Arrays.asList(input.next().split(",")));

        //hset()的map只接受<String,String>
        long idCreator=jedis.incr("id");
        Map<String,String> article=new HashMap<>();
        //article.put("id",String.valueOf(idCreator));
        jedis.lpush("article:list",String.valueOf(idCreator));
        article.put("view","0");
        article.put("title",title);
        article.put("author",author);
        article.put("content",content);
        //article.put("tag",tag.toString());
        for (String t:tags){
            jedis.sadd("article:"+Long.toString(idCreator)+":tags",t);
            jedis.sadd("tags:"+t+":article",Long.toString(idCreator));
        }
        article.put("postTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        String res=jedis.hmset("article:"+Long.toString(idCreator),article);
        if (res.equals("OK")){
            System.out.println("文章发布成功!");
        }else {
            System.out.println("文章发布失败!");
        }

        System.out.print("输入任意数字返回.....");
        Scanner scanner=new Scanner(new BufferedInputStream(System.in));
        String s=scanner.next();
        return;
    }

    //返回一页的文章,每页5篇文章
    static void showArticleOfPage(Jedis jedis,int page) throws ParseException {

        List<String> articleList=new ArrayList<>();
        articleList=jedis.lrange("article:list",0,-1);

        //判断总页数
        int numOfPage=5;//每页的文章数
        int totalPages;
        if(articleList.size()%numOfPage==0){
            totalPages=articleList.size()/numOfPage;
        } else {
            totalPages=articleList.size()/numOfPage+1;
        }
        //超过最大页数时只显示最大页数
        if (page>totalPages) page=totalPages;

        System.out.println("所有文章:(第"+page+"页/共"+totalPages+"页)");

        //只显示标题

        for (int i=(page-1)*numOfPage;i<page*numOfPage&&i<articleList.size();++i){
            System.out.print('('+String.valueOf(i)+')');
            //System.out.print("id:"+articles.get(i).getId());
            System.out.println("标题:"+jedis.hget("article:"+articleList.get(i),"title")+"(浏览数:"+jedis.hget("article:"+articleList.get(i),"view")+")");
            //+"评论数:"+articles.get(i).getComment().size()+')'
        }

    }

    static ArrayList<String> string2ArrayList(String s){
        ArrayList<String> res= new ArrayList<String>(Arrays.asList(s.substring(1,s.length()-1).split(",")));
        return res;
    }

    static void searchTags(Jedis jedis){
        Set<String> tagSet=new HashSet<>();
        tagSet=jedis.keys("tags:*:article");
        System.out.println("所有标签:");
        for (String s:tagSet){
            System.out.print(s.split(":")[1]+'\t');
        }
        System.out.println();

        System.out.print("请输入文章的标签(多个标签用英文逗号隔开):");
        Scanner inputTag=new Scanner(new BufferedInputStream(System.in));
        ArrayList<String> tags=new ArrayList<>(Arrays.asList(inputTag.next().split(",")));

        if (tags.isEmpty()){
            System.out.print("输入任意数字返回.....");
            Scanner scanner=new Scanner(new BufferedInputStream(System.in));
            String s=scanner.next();
            return;
        }
        System.out.println("包含标签"+tags.toString()+"的文章有:");
        if (tags.size()==1){
            int i=1;
            Set<String> res=jedis.smembers("tags:"+tags.get(0)+":article");
            if(res.isEmpty()) System.out.println("的结果为空");
            for (String s:res){
                System.out.println(i+"."+jedis.hget("article:"+s,"title")+"(浏览数:"+jedis.hget("article:"+s,"view")+")");
                i++;
            }
        } else {
            int i=1;
            String[] list=new String[tags.size()];
            for(int j=0;j<tags.size();++j){
                list[j]="tags:"+tags.get(j)+":article";
            }
            Set<String> res=jedis.sinter(list);
            if (res.isEmpty()) System.out.println("结果为空!");
            for (String s:res){
                System.out.println(i+"."+jedis.hget("article:"+s,"title")+"(浏览数:"+jedis.hget("article:"+s,"view")+")");
                i++;
            }
        }

        System.out.print("输入任意数字返回.....");
        Scanner scanner=new Scanner(new BufferedInputStream(System.in));
        String s=scanner.next();
        return;
    }

    static void displayHot(Jedis jedis){
        //0到4显示五条
        String stop="4";
        Set<String> list=jedis.zrevrange("article:view",Long.valueOf("0"),Long.valueOf(stop));
        ArrayList<String > hotArray=new ArrayList<>();
        for (String s:list){
            hotArray.add(s);
        }
        System.out.println("显示最热的五篇文章:");
        for(int i=0;i<hotArray.size()&&i<5;++i){
            System.out.print('('+String.valueOf(i)+')');
            System.out.println("标题:"+jedis.hget("article:"+hotArray.get(i),"title")+"(浏览数:"+jedis.hget("article:"+hotArray.get(i),"view")+")");
        }

        System.out.print("输入任意数字返回.....");
        Scanner scanner=new Scanner(new BufferedInputStream(System.in));
        String s=scanner.next();
        return;
    }
}
