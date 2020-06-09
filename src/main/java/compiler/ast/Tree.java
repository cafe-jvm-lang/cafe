package compiler.ast;

/**
 * The base contract for all tree nodes in AST.
 * 
 * @author Dhyey
 *
 */
public interface Tree {
	void accept(TreeVisitor v);
}
