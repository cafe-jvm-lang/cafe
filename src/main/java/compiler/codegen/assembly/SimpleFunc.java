package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;

import java.util.function.Predicate;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import compiler.Symbol;
import compiler.SymbolTable;
import compiler.SymbolTableMapper;
import compiler.utils.HandleType;
import compiler.utils.SymbolType;

public class SimpleFunc implements Func {

	private final ClassWriter cw;
	private final MethodVisitor mv;
	private boolean hasReturn;
	private final SymbolTable table;
	
	private static Predicate<Symbol> symbolChecker;
	private int symbolIndex=-1;
	private Symbol currSymbol;
	
	private SimpleFunc(final ClassWriter cw,int modifiers, final String name, final String descriptor,int block) {
		this.cw = cw;
		mv = cw.visitMethod(modifiers, name, descriptor, null, null);
		table = SymbolTableMapper.getSymbolTable(block);
	}
	
	static SimpleFunc simpleFunc(final ClassWriter cw,int modifiers, final String name, final int args,int block) {
		return new SimpleFunc(cw,modifiers, name, Func.functionSignature(args),block);
	}

	@Override
	public Func init() {
		mv.visitCode();
		return this;
	}

	@Override
	public Func declareVar(String var) {
		initVarAsgn(var);
		return this;
	}
	
	@Override
	public Func initVarAsgn(String var) {
		currSymbol = table.getSymbol(var, SymbolType.VAR);
		return this;
	}

	@Override
	public Func initVarAsgnEnd() {
		symbolIndex++;
		currSymbol.args = symbolIndex;
		table.setSymbol(currSymbol);
		BytecodeUtils.storeVariableFromOperandStack(mv, symbolIndex);
		return null;
	}

	@Override
	public Func invokeFunc(String name, int args, HandleType handleType) {
		if(handleType == HandleType.OPERATOR_HANDLE_TYPE)
			mv.visitInvokeDynamicInsn(name, Func.functionSignature(args), Func.OPERATOR_HANDLE, 2);
		else
			mv.visitInvokeDynamicInsn(name, Func.functionSignature(args), Func.FUNCTION_HANDLE, Program.packageClazz);
		return this;
	}

	@Override
	public Func loadLiteral(Object num) {
		BytecodeUtils.loadLiteralToOperandStack(mv, num);
		return this;
	}

	@Override
	public Func loadIdentifier(String idName) {
		symbolChecker = e -> e.name.equals(idName)
				&& e.symType == SymbolType.VAR
				&& e.args != null;
		
		Symbol symbol = table.getSymbol(symbolChecker);
		if(symbol == null) {
			if(Program.globalSymbolTable.hasSymbol(idName, SymbolType.VAR)) {
				mv.visitInvokeDynamicInsn("#"+idName, "()Ljava/lang/Object;", Func.FUNCTION_HANDLE, Program.packageClazz);
			}else {
				// Some proper error handling required
				System.out.println("No such symbol found");
			}
		}
		else {
			BytecodeUtils.loadVariableToOperandStack(mv, symbol.args);
		}
		return this;
	}
	
	@Override
	public Func loadReturnValue() {
		hasReturn = true;
		return this;
	}
	
	@Override
	public Func declareIfCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Func end() {
		if(!hasReturn)
			mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0,0);
		mv.visitEnd();
		return this;
	}
}
