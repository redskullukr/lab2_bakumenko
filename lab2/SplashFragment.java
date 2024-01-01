package stu.cn.ua.lab1_bogdan_bakumenko;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_fragment, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MainFragment())
                        .commit();
            }
        }, 2000);

        return view;
    }
}