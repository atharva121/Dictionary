package com.example.android.dictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.IInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;
    SimpleCursorAdapter suggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        myDbHelper = new DatabaseHelper(this);
        if (myDbHelper.checkDataBase()){
            openDataBase();
        }
        else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }
        final String[] from = new String[]{"en_word"};
        final int[] to = new int[]{R.id.suggestion_text};
        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                CursorAdapter ca = searchView.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                searchView.setQuery(clicked_word, false);
                searchView.clearFocus();
                searchView.setFocusable(false);
                Intent intent = new Intent(MainActivity.this, WordMeaning.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word", clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = searchView.getQuery().toString();
                Cursor c = myDbHelper.getMeaning(text);
                if (c.getCount() == 0){
                    searchView.setQuery("", false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word not found!");
                    builder.setMessage("Please search again!");
                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    searchView.clearFocus();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    searchView.clearFocus();
                    searchView.setFocusable(false);
                    Intent intent = new Intent(MainActivity.this, WordMeaning.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setIconifiedByDefault(false);
                Cursor cursorSuggestion = myDbHelper.getSuggestions(newText);
                suggestionAdapter.changeCursor(cursorSuggestion);
                return false;
            }
        });
    }

    protected static void openDataBase() {
        try {
            myDbHelper.openDataBase();
            databaseOpened = true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_exit){
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
