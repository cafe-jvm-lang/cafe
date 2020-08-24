package compiler.parser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

// import com.google.errorprone.annotations.Var;

import compiler.ast.Node.*;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
import compiler.util.LogType.Errors;
// import jdk.nashorn.internal.ir.Assignment;

public class MainParser extends Parser {
	static {
		ParserFactory.registerParser(ParserType.MAINPARSER, new MainParser());
	}

	private Lexer lexer;
	private Token token;
	private Log log;
	private boolean breakAllowed = false, innerLoop = false;

	private MainParser() {
	}

	private MainParser(ParserFactory factory, Lexer lexer) {
		System.out.println("PARSING");
		this.lexer = lexer;
		this.log = factory.log;
		// TESTING
		// nextToken();
		// while(token.kind != TokenKind.END) {
		// System.out.println(token.kind);
		// nextToken();
		// if(token.kind == TokenKind.ERROR)
		// break;
		// }
	}

	@Override
	protected MainParser instance(ParserFactory factory, Lexer lexer) {
		return new MainParser(factory, lexer);
	}

	Token token() {
		return token;
	}

	Token prevToken() {
		return lexer.prevToken();
	}

	void nextToken() {
		lexer.nextToken();
		token = lexer.token();
	}

	boolean accept(TokenKind kind/* ,Errors error ) */ /* pass specific error type */) {
		if (kind == token.kind) {
			nextToken();
			return true;
		} else {
			// TODO: throw Error
			log.error(token.pos, Errors.INVALID_IDENTIFIER);
			return false;
		}
	}

	ExprNode parseLogicalOrExpression() {
		/*
		 * parseLogicalAndExpression() while(TokenType == OR | TokenType == '||'){
		 * parseLogicalAndExpression() }
		 */
		ExprNode exp1 = parseLogicalAndExpression();
		while (token.kind == TokenKind.OROP || token.kind == TokenKind.OR) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseLogicalAndExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;

	}

	ExprNode parseLogicalAndExpression() {
		/*
		 * parseLogicalNotExpression() while(TokenType == AND | TokenType == '&&'){
		 * parseLogicalNotExpression() }
		 */
		ExprNode exp1 = parseLogicalNotExpression();
		while (token.kind == TokenKind.ANDOP || token.kind == TokenKind.AND) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseLogicalNotExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseLogicalNotExpression() {
		/*
		 * parseNotEqualToExpression() while(TokenType == NOT | TokenType == '!'){
		 * parseNotEqualToExpression() }
		 */
		ExprNode exp1 = parseNotEqualToExpression();
		while (token.kind == TokenKind.NOTOP || token.kind == TokenKind.NOT) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseNotEqualToExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseNotEqualToExpression() {
		/*
		 * parseEqualEqualExpression() accept(NOT_EQ) while(TokenType == '!='){
		 * parseEqualEqualExpression() }
		 */
		ExprNode exp1 = parseEqualEqualExpression();
		while (token.kind == TokenKind.NOTEQU) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseEqualEqualExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseEqualEqualExpression() {
		/*
		 * parseRealtionalExpression() while(TokenType == '=='){
		 * parseRealtionalExpression() }
		 */
		ExprNode exp1 = parseRealtionalExpression();
		while (token.kind == TokenKind.EQUEQU) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseRealtionalExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseRealtionalExpression() {
		/*
		 * parseBitOrExpression() while(TokenType == <,>,<=,>=,in ,not in, is, is not ){
		 * parseBitOrExpression() }
		 */
		ExprNode exp1 = parseBitOrExpression();
		while (token.kind == TokenKind.LT || token.kind == TokenKind.GT || token.kind == TokenKind.LTE
				|| token.kind == TokenKind.GTE || token.kind == TokenKind.IN || token.kind == TokenKind.IS
				|| token.kind == TokenKind.NOT) {
			String op = token.value();
			if (token.kind == TokenKind.IS) {
				accept(token.kind);
				op = "is";
				if (token.kind == TokenKind.NOT) {
					op = "isnot";
					accept(token.kind);
				}
			} else if (token.kind == TokenKind.NOT) {
				accept(token.kind);
				if (token.kind == TokenKind.IN) {
					op = "notin";
					accept(TokenKind.IN);
				}
			} else {
				op = token.value();
				accept(token.kind);
			}
			ExprNode exp2 = parseBitOrExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseBitOrExpression() {
		/*
		 * parseBitXorExpression() while(TokenType == '|'){ parseBitXorExpression() }
		 */

		ExprNode exp1 = parseBitXorExpression();
		while (token.kind == TokenKind.BITOR) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseBitXorExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseBitXorExpression() {
		/*
		 * parseLogicalAndExpression() while(TokenType == '^'){
		 * parseLogicalAndExpression() }
		 */
		ExprNode exp1 = parseBitAndExpression();
		while (token.kind == TokenKind.BITAND) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseBitAndExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseBitAndExpression() {
		/*
		 * parseBitRightShiftExpression() while(TokenType == '&'){
		 * parseBitRightShiftExpression() }
		 */
		ExprNode exp1 = parseBitRightShiftExpression();
		while (token.kind == TokenKind.ANDOP) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseBitRightShiftExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseBitRightShiftExpression() {
		/*
		 * parseBitLeftShiftExpression() while(TokenType == '>>' | TokenType == '>>>'){
		 * parseBitLeftShiftExpression() }
		 */
		ExprNode exp1 = parseBitLeftShiftExpression();
		while (token.kind == TokenKind.RSHIFT || token.kind == TokenKind.TRSHIFT) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseBitLeftShiftExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseBitLeftShiftExpression() {
		/*
		 * parseSubtractExpression() while(TokenType == '<<' | TokenType == '<<<'){
		 * parseSubtractExpression() }
		 */
		ExprNode exp1 = parseSubtractExpression();
		while (token.kind == TokenKind.LSHIFT) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseSubtractExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseSubtractExpression() {
		/*
		 * parseAdditionExpression() while(TokenType == '-'){ parseAdditionExpression()
		 * }
		 */
		ExprNode exp1 = parseAdditionExpression();
		while (token.kind == TokenKind.SUB) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseAdditionExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseAdditionExpression() {
		/*
		 * parseMultiplicationExpression() while(TokenType == '+'){
		 * parseMultiplicationExpression() }
		 */
		ExprNode exp1 = parseMultiplicationExpression();
		while (token.kind == TokenKind.ADD) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseMultiplicationExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseMultiplicationExpression() {
		/*
		 * parseDivisionExpression() while(TokenType == '*'){ parseDivisionExpression()
		 * }
		 */
		ExprNode exp1 = parseDivisionExpression();
		while (token.kind == TokenKind.MUL) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseDivisionExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseDivisionExpression() {
		/*
		 * parseFactorExpression() while(TokenType == /, %, // ){
		 * parseFactorExpression() }
		 */
		ExprNode exp1 = parseFactorExpression();
		while (token.kind == TokenKind.DIV || token.kind == TokenKind.MOD || token.kind == TokenKind.FLOORDIV) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseFactorExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		}
		return exp1;
	}

	ExprNode parseFactorExpression() {
		/*
		 * 
		 * if( TokenType == -, ~ ) parseFactorExpression() parsePowerExpression()
		 * 
		 */
		ExprNode exp1 = null;
		if (token.kind == TokenKind.SUB || token.kind == TokenKind.TILDE) {
			if (prevToken().kind == TokenKind.IDENTIFIER) {
				// Handle Case where previous token is identifier and sould not generate
				// UnaryOperator
				// exp1 = parsePowerExpression();
			} else {
				exp1 = parseFactorExpression();
				exp1 = new UnaryExprNode(exp1, token.value());
				accept(token.kind);
			}
		} else {
			exp1 = parsePowerExpression();
		}
		return exp1;
	}

	ExprNode parsePowerExpression() {
		/*
		 * parseAtomExpression() while(TokenType == '**'){ parseAtomExpression() }
		 */
		ExprNode exp1 = null, exp2;
		try {
			exp1 = parseAtomExpression();
			while (token.kind == TokenKind.POWER) {
				String op = token.value();
				accept(TokenKind.POWER);
				exp2 = parseFactorExpression();
				exp1 = new BinaryExprNode(exp1, exp2, op);
			}
			return exp1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exp1;

	}

	ExprNode parseAtomExpression() throws ParseException {
		/*
		 * List of Trailers parseAtom()
		 * 
		 * trail = parseTrailer() while (trail) trail = parseTrailer()
		 */

		ExprNode oExp = parseAtom();
		while (token.kind == TokenKind.LPAREN || token.kind == TokenKind.DOT || token.kind == TokenKind.LSQU) {
			switch (token.kind) {
				case LPAREN:
					accept(TokenKind.LPAREN);
					oExp = new FuncCallNode(oExp, parseParameter());
					break;

				case LSQU:
					ExprNode exp1, exp2;
					while (token.kind == TokenKind.LSQU) {
						accept(TokenKind.LSQU);
						exp1 = parseNumberLiteral();
						if (token.kind == TokenKind.COLON) {
							accept(TokenKind.COLON);
							exp2 = parseNumberLiteral();
							oExp = new SliceNode(oExp, exp1, exp2);
						} else {
							accept(TokenKind.RSQU);
							oExp = new SubscriptNode(oExp, exp1);
						}

					}
					break;
				case DOT:
					ExprNode e1;
					while (token.kind == TokenKind.DOT) {
						accept(TokenKind.DOT);
						e1 = parseIdentifier();
						oExp = new ObjectAccessNode(oExp, e1);
					}
					break;
			}
		}
		return oExp;

	}

	ExprNode parseExprStmt() {
		ExprNode exp1 = parseLogicalAndExpression();
		if (token.kind == TokenKind.OR || token.kind == TokenKind.OROP) {
			String op = token.value();
			accept(token.kind);
			ExprNode exp2 = parseLogicalAndExpression();
			exp1 = new BinaryExprNode(exp1, exp2, op);
		} else if (token.kind == TokenKind.EQU) {
			accept(token.kind);
			exp1 = parseValue();
		}
		return exp1;
	}

	ExprNode parseAtom() {
		/*
		 * switch TokenType case LPAREN: parseExpressionStatement() accept(RPAREN) case
		 * IDENTIFIER: parseIdentifier() case STRINGLITERAL: parseStringLiteral() case
		 * NUMLiteral: parseNumberLiteral() case BOOLLiteral: parseBoolLiteral() case
		 * NULL: parseNull() case THIS: parseThis()
		 */
		ExprNode exp1 = null;
		switch (token.kind) {
			case LPAREN:
				accept(TokenKind.LPAREN);
				exp1 = parseExprStmt();
				accept(TokenKind.RPAREN);
				break;
			case IDENTIFIER:
				exp1 = parseIdentifier();
				break;
			case STRLIT:
				exp1 = parseStringLiteral();
				break;
			case NUMLIT:
				try {
					exp1 = parseNumberLiteral();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("Exception Thrown in parseAtom by NUMLIT");
				}
				break;
			case TRUE:
				exp1 = parseBoolLiteral();
				break;
			case FALSE:
				exp1 = parseBoolLiteral();
				break;
			case NULL:
				exp1 = parseNull();
			case THIS:
				exp1 = parseThis();
		}
		return exp1;

	}

	ExprNode parseNull() {
		return new NullNode();
	}

	ExprNode parseThis() {
		return new ThisNode();
	}

	ExprNode parseIdentifier() {
		/*
		 * Create Identifier Node. return IdentNode()
		 */
		Token prev = token;
		accept(TokenKind.IDENTIFIER);
		return new IdenNode(prev.value());

	}

	ExprNode parseStringLiteral() {
		/*
		 * check Quotes accept(STRING LITERAL)
		 * 
		 */
		Token prev = token;
		accept(TokenKind.STRLIT);
		return new StrLitNode(prev.value());
	}

	ExprNode parseNumberLiteral() throws ParseException {
		/*
		 * 
		 */
		Token prevToken = token;
		accept(TokenKind.NUMLIT);
		Number num;
		if (accept(TokenKind.DOT)) {
			if (accept(TokenKind.NUMLIT)) {
				num = NumberFormat.getInstance().parse(prevToken.value() + '.' + token.value());
				accept(TokenKind.NUMLIT);
				return new NumLitNode(num);
			}
		} else {
			num = NumberFormat.getInstance().parse(prevToken.value());
			return new NumLitNode(num);
		}
		return null;
	}

	ExprNode parseBoolLiteral() {
		/*
		 * 
		 */

		if (token.kind == TokenKind.TRUE) {
			accept(TokenKind.TRUE);
			return new BoolLitNode(true);
		} else if (token.kind == TokenKind.FALSE) {
			accept(TokenKind.FALSE);
			return new BoolLitNode(false);
		}

		return null;

	}

	void parseSubscriptList() {
		/*
		 * while(LBRACKET) parseSubscript()
		 * 
		 */
	}

	void parseSubscript() {
		/*
		 * accept(LBRACKET) parseNumberLiteral() if ( COLON) parseNumberLiteral()
		 * accept(LBRACKET)
		 * 
		 * 
		 */
	}

	List<ExprNode> parseArgList() {
		/*
		 * parseArg() while(COMMA) parseArg()
		 */
		List<ExprNode> args = new ArrayList<>();
		while (token.kind != TokenKind.RPAREN) {
			args.add(parseValue());
			if (token.kind != TokenKind.RPAREN)
				accept(TokenKind.COMMA);
			else
				nextToken();
		}
		return args;

	}

	void parseArg() {
		/*
		 * parseValue()
		 */
	}

	void parseTrailer() {
		/*
		 * if (LPAREN) parseArgList() else if (DOT) parseIdentifier() else if (LBRACKET)
		 * parseSubscriptList() else return false
		 * 
		 */
	}

	void parseExpressionStatement() {
		/*
		 * parseLogAndExpression() if (|| | 'or') parseLogAndExpression() else if (EQUAL
		 * OPERATOR) parseEqualOperator() parseValue() else handle Error
		 * 
		 */
	}

	/* parseStatements */
	IfStmtNode parseIf() {
		ExprNode ifCond, elseBlock;
		BlockNode ifBlock = new BlockNode();

		accept(TokenKind.IF);
		accept(TokenKind.LPAREN);
		ifCond = parseLogicalOrExpression();
		accept(TokenKind.RPAREN);
		accept(TokenKind.LCURLY);
		ifBlock = parseLoopBlock();
		accept(TokenKind.RCURLY);
		return new IfStmtNode(ifCond, ifBlock);
	}

	StmtNode parseIfStatement() {
		/*
		 * Parse If Statement and check if there any 'else' is there, if 'yes' then
		 * parseIt and Break otherwise parse Else if statement and append to a list
		 */

		IfStmtNode ifNode = parseIf();
		List<StmtNode> elseIfList = new ArrayList<>();
		StmtNode elseBlock = null;
		while (token.kind == TokenKind.ELSE) {
			accept(TokenKind.ELSE);
			if (token.kind == TokenKind.IF) {
				elseIfList.add(parseIf());
			} else if (token.kind == TokenKind.LCURLY) {
				// elseBlock = parseBlock();

				break;
			}
		}
		if (elseBlock != null)
			elseIfList.add(new ElseStmtNode(ifNode, elseBlock));
		ifNode.setElsePart(elseIfList);
		return ifNode;
	}

	void parseElseStatement() {
		/*
		 * accept(ELSE) accept(LCURLY) parseBlockStatements() accept(RCURLY)
		 */
	}

	void parseElseIfStatement() {
		/*
		 * List of ElseIfNodes
		 * 
		 * while ( !ELSEIF ){ accept(ELSEIF) accept(LPAREN) parseLogORExpression()
		 * accept(RPAREN) accept(LCURLY) parseBlockStatements() accept(RCURLY)
		 * ElseIf.add(ElseIFNode(condition, block)
		 *
		 * return ElseIfNode
		 */
	}

	StmtNode parseAssignmentStatement() {
		/*
		 * parseIdentifier() while (DOT) parseIdentifier() parseEqualOperator()
		 * parseValue() accept(SEMI)
		 */

		ExprNode exp1 = parseIdentifier();
		ExprNode exp2;
		while (token.kind == TokenKind.DOT) {
			accept(TokenKind.DOT);
			exp2 = parseIdentifier();
			exp1 = new ObjectAccessNode(exp1, exp2);
		}
		accept(TokenKind.EQU);
		ExprNode exp = parseValue();
		accept(TokenKind.SEMICOLON);
		return new AsgnStmtNode(exp1, exp);
	}

	/* Parse Loops */
	List<StmtNode> parseForInit() {
		/*
		 * 
		 * 
		 */
		List<StmtNode> init = null;
		ExprNode iden, val;
		if (token.kind == TokenKind.SEMICOLON)
			return init;
		init = new ArrayList<StmtNode>();
		while (token.kind == TokenKind.VAR || token.kind == TokenKind.IDENTIFIER) {
			if (token.kind == TokenKind.VAR) {
				accept(TokenKind.VAR);
				iden = parseIdentifier();
				accept(TokenKind.EQU);
				val = parseValue();
				if (token.kind == TokenKind.COMMA)
					nextToken();
				init.add(new VarDeclNode((IdenNode) iden, val));
			} else {
				iden = parseIdentifier();
				accept(TokenKind.EQU);
				val = parseValue();
				if (token.kind == TokenKind.COMMA)
					nextToken();
				init.add(new AsgnStmtNode(iden, val));
			}
		}
		return init;
	}

	ExprNode parseForCondition() {
		/*
		 * parseLogOrEcpression()
		 */
		ExprNode cond = null;
		if (token.kind == TokenKind.SEMICOLON)
			return cond;
		cond = parseLogicalOrExpression();
		return cond;
	}

	List<AsgnStmtNode> parseForIncrement() {
		/*
		 * 
		 * parseAssignmentStatement()
		 */
		ExprNode iden, val, exp2;
		List<AsgnStmtNode> incrNodes = null;
		if (token.kind == TokenKind.RPAREN)
			return incrNodes;

		incrNodes = new ArrayList<AsgnStmtNode>();
		while (token.kind == TokenKind.IDENTIFIER) {
			iden = parseIdentifier();
			while (token.kind == TokenKind.DOT) {
				accept(TokenKind.DOT);
				exp2 = parseIdentifier();
				iden = new ObjectAccessNode(iden, exp2);
			}
			accept(TokenKind.EQU);
			val = parseValue();
			incrNodes.add(new AsgnStmtNode(iden, val));
			if (token.kind == TokenKind.COMMA)
				nextToken();
		}
		return incrNodes;
	}

	StmtNode parseForStatement() {
		/*
		 * accept(FOR) accept(LPAREN) if (SEMI ) parseForCondition() accept(SEMI)
		 * parseForIncr() else parseForInit() accept(SEMI) parseForCondition()
		 * accept(SEMI) parseForIncr()
		 */
		List<StmtNode> init = null;
		ExprNode cond = null;
		List<AsgnStmtNode> incrNode = null;
		BlockNode block = null;
		accept(TokenKind.FOR);
		accept(TokenKind.LPAREN);
		init = parseForInit();
		accept(TokenKind.SEMICOLON);
		cond = parseForCondition();
		accept(TokenKind.SEMICOLON);
		incrNode = parseForIncrement();
		accept(TokenKind.RPAREN);
		accept(TokenKind.LCURLY);
		block = parseLoopBlock();
		accept(TokenKind.RCURLY);
		return new ForStmtNode(init, cond, incrNode, block);
	}

	StmtNode parseLoopStatement() {
		/*
		 * accept(LOOP) parseLoopIdentifier() accept(IN) parseLoopValue()
		 * parseLoopBlock() parseCollectionComprehension()
		 * 
		 */
		IdenNode iden1, iden2 = null;
		ExprNode exp = null;
		BlockNode block = null;

		accept(TokenKind.LOOP);
		iden1 = (IdenNode) parseIdentifier();
		if (token.kind == TokenKind.COMMA) {
			nextToken();
			iden2 = (IdenNode) parseIdentifier();
		}
		accept(TokenKind.IN);
		exp = parseCollection();
		if (exp == null) {
			if (token.kind == TokenKind.LCURLY)
				exp = parseObjectCreation();
			else
				try {
					exp = parseAtomExpression();
				} catch (ParseException e) {
				}
		}
		accept(TokenKind.LCURLY);
		block = parseLoopBlock();
		accept(TokenKind.RCURLY);
		return new LoopStmtNode(iden1, iden2, exp, block);
	}

	StmtNode parseFlowStatement() {
		/*
		 * if(CONTINUE) return ContinueNode if(BREAK) return BreakNode
		 */
		if (breakAllowed)
			if (token.kind == TokenKind.CONTINUE)
				return new ContinueStmtNode();
			else
				return new BreakStmtNode();
		else
			System.out.println("Throw Error");
		return null;
	}
	/* Parse Loop Done */

	/* Parse Collection */
	ExprNode parseList() {
		/*
		 * List of Values
		 * 
		 * accept(LBRACKET) List.add(parseValue()) if(COMMA or DOTDOT)
		 * List.add(parseValue()) else if(LOOP_KEYWORD)
		 * List.add(parseForComprehension())
		 * 
		 * return List
		 */
		List<ExprNode> listNode = new ArrayList<>();
		while (token.kind != TokenKind.RSQU) {
			listNode.add(parseValue());
			if (token.kind != TokenKind.RSQU)
				accept(TokenKind.COMMA);
		}
		accept(TokenKind.RSQU);
		return new ListCollNode(listNode);
	}

	ExprNode parseListCollection() {
		/*
		 * List of Collection
		 * 
		 * if (RBRACKET){ return ListNode() }
		 * 
		 * else { return parseList(); }
		 * 
		 * 
		 */
		accept(TokenKind.LSQU);
		if (token.kind == TokenKind.RSQU) {
			accept(TokenKind.RSQU);
			return new ListCollNode();
		} else if (token.kind == TokenKind.LOOP) {
			return parseComprehension("list");
		} else {
			return parseList();
		}
	}

	void parseMap() { // NOT Used by MapColl
		/*
		 * List of Map<dynamic, dynamic>
		 * 
		 * case RBRACKET: return List.add(map)) case LBRACKET: map.addKey(parseValue())
		 * accept(COMMA) map.addValue(parseValue()) List.add(map)
		 * 
		 * return map
		 */

	}

	ExprNode parseMapCollection() {
		/*
		 * List of MapCollection List of Comp
		 * 
		 * case: LBRACKET mapCollection.add(parseMap())
		 * 
		 * if (COMMA) : mapCollection.add(parseMap()) if (LOOP )
		 * Comp.add(parseForComprehension())
		 * 
		 */
		Map<ExprNode, ExprNode> pairs = new HashMap<>();

		accept(TokenKind.LSQU);
		while (token.kind != TokenKind.RSQU) {
			ExprNode exp2 = new MapCollNode(), exp1 = new MapCollNode();
			accept(TokenKind.LSQU);
			if (token.kind == TokenKind.RSQU) {
				pairs.put(exp1, exp2);
				accept(TokenKind.RSQU);
				accept(TokenKind.COMMA);

				continue;
			} else if (token.kind == TokenKind.LOOP) {
				return parseComprehension("map");
			}
			exp1 = parseValue();
			accept(TokenKind.COMMA);
			exp2 = parseValue();
			accept(TokenKind.RSQU);

			pairs.put(exp1, exp2);
		}
		accept(TokenKind.RSQU);
		return new MapCollNode(pairs);
	}

	CompTypeNode parseComprehension(String type) {
		IdenNode iden1, iden2 = null;
		ExprNode exp = null;
		BlockNode block = null;
		ExprNode ifCond;

		CompTypeNode mapComp = type == "map" ? new MapCompNode()
				: type == "list" ? new ListCompNode()
						: type == "set" ? new SetCompNode() : type == "link" ? new LinkCompNode() : null;

		while (token.kind != TokenKind.RSQU) {
			switch (token.kind) {
				case LOOP:
					accept(TokenKind.LOOP);
					iden1 = (IdenNode) parseIdentifier();
					if (token.kind == TokenKind.COMMA) {
						nextToken();
						iden2 = (IdenNode) parseIdentifier();
					}
					accept(TokenKind.IN);
					exp = parseCollection();
					if (exp == null) {
						if (token.kind == TokenKind.LCURLY)
							exp = parseObjectCreation();
						else
							try {
								exp = parseAtomExpression();
							} catch (ParseException e) {
							}
					}
					mapComp.addExpr(new CompLoopNode(iden1, iden2, exp));
					break;
				case IF:
					accept(TokenKind.IF);
					accept(TokenKind.LPAREN);
					ifCond = parseLogicalOrExpression();
					accept(TokenKind.RPAREN);
					mapComp.addExpr(new CompIfNode(ifCond));
			}
		}
		accept(TokenKind.RSQU);
		return mapComp;
	}

	ExprNode parseSet() {
		/*
		 * List of Values
		 * 
		 * accept(LBRACKET) List.add(parseValue()) if(COMMA or DOTDOT)
		 * List.add(parseValue()) else if(LOOP_KEYWORD)
		 * List.add(parseForComprehension())
		 * 
		 * return List
		 */
		List<ExprNode> setNode = new ArrayList<>();
		while (token.kind != TokenKind.RSQU) {
			setNode.add(parseValue());
			if (token.kind != TokenKind.RSQU)
				accept(TokenKind.COMMA);
		}
		accept(TokenKind.RSQU);
		return new SetCollNode(setNode);
	}

	ExprNode parseSetCollection() {
		/*
		 * List of Collection
		 * 
		 * if (RBRACKET){ return SetNode() }
		 * 
		 * else { return parseSet(); }
		 * 
		 * 
		 */
		nextToken();
		accept(TokenKind.LSQU);
		if (token.kind == TokenKind.RSQU) {
			accept(TokenKind.RSQU);
			return new SetCollNode();
		} else if (token.kind == TokenKind.LOOP) {
			return parseComprehension("set");
		} else {
			return parseSet();
		}
	}

	ExprNode parseLink() {
		/*
		 * List of Values
		 * 
		 * accept(LBRACKET) List.add(parseValue()) if(COMMA or DOTDOT)
		 * List.add(parseValue()) else if(LOOP_KEYWORD)
		 * List.add(parseForComprehension())
		 * 
		 * return List
		 */
		List<ExprNode> listNode = new ArrayList<>();
		while (token.kind != TokenKind.RSQU) {
			listNode.add(parseValue());
			if (token.kind != TokenKind.RSQU)
				accept(TokenKind.COMMA);
		}
		accept(TokenKind.RSQU);
		return new LinkCollNode(listNode);
	}

	ExprNode parseLinkCollection() {
		/*
		 * List of Collection
		 * 
		 * if (RBRACKET){ return ListNode() }
		 * 
		 * else { return parseList(); }
		 * 
		 * 
		 */
		nextToken();
		accept(TokenKind.LSQU);
		if (token.kind == TokenKind.RSQU) {
			accept(TokenKind.RSQU);
			return new LinkCollNode();
		} else if (token.kind == TokenKind.LOOP) {
			return parseComprehension("link");
		} else {
			return parseLink();
		}
	}

	ExprNode parseCollection() {
		/*
		 * List of Collection
		 * 
		 * read TokenType: case LBRACKET: parseListCollection() case LINK: case SET:
		 * parseCollection() case MAP: parseMapCollection()
		 * 
		 * return List
		 */
		ExprNode collExpr = null;

		switch (token.kind) {
			case LSQU:
				collExpr = parseListCollection();
				break;
			case LINK:
				collExpr = parseLinkCollection();
				break;
			case SET:
				collExpr = parseSetCollection();
				break;
			case MAP:
				collExpr = parseMapCollection();
				break;
		}
		return collExpr;

	}

	/* Parse Collection Done */

	/* Parse Values */
	ExprNode parseObjectCreation() {
		/*
		 * List of ObjectNode
		 * 
		 * List.add(parseObject())
		 * 
		 * if Comma { List.add(parseObject()) }
		 * 
		 */

		Map<IdenNode, ExprNode> object = new HashMap<>();
		IdenNode idenNode;
		ExprNode exprNode;

		accept(TokenKind.LCURLY);
		while (token.kind != TokenKind.RCURLY) {
			idenNode = (IdenNode) parseIdentifier();
			accept(TokenKind.COLON);
			exprNode = parseValue();
			object.put(idenNode, exprNode);
			if (TokenKind.RCURLY != token.kind)
				accept(TokenKind.COMMA);
		}
		accept(TokenKind.RCURLY);
		return new ObjCreationNode(object);

	}

	void parseObject() { // Not Used
		/*
		 * accept(LCURLY) parseIdentifier()
		 * 
		 * check Colon
		 * 
		 * parseValue()
		 * 
		 * accept(RCURLY)
		 * 
		 * 
		 * return ObjectNode
		 */
	}

	ExprNode parseAnnFunction() {
		/*
		 * accept(FUNC) isAnnFunmc = true parseFunction()
		 * 
		 */
		accept(TokenKind.FUNC);
		accept(TokenKind.LPAREN);
		List<ExprNode> arg = parseArgList();
		accept(TokenKind.RPAREN);
		ArgsListNode args = new ArgsListNode(arg);

		accept(TokenKind.LCURLY);
		// List<StmtNode> stmt = parseBlockStatement();
		accept(TokenKind.RCURLY);
		BlockNode block = new BlockNode(); // BlockNode(stmt);
		return new AnnFuncNode(args, block);
	}

	ExprNode parseValue() {
		/*
		 * ValueNode
		 * 
		 * checks Grammar
		 * 
		 * Case Object: parseObjectCreation() Case Collection: parseCollection() Case
		 * AnnFunc: parseAnnFunction() case Operator: parseBitOrOperator()
		 * 
		 * return ValueNode
		 */
		ExprNode valExpr = null;
		switch (token.kind) {
			case LCURLY:
				valExpr = parseObjectCreation();
				break;
			case LSQU:
			case LINK:
			case SET:
			case MAP:
				valExpr = parseCollection();
				break;

			case FUNC:
				valExpr = parseAnnFunction();
				break;

			case BITOR:
				break;

		}
		return valExpr;
	}

	/* Parse Values DOne */

	void parseVariableDeclaration() {
		/*
		 * 
		 * parseIdentifier() parseEqualOperator() parseValue()
		 * 
		 * return VariableNode
		 */

	}

	List<VarDeclNode> parseVariable() {
		/*
		 * List Variables checks grammar for variable Declaration
		 * 
		 * 
		 * calls parseVariableDeclaration
		 * 
		 * return List
		 */
		List<VarDeclNode> varDeclNodes = new ArrayList<>();
		accept(TokenKind.VAR);
		while (token.kind != TokenKind.SEMICOLON) {
			IdenNode idenNode = (IdenNode) parseIdentifier();
			ExprNode exp = null;
			if (token.kind == TokenKind.EQU) {
				nextToken();
				exp = parseValue();
			}
			if (token.kind != TokenKind.SEMICOLON)
				accept(TokenKind.COMMA);
			varDeclNodes.add(new VarDeclNode(idenNode, exp));
		}
		accept(TokenKind.SEMICOLON);
		return varDeclNodes;

	}

	List<ConstDeclNode> parseConstVariable() {
		/*
		 * List Variables checks grammar for variable Declaration
		 * 
		 * 
		 * calls parseVariableDeclaration
		 * 
		 * return List
		 */
		List<ConstDeclNode> constDeclNodes = new ArrayList<>();
		accept(TokenKind.CONST);
		while (token.kind != TokenKind.SEMICOLON) {
			IdenNode idenNode = (IdenNode) parseIdentifier();
			accept(TokenKind.EQU);
			ExprNode exp = parseValue();
			if (token.kind != TokenKind.SEMICOLON)
				accept(TokenKind.COMMA);
			constDeclNodes.add(new ConstDeclNode(idenNode, exp));
		}
		accept(TokenKind.SEMICOLON);
		return constDeclNodes;

	}

	ParameterListNode parseParameter() {
		/*
		 * List of Arguments
		 * 
		 * responsibility: Check Grammar and parseIdentifier
		 * 
		 * calls: arguments.add = parseIdentifier() arguments.add = varArgs()
		 * 
		 * return List
		 */
		boolean varArg = false;
		List<IdenNode> idenNodes = new ArrayList<>();

		while (token.kind != TokenKind.RPAREN) {
			if (token.kind == TokenKind.VARARGS) {
				accept(TokenKind.VARARGS);
				varArg = true;
				idenNodes.add((IdenNode) parseIdentifier());
				accept(TokenKind.RPAREN);
				break;
			}
			idenNodes.add((IdenNode) parseIdentifier());
			if (TokenKind.RPAREN != token.kind)
				accept(TokenKind.COMMA);
		}

		return new ParameterListNode(idenNodes, varArg);

	}

	// ExprNode parseFunctionCall(){
	// IdenNode funcName = parseIdentifier();

	// }

	DeclNode parseFunctionDeclaration() {
		/*
		 * List of Parameter BlockNode
		 * 
		 * calls: name = isAnnFunc ? null : parseIdentifier() parameter =
		 * parseParameter() BlockNode= parseBlockStatement()
		 * 
		 * FunctionNode(name, parameter, BlockNode); returns FunctionNode
		 */
		accept(TokenKind.FUNC);
		ExprNode funcName = parseIdentifier();
		accept(TokenKind.LPAREN);
		ParameterListNode arg = parseParameter();
		accept(TokenKind.RPAREN);
		accept(TokenKind.LCURLY);
		List<StmtNode> stmt = new ArrayList<>();
		while (token.kind != TokenKind.RCURLY)
			stmt.addAll(parseBlock());
		accept(TokenKind.RCURLY);
		BlockNode block = new BlockNode();
		block.setStmt(stmt);
		return new FuncDeclNode((IdenNode) funcName, arg, block);
	}

	void parseDeclarativeStatement() {
		/*
		 * List of Declarative Statement
		 * 
		 * Checks Type of Statement and calls below methods to parse calls parseFunction
		 * calls parseVariable
		 */
	}

	void block() {
		/*
		 * Handles Cases For Each Type of Block Statements like DECL, ASGN, IF etc. and
		 * calls respective methods
		 *
		 */
	}

	void parseBlockStatement() {
		/*
		 * List of Statement
		 * 
		 * parse Grammar, checks type of Statement and calls block()
		 */

		/*
		 * switch(token.kind){ case VAR }
		 */

	}

	StmtNode parseReturnStatement() {
		accept(TokenKind.RET);
		ExprNode exp = parseValue();
		accept(TokenKind.SEMICOLON);
		return new ReturnStmtNode(exp);
	}

	BlockNode parseLoopBlock() {
		/*
		 * parseBlockStatement() parseFlowStatement()
		 */
		List<StmtNode> blockStats = new ArrayList<StmtNode>();
		BlockNode blockNode = new BlockNode();
		while (token.kind != TokenKind.RCURLY) {
			switch (token.kind) {
				case CONTINUE:
				case BREAK:
					blockStats.add(parseFlowStatement());
					break;
				default:
					blockStats.addAll(parseBlock());
			}
		}
		blockNode.setStmt(blockStats);
		return blockNode;
	}

	// return Block Statement Node
	List<StmtNode> parseBlock() {
		/*
		 * List of block Statements calls parseBlockStatement
		 */
		List<StmtNode> blockStmt = new ArrayList<>();
		switch (token.kind) {
			case VAR:
				blockStmt.addAll(parseVariable());
				break;
			case CONST:
				blockStmt.addAll(parseConstVariable());
				break;
			case FUNC:
				blockStmt.add(parseFunctionDeclaration());
				break;
			case IF:
				blockStmt.add(parseIfStatement());
				break;
			case FOR:
				innerLoop = breakAllowed ? true : false;
				breakAllowed = true;
				blockStmt.add(parseForStatement());
				breakAllowed = innerLoop ? true : false;
				innerLoop = false;
				break;
			case LOOP:
				blockStmt.add(parseLoopStatement());
				break;
			case RET:
				blockStmt.add(parseReturnStatement());
				break;
			case IDENTIFIER:
				blockStmt.add(parseAssignmentStatement());
				break;
		}
		return blockStmt;
	}

	// return Import Statement Node
	void parseImportStatement() {
		/* List of Imports */

		// accept('@');
		// boolean valid = checkFilePathRegex(token.value());
		// if(valid) return ImportStatement(token.value())
		// else Throw Error

	}

	// return Statement Node

	// return List Of Statements
	ProgramNode parseStatements() {
		/*
		 * List of Statement stats
		 * 
		 * calls parseStatement()
		 */

		// nextToken();
		// while(token.kind != END){
		// StatementNode stat= parseStatement();
		// stats.add(stat);
		// nextToken();
		// }
		// return ProgramNode(stats);

		nextToken();
		List<StmtNode> tree = new ArrayList<>();
		while (token.kind != TokenKind.END) {
			switch (token.kind ) {
				case IMPORT:
					break;
				default:
					tree.addAll(parseBlock());
					break;
			}
		}
		return new ProgramNode(tree);
	}

	// void parseStatementAsBlock() {
	// 	/*
	// 	 * switch(token.kind){ case IF: case FOR: case }
	// 	 */
	// }

	ProgramNode parseProgram() {
		// TODO: ProgramNode node;
		// node = parseStatements()
		// return node

		ProgramNode node = parseStatements();
		return node;
	}

	@Override
	public void parse() {
		// while(token.kind != TokenKind.END) {
		// if(token.kind == TokenKind.ERROR) {
		// return;
		// }
		// System.out.println(token.kind+" "+token.value()+" "+token.pos);
		// lexer.nextToken();
		// token = lexer.token();
		// }

		// Parser p = parseProgram();
		// return p;


		ProgramNode parser = parseProgram();
	}
}
