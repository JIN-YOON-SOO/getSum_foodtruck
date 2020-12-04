package com.example.getsumfoot.data;

public class EventData {

  public String 축제명;
  public String 축제시작일자, 축제종료일자;
  public String 홈페이지주소;
  public String 전화번호;
  public String 소재지도로명주소;


  public EventData(){}

    public String getFestival_name() {
        return 축제명;
    }

    public void setFestival_name(String 축제명) {
        this.축제명 = 축제명;
    }

    public String getStart_date() {
        return 축제시작일자;
    }

    public void setStart_date(String 축제시작일자) {
        this.축제시작일자 = 축제시작일자;
    }

    public String getEnd_data() {
        return 축제종료일자;
    }

    public void setEnd_data(String 축제종료일자) {
        this.축제종료일자 = 축제종료일자;
    }

    public String getTelephone() {
        return 전화번호;
    }

    public void setTelephone(String 전화번호) {
        this.전화번호 = 전화번호;
    }

    public String getHomepage() {
        return 홈페이지주소;
    }

    public void setHomepage(String 홈페이지주소) {
        this.홈페이지주소 = 홈페이지주소;
    }

    public String getAddress() {
        return 소재지도로명주소;
    }

    public void setAddress(String 소재지도로명주소) {
        this.소재지도로명주소 = 소재지도로명주소;
    }

}
