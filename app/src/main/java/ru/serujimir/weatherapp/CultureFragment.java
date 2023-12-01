package ru.serujimir.weatherapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CultureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CultureFragment extends Fragment {
    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CultureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Culture.
     */
    // TODO: Rename and change types and number of parameters
    public static CultureFragment newInstance(String param1, String param2) {
        CultureFragment fragment = new CultureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_culture, container, false);

        init();

        return view;
    }
    public void init(){
        ArrayList<CultureItem> cultureItemArrayList = new ArrayList<>();

        RecyclerView rvCulture;
        rvCulture = view.findViewById(R.id.rvCulture);
        rvCulture.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        CultureAdapter cultureAdapter = new CultureAdapter(getContext(), cultureItemArrayList);
        rvCulture.setAdapter(cultureAdapter);

        cultureItemArrayList.add(new CultureItem(getString(R.string.primeta_title_3), getString(R.string.primeta3), getResources().getDrawable(R.drawable.primeta3)));
        cultureItemArrayList.add(new CultureItem(getString(R.string.primeta_title_1), getString(R.string.primeta1), getResources().getDrawable(R.drawable.primeta1)));
        cultureItemArrayList.add(new CultureItem(getString(R.string.primeta_title_2), getString(R.string.primeta2), getResources().getDrawable(R.drawable.primeta2)));

    }

}