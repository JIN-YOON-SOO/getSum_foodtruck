package com.example.getsumfoot.data;

import org.jetbrains.annotations.NotNull;

public class EventMapData {

    public double 위도,경도;
    public String 소재지도로명주소;

    EventMapData(){}

    public EventMapData(double 위도, double 경도){
        this.위도 = 위도;
        this.경도 = 경도;
    }

        public double get_latitude(){
            return 위도;
        }

        public void set_latitude(int 위도) {
            this.위도 = 위도;
        }

        public double get_longitude(){
            return 경도;
        }

        public void set_longitude(int 경도) {
            this.경도 = 경도;
        }

        public String get_roadAdress(){ return 소재지도로명주소; }

        public void set_roadAdress(int 경도) {
        this.경도 = 경도;
    }


    }

