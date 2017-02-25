package com.example.purwagaikwad.emerge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    /*private RadioGroup radioGroup;
    private RadioButton sound;
    private RadioButton manual, auto;*/
    private Uri contact;
    private String contactName; //name of emergency contact
    private String contactNumber, contactOption; //contact number of emergency contact
    private int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final String TAG = MainActivity.class.getSimpleName();

    //references to input / interactive fields
    private RadioGroup rgContactOptions, rgCallOption;
    private RadioButton rdoTempSearch, rdoTempManual, rdoTempCall, rdoTempSMS;
    private EditText txtName, txtNumber;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /************************Working code *********************************
         radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
         sound = (RadioButton) findViewById(R.id.auto);
         radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

        @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        // find which radio button is selected
        if(checkedId == sound.getId()) {
        Toast.makeText(getApplicationContext(), "choice: Auto",
        Toast.LENGTH_SHORT).show();

        Intent sendIntent = new Intent();
        sendIntent.setClassName("com.example.androidgetuserlocation",
        "com.example.androidgetuserlocation.MainActivity");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "+61470204966");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);


        } else {
        Toast.makeText(getApplicationContext(), "choice: Manual",
        Toast.LENGTH_SHORT).show();
        }
        }

        });
         *************************************************************************/
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        txtName = (EditText) findViewById(R.id.txtName);
        txtNumber = (EditText) findViewById(R.id.txtNumber);

        rgContactOptions = (RadioGroup) findViewById(R.id.rgContacts);
        rdoTempSearch = (RadioButton) findViewById(R.id.rdoSearch);
        rdoTempManual = (RadioButton) findViewById(R.id.rdoManual);
        rgContactOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == rdoTempSearch.getId()) {
                    retrieveContactsOnClick(); //opens contacts list
                } else {
                    if (checkedId == rdoTempManual.getId()) {
                        txtName.requestFocus();
                    }
                }
            }
        });

        rgCallOption = (RadioGroup) findViewById(R.id.rgPanic);
        rdoTempSMS = (RadioButton) findViewById(R.id.rdoSMS);
        rdoTempCall = (RadioButton) findViewById(R.id.rdoCall);
        rgCallOption.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == rdoTempSMS.getId()) {
                    contactOption = "sms";
                } else {
                    if (checkedId == rdoTempCall.getId()) {
                        contactOption = "call";
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setClassName("com.example.androidgetuserlocation",
                            "com.example.androidgetuserlocation.MainActivity");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "+61470204966");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    //saveDetails();
                } catch (Exception e) {

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cancelDetails();
                } catch (Exception e) {
                }
            }
        });

    }

    //retrieving contact details --------- ******* --------- ******** -----
    //method to retrieve existing contacts
    private void retrieveContactsOnClick() {
        Log.d(TAG, "retrieveContactsOnClick");
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    //result of contact search
    @Override
    protected void onActivityResult(int requestCode, int requestResultCode, Intent resultData) {
        Log.d(TAG, "activity result");
        super.onActivityResult(requestCode, requestResultCode, resultData);
        //checking if a matching result was found
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && requestResultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + resultData.toString());
            contact = resultData.getData();
            retrieveContactNumber();
            retrieveContactName();
        }
    }

    //retrieving the relevant phone number
    private void retrieveContactNumber() {
        Cursor searchNumber = getContentResolver().query(contact, new String[]{ContactsContract.Contacts._ID}, null, null, null, null);
        String tempContactNumber = null;
        Log.d(TAG, "trying to retrieve unique contactID");
        //retrieving unique contact ID
        if (searchNumber.moveToFirst()) {
            tempContactNumber = searchNumber.getString(searchNumber.getColumnIndex(ContactsContract.Contacts._ID));
        }
        searchNumber.close();
        Log.d(TAG, "ContactID: " + tempContactNumber);

        //retrieving phone number
        Cursor phoneNumber = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, new String[]{tempContactNumber}, null);
        Log.d(TAG, "trying to retrieve phone number");
        if (phoneNumber.moveToFirst()) {
            contactNumber = phoneNumber.getString(phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phoneNumber.close();
        Log.d(TAG, "Phone Number: " + contactNumber);
        txtNumber.setText(contactNumber);
    }

    //retrieving the relevant contact name
    private void retrieveContactName() {
        String contactName = null;
        // querying contact data store
        Cursor cursor = getContentResolver().query(contact, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        Log.d(TAG, "Contact Name: " + contactName);
        txtName.setText(contactName);
    }

    //method to clear details
    private void cancelDetails() {
        txtName.setText("");
        txtNumber.setText("");
        Log.d(TAG, "clear details");
    }
}
