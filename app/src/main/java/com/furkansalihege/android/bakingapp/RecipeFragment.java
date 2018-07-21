package com.furkansalihege.android.bakingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furkansalihege.android.bakingapp.models.Recipe;
import com.furkansalihege.baking.baking_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static com.furkansalihege.android.bakingapp.models.Constants.BASE_URL;
import static com.furkansalihege.android.bakingapp.models.Constants.SAVED_LAYOUT_MANAGER;

public class RecipeFragment extends Fragment {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recyclerView;

    private Bundle savedInstanceState;
    private SimpleIdlingResource idlingResource;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        ButterKnife.bind(this, rootView);
        this.savedInstanceState = savedInstanceState;
        applyConfiguration(rootView);
        return rootView;
    }

    private void applyConfiguration(View rootView) {
        applyIdlingConfiguration();
        RecipeAdapter recipesAdapter = new RecipeAdapter((RecipeActivity) getActivity());
        applyLayoutManager(rootView);
        recyclerView.setAdapter(recipesAdapter);
        fetchRecipeData(recipesAdapter);
    }

    @SuppressLint("VisibleForTests")
    private void applyIdlingConfiguration() {
        RecipeActivity recipeActivity = (RecipeActivity) getActivity();
        idlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        idlingResource.setIdleState(false);
    }

    private void applyLayoutManager(View rootView) {
        if (rootView.getTag() != null && rootView.getTag().equals("sw-600")) {
            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),
                    getResources().getInteger(R.integer.grid_view_landscape_column_number));
            recyclerView.setLayoutManager(mLayoutManager);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }
    }

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder().create();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build();
    }

    public interface APIInterface {
        @GET("baking.json")
        Call<ArrayList<Recipe>> getRecipe();
    }
    private void fetchRecipeData(final RecipeAdapter recipesAdapter) {


        APIInterface apiInterface = getClient().create(APIInterface.class);
        Call<ArrayList<Recipe>> call = apiInterface.getRecipe();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                ArrayList<Recipe> recipeList = response.body();
                recipesAdapter.setRecipeData(recipeList, getContext());
                restoreViewState();
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable throwable) {
                Log.e("http error: ", throwable.getMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void restoreViewState() {
        if (savedInstanceState == null) {
            return;
        }
        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }

}
