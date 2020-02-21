package compiler.ast.body;

import compiler.ast.utils.EnclosingScope;

public class VarDeclarationNode extends ProgramNode{
	
	private String varName;
	private String varValue;
	
	private EnclosingScope scope;
	
	public VarDeclarationNode(String varName,String varValue,EnclosingScope scope) {
		this.varName = varName;
		this.varValue = varValue;
		this.scope = scope;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public String getVarValue() {
		return varValue;
	}
	
	public EnclosingScope getEnclosingScope() {
		return scope;
	}
}
