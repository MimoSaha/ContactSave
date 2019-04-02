package com.android.contactsaveprocess;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int PICK_CONTACT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button save = findViewById(R.id.save);
        Button insert = findViewById(R.id.insert);
        Button update = findViewById(R.id.update);

        NativeContactHelper.getInstance().getAppContext(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactAndSave();
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContact(1);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContact(2);
            }
        });

    }

    private void openContactAndSave() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;

        if (requestCode == PICK_CONTACT) {
            File file = NativeContactHelper.getInstance().prepareContactVcf(data.getData());
            Log.v("MIMO_SAHA::", "File path: " + file.getAbsolutePath());
        }
    }

    private void readContact(int state) {
        String path = "/storage/emulated/0/Contacts/contacts1554208330491.vcf";
        UserContact userContact = NativeContactHelper.getInstance().getUserInfo(path);

        Intent intent = new Intent(state == 1 ? Intent.ACTION_INSERT : Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(state == 1 ? ContactsContract.Contacts.CONTENT_TYPE : ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, userContact.getName());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, userContact.getContactEmail());

        ArrayList<ContentValues> data = new ArrayList<>();

        if (!TextUtils.isEmpty(userContact.getTypeMobile())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeMobile());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeHome())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeHome());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeHomeFax())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeHomeFax());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeMain())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeMain());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeOther())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeOther());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypePager())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypePager());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_PAGER);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeWork())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeWork());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
            data.add(row);
        }

        if (!TextUtils.isEmpty(userContact.getTypeWorkFax())) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userContact.getTypeWorkFax());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
            data.add(row);
        }

        if (userContact.getImageEncoded() != null) {
            ContentValues row = new ContentValues();
            row.put(ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, getBitmapToByte(userContact.getImageEncoded()));
            data.add(row);
        }

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        if (state == 1) {
            startActivityForResult(intent, 100);
        } else {
            startActivity(intent);
        }
    }

    private byte[] getBitmapToByte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();

        return byteArray;
    }
}
