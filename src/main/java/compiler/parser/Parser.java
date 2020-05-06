package compiler.parser;

import java.util.ArrayList;
import java.util.List;

import compiler.ast.ArgsNode;
import compiler.ast.ArgsNodeList;
import compiler.ast.BinaryExprNode;
import compiler.ast.Block;
import compiler.ast.ExprNode;
import compiler.ast.FuncCallNode;
import compiler.ast.FuncDeclNode;
import compiler.ast.IdentifierNode;
import compiler.ast.IfElseStmtNode;
import compiler.ast.IfStmtNode;
import compiler.ast.LiteralNode;
import compiler.ast.Node;
import compiler.ast.OperatorNode;
import compiler.ast.ParameterNode;
import compiler.ast.ParameterNodeList;
import compiler.ast.ProgramNode;
import compiler.ast.ReturnStmtNode;
import compiler.ast.StmtNode;
import compiler.ast.StmtNodeList;
import compiler.ast.UnaryExprNode;
import compiler.ast.VarDeclNode;
import compiler.ast.VarDeclNodeList;
import compiler.ast.VarDeclWithAsgnNode;
import compiler.lexer.Token;
import compiler.lexer.tokentypes.TokenType;
import compiler.utils.Scope;

public class Parser {

	private List<Token> tokenL;
	protected static Token currT;
	private static int curr = 0;

	private ProgramNode root;
	private FuncDeclNode mainF;

	private boolean error = false;
	private boolean isNextToken = false;

	public Parser(String fileName, List<Token> tokenL) {
		this.tokenL = tokenL;

		root = new ProgramNode(fileName);
		// createMainFunc();
	}

	final Token getNextToken() {
		if (curr < tokenL.size()) {
			return tokenL.get(curr++);
		}
		// System.out.println("Token list empty");
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
		ParameterNodeList args = null;
		StmtNodeList stmt = null;
		VarDeclNodeList var = null;

		//mainF = new FuncDeclNode(id1, args, stmt, var);
		mainF = new FuncDeclNode(id1, args, null);
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

	ArgsNodeList parseArgs(boolean isDecl) {

		if (error)
			return null;

		Node arg;
		ArgsNodeList argL = new ArgsNodeList();
		if (Utility.isLParen()) {
			currT = getNextToken();

			if ((arg = parseIdentifier()) != null) {
				argL.addElement(new ArgsNode<IdentifierNode>((IdentifierNode) arg,isDecl));
				currT = getNextToken();
			} else if ((arg = parseNumLiteral()) != null && !isDecl) {
				argL.addElement(new ArgsNode<LiteralNode>((LiteralNode) arg,isDecl));
				currT = getNextToken();
			} 
//			else {
//				error = true;
//				error("Args Error");
//			}

			while (!Utility.isRParen()) {
				if (Utility.isComma()) {
					currT = getNextToken();
					arg = ((arg = parseNumLiteral()) != null && !isDecl) ? arg : parseIdentifier();
					if (arg != null) {
						if (arg instanceof IdentifierNode)
							argL.addElement(new ArgsNode<IdentifierNode>((IdentifierNode) arg,isDecl));
						else
							argL.addElement(new ArgsNode<LiteralNode>((LiteralNode) arg,isDecl));
					} else {
						if (!error)
							error("Parser:expected IDEN || Int");
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

	ExprNode parseMulOp() {
		if (error)
			return null;

		if (Utility.isOpMultiply()) {
			return new OperatorNode(TokenType.OpTokenType.OP_MULTIPLY);
		}

		return null;
	}

	ExprNode parseDivOp() {
		if (Utility.isOpDivide()) {
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
			return new LiteralNode(Integer.parseInt(currT.getTokenValue()));
		}

		return null;
	}

	ExprNode parseFactorExp(boolean isConditional) {

		ExprNode e1 = null;
		ExprNode e2 = null;

		if (Utility.isLParen()) {
			currT = getNextToken();
			if (isConditional)
				e1 = parseLogOrExp();
			else
				e1 = parseAddExp(isConditional);
			if (e1 != null) {
				if (!isNextToken)
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
		} else if ((e1 = parseUnaryOp()) != null) { // RECHECK GRAMMAR {Accepts ---a,++++++b}
			currT = getNextToken();
			e2 = parseFactorExp(isConditional);
			return new UnaryExprNode((OperatorNode) e1, e2);
		} else if ((e1 = parseNumLiteral()) != null) {
			return e1;
		} else if ((e1 = parseIdentifier()) != null) {
			currT = getNextToken();
			ArgsNodeList argsL;
			if ((argsL = parseArgs(false)) != null)
				return new FuncCallNode((IdentifierNode) e1, argsL);
			else {
				isNextToken = true;
				return e1;
			}
		}

		return null;
	}

	ExprNode parseTerm2Exp(boolean isConditional) {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseFactorExp(isConditional)) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseDivOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseTerm2Exp(isConditional)) != null) {
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

	ExprNode parseTerm1Exp(boolean isConditional) {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseTerm2Exp(isConditional)) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseMulOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseTerm1Exp(isConditional)) != null) {
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

	ExprNode parseAddExp(boolean isConditional) {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;

		if ((e1 = parseTerm1Exp(isConditional)) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if ((e2 = parseAddSubOp()) != null) {
				while (e2 != null) {
					isNextToken = false;
					currT = getNextToken();

					if ((e3 = parseTerm1Exp(isConditional)) != null) {
						e1 = new BinaryExprNode(e1, (OperatorNode) e2, e3);		
					}
					else {
						error("Invalid Expr");
					}
					e2 = parseAddSubOp();
				}
				return e1;
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

		if ((e1 = parseAddExp(true)) != null) {
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

	ExprNode parseNonCondExpr() {

		ExprNode e1;
		ExprNode e2;
		ExprNode e3;
		ArgsNodeList argsL;

		if (error)
			return null;

		if ((e1 = parseIdentifier()) != null) {
			currT = getNextToken();
			if ((e2 = parseAsgOp()) != null) {
				isNextToken = false;
				currT = getNextToken();
				if ((e3 = parseAddExp(false)) != null) {
					return new BinaryExprNode(e1, (OperatorNode) e2, e3);
				} else {
					error("Invalid expr");
				}
			} else if ((argsL = parseArgs(false)) != null) {
				isNextToken = false;
				return new FuncCallNode((IdentifierNode) e1, argsL);
			} else {
				error("'=' expected");
			}
		} /*
			 * else if ((e1 = parseLogOrExp()) != null) { return e1; }
			 */
		return null;
	}

	ExprNode parseCondExpr() {

		if (error)
			return null;

		ExprNode e1;

		if (Utility.isLParen()) {
			currT = getNextToken();
			if ((e1 = parseLogOrExp()) != null) {
				if (!isNextToken)
					currT = getNextToken();

				if (Utility.isRParen()) {
					isNextToken = false;
					return e1;
				}
			}
		}

		return null;
	}

	StmtNode parseIfElseStmt(Scope scope) {

		if (error)
			return null;

		ExprNode ifCond = null;
//		StmtNodeList ifStmtL = new StmtNodeList();
//		VarDeclNodeList ifVarL = new VarDeclNodeList();
		Block ifBlock;
		Block elseBlock;
		
//		StmtNodeList elseStmtL = new StmtNodeList();
//		VarDeclNodeList elseVarL = new VarDeclNodeList();

		if (Utility.isIfStmt()) {
			currT = getNextToken();
			if ((ifCond = parseCondExpr()) != null) {
				if (!isNextToken)
					currT = getNextToken();
				if ((ifBlock = parseBlock(scope)) != null) {
//					ifStmtL.addAll(block.u);
//					ifVarL.addAll(block.t);

					currT = getNextToken();

					if (Utility.isElseStmt()) {
						currT = getNextToken();
						if ((elseBlock = parseBlock(scope)) != null) {
//							elseStmtL.addAll(block.u);
//							elseVarL.addAll(block.t);
							return new IfElseStmtNode(ifCond, ifBlock, elseBlock);
						} else {
							error("Illegal Block ELSE");
						}
					} else {
						isNextToken = true;
						return new IfStmtNode(ifCond, ifBlock);
					}
				} else {
					error("Illegal Block stmt IF");
				}
			} else {
				error("Illegal Expr IF");
			}
		}

		return null;
	}

	StmtNode parseReturnStmt() {

		if (error)
			return null;

		Node n1;

		if (Utility.isReturnStmt()) {
			currT = getNextToken();
			if ((n1 = parseAddExp(false)) != null) {
				if (!isNextToken)
					currT = getNextToken();
				if (Utility.isSemi()) {
					isNextToken = false;
					return new ReturnStmtNode((ExprNode) n1);
				} else {
					error("';' expected");
				}
			}
		}

		return null;
	}

	Node parseStmt(Scope scope) {
		Node n;

		if ((n = parseVarDecl()) != null) {
			return n;
		} else if ((n = parseNonCondExpr()) != null) {
			if (!isNextToken)
				currT = getNextToken();
			if (Utility.isSemi()) {
				isNextToken = false;
				return n;
			} else
				error("';' expected");
			return null;
		} else if (scope == Scope.LOCAL && (n = parseReturnStmt()) != null) {
			return n;
		} else if (scope == Scope.GLOBAL && (n = parseFuncDecl()) != null) {
			return n;
		} else if ((n = parseIfElseStmt(scope)) != null) {
			return n;
		} else {
			if (!error)
				error("Unexpected Token");
		}

		return null;
	}

	Block parseBlock(Scope scope) {

		Block b;
		VarDeclNodeList varL = new VarDeclNodeList();
		StmtNodeList stmtL = new StmtNodeList();

		Node n;

		if (Utility.isLBrace()) {
			currT = getNextToken();
			while (!Utility.isRBrace()) {
				n = parseStmt(scope);
				if (n != null) {
					if (n instanceof VarDeclNode)
						varL.addElement((VarDeclNode) n);
					else if (n instanceof VarDeclWithAsgnNode)
						varL.addElement((VarDeclWithAsgnNode) n);

					stmtL.addElement((StmtNode) n);
				} else {
					return null;
				}

				if (!isNextToken)
					currT = getNextToken();
			}

			isNextToken = false;
			b = new Block(varL, stmtL);
			return b;
		}

		return null;
	}

	ParameterNodeList parseParameters() {
		
		Node identifier;
		ParameterNodeList parameterNodeList = new ParameterNodeList();
		
		if (Utility.isLParen()) {
			currT = getNextToken();

			if ((identifier = parseIdentifier()) != null) {
				ParameterNode paramNode = new ParameterNode((IdentifierNode) identifier);
				parameterNodeList.addElement(paramNode);
				currT = getNextToken();
			}

			while (!Utility.isRParen()) {
				if (Utility.isComma()) {
					currT = getNextToken();
					identifier =  parseIdentifier();
					if (identifier != null) {
						ParameterNode paramNode = new ParameterNode((IdentifierNode) identifier);
						parameterNodeList.addElement(paramNode);
					} else {
						if (!error)
							error("Parser:expected IDEN ");
						return null;
					}
				} else {
					if (!error)
						error("',' expected");
					return null;
				}

				currT = getNextToken();
			}
			return parameterNodeList;
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
					e1 = parseAddExp(false);
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

	/**
	 * <pre>
	 * GOAL ::= FUNCDECL
	 * FUNCDECL ::= "func" IDEN "(" PARAMS ")" "{" BLOCK "}"
	 * </pre>
	 * 
	 * @return Node
	 */
	Node parseFuncDecl() {

		if (error)
			return null;

		if (!Utility.isFunc())
			return null;

		Node idM;
		ParameterNodeList parameterNodeList;
		StmtNodeList stmtM = new StmtNodeList();
		VarDeclNodeList varM = new VarDeclNodeList();
		Block block;

		currT = getNextToken();

		idM = parseIdentifier();
		if (idM != null) {
			currT = getNextToken();
			if ((parameterNodeList = parseParameters()) != null) {
				currT = getNextToken();
				if ((block = parseBlock(Scope.LOCAL)) != null) {
//					varM.addAll(block.t);
//					stmtM.addAll(block.u);

					return new FuncDeclNode((IdentifierNode) idM, parameterNodeList, block);
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
		parameterNodeList = null;
		stmtM = null;
		varM = null;
		block = null;
		return null;
	}

	/**
	 * Parser entry point <br>
	 * 
	 * <pre>
	 * Grammar:
	 * GOAL :: = PROGRAM
	 * PROGRAM ::= MAINF | FUNCDECL
	 * </pre>
	 */
	public Node parse() {

		currT = getNextToken();

		IdentifierNode idM = new IdentifierNode("main");
		StmtNodeList stmtM = new StmtNodeList();
		VarDeclNodeList varM = new VarDeclNodeList();

		Node n1;

		while (currT != null) {
			/*
			 * if ((n1 = parseFuncDecl()) != null) { root.addFuncDeclNode((FuncDeclNode)
			 * n1); stmtM.addElement((StmtNode) n1); currT = getNextToken(); } else
			 */
			if ((n1 = parseStmt(Scope.GLOBAL)) != null) {
				if (n1 instanceof VarDeclNode)
					varM.addElement((VarDeclNode) n1);
				else if (n1 instanceof VarDeclWithAsgnNode)
					varM.addElement((VarDeclWithAsgnNode) n1);
				else if (n1 instanceof FuncDeclNode)
					root.addFuncDeclNode((FuncDeclNode) n1);
				stmtM.addElement((StmtNode) n1);
				if (!isNextToken)
					currT = getNextToken();
			} else {
				if (!error)
					error("Some error");
				break;
			}
		}

		mainF = new FuncDeclNode(idM, null, new Block(varM,stmtM));
		root.setMainF(mainF);

		return root;
	}
}
