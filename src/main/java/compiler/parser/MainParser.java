package compiler.parser;

import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;

public class MainParser extends Parser {
	static {
		ParserFactory.registerParser(ParserType.MAINPARSER, new MainParser());
	}

	private Lexer lexer;
	
	private MainParser() {
	}

	private MainParser(ParserFactory factory, Lexer lexer) {
		System.out.println("PARSING");
		this.lexer = lexer;
	}

	@Override
	protected MainParser instance(ParserFactory factory,Lexer lexer) {
		return new MainParser(factory,lexer);
	}

	@Override
	public void parse() {
		lexer.nextToken();
		Token s = lexer.token();
		while(s.kind != TokenKind.END) {
			System.out.println(s.kind);
			lexer.nextToken();
			s = lexer.token();
		}
	}
}

class ParserMethods{
	
	void accept(/*TokenType tokenType*/) {
		/*
		 * if( tokenType == currentToken.tokenType)
		 * 		token = nextToken()
		 * 
		 * else
		 * 		throw Error
		 */
	}
	void parseLogicalOrExpression() {
		/*
		 * parseLogicalAndExpression()
		 * while(TokenType == OR | TokenType == '||'){
		 * 	parseLogicalAndExpression()
		 * }
		 */
	}
	void parseLogicalAndExpression() {
		/*
		 * parseLogicalNotExpression()
		 * while(TokenType == AND | TokenType == '&&'){
		 * 	parseLogicalNotExpression()
		 * }
		 */
	}
	void parseLogicalNotExpression() {
		/*
		 * parseNotEqualToExpression()
		 * while(TokenType == NOT | TokenType == '!'){
		 * 	parseNotEqualToExpression()
		 * }
		 */
	}
	void parseNotEqualToExpression() {
		/*
		 * parseEqualEqualExpression()
		 * accept(NOT_EQ)
		 * while(TokenType == '!='){
		 * 	parseEqualEqualExpression()
		 * }
		 */
	}
	void parseEqualEqualExpression() {
		/*
		 * parseRealtionalExpression()
		 * while(TokenType == '=='){
		 * 	parseRealtionalExpression()
		 * }
		 */
	}
	void parseRealtionalExpression() {
		/*
		 * parseBitOrExpression()
		 * while(TokenType == <,>,<=,>=,in ,not in, is, is not ){
		 * 	parseBitOrExpression()
		 * }
		 */
	}
	void parseBitOrExpression() {
		/*
		 * parseBitXorExpression()
		 * while(TokenType == '|'){
		 * 	parseBitXorExpression()
		 * }
		 */
	}
	
	void parseBitXorExpression() {
		/*
		 * parseLogicalAndExpression()
		 * while(TokenType == '^'){
		 * 	parseLogicalAndExpression()
		 * }
		 */
	}
	
	void parseBitAndExpression() {
		/*
		 * parseBitRightShiftExpression()
		 * while(TokenType == '&'){
		 * 	parseBitRightShiftExpression()
		 * }
		 */
	}
	
	void parseBitRightShiftExpression() {
		/*
		 * parseBitLeftShiftExpression()
		 * while(TokenType == '>>' | TokenType == '>>>'){
		 * 	parseBitLeftShiftExpression()
		 * }
		 */
	}
	
	void parseBitLeftShiftExpression() {
		/*
		 * parseSubtractExpression()
		 * while(TokenType == '<<' | TokenType == '<<<'){
		 * 	parseSubtractExpression()
		 * }
		 */
	}
	
	void parseSubtractExpression() {
		/*
		 * parseAdditionExpression()
		 * while(TokenType == '-'){
		 * 	parseAdditionExpression()
		 * }
		 */
	}
	
	void parseAdditionExpression() {
		/*
		 * parseMultiplicationExpression()
		 * while(TokenType == '+'){
		 * 	parseMultiplicationExpression()
		 * }
		 */
	}
	
	void parseMultiplicationExpression() {
		/*
		 * parseDivisionExpression()
		 * while(TokenType == '*'){
		 * 	parseDivisionExpression()
		 * }
		 */
	}
	
	void parseDivisionExpression() {
		/*
		 * parseFactorExpression()
		 * while(TokenType == /, %, // ){
		 * 	parseFactorExpression()
		 * }
		 */
	}
	
	void parseFactorExpression() {
		/*
		 * 
		 * if( TokenType == -, ~ )
		 * 		parseFactorExpression()
		 * parsePowerExpression()
		 * 
		 */
	}
	
	void parsePowerExpression() {
		/*
		 * parseAtomExpression()
		 * while(TokenType == '**'){
		 * 	parseAtomExpression()
		 * }
		 */
	}
	
		
	void parseAtomExpression() {
		/*
		 * List of Trailers
		 * parseAtom()
		 * 
		 * trail = parseTrailer()
		 * while (trail)
		 * 		trail = parseTrailer()
		 */
	}
	void parseAtom() {
		/*
		 * switch TokenType
		 * 		case LPAREN:
		 * 			parseExpressionStatement()
		 * 			accept(RPAREN)
		 * 		case IDENTIFIER:
		 * 			parseIdentifier()
		 * 		case STRINGLITERAL:
		 * 			parseStringLiteral()
		 * 		case NUMLiteral:
		 * 			parseNumberLiteral()
		 * 		case BOOLLiteral:
		 * 			parseBoolLiteral()
		 *  	case NULL:
		 *  		parseNull()
		 *  	case THIS:
		 *  		parseThis()
		 */
	}
	
	void parseIdentifier() {
		/*
		 * REGEX
		 */
	}
	
	void parseStringLiteral(){
		/*
		 * check Quotes
		 * accept(STRING LITERAL)
		 * 
		 */
	}
	
	void parseNumberLiteral() {
		/*
		 * 
		 */
	}
	
	void parseBoolLiteral() {
		/*
		 * 
		 */
	}
	
	
	void parseSubscriptList() {
		/*
		 * while(LBRACKET)
		 * 		parseSubscript()
		 * 
		 */
	}
	
	void parseSubscript() {
		/* 
		 * accept(LBRACKET)
		 * parseNumberLiteral()
		 * if ( COLON)
		 * 		parseNumberLiteral()
		 * accept(LBRACKET)
		 * 
		 * 
		 */
	}
	
	void parseArgList() {
		/*
		 * parseArg()
		 * while(COMMA)
		 * 		parseArg()
		 */
	}
	
	void parseArg() {
		/*
		 * parseValue()
		 */
	}
	
	
	void parseTrailer() {
		/*
		 * if (LPAREN)
		 * 		parseArgList()
		 * else if (DOT)
		 * 		parseIdentifier()
		 * else if (LBRACKET)
		 * 		parseSubscriptList()
		 * else
		 * 		return false
		 * 
		 */
	}
	
	void parseExpressionStatement() {
		/*
		 * parseLogAndExpression()
		 * if (|| | 'or')
		 * 		parseLogAndExpression()
		 * else 
		 * 		if (EQUAL OPERATOR)
		 * 			parseEqualOperator()
		 * 			parseValue()
		 * 		else
		 * 			handle Error
		 * 
		 */
	}
	
	
	
	/*  parseStatements	*/
	
	void parseIfStatement() {
		/*
		 * accept(IF)
		 * accept(LPAREN)
		 * parseLogORExpression()
		 * accept(RPAREN)
		 * accept(LCURLY)
		 * parseBlockStatements()
		 * accept(RCURLY)
		 * if ELSEIF
		 * 		parseElseIfStatement()
		 * if ELSE
		 * 		parseElseStatement()
		 */
	}
	
	void parseElseStatement() {
		/*
		 * accept(ELSE)
		 * accept(LCURLY)
		 * parseBlockStatements()
		 * accept(RCURLY)
		 */
	}
	
	void parseElseIfStatement() {
		/*
		 * List of ElseIfNodes
		 * 
		 * while ( !ELSEIF ){
		 * 		accept(ELSEIF)
		 * 		accept(LPAREN)
		 * 		parseLogORExpression()
		 * 		accept(RPAREN)
		 *		accept(LCURLY)
		 *		parseBlockStatements()
		 *		accept(RCURLY)
		 *		ElseIf.add(ElseIFNode(condition, block)
		 *
		 * return ElseIfNode
		 */
	}
	void parseAssignmentStatement() {
		/*
		 * parseIdentifier()
		 * while (DOT)
		 * 		parseIdentifier()
		 * parseEqualOperator()
		 * parseValue()
		 * accept(SEMI)
		 * 
		 */
		
	}
	
	/* Parse Loops */
	void parseForInit() {
		/*
		 * if ( VAR )
		 * 		accept(VAR)
		 * parseVariableDeclaration()
		 *   
		 */
	}
	
	void parseForCondition() {
		/*
		 * parseLogOrEcpression()
		 */
	}
	
	void parseForIncrement() {
		/*
		 * 
		 * parseAssignmentStatement()
		 */
	}
	void parseForStatement() {
		/*
		 * accept(FOR)
		 * accept(LPAREN)
		 * if (SEMI )
		 * 		parseForCondition()
		 * 		accept(SEMI)
		 * 		parseForIncr()
		 * else 
		 * 		parseForInit()
		 * 		accept(SEMI)
		 * 		parseForCondition()
		 * 		accept(SEMI)
		 * 		parseForIncr()
		 */
	}
	void parseLoopStatement() {
		/*
		 * accept(LOOP)
		 * parseLoopIdentifier()
		 * accept(IN)
		 * parseLoopValue()
		 * parseLoopBlock()
		 * parseCollectionComprehension()
		 * 
		 */
	}
	
	void parseLoopIdentifier() {
		/*
		 * parseIdentifier()
		 * if (COMMA) parseLoopIdentifier()
		 */
	}
	void parseLoopValues() {
		/*
		 * parseCollection()
		 * parseAtomExpr()
		 * parseObjectCreation()
		 */
	}
	void parseLoopBlock() {
		/*
		 * parseBlockStatement()
		 * parseFlowStatement()
		 */
	}
	void parseFlowStatement() {
		/*
		 * if(CONTINUE) return ContinueNode
		 * if(BREAK) return BreakNode
		 */
	}
	void parseCollectionComprehension() {
		/*
		 * List of collComp
		 * 
		 * if(LOOP) collComp.add(parseForComprehension())
		 * if(IF) collComp.add(parseIfComprehension())
		 *  
		 */
	}
	void parseForComprehension() {
		/*
		 * List of forComp
		 * 
		 * forComp.add(parseLoopStatement())
		 * return forComp
		 */
		
	}
	void parseIfComprehension() {
		/*
		 * List of ifComp
		 * 
		 * accept(IF)
		 * accept(LPAREN)
		 * parseLogExpression()
		 * accept(RPAREN)
		 * parseCollection()
		 * 
		 * return ifComp
		 */
	}
	
	/* Parse Loop Done */
	
	/* Parse Collection */
	void parseList() {
		/* 
		 * List of Values
		 * 
		 * accept(LBRACKET)
		 * List.add(parseValue())
		 * if(COMMA or DOTDOT)
		 * 		List.add(parseValue())
		 * else if(LOOP_KEYWORD)
		 * 		List.add(parseForComprehension())
		 * 
		 * return List
		 */
	}
	void parseListCollection() {
		/*
		 * List of Collection
		 * 
		 * if (RBRACKET){ return List.add(ListNode()) }
		 * 
		 * else {
		 * 	List.add(parseList())
		 * }
		 * 
		 * return List
		 */
	}
	
	void parseMap() {
		/*
		 * List of Map<dynamic, dynamic>
		 * 
		 * case RBRACKET:
		 * 		return List.add(map))
		 * case LBRACKET:
		 * 		map.addKey(parseValue())
		 * 		accept(COMMA)
		 * 		map.addValue(parseValue())
		 * 		List.add(map)
		 * 
		 *  return map
		 */
	}
	void parseMapCollection() {
		/*
		 * List of MapCollection
		 * List of Comp
		 * 
		 * case: LBRACKET
		 * 		mapCollection.add(parseMap())
		 * 
		 * 		if (COMMA) : 
		 * 			mapCollection.add(parseMap())
		 * 		if (LOOP )
		 * 			Comp.add(parseForComprehension())
		 * 
		 */
	}
	
	
	void parseCollection() {
		/*
		 * List of Collection
		 * 
		 * read TokenType:
		 * case LBRACKET: 
		 * 		parseListCollection()
		 * case LINK: case SET: 
		 * 		parseCollection()
		 * case MAP:
		 * 		parseMapCollection()
		 *  
		 * return List
		 */
	}
	
	/* Parse Collection Done */
	
	/* Parse Values */
	void parseObjectCreation(){
		/*
		 * List of ObjectNode
		 * 
		 * List.add(parseObject())
		 * 
		 * if Comma { List.add(parseObject()) }
		 * 
		 */
	}
	
	void parseObject() {
		/*
		 * accept(LCURLY)
		 * parseIdentifier()
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
	
	void parseAnnFunction() {
		/*
		 * accept(FUNC)
		 * isAnnFunmc = true
		 * parseFunction()
		 * 
		 */
	}
	
	void parseValue() {
		/* 
		 * ValueNode
		 * 
		 * checks Grammar
		 *  
		 * Case Object: parseObjectCreation()
		 * Case Collection: parseCollection()
		 * Case AnnFunc: parseAnnFunction()
		 * case Operator: parseBitOrOperator()
		 * 
		 * return ValueNode
		 */
	}
	
	/* Parse Values DOne */
	
	void parseVariableDeclaration() {
		/*
		 *  
		 * parseIdentifier()
		 * parseEqualOperator()
		 * parseValue()
		 *  
		 * return VariableNode
		 */
		
	}
	
	void parseVariable() {
		/*
		 * List Variables
		 * checks grammar for variable Declaration
		 * 
		 * 
		 * calls parseVariableDeclaration
		 * 
		 * return List
		 */
	}
	void parseParameter() {
		/*
		 * List of Arguments 
		 * 
		 * responsibility:
		 * Check Grammar and parseIdentifier
		 * 
		 * calls: 
		 * arguments.add = parseIdentifier()
		 * arguments.add = varArgs()
		 * 
		 * return List 
		 */
	}
	
	
	void parseFunction() {
		/* List of Parameter
		 * BlockNode
		 * 
		 * calls: 
		 * name = isAnnFunc ? null : parseIdentifier() 
		 * parameter = parseParameter()
		 * BlockNode= parseBlockStatement()
		 * 
		 * FunctionNode(name, parameter, BlockNode);
		 * returns FunctionNode
		 */
	}
	
	void parseDeclarativeStatement() {
		/*
		 * List of Declarative Statement
		 * 
		 * Checks Type of Statement and calls below methods to parse
		 * calls parseFunction
		 * calls parseVariable
		 */
	}
	
	void block() {
		/*
		 * Handles Cases For Each Type of Block Statements 
		 * like DECL, ASGN, IF etc.
		 * and calls respective methods
		 *
		 * */
	}
	
	void parseBlockStatement() {
		/*
		 * List of block Statements
		 * 
		 * parse Grammar, checks type of Statement and calls block() 
		 */
	}
	
	// return Block Statement Node
	void parseBlock() {
		/* List of block Statements 
		 * calls parseBlockStatement
		 * 
		 * */
	}
	
	// return Import Statement Node
	void parseImportStatement() {
		/* List of Imports */
		
	}
	
	// return Statement Node
	void parseStatement() {
		
		/* List of */ 
	}
	
	// return List Of Statements
	void parseStatements() {
		/* List of Statements
		 * 
		 * calls parseStatement()
		*/ 
	}
	
	// return Root Node
	public void  parse() {
		/* List of Statement Nodes */
		
	}
}

