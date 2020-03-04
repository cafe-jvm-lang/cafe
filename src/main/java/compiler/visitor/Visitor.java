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
	
	void visit(IntegerLiteral n);
	
	void visit(BinaryExprNode n);
	
	void visit(UnaryExprNode n);
	
	void visit(MethodCall n);
	
	void visit(IdentifierNode n);
	
	void visit(OperatorNode n);
}
