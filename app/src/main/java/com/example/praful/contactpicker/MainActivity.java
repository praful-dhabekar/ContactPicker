package com.example.praful.contactpicker;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";

    private PendingIntent sentPI, deliveredPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewName = (TextView) findViewById(R.id.tv_name);
        textViewNumber = (TextView) findViewById(R.id.tv_number);
        userPic = (ImageView) findViewById(R.id.iv_userPic);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

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
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message.getText().toString(), sentPI, deliveredPI);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }
}
