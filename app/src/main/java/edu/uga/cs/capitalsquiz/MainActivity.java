package edu.uga.cs.capitalsquiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    private static final int SWIPE_THRESHOLD = 100;
                    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {

                        float diffX = e2.getX() - e1.getX();
                        float diffY = e2.getY() - e1.getY();

                        if (Math.abs(diffX) > Math.abs(diffY)
                                && Math.abs(diffX) > SWIPE_THRESHOLD
                                && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                            if (diffX < 0) {
                                QuizFragment fragment = (QuizFragment)
                                        getSupportFragmentManager()
                                                .findFragmentById(R.id.fragment_container);

                                if (fragment != null) {
                                    fragment.submitAnswer();
                                }
                                return true;
                            }
                        }

                        return false;
                    }
                });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database questions from CSV
        CapitalsData capitalsData = new CapitalsData(this);

        Log.d("CSV_INIT", "Calling CSV initialization");

        capitalsData.initializeQuestionsFromCSV(
                this,
                new CapitalsData.InitializationListener() {
                    @Override
                    public void onInitializationComplete(boolean success) {

                        if (success) {
                            Log.d("CSV_INIT", "CSV initialization succeeded!");

                            // Verify that questions were inserted
                            CapitalsData data = new CapitalsData(MainActivity.this);
                            data.open();

                            List<Question> questions =
                                    data.retrieveAllQuestions();

                            Log.d("CSV_INIT",
                                    "Total questions: " + questions.size());

                            data.close();

                        } else {
                            Log.e("CSV_INIT", "CSV initialization failed!");
                        }
                    }
                }
        );

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
}