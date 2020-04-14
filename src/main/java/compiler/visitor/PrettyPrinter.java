package compiler.visitor;

import compiler.ast.ArgsNode;
import compiler.ast.ArgsNodeList;
import compiler.ast.BinaryExprNode;
import compiler.ast.ElseStmtNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.IntegerLiteralNode;
import compiler.ast.FuncCallNode;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;

public class PrettyPrinter implements Visitor {

	private int indent = 1;

	private void print(String text) {
		String format = "%" + indent + "s" + text + "%n";
		System.out.format(format, "");

	}

	@Override
	public void visit(ProgramNode n) {
		StmtNodeList l = n.getMainF().stmtL;
		for (int i = 0; i < l.size(); i++) {
			l.elementAt(i).accept(this);
		}
	}

	@Override
	public void visit(FuncDeclNode n) {
		print("FuncDeclNode:");
		indent += 4;
		n.nm.accept(this);
		ArgsNodeList a = n.argL;
		print("ArgsNodeList: ");
		indent += 4;
		for (int i = 0; i < a.size(); i++) {
			a.elementAt(i).accept(this);
		}
		indent -= 4;

		print("StmtNodeList: ");
		indent += 4;
		StmtNodeList l = n.stmtL;
		for (int i = 0; i < l.size(); i++) {
			l.elementAt(i).accept(this);
		}
		indent -= 4;
		indent -= 4;
	}

	@Override
	public void visit(VarDeclNode n) {
		print("VarDeclNode: ");
		indent += 4;
		n.nm.accept(this);
		indent -= 4;
	}

	@Override
	public void visit(VarDeclWithAsgnNode n) {
		print("VarDeclWithAsgnNode: ");
		indent += 4;
		n.nm.accept(this);
		indent -= 4;

		indent += 4;
		n.val.accept(this);
		indent -= 4;
	}

	@Override
	public <T> void visit(ArgsNode<T> n) {
		print("ArgsNode: " + n.arg);
	}

	@Override
	public void visit(ReturnStmtNode n) {
		print("ReturnStmtNode:");

		indent += 4;
		n.expr.accept(this);
		indent -= 4;
	}

	@Override
	public void visit(IntegerLiteralNode n) {
		print("IntegerLiteralNode: " + n.i);
	}

	@Override
	public void visit(BinaryExprNode n) {
		print("BinaryExprNode: ");

		indent += 4;
		n.expr1.accept(this);
		n.op.accept(this);
		n.expr2.accept(this);
		indent -= 4;
	}

	@Override
	public void visit(UnaryExprNode n) {
		print("UnaryExprNode: ");

		indent += 4;
		n.op.accept(this);
		n.expr.accept(this);
		indent -= 4;
	}

	@Override
	public void visit(FuncCallNode n) {
		print("MethodCallNode:");

		indent += 4;
		n.iden.accept(this);
		ArgsNodeList a = n.argsL;
		print("ArgsNodeList: ");
		indent += 4;
		for (int i = 0; i < a.size(); i++) {
			a.elementAt(i).accept(this);
		}
		indent -= 4;
		indent -= 4;
	}

	@Override
	public void visit(IdentifierNode n) {
		print("IdentifierNode: " + n.id);
	}

	@Override
	public void visit(OperatorNode n) {
		print("OperatorNode: " + n.type.toString());
	}

	@Override
	public void visit(IfStmtNode n) {
		print("IfStmtNode:");

		indent += 4;
		print("Condition:");
		indent += 4;
		n.cond.accept(this);
		indent -= 4;

		print("StmtNodeList:");
		indent += 4;
		StmtNodeList l = n.stmtL;
		for (int i = 0; i < l.size(); i++) {
			l.elementAt(i).accept(this);
		}

		indent -= 4;
		indent -= 4;
	}

	@Override
	public void visit(ElseStmtNode n) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visit(IfElseStmtNode n) {
		print("IfElseStmtNode:");

		indent += 4;
		print("IF:");
		indent += 4;
		print("Condition:");
		indent += 4;
		n.ifCond.accept(this);
		indent -= 4;

		print("StmtNodeList:");
		indent += 4;
		StmtNodeList l = n.ifStmtL;
		for (int i = 0; i < l.size(); i++) {
			l.elementAt(i).accept(this);
		}

		indent -= 4;
		indent -= 4;
		
		print("ELSE:");
		indent += 4;
		l = n.elseStmtL;
		for (int i = 0; i < l.size(); i++) {
			l.elementAt(i).accept(this);
		}

		indent -= 4;
		indent -= 4;
	}
}
