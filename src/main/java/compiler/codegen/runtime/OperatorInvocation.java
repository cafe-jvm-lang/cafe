package compiler.codegen.runtime;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.List;
import java.util.stream.Stream;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

public class OperatorInvocation {

	private static MethodHandle FALLBACK;
	
	static {
		try {
			Lookup lookup = MethodHandles.lookup();

			FALLBACK = lookup.findStatic(OperatorInvocation.class, "fallback", methodType(Object.class,OperatorCallSite.class,Object[].class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	static class OperatorCallSite extends MutableCallSite {

	    final Lookup callerLookup;
	    final String name;
	    MethodHandle fallback;

	    OperatorCallSite(Lookup callerLookup, String name, MethodType type) {
	      super(type);
	      this.callerLookup = callerLookup;
	      this.name = name;
	    }
	  }
	
	public static Object fallback(OperatorCallSite callSite,Object args[]) throws Throwable{
		MethodHandle target;
		//Stream.of(args).forEach(System.out::println);
		Class<?> clazz1 = args[0].getClass();
		Class<?> clazz2 = args[1].getClass();
		
		target = callSite.callerLookup.findStatic(OperatorInvocation.class, callSite.name, methodType(Object.class,clazz1,clazz2));
		Object op = target.invokeWithArguments(args);
		System.out.println(op);
		return op;
	}

	public static CallSite bootstrap(Lookup caller, String name, MethodType type, int arity) throws Throwable {
		//System.out.println(name);
		MethodHandle fallback;
		OperatorCallSite callSite = new OperatorCallSite(caller, name, type);
		if (arity == 2) {
			fallback = FALLBACK;
		}
		
		fallback = FALLBACK .bindTo(callSite)
							.asCollector(Object[].class, type.parameterCount())
							.asType(type);
		//System.out.println(fallback);
		callSite.fallback = fallback;
		callSite.setTarget(fallback);
		
		return callSite;
	}

	public static Object add(Integer a, Integer b) {
		System.out.println(a+"+"+b+"="+(a+b));
		return a + b;
	}
	
	public static Object sub(Integer a, Integer b) {
		System.out.println(a+"-"+b+"="+(a-b));
		return a - b;
	}
	public static Object mul(Integer a, Integer b) {
		System.out.println(a+"*"+b+"="+(a*b));
		return a * b;
	}
	public static Object div(Integer a, Integer b) {
		System.out.println(a+"/"+b+"="+(a/b));
		return a / b;
	}
}
