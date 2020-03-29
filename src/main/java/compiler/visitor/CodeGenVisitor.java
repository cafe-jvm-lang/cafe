package compiler.visitor;

import compiler.ast.ArgsNode;
import compiler.ast.BinaryExprNode;
import compiler.ast.ElseStmtNode;
import compiler.ast.FuncCallNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.IntegerLiteralNode;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.codegen.assembly.Func;

public class CodeGenVisitor implements Visitor{

	@Override
	public void visit(ProgramNode n) {
		n.getMainF().accept(this);
	}

	@Override
	public void visit(FuncDeclNode n) {
		Func func = new Func(n.nm.id);
	}

	@Override
	public void visit(VarDeclNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarDeclWithAsgnNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArgsNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ReturnStmtNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntegerLiteralNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BinaryExprNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(UnaryExprNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FuncCallNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IdentifierNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OperatorNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IfStmtNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ElseStmtNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IfElseStmtNode n) {
		// TODO Auto-generated method stub
		
	}
	
}
