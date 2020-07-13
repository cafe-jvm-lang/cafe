package compiler.ast;

import static compiler.ast.Node.Tag.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Node {

	public abstract Tag getTag();

	public abstract void accept(Visitor v);

	enum Tag {
		VARDECL, IDEN, CONSTDECL, NUMLIT, STRLIT, BOOLLIT, FUNCDECL, OBJCREATION, BLOCK, ANNFUNC, 
		LIST,SET,LINKEDLIST,MAP;
	}

	public static abstract class StmtNode extends Node {
		
	}

	public static abstract class ExprNode extends StmtNode {

	}

	public static abstract class DeclNode extends StmtNode {

	}

	public static class IdenNode extends ExprNode {
		public String name;
		public IdenNode(String n) {
			name = n;
		}
		@Override
		public Tag getTag() {
			return IDEN;
		}
		@Override
		public void accept(Visitor v) {
			v.visitIden(this);
		}
	}

	public static class NumLitNode extends ExprNode{
		public Number val;
		public NumLitNode(Number v) {
			val = v;
		}
		@Override
		public Tag getTag() {
			return NUMLIT;
		}
		@Override
		public void accept(Visitor v) {
			v.visitNumLit(this);
		}
	}
	
	public static class StrLitNode extends ExprNode{
		public String lit;
		public StrLitNode(String v) {
			this.lit = v;
		}
		@Override
		public Tag getTag() {
			return STRLIT;
		}
		@Override
		public void accept(Visitor v) {
			v.visitStrLit(this);
		}
	}
	
	public static class BoolLitNode extends ExprNode{
		public boolean lit;
		public BoolLitNode(boolean l) {
			this.lit = l;
		}
		@Override
		public Tag getTag() {
			return BOOLLIT;
		}
		@Override
		public void accept(Visitor v) {
			v.visitBoolLit(this);	
		}
	}
	
	public static class ObjCreationNode extends ExprNode{
		public Map<IdenNode, ExprNode> prop = new HashMap<>();
		public ObjCreationNode() {}
		public ObjCreationNode(Map<IdenNode,ExprNode> m) { prop = m; }
		public void addProp(IdenNode n, ExprNode e) {
			prop.put(n, e);
		}
		public void setProp(Map<IdenNode,ExprNode> m) { prop = m; }
		@Override
		public Tag getTag() {
			return OBJCREATION;
		}
		@Override
		public void accept(Visitor v) {
			v.visitObjCreation(this);
		}
		
	}
	
	public static class AnnFuncNode extends ExprNode{
		public ArgsListNode args;
		public BlockNode block;
		public AnnFuncNode(ArgsListNode a,BlockNode b) {
			args = a;
			block = b;
		}
		@Override
		public Tag getTag() {
			return ANNFUNC;
		}
		@Override
		public void accept(Visitor v) {
			v.visitAnnFunc(this);
		}	
	}
	
	static abstract class ListTypeCollNode extends ExprNode{
		public List<ExprNode> val = new ArrayList<>();
		public void addToColl(ExprNode n) {
			val.add(n);
		}
		public void setColl(List<ExprNode> l) { val = l; }
	}
	
	public static class ListCollNode extends ListTypeCollNode{
		public ListCollNode() {}
		public ListCollNode(List<ExprNode> l) { val = l; }
		@Override
		public Tag getTag() {
			return LIST;
		}
		@Override
		public void accept(Visitor v) {
			v.visitListColl(this);
		}
	}
	
	public static class SetCollNode extends ListTypeCollNode{
		public SetCollNode() {}
		public SetCollNode(List<ExprNode> l) { val = l; }
		@Override
		public Tag getTag() {
			return SET;
		}
		@Override
		public void accept(Visitor v) {
			v.visitSetColl(this);
		}
	}
	
	public static class LinkCollNode extends ListTypeCollNode{
		public LinkCollNode() {}
		public LinkCollNode(List<ExprNode> l) { val = l; }
		@Override
		public Tag getTag() {
			return LINKEDLIST;
		}
		@Override
		public void accept(Visitor v) {
			v.visitLinkColl(this);
		}
	}
	
	public static class MapCollNode extends ExprNode{
		public Map<ExprNode,ExprNode> pairs = new HashMap<>();
		public MapCollNode() {}
		public MapCollNode(Map<ExprNode,ExprNode> m) { pairs = m; }
		public void addPair(ExprNode n1,ExprNode n2) {
			pairs.put(n1, n2);
		}
		@Override
		public Tag getTag() {
			return MAP;
		}
		@Override
		public void accept(Visitor v) {
			v.visitMapColl(this);
		}
	}
	
	public static class BinaryExprNode extends ExprNode{
		public ExprNode e1;
		public ExprNode e2;
		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class UnaryExprNode extends ExprNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ThisNode extends ExprNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class NullNode extends ExprNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/** 
	 *  
	 */
	public static class FuncCallNode extends ExprNode{
/*		
			Ex		| invoked-on | args
		   sum(5,x)	|	sum		 | (5,x);
		   a[2](10)	|	a[5]	 | (10);
*/		
		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class SubscriptNode extends ExprNode{
/*		
		Ex		|subscript-on| subscript-index
	   sum()[5]	|	sum()	 | [5];
	   a[2]		|	a		 | [2];
*/
		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ObjectAccessNode extends ExprNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class SliceNode extends ExprNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ArgsListNode extends Node{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class VarDeclNode extends DeclNode {

		@Override
		public Tag getTag() {
			return VARDECL;
		}
		
		@Override
		public void accept(Visitor v) {
			v.visitVarDecl(this);
		}

	}

	public static class ConstDeclNode extends DeclNode{

		@Override
		public Tag getTag() {
			return CONSTDECL;
		}

		@Override
		public void accept(Visitor v) {
			v.visitConstDecl(this);
		}
		
	}
	
	public static class FuncDeclNode extends DeclNode{

		@Override
		public Tag getTag() {
			return FUNCDECL;
		}

		@Override
		public void accept(Visitor v) {
			v.visitFuncDecl(this);
		}
		
	}
	
	public static class ParameterListNode extends Node{

		@Override
		public Tag getTag() {
			return null;
		}

		@Override
		public void accept(Visitor v) {
			
		}
		
	}
	
	public static class ImportStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class AsgnStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static class IfStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ForStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class LoopStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ReturnStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class ContinueStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class BreakStmtNode extends StmtNode{

		@Override
		public Tag getTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void accept(Visitor v) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class BlockNode extends Node{
		List<StmtNode> block = new ArrayList<>();
		public void addStmt(StmtNode n) {
			block.add(n);
		}
		@Override
		public Tag getTag() {
			return BLOCK;
		}
		@Override
		public void accept(Visitor v) {
			v.visitBlock(this);
		}
	}
	
	public interface Visitor {
		void visitVarDecl(VarDeclNode n);

		void visitIden(IdenNode n);
		
		void visitConstDecl(ConstDeclNode n);
		
		void visitNumLit(NumLitNode n);
		
		void visitStrLit(StrLitNode n);
		
		void visitBoolLit(BoolLitNode n);
		
		void visitFuncDecl(FuncDeclNode n);
		
		void visitObjCreation(ObjCreationNode n);
		
		void visitBlock(BlockNode n);
		
		void visitAnnFunc(AnnFuncNode n);
		
		void visitListColl(ListCollNode n);
		
		void visitSetColl(SetCollNode n);
		
		void visitLinkColl(LinkCollNode n);
		
		void visitMapColl(MapCollNode n);
	}
}
