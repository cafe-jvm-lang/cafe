package compiler.visitor;

import compiler.ast.ArgsNode;
import compiler.ast.BinaryExprNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IntegerLiteral;
import compiler.ast.MethodCall;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;

public class PrettyPrinter implements Visitor{

	private int indent = 0;
	
	@Override
	public void visit(ProgramNode n) {
		
	}

	@Override
	public void visit(FuncDeclNode n) {
		// TODO Auto-generated method stub
		
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
	public void visit(IntegerLiteral n) {
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
	public void visit(MethodCall n) {
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

}
