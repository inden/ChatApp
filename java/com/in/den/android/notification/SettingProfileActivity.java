package com.in.den.android.notification;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileOutputStream;
import java.io.IOException;

public class SettingProfileActivity extends AppCompatActivity {

    Button imageButton;
    Button okButton;
    Button cancelButton;
    Button imgresetButton;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPass;
    ImageView imageViewICon;

    SharedPreferenceOp sharedPreferenceOp;

    int PICK_IMAGE_REQUEST = 1;
    private Uri iconUri = null;
    boolean bimagechanged = false;
    boolean bdefault = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        imageButton = (Button) findViewById(R.id.btnImg);
        okButton = (Button) findViewById(R.id.btnOk);
        cancelButton = (Button) findViewById(R.id.btnCancel);
        imgresetButton = (Button) findViewById(R.id.btnResetImg);
        imageViewICon = (ImageView) findViewById(R.id.imageViewIcon);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPass = (EditText) findViewById(R.id.editTextPassword);

        sharedPreferenceOp = new SharedPreferenceOp(this);

        editTextName.setText(sharedPreferenceOp.getUserName());
        editTextEmail.setText(sharedPreferenceOp.getEmail());
        editTextPass.setText(sharedPreferenceOp.getPassword());

        if (sharedPreferenceOp.isIconFile() && !sharedPreferenceOp.getRealpath().isEmpty()) {
            bdefault = false;
            Picasso.with(this).load(Uri.parse(sharedPreferenceOp.getRealpath())).resize(50, 50).centerCrop().into(imageViewICon);
        } else {
            bdefault = true;
        }

        imgresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewICon.setImageResource(R.drawable.common_ic_googleplayservices);
                bdefault = true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bdefault = false;
                bimagechanged = true;
                requestProfile();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    Intent intent = new Intent();

                    if (!bdefault) {
                        if (bimagechanged) {
                            intent.setData(iconUri);
                        }
                        intent.putExtra("localicon", true);
                    } else {
                        intent.putExtra("localicon", false);
                    }

                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    private void requestProfile() {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            iconUri = data.getData();

            Picasso.with(this).load(iconUri).resize(50, 50).centerCrop().into(imageViewICon);

            bimagechanged = true;
            bdefault = false;

        }
    }
}
