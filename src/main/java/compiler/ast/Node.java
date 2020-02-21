package compiler.ast;

public abstract class Node implements NodeCreator<Node>{
	
	private Node parentNode;
	
	@Override
	public Node getParentNode() {
		return parentNode;
	}
	
	@Override
	public void setParentNode(Node node) {
		parentNode = node;
	}
}
