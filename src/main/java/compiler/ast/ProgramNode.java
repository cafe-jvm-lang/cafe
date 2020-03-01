package compiler.ast;

import java.util.List;
import java.util.Vector;

import compiler.ast.visitor.Visitor;

public class ProgramNode implements Node{
	private FuncDeclNode mainF;
	private final Vector<FuncDeclNode> funcDeclL;
	
	public ProgramNode() {
		funcDeclL = new Vector<>();
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public void setMainF(FuncDeclNode mainF) {
		this.mainF = mainF;
	}
	
	public FuncDeclNode getMainF() {
		return mainF;
	}
	
	public List<FuncDeclNode> getFuncList(){
		return funcDeclL;
	} 
	
	public void addFuncDeclNode(FuncDeclNode node) {
		funcDeclL.add(node);
	}
}