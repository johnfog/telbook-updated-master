package com.informix.azm.telbook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etFio;
    Spinner spinner;
    DBHelper dbHelper;
    Intent intent;
    ListView searchResult;
    ArrayList<UserContact> userContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        DBHelper.setDatabase(database);


        ImageButton btnAPIP = (ImageButton) findViewById(R.id.btnAPIP);
        Button btnOrg = (Button) findViewById(R.id.btnOrg);
        ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        Button btnFio = (Button) findViewById(R.id.btnFio);
        etFio = (EditText) findViewById(R.id.etFio);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnAPIP.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnOrg.setOnClickListener(this);
        btnFio.setOnClickListener(this);

        final DrawAreas ulus=new DrawAreas(database);
        ulus.DrawArea(spinner);

            etFio.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (etFio.length() > 0)
                            startSearch();
                        return true;
                    }
                    return false;
                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    dbHelper.setRealArea(ulus.areaIdS.get(spinner.getSelectedItemPosition()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    @Override
    public void onClick(View v) {

        intent = new Intent();

        switch (v.getId()) {
            case R.id.btnFio:
                etFio.setText("");
                searchResult.setAdapter(null);
                break;
            case R.id.btnOrg:
                intent.setClass(this, OrgActivity.class);
                startActivity(intent);
                break;
            case R.id.btnUpdate:
                intent = new Intent(this, update_db.class);
                startActivity(intent);
                break;
            case R.id.btnSearch:
                if (etFio.length()>0)
                startSearch();
                break;

        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void startSearch(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        searchResult = (ListView) findViewById(R.id.searchResult);
        userContact = dbHelper.searchByFio(etFio.getText().toString(), database);
        ItemMenuUsers itemMenuUsers = new ItemMenuUsers(userContact);
        itemMenuUsers.DrawMenu(searchResult);
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });
        hideSoftKeyboard(MainActivity.this);
        }

}
