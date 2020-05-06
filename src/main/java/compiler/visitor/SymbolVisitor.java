package compiler.visitor;

import compiler.Symbol;
import compiler.SymbolTable;
import compiler.SymbolTableMapper;
import compiler.ast.ArgsNode;
import compiler.ast.BinaryExprNode;
import compiler.ast.Block;
import compiler.ast.ElseStmtNode;
import compiler.ast.FuncCallNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.LiteralNode;
import compiler.ast.OperatorNode;
import compiler.ast.ParameterNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.utils.SymbolType;

public class SymbolVisitor implements Visitor {

	private SymbolTable globalSymbolTable;
	private SymbolTable currSymbolTable;

	public SymbolVisitor() {
		globalSymbolTable = SymbolTableMapper.globalSymbolTable();
		currSymbolTable = globalSymbolTable;
	}

	boolean checkSymbol(SymbolTable symTable, String n, SymbolType type) {
		if (symTable != null) {
			if (symTable.hasSymbol(n, type)) {
				return true;
			} else
				return checkSymbol(symTable.getParent(), n, type);
		}
		return false;
	}

	@Override
	public void visit(ProgramNode n) {
		FuncDeclNode mainFunc = n.getMainF();
		
		mainFunc.block.stmtL.forEach(e -> e.accept(this));;
	}

	@Override
	public void visit(FuncDeclNode n) {
		String name = n.nm.id;
		int args = n.argL.size();
		int id = n.block.hashCode();

		if (checkSymbol(globalSymbolTable, name, SymbolType.FUNC)) {
			int a = globalSymbolTable.getSymbol(name, SymbolType.FUNC).args;
			if (a != args) {
				Symbol symbol = new Symbol(name, SymbolType.FUNC, n, args);
				globalSymbolTable.addSymbol(symbol);
				n.block.accept(this);
			} else {
				System.out.println("Func " + name + "() already exists");

			}
		} else {
			Symbol symbol = new Symbol(name, SymbolType.FUNC, n, args);
			globalSymbolTable.addSymbol(symbol);
			n.block.accept(this);
		}
	}

	@Override
	public void visit(VarDeclNode n) {

		String name = n.nm.id;

		if (!checkSymbol(currSymbolTable, name, SymbolType.VAR)) {
			Symbol symbol = new Symbol(name, SymbolType.VAR, n, null);
			currSymbolTable.addSymbol(symbol);
		}
		else {
			System.out.println("Var "+name+" already exists");
		}
	}

	@Override
	public void visit(VarDeclWithAsgnNode n) {
		String name = n.nm.nm.id;

		if (!checkSymbol(currSymbolTable, name, SymbolType.VAR)) {
			Symbol symbol = new Symbol(name, SymbolType.VAR, n, null);
			currSymbolTable.addSymbol(symbol);
		}
		else {
			System.out.println("Var "+name+" already exists");
		}
	}

	@Override
	public <T> void visit(ArgsNode<T> n) {
		if (n.arg instanceof IdentifierNode) {
			IdentifierNode iden = (IdentifierNode) n.arg;
			iden.accept(this);
		}
	}

	@Override
	public void visit(ReturnStmtNode n) {
		n.expr.accept(this);

	}

	@Override
	public void visit(LiteralNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BinaryExprNode n) {
		n.expr1.accept(this);
		n.expr2.accept(this);

	}

	@Override
	public void visit(UnaryExprNode n) {
		n.expr.accept(this);

	}

	@Override
	public void visit(FuncCallNode n) {
		if (checkSymbol(globalSymbolTable, n.iden.id, SymbolType.FUNC)) {
			n.argsL.forEach(e -> e.accept(this));
		} else {
			System.out.println("No such function");
		}
	}

	@Override
	public void visit(IdentifierNode n) {
		if (!checkSymbol(currSymbolTable, n.id, SymbolType.VAR)) {
			System.out.println("Var Not found");
		}
	}

	@Override
	public void visit(OperatorNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStmtNode n) {
		n.cond.accept(this);
		n.block.accept(this);
	}

	@Override
	public void visit(ElseStmtNode n) {
		n.block.accept(this);

	}

	@Override
	public void visit(IfElseStmtNode n) {
		n.ifCond.accept(this);
		n.ifNode.accept(this);
		n.elseNode.accept(this);

	}

	@Override
	public void visit(ParameterNode n) {
		String name = n.n.id;
		Symbol symbol = new Symbol(name, SymbolType.FUNC, n, null);
		currSymbolTable.addSymbol(symbol);
	}

	@Override
	public void visit(Block n) {
		SymbolTable table = new SymbolTable();
		table.setParent(currSymbolTable);
		SymbolTableMapper.addSymbolTable(n.hashCode(), table);
		currSymbolTable = table;

		n.stmtL.forEach(e -> e.accept(this));

		if (currSymbolTable != globalSymbolTable)
			currSymbolTable = currSymbolTable.getParent();
	}

//	private SymbolTable globalSymbolTable;
//
//	private SymbolTable currSymTable;
//	private Node currBlock;
//	private Scope scope;
//	private Symbol funcSym;
//
//	private Map<Node, SymbolTable> mapper;
//	
//	public SymbolVisitor() {
//		mapper = new HashMap<>();
//		globalSymbolTable = SymbolTableMapper.globalSymbolTable();
//	}
//
//	void genSymbolTableMapping() {
//		mapper.put(currBlock, currSymTable);
//	}
//
//	boolean checkSymbol(SymbolTable symTable, IdentifierNode n, SymbolType type) {
//		if (symTable != null) {
//			if (symTable.hasSymbol(n.id, type)) {
//				return true;
//			} else
//				return checkSymbol(symTable.getParent(), n, type);
//		}
//		return false;
//	}
//
//	@Override
//	public void visit(ProgramNode n) {
//
//		// Init mapper with all known functions and their empty symbol tables
//
//		List<FuncDeclNode> funcL = n.getFuncList();
//		SymbolTable table;
//		
//		for (int i = 0; i < funcL.size(); i++) {
//			String funcName = funcL.get(i).nm.id;
//			int funcArgsSize = funcL.get(i).argL.size();
//			int id = funcL.get(i).block.hashCode();
//			
//			table = new SymbolTable();
//			Symbol symbol = new Symbol(funcName, SymbolType.FUNC, Scope.GLOBAL, funcL.get(i), funcArgsSize);
//			if (globalSymbolTable.addSymbol(symbol)) {
//				SymbolTableMapper.addSymbolTable(id, table);
//				mapper.put(funcL.get(i), table);
//			} else {
//				System.out.println("Method " + funcName + "() already exists");
//				System.exit(0);
//			}
//		}
//
//		// Iterate over each stmt
//		// if its a var-decl, add it to symbol table of enclosing scope,
//		// else check if the given iden is present in the scope
//
//		StmtNodeList stmtL = n.getMainF().block.stmtL;
//
//		currBlock = n;
//		currSymTable = globalSymbolTable;
//		scope = Scope.GLOBAL;
//
//		for (int i = 0; i < stmtL.size(); i++) {
//			stmtL.elementAt(i).accept(this);
//			currBlock = n;
//			currSymTable = globalSymbolTable;
//			scope = Scope.GLOBAL;
//		}
//
//		genSymbolTableMapping();
//		SymbolTableMapper.print();
//	}
//
//	@Override
//	public void visit(FuncDeclNode n) {
//
//		// If func has return type, it will be set when ReturnStmtNode is visited
//		// Type.NULL for func means a void method, which can be overridden if return
//		// stmt is visited
//
//		funcSym = globalSymbolTable.getSymbol(n.nm.id, SymbolType.FUNC);
//
//		//StmtNodeList stmtL = n.block.stmtL;
//		ArgsNodeList argL = n.argL;
//
//		SymbolTable temp;
//
//		// Retrieve symbol table
//		currSymTable = mapper.get(n);
//		temp = currSymTable;
//		currSymTable.setParent(globalSymbolTable);
//
//		currBlock = n;
//		scope = Scope.LOCAL;
//
//		for (int i = 0; i < argL.size(); i++) {
//			argL.elementAt(i).accept(this);
//		}
//
//		for (int i = 0; i < stmtL.size(); i++) {
//			stmtL.elementAt(i).accept(this);
//
//			// Make sure the symbol table,block and scope are reset, when nested stmts
//			// return.
//			currBlock = n;
//			currSymTable = temp;
//			scope = Scope.LOCAL;
//		}
//
//		genSymbolTableMapping();
//
//		// Assuming return stmt will fill the returnType of func node (if not a void
//		// func)
//		 globalSymbolTable.addSymbol(funcSym);
//	}
//
//	@Override
//	public void visit(VarDeclNode n) {
//		SymbolType symType = SymbolType.VAR;
//
//		Symbol sym = new Symbol(n.nm.id, symType, scope, n, null);
//		if (!currSymTable.addSymbol(sym)) {
//			System.out.println("Var " + n.nm.id + " already exists");
//		}
//	}
//
//	@Override
//	public void visit(VarDeclWithAsgnNode n) {
//		SymbolType symType = SymbolType.VAR;
//
//		Symbol sym = new Symbol(n.nm.nm.id, symType, scope, n, null);
//		if (!currSymTable.addSymbol(sym)) {
//			System.out.println("Var " + n.nm.nm.id + " already exists");
//		}
//		n.val.accept(this);
//	}
//
//	@Override
//	public <T> void visit(ArgsNode<T> n) {
//		if (n.arg instanceof IdentifierNode) {
//			IdentifierNode arg = (IdentifierNode) n.arg;
//			Symbol sym = new Symbol(arg.id, SymbolType.VAR, scope, n, null);
//			currSymTable.addSymbol(sym);
//		}
//
//	}
//
//	@Override
//	public void visit(ReturnStmtNode n) {
//		n.expr.accept(this);
//	}
//
//	@Override
//	public void visit(IntegerLiteralNode n) {
//
//	}
//
//	@Override
//	public void visit(BinaryExprNode n) {
//		n.expr1.accept(this);
//		n.expr2.accept(this);
//	}
//
//	@Override
//	public void visit(UnaryExprNode n) {
//		n.expr.accept(this);
//	}
//
//	@Override
//	public void visit(FuncCallNode n) {
//		Symbol sym;
//
//		if ((sym = globalSymbolTable.getSymbol(n.iden.id, SymbolType.FUNC)) != null) {
//			// sym.print();
//			if (n.argsL.size() == sym.args) {
//				for (int i = 0; i < n.argsL.size(); i++) {
//					ArgsNode<?> arg = n.argsL.elementAt(i);
//					if (arg.arg instanceof IdentifierNode) {
//						if (!checkSymbol(currSymTable, (IdentifierNode) arg.arg, SymbolType.VAR)) {
//							System.out.println("Unknown Arg Var: " + arg.arg);
//						}
//					}
//				}
//			} else {
//				System.out.println("Invalid number of args for func: " + n.iden.id);
//			}
//
//		}
//
//	}
//
//	@Override
//	public void visit(IdentifierNode n) {
//		if (!checkSymbol(currSymTable, n, SymbolType.VAR)) {
//			System.out.println("Unknown iden: " + n.id);
//		}
//	}
//
//	@Override
//	public void visit(OperatorNode n) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void visit(IfStmtNode n) {
//		StmtNodeList stmtL = n.stmtL;
//		n.cond.accept(this);
//
//		// New symbol table for if stmt and swap tables for setting parent table
//		SymbolTable tb = new SymbolTable();
//		tb.setParent(currSymTable);
//		currSymTable = tb;
//
//		currBlock = n;
//		scope = Scope.NESTED_LOCAL;
//
//		for (int i = 0; i < stmtL.size(); i++) {
//			stmtL.elementAt(i).accept(this);
//			currBlock = n;
//			currSymTable = tb;
//			scope = Scope.NESTED_LOCAL;
//		}
//
//		genSymbolTableMapping();
//	}
//
//	@Override
//	public void visit(ElseStmtNode n) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void visit(IfElseStmtNode n) {
//		StmtNodeList stmtL = n.ifStmtL;
//		n.ifCond.accept(this);
//
//		SymbolTable parent = currSymTable;
//
//		// New symbol table for IF stmt and swap tables for setting parent table
//		SymbolTable tb = new SymbolTable();
//		tb.setParent(parent);
//		currSymTable = tb;
//
//		currBlock = n.ifNode;
//		scope = Scope.NESTED_LOCAL;
//
//		for (int i = 0; i < stmtL.size(); i++) {
//			stmtL.elementAt(i).accept(this);
//			currBlock = n.ifNode;
//			currSymTable = tb;
//			scope = Scope.NESTED_LOCAL;
//		}
//
//		genSymbolTableMapping();
//
//		stmtL = n.elseStmtL;
//
//		// New symbol table for ELSE stmt and swap tables for setting parent table
//		tb = new SymbolTable();
//		tb.setParent(parent);
//		currSymTable = tb;
//
//		currBlock = n.elseNode;
//		scope = Scope.NESTED_LOCAL;
//
//		for (int i = 0; i < stmtL.size(); i++) {
//			stmtL.elementAt(i).accept(this);
//			currBlock = n.elseNode;
//			currSymTable = tb;
//			scope = Scope.NESTED_LOCAL;
//		}
//
//		genSymbolTableMapping();
//
//	}
//	
//	@Override
//	public void visit(Block n) {
//		// TODO Auto-generated method stub
//		
//	}
}
