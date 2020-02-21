package compiler.ast;

import java.util.List;

public interface NodeCreator<T> {
	T getParentNode();
	void setParentNode(T node);

}
