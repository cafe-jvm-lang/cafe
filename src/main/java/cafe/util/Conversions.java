package cafe.util;

import cafe.BasePrototype;

public class Conversions {
    public static int Int(BasePrototype b, String s){
        return Integer.parseInt(s);
    }

    public static float Float(BasePrototype b, String s){
        return Float.parseFloat(s);
    }
}
