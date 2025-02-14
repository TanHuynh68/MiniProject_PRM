
        package group1.mini_project_racing;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    EditText username, password;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mediaPlayer = MediaPlayer.create(this, R.raw.click);

        Button startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            } else {
                username.setText("");
                password.setText("");
                username.requestFocus();
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

