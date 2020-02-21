package compiler.ast;

public interface NodeCreator<T> {
	T getParentNode();
	void setParentNode(T node);

}
