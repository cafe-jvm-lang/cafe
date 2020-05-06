package compiler.ast1;

import compiler.visitor.Visitor;

public class ProgramNode implements Node {
	
	public String fileName;
	public Block main;
	
	
	
	public ProgramNode(String fileName, Block main) {
		this.fileName = fileName;
		this.main = main;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}
}
