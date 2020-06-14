package compiler.ast;

import static compiler.ast.Node.Tag.IDEN;
import static compiler.ast.Node.Tag.VARDECL;

public abstract class Node{
	
	public abstract Tag getTag();
	public abstract void accept(Visitor v);
	
	enum Tag {
		VARDECL,
		IDEN
	}

	public static abstract class StmtNode extends Node {
		
	}

	public static abstract class ExprNode extends StmtNode {

	}
	
	public static abstract class DeclNode extends StmtNode {

	}

	public static class IdenNode extends ExprNode{

		@Override
		public Tag getTag() {
			return IDEN;
		}

		@Override
		public void accept(Visitor v) {
			v.visitIdenNode(this);
		}
	
	}
	
	public static class VarDeclNode extends DeclNode{

		@Override
		public void accept(Visitor v) {
			v.visitVarDecl(this);
		}

		@Override
		public Tag getTag() {
			return VARDECL;
		}
		
	}
	
	public interface Visitor{
		void visitVarDecl(VarDeclNode n);
		void visitIdenNode(IdenNode n);
	}
}
