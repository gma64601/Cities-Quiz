package edu.uga.cs.capitalsquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    private TextView questionNumber;
    private TextView statehoodYear;
    private TextView questionText;

    private RadioGroup answerChoices;
    private RadioButton choice1;
    private RadioButton choice2;
    private RadioButton choice3;

    private int currentQuestion = 0;
    private int score = 0;

    private List<Question> questions;

    private static class Question {
        String state;
        int year;
        String capital;
        List<String> choices;

        Question(String state, int year, String capital,
                 String choice2, String choice3) {

            this.state = state;
            this.year = year;
            this.capital = capital;

            choices = new ArrayList<>(Arrays.asList(
                    capital, choice2, choice3
            ));

            Collections.shuffle(choices);
        }
    }

    public QuizFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_quiz,
                container,
                false
        );

        questionNumber = view.findViewById(R.id.tv_question_number);
        statehoodYear = view.findViewById(R.id.tv_statehood_year);
        questionText = view.findViewById(R.id.tv_question);

        answerChoices = view.findViewById(R.id.rg_answer_choices);

        choice1 = view.findViewById(R.id.rb_choice_1);
        choice2 = view.findViewById(R.id.rb_choice_2);
        choice3 = view.findViewById(R.id.rb_choice_3);

        loadSampleQuestions();
        displayQuestion();

        return view;
    }

    //sample questions until DB is set up permenantly
    private void loadSampleQuestions() {

        questions = new ArrayList<>();

        questions.add(new Question(
                "Georgia", 1788, "Atlanta",
                "Savannah", "Augusta"
        ));

        questions.add(new Question(
                "California", 1850, "Sacramento",
                "Los Angeles", "San Francisco"
        ));

        questions.add(new Question(
                "Florida", 1845, "Tallahassee",
                "Miami", "Orlando"
        ));

        questions.add(new Question(
                "Texas", 1845, "Austin",
                "Dallas", "Houston"
        ));

        questions.add(new Question(
                "New York", 1788, "Albany",
                "New York City", "Buffalo"
        ));

        questions.add(new Question(
                "Ohio", 1803, "Columbus",
                "Cleveland", "Cincinnati"
        ));
    }

    private void displayQuestion() {

        Question question = questions.get(currentQuestion);

        questionNumber.setText(
                "Question " + (currentQuestion + 1) + " of 6"
        );

        statehoodYear.setText(
                "Joined the Union: " + question.year
        );

        questionText.setText(
                "What is the capital of " + question.state + "?"
        );

        choice1.setText(question.choices.get(0));
        choice2.setText(question.choices.get(1));
        choice3.setText(question.choices.get(2));

        answerChoices.clearCheck();
    }

    public void submitAnswer() {

        // Make sure the fragment is still active.
        if (!isAdded() || answerChoices == null) {
            return;
        }

        // Check whether an answer was selected.
        int selectedId = answerChoices.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select an answer first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Question question = questions.get(currentQuestion);

        RadioButton selectedButton =
                requireView().findViewById(selectedId);

        String selectedAnswer =
                selectedButton.getText().toString();

        // Check the selected answer.
        if (selectedAnswer.equals(question.capital)) {
            score++;
        }

        currentQuestion++;

        // Display the next question or show the results.
        if (currentQuestion < questions.size()) {
            displayQuestion();
        } else {
            showResults();
        }
    }

    private void showResults() {

        Bundle bundle = new Bundle();
        bundle.putInt("score", score);

        ResultsFragment resultsFragment = new ResultsFragment();
        resultsFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        resultsFragment
                )
                .commit();
    }
}