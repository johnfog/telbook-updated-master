package com.informix.azm.telbook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class OrgActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleApiClient client;
    boolean onType=true;
    boolean onSearch=false;
    OrgContact org;
    EditText etFio;
    DBHelper dbHelper;
    String[][] list;
    final ArrayList<String> orgTypes = new ArrayList<String>();
    final ArrayList<Integer> orgTypesId = new ArrayList<Integer>();
    ArrayList<String> orgNames;
    ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
    ExpandableListView listView;
    ExpListAdapter adapterForTypes;
    ExpListAdapter adapterForOrgs;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_list);

        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        Button btnOrg = (Button) findViewById(R.id.btnOrg);
        Button btnFio = (Button) findViewById(R.id.btnFio);
        etFio = (EditText) findViewById(R.id.etFio);
        btnSearch.setOnClickListener(this);
        btnOrg.setOnClickListener(this);
        btnFio.setOnClickListener(this);

        ListOrg(database);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (onType) {
                    String clickedOrgName = adapterForTypes.getChildById(groupPosition, childPosition);
                    onType = false;
                    ShowOrgUsers(clickedOrgName);
                } else {
                    Intent intent = new Intent(OrgActivity.this, ContactDetailActivity.class);
                    intent.putExtra("userid", org.GetUserIdOnOrg(groupPosition, childPosition));
                    startActivity(intent);
                }

                return false;
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {


            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (onSearch) {
                    org = dbHelper.searchOrgByName(adapterForOrgs.getGroup(groupPosition).toString(), database);
                    org.DrawOrgContact(listView, getApplicationContext());
                }
                onSearch=false;
                return false;
            }
        });



        etFio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                startSearchOrg();
            }
        });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void ShowOrgUsers(String clickedOrgName) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ExpandableListView searchResult = (ExpandableListView) findViewById(R.id.expandableListView);
        org=dbHelper.searchOrgByName(clickedOrgName, database);
        org.DrawOrgContact(searchResult,getApplicationContext());

    }


    private void ListOrg(SQLiteDatabase database) {

        String[][] orgListByType;
        ArrayList<Integer> orgId = new ArrayList<Integer>();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        final DrawAreas ulus=new DrawAreas(database);
        ulus.DrawArea(spinner);


        try {
            list = dbHelper.ListOrg(database);

            for (int i = 0; i < (list[0].length); i++) {
                orgTypes.add(list[0][i]);
                orgTypesId.add(Integer.parseInt(list[1][i]));
            }


        for (int i=0;i< (orgTypesId.size());i++) {
            orgListByType = dbHelper.ListOrgOnId(String.valueOf(orgTypesId.get(i)),database);
            orgNames= new ArrayList<String>();

            for (int k = 0; k < (orgListByType[0].length); k++) {
                orgNames.add(orgListByType[0][k]);
                orgId.add(Integer.parseInt(orgListByType[1][k]));
            }
            groups.add(orgNames);
        }

        adapterForTypes = new ExpListAdapter(getApplicationContext(), groups,orgTypes);
        listView.setAdapter(adapterForTypes);


         etFio.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    startSearchOrg();
                    hideSoftKeyboard(OrgActivity.this);
                    return actionId == EditorInfo.IME_ACTION_DONE;
                }
            });
        } catch (Exception e) {}
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }



    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btnFio:
                Intent intent = new Intent(OrgActivity.this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.btnOrg:
                groups.clear();
                onType=true;
                orgNames.clear();
                orgTypes.clear();
                orgTypesId.clear();
                ListOrg(database);

                break;
            case R.id.btnSearch:
                startSearchOrg();
                hideSoftKeyboard(OrgActivity.this);
                break;

        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void startSearchOrg() {
        onType=false;
        onSearch=true;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);

            list = dbHelper.SearchOrg(etFio.getText().toString(),database);

        orgTypes.clear();
            for (int i = 0; i < (list[0].length); i++) {
                orgTypes.add(list[0][i]);
                orgTypesId.add(Integer.parseInt(list[1][i]));
            }

        adapterForOrgs = new ExpListAdapter(getApplicationContext(), groups,orgTypes);
        listView.setAdapter(adapterForOrgs);


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OrgActivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.informix.azm.telbook/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OrgActivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.informix.azm.telbook/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}




