package com.android.contactsaveprocess;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ezvcard.VCard;
import ezvcard.io.text.VCardReader;
import ezvcard.property.FormattedName;
import ezvcard.property.Photo;
import ezvcard.property.Telephone;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [12-Feb-2019 at 5:22 PM].
 * Email:
 * Project: ContactVcfOperation.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [12-Feb-2019 at 5:22 PM].
 * --> <Second Editor> on [12-Feb-2019 at 5:22 PM].
 * Reviewed by :
 * --> <First Reviewer> on [12-Feb-2019 at 5:22 PM].
 * --> <Second Reviewer> on [12-Feb-2019 at 5:22 PM].
 * ============================================================================
 **/
public class NativeContactHelper {

    private static NativeContactHelper nativeContactHelper = new NativeContactHelper();
    private final String TAG = "NativeContacts";
    private Context context;

    public static NativeContactHelper getInstance() {
        return nativeContactHelper;
    }

    public void getAppContext(Context context) {
        this.context = context;
    }

    public File prepareContactVcf(Uri contactUri) {

        Cursor cursor = null;

        try {

            cursor = context.getContentResolver().query(contactUri,
                    null, null, null, null);

            if (cursor == null || cursor.getCount() == 0)
                return null;

            String id = "", name = "", number = "", typeMobile = "", typeHome = "", typeHomeFax = "",
                    typeMain = "", typeOther = "", typePager = "", typeWork = "", typeWorkFax = "",
                    imageEncoded = "", emailAddress = "";

            while (cursor.moveToNext()) {

                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String imageUri = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                if (!TextUtils.isEmpty(imageUri)) {
                    imageEncoded = convertUriToBase64(context, imageUri);
                }

                Cursor emailCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    emailAddress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                emailCur.close();


                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    //the below cursor will give you details for multiple contacts
                    Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the                                                                                                                                                                                                                            contact list
                    while (pCursor.moveToNext()) {

                        int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        number = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //you will get all phone numbers according to it's type as below switch case.
                        //Logs.e will print the phone number along with the name in DDMS.
                        // you can use these details where ever you want.

                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                Log.v(TAG, "TYPE_MOBILE: " + number);
                                typeMobile = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                Log.v(TAG, "TYPE_HOME: " + number);
                                typeHome = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                Log.v(TAG, "TYPE_WORK: " + number);
                                typeWork = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                Log.v(TAG, "TYPE_MAIN: " + number);
                                typeMain = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                Log.v(TAG, "TYPE_FAX_WORK: " + number);
                                typeWorkFax = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                                Log.v(TAG, "TYPE_FAX_HOME: " + number);
                                typeHomeFax = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                                Log.v(TAG, "TYPE_PAGER: " + number);
                                typePager = number;
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                Log.v(TAG, "TYPE_OTHER: " + number);
                                typeOther = number;
                                break;

                            default:
                                break;
                        }
                    }
                    pCursor.close();
                }
            }

            String filePath = generateVcfAndGetFilePath();

            File vcfFile = new File(filePath);

            FileWriter fw = new FileWriter(vcfFile);
            fw.write("BEGIN:VCARD\r\n");
            fw.write("VERSION:2.1\r\n");
            fw.write("N:" + name + "\r\n");
            fw.write("FN:" + name + "\r\n");
            fw.write("TEL;CELL:" + typeMobile + "\r\n");
            if (!typeHome.equals("") && typeHome != null) {
                fw.write("TEL;TYPE=HOME,VOICE:" + typeHome + "\r\n");
            }
            if (!TextUtils.isEmpty(imageEncoded)) {
                /*fw.write("PHOTO;TYPE=JPEG;ENCODING=BASE64:" + imageEncoded + "\r\n");*/
                fw.write("PHOTO;ENCODING=B;TYPE=JPEG:" + imageEncoded + "\r\n");
            }
            if (!typeWork.equals("") && typeWork != null) {
                fw.write("TEL;TYPE=WORK,VOICE:" + typeWork + "\r\n");
            }
            if (!typeMain.equals("") && typeMain != null) {
                fw.write("TEL;TYPE=MAIN,VOICE:" + typeMain + "\r\n");
            }
            if (!typeWorkFax.equals("") && typeWorkFax != null) {
                fw.write("TEL;TYPE=TYPE_FAX_WORK,VOICE:" + typeWorkFax + "\r\n");
            }
            if (!typeHomeFax.equals("") && typeHomeFax != null) {
                fw.write("TEL;TYPE=TYPE_FAX_HOME,VOICE:" + typeHomeFax + "\r\n");
            }
            if (!typePager.equals("") && typePager != null) {
                fw.write("TEL;TYPE=TYPE_PAGER,VOICE:" + typePager + "\r\n");
            }
            if (!typeOther.equals("") && typeOther != null) {
                fw.write("TEL;TYPE=TYPE_OTHER,VOICE:" + typeOther + "\r\n");
            }
            if (!emailAddress.equals("") && emailAddress != null) {
                fw.write("EMAIL;TYPE=PREF,INTERNET:" + emailAddress + "\r\n");
            }
            fw.write("END:VCARD" + "\r\n");
            fw.close();

            return vcfFile;

        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public String generateVcfAndGetFilePath() {

        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = new File(root + "/Contacts");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        long timestramp = System.currentTimeMillis();
        String fname = "contacts" + timestramp + ".vcf";

        return myDir.getAbsolutePath() + "/" + fname;
    }

    private String convertUriToBase64(Context context, String photoUri) {
        InputStream imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(Uri.parse(photoUri));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();
        // line break has to be removed, so it is on the same line as PHOTO
        return Base64.encodeToString(bitmapData, Base64.DEFAULT).replaceAll("\n", "");
    }

    public Bitmap getVcfBitmap(String vcfUrl) {
        File file;
        VCardReader reader = null;
        Bitmap bitmap = null;
        if (vcfUrl != null) {
            try {
                file = new File(vcfUrl);
                reader = new VCardReader(file);
                VCard vcard;

                while ((vcard = reader.readNext()) != null) {
                    List<Photo> photos = vcard.getPhotos();
                    Log.v("MIMO_SAHA:", "Photos: " + photos.size());

                    if (photos.size() > 0) {
                        Photo photo = photos.get(0);
                        byte[] photoByte = photo.getData();
                        bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                        Log.v("MIMO_SAHA:", "Photos: " + photo.getUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    public UserContact getUserInfo(String vcfUrl) {
        File file;
        VCardReader reader = null;
        UserContact userContact = null;
        if (vcfUrl != null) {
            try {
                file = new File(vcfUrl);
                reader = new VCardReader(file);
                VCard vcard;

                while ((vcard = reader.readNext()) != null) {
                    FormattedName formattedName = vcard.getFormattedName();
                    List<Photo> photos = vcard.getPhotos();

                    Bitmap bitmap = null;

                    if (photos.size() > 0) {
                        Photo photo = photos.get(0);
                        byte[] photoByte = photo.getData();
                        bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                        Log.v("MIMO_SAHA:", "Photos: " + photo.getUrl());
                    }

                    userContact = new UserContact()
                            .setName((formattedName == null) ? null : formattedName.getValue())
                            .setImageEncoded(bitmap)
                            .setContactEmail(vcard.getEmails().get(0).getValue());

                    int contacts = 0;

                    for (Telephone telephone : vcard.getTelephoneNumbers()) {
                        String number = telephone.getText();

                        if (!TextUtils.isEmpty(number)) {

                            String type = telephone.getTypes().get(0).getValue();
                            contacts++;

                            if (type.equalsIgnoreCase("CELL")) {
                                userContact.setTypeMobile(number);
                            } else if (type.equalsIgnoreCase("HOME,VOICE")) {
                                userContact.setTypeHome(number);
                            } else if (type.equalsIgnoreCase("WORK,VOICE")) {
                                userContact.setTypeWork(number);
                            } else if (type.equalsIgnoreCase("MAIN,VOICE")) {
                                userContact.setTypeMain(number);
                            } else if (type.equalsIgnoreCase("TYPE_FAX_WORK,VOICE")) {
                                userContact.setTypeWorkFax(number);
                            } else if (type.equalsIgnoreCase("TYPE_FAX_HOME,VOICE")) {
                                userContact.setTypeHomeFax(number);
                            } else if (type.equalsIgnoreCase("TYPE_PAGER,VOICE")) {
                                userContact.setTypePager(number);
                            } else if (type.equalsIgnoreCase("TYPE_OTHER,VOICE")) {
                                userContact.setTypeOther(number);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return userContact;
    }

    public String readVcfName(String vcfUrl) {
        File file;
        VCardReader reader = null;
        String mName = "";
        if (vcfUrl != null) {
            try {
                file = new File(vcfUrl);
                reader = new VCardReader(file);
                VCard vcard;

                while ((vcard = reader.readNext()) != null) {
                    FormattedName fn = vcard.getFormattedName();
                    List<Photo> photos = vcard.getPhotos();
                    mName = (fn == null) ? null : fn.getValue();
                    Log.v("MIMO_SAHA:", "Photos: " + photos.size());

                    if (photos.size() > 0) {
                        Photo photo = photos.get(0);
                        Log.v("MIMO_SAHA:", "Photos: " + photo.getUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mName;
    }

    public String readVcfContact(String vcfUrl) {
        File file;
        VCardReader reader = null;
        String contact = "";
        if (vcfUrl != null) {
            try {
                file = new File(vcfUrl);
                reader = new VCardReader(file);
                VCard vcard;

                while ((vcard = reader.readNext()) != null) {
                    contact = getContactNumber(vcard.getTelephoneNumbers());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return contact;
    }

    private String getContactNumber(List<Telephone> telephones) {
        for (Telephone telephone : telephones) {
            String number = telephone.getText();
            Log.v("MIMO_SAHA:", "Contact Type: " + telephone.getTypes().get(0));
            if (!TextUtils.isEmpty(number)) {
                return number;
            }
        }
        return "";
    }
}
