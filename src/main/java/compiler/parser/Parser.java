package compiler.parser;

import java.util.List;

import compiler.ast.ArgsNode;
import compiler.ast.ArgsNodeList;
import compiler.ast.BinaryExprNode;
import compiler.ast.ExprNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IntegerLiteral;
import compiler.ast.Node;
import compiler.ast.NodeWithVarDecl;
import compiler.ast.OperatorNode;
import compiler.ast.ProgramNode;
import compiler.ast.StmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclNodeList;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.lexer.Token;
import compiler.lexer.tokentypes.TokenType;

public class Parser {

	private List<Token> tokenL;
	protected static Token currT;
	private static int curr = 0;

	private ProgramNode root;
	private FuncDeclNode mainF;

	private boolean error = false;
	private boolean isNextToken = false;

	public Parser(List<Token> tokenL) {
		this.tokenL = tokenL;

		root = new ProgramNode();
		// createMainFunc();
	}

	final Token getNextToken() {
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
		System.out.println("\nToken:" + currT.getTokenValue());

	}

	void createMainFunc() {
		IdentifierNode id1 = new IdentifierNode("main");
		ArgsNodeList args = null;
		StmtNodeList stmt = null;
		VarDeclNodeList var = null;

		mainF = new FuncDeclNode(id1, args, stmt, var);
	}

	ExprNode parseIdentifier() {

		if (error)
			return null;

		String iden = currT.getTokenValue();
		if (Utility.isIdentifier()) {
			return new IdentifierNode(iden);

		}
		return null;
	}

	ArgsNodeList parseArgs() {

		if (error)
			return null;

		Node arg;
		ArgsNodeList argL = new ArgsNodeList();

		if (Utility.isLParen()) {
			currT = getNextToken();

			if ((arg = parseIdentifier()) != null) {
				argL.addElement((ArgsNode) arg);
				currT = getNextToken();
			}

			while (!Utility.isRParen()) {
				if (Utility.isComma()) {
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

	ExprNode parseAsgOp() {

		if (error)
			return null;

		if (Utility.isOpEq()) {
			return new OperatorNode(TokenType.OpTokenType.OP_ASG);
		}

		return null;
	}

	ExprNode parseLogOrOp() {
		if (error)
			return null;

		if (Utility.isOpLogOr()) {
			return new OperatorNode(TokenType.OpTokenType.OP_LOGOR);
		}

		return null;
	}

	ExprNode parseLogAndOp() {
		if (error)
			return null;

		if (Utility.isOpLogAnd()) {
			return new OperatorNode(TokenType.OpTokenType.OP_LOGAND);
		}

		return null;
	}

	ExprNode parseRelEqOp() {
		if (error)
			return null;

		if (Utility.isOpRelNE()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELNE);
		} else if (Utility.isOpRelEE()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELEE);
		}

		return null;
	}

	ExprNode parseRelOp() {
		if (error)
			return null;

		if (Utility.isOpRelLT()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELLT);
		} else if (Utility.isOpRelGT()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELGT);
		} else if (Utility.isOpRelLE()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELLE);
		} else if (Utility.isOpRelGE()) {
			return new OperatorNode(TokenType.OpTokenType.OP_RELGE);
		}

		return null;
	}

	ExprNode parseMulDivOp() {
		if (error)
			return null;

		if (Utility.isOpMultiply()) {
			return new OperatorNode(TokenType.OpTokenType.OP_MULTIPLY);
		} else if (Utility.isOpDivide()) {
			return new OperatorNode(TokenType.OpTokenType.OP_DIVIDE);
		}

		return null;
	}

	ExprNode parseAddSubOp() {
		if (error)
			return null;

		if (Utility.isOpPlus()) {
			return new OperatorNode(TokenType.OpTokenType.OP_PLUS);
		} else if (Utility.isOpMinus()) {
			return new OperatorNode(TokenType.OpTokenType.OP_MINUS);
		}

		return null;
	}

	ExprNode parseUnaryOp() {

		if (error)
			return null;

		if (Utility.isOpBitCompliment()) {
			return new OperatorNode(TokenType.OpTokenType.OP_BITCOMPLIMENT);
		} else if (Utility.isOpLogNot()) {
			return new OperatorNode(TokenType.OpTokenType.OP_LOGNOT);
		} else if (Utility.isOpMinus()) {
			return new OperatorNode(TokenType.OpTokenType.OP_MINUS);
		}

		return null;
	}

	ExprNode parseNumLiteral() {
		if (error)
			return null;

		if (Utility.isNumLiteral()) {
			return new IntegerLiteral(Integer.parseInt(currT.getTokenValue()));
		}

		return null;
	}

	ExprNode parseFactorExp() {

		ExprNode e1 = null;
		ExprNode e2 = null;

		if (Utility.isLParen()) {
			currT = getNextToken();
			e1 = parseLogOrExp();
			if (e1 != null) {
				if(!isNextToken)
					currT = getNextToken();
				if (Utility.isRParen()) {
					isNextToken = false;
					return e1;
				} else {
					error("')' expected");
				}
			} else {
				error("Invalid expr");
			}
		} else if ((e1 = parseUnaryOp()) != null) { // RECHECK GRAMMAR  {Accepts ---a,++++++b}
			currT = getNextToken();
			e2 = parseFactorExp();
			return new UnaryExprNode((OperatorNode) e1, e2);
		} else if ((e1 = parseNumLiteral()) != null) {
			return e1;
		} else if ((e1 = parseIdentifier()) != null) {
			return e1;
		}

		return null;
	}

	ExprNode parseTermExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseFactorExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseMulDivOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseTermExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}
		return null;
	}

	ExprNode parseAddExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseTermExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseAddSubOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseAddExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}
		return null;
	}

	ExprNode parseRelExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseAddExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseRelOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseRelExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}

		return null;
	}

	ExprNode parseEqExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseRelExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseRelEqOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseEqExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}

		return null;
	}

	ExprNode parseLogAndExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseEqExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseLogAndOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseLogAndExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}

		return null;
	}

	ExprNode parseLogOrExp() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseLogAndExp()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseLogOrOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseLogOrExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid Expr");
				}
			} else {
				isNextToken = true;
				return e1;
			}
		}

		return null;
	}

	ExprNode parseExpr() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if (error)
			return null;

		if ((e1 = parseIdentifier()) != null) {
			currT = getNextToken();
			if ((e2 = parseAsgOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseLogOrExp()) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid expr");
				}
			} else {
				error("'=' expected");
			}
		} else if ((e1 = parseLogOrExp()) != null) {
			return e1;
		}
		return null;
	}

	StmtNode parseReturnStmt() {

		if (error)
			return null;

		StmtNode n1;

		if (Utility.isReturnStmt()) {
			currT = getNextToken();
			if ((n1 = parseLogOrExp()) != null) {
				if (!isNextToken)
					currT = getNextToken();
				if (Utility.isSemi()) {
					isNextToken = false;
					return n1;
				} else {
					error("';' expected");
				}
			}
		}

		return null;
	}

//	Block<VarDeclNodeList, StmtNodeList> parseStmt(final Scope scope) {
//
//		if (error)
//			return null;
//
//		VarDeclNodeList varL = new VarDeclNodeList();
//		StmtNodeList stmtL = new StmtNodeList();
//
//		Node n;
//		
//		// Error here , reset while cond
//		while (currT != null) {
//			if (currT.getTokenType() == TokenType.KWTokenType.KW_FUNC) {
//				if (scope == Scope.GLOBAL) {
//					return new Block<>(varL, stmtL);
//				} else {
//					if (!error)
//						error("Unexpected token 'func'");
//					return null;
//				}
//			}
//
//			if ((n = parseVarDecl()) != null) {
//				if (n instanceof VarDeclNode)
//					varL.addElement((NodeWithVarDecl) (VarDeclNode) n);
//				else if (n instanceof VarDeclWithAsgnNode)
//					varL.addElement((NodeWithVarDecl) (VarDeclWithAsgnNode) n);
//
//				currT = getNextToken();
//			} else if ((n = parseExpr()) != null) {
//				stmtL.addElement((ExprNode) n);
//			} else if ((n = parseReturnStmt()) != null) {
//				stmtL.addElement((StmtNode) n);
//			} else {
//				if (!error)
//					error("Unexpected Token");
//				break;
//			}
//		}
//
//		return null;
//	}

	Node parseStmt() {
		Node n;

		if ((n = parseVarDecl()) != null) {
			return n;
		} else if ((n = parseExpr()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if (Utility.isSemi()) {
				isNextToken = false;
				return n;
			} else
				error("';' expected");
				return null;
		} else if ((n = parseReturnStmt()) != null) {
			return n;
		} else {
			if (!error)
				error("Unexpected Token");
		}

		return null;
	}

	Block<VarDeclNodeList, StmtNodeList> parseBlock() {

		Block<VarDeclNodeList, StmtNodeList> b;
		VarDeclNodeList varL = new VarDeclNodeList();
		StmtNodeList stmtL = new StmtNodeList();

		Node n;

		if (Utility.isLBrace()) {
			currT = getNextToken();
			while (!Utility.isRBrace()) {
				n = parseStmt();
				if(n != null) {
					if (n instanceof VarDeclNode)
						varL.addElement((VarDeclNode) n);
					else if (n instanceof VarDeclWithAsgnNode)
						varL.addElement((VarDeclWithAsgnNode) n);
					else if (n instanceof StmtNode)
						stmtL.addElement((StmtNode) n);
				}
				else {
					return null;
				}

				if (!isNextToken)
					currT = getNextToken();
			}

			b = new Block<>(varL, stmtL);
			return b;
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
			if (Utility.isSemi())
				return new VarDeclNode((IdentifierNode) id1);
			else {
				opAsg = parseAsgOp();
				if (opAsg != null) {
					currT = getNextToken();
					e1 = parseLogOrExp();
					if (e1 != null) {
						if (!isNextToken)
							currT = getNextToken();
						if (Utility.isSemi()) {
							isNextToken = false;
							return new VarDeclWithAsgnNode(new VarDeclNode((IdentifierNode) id1), (ExprNode) e1);
						} else {
							error("';' expected");
						}
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

		if (!Utility.isFunc())
			return null;

		Node idM;
		ArgsNodeList argsM;
		StmtNodeList stmtM = new StmtNodeList();
		VarDeclNodeList varM = new VarDeclNodeList();
		Block<VarDeclNodeList, StmtNodeList> block;

		currT = getNextToken();

		idM = parseIdentifier();
		if (idM != null) {
			currT = getNextToken();
			if ((argsM = parseArgs()) != null) {
				currT = getNextToken();
				if ((block = parseBlock()) != null) {
					varM.addAll(block.t);
					stmtM.addAll(block.u);

					return new FuncDeclNode((IdentifierNode) idM, argsM, stmtM, varM);
				} else {
					if (!error)
						error("Invalid block");
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

	/**
	 * Parser entry point <br>
	 *
	 */
	public void parse() {

		currT = getNextToken();

		IdentifierNode idM = new IdentifierNode("main");
		ArgsNodeList argsM = null;
		StmtNodeList stmtM = new StmtNodeList();
		VarDeclNodeList varM = new VarDeclNodeList();

		Node n1;

		while (currT != null) {
			if ((n1 = parseFuncDecl()) != null) {
				root.addFuncDeclNode((FuncDeclNode) n1);
				stmtM.addElement((StmtNode) n1);
				currT = getNextToken();
			} else if ((n1 = parseStmt()) != null) {
				if (n1 instanceof VarDeclNode)
					varM.addElement((VarDeclNode) n1);
				else if (n1 instanceof VarDeclWithAsgnNode)
					varM.addElement((VarDeclWithAsgnNode) n1);
				
				stmtM.addElement((StmtNode) n1);
				if (!isNextToken)
					currT = getNextToken();
			} else {
				if (!error)
					error("Some error");
				break;
			}
		}

		mainF = new FuncDeclNode(idM, argsM, stmtM, varM);
		root.setMainF(mainF);
		System.out.println("Success");
	}
}
