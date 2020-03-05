package compiler.visitor;

import compiler.ast.ArgsNode;
import compiler.ast.BinaryExprNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.IntegerLiteralNode;
import compiler.ast.MethodCallNode;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;

public interface Visitor {
	void visit(ProgramNode n);
	
	void visit(FuncDeclNode n);
	
	void visit(VarDeclNode n);
	
	void visit(VarDeclWithAsgnNode n);
	
	void visit(ArgsNode n);
	
	void visit(ReturnStmtNode n);
	
	void visit(IntegerLiteralNode n);
	
	void visit(BinaryExprNode n);
	
	void visit(UnaryExprNode n);
	
	void visit(MethodCallNode n);
	
	void visit(IdentifierNode n);
	
	void visit(OperatorNode n);
	
	void visit(IfStmtNode n);
	
	void visit(IfElseStmtNode n);
}
