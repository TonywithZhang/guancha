package com.tec.zhang.guancha.recycler;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import androidx.annotation.NonNull;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ParseHTML {
    private boolean transferFinish = false;
    final String guanchaUrl = "https://www.guancha.cn/";
    //final String picDirectory = "D:\\downloaded\\ugopen\\guanchaPic";
    private Document guanchaWebPage;
    private ArrayList<GuanChaSouceData> importantNews;

    public ArrayList<GuanChaSouceData> getImportantNews() {
        return importantNews;
    }


    public void setTransferFinish(boolean transferFinish) {
        this.transferFinish = transferFinish;
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
        importantNews.add(new GuanChaSouceData(NEWS_TYPE.FIRST_PAGE,headLine.text(),"","",headImage.attr("abs:src"),"",0,0,"",headLine.attr("abs:href")));

        Elements viseHead = guanchaWebPage.select(".content-headline").select(".c_hidden");
        if (viseHead.size() != 0) {
            for (Element ele : viseHead) {
                if (!ele.text().equals("")) {
                    importantNews.add(new GuanChaSouceData(NEWS_TYPE.FIRST_PAGE,ele.text(),"","","","",0,0,"",ele.select("a").get(0).attr("abs:href")));
                }
            }
        }
    }

    public List<GuanChaSouceData> getNormalNews() {
        return normalNews;
    }

    private ArrayList<GuanChaSouceData> normalNews;

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
        parseThreeColumn(guanchaWebPage,normalNews,NEWS_TYPE.FIRST_PAGE,"首页");
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
    public static class GuanChaSouceData implements Parcelable {
        private boolean isHeadLine = false;
        private NEWS_TYPE newsType;
        private String articleUrl;
        private String title;
        private String authorName;
        private String authorIntroduction;
        private String imageUrl;
        private String shortArticle;
        private int readsNum = 0;
        private int commentNum = 0;
        private String belongTo;

        public GuanChaSouceData(NEWS_TYPE newsType,String title, String authorName, String authorIntroduction, String imageUrl,
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

        public void setHeadLine(boolean headLine) {
            isHeadLine = headLine;
        }

        public NEWS_TYPE getNewsType() {
            return newsType;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.articleUrl);
            dest.writeString(this.title);
            dest.writeString(this.authorName);
            dest.writeString(this.authorIntroduction);
            dest.writeString(this.imageUrl);
            dest.writeString(this.shortArticle);
            dest.writeInt(this.readsNum);
            dest.writeInt(this.commentNum);
            dest.writeString(this.belongTo);
        }

        protected GuanChaSouceData(Parcel in) {
            this.articleUrl = in.readString();
            this.title = in.readString();
            this.authorName = in.readString();
            this.authorIntroduction = in.readString();
            this.imageUrl = in.readString();
            this.shortArticle = in.readString();
            this.readsNum = in.readInt();
            this.commentNum = in.readInt();
            this.belongTo = in.readString();
        }

        public static final Parcelable.Creator<GuanChaSouceData> CREATOR = new Parcelable.Creator<GuanChaSouceData>() {
            @Override
            public GuanChaSouceData createFromParcel(Parcel source) {
                return new GuanChaSouceData(source);
            }

            @Override
            public GuanChaSouceData[] newArray(int size) {
                return new GuanChaSouceData[size];
            }
        };
    }
    /**
     * 得到模块的链接
     * */
    private ArrayList<String> moduleUrl;
    public void createModuleUrls(){
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        moduleUrl = new ArrayList<>();

        Elements eles = guanchaWebPage.select(".header-nav-cell").select("a");
        if(eles.size() != 0) {
            for(Element ele : eles) {
                moduleUrl.add(ele.attr("abs:href"));
            }
        }
    }

    /**
     * 风闻页面解析
     *
     * */
    private ArrayList<GuanChaSouceData> fengwenList;
    public void parseFengwen(){
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fengwenList = new ArrayList<>();

        try {
            Document fengwenDoc = Jsoup.connect(moduleUrl.get(1)).get();
            Elements fengwenNews = fengwenDoc.select(".active").select(".orderby-last-publish").select("li");
            for(Element ele : fengwenNews) {
                if(ele.select(".list-item").size() == 0) {
                    continue;
                }
                String articleUrl = ele.select(".list-item").select("h4").get(0).select("a").get(0).attr("abs:href");
                String title = ele.select(".list-item").select("h4").get(0).text();
                String authorName = ele.select(".user-box").select(".user-main").select("a").get(0).text();
                String authorIntroduction = ele.select(".user-box").select(".user-main").select("p").get(0).text();
                String imageUrl = ele.select(".item-pic").select("img").size() == 0? "": ele.select(".item-pic").select("img").get(0).attr("abs:src");
                String shortArticle = ele.select(".item-info").select(".item-content").get(0).text();
                Elements readsString = ele.select(".op-tools").select("a");
                int reads = 0;
                for(Element read : readsString) {
                    if(read.text().startsWith("点击")) {
                        reads = read.select("span").get(0).text().equals("")? 0: Integer.parseInt(read.select("span").get(0).text());
                    }
                }
                String commentString = ele.select(".op-tools").select(".comment").select("span").get(0).text();
                int comment = commentString.equals("")?0 : Integer.parseInt(commentString);
                String belongTo = ele.select(".topic_tag").get(0).text();
                fengwenList.add(new GuanChaSouceData(NEWS_TYPE.FENGWEN,title, authorName, authorIntroduction, imageUrl, shortArticle, reads, comment, belongTo, articleUrl));
            }
            Log.d(TAG, "parseFengwen: 新闻列表总共" + fengwenList.size() + "条");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GuanChaSouceData> getFengwenList() {
        return fengwenList;
    }

    /**
     * 国际页面解析
     * */
    private ArrayList<GuanChaSouceData> internationalNews;

    public ArrayList<GuanChaSouceData> getInternationalNews() {
        return internationalNews;
    }

    public void createInternationalNews(){
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        internationalNews = new ArrayList<>();
        try {
            Document internationalPage = Jsoup.connect(moduleUrl.get(2)).get();
            if (internationalPage.select(".two-coloum").size() == 0)
            parseThreeColumn(internationalPage,internationalNews,NEWS_TYPE.INTERNATIONAL,"国际");
            else parseTwoColumn(internationalPage,internationalNews,NEWS_TYPE.INTERNATIONAL,"国际");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 军事页面解析
     * */
    public ArrayList<GuanChaSouceData> getMilitaryNews() {
        return militaryNews;
    }

    private  ArrayList<GuanChaSouceData> militaryNews;
    public  void createMilitaryNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        militaryNews = new ArrayList<>();

        try {
            Document militaryPage = Jsoup.connect(moduleUrl.get(3)).get();
            if (militaryPage.select(".two-coloum").size() == 0)
                parseThreeColumn(militaryPage,militaryNews,NEWS_TYPE.MILITARY,"军事");
            else parseTwoColumn(militaryPage,militaryNews,NEWS_TYPE.MILITARY,"军事");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 财经页面解析
     * */
    public ArrayList<GuanChaSouceData> getFinancialNews() {
        return financialNews;
    }

    private ArrayList<GuanChaSouceData> financialNews;
    public void createFinancialNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        financialNews = new ArrayList<>();
        try {
            Document financialPage = Jsoup.connect(moduleUrl.get(4)).get();
            if (financialPage.select(".two-coloum").size() == 0)
                parseThreeColumn(financialPage,financialNews,NEWS_TYPE.FINANCIAL,"财经");
            else parseTwoColumn(financialPage,financialNews,NEWS_TYPE.FINANCIAL,"财经");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 产经页面解析
     * */
    public ArrayList<GuanChaSouceData> getProductionNews() {
        return productionNews;
    }

    private ArrayList<GuanChaSouceData> productionNews;
    public void createProductionNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        productionNews = new ArrayList<>();

        try {
            Document productionPage = Jsoup.connect(moduleUrl.get(5)).get();
            if (productionPage.select(".two-coloum").size() == 0)
                parseThreeColumn(productionPage,productionNews,NEWS_TYPE.PRODUCTION,"产经");
            else parseTwoColumn(productionPage,productionNews,NEWS_TYPE.PRODUCTION,"产经");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 科技页面解析
     * */
    public ArrayList<GuanChaSouceData> getTecnologyNews() {
        return tecnologyNews;
    }

    private ArrayList<GuanChaSouceData> tecnologyNews;
    public void createTecnologyNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tecnologyNews = new ArrayList<>();

        try {
            Document tecnologyPage = Jsoup.connect(moduleUrl.get(6)).get();
            if (tecnologyPage.select(".two-coloum").size() == 0)
                parseThreeColumn(tecnologyPage,tecnologyNews,NEWS_TYPE.TECHNOLOGY,"科技");
            else parseTwoColumn(tecnologyPage,tecnologyNews,NEWS_TYPE.TECHNOLOGY,"科技");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 汽车页面解析
     * */
    public ArrayList<GuanChaSouceData> getAutoNews() {
        return autoNews;
    }

    private ArrayList<GuanChaSouceData> autoNews;
    public void createAutoNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        autoNews = new ArrayList<>();

        try {
            Document autoPage = Jsoup.connect(moduleUrl.get(7)).get();
            if (autoPage.select(".two-coloum").size() == 0)
                parseThreeColumn(autoPage,autoNews,NEWS_TYPE.AUTO,"汽车");
            else parseTwoColumn(autoPage,autoNews,NEWS_TYPE.AUTO,"汽车");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 智库界面解析
     * */
    public ArrayList<GuanChaSouceData> getLeadAheadNews() {
        return leadAheadNews;
    }

    private ArrayList<GuanChaSouceData> leadAheadNews;
    public void createLeadAheadNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        leadAheadNews = new ArrayList<>();

        try {
            Document leadPage = Jsoup.connect(moduleUrl.get(8)).get();
            Elements toutiao = leadPage.select("#idSlider").get(0).select("a");
            for(Element ele : toutiao) {
                String articleUrl = ele.attr("abs:href");
                String title = ele.select("img").get(0).attr("alt");
                String imageUrl = ele.select("img").get(0).attr("abs:src");
                leadAheadNews.add(new GuanChaSouceData(NEWS_TYPE.LEADING,title, "", "", imageUrl, "", 0, 0, "", articleUrl));
            }

            //普通新闻解析
            Elements normalNews = leadPage.select(".edge-list").select("li");
            for(Element ele : normalNews) {
                Elements titleSection = ele.select("h4").select("a");
                String articleUrl = titleSection.get(titleSection.size() - 1).attr("abs:href");
                String title = titleSection.get(titleSection.size() - 1).text();
                String authorName = ele.select(".author-intro").select("p").select("a").get(0).text();
                String authorIntroduction =ele.select(".author-intro").select("span").get(0).text();
                String imageUrl = ele.select(".img").select("img").attr("abs:src");
                String shortArticle = ele.select(".module-artile").get(0).text();
                int readsNum = 0;
                int commentNum = 0;
                String belongTo = "";
                leadAheadNews.add(new GuanChaSouceData(NEWS_TYPE.LEADING,title, authorName, authorIntroduction, imageUrl, shortArticle, readsNum, commentNum, belongTo, articleUrl));
            }
        }catch(IOException e) {e.printStackTrace();}
    }
    /**
     * 视频页面解析
     * */
    public ArrayList<GuanChaSouceData> getVideoNews() {
        return videoNews;
    }

    private ArrayList<GuanChaSouceData> videoNews;
    public void createVideoNews() {
        while (!transferFinish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        videoNews = new ArrayList<>();

        try {
            Document videoPage = Jsoup.connect(moduleUrl.get(moduleUrl.size() - 1)).get();
            //新闻分为两页，下面是第一页
            Elements firstSegment = videoPage.select(".new-left-list").select("li");
            for(Element ele : firstSegment) {
                String articleUrl = ele.select("h4").select("a").get(0).attr("abs:href");
                String title = ele.select("h4").get(0).text();
                String authorName = "";
                String authorIntroduction = "";
                String imageUrl = ele.select("img").get(0).attr("abs:src");
                if(!imageUrl.substring(imageUrl.length() - 5).contains(".")) {
                    imageUrl = "";
                }
                String shortArticle = "";
                int readsNum = ele.select(".interact-attention").get(0).text().equals("") ? 0 : Integer.parseInt(ele.select(".interact-attention").get(0).text());
                int commentNum = ele.select(".interact-comment").get(0).text().equals("") ? 0 : Integer.parseInt(ele.select(".interact-comment").get(0).text());
                String belongTo = ele.select(".interact-key").size() == 0 ? "" : ele.select(".interact-key").get(0).text();
                videoNews.add(new GuanChaSouceData(NEWS_TYPE.VIDEO,title, authorName, authorIntroduction, imageUrl, shortArticle, readsNum, commentNum, belongTo, articleUrl));
            }

            //第二段解析
            Elements secondSegment = videoPage.select(".Review-item").select("li");
            for(Element ele : secondSegment) {
                String articleUrl = ele.select("h4").select("a").get(0).attr("abs:href");
                String title = ele.select("h4").get(0).text();
                String authorName = "";
                String authorIntroduction = "";
                String imageUrl = "";
                String shortArticle = ele.select("p").text();
                int readsNum = ele.select(".interact-attention").get(0).text().equals("") ? 0 : Integer.parseInt(ele.select(".interact-attention").get(0).text());
                int commentNum = ele.select(".interact-comment").get(0).text().equals("") ? 0 : Integer.parseInt(ele.select(".interact-comment").get(0).text());
                String belongTo = ele.select(".interact-key").size() == 0 ? "" : ele.select(".interact-key").get(0).text();
                videoNews.add(new GuanChaSouceData(NEWS_TYPE.VIDEO,title, authorName, authorIntroduction, imageUrl, shortArticle, readsNum, commentNum, belongTo, articleUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 枚举类，表示新闻的类型
     * */
    public static enum NEWS_TYPE{
        FIRST_PAGE,FENGWEN,INTERNATIONAL,MILITARY,FINANCIAL,PRODUCTION,TECHNOLOGY,AUTO,LEADING,VIDEO
    }

    //解析三段格式的新闻页面
    private void parseThreeColumn(@NonNull Document page, ArrayList<GuanChaSouceData> moduleList, NEWS_TYPE type, String moduleName){
        Elements toutiao = page.select(".new-header-list").select("li");
        for(Element ele : toutiao) {
            String articleUrl = ele.select("h4").select("a").get(0).attr("abs:href");
            String title = ele.select("h4").text();
            String authorName = "";
            String authorIntroduction = "";
            String imageUrl = ele.select("img").attr("abs:src");
            String shortArticle = "";
            String readsNumString = ele.select(".interact-attention").get(0).text();
            int readsNum = readsNumString.equals("")? 0 : Integer.parseInt(readsNumString);
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = commentString.equals("") ? 0 : Integer.parseInt(commentString);
            String belongTo = moduleName;
            moduleList.add(new GuanChaSouceData(type,title, authorName, authorIntroduction, imageUrl, shortArticle, readsNum, commentNum, belongTo, articleUrl));
        }
        //下面是三段新闻解析
        Elements firstSegment = page.select(".module-news-main").get(0).select("li");
        for(Element ele : firstSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            String newsTitle = title.get(0).text();
            String authorName = ele.select(".fix").get(0).select("a").get(1).text();
            String authorIntro = ele.select("span").get(0).text();
            String imageUrl = ele.select(".module-img").size() == 0 ? "" : ele.select(".module-img").get(0).select("img").get(0).attr("abs:src");
            String shortArticle = ele.select(".module-artile").get(0).text();
            shortArticle = shortArticle.substring(0, shortArticle.length() - 5);
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = moduleName;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            String articleUrl = title.select("a").get(0).attr("abs:href");
            moduleList.add(new GuanChaSouceData(type,newsTitle, authorName, authorIntro, imageUrl, shortArticle, readsNum, commentNum, belongTo,articleUrl));
        }
        //以下是第二段解析
        Elements secondSegment = page.select(".img-List").select("li");
        for(Element ele : secondSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            String newsTitle = title.get(0).text();
            String imageUrl = ele.select(".fastRead-img").size() == 0 ? "" : ele.select(".fastRead-img").get(0).select("img").get(0).attr("abs:src");
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = moduleName;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            String articleUrl = title.select("a").get(0).attr("abs:href");
            GuanChaSouceData sourceData = new GuanChaSouceData(type,newsTitle, "", "", imageUrl, "", readsNum, commentNum, belongTo,articleUrl);
            moduleList.add(sourceData);
        }
        //以下为第三段解析
        Elements lastSegment = page.select(".img-List").select("li");
        for(Element ele : lastSegment) {
            Elements title = ele.select("h4");
            if(title.size() == 0) continue;
            String newsTitle = title.get(0).select("a").get(0).text();
            String imageUrl = ele.select(".fastRead-img").get(0).select("img").get(0).attr("abs:src");
            int readsNum = Integer.parseInt(ele.select(".interact-attention").get(0).text());
            String commentString = ele.select(".interact-comment").get(0).text();
            int commentNum = (commentString.equals(""))? 0 :Integer.parseInt(commentString);
            Elements elementBelongTo = ele.select(".interact-key");
            String belongTo = moduleName;
            if(elementBelongTo.size() == 0) {
                belongTo = "";
            }else {belongTo = elementBelongTo.get(0).text();}
            String articleUrl = title.select("a").get(0).attr("abs:href");
            GuanChaSouceData sourceData = new GuanChaSouceData(type,newsTitle, "", "", imageUrl, "", readsNum, commentNum, belongTo,articleUrl);
            moduleList.add(sourceData);}
    }

    private void parseTwoColumn(@NonNull Document page,ArrayList<GuanChaSouceData> moduleList, NEWS_TYPE type, String moduleName){
        Elements mainPage = page.select(".two-coloum");

        //第一段的头条新闻解析
        Elements toutiao = mainPage.select(".main_image").select("li");
        for(Element ele : toutiao) {
            String articleUrl = ele.select("a").get(0).attr("abs:href");
            String imageUrl = ele.select("img").get(0).attr("abs:src");
            String title = ele.select("span").get(0).text();
            moduleList.add(new GuanChaSouceData(type,title, "", "", imageUrl, "", 0, 0, moduleName, articleUrl));
        }
        //第一段剩下的文章的解析
        Elements firstSegment = mainPage.select(".new-left-list").select("li");
        for(Element ele : firstSegment) {
            String imageUrl = ele.select("img").get(0).attr("abs:src");
            String articleUrl = ele.select("a").get(0).attr("abs:href");
            String title = ele.select("h4").get(0).text();
            String shortArticle = ele.select("p").text();
            String belongTo = ele.select(".interact-key").text();
            moduleList.add(new GuanChaSouceData(type,title, "", "", imageUrl, shortArticle, 0, 0, belongTo, articleUrl));
        }

        Elements secondSegment = mainPage.select(".column-right-title").select("li");
        for(Element ele : secondSegment) {
            String articleUrl = ele.select("a").get(0).attr("abs:href");
            String title = ele.select("a").get(0).text();
            moduleList.add(new GuanChaSouceData(type,title, "", "", "", "", 0, 0, moduleName, articleUrl));
        }
    }
}
