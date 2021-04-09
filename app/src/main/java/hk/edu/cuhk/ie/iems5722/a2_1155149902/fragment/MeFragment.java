package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;

public class MeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_me, container, false);

        return root;
    }
}