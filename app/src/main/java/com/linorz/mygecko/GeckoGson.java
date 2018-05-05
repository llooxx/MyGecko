package com.linorz.mygecko;

import java.util.List;

/**
 * Created by linorz on 2018/3/8.
 */

public class GeckoGson {

    private List<GeckoBean> geckos;

    public List<GeckoBean> getGeckos() {
        return geckos;
    }

    public void setGeckos(List<GeckoBean> geckos) {
        this.geckos = geckos;
    }

    public static class GeckoBean {
        /**
         * num : 3
         * kind : srd
         * gender : 公
         * birth : 20180101
         * eggtime : 20171201
         * parent : 1x2
         * place : 1-1
         * weight : 30
         * picture : 3srd20180101公.jpg
         */

        private int num;
        private String kind;
        private String gender;
        private String birth;
        private String eggtime;
        private String parent;
        private String place;
        private int weight;
        private String picture;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getEggtime() {
            return eggtime;
        }

        public void setEggtime(String eggtime) {
            this.eggtime = eggtime;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }
}
