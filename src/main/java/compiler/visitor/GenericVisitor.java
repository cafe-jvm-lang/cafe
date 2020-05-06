package compiler.visitor;

import compiler.ast.ArgsNode;
import compiler.ast.BinaryExprNode;
import compiler.ast.Block;
import compiler.ast.ElseStmtNode;
import compiler.ast.FuncCallNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.LiteralNode;
import compiler.ast.OperatorNode;
import compiler.ast.ParameterNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;

public interface GenericVisitor {
	<R> R visit(ProgramNode n);
	
	<R> R visit(FuncDeclNode n);
	
	<R> R visit(VarDeclNode n);
	
	<R> R visit(VarDeclWithAsgnNode n);
	
	<T,R> R visit(ArgsNode<T> n);
	
	<R> R visit(ParameterNode n);
	
	<R> R visit(ReturnStmtNode n);
	
	<R> R visit(LiteralNode n);
	
	<R> R visit(BinaryExprNode n);
	
	<R> R visit(UnaryExprNode n);
	
	<R> R visit(FuncCallNode n);
	
	<R> R visit(IdentifierNode n);
	
	<R> R visit(OperatorNode n);
	
	<R> R visit(IfStmtNode n);
	
	<R> R visit(ElseStmtNode n);
	
	<R> R visit(IfElseStmtNode n);
	
	<R> R visit(Block n);
}
