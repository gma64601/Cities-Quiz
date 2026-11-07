package edu.uga.cs.capitalsquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultsFragment extends Fragment {

    private RecyclerView pastResults;
    private TextView emptyMessage;

    public ResultsFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_results,
                container,
                false
        );

        TextView finalScore = view.findViewById(R.id.tv_final_score);
        Button backHome = view.findViewById(R.id.btn_back_home);

        pastResults = view.findViewById(R.id.rv_past_results);
        emptyMessage = view.findViewById(R.id.tv_empty_results);

        RecyclerView recyclerView = pastResults;
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey("score")) {
            int score = bundle.getInt("score");
            finalScore.setText("Your Score: " + score + " / 6");
            finalScore.setVisibility(View.VISIBLE);
        } else {
            finalScore.setVisibility(View.GONE);
        }

        backHome.setOnClickListener(v -> {
            FragmentManager fragmentManager =
                    requireActivity().getSupportFragmentManager();

            fragmentManager.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        loadPastResults();

        return view;
    }

    private void loadPastResults() {

        new AsyncTask<Void, List<Quiz>>() {

            @Override
            protected List<Quiz> doInBackground(Void... params) {
                CapitalsData data =
                        new CapitalsData(requireContext());

                data.open();

                List<Quiz> quizzes = data.retrieveAllQuizzes();

                data.close();

                return quizzes;
            }

            @Override
            protected void onPostExecute(List<Quiz> quizzes) {

                if (!isAdded()) {
                    return;
                }

                if (quizzes == null || quizzes.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    pastResults.setVisibility(View.GONE);
                } else {
                    emptyMessage.setVisibility(View.GONE);
                    pastResults.setVisibility(View.VISIBLE);

                    pastResults.setAdapter(
                            new QuizResultsAdapter(quizzes));
                }
            }

        }.execute();
    }
}