package compiler.cafelang;

import cafe.DynamicObject;

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
        init();
    }

}
