package stu.cn.ua.lab1_bogdan_bakumenko;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.Random;

public class GameService extends Service {
    private boolean isPlayerXTurn = true;
    private boolean isGameOver = false;
    private String[][] board = new String[3][3];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startGameWithComputerX();
        return START_STICKY;
    }

    public class GameBinder extends Binder {
        GameService getService() {
            return GameService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startGameWithComputerX() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                makeComputerMove();
            }
        }, 1000);
    }

    private void makeComputerMove() {
        if (!isGameOver) {
            Random random = new Random();
            int row, col;

            do {
                row = random.nextInt(3);
                col = random.nextInt(3);
            } while (board[row][col] != null);

            String player = isPlayerXTurn ? "X" : "O";
            board[row][col] = player;

            // Оновіть інтерфейс або викличте методи GameFragment для оновлення інтерфейсу

            if (checkWin(row, col, player)) {
                // Логіка перемоги
            } else if (isBoardFull()) {
                // Логіка нічиєї
            } else {
                isPlayerXTurn = !isPlayerXTurn;
                startGameWithComputerX(); // Перехід до наступного ходу
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
        // Перевірка, чи всі клітини дошки заповнені
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }
}