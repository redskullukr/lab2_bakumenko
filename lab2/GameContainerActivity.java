package stu.cn.ua.lab1_bogdan_bakumenko;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SplashFragment())
                    .commit();
        }
    }

}