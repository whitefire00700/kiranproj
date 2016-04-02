package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.hibs.GPSRoute.Database.SqliteContacts;
import com.hibs.GPSRoute.Pojo.Contacts;
import com.hibs.GPSRoute.R;
import com.hibs.GPSRoute.Utils.Utility;

import java.util.ArrayList;

public class Activity_ViewContacts extends Activity {

    Button btnView, btnViewAll;
    EditText fname;
    SqliteContacts db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_register);
        fname = (EditText) findViewById(R.id.efname);
        btnView = (Button) findViewById(R.id.btnView);
        btnViewAll = (Button) findViewById(R.id.btnViewAll);
        db = new SqliteContacts(Activity_ViewContacts.this);

        btnView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = fname.getText().toString().trim();

                if (!db.checkContactExistsByName(name)) {
                    Utility.alertBox(Activity_ViewContacts.this, "Error", "No records found");
                    return;
                }

                Contacts contacts = db.getContactByName(name);
                StringBuffer buffer = new StringBuffer();
                buffer.append("Name: " + contacts.getfName() + "\n");
                buffer.append("LName: " + contacts.getlName() + "\n");
                buffer.append("Area: " + contacts.getArea() + "\n");
                buffer.append("Address: " + contacts.getAddress() + "\n");
                buffer.append("Mobile " + contacts.getMobile() + "\n");


                Utility.alertBox(Activity_ViewContacts.this, "Contact details", buffer.toString());
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (db.getTotalContactsCount() == 0) {
                    Utility.alertBox(Activity_ViewContacts.this, "Error","No records found");
                    return;
                }

                ArrayList<Contacts> contactsList = db.getAllContacts();
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < contactsList.size(); i++) {
                    Contacts contacts = contactsList.get(i);
                    buffer.append("Name: " + contacts.getfName() + "\n");
                    buffer.append("LName: " + contacts.getlName() + "\n");
                    buffer.append("Area: " + contacts.getArea() + "\n");
                    buffer.append("Address: " + contacts.getAddress() + "\n");
                    buffer.append("Mobile " + contacts.getMobile() + "\n");

                }
                Utility.alertBox(Activity_ViewContacts.this, "Contact details", buffer.toString());

            }
        });
    }


}
