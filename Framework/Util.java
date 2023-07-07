package util;

import etu1816.framework.Scope;
import etu1816.framework.ScopeType;
import etu1816.framework.Mapping;
import etu1816.framework.annotation.*;
import etu1816.framework.ModelView;
import etu1816.framework.FileUpload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import jakarta.servlet.http.Part;

public class Util {

    public String getURL(String url) {
        String[] methode = url.split("/");
        String method = methode[methode.length - 1];
        return method;

    }
    public void initObject(Object o) throws IllegalAccessException, ParseException {
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            field.set(o, castPrimaryType("", field.getType()));
            field.setAccessible(false);
        }
    }

    public ArrayList<Class<?>> FindAllClass(String path, String path2) throws Exception {
        ArrayList<Class<?>> list = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()) {
            return list;
        }

        File[] allfile = file.listFiles();
        for (int i = 0; i < allfile.length; i++) {
            if (allfile[i].isDirectory()) {
                ArrayList<Class<?>> list1 = new ArrayList<>();
                list1 = this.FindAllClass(allfile[i].getAbsolutePath(), path2);
                list.addAll(list1);

            } else if (allfile[i].getName().endsWith(".class")) {
                String allpath = allfile[i].getPath();
                allpath = allpath.replace("\\", "/");
                int debutpath2 = allpath.indexOf(path2);
                int debutpathcouper = debutpath2 + path2.length();

                String className = allpath.substring(debutpathcouper);
                className = className.replace(".class", "");
                className = className.replace("/", ".");

                Class<?> classe = Class.forName(className);
                list.add(classe);

            }

        }
        return list;
    }
    public String casse(String input) {
        char[] strrep = input.toCharArray();
        strrep[0] = Character.toUpperCase(strrep[0]);

        return new String(strrep);
    }
    
    public ModelView invokeMethod(HttpServletRequest request, Mapping mapping) throws Exception {
        ArrayList<Class<?>> type = new ArrayList<>();
        ArrayList<Object> value = new ArrayList<>();
        this.setArgValue(request, mapping, type, value);

        Object o = this.setObjectByRequest(request, mapping);

        return (ModelView) o.getClass().getMethod(mapping.getMethod(), type.toArray(Class[]::new)).invoke(o, value.toArray(Object[]::new));
    }

    public Object castPrimaryType(String data, Class<?> type) throws ParseException {
        if(data == null || type == null) return null;
        if(data.equals("")) {
            if(type.equals(Date.class) || type.equals(String.class)) return null;
            else return 0;
        }
        if (type.equals(Date.class)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return type.cast(format.parse(data));
        }else if(type.equals(int.class)) return Integer.parseInt(data);
        else if(type.equals(float.class)) return Float.parseFloat(data);
        else if(type.equals(double.class)) return Double.parseDouble(data);
        else if(type.equals(boolean.class)) return Boolean.getBoolean(data);

        return data;
    }
    public void setArgValue(HttpServletRequest request, Mapping mapping, ArrayList<Class<?>> type, ArrayList<Object> value) throws Exception {
        Method m = this.getMethodByClassName(mapping.getClassName(), mapping.getMethod());

        if(m.isAnnotationPresent(MethodAnnotation.class) && !m.getAnnotation(MethodAnnotation.class).paramName().equals("") ) {
            type.addAll(List.of(m.getParameterTypes()));

            String[] paramName = m.getAnnotation(MethodAnnotation.class).paramName().split(",");

            if(paramName.length != type.size()) throw new Exception("Number of argument exception \n" +
                    "\t" + paramName.length + " declared but " + type.size() + " expected");

            String value_temp;
            for (int i=0; i< paramName.length; i++) {
                value_temp = request.getParameter(paramName[i].trim());
                value.add(this.castPrimaryType(value_temp, type.get(i)));
            }
        }
    }
    public FileUpload getValueUploadedFile(HttpServletRequest request, String field_name) throws ServletException, IOException {
        Part filePart = request.getPart(field_name);
        FileUpload result = new FileUpload();
        result.setName(filePart.getSubmittedFileName());
        result.setFile(filePart.getInputStream().readAllBytes());

        return result;
    }

    public Method getMethodByClassName(String className, String method) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Object o = clazz.getDeclaredConstructor().newInstance();

        Method result = null;
        Method[] allMethod = o.getClass().getDeclaredMethods();
        for (Method m : allMethod) {
            if(m.getName() .equals(method)) {
                result = m;
                break;
            }
        }

        return result;
    }

    public void setAttributeRequest(HttpServletRequest request, ModelView mv) {
        HashMap<String, Object> donne = mv.getData();
        for(String key : donne.keySet()) {
            request.setAttribute(key, donne.get(key));
        }
    }

    public Object setObjectByRequest(HttpServletRequest request, Mapping map) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(map.getClassName());
        Object o = clazz.getDeclaredConstructor().newInstance();

        Field[] allField = o.getClass().getDeclaredFields();
        String field_name;
        String value;

        for(Field f : allField) {
            field_name = f.getName();
            value = request.getParameter(field_name);

            if(value != null) {
                try {
                    o.getClass()
                            .getMethod("set"+this.casse(field_name), f.getType())
                            .invoke(o, this.castPrimaryType(value, f.getType()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return o;
    }
    public boolean isIn(String[] data, String find) {
        for (String s : data) {
            if(s.trim().equals(find)) return true;
        }
        return false;
    }

    public void loadMapping(String path, String tomPath, HashMap<String, Mapping> mappingUrls, HashMap<String, Object> singleton) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException,Exception {
        List<Class<?>> allClass = this.FindAllClass(path, tomPath);
        Mapping mapping;
        Method[] allMethods;

        for(Class<?> c : allClass) {
            allMethods = c.getMethods();

            if (c.isAnnotationPresent(Scope.class)) {
                if (c.getAnnotation(Scope.class).type().equals(ScopeType.SINGLETON)){
                    Class<?> clazz = Class.forName(c.getName());
                    Object temp = clazz.getDeclaredConstructor().newInstance();
                    singleton.put(c.getName(), temp);
                }
            }

            for(Method m : allMethods) {
                if(m.isAnnotationPresent(MethodAnnotation.class)) {
                    mapping = new Mapping();
                    mapping.setClassName(c.getName());
                    mapping.setMethod(m.getName());
                    mappingUrls.put(m.getAnnotation(MethodAnnotation.class).url(), mapping);
                }
            }
        }
    }



}
