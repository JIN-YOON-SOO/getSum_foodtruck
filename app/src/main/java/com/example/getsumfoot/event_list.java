package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.net.URL;

public class event_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        StrictMode.enableDefaults();

        TextView status = (TextView)findViewById(R.id.notice_event_1);//파싱된 결과확인
        TextView specific_status = (TextView)findViewById(R.id.notice_specific_event);

        boolean initem = false, infstvlNm = false, inopar = false, infstvlStartDate = false, infstvlEndDate = false;
        boolean infstvlCo = false, inmnnst = false, inauspcInstt = false, insuprtInstt = false, inphoneNumber=false;
        boolean inhomepageUrl = false, inrelateInfo= false, inrdnmadr = false, inlnmadr = false, inlatitude = false;
        boolean inlongitude = false, inreferenceDate = false, ininstt_code = false, ininstt_nm = false;

        String item = null, fstvlNm = null, opar = null, fstvlStartDate = null, fstvlEndDate = null;
        String fstvlCo = null, mnnst = null, auspcInstt = null, suprtInstt = null, phoneNumber=null;
        String homepageUrl = null, relateInfo= null, rdnmadr = null, lnmadr = null, latitude = null;
        String longitude = null, referenceDate = null, instt_code = null, instt_nm = null;

        int page = 0;

        try{
            URL url1 = new URL("http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api?"
                    + "ServiceKey=nY9wOEJzTDCDsVQupAlx47fDDlq4dpiv63jSFYniHq6mYfLz1TDOi3hzlY6aCODNolmw0vLzaqQOvJK7Nq3loQ%3D%3D"
                    + "&pageNo=7&numOfRows=100&type=xml"
            );
            URL url2 = new URL("http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api?"
                    + "ServiceKey=nY9wOEJzTDCDsVQupAlx47fDDlq4dpiv63jSFYniHq6mYfLz1TDOi3hzlY6aCODNolmw0vLzaqQOvJK7Nq3loQ%3D%3D"
                    + "&pageNo=8&numOfRows=100&type=xml"
            );

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url1.openStream(), null);
            parser.setInput(url2.openStream(), null);

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if(parser.getName().equals("fstvlNm")){ //축제명 만나면 내용을 받을수 있게 하자
                            infstvlNm = true;
                        }
                        if(parser.getName().equals("opar")){ //개최장소 만나면 내용을 받을수 있게 하자
                            inopar = true;
                        }
                        if(parser.getName().equals("fstvlStartDate")){ //축제시작일자 만나면 내용을 받을수 있게 하자
                            infstvlStartDate = true;
                        }
                        if(parser.getName().equals("fstvlEndDate")){ //축제종료일자 만나면 내용을 받을수 있게 하자
                            infstvlEndDate = true;
                        }
                        if(parser.getName().equals("fstvlCo")){ //축제내용 만나면 내용을 받을수 있게 하자
                            infstvlCo = true;
                        }
                        if(parser.getName().equals("mnnst")){ //주관기관 만나면 내용을 받을수 있게 하자
                            inmnnst = true;
                        }
                        if(parser.getName().equals("auspcInst")){ //주최기관 만나면 내용을 받을수 있게 하자
                            inauspcInstt = true;
                        }
                        if(parser.getName().equals("suprtInstt")){ //후원기관 만나면 내용을 받을수 있게 하자
                            insuprtInstt = true;
                        }
                        if(parser.getName().equals("phoneNumber")){ //전화번호 만나면 내용을 받을수 있게 하자
                            inphoneNumber = true;
                        }
                        if(parser.getName().equals("homepageUrl")){ //홈페이지주소 만나면 내용을 받을수 있게 하자
                            inhomepageUrl = true;
                        }
                        if(parser.getName().equals("relateInfo")){ //관련정보 만나면 내용을 받을수 있게 하자
                            inrelateInfo = true;
                        }
                        if(parser.getName().equals("rdnmadr")) { //소재지도로명주소 만나면 내용을 받을 수 있게 하자
                            inrdnmadr = true;
                        }
                        if(parser.getName().equals("lnmadr")) { //소재지지번주소 만나면 내용을 받을 수 있게 하자
                            inlnmadr = true;
                        }
                        if(parser.getName().equals("latitude")) { //위도 만나면 내용을 받을 수 있게 하자
                            inlatitude = true;
                        }
                        if(parser.getName().equals("longitude")) { //경도 만나면 내용을 받을 수 있게 하자
                            inlongitude = true;
                        }
                        if(parser.getName().equals("referenceDate")) { //데이터기준일자 만나면 내용을 받을 수 있게 하자
                            inreferenceDate = true;
                        }
                        if(parser.getName().equals("instt_code")) { //제공기관코드 만나면 내용을 받을 수 있게 하자
                            ininstt_code = true;
                        }
                        if(parser.getName().equals("instt_nm")) { //제공기관기관명 만나면 내용을 받을 수 있게 하자
                            ininstt_nm = true;
                        }
                        if(parser.getName().equals("message")){ //message 태그를 만나면 에러 출력
                            status.setText(status.getText()+"Error!");
                        }

                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
                        if(infstvlNm){ //true일 때 태그의 내용을 저장.
                            fstvlNm = parser.getText();
                            infstvlNm = false;
                        }
                        if(inopar){ //true일 때 태그의 내용을 저장.
                            opar = parser.getText();
                            inopar = false;
                        }
                        if(infstvlStartDate){ //true일 때 태그의 내용을 저장.
                            fstvlStartDate = parser.getText();
                            infstvlStartDate = false;
                        }
                        if(infstvlEndDate){ //true일 때 태그의 내용을 저장.
                            fstvlEndDate = parser.getText();
                            infstvlEndDate = false;
                        }
                        if(infstvlCo){ //true일 때 태그의 내용을 저장.
                            fstvlCo = parser.getText();
                            infstvlCo = false;
                        }
                        if(inmnnst){ //true일 때 태그의 내용을 저장.
                            mnnst = parser.getText();
                            inmnnst = false;
                        }
                        if(inauspcInstt ){ //true일 때 태그의 내용을 저장.
                            auspcInstt = parser.getText();
                            inauspcInstt = false;
                        }
                        if(insuprtInstt){ //true일 때 태그의 내용을 저장.
                            suprtInstt = parser.getText();
                            insuprtInstt = false;
                        }
                        if(inphoneNumber){ //true일 때 태그의 내용을 저장.
                            phoneNumber = parser.getText();
                            inphoneNumber = false;
                        }
                        if(inhomepageUrl){ //true일 때 태그의 내용을 저장.
                            homepageUrl = parser.getText();
                            inhomepageUrl = false;
                        }
                        if(inrelateInfo){ //true일 때 태그의 내용을 저장.
                            relateInfo = parser.getText();
                            inrelateInfo = false;
                        }
                        if(inrdnmadr){ //true일 때 태그의 내용을 저장.
                            rdnmadr = parser.getText();
                            inrdnmadr = false;
                        }
                        if(inlnmadr){ //true일 때 태그의 내용을 저장.
                            lnmadr = parser.getText();
                            inlnmadr = false;
                        }
                        if(inlatitude){ //true일 때 태그의 내용을 저장.
                            latitude = parser.getText();
                            inlatitude = false;
                        }
                        if(inlongitude){ //true일 때 태그의 내용을 저장.
                            longitude = parser.getText();
                            inlongitude = false;
                        }
                        if(inreferenceDate){ //true일 때 태그의 내용을 저장.
                            referenceDate = parser.getText();
                            inreferenceDate = false;
                        }
                        if(ininstt_code){ //true일 때 태그의 내용을 저장.
                            instt_code = parser.getText();
                            ininstt_code = false;
                        }
                        if(ininstt_nm){ //true일 때 태그의 내용을 저장.
                            instt_nm = parser.getText();
                            ininstt_nm = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item")){
                            if(fstvlNm.indexOf("코로나") == -1) {
                                if(fstvlStartDate.indexOf("2020") != -1 | fstvlStartDate.indexOf("2021") != -1){
                                        if(rdnmadr.indexOf("서울특별시") != -1) {
                                            status.setText(status.getText() + "# " + fstvlNm + "\n \n");
                                            initem = false;
                                        }
                                }
                            }
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            status.setText("Error!");
        }
    }
}




        

