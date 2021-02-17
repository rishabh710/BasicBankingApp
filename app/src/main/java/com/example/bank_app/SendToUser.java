package com.example.bank_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.bank_app.CustomAdapter.CustomeAdapterSendToUser;
import com.example.bank_app.DatabaseHelper.DatabaseHelper;
import com.example.bank_app.Model.Model;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SendToUser extends AppCompatActivity {
    List<Model> modelList_sendtouser = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CustomeAdapterSendToUser adapter;

    String phonenumber, name, currentamount, transferamount, remainingamount;
    String selectuser_phonenumber, selectuser_name, selectuser_balance, date;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/spacemono.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_send_to_user);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy, hh:mm a");
        date = simpleDateFormat.format(calendar.getTime());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            phonenumber = bundle.getString("phonenumber");
            name = bundle.getString("name");
            currentamount = bundle.getString("currentamount");
            transferamount = bundle.getString("transferamount");
            showData(phonenumber);
        }
    }

    private void showData(String phonenumber) {
        modelList_sendtouser.clear();
        Log.d("DEMO",phonenumber);
        Cursor cursor = new DatabaseHelper(this).readselectuserdata(phonenumber);
        while(cursor.moveToNext()){
            String balancefromdb = cursor.getString(2);
            Double balance = Double.parseDouble(balancefromdb);

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setGroupingUsed(true);
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            String price = nf.format(balance);

            Model model = new Model(cursor.getString(0), cursor.getString(1), price);
            modelList_sendtouser.add(model);
        }

        adapter = new CustomeAdapterSendToUser(SendToUser.this, modelList_sendtouser);
        mRecyclerView.setAdapter(adapter);
    }

    public void selectuser(int position) {
        selectuser_phonenumber = modelList_sendtouser.get(position).getPhoneno();
        Cursor cursor = new DatabaseHelper(this).readparticulardata(selectuser_phonenumber);
        while(cursor.moveToNext()) {
            selectuser_name = cursor.getString(1);
            selectuser_balance = cursor.getString(2);
            Double Dselectuser_balance = Double.parseDouble(selectuser_balance);
            Double Dselectuser_transferamount = Double.parseDouble(transferamount);
            Double Dselectuser_remainingamount = Dselectuser_balance + Dselectuser_transferamount;

            new DatabaseHelper(this).insertTransferData(date, name, selectuser_name, transferamount, "Success");
            new DatabaseHelper(this).updateAmount(selectuser_phonenumber, Dselectuser_remainingamount.toString());
            calculateAmount();
            Toast.makeText(this, "Transaction Successful!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SendToUser.this, UserList.class));
            finish();
        }
    }

    private void calculateAmount() {
        Double Dcurrentamount = Double.parseDouble(currentamount);
        Double Dtransferamount = Double.parseDouble(transferamount);
        Double Dremainingamount = Dcurrentamount - Dtransferamount;
        remainingamount = Dremainingamount.toString();
        new DatabaseHelper(this).updateAmount(phonenumber, remainingamount);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder_exitbutton = new AlertDialog.Builder(SendToUser.this);
        builder_exitbutton.setTitle("Do you want to cancel the transaction?").setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DatabaseHelper(SendToUser.this).insertTransferData(date, name, "Not selected", transferamount, "Failed");
                        Toast.makeText(SendToUser.this, "Transaction Cancelled!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SendToUser.this, UserList.class));
                        finish();
                    }
                }).setNegativeButton("No", null);
        AlertDialog alertexit = builder_exitbutton.create();
        alertexit.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Model> newList = new ArrayList<>();
                for(Model model : modelList_sendtouser){
                    String name = model.getName().toLowerCase();
                    if(name.contains(newText)){
                        newList.add(model);
                    }
                }
                adapter.setFilter(newList);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
