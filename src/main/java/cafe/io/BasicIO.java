package cafe.io;

import cafe.BasePrototype;

public class BasicIO {
    public static void print(BasePrototype b, Object o) {
        System.out.print(o);
    }

    public static void println(BasePrototype b, Object o) {
        System.out.println(o);
    }

    public static String input(BasePrototype b) {
        return System.console()
                     .readLine();
    }

    public static String input(BasePrototype b, String s) {
        println(b, s);
        return input(b);
    }
}
