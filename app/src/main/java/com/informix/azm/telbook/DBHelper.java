package com.informix.azm.telbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by adm on 28.01.2016.
 */
public class DBHelper extends SQLiteOpenHelper{
    // Объявляем Таблицы базы
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactDb";
    public static final String TABLE_AREAS = "s_areas";
    public static final String TABLE_DEPART = "s_depart";
    public static final String TABLE_ORG = "s_org";
    public static final String TABLE_OTYPE = "s_otype";
    public static final String TABLE_USERS = "s_users";

    // Объявляем Ключи таблицы s_users
    public static final String KEY_ID = "ID";
    public static final String KEY_FIO = "FIO";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_CONTACTS = "CONTACTS";
    public static final String KEY_PHONE = "PHONE";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_DEPARTID = "DEPARTID";
    public static final String KEY_ORGID = "ORGID";
    public static final String KEY_SORTING = "SORTING";

    // Объявляем Ключи таблицы s_org
    public static final String KEY_COMPANY = "COMPANY";
    public static final String KEY_ADRES = "ADRES";
    public static final String KEY_AREAID = "AREAID";
    public static final String KEY_DESCR = "DESCR";
    public static final String KEY_TYPEID = "TYPEID";

    // Объявляем Ключи таблицы s_areas
    public static final String KEY_SNAME = "S_NAME";

    // Объявляем Ключи таблицы s_depart
    public static final String KEY_DEPARTMENT = "DEPARTMENT";

    // Объявляем Ключи таблицы s_otype
    public static final String KEY_TITLE = "TITLE";

    public static SQLiteDatabase database;

    public static void setDatabase(SQLiteDatabase database) {
        DBHelper.database = database;
    }

    public void setRealArea(int realArea) {
        this.realArea = realArea;
    }

    public int getRealArea() {
        return realArea;
    }

    public int realArea;

    public DBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USERS + "(" + KEY_ID + " integer primary key," + KEY_FIO + " text," + KEY_STATUS + " text," + KEY_CONTACTS + " text," + KEY_PHONE + " text," + KEY_EMAIL + " text," + KEY_DEPARTID + " integer," + KEY_ORGID + " integer," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_ORG + "(" + KEY_ID + " integer primary key," + KEY_COMPANY + " text," + KEY_ADRES + " text," + KEY_AREAID + " integer," + KEY_DESCR + " text," + KEY_TYPEID + " integer)");
        db.execSQL("create table " + TABLE_AREAS + "(" + KEY_ID + " integer primary key," + KEY_SNAME + " text," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_DEPART + "(" + KEY_ID + " integer primary key," + KEY_DEPARTMENT + " text,"+ KEY_ORGID + " integer," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_OTYPE + "(" + KEY_ID + " integer primary key," + KEY_TITLE + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist " + TABLE_USERS);
        db.execSQL("drop table if exist " + TABLE_ORG);
        db.execSQL("drop table if exist " + TABLE_AREAS);
        db.execSQL("drop table if exist " + TABLE_DEPART);
        db.execSQL("drop table if exist " + TABLE_OTYPE);
        onCreate(db);
    }


    public String[][] areaGetter(SQLiteDatabase database){

        String[][] result;
        String querry = "select * from s_areas order by SORTING ASC";
        Cursor cursor = database.rawQuery(querry, null);
        int SNAMEIndex = cursor.getColumnIndex(DBHelper.KEY_SNAME);
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);


        //Вывод результатов
        int i = 0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(SNAMEIndex);
                    result[1][i] = String.valueOf(cursor.getInt(idIndex));
                    i++;


                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[1][1];
            result[0][0] = "г. Якутск";
            result[1][0] = "35";
        }
        cursor.close();
        return result;

    }


    public ArrayList<UserContact> searchByFio(String fioString,SQLiteDatabase database) {

        String querry;
        ArrayList<UserContact> result= new ArrayList<UserContact>();
        UserContact userContact;
        // Строка запроса в sql для ФИО

        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE "+ DBHelper.KEY_FIO +" like ?";
        querry = querry+ " and areaid=" + getRealArea()+" ORDER BY fio asc";


        // Поиск всех вхождений базы данных удовлетворяющих условию в cursor

        //Заменяем первый символ поискана на заглавную букву
        char[] a = fioString.toCharArray();
        if (a.length > 0) {
            a[0] = Character.toUpperCase(a[0]);
            for (int i = 1; i < a.length; i++) {
                if (a[i] == ' ') {
                    if (i!=a.length-1) i++;
                    a[i] = Character.toUpperCase(a[i]);
                } else {
                    a[i] = Character.toLowerCase(a[i]);
                }

            }
        }

        fioString = String.valueOf(a);

        Cursor cursor = database.rawQuery(querry, new String[]{"%" + fioString + "%"});

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);


        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex),cursor.getString(FIOIndex),cursor.getString(STATUSIndex),cursor.getString(PHONEIndex),cursor.getString(CONTACTSIndex),cursor.getString(EMAILIndex),cursor.getInt(DEPARTIDIndex),cursor.getString(ORGIndex),cursor.getInt(ORGIDIDIndex),cursor.getInt(SORTING),cursor.getString(DEPARTMENT),cursor.getString(ADRES));
                    result.add(userContact);


                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            userContact = new UserContact(0,"Ничего не найдено",null,null,null,null,0,null,0,0);
            result.add(userContact);
        }

        cursor.close();

        return result;

    }


    public UserContact searchUserById(Integer id, SQLiteDatabase database) {

        String querry;
        UserContact result= new UserContact(0,"Ничего не найдено",null,null,null,null,0,null,0,0);
        UserContact userContact;
        // Строка запроса в sql для ФИО

        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE s_users."+ DBHelper.KEY_ID +" =" + id;


        // Поиск всех вхождений базы данных удовлетворяющих условию в cursor

        //Заменяем первый символ поискана на заглавную букву

        Cursor cursor = database.rawQuery(querry, null);

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);


        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex),cursor.getString(FIOIndex),cursor.getString(STATUSIndex),cursor.getString(PHONEIndex),cursor.getString(CONTACTSIndex),cursor.getString(EMAILIndex),cursor.getInt(DEPARTIDIndex),cursor.getString(ORGIndex),cursor.getInt(ORGIDIDIndex),cursor.getInt(SORTING),cursor.getString(DEPARTMENT),cursor.getString(ADRES));
                    result=userContact;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        cursor.close();
        return result;

    }



    public OrgContact searchOrgByName(String companyName, SQLiteDatabase database) {

        String querry;
        OrgContact result;
        UserContact userContact;
        ArrayList<UserContact> listuserContacts = new ArrayList<UserContact>();
        ArrayList<String> departs = new ArrayList<String>();
        ArrayList<ArrayList<UserContact>> userByDeparts = new ArrayList<ArrayList<UserContact>>();
        ArrayList<Integer> departsId = new ArrayList<Integer>();
        // Строка запроса в sql для ФИО

        querry = "SELECT department,s_depart.id FROM s_org INNER JOIN s_depart ON s_depart.ORGID=s_org.id where COMPANY == \""+companyName+"\" ORDER BY s_depart.sorting";

        Cursor cursor = database.rawQuery(querry, null);


        int DEPARTIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);


        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    departs.add(cursor.getString(DEPARTIndex));
                    departsId.add(cursor.getInt(DEPARTIDIndex));

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            departs.add("Отдел не указан");
            departsId.add(0);
        }


        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE "+DBHelper.TABLE_ORG+"."+DBHelper.KEY_COMPANY +" == \""+companyName+"\" ORDER BY s_users.sorting";

        cursor = database.rawQuery(querry, null);

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTID = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);


                //Вывод результатов
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            userContact = new UserContact(cursor.getInt(idIndex), cursor.getString(FIOIndex), cursor.getString(STATUSIndex), cursor.getString(PHONEIndex), cursor.getString(CONTACTSIndex), cursor.getString(EMAILIndex), cursor.getInt(DEPARTID), cursor.getString(ORGIndex), cursor.getInt(ORGIDIDIndex), cursor.getInt(SORTING));
                            listuserContacts.add(userContact);

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                } else {
                    userContact = new UserContact(0, "Ничего не найдено", null, null, null, null, 0, null, 0, 0);
                    listuserContacts.add(userContact);
                }

        cursor.close();

        ArrayList<UserContact> userWithoutDepart=new ArrayList<UserContact>();
        for (int i=0;i<departs.size();i++){
            ArrayList<UserContact> userOnDepart=new ArrayList<UserContact>();
            for (UserContact user:listuserContacts){
                if (user.DEPARTID==departsId.get(i)){
                    userOnDepart.add(user);

                }
                if (user.DEPARTID==0){
                    userWithoutDepart.add(user);
                }

            }
            userByDeparts.add(userOnDepart);

        }
        userByDeparts.add(userWithoutDepart);
        result=new OrgContact(userByDeparts,departs);
        return result;

    }


    public String[][] ListOrg(SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM " + DBHelper.TABLE_OTYPE;

        Cursor cursor = database.rawQuery(querry, null);
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(TitleIndex);
                    result[1][i] = cursor.getString(IDIndex);
                        i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[1][1];
            result[0][0] = "Иные";
            result[1][0] = "6";
        }
        cursor.close();
        return result;

    }

    public String[][] ListOrgOnId(String typeId, SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM "+DBHelper.TABLE_ORG +" WHERE "+ DBHelper.KEY_TYPEID +" = "+ typeId+ " ORDER BY s_org.company asc";

        Cursor cursor = database.rawQuery(querry, null);
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(ORGIndex);
                    result[1][i] = cursor.getString(IDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "Иные";
            result[1][0] = "6";
        }
        cursor.close();
        return result;

    }


    public String[][] SearchOrg(String orgName, SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM "+DBHelper.TABLE_ORG +" WHERE "+ DBHelper.TABLE_ORG+"." + DBHelper.KEY_COMPANY + " like ?";

        Cursor cursor = database.rawQuery(querry, new String[]{"%" + orgName + "%"});
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(ORGIndex);
                    result[1][i] = cursor.getString(IDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "Ничего не найденно";
            result[1][0] = "0";
        }
        cursor.close();
        return result;

    }





}
