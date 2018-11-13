package com.tec.zhang.guancha.recycler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHTML {
    private boolean transferFinish = false;
    final String guanchaUrl = "https://www.guancha.cn/";
    final String picDirectory = "D:\\downloaded\\ugopen\\guanchaPic";
    private Document guanchaWebPage;
    private List<GuanChaSouceData> importantNews;

    public List<GuanChaSouceData> getImportantNews() {
        return importantNews;
    }

    /**

     * 获取头条新闻
     * */
    public  void getHeadLine() {
        try {
            while (!transferFinish) Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        importantNews = new ArrayList<>();
        Elements guanchaHead = guanchaWebPage.getElementsByTag("h3");
        Element headLine = guanchaHead.get(0).select("a").get(0);
        Element headImage = guanchaWebPage.select("img").select("[alt=" + headLine.text() + "]").get(0);
        importantNews.add(new GuanChaSouceData(headLine.text(),"","",headImage.attr("src"),"",0,0,"",""));
/*
		for(Element ele : guanchaHead) {
			Elements topOne = ele.select("a");
			for(Element ele1 : topOne) {

			}
		}
*/

        Elements viseHead = guanchaWebPage.select(".content-headline").select(".c_hidden");
        if (viseHead.size() != 0) {
            for (Element ele : viseHead) {
                if (!ele.text().equals("")) {
                    importantNews.add(new GuanChaSouceData(ele.text(),"","","","",0,0,"",""));
                }
            }
            //System.out.println(headImage.attr("src"));
        }
    }

    public List<GuanChaSouceData> getNormalNews() {
        return normalNews;
    }

    private List<GuanChaSouceData> normalNews;

    public void createNormalNews() {
        /**
         * 观网新闻分成三个板块
         * 属性都是各不同的
         * 需要分别解析
         * */

        try {
            while (!transferFinish) Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        normalNews = new ArrayList<>();
        //以下是第一段的解析，装填入新闻列表
        Elements firstSegment = guanchaWebPage.select(".module-news-main").get(0).select("li");
        for(Element ele : firstSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            //System.out.println(title.get(0).text());
            String newsTitle = title.get(0).text();
            String authorName = ele.select(".fix").get(0).select("a").get(1).text();
            //System.out.println(authorName);
            String authorIntro = ele.select("span").get(0).text();
            //System.out.println(authorIntro);
            String imageUrl = ele.select(".module-img").get(0).select("img").get(0).attr("abs:src");
            //System.out.println(imageUrl);
            String shortArticle = ele.select(".module-artile").get(0).text();
            shortArticle = shortArticle.substring(0, shortArticle.length() - 5);
            //System.out.println(shortArticle);
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            //System.out.println(readsNum);
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            //System.out.println(commentNum);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = null;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            //System.out.println(belongTo);
            String articleUrl = title.select("a").get(0).attr("abs:href");
            normalNews.add(new GuanChaSouceData(newsTitle, authorName, authorIntro, imageUrl, shortArticle, readsNum, commentNum, belongTo,articleUrl));
        }
        //以下是第二段解析
        Elements secondSegment = guanchaWebPage.select(".img-List").select("li");
        //System.out.println(secondSegment.size());
        for(Element ele : secondSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            //System.out.println(title.get(0).text());
            String newsTitle = title.get(0).text();
            String imageUrl = ele.select(".fastRead-img").get(0).select("img").get(0).attr("abs:src");
            //System.out.println(imageUrl);
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            //System.out.println(readsNum);
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            //System.out.println(commentNum);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = null;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            //System.out.println(belongTo);
            String articleUrl = title.select("a").get(0).attr("abs:href");
            GuanChaSouceData sourceData = new GuanChaSouceData(newsTitle, "", "", imageUrl, "", readsNum, commentNum, belongTo,articleUrl);
            //System.out.println(sourceData);
            normalNews.add(sourceData);
        }
        //以下为第三段解析,风闻社区为动态加载模式，暂且放弃
        Elements lastSegment = guanchaWebPage.select(".img-List").select("li");
        //System.out.println(lastSegment.childNodeSize());
        for(Element ele : secondSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            //System.out.println(title.get(0).text());
            String newsTitle = title.get(0).select("a").get(0).text();
            String imageUrl = ele.select(".fastRead-img").get(0).select("img").get(0).attr("abs:src");
            //System.out.println(imageUrl);
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            //System.out.println(readsNum);
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            //System.out.println(commentNum);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = null;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            //System.out.println(belongTo);
            String articleUrl = title.select("a").get(0).attr("abs:href");
            GuanChaSouceData sourceData = new GuanChaSouceData(newsTitle, "", "", imageUrl, "", readsNum, commentNum, belongTo,articleUrl);
            //System.out.println(sourceData);
            normalNews.add(sourceData);
        }
    }

    /**
     * 初始化网页对象
     *
     * */
    public void init() {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        guanchaWebPage = Jsoup.connect(guanchaUrl).get();
                        transferFinish = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

    }

    /**
     * 内部类用来存储用户数据
     *
     *
     * */
    public static class GuanChaSouceData{
        private String articleUrl;
        private String title;
        private String authorName = new String();
        private String authorIntroduction = new String();
        private String imageUrl;
        private String shortArticle = new String();
        private int readsNum = 0;
        private int commentNum = 0;
        private String belongTo = new String();

        public GuanChaSouceData(String title, String authorName, String authorIntroduction, String imageUrl,
                                String shortArticle, int readsNum, int commentNum, String belongTo,String articleUrl) {
            super();
            this.title = title;
            this.authorName = authorName;
            this.authorIntroduction = authorIntroduction;
            this.imageUrl = imageUrl;
            this.shortArticle = shortArticle;
            this.readsNum = readsNum;
            this.commentNum = commentNum;
            this.belongTo = belongTo;
            this.articleUrl = articleUrl;
        }
        public String getBelongTo() {
            return belongTo;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public int getReadsNum() {
            return readsNum;
        }

        public String getShortArticle() {
            return shortArticle;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getAuthorIntroduction() {
            return authorIntroduction;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getTitle() {
            return title;
        }
        public String toString(){
            return title + "  " + authorName + "  " + authorIntroduction + "  " + imageUrl + "  " + shortArticle + "  " + readsNum + "  " + commentNum + "  " + belongTo + "  " + articleUrl;
        }
        public String getArticleUrl() {
            return articleUrl;
        }
    }
}
