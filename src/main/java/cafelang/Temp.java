package cafelang;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

public class Temp {

    static Object rr() {
        return null;
    }

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
        int a = 1;
        File f = new File("C:/dir");
        URL[] cp = {f.toURI().toURL()};
        URLClassLoader urlcl = new URLClassLoader(cp);
        Class clazz = urlcl.loadClass("distantinterfaces.DistantClass");
    }

}

