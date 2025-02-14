package group1.mini_project_racing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer; // Âm thanh click
    private MediaPlayer bgMediaPlayer; // Nhạc nền
    private MediaPlayer waitingMediaPlayer; // Nhạc chờ
    private SeekBar racing1, racing2, racing3;
    private EditText betRacing1, betRacing2, betRacing3;
    private TextView playerMoneyText;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private boolean raceRunning = false;
    private double playerMoney = 1000;

    // Phát nhạc chờ
    private void playWaitingMusic() {
        if (waitingMediaPlayer == null) {
            waitingMediaPlayer = MediaPlayer.create(this, R.raw.background); // Thay đổi tên file nhạc chờ
            waitingMediaPlayer.setLooping(true); // Lặp lại nhạc chờ
        }
        if (!waitingMediaPlayer.isPlaying()) {
            waitingMediaPlayer.start();
        }
    }

    // Dừng nhạc chờ
    private void stopWaitingMusic() {
        if (waitingMediaPlayer != null && waitingMediaPlayer.isPlaying()) {
            waitingMediaPlayer.stop();
            waitingMediaPlayer.release();
            waitingMediaPlayer = null;
        }
    }

    // Phát nhạc nền khi bắt đầu đua
    private void playBackgroundMusic() {
        if (bgMediaPlayer == null) {
            bgMediaPlayer = MediaPlayer.create(this, R.raw.swimming);
            bgMediaPlayer.setLooping(true); // Lặp lại nhạc
        }
        if (!bgMediaPlayer.isPlaying()) {
            bgMediaPlayer.start();
        }
    }

    // Dừng nhạc nền
    private void stopBackgroundMusic() {
        if (bgMediaPlayer != null && bgMediaPlayer.isPlaying()) {
            bgMediaPlayer.stop();
            bgMediaPlayer.release();
            bgMediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeViews();
        setButtonListeners();
        updateMoneyDisplay();
        applyWindowInsets();

        // Phát nhạc chờ ngay khi ứng dụng khởi động
        playWaitingMusic();
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        stopWaitingMusic(); // Dừng nhạc chờ khi hủy hoạt động
        stopBackgroundMusic(); // Dừng nhạc nền khi hủy hoạt động
        super.onDestroy();
    }

    private void initializeViews() {
        racing1 = findViewById(R.id.racing_1);
        racing2 = findViewById(R.id.racing_2);
        racing3 = findViewById(R.id.racing_3);
        betRacing1 = findViewById(R.id.bet_racing_1);
        betRacing2 = findViewById(R.id.bet_racing_2);
        betRacing3 = findViewById(R.id.bet_racing_3);
        playerMoneyText = findViewById(R.id.player_money);
    }

    private void setButtonListeners() {
        Button startRaceButton = findViewById(R.id.btn_start_race);
        Button resetButton = findViewById(R.id.btn_reset);

        // Khởi tạo âm thanh click
        mediaPlayer = MediaPlayer.create(this, R.raw.click);

        // Nút bắt đầu đua
        startRaceButton.setOnClickListener(v -> {
            playClickSound();
            if (!raceRunning) {
                stopWaitingMusic(); // Dừng nhạc chờ trước khi bắt đầu đua
                handler.postDelayed(this::startRace, 0); // Bắt đầu đua ngay lập tức
            }
        });

        // Nút reset
        resetButton.setOnClickListener(v -> {
            playClickSound(); // Thêm âm thanh khi bấm nút reset
            reset();
        });
    }

    private void playClickSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(this, R.raw.click);
            }
            mediaPlayer.start();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startRace() {
        raceRunning = true;
        playBackgroundMusic(); // Bắt đầu nhạc nền
        resetRaceProgress();

        double totalBet = getTotalBetAmount();
        if (totalBet > playerMoney) {
            showToast("Không đủ tiền để cược!");
            raceRunning = false;
            stopBackgroundMusic(); // Dừng nhạc nếu không đủ tiền
            return;
        }

        playerMoney -= totalBet;
        playerMoney = Math.round(playerMoney * 100.0) / 100.0;
        updateMoneyDisplay();
        handler.postDelayed(raceRunnable, 100);
    }

    private void resetRaceProgress() {
        racing1.setProgress(0);
        racing2.setProgress(0);
        racing3.setProgress(0);
    }

    private double getTotalBetAmount() {
        return getBetAmount(betRacing1) + getBetAmount(betRacing2) + getBetAmount(betRacing3);
    }

    private double getBetAmount(EditText betInput) {
        String betText = betInput.getText().toString();
        try {
            return betText.isEmpty() ? 0 : Double.parseDouble(betText);
        } catch (NumberFormatException e) {
            showToast("Vui lòng nhập số hợp lệ!");
            return 0;
        }
    }


    private final Runnable raceRunnable = () -> {
        if (!raceRunning) return;
        incrementRaceProgress();
        checkRaceWinner();
    };

    private void incrementRaceProgress() {
        racing1.incrementProgressBy(random.nextInt(2) + 1);
        racing2.incrementProgressBy(random.nextInt(2) + 1);
        racing3.incrementProgressBy(random.nextInt(2) + 1);
    }

    private void checkRaceWinner() {
        if (racing1.getProgress() >= 100) {
            announceWinner(1);
        } else if (racing2.getProgress() >= 100) {
            announceWinner(2);
        } else if (racing3.getProgress() >= 100) {
            announceWinner(3);
        } else {
            handler.postDelayed(raceRunnable, 100);
        }
    }

    private void announceWinner(int winner) {
        raceRunning = false;
        stopBackgroundMusic(); // Dừng nhạc khi đua kết thúc
        double winnings = calculateWinnings(winner);
        playerMoney += winnings;
        playerMoney = Math.round(playerMoney * 100.0) / 100.0;
        updateMoneyDisplay();

        // Chuyển đến màn hình chiến thắng
        Intent intent = new Intent(MainActivity.this, EndActivity.class);
        intent.putExtra("WINNER", winner);
        intent.putExtra("UPDATED_MONEY", playerMoney);
        startActivity(intent);
    }
    private double calculateWinnings(int winner) {
        double winnings = 0;
        if (winner == 1) winnings += getBetAmount(betRacing1) * 2;
        if (winner == 2) winnings += getBetAmount(betRacing2) * 2;
        if (winner == 3) winnings += getBetAmount(betRacing3) * 2;
        return Math.round(winnings * 100.0) / 100.0;
    }

    @SuppressLint("SetTextI18n")
    private void updateMoneyDisplay() {
        playerMoneyText.setText("Số tiền: $" + playerMoney);
    }

    private void reset() {
        raceRunning = false;
        stopBackgroundMusic(); // Dừng nhạc khi reset
        stopWaitingMusic(); // Dừng nhạc chờ khi reset
        playerMoney = 1000;
        updateMoneyDisplay();
        resetRaceProgress();
        clearBetInputs();
    }

    private void clearBetInputs() {
        betRacing1.setText("");
        betRacing2.setText("");
        betRacing3.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}