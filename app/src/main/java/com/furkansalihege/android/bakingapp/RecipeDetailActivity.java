package com.furkansalihege.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.furkansalihege.android.bakingapp.models.Recipe;
import com.furkansalihege.android.bakingapp.models.Step;
import com.furkansalihege.baking.baking_app.R;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;

import static com.furkansalihege.android.bakingapp.models.Constants.INTENT_RECIPE_DETAILS;
import static com.furkansalihege.android.bakingapp.models.Constants.INTENT_STEP_DETAILS_INDEX;
import static com.furkansalihege.android.bakingapp.models.Constants.INTENT_STEP_LIST;
import static com.furkansalihege.android.bakingapp.models.Constants.RECIPE_DETAILS_FRAGMENT_ARG;
import static com.furkansalihege.android.bakingapp.models.Constants.STEP_DETAILS_FRAGMENT_ARG;
import static com.furkansalihege.android.bakingapp.models.Constants.STEP_DETAILS_FRAGMENT_FULLSCREEN_ARG;
import static com.furkansalihege.android.bakingapp.models.Constants.STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARG;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailAdapter.onItemClickListener {

    private Recipe recipe;
    private FragmentManager fragmentManager;
    private RecipeDetailFragment recipeDetailFragment;

    private static final String SAVED_STEP_SELECTED_INDEX_KEY = "saved_step_selected_index";
    private static final String SAVED_RECIPE_KEY = "saved_recipe";
    private int stepSelectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            loadDataFromExtras();
            return;
        }
        loadFromSavedInstanceState(savedInstanceState);
    }

    private void loadDataFromExtras() {
        Intent intent = getIntent();
        if (!intent.hasExtra(INTENT_RECIPE_DETAILS)) {
            return;
        }
        Bundle data = intent.getExtras();
        assert data != null;
        recipe = data.getParcelable(INTENT_RECIPE_DETAILS);
        updateActionBar();
        recipeDetailFragment();
        if (isLargeScreen()) {
            stepDetailFragment(stepSelectedIndex);
        }
    }

    private void loadFromSavedInstanceState(Bundle savedInstanceState) {
        recipe = savedInstanceState.getParcelable(SAVED_RECIPE_KEY);
        recipeDetailFragment = (RecipeDetailFragment) fragmentManager.
                findFragmentById(R.id.recipe_details_fragment_container);
        stepSelectedIndex = savedInstanceState.getInt(SAVED_STEP_SELECTED_INDEX_KEY, 0);
        recipeDetailFragment.setSelectionIndex(stepSelectedIndex);
    }

    private void updateActionBar() {
        assert recipe != null;
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(recipe.getName());
    }

    private void recipeDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_DETAILS_FRAGMENT_ARG, recipe);
        recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_details_fragment_container, recipeDetailFragment)
                .commit();
    }

    @Override
    public void onItemClick(int index) {
        if (isLargeScreen()) {
            this.stepSelectedIndex = index;
            stepDetailFragment(index);
            return;
        }
        Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putParcelableArrayListExtra(INTENT_STEP_LIST, new ArrayList<>(recipe.getSteps()));
        intent.putExtra(INTENT_STEP_DETAILS_INDEX, index);
        startActivity(intent);
    }

    private void stepDetailFragment(int index) {
        Step step = recipe.getSteps().get(index);
        recipeDetailFragment.setSelectionIndex(index);
        Bundle args = new Bundle();
        args.putParcelable(STEP_DETAILS_FRAGMENT_ARG, step);
        args.putBoolean(STEP_DETAILS_FRAGMENT_FULLSCREEN_ARG, false);
        args.putLong(STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARG, C.TIME_UNSET);
        final StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.step_details_fragment_container, stepDetailFragment)
                .commit();
    }

    private boolean isLargeScreen() {
        return findViewById(R.id.activity_recipe_detail).getTag() != null &&
                findViewById(R.id.activity_recipe_detail).getTag().equals("sw600");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_RECIPE_KEY, recipe);
        outState.putInt(SAVED_STEP_SELECTED_INDEX_KEY, stepSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
