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

public class ResultsFragment extends Fragment {

    public ResultsFragment() {
        // Required empty public constructor
    }

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

        TextView finalScore = view.findViewById(
                R.id.tv_final_score
        );

        Button backHome = view.findViewById(
                R.id.btn_back_home
        );

        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey("score")) {

            int score = bundle.getInt("score");

            finalScore.setText(
                    "Your Score: " + score + " / 6"
            );

        } else {

            finalScore.setVisibility(View.GONE);
        }

        // return to the home screen
        backHome.setOnClickListener(v -> {

            androidx.fragment.app.FragmentManager fragmentManager =
                    requireActivity().getSupportFragmentManager();

            fragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            );

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        return view;
    }
}