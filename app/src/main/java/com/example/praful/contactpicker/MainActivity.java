package com.example.praful.contactpicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 123;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private TextView textViewName;
    private TextView textViewNumber;
    private Button pickContact;
    private ImageView userPic;
    private EditText message;
    private Button send;
    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewName = (TextView) findViewById(R.id.tv_name);
        textViewNumber = (TextView) findViewById(R.id.tv_number);
        userPic = (ImageView) findViewById(R.id.iv_userPic);

        message = (EditText) findViewById(R.id.et_Message);
        send = (Button) findViewById(R.id.btn_sendMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }


    public void pickContact(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_PICK_CONTACT && resultCode == RESULT_OK) {

            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;

            }
        } else {
            Log.e("MainActivity:", "Failed to pick contact");
            Toast.makeText(this, "Failed to pick contact! \nCheck Permissions Setting.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     *
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            phoneNo = null;
            String name = null;
            String img = "";
            Bitmap uPic = null;
            //getData() method will have the Content Uri of selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            //column index of contact number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            //column idex of contact name
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String image_uri = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);

            textViewName.setText(name);
            textViewNumber.setText(phoneNo);

            if (image_uri != null) {
                userPic.setImageURI(Uri.parse(image_uri));
            } else {
                Toast.makeText(this, "Seems like your selected \ncontact dosen't have picture", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error accessing contacts", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message.getText().toString(), null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}
