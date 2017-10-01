package com.reaper.rick.raklibrary;

/**
 * Created by Reaper on 10/1/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rak extends SQLiteOpenHelper {

    RakFunction rakFunction = new RakFunction();

    Object currentObject;

    private  Context context;

    // All Static variables
    // Database Version
    private String primaryKey = " ";
    private String tableName = "";
    boolean newTable = true;


    public Rak(Context context, String tableName, Object o, String... primaryKey) {
        super(context, tableName, null, 1);
        this.currentObject = o;
        if(primaryKey.length>0){
            this.primaryKey = primaryKey[0];
        }
        this.tableName = tableName;
        this.context = context;
        if(newTable){
            SQLiteDatabase db = this.getWritableDatabase();
            onCreate(db);
        }
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        newTable = false;
        Log.d("Exc", "onCreate()");
        Object o = currentObject;
        List<String> names = new ArrayList<>();
        List<String> types = new ArrayList<>();;
        String type;
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(!name.equalsIgnoreCase("id")){
                Log.d("Exc","insert() name="+name);
            }
            names.add(name);
            type = rakFunction.getType(value);
            if(type.equalsIgnoreCase("String")){
                type="Text";
            }
            types.add(type);
        }
        String queryTable = "";
        for(int i =0;i<names.size();i++){
            if(names.get(i).equalsIgnoreCase(primaryKey)){
                types.set(i,types.get(i) + " PRIMARY KEY");
            }
            if(i!=names.size()-1)
                queryTable = queryTable+names.get(i)+" "+types.get(i)+",";
            else
                queryTable = queryTable+names.get(i)+" "+types.get(i);
        }
        Log.d("Exc", "onCreate() queryTable="+queryTable);
        String QUERY = "CREATE TABLE IF NOT EXISTS " + currentObject.getClass().getSimpleName() + "("
                //CREATE LOOPING FOR THIS
                + queryTable
                + ")";
        db.execSQL(QUERY);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Exc", "onUpgrade()");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + currentObject.getClass().getSimpleName());
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void insert(Object o) {
        SQLiteDatabase db = this.getWritableDatabase();

        String objectName = o.getClass().getSimpleName().toLowerCase();
        Log.d("Exc","insert() name="+objectName);

        ContentValues values = new ContentValues();

        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Log.d("Exc","insert() name="+name);
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Log.d("Exc","insert() value="+value.toString());
            if(!name.equalsIgnoreCase(primaryKey)){
                values.put(name, value.toString()); // Contact Name
            }
        }
        // Inserting Row
        Log.d("Exc","insert() objectName="+objectName);
        Log.d("Exc","insert() values.toString()="+values.toString());
        db.insert(objectName, null, values);
        db.close(); // Closing database connection
    }

    public List<Object> select(String... where) {
        List<Object> objectList = new ArrayList<>();
        // Select All Query
        String  selectQuery;
        if(where.length>0){
            selectQuery = "SELECT  * FROM " + currentObject.getClass().getSimpleName()+" WHERE "+ where[0];
        }else{
            selectQuery = "SELECT  * FROM " + currentObject.getClass().getSimpleName();
        }

        Constructor<?> ctor = null;
        Object o = null;
        try {
            Class<?> clazz = null;
            clazz = Class.forName(currentObject.getClass().getName());
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            //Create new Object
            do {
                for(int i=0;i<cursor.getColumnNames().length;i++) {
                    try {
                        o = ctor.newInstance(new Object[]{});
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    for (Field field : o.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        String name = field.getName();
                        Object value = null;
                        try {
                            value = field.get(o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        String type = rakFunction.getType(value);

                        Field variableName = null;
                        //VARIABLE CONTROL
                        try {
                        /* Get Variable Type from variable name */
                            variableName = o.getClass().getDeclaredField(name);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        // set Variable Value from Unknown Class
                        variableName.setAccessible(true);
                        try {
                            //SET
                            //Find name of variable index in Cursor
                            int indexOfCursor = this.findIndex(cursor,name);
                            //Get the value
                            //Insert to new object
                            if (type.equalsIgnoreCase("Integer")) {
                                variableName.setInt(o, Integer.parseInt(cursor.getString(indexOfCursor)));
                            } else if (type.equalsIgnoreCase("String")) {
                                variableName.set(o, cursor.getString(indexOfCursor));
                            } else {
                                variableName.setBoolean(o, Boolean.parseBoolean(cursor.getString(indexOfCursor)));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//Finish Modify Object
                }
                // Adding contact to list
                objectList.add(o);
            } while (cursor.moveToNext());
        }
        // return contact list
        return objectList;
    }

    // Updating single contact
//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(currentObject.getClass().getSimpleName(), values, KEY_ID + " = ?",
//                new String[]{String.valueOf(contact.getID())});
//    }

//     Deleting single contact
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(currentObject.getClass().getSimpleName(), getPrimary() + " = ?",
                new String[]{Integer.toString(id)});
        db.close();
    }

    public void delete(String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + currentObject.getClass().getSimpleName()+" WHERE "+where);
        db.close();
    }


    // Getting contacts Count
    public int count(Object o) {
        String objectName = o.getClass().getSimpleName();
        String countQuery = "SELECT  * FROM " + objectName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public int clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "DROP TABLE IF EXISTS " + currentObject.getClass().getSimpleName();
        db.execSQL(countQuery);
        onCreate(db);
        return 0;
    }

    //GET PRIMARY NAME
    private String getPrimary(){
        String primary;
        Object o = currentObject;
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if(name.contains("primary")){
                return name;
            }
        }
        return "";
    }

    //RAK DATABASE
    public Object createObject(Object obj, HashMap hm) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //Create object from known Object
        Log.d("Exc ", "RakDB createObject()");
        Class<?> clazz = Class.forName(obj.getClass().getName());
        Constructor<?> ctor = clazz.getConstructor();
        String name = "";
        String[] resVal = {"", ""};
        List<String> names = new ArrayList<>();
        boolean createObject = true;

        //Create Object
        Object o = ctor.newInstance(new Object[]{});
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String type = rakFunction.getType(value);
            Field variableName = null;
            //VARIABLE CONTROL
            try {
                /* Get Variable Type from variable name */
                variableName = o.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            // set Variable Value from Unknown Class
            variableName.setAccessible(true);
            try {
                //@hm : <ModelName-Counter>
                //Value : TypeData-Value
                Log.i("Exc", "RakDB createObject() name=" + name);
                String resData = (String) hm.get(name);
                Log.i("Exc", "RakDB createObject() resData=" + resData);
                resVal = resData.split("-");
                if (resVal[0].equalsIgnoreCase("Integer")) {
                    variableName.setInt(o, Integer.parseInt(resVal[1]));
                } else if (resVal[0].equalsIgnoreCase("String")) {
                    variableName.set(o, resVal[1]);
                } else {
                    variableName.setBoolean(o, Boolean.parseBoolean(resVal[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//Finish Modify Object
        return o;
    }

    private int findIndex(Cursor cursor,String name){
        for(int i = 0;i<cursor.getColumnNames().length;i++){
            if(name.equalsIgnoreCase(cursor.getColumnNames()[i])){
                return i;
            }
        }
        return 0;
    }

}