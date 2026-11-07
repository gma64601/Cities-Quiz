package edu.uga.cs.capitalsquiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.QuizViewHolder> {

    private final List<Quiz> quizzes;

    public QuizResultsAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_result, parent, false);

        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        Quiz quiz = quizzes.get(position);

        SimpleDateFormat formatter =
                new SimpleDateFormat("MMM d, yyyy 'at' h:mm a",
                        Locale.getDefault());

        String date = formatter.format(new Date(quiz.getDate()));

        holder.quizDate.setText(date);
        holder.quizScore.setText(
                "Score: " + quiz.getResult() + " / " + quiz.getQuestionsAnswered());
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {

        TextView quizDate;
        TextView quizScore;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            quizDate = itemView.findViewById(R.id.tv_quiz_date);
            quizScore = itemView.findViewById(R.id.tv_quiz_score);
        }
    }
}
