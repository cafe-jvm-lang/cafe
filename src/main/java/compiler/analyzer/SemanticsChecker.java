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

	private enum Expr {
		LHS, RHS
	}

	private enum ObjAcc{
		accesedOn,prop
	}

	private Expr exprType = null;
	private ObjAcc objType = null;
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

	private Symbol symbol(String name){
		return new Symbol(name);
	}

	@Override
	public void visitProgram(ProgramNode n) {
		for (StmtNode stmt : n.stmts) {
			stmt.accept(this);
		}
	}

	@Override
	public void visitVarDecl(VarDeclNode n) {
		Symbol sym = symbol(n.var.name);
		if(!CST.insert(sym))
			logErrors(Errors.DUPLICATE_SYMBOL,n.var.name);
		if(n.value != null)
			n.value.accept(this);
	}

	@Override
	public void visitIden(IdenNode n) {
		if (!CST.isPresent(n.name))
			logErrors(Errors.SYMBOL_NOT_DECLARED,n.name);
	}

	@Override
	public void visitConstDecl(ConstDeclNode n) {
		Symbol sym = symbol(n.var.name);
		if(!CST.insert(sym))
			logErrors(Errors.DUPLICATE_SYMBOL,n.var.name);
		n.val.accept(this);
	}

	@Override
	public void visitNumLit(NumLitNode n) {
		if (exprType == Expr.LHS )
			logErrors(Errors.LHS_EXPR_ERROR,n.lit.toString());
	}

	@Override
	public void visitStrLit(StrLitNode n) {
		if (exprType == Expr.LHS)
			logErrors(Errors.LHS_EXPR_ERROR,n.lit);
	}

	@Override
	public void visitBoolLit(BoolLitNode n) {
		if (exprType == Expr.LHS)
			logErrors(Errors.LHS_EXPR_ERROR,String.valueOf(n.lit));
	}

	@Override
	public void visitFuncDecl(FuncDeclNode n) {
		Symbol sym = symbol(n.name.name);
		if(!CST.insert(sym))
			logErrors(Errors.DUPLICATE_SYMBOL,n.name.name);
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
		CST = new SymbolTable(CST);
		n.params.accept(this);
		n.block.accept(this);
		CST = CST.parent;
	}

	@Override
	public void visitListColl(ListCollNode n) {
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitSetColl(SetCollNode n) {
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitLinkColl(LinkCollNode n) {
		for (ExprNode expr : n.val)
			expr.accept(this);
	}

	@Override
	public void visitMapColl(MapCollNode n) {
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
			logErrors(Errors.LHS_EXPR_ERROR,"");
		}
	}

	@Override
	public void visitUnaryExpr(UnaryExprNode n) {
		if (exprType != Expr.LHS) {
			n.e.accept(this);
		} else {
			logErrors(Errors.LHS_EXPR_ERROR,"");
		}
	}

	@Override
	public void visitThis(ThisNode n) {
		if (exprType == Expr.LHS)
			logErrors(Errors.LHS_EXPR_ERROR,"this");
	}

	@Override
	public void visitNull(NullNode n) {
		if (exprType == Expr.LHS )
			logErrors(Errors.LHS_EXPR_ERROR,"null");
	}

	@Override
	public void visitFuncCall(FuncCallNode n) {
//		if (exprType == Expr.LHS && objType == ObjAcc.accesedOn){
//			logErrors(Errors.LHS_EXPR_ERROR,"");
//			return;
//		}
		if(objType != ObjAcc.prop) {
			n.invokedOn.accept(this);
		}
		n.args.accept(this);

	}

	@Override
	public void visitSubscript(SubscriptNode n) {
		if(objType == ObjAcc.accesedOn)
			n.subscriptOf.accept(this);
		n.index.accept(this);
	}

	@Override
	public void visitObjAccess(ObjectAccessNode n) {
		ObjectAccessNode node = n;
		Node.Tag tag = n.prop.getTag();
		if(exprType == Expr.LHS){
			tag = node.prop.getTag();
			if(tag != Node.Tag.IDEN && tag != Node.Tag.SUBSCRIPT){
				logErrors(Errors.LHS_EXPR_ERROR,"");
				return;
			}
			exprType = null;
		}


		objType = ObjAcc.accesedOn;
		n.accessedOn.accept(this);
		objType = ObjAcc.prop;
		if(n.prop.getTag() != Node.Tag.IDEN)
			n.prop.accept(this);
		objType = null;
	}

	@Override
	public void visitSlice(SliceNode n) {
		if(objType == ObjAcc.accesedOn)
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
		for(IdenNode iden : n.params) {
			Symbol sym = symbol(iden.name);
			if (!CST.insert(sym))
				logErrors(Errors.DUPLICATE_SYMBOL, iden.name);
		}
	}

	@Override
	public void visitImportStmt(ImportStmtNode n) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitAsgnStmt(AsgnStmtNode n) {
		exprType = Expr.LHS;
		n.lhs.accept(this);
		exprType = Expr.RHS;
		n.rhs.accept(this);
	}

	@Override
	public void visitIfStmt(IfStmtNode n) {
		n.ifCond.accept(this);

		CST = new SymbolTable(CST).notDeclarable();
		n.ifBlock.accept(this);
		CST = CST.parent;
		if (n.elsePart != null){
			CST = new SymbolTable(CST).notDeclarable();
			n.elsePart.accept(this);
			CST = CST.parent;
		}
	}

	@Override
	public void visitElseStmt(ElseStmtNode n) {
		n.elsePart.accept(this);
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

	private void logErrors(Errors err,String val){
		log.error(err,val);
	}

}
