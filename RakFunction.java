package com.reaper.rick.raklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.reaper.rick.raklibrary.RakController;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Reaper on 8/13/2017.
 */

public class RakFunction extends RakController {

    public Activity activity;
    public Context context;

    public RakFunction() {
    }

    // @important!!!
    // YOU SHOULD INIT THIS FIRST !!! IMPORTANT
    public RakFunction(Context context) {
        this.context = context;
    }

    public RakFunction(Activity activity) {
        this.activity = activity;
    }


    private boolean whereFilter(String name, String value, String... where) {
        Log.d("Exc", "whereFilter()");
        String operator;
        String[] splitter;
        if(where!=null){
                for (int i = 0; i < where.length; i++) {//Check di setiap where
                    operator = getOperator(where[i]);
                    splitter = where[i].split(operator);
                    Log.d("Exc", "whereFilter() splitter[0]="+splitter[0]);
                    Log.d("Exc", "whereFilter() name="+name);
                    if(name.equalsIgnoreCase(splitter[0])){
                        Log.d("Exc", "whereFilter()"+value+" "+operator+" "+splitter[1]);
                        if(whereSame(value,operator,splitter[1])){
                            //Jika sama maka biarkan
                        }else {
                            return false;
                        }
                    }
                }
        }
        return true;
    }

    public List<Object> select(Object o, String... where) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Log.i("Exc", "select()");
        Class cls = o.getClass();
        List<Object> objResult = new ArrayList<>();
        //TODO : Get Data from SharedPreferrences
        HashMap dataNew = new HashMap();
        String nameColumn = cls.getSimpleName();
        Log.i("Exc", "select() nameColumn =" + nameColumn);
        String tableValue = loadData(nameColumn);
        String valCol = "dummy";
        String[] listColumn = tableValue.split("-");
        for (int i = 0; i < listColumn.length; i++) {
            Log.i("Exc", "select() listCol =" + listColumn[i]);
        }
        String nameCol;
        int sizeNow = count(cls) + 1;
        Log.i("Exc", "select() sizeNow=" + sizeNow);
        int counter = 1;//Start from id = 1
        for (int i = 0; i < listColumn.length; i++) {//For Every Column
            for (int j = 1; j < sizeNow; j++) {//Get All Column Start from 1
                Log.i("Exc", "select() load=" + nameColumn + "-" + j + "-" + listColumn[i]);
                valCol = loadData(nameColumn + "-" + j + "-" + listColumn[i]);
                Log.i("Exc", "select() valCol=" + valCol);
                dataNew.put(listColumn[i] + "-" + j, valCol);
                Log.i("Exc", "select() HashMap=" + listColumn[i] + "-" + j);
            }
        }
        for (int i = 0; i < listColumn.length; i++) {
            for (int j = 1; j < dataNew.size() / listColumn.length; j++) {
                Log.i("Exc", "select() dataNew.select=" + listColumn[i] + "-" + j);
                Log.i("Exc", "select() dataNew=" + dataNew.get(listColumn[i] + "-" + j));
            }
        }
        objResult = createObject(o, dataNew, where);
        return objResult;
    }

    //SELECT * FROM OBJECT
    public List<Object> select(Object o) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Log.i("Exc", "select()");
        Class cls = o.getClass();
        List<Object> objResult = new ArrayList<>();
        //TODO : Get Data from SharedPreferrences
        HashMap dataNew = new HashMap();
        String nameColumn = cls.getSimpleName();
        Log.i("Exc", "select() nameColumn =" + nameColumn);
        String tableValue = loadData(nameColumn);
        String valCol = "dummy";
        String[] listColumn = tableValue.split("-");
        for (int i = 0; i < listColumn.length; i++) {
            Log.i("Exc", "select() listCol =" + listColumn[i]);
        }
        String nameCol;
        int sizeNow = count(cls) + 1;
        Log.i("Exc", "select() sizeNow=" + sizeNow);
        int counter = 1;//Start from id = 1
        for (int i = 0; i < listColumn.length; i++) {//For Every Column
            for (int j = 1; j < sizeNow; j++) {//Get All Column Start from 1
                Log.i("Exc", "select() load=" + nameColumn + "-" + j + "-" + listColumn[i]);
                valCol = loadData(nameColumn + "-" + j + "-" + listColumn[i]);
                Log.i("Exc", "select() valCol=" + valCol);
                dataNew.put(listColumn[i] + "-" + j, valCol);
                Log.i("Exc", "select() HashMap=" + listColumn[i] + "-" + j);
            }
        }
        for (int i = 0; i < listColumn.length; i++) {
            for (int j = 1; j < dataNew.size() / listColumn.length; j++) {
                Log.i("Exc", "select() dataNew.select=" + listColumn[i] + "-" + j);
                Log.i("Exc", "select() dataNew=" + dataNew.get(listColumn[i] + "-" + j));
            }
        }
        objResult = createObject(o, dataNew);
        return objResult;
    }

    public boolean save(Object o) {
        Log.d("Exc", "save()");
        //Create Data
        String modelName = o.getClass().getSimpleName();
        String data = "";
        String listColumn = "";
        int sizeNow = this.count(o.getClass()) + 1;

        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String nameColumn = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String type = this.getType(value);
            String number = Integer.toString(sizeNow);
            // @ModelName-1-TabelName
            // Value TypeData-Value
            data = modelName + "-" + number + "-" + nameColumn;
            String val;
            //DataConverter
            if (type.equalsIgnoreCase("String")) {
                val = (String) value;
            } else if (type.equalsIgnoreCase("Integer")) {
                val = Integer.toString((Integer) value);
            } else {
                val = Boolean.toString((Boolean) value);
            }

            Log.d("Exc", "save() Data=" + data);
            Log.d("Exc", "save() Value=" + type + "-" + val);

            if (saveData(data, type + "-" + val)) {

            } else {
                break;
            }
            //Value for @ModelName : ColumnName += ...
            if (listColumn.equalsIgnoreCase("")) {//First Data
                listColumn = nameColumn;
            } else {// n-Data become Column1-Column2
                listColumn = listColumn + "-" + nameColumn;
            }
        }
        Log.i("Exc", "save() modelName=" + modelName);
        Log.i("Exc", "save() listColumn=" + listColumn);
        // @ModelName
        // Value : Column1-Column2-Column3-....
        if (saveData(modelName, listColumn)) {
            return true;
        }
        return false;
    }

    public static boolean delete(Object o) {
        return false;
    }

    public static boolean update(Object o) {
        return false;
    }

    public int count(Class cls) {
        HashMap dataNew = new HashMap();
        String nameColumn = cls.getSimpleName();
        String tableValue = loadData(nameColumn);
        if (tableValue.isEmpty()) {
            return 0;
        }
        String valCol = "dummy";
        String[] listColumn = tableValue.split("-");
        int counter = 1;
        while (!valCol.equalsIgnoreCase("")) {
            valCol = loadData(nameColumn + "-" + counter + "-" + listColumn[0]);
            if (valCol.equalsIgnoreCase("")) {//When data not found
                counter--;
                break;
            } else {
                dataNew.put(counter + "-" + listColumn, valCol);
                counter++;
            }
        }
        return counter;
    }

    public boolean drop(Object o) {
        //1.Dapatkan @ModelName beserta @Variable
        //2.Hapus dengan replace "" ke setiap iterasi
        Log.i("Exc", "select()");
        Class cls = o.getClass();
        List<Object> objResult = new ArrayList<>();
        //TODO : Get Data from SharedPreferrences
        HashMap dataNew = new HashMap();
        String nameColumn = cls.getSimpleName();
        Log.i("Exc", "select() nameColumn =" + nameColumn);
        String tableValue = loadData(nameColumn);
        String valCol = "dummy";
        String[] listColumn = tableValue.split("-");
        for (int i = 0; i < listColumn.length; i++) {
            Log.i("Exc", "select() listCol =" + listColumn[i]);
        }
        String nameCol;
        int sizeNow = count(cls) + 1;
        Log.i("Exc", "select() sizeNow=" + sizeNow);
        int counter = 1;//Start from id = 1
        for (int i = 0; i < listColumn.length; i++) {//For Every Column
            for (int j = 1; j < sizeNow; j++) {//Get All Column Start from 1
                saveData(nameColumn + "-" + j + "-" + listColumn[i], "");//DELETE ALL
            }
        }
        return true;
    }

    private boolean saveData(String name, String value) {
        SharedPreferences prefs;
        if (context == null) {
            prefs = activity.getSharedPreferences("RakFunction", 0);
        } else {
            prefs = context.getSharedPreferences("RakFunction", 0);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, value);
        Log.d("Reponse", name + " masuk :" + value);
        if (editor.commit()) {
            return true;
        } else {
            return false;
        }
    }

    private String loadData(String name) {
        SharedPreferences prefs;
        if (context == null) {
            prefs = activity.getSharedPreferences("RakFunction", 0);
        } else {
            prefs = context.getSharedPreferences("RakFunction", 0);
        }
        String data = prefs.getString(name, "");
        Log.d("Reponse", name + " keluar:" + data);
        return data;
    }

    public String getType(Object value) {
        String type = "";
        if (value instanceof String) {
            type = "String";
        } else if (value instanceof Integer) {
            type = "Integer";
        } else if (value instanceof Boolean) {
            type = "Boolean";
        } else {
            type = "unknown";
        }
        return type;
    }

    public List<Object> createObject(Object obj, HashMap hm,String... where) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //Create object from known Object
        Log.d("Exc", "createObject()");
        Class<?> clazz = Class.forName(obj.getClass().getName());
        Constructor<?> ctor = clazz.getConstructor();
        List<Object> objects = new ArrayList<>();
        int sizeNow = this.count(obj.getClass()) + 1;
        String name = "";
        String[] resVal = {"",""};
        List<String> names = new ArrayList<>();
        boolean createObject = true;

        //Create Object
        for (int i = 1; i < sizeNow; i++) {
            Object o = ctor.newInstance(new Object[] {});
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                name = field.getName();
                names.add(name);
                Object value = null;
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                String type = this.getType(value);

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
                    Log.i("Exc", "createObject() name=" + name + "-" + i);
                    String resData = (String) hm.get(name + "-" + i);
                    Log.i("Exc", "createObject() resData=" + resData);
                    resVal = resData.split("-");
                    if (resVal[0].equalsIgnoreCase("Integer")) {
                        variableName.setInt(o, Integer.parseInt(resVal[1]));
                    } else if (resVal[0].equalsIgnoreCase("String")) {
                        variableName.set(o, resVal[1]);
                    } else {
                        variableName.setBoolean(o, Boolean.parseBoolean(resVal[1]));
                    }

                    //FILTERING
                    if(whereFilter(name,resVal[1],where)){
                        createObject = true;
                    }else{
                        createObject = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//Finish Modify Object
            if(where!=null){
                if(createObject){
                    Log.d("Exc", "createObject() creating..."+name+resVal);
                    objects.add(o);//Add to List
                }
            }else{
                objects.add(o);//Add to List
            }

        }
        Log.i("Exc", "objectCreated()");
        return objects;
    }
}
