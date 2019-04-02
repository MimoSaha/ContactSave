package com.android.contactsaveprocess;

import android.graphics.Bitmap;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [02-Apr-2019 at 5:27 PM].
 * Email:
 * Project: ContactSaveProcess.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [02-Apr-2019 at 5:27 PM].
 * --> <Second Editor> on [02-Apr-2019 at 5:27 PM].
 * Reviewed by :
 * --> <First Reviewer> on [02-Apr-2019 at 5:27 PM].
 * --> <Second Reviewer> on [02-Apr-2019 at 5:27 PM].
 * ============================================================================
 **/
public class UserContact {

    private String name = "";
    private String typeMobile = "";
    private String typeHome = "";
    private String typeHomeFax = "";
    private String typeMain = "";
    private String typeOther = "";
    private String typePager = "";
    private String typeWork = "";
    private String typeWorkFax = "";
    private String contactEmail = "";
    private Bitmap imageEncoded;
    private int numberOfContacts;

    public int getNumberOfContacts() {
        return numberOfContacts;
    }

    public UserContact setNumberOfContacts(int numberOfContacts) {
        this.numberOfContacts = numberOfContacts;
        return this;
    }

    public UserContact setName(String name) {
        this.name = name;
        return this;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public UserContact setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public UserContact setTypeMobile(String typeMobile) {
        this.typeMobile = typeMobile;
        return this;
    }

    public UserContact setTypeHome(String typeHome) {
        this.typeHome = typeHome;
        return this;
    }

    public UserContact setTypeHomeFax(String typeHomeFax) {
        this.typeHomeFax = typeHomeFax;
        return this;
    }

    public UserContact setTypeMain(String typeMain) {
        this.typeMain = typeMain;
        return this;
    }

    public UserContact setTypeOther(String typeOther) {
        this.typeOther = typeOther;
        return this;
    }

    public UserContact setTypePager(String typePager) {
        this.typePager = typePager;
        return this;
    }

    public UserContact setTypeWork(String typeWork) {
        this.typeWork = typeWork;
        return this;
    }

    public UserContact setTypeWorkFax(String typeWorkFax) {
        this.typeWorkFax = typeWorkFax;
        return this;
    }

    public UserContact setImageEncoded(Bitmap imageEncoded) {
        this.imageEncoded = imageEncoded;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getTypeMobile() {
        return typeMobile;
    }

    public String getTypeHome() {
        return typeHome;
    }

    public String getTypeHomeFax() {
        return typeHomeFax;
    }

    public String getTypeMain() {
        return typeMain;
    }

    public String getTypeOther() {
        return typeOther;
    }

    public String getTypePager() {
        return typePager;
    }

    public String getTypeWork() {
        return typeWork;
    }

    public String getTypeWorkFax() {
        return typeWorkFax;
    }

    public Bitmap getImageEncoded() {
        return imageEncoded;
    }
}
