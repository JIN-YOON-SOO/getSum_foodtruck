## 몇 가지..
- BaseActivity : 메뉴바(햄버거바)가 붙어있는 액티비티. 이 액티비티 안에서 Fragment 전환으로 화면 전환이 이루어짐<br>
->판매자/소비자별로 잘 실행됨!(Devicemap빼고.. )<br>
- BaseActivity로 실행하면 기본값: 소비자 메뉴 이므로 판매자용 메뉴를 보고싶으면 BaseActivity에서 is_seller = true 로 바꿔서 테스트.
- layout 폴더에 nav, app_bar 붙은 것들 햄버거바에서 쓰는 레이아웃임<br>

<br><br>
### Fragment 사용법 중요한거
- 거의 같은데 oncreate메소드에서 구현할 것들을 oncreateview에서 한다고 생각하면 됨<br>
- this->getActivity()로 바꿔서 써야함<br>
- oncreateview에서 inflater로 view 받아서 view.findviewbyId 이렇게 해야함. 작업된 코드 참고<br>
- Fragment->Activity 전환 : intent에 this 대신 getActivity사용<br>
- Activity->Fragment 전환 : intent 대신 replaceFragment 메소드 사용해야함. MypageSellerModifyActivity에 작성된 코드 탐고 / data 폴더 안에 ReplaceFragment.java 참고
