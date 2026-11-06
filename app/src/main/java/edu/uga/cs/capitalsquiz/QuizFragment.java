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

    private long quizId = -1;

    private boolean submitting = false;

    private List<Question> questions = new ArrayList<>();

    private static class QuizLoadResult {
        List<Question> questions;
        long quizId;
        boolean success;

        QuizLoadResult(List<Question> questions, long quizId, boolean success) {
            this.questions = questions;
            this.quizId = quizId;
            this.success = success;
        }
    }

    public QuizFragment() {
        //empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        questionNumber = view.findViewById(R.id.tv_question_number);
        statehoodYear = view.findViewById(R.id.tv_statehood_year);
        questionText = view.findViewById(R.id.tv_question);

        answerChoices = view.findViewById(R.id.rg_answer_choices);

        choice1 = view.findViewById(R.id.rb_choice_1);
        choice2 = view.findViewById(R.id.rb_choice_2);
        choice3 = view.findViewById(R.id.rb_choice_3);

        loadQuestionsFromDatabase();

        return view;
    }

    private void loadQuestionsFromDatabase() {

        new AsyncTask<Void, QuizLoadResult>() {

            @Override
            protected QuizLoadResult doInBackground(Void... params) {

                CapitalsData data = new CapitalsData(requireContext());

                try {
                    data.open();

                    List<Question> allQuestions = data.retrieveAllQuestions();

                    if (allQuestions == null || allQuestions.size() < 6) {
                        return new QuizLoadResult(null, -1, false);
                    }

                    Collections.shuffle(allQuestions);

                    List<Question> selectedQuestions = new ArrayList<>(allQuestions.subList(0, 6));

                    long newQuizId = data.storeQuiz();

                    if (newQuizId == -1) {
                        return new QuizLoadResult(null, -1, false);
                    }

                    return new QuizLoadResult(selectedQuestions, newQuizId, true);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new QuizLoadResult(null, -1, false);

                } finally {
                    data.close();
                }
            }

            @Override
            protected void onPostExecute(QuizLoadResult result) {

                if (!isAdded()) {
                    return;
                }

                if (!result.success) {
                    Toast.makeText(
                            requireContext(),
                            "Unable to load quiz questions.",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                questions = result.questions;
                quizId = result.quizId;

                displayQuestion();
            }

        }.execute();
    }

    private void displayQuestion() {

        if (questions == null ||
                currentQuestion >= questions.size()) {
            return;
        }

        Question question = questions.get(currentQuestion);

        List<String> choices = new ArrayList<>();

        choices.add(question.getCapitalCity());
        choices.add(question.getSecondCity());
        choices.add(question.getThirdCity());

        Collections.shuffle(choices);

        questionNumber.setText(
                "Question " + (currentQuestion + 1) + " of 6"
        );

        statehoodYear.setText(
                "Joined the Union: " + question.getStatehood()
        );

        questionText.setText(
                "What is the capital of " + question.getState() + "?"
        );

        choice1.setText(choices.get(0));
        choice2.setText(choices.get(1));
        choice3.setText(choices.get(2));

        answerChoices.clearCheck();
        answerChoices.setEnabled(true);
    }

    public void submitAnswer() {

        if (!isAdded() || answerChoices == null ||
                questions == null || questions.isEmpty() ||
                submitting) {
            return;
        }

        int selectedId = answerChoices.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select an answer first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RadioButton selectedButton =
                requireView().findViewById(selectedId);

        String selectedAnswer =
                selectedButton.getText().toString();

        Question question = questions.get(currentQuestion);

        boolean correct = selectedAnswer.equals(
                question.getCapitalCity()
        );

        submitting = true;
        answerChoices.setEnabled(false);

        int updatedScore = score + (correct ? 1 : 0);
        int updatedAnswered = currentQuestion + 1;

        new AsyncTask<Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                CapitalsData data = new CapitalsData(requireContext());

                try {
                    data.open();

                    data.storeQuizQuestion(
                            question.getId(),
                            quizId,
                            selectedAnswer
                    );

                    if (updatedAnswered == 6) {
                        data.completeQuiz(
                                quizId,
                                updatedScore,
                                updatedAnswered
                        );
                    } else {
                        data.updateQuiz(
                                quizId,
                                updatedScore,
                                updatedAnswered
                        );
                    }

                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;

                } finally {
                    data.close();
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {

                if (!isAdded()) {
                    return;
                }

                submitting = false;

                if (!success) {
                    answerChoices.setEnabled(true);

                    Toast.makeText(
                            requireContext(),
                            "Unable to save your answer. Please try again.",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                score = updatedScore;
                currentQuestion++;

                if (currentQuestion < questions.size()) {
                    displayQuestion();
                } else {
                    showResults();
                }
            }

        }.execute();
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