package com.example.getsumfoot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.getsumfoot.data.OrderData;

import java.util.ArrayList;

//todo firebase 연결해서 필요한 정보 뿌리기
public class CustomerOrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<OrderData> list = new ArrayList<>();


//    // : Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // : Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public CustomerLikesFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CustomerLikesFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CustomerLikesFragment newInstance(String param1, String param2) {
//        CustomerLikesFragment fragment = new CustomerLikesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_customer_likes, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_customer_likes);
        //todo 수정
        list.add(new OrderData("dsd","dsafa","fsdf","fsfs",3, 35000));
        recyclerView.setHasFixedSize(true);
        adapter = new CustomerOrdersAdapter(list, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Log.e("Frag", "CustomerOrdersFragment");
        return rootView;
    }
}