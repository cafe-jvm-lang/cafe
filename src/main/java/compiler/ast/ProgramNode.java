package compiler.ast;

import java.util.List;
import java.util.Vector;

import compiler.ast.visitor.Visitor;

public class ProgramNode implements Node{
	private Vector<VarDeclWithAsgnNode> varDeclL;
	private Vector<FuncDeclNode> funcDeclL;
	
	public ProgramNode() {
		varDeclL = new Vector<>();
		funcDeclL = new Vector<>();
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public List<VarDeclWithAsgnNode> getVarList(){
		return varDeclL;
	}
	
	public List<FuncDeclNode> getFuncList(){
		return funcDeclL;
	}
	
	public void addVarDeclNode(VarDeclWithAsgnNode node) {
		varDeclL.add(node);
	}
	
	public void addFuncDeclNode(FuncDeclNode node) {
		funcDeclL.add(node);
	}
}
