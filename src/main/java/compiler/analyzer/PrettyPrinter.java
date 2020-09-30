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

public class PrettyPrinter implements Node.Visitor{

	int tabs = 0;

	private void addTabs(){
		for(int i=0;i<tabs;i++)
			System.out.print("  ");
	}

	private void printWithTabs(String msg){
		addTabs();
		System.out.println(msg);
	}

	private void addBeautify(String msg,Node n){
		addTabs();
		tabs++;
		System.out.println(msg);
		if(n!=null)
			n.accept(this);
		tabs--;
	}

	public void prettyPrint(Node n) {
		n.accept(this);
	}

	@Override
	public void visitProgram(ProgramNode n) {
		List<StmtNode> stmts = n.stmts;
		
		for(StmtNode stmt: stmts){
			stmt.accept(this);
		}
	}
	
	@Override
	public void visitVarDecl(VarDeclNode n) {
		printWithTabs("VarDeclNode");
		tabs++;
		addBeautify("var", n.var);
		addBeautify("value", n.value);
		tabs--;
	}

	@Override
	public void visitIden(IdenNode n) {
		printWithTabs("IdenNode: "+n.name);
	}

	@Override
	public void visitConstDecl(ConstDeclNode n) {
		printWithTabs("ConstDeclNode");
		tabs++;

		addBeautify("variable-name", n.var);
		addBeautify("value", n.val);

		tabs--;
	}

	@Override
	public void visitNumLit(NumLitNode n) {
		printWithTabs("NumLitNode: "+n.lit);
	}

	@Override
	public void visitStrLit(StrLitNode n) {
		printWithTabs("StrLitNode: "+n.lit);
	}

	@Override
	public void visitBoolLit(BoolLitNode n) {
		printWithTabs("BoolLitNode: "+n.lit);
	}

	@Override
	public void visitFuncDecl(FuncDeclNode n) {
		printWithTabs("FuncDeclNode");
		tabs++;

		addBeautify("func-name", n.name);
		addBeautify("parameter-list", n.params);
		addBeautify("func-block", n.block);
		
		tabs--;
	}

	@Override
	public void visitObjCreation(ObjCreationNode n) {
		printWithTabs("ObjCreationNode");
		for(Map.Entry<IdenNode, ExprNode> entry : n.prop.entrySet()) {
			addBeautify("key", entry.getKey());
			addBeautify("->", entry.getValue());
		}
	}

	@Override
	public void visitBlock(BlockNode n) {
		printWithTabs("BlockNode");
		tabs++;
		for(StmtNode node : n.block)
			addBeautify("", node);
		tabs--;
	}

	@Override
	public void visitAnnFunc(AnnFuncNode n) {
		printWithTabs("AnnFuncNode");
		tabs++;
		addBeautify("args", n.params);
		n.params.accept(this);
		n.block.accept(this);
		tabs--;
	}

	@Override
	public void visitListColl(ListCollNode n) {
		printWithTabs("ListCollNode:");
		tabs++;
		for(ExprNode node: n.val)
			addBeautify("#val", node);
		tabs--;
	}

	@Override
	public void visitSetColl(SetCollNode n) {
		printWithTabs("SetCollNode:");
		tabs++;
		for(ExprNode node: n.val)
			addBeautify("#val", node);
		tabs--;
	}

	@Override
	public void visitLinkColl(LinkCollNode n) {
		printWithTabs("LinkCollNode:");

		tabs++;
		for(ExprNode node: n.val)
			addBeautify("#val", node);
		tabs--;
	}

	@Override
	public void visitMapColl(MapCollNode n) {
		printWithTabs("MapCollNode:");
		tabs++;
		for(Map.Entry<ExprNode, ExprNode> entry : n.pairs.entrySet()) {
			addBeautify("#key", entry.getKey());
			addBeautify("->value", entry.getValue());
		}
		tabs--;
	}

	@Override
	public void visitBinaryExpr(BinaryExprNode n) {
		printWithTabs("BinaryExprNode");
		tabs++;
		printWithTabs("Operator: "+n.op);
		addBeautify("e1", n.e1);
		addBeautify("e2", n.e2);
		
		tabs--;
	}

	@Override
	public void visitUnaryExpr(UnaryExprNode n) {
		printWithTabs("UnaryExprNode");
		tabs++;
		printWithTabs("\t Operator"+n.op);
		addBeautify("e", n.e);
		tabs--;
	}

	@Override
	public void visitThis(ThisNode n) {
		printWithTabs("ThisNode");
	}

	@Override
	public void visitNull(NullNode n) {
		printWithTabs("NullNode");
	}

	@Override
	public void visitFuncCall(FuncCallNode n) {
		printWithTabs("FuncCallNode");
		tabs++;
		addBeautify("invoked-on", n.invokedOn);
		addBeautify("params", n.args);
		
		tabs--;
	}

	@Override
	public void visitSubscript(SubscriptNode n) {
		printWithTabs("SubscriptNode");
		tabs++;

		addBeautify("subscript-of", n.subscriptOf);
		addBeautify("index", n.index);
		
		tabs--;
	}

	@Override
	public void visitObjAccess(ObjectAccessNode n) {
		printWithTabs("ObjectAccessNode");
		tabs++;

		addBeautify("AccessedOn", n.accessedOn);
		addBeautify("Property", n.prop);

		tabs--;
	}

	@Override
	public void visitSlice(SliceNode n) {
		printWithTabs("SliceNode");
		tabs++;

		addBeautify("sliced-on", n.slicedOn);
		addBeautify("start-index", n.start);
		addBeautify("end-index", n.end);

		tabs--;
	}

	@Override
	public void visitArgsList(ArgsListNode n) {
		printWithTabs("ArgsListNode");
		tabs++;
		for(ExprNode arg: n.args){
			addBeautify("#argument", arg);
		}	

		tabs--;
	}

	@Override
	public void visitParamList(ParameterListNode n) {
		printWithTabs("ParameterListNode");
		tabs++;

		for(ExprNode param: n.params){
			addBeautify("#parameter", param);
		}	

		tabs--;
	}

	@Override
	public void visitImportStmt(ImportStmtNode n) {
		// TODO Not Supported yet
		
	}

	@Override
	public void visitAsgnStmt(AsgnStmtNode n) {
		printWithTabs("AsgnStmtNode");
		tabs++;

		addBeautify("lhs", n.lhs);
		addBeautify("rhs", n.rhs);

		tabs--;
	}

	@Override
	public void visitIfStmt(IfStmtNode n) {
		printWithTabs("IfStmtNode");
		tabs++;

		addBeautify("if-condition", n.ifCond);
		addBeautify("if-block", n.ifBlock);
		if(n.elsePart != null)
			n.elsePart.accept(this);

		tabs--;
	}

	@Override
	public void visitElseStmt(ElseStmtNode n) {
		printWithTabs("ElseStmtNode");
		tabs++;
		addBeautify("else-block", n.elsePart);
		tabs--;
	}

	@Override
	public void visitForStmt(ForStmtNode n) {
		printWithTabs("ForStmtNode");

		tabs++;
		if(n.init != null)
			for(StmtNode node: n.init)
				addBeautify("#init", node);

		n.cond.accept(this);

		if(n.counters != null)
			for(StmtNode node: n.counters)
				addBeautify("#init", node);
		addBeautify("for-block", n.block);
		
		tabs--;
	}

	@Override
	public void visitLoopStmt(LoopStmtNode n) {
		printWithTabs("LoopStmtNode");

		tabs++;
		addBeautify("#var 1", n.var1);
		addBeautify("#var 2", n.var2);
		addBeautify("collection", n.collection);
		addBeautify("loop-block", n.block);
		tabs--;
	}

	@Override
	public void visitReturnStmt(ReturnStmtNode n) {
		printWithTabs("ReturnStmtNode");

		tabs++;
		addBeautify("return-expr", n.expr);
		tabs--;
	}

	@Override
	public void visitContinueStmt(ContinueStmtNode n) {
		printWithTabs("ContinueStmtNode");
	}

	@Override
	public void visitBreakStmt(BreakStmtNode n) {
		printWithTabs("BreakStmtNode");
	}

	@Override
	public void visitListComp(ListCompNode n) {
		printWithTabs("ListCompNode");
		tabs++;

		for(CompNode node: n.nested){
			addBeautify("#comp", node);
		}

		tabs--;
	}

	@Override
	public void visitLinkComp(LinkCompNode n) {
		printWithTabs("LinkCompNode");
		tabs++;

		for(CompNode node: n.nested){
			addBeautify("#comp", node);
		}
		
		tabs--;
		
	}

	@Override
	public void visitSetComp(SetCompNode n) {
		printWithTabs("SetCompNode");
		tabs++;

		for(CompNode node: n.nested){
			addBeautify("#comp", node);
		}
		
		tabs--;
		
	}

	@Override
	public void visitMapComp(MapCompNode n) {
		printWithTabs("MapCompNode");
		tabs++;

		for(CompNode node: n.nested){
			addBeautify("#comp", node);
		}
		
		tabs--;
	}

	@Override
	public void visitCompLoop(CompLoopNode n) {
		printWithTabs("CompLoopNode");

		tabs++;
		addBeautify("#var 1", n.var1);
		addBeautify("#var 2", n.var2);
		addBeautify("collection", n.collection);
		
		tabs--;
	}

	@Override
	public void visitCompIf(CompIfNode n) {
		printWithTabs("CompIfNode");

		tabs++;
		addBeautify("comp-if", n.ifCond);
		tabs--;
	}

	@Override
	public void visitListRange(RangeNode n) {
		printWithTabs("RangeNode");
		tabs++;

		printWithTabs("Type: "+n.type.toString());
		addBeautify("range-start", n.rangeStart);
		addBeautify("range-end", n.rangeEnd);

		tabs--;
	}

}
