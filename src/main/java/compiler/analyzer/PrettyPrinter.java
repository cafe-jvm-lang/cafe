package compiler.analyzer;

import java.util.List;
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

public class PrettyPrinter implements Node.Visitor{

	public void prettyPrint(Node n) {
		n.accept(this);
	}

	@Override
	public void visitProgram(ProgramNode n) {
		List<StmtNode> stmts = n.stmts;
		
		stmts.stream().forEach(e->e.accept(this));
	}
	
	@Override
	public void visitVarDecl(VarDeclNode n) {
		n.var.accept(this);
		n.value.accept(this);
	}

	@Override
	public void visitIden(IdenNode n) {
		System.out.println("IdenNode: "+n.name);
	}

	@Override
	public void visitConstDecl(ConstDeclNode n) {
		System.out.println("ConstDeclNode");
		n.var.accept(this);
		n.val.accept(this);
	}

	@Override
	public void visitNumLit(NumLitNode n) {
		System.out.println("NumLitNode: "+n.lit);
	}

	@Override
	public void visitStrLit(StrLitNode n) {
		System.out.println("StrLitNode: "+n.lit);
	}

	@Override
	public void visitBoolLit(BoolLitNode n) {
		System.out.println("BoolLitNode: "+n.lit);
	}

	@Override
	public void visitFuncDecl(FuncDeclNode n) {
		System.out.println("FuncDeclNode");
		n.name.accept(this);
		n.params.accept(this);
		n.block.accept(this);
	}

	@Override
	public void visitObjCreation(ObjCreationNode n) {
		System.out.println("ObjCreationNode");
		for(Map.Entry<IdenNode, ExprNode> entry : n.prop.entrySet()) {
			System.out.print("key:");
			entry.getKey().accept(this);
			System.out.print("value:");
			entry.getValue().accept(this);
		}
	}

	@Override
	public void visitBlock(BlockNode n) {
		System.out.println("BlockNode");
		n.block.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitAnnFunc(AnnFuncNode n) {
		System.out.println("AnnFuncNode");
		n.args.accept(this);
		n.block.accept(this);
	}

	@Override
	public void visitListColl(ListCollNode n) {
		System.out.println("ListCollNode:");
		n.val.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitSetColl(SetCollNode n) {
		System.out.println("SetCollNode:");
		n.val.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitLinkColl(LinkCollNode n) {
		System.out.println("LinkCollNode:");
		n.val.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitMapColl(MapCollNode n) {
		System.out.println("MapCollNode:");
		for(Map.Entry<ExprNode, ExprNode> entry : n.pairs.entrySet()) {
			System.out.print("key:");
			entry.getKey().accept(this);
			System.out.print("value:");
			entry.getValue().accept(this);
		}
	}

	@Override
	public void visitBinaryExpr(BinaryExprNode n) {
		System.out.println("BinaryExprNode");
		System.out.println("\t Operator"+n.op);
		n.e1.accept(this);
		n.e2.accept(this);
	}

	@Override
	public void visitUnaryExpr(UnaryExprNode n) {
		System.out.println("UnaryExprNode");
		System.out.println("\t Operator"+n.op);
		n.e.accept(this);
	}

	@Override
	public void visitThis(ThisNode n) {
		System.out.println("ThisNode");
	}

	@Override
	public void visitNull(NullNode n) {
		System.out.println("NullNode");
	}

	@Override
	public void visitFuncCall(FuncCallNode n) {
		System.out.println("FuncCallNode");
		n.invokedOn.accept(this);
		n.parmas.accept(this);
	}

	@Override
	public void visitSubscript(SubscriptNode n) {
		System.out.println("SubscriptNode");
		n.subscriptOf.accept(this);
		n.index.accept(this);
	}

	@Override
	public void visitObjAccess(ObjectAccessNode n) {
		System.out.println("ObjectAccessNode");
		n.accessedOn.accept(this);
		n.prop.accept(this);
	}

	@Override
	public void visitSlice(SliceNode n) {
		System.out.println("SliceNode");
		n.slicedOn.accept(this);
		n.start.accept(this);
		n.end.accept(this);
	}

	@Override
	public void visitArgsList(ArgsListNode n) {
		System.out.println("ArgsListNode");
		n.args.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitParamList(ParameterListNode n) {
		System.out.println("ParameterListNode");
		n.params.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitImportStmt(ImportStmtNode n) {
		// TODO Not Supported yet
		
	}

	@Override
	public void visitAsgnStmt(AsgnStmtNode n) {
		System.out.println("AsgnStmtNode");
		n.lhs.accept(this);
		n.rhs.accept(this);
	}

	@Override
	public void visitIfStmt(IfStmtNode n) {
		System.out.println("IfStmtNode");
		n.ifCond.accept(this);
		n.ifBlock.accept(this);
		n.elsePart.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitElseStmt(ElseStmtNode n) {
		System.out.println("ElseStmtNode");
		//n.elsePart.accept(this);
	}

	@Override
	public void visitForStmt(ForStmtNode n) {
		System.out.println("ForStmtNode");
		n.init.stream().forEach(e->e.accept(this));
		n.cond.accept(this);
		n.counters.stream().forEach(e->e.accept(this));
		n.block.accept(this);
	}

	@Override
	public void visitLoopStmt(LoopStmtNode n) {
		System.out.println("LoopStmtNode");
		n.var1.accept(this);
		n.var2.accept(this);
		n.collection.accept(this);
		n.block.accept(this);
		
	}

	@Override
	public void visitReturnStmt(ReturnStmtNode n) {
		System.out.println("ReturnStmtNode");
		n.expr.accept(this);
	}

	@Override
	public void visitContinueStmt(ContinueStmtNode n) {
		System.out.println("ContinueStmtNode");
	}

	@Override
	public void visitBreakStmt(BreakStmtNode n) {
		System.out.println("BreakStmtNode");
	}

	@Override
	public void visitListComp(ListCompNode n) {
		System.out.println("ListCompNode");
		n.nested.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitLinkComp(LinkCompNode n) {
		System.out.println("LinkCompNode");
		n.nested.stream().forEach(e->e.accept(this));
		
	}

	@Override
	public void visitSetComp(SetCompNode n) {
		System.out.println("SetCompNode");
		n.nested.stream().forEach(e->e.accept(this));
		
	}

	@Override
	public void visitMapComp(MapCompNode n) {
		System.out.println("MapCompNode");
		n.nested.stream().forEach(e->e.accept(this));
	}

	@Override
	public void visitCompLoop(CompLoopNode n) {
		System.out.println("CompLoopNode");
		n.var1.accept(this);
		n.var2.accept(this);
		n.collection.accept(this);
	}

	@Override
	public void visitCompIf(CompIfNode n) {
		System.out.println("CompIfNode");
		n.ifCond.accept(this);
	}

	@Override
	public void visitListRange(RangeNode n) {
		System.out.println("RangeNode");
		System.out.println(n.type);
		n.rangeStart.accept(this);
		n.rangeEnd.accept(this);
	}

}
