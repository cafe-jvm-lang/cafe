package compiler.analyzer;

import java.util.Map;

import compiler.ast.Node;
import compiler.ast.Node.AnnFuncNode;
import compiler.ast.Node.ArgsListNode;
import compiler.ast.Node.AsgnStmtNode;
import compiler.ast.Node.BinaryExprNode;
import compiler.ast.Node.BlockNode;
import compiler.ast.Node.BoolLitNode;
import compiler.ast.Node.BreakStmtNode;
import compiler.ast.Node.CompIfNode;
import compiler.ast.Node.CompLoopNode;
import compiler.ast.Node.CompNode;
import compiler.ast.Node.ConstDeclNode;
import compiler.ast.Node.ContinueStmtNode;
import compiler.ast.Node.ElseStmtNode;
import compiler.ast.Node.ExprNode;
import compiler.ast.Node.ForStmtNode;
import compiler.ast.Node.FuncCallNode;
import compiler.ast.Node.FuncDeclNode;
import compiler.ast.Node.IdenNode;
import compiler.ast.Node.IfStmtNode;
import compiler.ast.Node.ImportStmtNode;
import compiler.ast.Node.LinkCollNode;
import compiler.ast.Node.LinkCompNode;
import compiler.ast.Node.ListCollNode;
import compiler.ast.Node.ListCompNode;
import compiler.ast.Node.LoopStmtNode;
import compiler.ast.Node.MapCollNode;
import compiler.ast.Node.MapCompNode;
import compiler.ast.Node.NullNode;
import compiler.ast.Node.NumLitNode;
import compiler.ast.Node.ObjCreationNode;
import compiler.ast.Node.ObjectAccessNode;
import compiler.ast.Node.ParameterListNode;
import compiler.ast.Node.ProgramNode;
import compiler.ast.Node.RangeNode;
import compiler.ast.Node.ReturnStmtNode;
import compiler.ast.Node.SetCollNode;
import compiler.ast.Node.SetCompNode;
import compiler.ast.Node.SliceNode;
import compiler.ast.Node.StmtNode;
import compiler.ast.Node.StrLitNode;
import compiler.ast.Node.SubscriptNode;
import compiler.ast.Node.ThisNode;
import compiler.ast.Node.UnaryExprNode;
import compiler.ast.Node.VarDeclNode;
import compiler.util.Context;
import compiler.util.Log;
import compiler.util.LogType.Errors;

public class SemanticsChecker implements Node.Visitor {
	protected static final Context.Key<SemanticsChecker> semanticsKey = new Context.Key<>();

	// Global Symbol Table
	private final SymbolTable GST;

	// Current Symbol Table
	private SymbolTable CST;

	private static enum Expr {
		LHS, LOOP, RANGE, NONE
	}

	private Expr exprType = null;

	// caller node
	private Node.Tag caller = null;

	private Log log;

	public static SemanticsChecker instance(Context context) {
		SemanticsChecker instance = context.get(semanticsKey);
		if (instance == null)
			instance = new SemanticsChecker(context);
		return instance;
	}

	private SemanticsChecker(Context context) {
		context.put(semanticsKey, this);

		GST = new SymbolTable(null);
		CST = GST;

		log = Log.instance(context);
	}

	@Override
	public void visitProgram(ProgramNode n) {
		caller = Node.Tag.PROGRAM;
		for (StmtNode stmt : n.stmts) {
			stmt.accept(this);
		}
	}

	@Override
	public void visitVarDecl(VarDeclNode n) {
		CST.insert(n.var);
		n.value.accept(this);
	}

	@Override
	public void visitIden(IdenNode n) {
		if (!CST.isPresent(n))
			log.error(Errors.SYMBOL_NOT_DECLARED);
	}

	@Override
	public void visitConstDecl(ConstDeclNode n) {
		CST.insert(n.var);
		n.val.accept(this);
	}

	@Override
	public void visitNumLit(NumLitNode n) {
		if (exprType == Expr.LHS || exprType == Expr.LOOP)
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitStrLit(StrLitNode n) {
		if (exprType == Expr.LHS || exprType == Expr.LOOP)
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitBoolLit(BoolLitNode n) {
		if (exprType == Expr.LHS || exprType == Expr.LOOP)
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitFuncDecl(FuncDeclNode n) {
		CST.insert(n.name);
		CST = new SymbolTable(CST);
		n.params.accept(this);
		n.block.accept(this);
		CST = CST.parent;
	}

	@Override
	public void visitObjCreation(ObjCreationNode n) {
		for (Map.Entry<IdenNode, ExprNode> entry : n.prop.entrySet()) {
			entry.getValue().accept(this);
		}
	}

	@Override
	public void visitBlock(BlockNode n) {
		for (StmtNode stmt : n.block) {
			stmt.accept(this);
		}
	}

	@Override
	public void visitAnnFunc(AnnFuncNode n) {
		caller = Node.Tag.ANNFUNC;
		CST = new SymbolTable(CST);
		n.params.accept(this);
		n.block.accept(this);
		CST = CST.parent;
	}

	@Override
	public void visitListColl(ListCollNode n) {
		caller = Node.Tag.LIST;
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitSetColl(SetCollNode n) {
		caller = Node.Tag.SET;
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitLinkColl(LinkCollNode n) {
		caller = Node.Tag.LINKEDLIST;
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitMapColl(MapCollNode n) {
		caller = Node.Tag.MAP;
		for (Map.Entry<ExprNode, ExprNode> entry : n.pairs.entrySet()) {
			entry.getKey().accept(this);
			entry.getValue().accept(this);
		}
	}

	@Override
	public void visitBinaryExpr(BinaryExprNode n) {
		if (exprType != Expr.LHS) {
			n.e1.accept(this);
			n.e2.accept(this);
		} else {
			log.error(Errors.LHS_EXPR_ERROR);
		}
	}

	@Override
	public void visitUnaryExpr(UnaryExprNode n) {
		if (exprType != Expr.LHS) {
			n.e.accept(this);
		} else {
			log.error(Errors.LHS_EXPR_ERROR);
		}
	}

	@Override
	public void visitThis(ThisNode n) {
		if (exprType == Expr.LHS || exprType == Expr.LOOP)
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitNull(NullNode n) {
		if (exprType == Expr.LHS || exprType == Expr.LOOP)
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitFuncCall(FuncCallNode n) {
		if (exprType != Expr.LHS) {
			n.invokedOn.accept(this);
			n.args.accept(this);
		} else
			log.error(Errors.LHS_EXPR_ERROR);
	}

	@Override
	public void visitSubscript(SubscriptNode n) {
		n.subscriptOf.accept(this);
		n.index.accept(this);
	}

	@Override
	public void visitObjAccess(ObjectAccessNode n) {
		if (caller != Node.Tag.OBJACCESS) {
			caller = Node.Tag.OBJACCESS;
			n.accessedOn.accept(this);
		}
	}

	@Override
	public void visitSlice(SliceNode n) {
		n.slicedOn.accept(this);
		n.start.accept(this);
		n.end.accept(this);
	}

	@Override
	public void visitArgsList(ArgsListNode n) {
		for (ExprNode expr : n.args) {
			expr.accept(this);
		}
	}

	@Override
	public void visitParamList(ParameterListNode n) {
		CST.insertAll(n.params);
	}

	@Override
	public void visitImportStmt(ImportStmtNode n) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitAsgnStmt(AsgnStmtNode n) {
		exprType = Expr.LHS;
		n.lhs.accept(this);
		exprType = Expr.NONE;
		n.rhs.accept(this);
	}

	@Override
	public void visitIfStmt(IfStmtNode n) {
		n.ifCond.accept(this);
		n.ifBlock.accept(this);
		if (n.elsePart != null)
			for (StmtNode stmt : n.elsePart)
				stmt.accept(this);
	}

	@Override
	public void visitElseStmt(ElseStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitForStmt(ForStmtNode n) {
		for (StmtNode stmt : n.init)
			stmt.accept(this);
		n.cond.accept(this);
		for (AsgnStmtNode stmt : n.counters)
			stmt.accept(this);
	}

	@Override
	public void visitLoopStmt(LoopStmtNode n) {
		n.var1.accept(this);
		n.var2.accept(this);
		n.collection.accept(this);
		n.block.accept(this);
	}

	@Override
	public void visitReturnStmt(ReturnStmtNode n) {
		n.expr.accept(this);
	}

	@Override
	public void visitContinueStmt(ContinueStmtNode n) {
		
	}

	@Override
	public void visitBreakStmt(BreakStmtNode n) {

	}

	@Override
	public void visitListComp(ListCompNode n) {
		for(CompNode node: n.nested)
			node.accept(this);
	}

	@Override
	public void visitLinkComp(LinkCompNode n) {
		for(CompNode node: n.nested)
			node.accept(this);
	}

	@Override
	public void visitSetComp(SetCompNode n) {
		for(CompNode node: n.nested)
			node.accept(this);
	}

	@Override
	public void visitMapComp(MapCompNode n) {
		for(CompNode node: n.nested)
			node.accept(this);
	}

	@Override
	public void visitCompLoop(CompLoopNode n) {

	}

	@Override
	public void visitCompIf(CompIfNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitListRange(RangeNode n) {

	}

}
