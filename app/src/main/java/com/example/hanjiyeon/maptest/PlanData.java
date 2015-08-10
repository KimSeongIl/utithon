package com.example.hanjiyeon.maptest;


public class PlanData
{

        private int id;
        private double lat;
        private double lng;
        private String content;
        private int plansuc;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLat() {
            return lat;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLng() {
            return lng;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setPlansuc(int plansuc) {
            this.plansuc = plansuc;
        }

        public int getPlansuc() {
            return plansuc;
        }
}
