package compiler.codegen.assembly;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.Arrays;

import compiler.codegen.runtime.Bootstrapper;

public class Test {
	static class inner{
		
	}
	
	static MethodHandle handle;
	{
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			handle = lookup.findStatic(Test.class, "X", methodType(Object.class,Object[].class));
			System.out.println(handle);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void test() throws Throwable{
		//Test i = new Test();
		MethodHandle handle2 = handle//.bindTo(new Test())
				.asCollector(Object[].class, 3);
		System.out.println(handle2);
		handle2.invokeWithArguments(1,2,3);
	}
	
	public static Object X(Object[] x) {
		Arrays.stream(x).forEach(e->System.out.println(e.getClass()));
		System.out.println(x);
		return x;

	}
	
	public static void main(String[] args) throws Throwable {
		new Test().test();
//		MethodHandles.Lookup lookup = MethodHandles.lookup();
//        MethodType methodTypes = MethodType.methodType(boolean.class, Object.class);
//        MethodHandle methodHandle = lookup.findVirtual(ArrayList.class, "add", methodTypes);
//
//        List<Object> objectList1 = new ArrayList<>();
//        System.out.println(methodHandle);
//        MethodHandle addMH1 = methodHandle.bindTo(objectList1);
//        System.out.println(addMH1);
//        List<Object> objectList2 = new ArrayList<>();
//        MethodHandle addMH2 = methodHandle.bindTo(objectList2);
//        addMH1.invoke("Hello");
//        addMH2.invoke("Bye");
//        System.out.println(System.identityHashCode(addMH1));
//        System.out.println(System.identityHashCode(addMH2));
	}
}
