// LoginActivity.java
package stu.cn.ua.lab1_bogdan_bakumenko;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    private EditText editTextLogin;
    private EditText editTextPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        editTextLogin = view.findViewById(R.id.editTextLogin);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        return view;
    }

    public void login(View view) {
        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();

        if (isValidLogin(login) && isValidPassword(password)) {
            // Вхід пройшов успішно, тепер відображаємо GameFragment
            GameFragment gameFragment = new GameFragment();
            Bundle bundle = new Bundle();
            bundle.putString("LOGIN_NAME", login);
            gameFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, gameFragment)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Невірний логін або пароль", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidLogin(String login) {
        return login.equals("admin");
    }

    private boolean isValidPassword(String password) {
        return password.equals("admin");
    }
}