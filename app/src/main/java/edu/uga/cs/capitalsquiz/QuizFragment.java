package edu.uga.cs.capitalsquiz;

import android.content.Context;
import android.content.SharedPreferences;
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
    private boolean quizCompleted = false;

    private List<Question> questions = new ArrayList<>();

    private String[] restoredChoices = null;

    private static final String PREFS_NAME = "active_quiz";
    private static final String KEY_QUIZ_ID = "quiz_id";
    private static final String KEY_QUESTION_IDS = "question_ids";
    private static final String KEY_CURRENT_QUESTION = "current_question";
    private static final String KEY_SCORE = "score";
    private static final String KEY_CHOICE_1 = "choice_1";
    private static final String KEY_CHOICE_2 = "choice_2";
    private static final String KEY_CHOICE_3 = "choice_3";
    private static final String KEY_SELECTED_ANSWER = "selected_answer";

    private static class QuizLoadResult {
        List<Question> questions;
        long quizId;
        int currentQuestion;
        int score;
        String[] choices;
        String selectedAnswer;
        boolean success;

        QuizLoadResult(List<Question> questions, long quizId, int currentQuestion, int score, String[] choices, String selectedAnswer, boolean success) {
            this.questions = questions;
            this.quizId = quizId;
            this.currentQuestion = currentQuestion;
            this.score = score;
            this.choices = choices;
            this.selectedAnswer = selectedAnswer;
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

    @Override
    public void onPause() {
        saveQuizProgress();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (questions != null && !questions.isEmpty()) {
            restoreSelectedAnswer();
        }
    }

    private void loadQuestionsFromDatabase() {
        Context appContext = requireContext().getApplicationContext();

        new AsyncTask<Void, QuizLoadResult>() {
            @Override
            protected QuizLoadResult doInBackground(Void... params) {
                CapitalsData data = new CapitalsData(appContext);

                try {
                    data.open();
                    List<Question> allQuestions = data.retrieveAllQuestions();

                    if (allQuestions == null || allQuestions.size() < 6) {
                        return new QuizLoadResult(null, -1, 0, 0, null, null, false);
                    }

                    SharedPreferences preferences =
                            appContext.getSharedPreferences(
                                    PREFS_NAME, Context.MODE_PRIVATE);

                    long savedQuizId =
                            preferences.getLong(KEY_QUIZ_ID, -1);

                    String savedQuestionIds =
                            preferences.getString(KEY_QUESTION_IDS, "");

                    if (savedQuizId != -1 && !savedQuestionIds.isEmpty()) {
                        String[] ids = savedQuestionIds.split(",");
                        List<Question> restoredQuestions = new ArrayList<>();

                        for (String id : ids) {
                            for (Question question : allQuestions) {
                                if (String.valueOf(question.getId()).equals(id)) {
                                    restoredQuestions.add(question);
                                    break;
                                }
                            }
                        }

                        if (restoredQuestions.size() == 6) {
                            int savedCurrentQuestion =
                                    preferences.getInt(KEY_CURRENT_QUESTION, 0);

                            int savedScore =
                                    preferences.getInt(KEY_SCORE, 0);

                            String[] savedChoices = {
                                    preferences.getString(KEY_CHOICE_1, ""),
                                    preferences.getString(KEY_CHOICE_2, ""),
                                    preferences.getString(KEY_CHOICE_3, "")
                            };

                            String selectedAnswer =
                                    preferences.getString(KEY_SELECTED_ANSWER, "");

                            if (savedCurrentQuestion >= 0
                                    && savedCurrentQuestion < 6) {
                                return new QuizLoadResult(
                                        restoredQuestions,
                                        savedQuizId,
                                        savedCurrentQuestion,
                                        savedScore,
                                        savedChoices,
                                        selectedAnswer,
                                        true);
                            }
                        }
                    }

                    Collections.shuffle(allQuestions);
                    List<Question> selectedQuestions = new ArrayList<>(allQuestions.subList(0, 6));

                    long newQuizId = data.storeQuiz();

                    if (newQuizId == -1) {
                        return new QuizLoadResult(null, -1, 0, 0, null, null, false);
                    }

                    return new QuizLoadResult(selectedQuestions, newQuizId, 0, 0, null, null, true);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new QuizLoadResult(null, -1, 0, 0, null, null, false);
                } finally {
                    data.close();
                }
            }

            @Override
            protected void onPostExecute(QuizLoadResult result) {
                if (!isAdded()) return;

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
                currentQuestion = result.currentQuestion;
                score = result.score;
                restoredChoices = result.choices;

                displayQuestion();

                if (result.selectedAnswer != null
                        && !result.selectedAnswer.isEmpty()) {
                    selectSavedAnswer(result.selectedAnswer);
                }

                saveQuizProgress();
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

        if (restoredChoices != null
                && restoredChoices.length == 3
                && !restoredChoices[0].isEmpty()
                && !restoredChoices[1].isEmpty()
                && !restoredChoices[2].isEmpty()) {

            choices.add(restoredChoices[0]);
            choices.add(restoredChoices[1]);
            choices.add(restoredChoices[2]);

        } else {
            choices.add(question.getCapitalCity());
            choices.add(question.getSecondCity());
            choices.add(question.getThirdCity());

            Collections.shuffle(choices);
        }

        restoredChoices = null;

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

        saveQuizProgress();
    }

    public void submitAnswer() {
        if (!isAdded() || answerChoices == null ||
                questions == null || questions.isEmpty() ||
                submitting || quizCompleted || currentQuestion >= questions.size()) {
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

        saveQuizProgress();

        int updatedScore = score + (correct ? 1 : 0);
        int updatedAnswered = currentQuestion + 1;

        Context appContext = requireContext().getApplicationContext();

        new AsyncTask<Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                CapitalsData data = new CapitalsData(appContext);

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
                if (!isAdded()) return;

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
                    quizCompleted = true;
                    clearSavedQuiz();
                    showResults();
                }
            }
        }.execute();
    }

    private void saveQuizProgress() {
        if (quizCompleted || quizId == -1
                || questions == null || questions.isEmpty()) {
            return;
        }

        if (getContext() == null) {
            return;
        }

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        PREFS_NAME, Context.MODE_PRIVATE);

        StringBuilder questionIds = new StringBuilder();

        for (Question question : questions) {
            if (questionIds.length() > 0) {
                questionIds.append(",");
            }

            questionIds.append(question.getId());
        }

        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(KEY_QUIZ_ID, quizId);
        editor.putString(KEY_QUESTION_IDS, questionIds.toString());
        editor.putInt(KEY_CURRENT_QUESTION, currentQuestion);
        editor.putInt(KEY_SCORE, score);

        if (answerChoices != null && choice1 != null
                && choice2 != null && choice3 != null) {

            editor.putString(KEY_CHOICE_1, choice1.getText().toString());
            editor.putString(KEY_CHOICE_2, choice2.getText().toString());
            editor.putString(KEY_CHOICE_3, choice3.getText().toString());

            int selectedId = answerChoices.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedButton =
                        requireView().findViewById(selectedId);

                editor.putString(
                        KEY_SELECTED_ANSWER,
                        selectedButton.getText().toString());
            } else {
                editor.putString(KEY_SELECTED_ANSWER, "");
            }
        }

        editor.apply();
    }

    private void restoreSelectedAnswer() {
        if (getContext() == null || answerChoices == null) {
            return;
        }

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        PREFS_NAME, Context.MODE_PRIVATE);

        String selectedAnswer =
                preferences.getString(KEY_SELECTED_ANSWER, "");

        if (!selectedAnswer.isEmpty()) {
            selectSavedAnswer(selectedAnswer);
        }
    }

    private void selectSavedAnswer(String selectedAnswer) {
        if (answerChoices == null) {
            return;
        }

        if (choice1.getText().toString().equals(selectedAnswer)) {
            choice1.setChecked(true);
        } else if (choice2.getText().toString().equals(selectedAnswer)) {
            choice2.setChecked(true);
        } else if (choice3.getText().toString().equals(selectedAnswer)) {
            choice3.setChecked(true);
        }
    }

    private void clearSavedQuiz() {
        if (getContext() == null) {
            return;
        }

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        PREFS_NAME, Context.MODE_PRIVATE);

        preferences.edit().clear().apply();
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