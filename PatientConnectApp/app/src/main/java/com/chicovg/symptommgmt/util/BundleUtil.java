package com.chicovg.symptommgmt.util;

import android.os.Bundle;
import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by victorguthrie on 10/30/14.
 */
public class BundleUtil {

    private static DateFormat datetimeFormat = new SimpleDateFormat(SymptomMgmtApi.DATETIME_FORMAT);

    public static Bundle toBundle(Object object){
        Bundle bundle = new Bundle();

        if(null == object)
            return bundle;

        Class clazz = object.getClass();
        bundle.putString("ClassName", clazz.getName());

        try{
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                String name = field.getName();

                if(field.isAnnotationPresent(BundleField.class) && null != field.get(object)){
                    BundleField bundleField = field.getAnnotation(BundleField.class);
                    switch (bundleField.type()){
                        case STRING: bundle.putString(name, (String)field.get(object));
                            break;
                        case INTEGER: bundle.putInt(name, field.getInt(object));
                            break;
                        case LONG: bundle.putLong(name, field.getLong(object));
                            break;
                        case ENUM: bundle.putString(name, String.valueOf(field.get(object)));
                            break;
                        case DATE: bundle.putString(name, datetimeFormat.format((Date)field.get(object)));
                            break;
                        case DATETIME: bundle.putString(name, datetimeFormat.format((Date)field.get(object)));
                            break;
                        case COLLECTION: Collection collection = (Collection)field.get(object);
                            int i=0;
                            for(Iterator iterator = collection.iterator(); iterator.hasNext();){
                                bundle.putBundle(name+"_"+i, toBundle(iterator.next()));
                                i++;
                            }
                            break;
                        case OBJECT: bundle.putBundle(name, toBundle(field.get(object)));
                            break;
                        case BOOLEAN: bundle.putBoolean(name, field.getBoolean(object));
                            break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    public static Object fromBundle(Bundle bundle){
        Object object = new Object();
        ClassLoader classLoader = BundleUtil.class.getClassLoader();

        if(null==bundle)
            return null;

        try {
            Class clazz = classLoader.loadClass(bundle.getString("ClassName"));
            Constructor constructor = clazz.getConstructor();
            object = constructor.newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                String name = field.getName();

                if(field.isAnnotationPresent(BundleField.class) && (bundle.containsKey(name) || bundle.containsKey(name+"_0"))){
                    BundleField bundleField = field.getAnnotation(BundleField.class);
                    switch (bundleField.type()){
                        case STRING: field.set(object, bundle.getString(name, null));
                            break;
                        case INTEGER: field.set(object, bundle.getInt(name));
                            break;
                        case LONG: field.set(object, bundle.getLong(name));
                            break;
                        case ENUM: field.set(object, Enum.valueOf((Class<Enum>)field.getType(), bundle.getString(name)));
                            break;
                        case DATE: field.set(object, datetimeFormat.parse(bundle.getString(name)));
                            break;
                        case DATETIME: field.set(object, datetimeFormat.parse(bundle.getString(name)));
                            break;
                        case COLLECTION:
                            List list = new ArrayList();
                            int i = 0;
                            while(bundle.containsKey(name+"_"+i)){
                                list.add(fromBundle(bundle.getBundle(name+"_"+i)));
                                i++;
                            }
                            field.set(object, list);
                            break;
                        case OBJECT: field.set(object, fromBundle(bundle.getBundle(name)));
                            break;
                        case BOOLEAN: field.set(object, bundle.getBoolean(name, false));
                            break;
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | ParseException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return object;
    }

}
