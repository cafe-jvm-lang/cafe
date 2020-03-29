package compiler.visitor;

import compiler.SymbolTable;
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
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.codegen.assembly.Func;
import compiler.codegen.assembly.Program;
import compiler.utils.SymbolType;

public class CodeGenVisitor implements Visitor{

	Program prog;
	Func mainFunc;
	
	@Override
	public void visit(ProgramNode n) {
		prog = new Program(n.fileName);
		n.getMainF().accept(this);
	}

	@Override
	public void visit(FuncDeclNode n) {
		mainFunc = prog.genMainFunc();
		StmtNodeList stmtL = n.stmtL;
		
		for(int i=0;i<stmtL.size() ;i++) {
			stmtL.elementAt(i).accept(this);
		}
	}

	@Override
	public void visit(VarDeclNode n) {
		
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
		FuncDeclNode callee = (FuncDeclNode) SymbolTable.getSymbol(n.iden, SymbolType.FUNC).ogNode;
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
