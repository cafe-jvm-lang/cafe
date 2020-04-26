package compiler.codegen.runtime;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;

import compiler.codegen.runtime.OperatorInvocation.OperatorCallSite;

public class MethodInvocation {
	private static MethodHandle FALLBACK;
	
	static {
		try {
			Lookup lookup = MethodHandles.lookup();

			FALLBACK = lookup.findStatic(MethodInvocation.class, "fallback", methodType(Object.class,MethodInvocationCallSite.class,Object[].class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	static class MethodInvocationCallSite extends MutableCallSite {
	    final Lookup callerLookup;
	    final String name;
	    MethodHandle fallback;
	    String clazz;
	    
	    MethodInvocationCallSite(Lookup callerLookup, String name, MethodType type,String clazz) {
	      super(type);
	      this.callerLookup = callerLookup;
	      this.name = name;
	      this.clazz = clazz;
	    }
	}
	
	public static Object fallback(MethodInvocationCallSite callSite,Object args[]) throws Throwable{
		MethodHandle target;
		
		target = callSite.callerLookup.findStatic(Class.forName(callSite.clazz), callSite.name, callSite.type());
		
		if(args.length > 0)
			System.out.println(args[0]);
		return target.invokeWithArguments(args);
	}
	
	public static CallSite bootstrap(Lookup caller, String name, MethodType type,Object... args) throws Throwable {

		MethodHandle fallback;
		MethodInvocationCallSite callSite = new MethodInvocationCallSite(caller, name, type,(String)args[0]);
		fallback = FALLBACK;
		
		fallback = FALLBACK.bindTo(callSite)
				.asCollector(Object[].class, type.parameterCount())
				.asType(type);
		
		callSite.fallback = fallback;
		callSite.setTarget(fallback);
		System.out.println("here-------------------------------------------");
		return callSite;
	}
	
}
