package com.example.getsumfoot.data;

public class EventMapData {

    public int 위도,경도;

    public EventMapData(int 위도, int 경도){
        this.위도 = 위도;
        this.경도 = 경도;
    }

        public int get_latitude(){
            return 위도;
        }

        public void set_latitude(int 위도) {
            this.위도 = 위도;
        }

        public int get_longitude(){
            return 경도;
        }

        public void set_longitude(int 경도) {
            this.경도 = 경도;
        }

    }

