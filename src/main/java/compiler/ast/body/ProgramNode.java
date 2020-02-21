package compiler.ast.body;

import java.util.ArrayList;
import java.util.List;

import compiler.ast.Node;

public class ProgramNode extends Node{
	private List<MethodDeclarationNode> methodDeclList;
	private List<VarDeclarationNode> varDeclList;
	
	public ProgramNode() {
		methodDeclList = new ArrayList<MethodDeclarationNode>();
		varDeclList = new ArrayList<VarDeclarationNode>();
	}
	
	public List<MethodDeclarationNode> getMethodDeclList(){
		return methodDeclList;
	}
	
	public List<VarDeclarationNode> getVarDeclList(){
		return varDeclList;
	}
	
	public void addMethodDecl(MethodDeclarationNode node) {
		methodDeclList.add(node);
	}
	
	public void addVarDecl(VarDeclarationNode node) {
		varDeclList.add(node);
	}
}
