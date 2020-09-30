package compiler.cafelang;

import cafe.DynamicObject;

public class Temp {
    static DynamicObject doo= new DynamicObject()  ;

    void hello(){
        doo.define(null,null);
    }

}
