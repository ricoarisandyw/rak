package com.reaper.rick.raklibrary;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Reaper on 9/27/2017.
 */

public class RakController {

    public String getOperator(String where) {
        String[] splitter;
        String operator = "";
        splitter = where.split("<=");//Pisah dengan <=
        if (splitter.length != 2) {//Jika belum terpisah
            splitter = where.split(">=");//Pisah dengan >=
            if (splitter.length != 2) {//Jika belum terpisah
                splitter = where.split("!=");//Pisah dengan !=
                if (splitter.length != 2) {//Jika belum terpisah
                    splitter = where.split(" not like ");//Pisah dengan =
                    if (splitter.length != 2) {//Jika belum terpisah
                        splitter = where.split(" like ");//Pisah dengan =
                        if (splitter.length != 2) {//Jika belum terpisah
                            splitter = where.split("=");//Pisah dengan =
                            if (splitter.length != 2) {//Jika belum terpisah
                                splitter = where.split("<");//Pisah dengan =
                                if (splitter.length != 2) {//Jika belum terpisah
                                    splitter = where.split(">");//Pisah dengan =
                                    if (splitter.length != 2) {//Jika belum terpisah

                                    } else {
                                        operator = ">";
                                    }
                                } else {
                                    operator = "<";
                                }
                            } else {
                                operator = "=";
                            }
                        } else {
                            operator = " like ";
                        }
                    } else {
                        operator = " not like ";
                    }
                } else {
                    operator = "!=";
                }
            } else {
                operator = ">=";
            }
        } else {
            operator = "<=";
        }
        return operator;
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

    public String getData() {
        return "";
    }

    public ArrayList<String> getVar(Object o) {
        ArrayList<String> var = new ArrayList<>();
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
            var.add(type + "-" + name);
        }
        return var;
    }

    public Object SetClass(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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

            Field variableName = null;
            try {
                variableName = o.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            // this is for private scope
            variableName.setAccessible(true);
            try {
                if (type.equalsIgnoreCase("Integer")) {
                    variableName.set(o, 77);
                } else if (type.equalsIgnoreCase("String")) {
                    variableName.set(o, "Mugi Chan");
                } else {
                    variableName.set(o, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public String vardump(Object o) {
        String data = o.getClass().getSimpleName();
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
            data = data + "\n" + type + " " + name + "=" + value + ";";
        }
        data = data + "}";
        return data;
    }

    public boolean whereSame(String value, String operator, String whereValue){
        int int1,int2;
        boolean bool1,bool2;
        Log.i("Exc", "whereSame()="+value+operator+whereValue);
        switch (operator){
            case " like ":
                if(whereValue.charAt(0)=='%'&&whereValue.charAt(whereValue.length()-1)=='%'){
                    String newVal ="";
                    for(int i=0;i<whereValue.length();i++){
                        if(i==0||i==whereValue.length()-1){
                        }else{
                            newVal += whereValue.charAt(i);
                        }
                    }
                    if(value.contains(newVal)){
                        return true;
                    }else {
                        return false;
                    }
                }else if(whereValue.charAt(0)=='%'){
                    //Check Last Character
                    int j = 0;
                    for (int i =value.length()-whereValue.length()+1;i<value.length();i++){
                        if(value.charAt(i)==whereValue.charAt(j+1)){

                        }else{
                            return false;
                        }
                        j++;
                    }
                    return true;

                }else if(whereValue.charAt(whereValue.length()-1)=='%'){
                    //Check First Character
                    for (int i =0;i<whereValue.length()-1;i++){
                        Log.d("Exc",value.charAt(i)+"<VS>"+whereValue.charAt(i));
                        if(value.charAt(i)==whereValue.charAt(i)){

                        }else{
                            return false;
                        }
                    }
                    return  true;
                }else{
                    if(value.equalsIgnoreCase(whereValue)){
                        return true;
                    }else {
                        return false;
                    }
                }
            case " not like ":
                if(whereValue.charAt(0)=='%'&&whereValue.charAt(whereValue.length()-1)=='%'){
                    String newVal ="";
                    for(int i=0;i<whereValue.length();i++){
                        if(i==0||i==whereValue.length()-1){
                        }else{
                            newVal += whereValue.charAt(i);
                        }
                    }
                    if(value.contains(newVal)){
                        return false;
                    }else {
                        return true;
                    }
                }else if(whereValue.charAt(0)=='%'){
                    //Check Last Character
                    int j = 0;
                    for (int i =value.length()-whereValue.length()+1;i<value.length();i++){
                        if(value.charAt(i)==whereValue.charAt(j+1)){
                            return false;
                        }else{

                        }
                        j++;
                    }
                    return true;

                }else if(whereValue.charAt(whereValue.length()-1)=='%'){
                    //Check First Character
                    for (int i =0;i<whereValue.length()-1;i++){
                        Log.d("Exc", value.charAt(i)+"<VS>"+whereValue.charAt(i));
                        if(value.charAt(i)==whereValue.charAt(i)){

                        }else{
                            return true;
                        }
                    }
                    return  false;
                }else{
                    if(value.equalsIgnoreCase(whereValue)){
                        return false;
                    }else {
                        return true;
                    }
                }
            case "=":
                try {
                    int1 = Integer.parseInt(value);
                    int2 = Integer.parseInt(whereValue);
                    if(int1==int2){return true;}else{return false;}
                }catch (Exception e){

                }
            case "<":
                int1 = Integer.parseInt(value);
                int2 = Integer.parseInt(whereValue);
                if(int1<int2){return true;}else{return false;}
            case ">":
                int1 = Integer.parseInt(value);
                int2 = Integer.parseInt(whereValue);
                if(int1>int2){return true;}else{return false;}
            case "!=":
                int1 = Integer.parseInt(value);
                int2 = Integer.parseInt(whereValue);
                if(int1!=int2){return true;}else{return false;}
            case "<=":
                int1 = Integer.parseInt(value);
                int2 = Integer.parseInt(whereValue);
                if(int1<=int2){return true;}else{return false;}
            case ">=":
                int1 = Integer.parseInt(value);
                int2 = Integer.parseInt(whereValue);
                if(int1>=int2){return true;}else{return false;}
        }
        return false;
    }

    public Object getDefaultObject(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String type = getType(o);

            Field variableName = null;
            try {
                variableName = o.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            // this is for private scope
            variableName.setAccessible(true);
            try {
                if (type.equalsIgnoreCase("Integer")) {
                    variableName.set(o, 999);
                } else if (type.equalsIgnoreCase("String")) {
                    variableName.set(o, "default Var");
                } else {
                    variableName.set(o, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return o;
    }
}
