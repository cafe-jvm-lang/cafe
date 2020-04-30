package compiler.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import compiler.Symbol;
import compiler.SymbolTable;
import compiler.SymbolTableMapper;
import compiler.ast.ArgsNode;
import compiler.ast.ArgsNodeList;
import compiler.ast.BinaryExprNode;
import compiler.ast.ElseStmtNode;
import compiler.ast.FuncCallNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.IntegerLiteralNode;
import compiler.ast.Node;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.utils.Scope;
import compiler.utils.SymbolType;
import compiler.utils.Type;

public class SymbolVisitor implements Visitor {
	private SymbolTable globalSymbolTable;

	private SymbolTable currSymTable;
	private Node currBlock;
	private Scope scope;
	private Symbol funcSym;

	private Map<Node, SymbolTable> mapper;

	public SymbolVisitor(SymbolTableMapper symbolTableMapper) {
		mapper = new HashMap<>();
		globalSymbolTable = SymbolTableMapper.globalSymbolTable;

	}

	void genSymbolTableMapping() {
		mapper.put(currBlock, currSymTable);
	}

	boolean checkSymbol(SymbolTable symTable, IdentifierNode n, SymbolType type) {
		if (symTable != null) {
			if (symTable.hasSymbol(n, type)) {
				return true;
			}
			else
				return checkSymbol(symTable.getParent(), n, type);
		}
		return false;
	}

	@Override
	public void visit(ProgramNode n) {

		// Init mapper with all known functions and their empty symbol tables

		List<FuncDeclNode> funcL = n.getFuncList();

		for (int i = 0; i < funcL.size(); i++) {
			SymbolTable table = new SymbolTable();
			
			SymbolTableMapper.addSymbolTable(funcL.get(i).nm.id, table);
			mapper.put(funcL.get(i), table);
			if (!globalSymbolTable.addSymbol(new Symbol(funcL.get(i).nm, SymbolType.FUNC, Scope.GLOBAL, null,
					funcL.get(i), funcL.get(i).argL.size()))) {
				System.out.println("Method " + funcL.get(i).nm.id + "() already exists");
			}
		}

		// Iterate over each stmt
		// if its a var-decl, add it to symbol table of enclosing scope,
		// else check if the given iden is present in the scope

		StmtNodeList stmtL = n.getMainF().stmtL;

		currBlock = n;
		currSymTable = globalSymbolTable;
		scope = Scope.GLOBAL;

		for (int i = 0; i < stmtL.size(); i++) {
			stmtL.elementAt(i).accept(this);
			currBlock = n;
			currSymTable = globalSymbolTable;
			scope = Scope.GLOBAL;
		}

		genSymbolTableMapping();
	}

	@Override
	public void visit(FuncDeclNode n) {

		// If func has return type, it will be set when ReturnStmtNode is visited
		// Type.NULL for func means a void method, which can be overridden if return
		// stmt is visited

		funcSym = globalSymbolTable.getSymbol(n.nm, SymbolType.FUNC);
		
		StmtNodeList stmtL = n.stmtL;
		ArgsNodeList argL = n.argL;

		SymbolTable temp;

		// Retrieve symbol table
		currSymTable = mapper.get(n);
		temp = currSymTable;
		currSymTable.setParent(globalSymbolTable);

		currBlock = n;
		scope = Scope.LOCAL;

		for (int i = 0; i < argL.size(); i++) {
			argL.elementAt(i).accept(this);
		}

		for (int i = 0; i < stmtL.size(); i++) {
			stmtL.elementAt(i).accept(this);

			// Make sure the symbol table,block and scope are reset, when nested stmts
			// return.
			currBlock = n;
			currSymTable = temp;
			scope = Scope.LOCAL;
		}

		genSymbolTableMapping();

		// Assuming return stmt will fill the returnType of func node (if not a void
		// func)
		// globalSymbolTable.addSymbol(funcSym);
	}

	@Override
	public void visit(VarDeclNode n) {
		SymbolType symType = SymbolType.VAR;
		Type type = Type.NULL;

		Symbol sym = new Symbol(n.nm, symType, scope, type, n, null);
		if (!currSymTable.addSymbol(sym)) {
			System.out.println("Var " + n.nm.id + " already exists");
		}
	}

	@Override
	public void visit(VarDeclWithAsgnNode n) {
		SymbolType symType = SymbolType.VAR;

		// Expression Eval has to be added for determining types
		Type type = Type.INT;

		Symbol sym = new Symbol(n.nm.nm, symType, scope, type, n, null);
		if (!currSymTable.addSymbol(sym)) {
			System.out.println("Var " + n.nm.nm.id + " already exists");
		}
		n.val.accept(this);
	}

	@Override
	public <T> void visit(ArgsNode<T> n) {
		if (n.arg instanceof IdentifierNode) {
			Symbol sym = new Symbol((IdentifierNode) n.arg, SymbolType.VAR, scope, Type.NULL, n, null);
			currSymTable.addSymbol(sym);
		}

	}

	@Override
	public void visit(ReturnStmtNode n) {
		n.expr.accept(this);

		// Expression Eval has to be added for determining types
		// For now, assuming return type as int only.
		Type type = Type.INT;

		// Return type of enclosing function
		funcSym.setType(type);

	}

	@Override
	public void visit(IntegerLiteralNode n) {

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
		Symbol sym;

		if ((sym = globalSymbolTable.getSymbol(n.iden, SymbolType.FUNC)) != null) {
			sym.print();
			if (n.argsL.size() == sym.args) {
				for (int i = 0; i < n.argsL.size(); i++) {
					ArgsNode<?> arg = n.argsL.elementAt(i);
					if (arg.arg instanceof IdentifierNode) {
						if (!checkSymbol(currSymTable, (IdentifierNode) arg.arg, SymbolType.VAR)) {
							System.out.println("Unknown Arg Var: " + arg.arg);
						}
					}
				}
			} else {
				System.out.println("Invalid number of args for func: " + n.iden.id);
			}

		}

	}

	@Override
	public void visit(IdentifierNode n) {
		if (!checkSymbol(currSymTable, n, SymbolType.VAR)) {
			System.out.println("Unknown iden: " + n.id);
		}
	}

	@Override
	public void visit(OperatorNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStmtNode n) {
		StmtNodeList stmtL = n.stmtL;
		n.cond.accept(this);

		// New symbol table for if stmt and swap tables for setting parent table
		SymbolTable tb = new SymbolTable();
		tb.setParent(currSymTable);
		currSymTable = tb;

		currBlock = n;
		scope = Scope.NESTED_LOCAL;

		for (int i = 0; i < stmtL.size(); i++) {
			stmtL.elementAt(i).accept(this);
			currBlock = n;
			currSymTable = tb;
			scope = Scope.NESTED_LOCAL;
		}

		genSymbolTableMapping();
	}

	@Override
	public void visit(ElseStmtNode n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfElseStmtNode n) {
		StmtNodeList stmtL = n.ifStmtL;
		n.ifCond.accept(this);

		SymbolTable parent = currSymTable;

		// New symbol table for IF stmt and swap tables for setting parent table
		SymbolTable tb = new SymbolTable();
		tb.setParent(parent);
		currSymTable = tb;

		currBlock = n.ifNode;
		scope = Scope.NESTED_LOCAL;

		for (int i = 0; i < stmtL.size(); i++) {
			stmtL.elementAt(i).accept(this);
			currBlock = n.ifNode;
			currSymTable = tb;
			scope = Scope.NESTED_LOCAL;
		}

		genSymbolTableMapping();

		stmtL = n.elseStmtL;

		// New symbol table for ELSE stmt and swap tables for setting parent table
		tb = new SymbolTable();
		tb.setParent(parent);
		currSymTable = tb;

		currBlock = n.elseNode;
		scope = Scope.NESTED_LOCAL;

		for (int i = 0; i < stmtL.size(); i++) {
			stmtL.elementAt(i).accept(this);
			currBlock = n.elseNode;
			currSymTable = tb;
			scope = Scope.NESTED_LOCAL;
		}

		genSymbolTableMapping();

	}
}
