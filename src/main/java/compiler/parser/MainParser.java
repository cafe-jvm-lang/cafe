package compiler.parser;

import compiler.ast.Node;
import compiler.ast.Node.*;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
import compiler.util.Position;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static compiler.util.Log.Type.*;
import static compiler.util.Messages.message;

public class MainParser extends Parser {
    static {
        ParserFactory.registerParser(ParserType.MAINPARSER, new MainParser());
    }

    private Lexer lexer;
    private Token token;
    private Log log;
    private boolean breakAllowed = false, innerLoop = false, error = false;
    private List<String> debug = new ArrayList<>();
    private MainParser() {
    }

    private MainParser(ParserFactory factory, Lexer lexer) {
        debug.add("PARSING");
        this.lexer = lexer;
        this.log = factory.log;
        // TESTING
        // nextToken();
        // while(token.kind != TokenKind.END) {
        // debug.add(token.kind);
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
        if (!error) {
            lexer.nextToken();
            token = lexer.token();
        }
    }

    private String errorDescription(Position position, String message) {
        return message + ' ' + message(SOURCE_POSITION, position.getStartLine(), position.getStartColumn());
    }

    private boolean accept(TokenKind tokenKindExpected) {
        if (error) return false;
        if (token.kind == TokenKind.ERROR) {
            error = true;
            return false;
        }
        if (tokenKindExpected == token.kind) {
            nextToken();
            return true;
        } else {
            logError(tokenKindExpected);
            return false;
        }
    }

//    private boolean accept(TokenKind tokenKindExpected) {
//        return accept(tokenKindExpected);
//    }

    private void logError(TokenKind tokenKindExpected){
        error = true;
        log.report(SYMBOL_EXPECTED, token.pos,
                errorDescription(token.pos, message(SYMBOL_EXPECTED, tokenKindExpected, token.kind)));
    }

    private void logError(Log.Type issue){
        error = true;
        log.report(issue, token.pos,
                errorDescription(token.pos, message(issue)));

    }

//	void accept(TokenKind kind/* ,Errors error ) */ /* pass specific error type */) {
//		if (kind == token.kind) {
//			nextToken();
//			// return true;
//		} else if (token.kind == TokenKind.ERROR) {
//			error = true;
//			//debug.add("Expected "+ kind+ " Found "+token.kind+" "+ token.pos.line);
//			//log.report(token.pos, Errors.INVALID_IDENTIFIER);
//			// System.exit(0);
//			// return false;
//		} else {
//			// TODO: throw Error
//			error = true;
//			debug.add("Expected "+ kind+ " Found "+token.kind);
//			//log.report(token.pos, Errors.INVALID_IDENTIFIER);
//			// System.exit(0);
//			// return false;
//		}
//	}

    ExprNode parseLogicalOrExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == OR | TokenType == '||'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseLogicalAndExpression();
        while (token.kind == TokenKind.OROP || token.kind == TokenKind.OR) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseLogicalAndExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;

    }

    ExprNode parseLogicalAndExpression() {
        /*
         * parseLogicalNotExpression() while(TokenType == AND | TokenType == '&&'){
         * parseLogicalNotExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseLogicalNotExpression();
        while (token.kind == TokenKind.ANDOP || token.kind == TokenKind.AND) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseLogicalNotExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseLogicalNotExpression() {
        /*
         * parseNotEqualToExpression() while(TokenType == NOT | TokenType == '!'){
         * parseNotEqualToExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseNotEqualToExpression();
        while (token.kind == TokenKind.NOTOP || token.kind == TokenKind.NOT) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseNotEqualToExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseNotEqualToExpression() {
        /*
         * parseEqualEqualExpression() accept(NOT_EQ) while(TokenType == '!='){
         * parseEqualEqualExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseEqualEqualExpression();
        while (token.kind == TokenKind.NOTEQU) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseEqualEqualExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseEqualEqualExpression() {
        /*
         * parseRealtionalExpression() while(TokenType == '=='){
         * parseRealtionalExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseRelationalExpression();
        while (token.kind == TokenKind.EQUEQU) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseRelationalExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseRelationalExpression() {
        /*
         * parseBitOrExpression() while(TokenType == <,>,<=,>=,in ,not in, is, is not ){
         * parseBitOrExpression() }
         */
        if (error) return null;
        Token tk = token;
        ExprNode exp1 = parseBitOrExpression();
        if(exp1 == null){
            error= true;
            return null;
        }
        while (token.kind == TokenKind.LT || token.kind == TokenKind.GT || token.kind == TokenKind.LTE
                || token.kind == TokenKind.GTE || token.kind == TokenKind.IN || token.kind == TokenKind.IS
                || token.kind == TokenKind.NOT) {
            if (error)
                return null;
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
        if (error) return null;
        if(exp1 != null)
            exp1.setFirstToken(tk);
        return exp1;
    }

    ExprNode parseBitOrExpression() {
        /*
         * parseBitXorExpression() while(TokenType == '|'){ parseBitXorExpression() }
         */

        if (error) return null;
        ExprNode exp1 = parseBitXorExpression();
        while (token.kind == TokenKind.BITOR) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseBitXorExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseBitXorExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == '^'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseBitAndExpression();
        while (token.kind == TokenKind.BITAND) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseBitAndExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseBitAndExpression() {
        /*
         * parseBitRightShiftExpression() while(TokenType == '&'){
         * parseBitRightShiftExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseBitRightShiftExpression();
        while (token.kind == TokenKind.ANDOP) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseBitRightShiftExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseBitRightShiftExpression() {
        /*
         * parseBitLeftShiftExpression() while(TokenType == '>>' | TokenType == '>>>'){
         * parseBitLeftShiftExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseBitLeftShiftExpression();
        while (token.kind == TokenKind.RSHIFT || token.kind == TokenKind.TRSHIFT) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseBitLeftShiftExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseBitLeftShiftExpression() {
        /*
         * parseSubtractExpression() while(TokenType == '<<' | TokenType == '<<<'){
         * parseSubtractExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseSubtractExpression();
        while (token.kind == TokenKind.LSHIFT) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseSubtractExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseSubtractExpression() {
        /*
         * parseAdditionExpression() while(TokenType == '-'){ parseAdditionExpression()
         * }
         */
        if (error) return null;
        ExprNode exp1 = parseAdditionExpression();
        while (token.kind == TokenKind.SUB) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseAdditionExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseAdditionExpression() {
        /*
         * parseMultiplicationExpression() while(TokenType == '+'){
         * parseMultiplicationExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseMultiplicationExpression();
        while (token.kind == TokenKind.ADD) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseMultiplicationExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseMultiplicationExpression() {
        /*
         * parseDivisionExpression() while(TokenType == '*'){ parseDivisionExpression()
         * }
         */
        if (error) return null;
        ExprNode exp1 = parseDivisionExpression();
        while (token.kind == TokenKind.MUL) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseDivisionExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseDivisionExpression() {
        /*
         * parseFactorExpression() while(TokenType == /, %, // ){
         * parseFactorExpression() }
         */
        if (error) return null;
        ExprNode exp1 = parseFactorExpression();
        while (token.kind == TokenKind.DIV || token.kind == TokenKind.MOD || token.kind == TokenKind.FLOORDIV) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExprNode exp2 = parseFactorExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseFactorExpression() {
        /*
         *
         * if( TokenType == -, ~ ) parseFactorExpression() parsePowerExpression()
         *
         */
        if (error) return null;
        ExprNode exp1 = null;
        if (token.kind == TokenKind.SUB || token.kind == TokenKind.TILDE) {
            if (prevToken().kind == TokenKind.IDENTIFIER) {
                // Handle Case where previous token is identifier and sould not generate
                // UnaryOperator
                exp1 = parsePowerExpression();
            } else {
                Token operatorToken = token;
                accept(token.kind);
                exp1 = parsePowerExpression();
                exp1 = new UnaryExprNode(exp1, operatorToken.value());
            }
        } else {
            exp1 = parsePowerExpression();
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parsePowerExpression() {
        /*
         * parseAtomExpression() while(TokenType == '**'){ parseAtomExpression() }
         */
        if (error) return null;
        ExprNode exp1 = null, exp2;
        try {
            exp1 = parseAtomExpression();
            while (token.kind == TokenKind.POWER) {
                if (error)
                    return null;
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
        if (error) return null;
        return exp1;

    }

    ExprNode parseTrailer(ExprNode oExp) throws ParseException {
        ExprNode node = null;
        if (token.kind == TokenKind.LPAREN || token.kind == TokenKind.DOT || token.kind == TokenKind.LSQU) {
            switch (token.kind) {
                case LPAREN:
                    accept(TokenKind.LPAREN);
                    node = new FuncCallNode(oExp, new ArgsListNode(parseArgList()));
                    accept(TokenKind.RPAREN);
                    break;

                case LSQU:
                    ExprNode exp1, exp2;
                    debug.add("Atom Expr: " + token.kind);
                    while (token.kind == TokenKind.LSQU) {
                        if (error)
                            return null;
                        accept(TokenKind.LSQU);
                        if (token.kind == TokenKind.IDENTIFIER)
                            exp1 = parseIdentifier();
                        else
                            exp1 = parseNumberLiteral();
                        if (token.kind == TokenKind.COLON) {
                            accept(TokenKind.COLON);
                            if (token.kind == TokenKind.IDENTIFIER)
                                exp2 = parseIdentifier();
                            else
                                exp2 = parseNumberLiteral();
                            accept(TokenKind.RSQU);
                            node = new SliceNode(oExp, exp1, exp2);
                        } else {
                            accept(TokenKind.RSQU);
                            node = new SubscriptNode(oExp, exp1);
                            oExp = node;
                        }

                    }
                    break;
                case DOT:
                    ExprNode e1;
                    debug.add("Atom DOT:" + oExp);
                    accept(TokenKind.DOT);
                    e1 = parseIdentifier();
                    if (error)
                        return null;
                    ExprNode trail = e1;
                    while (token.kind != TokenKind.DOT && (token.kind == TokenKind.LSQU || token.kind == TokenKind.LPAREN)) {
                        trail = parseTrailer(trail);
                    }
                    if (trail == null)
                        node = new ObjectAccessNode(oExp, e1);
                    else
                        node = new ObjectAccessNode(oExp, trail);
                    debug.add("Token Kind: " + token.kind);
                    break;
            }
        }
        return node;
    }

    ExprNode parseAtomExpression() throws ParseException {
        /*
         * List of Trailers parseAtom()
         *
         * trail = parseTrailer() while (trail) trail = parseTrailer()
         */

        if (error) return null;
        debug.add("Atom Expr Node Token: " + token.kind);
        ExprNode oExp = parseAtom();
        if(oExp == null){
            error = true;
            return null;
        }
        debug.add("Atom Expr Node Token: " + token.kind);
        if (oExp instanceof IdenNode || oExp instanceof ThisNode) {
            ExprNode trailer;
            while ((trailer = parseTrailer(oExp)) != null) {
                oExp = trailer;
            }
        }
        
        if (error) return null;
        if(oExp == null){
            error = true;
            return null;
        }
        return oExp;
    }

    StmtNode parseExprStmt() {
        if (error) return null;
        ExprNode exp1 = parseLogicalAndExpression();
        ExprNode exp2;
        debug.add("Expr Stmt : " + token.kind);
        if (token.kind == TokenKind.OR || token.kind == TokenKind.OROP) {
            String op = token.value();
            accept(token.kind);
            exp2 = parseLogicalAndExpression();
            exp1 = new BinaryExprNode(exp1, exp2, op);
        } else if (token.kind == TokenKind.EQU) {
            accept(token.kind);
            exp2 = parseValue();

            return new AsgnStmtNode(exp1, exp2);
        }
        if (error) return null;
        return exp1;
    }

    ExprNode parseAtom() {
        /*
         * switch TokenType case LPAREN: parseExpressionStatement() accept(RPAREN) case
         * IDENTIFIER: parseIdentifier() case STRINGLITERAL: parseStringLiteral() case
         * NUMLiteral: parseNumberLiteral() case BOOLLiteral: parseBoolLiteral() case
         * NULL: parseNull() case THIS: parseThis()
         */
        if (error) return null;
        debug.add("Atom Node Token: " + token.kind);
        StmtNode exp1 = null;
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
                    debug.add("Exception Thrown in parseAtom by NUMLIT");
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
                break;
            case THIS:
                exp1 = parseThis();
                break;
            default:
                logError(INVALID_EXPRESSION);
        }
        if (error) return null;
        if (exp1 == null){
            error = true;
            return null;
        }
        return (ExprNode) exp1;

    }

    ExprNode parseNull() {
        if (error) return null;
        Token tk = token;
        accept(TokenKind.NULL);
        NullNode nullNode = new NullNode();
        nullNode.setFirstToken(tk);
        return nullNode;
    }

    ExprNode parseThis() {
        if (error) return null;
        Token tk = token;
        accept(TokenKind.THIS);
        ThisNode thisNode = new ThisNode();
        thisNode.setFirstToken(tk);
        return thisNode;
    }

    ExprNode parseIdentifier() {
        /*
         * Create Identifier Node. return IdentNode()
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.IDENTIFIER);
        IdenNode iden = new IdenNode(prev.value());
        iden.setFirstToken(prev);
        return iden;

    }

    ExprNode parseStringLiteral() {
        /*
         * check Quotes accept(STRING LITERAL)
         *
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.STRLIT);
        return new StrLitNode(prev.value());
    }

    ExprNode parseNumberLiteral() throws ParseException {
        /*
         *
         */
        if (error) return null;
        Token prevToken = token;
        accept(TokenKind.NUMLIT);
        Number num, num2;
        debug.add("Num Literal PrevToken: " + prevToken.kind);
        debug.add("Num Literal Token: " + token.kind);
        if (token.kind == TokenKind.DOT) {
            nextToken();
            if (token.kind == TokenKind.NUMLIT) {
                num = NumberFormat.getInstance()
                                  .parse(prevToken.value() + '.' + token.value());
                accept(TokenKind.NUMLIT);
                if (error) return null;
                return new NumLitNode(num);
            } else {
                accept(TokenKind.NUMLIT);
            }
        } else {
            num = NumberFormat.getInstance()
                              .parse(prevToken.value());
            if (num instanceof Long) {
                if ((long) num <= Integer.MAX_VALUE) {
                    num2 = num.intValue();
                } else {
                    num2 = num;
                }
            } else {
                num2 = num;
            }
            if (error) return null;
            return new NumLitNode(num2);
        }

        return null;
    }

    ExprNode parseBoolLiteral() {
        /*
         *
         */
        if (error) return null;
        Token tk = token;
        if (token.kind == TokenKind.TRUE) {
            accept(TokenKind.TRUE);
            if (error) return null;

            BoolLitNode boolLit = new BoolLitNode(true);
            boolLit.setFirstToken(tk);
            return boolLit;
        } else if (token.kind == TokenKind.FALSE) {
            accept(TokenKind.FALSE);
            if (error) return null;

            BoolLitNode boolLit = new BoolLitNode(false);
            boolLit.setFirstToken(tk);
            return boolLit;
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
        if (error) return null;
        List<ExprNode> args = new ArrayList<>();
        while (token.kind != TokenKind.RPAREN) {
            if (error)
                return null;
            args.add(parseValue());
            if (token.kind != TokenKind.RPAREN)
                accept(TokenKind.COMMA);
        }
        if (error) return null;
        return args;

    }

    void parseArg() {
        /*
         * parseValue()
         */
    }

    //void parseTrailer() {
    /*
     * if (LPAREN) parseArgList() else if (DOT) parseIdentifier() else if (LBRACKET)
     * parseSubscriptList() else return false
     *
     */
    //}

    void parseExpressionStatement() {
        /*
         * parseLogAndExpression() if (|| | 'or') parseLogAndExpression() else if (EQUAL
         * OPERATOR) parseEqualOperator() parseValue() else handle Error
         *
         */
    }

    /* parseStatements */
    IfStmtNode parseIf() {
        if (error) return null;
        ExprNode ifCond=null;
        BlockNode ifBlock = new BlockNode();

        Token firstToken = token;

        nextToken();
        accept(TokenKind.LPAREN);
        ifCond = parseLogicalOrExpression();
        if(ifCond == null){
        //    log.report(Type.ERROR, token.pos, errorDescription(token.pos,  "If without condition!"));
            logError(INVALID_EXPRESSION);
            error = true;
            return null;
        }
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        if (token.kind != TokenKind.RCURLY)
            ifBlock = parseLoopBlock();

        accept(TokenKind.RCURLY);

        if (error) return null;

        IfStmtNode ifNode = new IfStmtNode(ifCond, ifBlock);
        ifNode.setFirstToken(firstToken);
        return ifNode;
    }

    StmtNode parseIfStatement() {
        /*
         * Parse If Statement and check if there any 'else' is there, if 'yes' then
         * parseIt and Break otherwise parse Else if statement and append to a list
         */

        if (error) return null;

        IfStmtNode ifNode;
        if ((ifNode = parseIf()) != null) {
            StmtNode elseBlock = null;
            if (token.kind == TokenKind.ELSE) {
                Token elseFT = token;
                nextToken();
                if (token.kind == TokenKind.IF) {
                    elseBlock = parseIfStatement();
                } else if (accept(TokenKind.LCURLY)) {
                    BlockNode blockNode = new BlockNode();
                    List<StmtNode> stmt = new LinkedList<>();
                    if (token.kind != TokenKind.RCURLY)
                        stmt = parseBlock();
                    blockNode.setStmt(stmt);
                    accept(TokenKind.RCURLY);
                    elseBlock = new ElseStmtNode(ifNode, blockNode);
                    elseBlock.setFirstToken(elseFT);
                }
                if (error) return null;
                ifNode.setElsePart(elseBlock);
            }
            return ifNode;
        }
        return null;
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
        if (error) return null;

        ExprNode exp1 = parseIdentifier();
        accept(TokenKind.DOT);
        ExprNode exp2 = parseIdentifier();
        exp1 = new ObjectAccessNode(exp1, exp2);
        debug.add("Obj Access Parse Assign: " + exp1);
        while (token.kind == TokenKind.DOT) {
            if (error)
                return null;
            accept(TokenKind.DOT);
            exp2 = parseIdentifier();
            exp1 = new ObjectAccessNode(exp1, exp2);
        }
        accept(TokenKind.EQU);
        ExprNode exp = parseValue();
        accept(TokenKind.SEMICOLON);
        if (error) return null;
        return new AsgnStmtNode(exp1, exp);
    }

    /* Parse Loops */
    List<StmtNode> parseForInit() {
        /*
         *
         *
         */
        if (error) return null;
        List<StmtNode> init = null;
        ExprNode iden, val;
        if (token.kind == TokenKind.SEMICOLON)
            return init;
        init = new ArrayList<>();
        while (token.kind == TokenKind.VAR || token.kind == TokenKind.IDENTIFIER) {
            if (error)
                return null;
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
        if (error) return null;
        if(init.isEmpty()) return null;
        return init;
    }

    ExprNode parseForCondition() {
        /*
         * parseLogOrEcpression()
         */
        if (error) return null;
        ExprNode cond = null;
        if (token.kind == TokenKind.SEMICOLON)
            return cond;
        cond = parseLogicalOrExpression();
        if (error) return null;
        return cond;
    }

    List<AsgnStmtNode> parseForIncrement() {
        /*
         *
         * parseAssignmentStatement()
         */
        if (error) return null;
        ExprNode iden, val, exp2;
        List<AsgnStmtNode> incrNodes = null;
        if (token.kind == TokenKind.RPAREN) {
            if (error) return null;
            return incrNodes;
        }

        incrNodes = new ArrayList<>();
        while (token.kind == TokenKind.IDENTIFIER) {
            if (error)
                return null;
            iden = parseIdentifier();
            // accept(TokenKind.DOT);
            // ExprNode expn = parseIdentifier();
            // iden = new ObjectAccessNode(iden, expn);
            while (token.kind == TokenKind.DOT) {
                if (error)
                    return null;
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
        if (error) return null;
        return incrNodes;
    }

    StmtNode parseForStatement() {
        /*
         * accept(FOR) accept(LPAREN) if (SEMI ) parseForCondition() accept(SEMI)
         * parseForIncr() else parseForInit() accept(SEMI) parseForCondition()
         * accept(SEMI) parseForIncr()
         */
        if (error) return null;
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
        if (error) return null;
        return new ForStmtNode(init, cond, incrNode, block);
    }

    StmtNode parseLoopStatement() {
        /*
         * accept(LOOP) parseLoopIdentifier() accept(IN) parseLoopValue()
         * parseLoopBlock() parseCollectionComprehension()
         *
         */
        if (error) return null;
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
        try {
            exp = parseAtomExpression();
        } catch (ParseException e) {
        }

        if (exp == null) {
            if (token.kind == TokenKind.LCURLY)
                exp = parseCollection();
            else
                exp = parseObjectCreation();

        }
        accept(TokenKind.LCURLY);
        block = parseLoopBlock();
        accept(TokenKind.RCURLY);
        if (error) return null;
        return new LoopStmtNode(iden1, iden2, exp, block);
    }

    StmtNode parseFlowStatement() {
        /*
         * if(CONTINUE) return ContinueNode if(BREAK) return BreakNode
         */
        if (error) return null;
        Token tk=token;
        if (breakAllowed)
            if (token.kind == TokenKind.CONTINUE) {
                if (error) return null;
                accept(TokenKind.CONTINUE);
                accept(TokenKind.SEMICOLON);
                ContinueStmtNode continueStmtNode = new ContinueStmtNode();
                continueStmtNode.setFirstToken(tk);
                return continueStmtNode;
            } else {
                if (error) return null;
                accept(TokenKind.BREAK);
                accept(TokenKind.SEMICOLON);
                BreakStmtNode breakStmtNode = new BreakStmtNode();
                breakStmtNode.setFirstToken(tk);
                return breakStmtNode;
            }
        else {
            error = true;
            accept(TokenKind.IDENTIFIER);
        }
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
        if (error) return null;
        List<ExprNode> listNode = new ArrayList<>();
        ExprNode exp1, exp2;
        exp1 = parseValue();
        if (token.kind == TokenKind.RANGE) {
            accept(TokenKind.RANGE);
            exp2 = parseValue();
            accept(TokenKind.RSQU);
            return new RangeNode(exp1, exp2, RangeNode.Type.LIST);
        }
        listNode.add(exp1);
        while (token.kind != TokenKind.RSQU) {
            if (error)
                return null;
            accept(TokenKind.COMMA);
            if (token.kind == TokenKind.RSQU) accept(TokenKind.IDENTIFIER);
            listNode.add(parseValue());
        }
        accept(TokenKind.RSQU);
        if (error) return null;
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
        if (error) return null;
        accept(TokenKind.LSQU);
        if (token.kind == TokenKind.RSQU) {
            accept(TokenKind.RSQU);
            if (error) return null;
            return new ListCollNode();
        } else if (token.kind == TokenKind.LOOP) {
            if (error) return null;
            return parseComprehension("list");        // Accept Identifier Before
        } else {
            if (error) return null;
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
        if (error) return null;
        Map<ExprNode, ExprNode> pairs = new LinkedHashMap<>();
        nextToken();
        accept(TokenKind.LSQU);
        debug.add("Map Collection : " + token.kind);
        while (token.kind != TokenKind.RSQU) {
            if (error)
                return null;
            ExprNode exp2 = new MapCollNode(), exp1 = new MapCollNode();
            if (token.kind == TokenKind.COMMA)
                nextToken();
            accept(TokenKind.LSQU);
            if (token.kind == TokenKind.RSQU) {
                pairs.put(exp1, exp2);
                accept(TokenKind.RSQU);
                accept(TokenKind.COMMA);
                continue;
            } else if (token.kind == TokenKind.LOOP) {
                return parseComprehension("map");        // Accept Identifier Before
            } else {
                exp1 = parseValue();
                accept(TokenKind.COMMA);
                exp2 = parseValue();
                accept(TokenKind.RSQU);
            }

            pairs.put(exp1, exp2);
        }
        accept(TokenKind.RSQU);
        if (error) return null;
        return new MapCollNode(pairs);
    }

    CompTypeNode parseComprehension(String type) {
        if (error) return null;
        IdenNode iden1, iden2 = null;
        ExprNode exp = null;
        BlockNode block = null;
        ExprNode ifCond;

        CompTypeNode mapComp = type == "map" ? new MapCompNode()
                : type == "list" ? new ListCompNode()
                : type == "set" ? new SetCompNode() : type == "link" ? new LinkCompNode() : null;

        while (token.kind != TokenKind.RSQU) {
            if (error)
                return null;
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
                    debug.add("Comprehension: " + token.kind);
                    mapComp.addExpr(new CompIfNode(ifCond));
                    break;
                default:
                    error = true;
                    accept(TokenKind.IDENTIFIER);
            }
        }
        accept(TokenKind.RSQU);
        if (error) return null;
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
        if (error) return null;
        List<ExprNode> setNode = new ArrayList<>();
        ExprNode exp1, exp2;
        exp1 = parseValue();
        if (token.kind == TokenKind.RANGE) {
            accept(TokenKind.RANGE);
            exp2 = parseValue();
            accept(TokenKind.RSQU);
            return new RangeNode(exp1, exp2, RangeNode.Type.SET);
        }
        setNode.add(exp1);
        while (token.kind != TokenKind.RSQU) {
            if (error)
                return null;
            accept(TokenKind.COMMA);
            if (token.kind == TokenKind.RSQU) accept(TokenKind.IDENTIFIER);
            setNode.add(parseValue());
        }
        accept(TokenKind.RSQU);
        if (error) return null;
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
        if (error) return null;
        nextToken();
        accept(TokenKind.LSQU);
        if (token.kind == TokenKind.RSQU) {
            accept(TokenKind.RSQU);
            if (error) return null;
            return new SetCollNode();
        } else if (token.kind == TokenKind.LOOP) {
            if (error) return null;
            return parseComprehension("set");    // Accept Identifier Before
        } else {
            if (error) return null;
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
        if (error) return null;
        List<ExprNode> listNode = new ArrayList<>();
        ExprNode exp1, exp2;
        exp1 = parseValue();
        if (token.kind == TokenKind.RANGE) {
            accept(TokenKind.RANGE);
            exp2 = parseValue();
            accept(TokenKind.RSQU);
            return new RangeNode(exp1, exp2, RangeNode.Type.LINK);
        }
        listNode.add(exp1);
        while (token.kind != TokenKind.RSQU) {
            if (error)
                return null;
            accept(TokenKind.COMMA);
            if (token.kind == TokenKind.RSQU) accept(TokenKind.IDENTIFIER);
            listNode.add(parseValue());
        }
        accept(TokenKind.RSQU);
        if (error) return null;
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
        if (error) return null;
        nextToken();
        accept(TokenKind.LSQU);
        if (token.kind == TokenKind.RSQU) {
            accept(TokenKind.RSQU);
            if (error) return null;
            return new LinkCollNode();
        } else if (token.kind == TokenKind.LOOP) {
            if (error) return null;
            return parseComprehension("link");        // Accept Identifier Before
        } else {
            if (error) return null;
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
        if (error) return null;
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
        if (error) return null;
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

        if (error) return null;
        Map<IdenNode, ExprNode> object = new LinkedHashMap<>();
        IdenNode idenNode;
        ExprNode exprNode;

        accept(TokenKind.LCURLY);
        while (token.kind != TokenKind.RCURLY) {
            if (error)
                return null;
            idenNode = (IdenNode) parseIdentifier();
            accept(TokenKind.COLON);
            exprNode = parseValue();
            object.put(idenNode, exprNode);
            debug.add("Obj Creation Token : " + token.kind);
            if (TokenKind.RCURLY != token.kind)
                accept(TokenKind.COMMA);
        }
        debug.add("Object Creation: " + object);
        accept(TokenKind.RCURLY);
        if (error) return null;
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
        if (error) return null;
        List<StmtNode> stmt = new ArrayList<>();
        accept(TokenKind.FUNC);
        accept(TokenKind.LPAREN);
        ParameterListNode params = parseParameter();
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        debug.add("Ann Func Node: " + token.kind);
        while (token.kind != TokenKind.RCURLY) {
            if (error)
                return null;
            List<StmtNode> stm = parseBlock();
            if (stm == null) return null;
            stmt.addAll(stm);
        }
        debug.add("Ann Func Node: " + token.kind);
        accept(TokenKind.RCURLY);
        BlockNode block = new BlockNode(); // BlockNode(stmt);
        block.setStmt(stmt);
        if (error) return null;
        return new AnnFuncNode(params, block);
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
        if (error) return null;
        ExprNode valExpr = null;
        debug.add("Parse Value: " + token.kind);
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

            default:
                valExpr = parseBitOrExpression();
                break;

        }
        if (error) return null;
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
        if (error) return null;
        List<VarDeclNode> varDeclNodes = new ArrayList<>();
        accept(TokenKind.VAR);
        while (token.kind != TokenKind.SEMICOLON) {
            if (error)
                return null;
            Token tk = token;
            IdenNode idenNode = (IdenNode) parseIdentifier();
            ExprNode exp = null;
            if (error)
                return null;
            if (token.kind == TokenKind.EQU) {
                nextToken();
                exp = parseValue();
            }
            if (token.kind != TokenKind.SEMICOLON) {
                accept(TokenKind.COMMA);
            }
            debug.add("Var Decl: " + exp);
            VarDeclNode varDecl = new VarDeclNode(idenNode, exp);
            varDecl.setFirstToken(tk);
            varDeclNodes.add(varDecl);
        }
        accept(TokenKind.SEMICOLON);
        if (error) return null;
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
        if (error) return null;
        List<ConstDeclNode> constDeclNodes = new ArrayList<>();
        accept(TokenKind.CONST);
        while (token.kind != TokenKind.SEMICOLON) {
            if (error)
                return null;
            Token tk = token;
            IdenNode idenNode = (IdenNode) parseIdentifier();
            accept(TokenKind.EQU);
            ExprNode exp = parseValue();
            if (token.kind != TokenKind.SEMICOLON)
                accept(TokenKind.COMMA);
            ConstDeclNode constDecl = new ConstDeclNode(idenNode, exp);
            constDecl.setFirstToken(tk);
            constDeclNodes.add(constDecl);
        }
        accept(TokenKind.SEMICOLON);
        if (error) return null;
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
        if (error) return null;
        boolean varArg = false;
        List<IdenNode> idenNodes = new ArrayList<>();

        while (token.kind != TokenKind.RPAREN) {
            if (error)
                return null;
            debug.add("ARG List: " + token.kind);
            debug.add("ARG List: " + token.value());
            if (error)
                return null;
            if (token.kind == TokenKind.VARARGS) {
                accept(TokenKind.VARARGS);
                varArg = true;
                idenNodes.add((IdenNode) parseIdentifier());
                // accept(TokenKind.RPAREN);
                break;
            }
            idenNodes.add((IdenNode) parseIdentifier());
            if (TokenKind.RPAREN != token.kind)
                accept(TokenKind.COMMA);
        }

        if (error) return null;
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
        if (error) return null;
        accept(TokenKind.FUNC);
        Token tk = token;
        ExprNode funcName = parseIdentifier();
        accept(TokenKind.LPAREN);
        ParameterListNode arg = parseParameter();
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        List<StmtNode> stmt = new ArrayList<>();
        while (token.kind != TokenKind.RCURLY) {
            if (error)
                return null;
            List<StmtNode> stm = parseBlock();
            if (stm == null) return null;
            stmt.addAll(stm);
        }
        accept(TokenKind.RCURLY);
        BlockNode block = new BlockNode();
        block.setStmt(stmt);
        if (error) return null;

        FuncDeclNode funcDecl = new FuncDeclNode((IdenNode) funcName, arg, block);
        funcDecl.setFirstToken(tk);
        return funcDecl;
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
        if (error) return null;
        accept(TokenKind.RET);
        Token tk = token;
        ExprNode exp = parseValue();
        debug.add("Return : " + exp);
        accept(TokenKind.SEMICOLON);
        if (error) return null;

        ReturnStmtNode rtrnNode = new ReturnStmtNode(exp);
        rtrnNode.setFirstToken(tk);
        return rtrnNode;
    }

    BlockNode parseLoopBlock() {
        /*
         * parseBlockStatement() parseFlowStatement()
         */
        if (error) return null;
        List<StmtNode> blockStats = new ArrayList<>();
        BlockNode blockNode = new BlockNode();
        while (token.kind != TokenKind.RCURLY) {
            if (error)
                return null;
            switch (token.kind) {
                case CONTINUE:
                case BREAK:
                    blockStats.add(parseFlowStatement());
                    break;
                default:
                    List<StmtNode> stm = parseBlock();
                    if (stm == null) return null;
                    blockStats.addAll(stm);
            }
        }
        if (error) return null;
        blockNode.setStmt(blockStats);
        return blockNode;
    }

    // return Block Statement Node
    List<StmtNode> parseBlock() {
        /*
         * List of block Statements calls parseBlockStatement
         */
        if (error) return null;
        List<StmtNode> blockStmt = new ArrayList<>();
        debug.add("Token kind " + token.kind);
        switch (token.kind) {
            case VAR:
                List<VarDeclNode> stm = parseVariable();
                if (stm == null) return null;
                blockStmt.addAll(stm);
                break;
            case CONST:
                List<ConstDeclNode> stm1 = parseConstVariable();
                if (stm1 == null) return null;
                blockStmt.addAll(stm1);
                break;
            case FUNC:
                DeclNode decl = parseFunctionDeclaration();
                if (decl == null) return null;
                blockStmt.add(decl);
                break;
            case IF:
                StmtNode stm2 = parseIfStatement();
                if (stm2 == null) return null;
                blockStmt.add(stm2);
                break;
            case FOR:
                innerLoop = breakAllowed ? true : false;
                breakAllowed = true;
                StmtNode stm3 = parseForStatement();
                if (stm3 == null) return null;
                blockStmt.add(stm3);
                breakAllowed = innerLoop ? true : false;
                innerLoop = false;
                break;
            case LOOP:
                StmtNode stm4 = parseLoopStatement();
                if (stm4 == null) return null;
                blockStmt.add(stm4);
                break;
            case RET:
                StmtNode stm5 = parseReturnStatement();
                if (stm5 == null) return null;
                blockStmt.add(stm5);
                break;
            case IDENTIFIER:
            case THIS: case NULL:
                StmtNode stm6 = parseExprStmt();
                if (stm6 == null) return null;
                blockStmt.add(stm6);
                debug.add("Block Stmt: " + token.kind);
                accept(TokenKind.SEMICOLON);
                break;
            case BREAK: case CONTINUE:
                StmtNode stm7 = parseFlowStatement();
                blockStmt.add(stm7);
                debug.add("Block Stmt: " + token.kind);
                break;
            default:
                logError(TokenKind.IDENTIFIER);
        }
        if (error) return null;
        debug.add("Block Stmt: " + blockStmt);
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
        if (error) return null;

        nextToken();
        debug.add("Stmt Node Token: " + token.kind);
        List<StmtNode> tree = new ArrayList<>();
        while (token.kind != TokenKind.END) {
            if (error)
                return null;
            switch (token.kind) {
                case IMPORT:
                    break;
                default:
                    List<StmtNode> stmt = parseBlock();
                    if (stmt == null) return null;
                    tree.addAll(stmt);
                    break;
            }
        }
        if (error) return null;
        debug.add("Block Statements " + tree);
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
        if (error) return null;
        ProgramNode node = parseStatements();
        if (error) return null;
        return node;
    }

    @Override
    public Node parse() {
        // while(token.kind != TokenKind.END) {
        // if(token.kind == TokenKind.ERROR) {
        // return;
        // }
        // debug.add(token.kind+" "+token.value()+" "+token.pos);
        // lexer.nextToken();
        // token = lexer.token();
        // }

        // Parser p = parseProgram();
        // return p;

        return parseProgram();
    }
}
