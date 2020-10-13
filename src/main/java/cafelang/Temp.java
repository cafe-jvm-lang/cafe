package cafelang;

import cafe.DynamicObject;

import java.util.ArrayList;
import java.util.List;

public class Temp {
    static DynamicObject doo= new DynamicObject()  ;

    static void insertINto(String key, Object value){
        doo.define(key,value);
    }

    static Object retrieve(String key){
        return doo.get(key);
    }

    static void init(){

    }

    public static void main(String[] args) {
        int a = 10;
        List<Integer> l = new ArrayList<>();
        l.add(a);
        l.set(0,11);
        init();
    }

}
