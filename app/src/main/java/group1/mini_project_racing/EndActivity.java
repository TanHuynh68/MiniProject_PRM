package group1.mini_project_racing;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    private MediaPlayer victoryMediaPlayer; // Nhạc chúc mừng
    private TextView winnerTextView;
    private TextView updatedMoneyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        winnerTextView = findViewById(R.id.winner_text);
        updatedMoneyTextView = findViewById(R.id.updated_money_text);
        Button backButton = findViewById(R.id.back_button);

        // Nhận dữ liệu từ MainActivity
        Intent intent = getIntent();
        int winner = intent.getIntExtra("WINNER", 0);
        double updatedMoney = intent.getDoubleExtra("UPDATED_MONEY", 1000);

        // Hiển thị thông tin người chiến thắng và số tiền cập nhật
        winnerTextView.setText("Đường đua " + winner + " thắng!");
        updatedMoneyTextView.setText("Số tiền của bạn: $" + updatedMoney);

        // Phát nhạc chúc mừng
        playVictoryMusic();

        // Xử lý sự kiện nút quay lại
        backButton.setOnClickListener(v -> {
            stopVictoryMusic(); // Dừng nhạc khi quay lại
            finish(); // Kết thúc Activity
        });
    }

    private void playVictoryMusic() {
        if (victoryMediaPlayer == null) {
            victoryMediaPlayer = MediaPlayer.create(this, R.raw.congratulation); // Thay đổi tên file nhạc chúc mừng
            victoryMediaPlayer.setLooping(false); // Không lặp lại nhạc
            victoryMediaPlayer.start();
        }
    }

    private void stopVictoryMusic() {
        if (victoryMediaPlayer != null) {
            if (victoryMediaPlayer.isPlaying()) {
                victoryMediaPlayer.stop();
            }
            victoryMediaPlayer.release();
            victoryMediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopVictoryMusic(); // Dừng nhạc khi hủy hoạt động
        super.onDestroy();
    }
}