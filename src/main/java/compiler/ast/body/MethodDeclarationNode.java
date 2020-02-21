package compiler.ast.body;

import java.util.List;

import compiler.ast.stmt.StmtNode;

public class MethodDeclarationNode extends ProgramNode{
	private String methodName;
	private ArgsNode args;

	private List<StmtNode> stmtList;
	private List<VarDeclarationNode> varList;
	
	public MethodDeclarationNode(String methodName) {
		this(methodName,null);
	}
	
	public MethodDeclarationNode(String methodName,ArgsNode args) {
		this.methodName = methodName;
		this.args = args;
	}
	
	public List<StmtNode> getStmtList(){
		return stmtList;
	}
	
	public List<VarDeclarationNode> getVarList(){
		return varList;
	}
}
