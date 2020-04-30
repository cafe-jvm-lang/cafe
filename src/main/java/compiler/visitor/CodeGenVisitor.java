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
import compiler.codegen.assembly.ExprFunc;
import compiler.codegen.assembly.Func;
import compiler.codegen.assembly.Program;
import compiler.lexer.tokentypes.TokenType.OpTokenType;
import compiler.utils.HandleType;

public class CodegenVisitor implements GenericVisitor {

	private Program program;
	private Func mainFunc;
	private Func constructor;
	private Func currFunc;
	private Func currExpr = null;
	ExprFunc.Stack currStack;

	@Override
	public <R> R visit(ProgramNode n) {
		Program program = new Program(n.fileName);

		constructor = program.visitConstructor();
		constructor.visitEnd();
		// Get main function
		currFunc = mainFunc = program.visitMain();
		// Iterate over all variables in main func, and declare them as class variables
		// with private static modifiers.
		n.getMainF().stmtL.forEach(e -> {
			e.accept(this);
		});

		mainFunc.visitEnd();
		program.visitEnd();

		return null;

	}

	@Override
	public <R> R visit(FuncDeclNode n) {
		 return null;
	}

	@Override
	public <R> R visit(VarDeclNode n) {
		currFunc.visitVarAsgn(n.nm.id);
		currFunc.visitVarAsgnEnd();
		return null;
	}

	@Override
	public <R> R visit(VarDeclWithAsgnNode n) {
		String nm = n.nm.nm.id;
		currFunc.visitVarAsgn(nm);

		n.val.accept(this);

		currFunc.visitVarAsgnEnd();

//		currExpr = currFunc.visitExpr("calc"+nm);
//		currStack = currExpr.getExprStack();
//		n.val.accept(this);
//		currExpr.genExprFunc1();
//		currFunc.visitVarAsgn(nm, currExpr);

		return null;
	}

	@Override
	public <T, R> R visit(ArgsNode<T> n) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public <R> R visit(ReturnStmtNode n) {
		return null;
	}

	@Override
	public Number visit(IntegerLiteralNode n) {
		currFunc.loadLiteral(n.i);
		return n.i;
	}

	@Override
	public <R> R visit(BinaryExprNode n) {
		String opName = n.op.type.getOp();
		
		if(!opName.equals("asg")) {
			n.expr1.accept(this);
			n.expr2.accept(this);
			currFunc.visitFuncInvk(opName, /* args= */2, HandleType.OPERATOR_HANDLE_TYPE,2);
		}else {
			currFunc.visitVarAsgn(n.expr1.accept(this));
			n.expr2.accept(this);
			currFunc.visitVarAsgnEnd();
			//currFunc.visitFuncInvk(opName, /* args= */2, HandleType.OPERATOR_HANDLE_TYPE,2);	
		}

//		boolean isExpr1Term = n.expr1 instanceof NodeWithTerminalExpr;
//		boolean isExpr2Term = n.expr2 instanceof NodeWithTerminalExpr;
//		if (isExpr1Term && isExpr2Term) {
//			
//			currStack.push(n.op.type.getOp());
//			//if(n.op.type.getOp().equals("#div") ) {
//				currStack.push(n.expr1.accept(this));
//				currStack.push(n.expr2.accept(this));
////			}
////			else {	// for #add & #sub
////				currStack.push(n.expr1.accept(this));
////				currStack.push(n.expr2.accept(this));	
////			}
//		} else if (isExpr1Term) {
//			currStack.push(n.op.type.getOp());
//			currStack.push(n.expr1.accept(this));
//			n.expr2.accept(this);
//		} else if (isExpr2Term) {
//			currStack.push(n.op.type.getOp());
//			n.expr1.accept(this);
//			currStack.push(n.expr2.accept(this));
//		} else {
//			currStack.push(n.op.type.getOp());
//			n.expr1.accept(this);
//			n.expr2.accept(this);
//		}

		return null;
	}

	@Override
	public <R> R visit(UnaryExprNode n) {

		n.expr.accept(this);
		currFunc.loadLiteral(-1);	// temporary solution for nnegative numbers only
		String opName = n.op.type.getOp();
		currFunc.visitFuncInvk("mul", /* args= */2, HandleType.OPERATOR_HANDLE_TYPE,2);

//		boolean isExpr1Term = n.expr instanceof NodeWithTerminalExpr;
//		
//		currStack.push("#mul");
//		currStack.push(-1);
//		
//		if(isExpr1Term) {
//			currStack.push(n.expr.accept(this));
//		}
//		else {
//			n.expr.accept(this);
//		}

		return null;
	}

	@Override
	public <R> R visit(FuncCallNode n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(IdentifierNode n) {
		currFunc.loadIdentifier(n.id);
		return n.id;
	}

	@Override
	public OpTokenType visit(OperatorNode n) {
		return n.type;

	}

	@Override
	public <R> R visit(IfStmtNode n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R visit(ElseStmtNode n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R visit(IfElseStmtNode n) {
		// TODO Auto-generated method stub
		return null;
	}

}
