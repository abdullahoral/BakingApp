package com.furkansalihege.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.furkansalihege.baking.baking_app.R;

import java.util.ArrayList;

import static com.furkansalihege.android.bakingapp.models.Constants.WIDGET_INGREDIENT;
import static com.furkansalihege.android.bakingapp.models.Constants.WIDGET_QUANTITY;


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final ArrayList<String> ingredientList;
    private final ArrayList<String> quantityList;

    ListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        ingredientList = intent.getStringArrayListExtra(WIDGET_INGREDIENT);
        quantityList = intent.getStringArrayListExtra(WIDGET_QUANTITY);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient);
        views.setTextViewText(R.id.text_ingredient_name, ingredientList.get(i));
        views.setTextViewText(R.id.text_ingredient_quantity, quantityList.get(i));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
