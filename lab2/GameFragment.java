package stu.cn.ua.lab1_bogdan_bakumenko;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class GameFragment extends Fragment {
    private TextView textViewPlayerName;
    private TextView textViewGameStatus;
    private GridLayout gridLayoutBoard;
    private Button buttonRestart;
    private Button buttonExit;

    private boolean isPlayerXTurn = true;
    private boolean isGameOver = false;
    private String[][] board = new String[3][3];
    private View view;
    private GameService gameService;
    private boolean isServiceBound = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.game_fragment, container, false);

        String loginName = getActivity().getIntent().getStringExtra("LOGIN_NAME");

        textViewPlayerName = view.findViewById(R.id.textViewPlayerName);
        textViewGameStatus = view.findViewById(R.id.textViewGameStatus);
        gridLayoutBoard = view.findViewById(R.id.gridLayoutBoard);
        buttonRestart = view.findViewById(R.id.buttonRestart);
        buttonExit = view.findViewById(R.id.buttonExit);

        textViewPlayerName.setText("Ім'я гравця: " + loginName);

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        showSymbolSelectionDialog();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Підключення до служби при старті фрагмента
        Intent intent = new Intent(getActivity(), GameService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Відключення від служби при зупинці фрагмента
        if (isServiceBound) {
            getActivity().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    // Створення ServiceConnection для взаємодії з службою
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            GameService.GameBinder gameBinder = (GameService.GameBinder) binder;
            gameService = gameBinder.getService();
            isServiceBound = true;

            // Викликати методи служби або реєстрація слухача, якщо потрібно
            gameService.startGameWithComputerX();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String cellId = "cell_" + i + j;
                int resourceId = getResources().getIdentifier(cellId, "id", getContext().getPackageName());
                Button cell = view.findViewById(resourceId);
                cell.setOnClickListener(new CellClickListener(i, j));
            }
        }

        updateGameStatus();

        // Добавьте логику для первого хода компьютера здесь
        if (!isPlayerXTurn) {
            computerMove();
        }
    }

    private void updateGameStatus() {
        String player = isPlayerXTurn ? "X" : "O";
        textViewGameStatus.setText("Хід гравця: " + player);
    }

    private void showSymbolSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // або getContext()
        builder.setTitle("Выберите символ")
                .setItems(new String[]{"X", "O"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Игрок выбрал X
                            isPlayerXTurn = true;
                            initializeBoard();
                        } else {
                            // Игрок выбрал O, начать игру с компьютером, где компьютер начинает первым
                            initializeBoard();
                            computerMove();
                        }
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private class CellClickListener implements View.OnClickListener {
        private int row;
        private int col;

        CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (!isGameOver && board[row][col] == null) {
                String player = isPlayerXTurn ? "X" : "O";
                board[row][col] = player;
                Button cell = (Button) v;
                cell.setText(player);

                if (checkWin(row, col, player)) {
                    showGameOverDialog(player + " переміг!");
                } else if (isBoardFull()) {
                    showGameOverDialog("Нічия!");
                } else {
                    isPlayerXTurn = !isPlayerXTurn;
                    updateGameStatus();

                    // Після ходу гравця, дайте хід комп'ютеру
                    computerMove();
                }
            }
        }
    }

    private boolean checkWin(int row, int col, String player) {
        // Перевірка горизонтальних, вертикальних і діагональних ліній для переможця
        return (board[row][0] == player && board[row][1] == player && board[row][2] == player) || // Перевірка рядків
                (board[0][col] == player && board[1][col] == player && board[2][col] == player) || // Перевірка стовпців
                (row == col && board[0][0] == player && board[1][1] == player && board[2][2] == player) || // Перевірка головної діагоналі
                (row + col == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player); // Перевірка побічної діагоналі
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showGameOverDialog(String message) {
        isGameOver = true;
        textViewGameStatus.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity()); // Використовуйте requireActivity()
        builder.setMessage(message)
                .setPositiveButton("Заново", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                })
                .setNegativeButton("Вийти", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish(); // Закрийте активність
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void restartGame() {
        // Очистка игрового поля и перезапуск игры
        clearBoard();

        // Показать диалог выбора символа снова
        showSymbolSelectionDialog();
    }

    private void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = null;
                String cellId = "cell_" + i + j;
                int resourceId = getResources().getIdentifier(cellId, "id", getContext().getPackageName());
                Button cell = view.findViewById(resourceId);
                cell.setText("");
            }
        }
        isPlayerXTurn = true;
        isGameOver = false;
        updateGameStatus();
    }


    private boolean isComputerTurn = false;
    private void computerMove() {
        if (!isGameOver) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    makeComputerMove();
                }
            }, 1000); // Задержка в 1 секунду (1000 миллисекунд)
        }
    }

    private void makeComputerMove() {
        if (!isGameOver) {
            Random random = new Random();
            int row, col;

            do {
                row = random.nextInt(3);
                col = random.nextInt(3);
            } while (board[row][col] != null);

            // Измените "X" на "O" для компьютера, если игрок выбрал "X"
            String player = isPlayerXTurn ? "X" : "O";

            board[row][col] = player;
            String cellId = "cell_" + row + col;
            int resourceId = getResources().getIdentifier(cellId, "id", getContext().getPackageName());
            Button cell = view.findViewById(resourceId);
            cell.setText(player);

            if (checkWin(row, col, player)) {
                showGameOverDialog(player + " переміг!");
            } else if (isBoardFull()) {
                showGameOverDialog("Нічия!");
            } else {
                isPlayerXTurn = !isPlayerXTurn;
                updateGameStatus();
            }
        }
    }

    private void startGameWithComputerX() {
        isPlayerXTurn = true; // Компьютер начинает первым
        initializeBoard();
        computerMove(); // Ход компьютера
    }
}