package compiler.analyzer;

import java.util.Map;

import org.checkerframework.checker.units.qual.K;

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

public class SemanticsChecker implements Node.Visitor {
	protected static final Context.Key<SemanticsChecker> semanticsKey = new Context.Key<>();

	// Global Symbol Table
	private final SymbolTable GST;

	// Current Symbol Table
	private SymbolTable CST;

	private static enum Expr {
		LHS, RHS
	}

	private Expr exprType = null;

	// caller node
	private Node.Tag caller = null;
	
	private Log log;

	public static SemanticsChecker instance(Context context) {
		SemanticsChecker instance = context.get(semanticsKey);
		if(instance == null)
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

	}

	@Override
	public void visitConstDecl(ConstDeclNode n) {
		CST.insert(n.var);
		n.val.accept(this);
	}

	@Override
	public void visitNumLit(NumLitNode n) {
		
	}

	@Override
	public void visitStrLit(StrLitNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBoolLit(BoolLitNode n) {
		// TODO Auto-generated method stub

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
		for(Map.Entry<IdenNode, ExprNode> entry: n.prop.entrySet()) {
			entry.getValue().accept(this);
		}
	}

	@Override
	public void visitBlock(BlockNode n) {
		for(StmtNode stmt: n.block) {
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
		
	}

	@Override
	public void visitSetColl(SetCollNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLinkColl(LinkCollNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMapColl(MapCollNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBinaryExpr(BinaryExprNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitUnaryExpr(UnaryExprNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitThis(ThisNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitNull(NullNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFuncCall(FuncCallNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSubscript(SubscriptNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitObjAccess(ObjectAccessNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSlice(SliceNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitArgsList(ArgsListNode n) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void visitIfStmt(IfStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitElseStmt(ElseStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitForStmt(ForStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLoopStmt(LoopStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitReturnStmt(ReturnStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitContinueStmt(ContinueStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBreakStmt(BreakStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitListComp(ListCompNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLinkComp(LinkCompNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSetComp(SetCompNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMapComp(MapCompNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitCompLoop(CompLoopNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitCompIf(CompIfNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitListRange(RangeNode n) {
		// TODO Auto-generated method stub

	}

}
