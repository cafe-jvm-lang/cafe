package compiler.parser;

import java.util.List;

import compiler.ast.ArgsNode;
import compiler.ast.ArgsNodeList;
import compiler.ast.ExprNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.Node;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.StmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclNodeList;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.lexer.Token;
import compiler.lexer.tokentypes.TokenType;
import compiler.utils.Constants;
import compiler.utils.Scope;

public class Parser {

	private List<Token> tokenL;
	private Token currT;
	private static int curr = 0;

	private ProgramNode root;
	private FuncDeclNode mainF;

	private boolean error = false;

	public Parser(List<Token> tokenL) {
		this.tokenL = tokenL;

		root = new ProgramNode();
		// createMainFunc();
	}

	public Token getNextToken() {
		if (curr < tokenL.size()) {
			return tokenL.get(curr++);
		}
		System.out.println("Token list empty");
		return null;
	}

	public void error(String err) {
		error = true;
		System.out.println(err + "\n at ");
		currT.getPosition().print();

	}

	void createMainFunc() {
		IdentifierNode id1 = new IdentifierNode("main");
		ArgsNodeList args = null;
		StmtNodeList stmt = null;
		VarDeclNodeList var = null;

		mainF = new FuncDeclNode(id1, args, stmt, var);
	}

	boolean parseSemi() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_SEMI)
			return true;
		return false;
	}

	boolean parseLParen() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_LPAREN)
			return true;
		return false;
	}

	boolean parseRParen() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_RPAREN)
			return true;
		return false;
	}

	boolean parseLBrace() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_LBRACE)
			return true;
		return false;
	}

	boolean parseRBrace() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_RBRACE)
			return true;
		return false;
	}

	boolean parseComma() {
		if (currT.getTokenType() == TokenType.SepTokenType.SEP_COMMA)
			return true;
		return false;
	}

	Node parseIdentifier() {

		if (error)
			return null;

		String iden = currT.getTokenValue();
		if (currT.getTokenType() == TokenType.KWTokenType.TK_IDENTIFIER) {
			if (Constants.IDEN_PATTERN.matcher(iden).matches()) {
				return new IdentifierNode(iden);
			}
		}
		return null;
	}

	ArgsNodeList parseArgs() {

		if (error)
			return null;

		Node arg;
		ArgsNodeList argL = new ArgsNodeList();

		if (parseLParen()) {
			currT = getNextToken();

			if ((arg = parseIdentifier()) != null)
				argL.addElement((ArgsNode) arg);

			currT = getNextToken();
			while (!parseRParen()) {
				if (parseComma()) {
					currT = getNextToken();
					arg = parseIdentifier();
					if (arg != null) {
						argL.addElement((ArgsNode) arg);
					} else {
						if (!error)
						error("expected IDEN");
						return null;
					}
				} else {
					if (!error)
					error("',' expected");
					return null;
				}

				currT = getNextToken();
			}
			return argL;
		}

		arg = null;
		argL = null;
		return null;
	}

	Node parseAsgOp() {

		if (error)
			return null;

		if (currT.getTokenType() == TokenType.OpTokenType.OP_ASG) {
			return new OperatorNode(TokenType.OpTokenType.OP_ASG);
		}

		return null;
	}

	ExprNode parseExpr() {

		if (error)
			return null;

		return null;
	}

	StmtNode parseReturnStmt() {

		if (error)
			return null;

		return null;
	}

	Block<VarDeclNodeList, StmtNodeList> parseStmt(Scope scope) {

		if (error)
			return null;

		VarDeclNodeList varL = new VarDeclNodeList();
		StmtNodeList stmtL = new StmtNodeList();

		Node n;

		while (currT != null) {
			if (currT.getTokenType() == TokenType.KWTokenType.KW_FUNC) {
				if (scope == Scope.GLOBAL) {
					return new Block<>(varL, stmtL);
				} else {
					if (!error)
					error("Unexpected token 'func'");
					return null;
				}
			}

			if ((n = parseVarDecl()) != null) {
				varL.addElement((VarDeclNode) n);
			} else if ((n = parseExpr()) != null) {
				stmtL.addElement((ExprNode) n);
			} else if ((n = parseReturnStmt()) != null) {
				stmtL.addElement((StmtNode) n);
			} else {
				if (!error)
				error("Unexpected Token");
				break;
			}
		}

		return null;
	}

	Node parseVarDecl() {

		if (error)
			return null;

		if (!(currT.getTokenType() == TokenType.KWTokenType.KW_VAR))
			return null;

		Node id1;
		Node opAsg;
		Node e1;

		currT = getNextToken();

		id1 = parseIdentifier();
		if (id1 != null) {
			currT = getNextToken();
			if (parseSemi())
				return new VarDeclNode((IdentifierNode) id1);
			else {
				opAsg = parseAsgOp();
				if (opAsg != null) {
					e1 = parseExpr();
					if (e1 != null) {
						return new VarDeclWithAsgnNode((IdentifierNode) id1, (ExprNode) e1);
					} else {
						if (!error)
						error("Invalid Token");
					}
				} else {
					if (!error)
					error("Invalid TOken");
				}
			}

		} else {
			if (!error)
			error("Invaid IDEN");
		}

		id1 = null;
		opAsg = null;
		e1 = null;
		return null;
	}

	Node parseFuncDecl() {

		if (error)
			return null;

		if (!(currT.getTokenType() == TokenType.KWTokenType.KW_FUNC))
			return null;

		Node idM;
		ArgsNodeList argsM;
		StmtNodeList stmtM;
		VarDeclNodeList varM;
		Block<VarDeclNodeList, StmtNodeList> block;

		currT = getNextToken();

		idM = parseIdentifier();
		if (idM != null) {
			currT = getNextToken();
			if ((argsM = parseArgs()) != null) {
				currT = getNextToken();
				if (parseLBrace()) {
					currT = getNextToken();

					block = parseStmt(Scope.LOCAL);
					if (block != null) {
						stmtM = block.u;
						varM = block.t;
						if (parseRBrace()) {
							return new FuncDeclNode((IdentifierNode) idM, argsM, stmtM, varM);
						} else {
							if (!error)
							error("'}' expected");
						}
					}
				} else {
					if (!error)
					error("'{' expected");
				}
			} else {
				if (!error)
				error("invalid ARGS");
			}
		} else {
			if (!error)
			error("Invalid IDEN");
		}

		idM = null;
		argsM = null;
		stmtM = null;
		varM = null;
		block = null;
		return null;
	}

	public void parse() {

		currT = getNextToken();

		IdentifierNode idM = new IdentifierNode("main");
		ArgsNodeList argsM = null;
		StmtNodeList stmtM = new StmtNodeList();
		VarDeclNodeList varM = new VarDeclNodeList();
		Block<VarDeclNodeList, StmtNodeList> block;

		Node t1;
		while (currT != null) {
			if ((block = parseStmt(Scope.GLOBAL)) != null) {
				stmtM.addAll(block.u);
				varM.addAll(block.t);
			} else if ((t1 = parseFuncDecl()) != null) {
				root.addFuncDeclNode((FuncDeclNode) t1);
				currT = getNextToken();
			} else {
				if (!error)
					error("Some error");
				break;
			}
		}

		mainF = new FuncDeclNode(idM, argsM, stmtM, varM);
		root.setMainF(mainF);
	}
}
