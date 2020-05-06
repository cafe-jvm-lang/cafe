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

	@Override
	public <R> R visit(ProgramNode n) {
		program = Program.newProgram(n.fileName);
		program.makeDefaultConstructor();

		// Get main function
		currFunc = mainFunc = program.initMain();

		n.getMainF().block.accept(this); 

		mainFunc.end();
		program.end();

		return null;

	}

	@Override
	public <R> R visit(FuncDeclNode n) {

		// create new function
		String funcName = n.nm.id;
		int funcArgs = n.argL.size();
		int block = n.block.hashCode();
		
		currFunc = program.initSimpleFunc(block,funcName, funcArgs);

		// iterate over this function
		n.block.accept(this);
		currFunc.end();

		// continue iterating main function
		currFunc = mainFunc;

		return null;
	}

	@Override
	public <R> R visit(VarDeclNode n) {
		currFunc.declareVar(n.nm.id);
		return null;
	}

	@Override
	public <R> R visit(VarDeclWithAsgnNode n) {
		String nm = n.nm.nm.id;
		currFunc.initVarAsgn(nm);

		n.val.accept(this);

		currFunc.initVarAsgnEnd();

		return null;
	}

	@Override
	public <T, R> R visit(ArgsNode<T> n) {
		
//		if(n.arg instanceof IdentifierNode)
//			IdentifierNode node = (IdentifierNode)n.arg;
//		n.arg.accept(this);
//		return null;
	}

	@Override
	public <R> R visit(ReturnStmtNode n) {

		n.expr.accept(this);
		currFunc.loadReturnValue();

		return null;
	}

	@Override
	public Object visit(LiteralNode n) {
		currFunc.loadLiteral(n.i);
		return n.i;
	}

	@Override
	public <R> R visit(BinaryExprNode n) {
		String opName = n.op.type.getOp();

		if (!opName.equals("asg")) {
			n.expr1.accept(this);
			n.expr2.accept(this);
			currFunc.invokeFunc(opName, /* args= */2, HandleType.OPERATOR_HANDLE_TYPE);
		} else {
			currFunc.initVarAsgn(n.expr1.accept(this));
			n.expr2.accept(this);
			currFunc.initVarAsgnEnd();
		}

		return null;
	}

	@Override
	public <R> R visit(UnaryExprNode n) {

		n.expr.accept(this);
		currFunc.loadLiteral(-1); // temporary solution for negative numbers only
		currFunc.invokeFunc("mul", /* args= */2, HandleType.OPERATOR_HANDLE_TYPE);
		return null;
	}

	@Override
	public <R> R visit(FuncCallNode n) {
		String funcName = n.iden.id;
		int argsSize = n.argsL.size();

		n.argsL.forEach(e -> e.accept(this));
		currFunc.invokeFunc(funcName, argsSize, HandleType.FUNCTION_HANDLE_TYPE);
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
		
		return null;
	}

	@Override
	public <R> R visit(ElseStmtNode n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R visit(IfElseStmtNode n) {
		
		return null;
	}

	@Override
	public <R> R visit(ParameterNode n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R visit(Block n) {
		n.stmtL.forEach(e -> e.accept(this));
		return null;
	}

}
