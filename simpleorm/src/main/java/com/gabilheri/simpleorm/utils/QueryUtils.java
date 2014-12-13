package com.gabilheri.simpleorm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gabilheri.simpleorm.OrmObject;
import com.gabilheri.simpleorm.annotations.OrmField;
import com.gabilheri.simpleorm.annotations.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 12/6/14.
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static String getColType(Class<?> col) {

        if ((col.equals(Boolean.class)) ||
                (col.equals(Boolean.TYPE)) ||
                (col.equals(Integer.class)) ||
                (col.equals(Integer.TYPE)) ||
                (col.equals(Long.class)) ||
                (col.equals(Long.TYPE)))  {
            return "INTEGER";
        }

        // SQlite v3 conventions
        if ((col.equals(Double.class)) || (col.equals(Double.TYPE)) || (col.equals(Float.class)) ||
                (col.equals(Float.TYPE))) {
            return "REAL";
        }

        // Save Date as text.
        if ((col.equals(String.class)) || (col.equals(Character.TYPE)) ||
                (col.equals(java.util.Date.class)) ||
                (col.equals(java.sql.Date.class)) ||
                (col.equals(java.util.Calendar.class))) {
            return "TEXT";
        }

        return "";
    }

    public static long save(Class<?> ormClass, OrmObject object, SQLiteDatabase db, Context context) {
        Table table = ormClass.getAnnotation(Table.class);

        HashMap<String, Object> saveValues = new HashMap<>();
        HashMap<String, Object> buildValues = QueryUtils.buildMap(ormClass, object);

        for(Map.Entry<String, Object> entry : buildValues.entrySet()) {
            String key = entry.getKey();
            Object value =  entry.getValue();

            if(value instanceof OrmObject) {
                Log.i(LOG_TAG, "Instance of: " + value.getClass().getSimpleName());
                long _id = save(value.getClass(), (OrmObject) value, db, context);
                saveValues.put(key, _id);
            } else {
                saveValues.put(key, value);
            }

            //Log.d(LOG_TAG, "Key:"+key+", values:" + (String)(value == null ? null : value.toString()));
        }

        return db.insert(table.name(), null, QueryUtils.getValuesFromMap(saveValues, context));
    }

    public static HashMap<String, Object> buildMap(Class<?> ormClass, OrmObject obj) {
        HashMap<String, Object> map = new HashMap<>();

        Field[] fields = ormClass.getDeclaredFields();

        for(Field f : fields) {
            f.setAccessible(true);
            if(f.isAnnotationPresent(OrmField.class)) {
                OrmField ormF = f.getAnnotation(OrmField.class);
                try {
                    Log.d(LOG_TAG, "Key: " + ormF.name() + ", Value: " + f.get(obj));
                    map.put(ormF.name(), f.get(obj));
                } catch (IllegalAccessException ex) {
                    Log.d(LOG_TAG, ex.getMessage());
                }
            }
        }

        return map;
    }

    public static ContentValues getValuesFromMap(HashMap<String, Object> map, Context context) {

        ContentValues values = new ContentValues(map.size());

        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if(entry.getValue() instanceof Long) {
                values.put(key, (Long) entry.getValue());
            } else if(entry.getValue() instanceof Integer) {
                values.put(key, (Integer) entry.getValue());
            } else if(entry.getValue() instanceof String) {
                values.put(key, (String) entry.getValue());
            } else if(entry.getValue() instanceof Float) {
                values.put(key, (Float) entry.getValue());
            } else if(entry.getValue() instanceof Double) {
                values.put(key, (Double) entry.getValue());
            } else if(entry.getValue() instanceof java.sql.Date || entry.getValue() instanceof Date) {
                values.put(key, DateUtils.getLongDate((Date) entry.getValue(), context));
            }
        }
        return values;
    }

    public static <T> List<T> getAll(Class<T> obj, SQLiteDatabase db) {
        List<T> objects = new ArrayList<>();

        Table table = null;

        if(obj.isAnnotationPresent(Table.class)) {
            table = obj.getAnnotation(Table.class);
        } else {
            return null;
        }

        String query = "SELECT * FROM " + table.name();

        Cursor cursor = db.rawQuery(query, null);

        Field[] fields = obj.getDeclaredFields();

        if(cursor.moveToFirst()) {
            do {
                try {
                    OrmObject<T> tORM = new OrmObject<>(obj);

                    T ormObj = tORM.build();

                    for (Field f : fields) {
                        FieldUtils.setFieldFromCursor(cursor, f, ormObj, db);
                    }

                    objects.add(ormObj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        return objects;
    }

    public static OrmObject findById(Class<?> obj, SQLiteDatabase db, long id) {

        Table table = obj.getAnnotation(Table.class);

        String query  = "SELECT  * FROM " + table + " WHERE _ID = " + id;

        Cursor c = db.rawQuery(query, null);

        if(c != null) c.moveToFirst();

        OrmObject inst = new OrmObject();
        Field[] fields = obj.getDeclaredFields();

        for(Field f : fields) {
            FieldUtils.setFieldFromCursor(c, f, inst, db);
        }

        return inst;
    }
}
