/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

package compiler.parser;

import compiler.ast.Node;
import compiler.ast.Node.*;
import compiler.gen.ASTToCafeIrVisitor;
import compiler.gen.AnnFuncNameGenerator;
import compiler.ir.*;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
import compiler.util.Position;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static compiler.util.Log.Type.*;
import static compiler.util.Messages.message;

public class ParseIR extends Parser {
    static {
        ParserFactory.registerParser(ParserType.MAINPARSER, new ParseIR());
    }

    private static final class Context {
        final static ParseIR.Context context = new ParseIR.Context();

        public CafeModule module;
        private final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
        private final Deque<Node.FuncNode> functionStack = new LinkedList<>();
        private final Deque<Deque<Object>> objectStack = new LinkedList<>();
        private final Deque<ForLoopStatement> forLoopStack = new LinkedList<>();
        private final AnnFuncNameGenerator annFuncNameGenerator = new AnnFuncNameGenerator();

        private boolean isModuleScope = true;

        private boolean isProperty = false;

        // for functions to be exported
        private boolean isExport = false;

        public void enterProperty() {
            isProperty = true;
        }

        public void leaveProperty() {
            isProperty = false;
        }

        public boolean isProperty() {
            return isProperty;
        }

        private Context() {
        }

        public CafeModule createModule(String moduleName) {
            ReferenceTable global = new ReferenceTable();
            referenceTableStack.push(global);
            module = CafeModule.create(moduleName, global);
            return module;
        }

        public Block enterScope() {
            ReferenceTable blockReferenceTable = referenceTableStack.peek()
                    .fork();
            referenceTableStack.push(blockReferenceTable);
            return Block.create(blockReferenceTable);
        }

        public void leaveScope() {
            referenceTableStack.pop();
        }

        public void enterFunc(Node.FuncNode n) {
            isModuleScope = false;
            functionStack.push(n);
            annFuncNameGenerator.enter();
        }

        public void leaveFunc() {
            functionStack.pop();
            if (functionStack.size() == 0)
                isModuleScope = true;
            annFuncNameGenerator.leave();
        }

        public String getNextAnnFuncName() {
            String name = annFuncNameGenerator.current();
            annFuncNameGenerator.next();
            return name;
        }

        public enum Scope {
            GLOBAL, LOCAL, CLOSURE
        }

        public ParseIR.Context.Scope currentScope() {
            if (functionStack.size() == 0)
                return ParseIR.Context.Scope.GLOBAL;
            if (functionStack.size() == 1)
                return ParseIR.Context.Scope.LOCAL;
            return ParseIR.Context.Scope.CLOSURE;
        }

        public void newObjectStack() {
            objectStack.push(new LinkedList<>());
        }

        public void popObjectStack() {
            objectStack.pop();
        }

        public void push(Object object) {
            if (objectStack.isEmpty()) {
                newObjectStack();
            }
            objectStack.peek()
                    .push(object);
        }

        public Object pop() {
            return objectStack.peek()
                    .pop();
        }

        public Object peek() {
            return objectStack.peek();
        }

        SymbolReference createSymbolReference(String name, SymbolReference.Kind kind, SymbolReference.Scope scope) {
            SymbolReference ref = SymbolReference.of(name, kind, scope);
            referenceTableStack.peek()
                    .add(ref);
            return ref;
        }

        public SymbolReference createSymbolReference(String name, Node.Tag tag) {
            return createSymbolReference(name, getSymbolKind(tag), getSymbolScope());
        }

        public SymbolReference getReference(String name) {
            return referenceTableStack.peek()
                    .get(name);
        }

        SymbolReference.Kind getSymbolKind(Node.Tag tag) {
            if (tag == Node.Tag.VARDECL) {
                return SymbolReference.Kind.VAR;
            } else if (tag == Node.Tag.CONSTDECL) {
                return SymbolReference.Kind.CONST;
            }
            throw new AssertionError("Invalid Symbol Kind");
        }

        SymbolReference.Scope getSymbolScope() {
            // There can be only 2 visible symbol scopes: GLOBAL & LOCAL.
            // A symbol declared inside a closure is LOCAL to that closure & a CLOSURE itself is LOCAL to its parent block.
            // So there is no CLOSURE scope for symbols.
            ParseIR.Context.Scope scope = currentScope();
            if (scope == ParseIR.Context.Scope.GLOBAL)
                return SymbolReference.Scope.GLOBAL;
            if (scope == ParseIR.Context.Scope.LOCAL || scope == ParseIR.Context.Scope.CLOSURE)
                return SymbolReference.Scope.LOCAL;
            throw new AssertionError("Invalid Symbol Scope");
        }

        public void addFunction(CafeFunction function) {
            context.module.addFunction(function);
        }
    }

    public CafeModule parseToIR(String moduleName) {
        ParseIR.Context.context.createModule(moduleName);
        ParseIR.Context.context.newObjectStack();
        parse();
        return ParseIR.Context.context.module;
    }


    private Lexer lexer;
    private Token token;
    private Log log;
    private boolean breakAllowed = false, innerLoop = false, error = false;
    private List<String> debug = new ArrayList<>();

    private ParseIR() {
    }

    private ParseIR(ParserFactory factory, Lexer lexer) {
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
    protected ParseIR instance(ParserFactory factory, Lexer lexer) {
        return new ParseIR(factory, lexer);
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

    private void logError(TokenKind tokenKindExpected) {
        error = true;
        log.report(SYMBOL_EXPECTED, token.pos,
                errorDescription(token.pos, message(SYMBOL_EXPECTED, tokenKindExpected, token.kind)));
    }

    private void logError(Log.Type issue, Object... values) {
        error = true;
        log.report(issue, token.pos,
                errorDescription(token.pos, message(issue, values)));

    }

    ExpressionStatement parseLogicalOrExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == OR | TokenType == '||'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseLogicalAndExpression();
        while (token.kind == TokenKind.OROP || token.kind == TokenKind.OR) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseLogicalAndExpression();
//            exp1 = new BinaryExprNode(exp1, exp2, op);
            exp1 = BinaryExpression.of(OperatorType.OR)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;
        return exp1;

    }

    ExpressionStatement parseLogicalAndExpression() {
        /*
         * parseLogicalNotExpression() while(TokenType == AND | TokenType == '&&'){
         * parseLogicalNotExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseLogicalNotExpression();
        while (token.kind == TokenKind.ANDOP || token.kind == TokenKind.AND) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseLogicalNotExpression();
            exp1 = BinaryExpression.of(OperatorType.AND)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseLogicalNotExpression() {
        /*
         * parseNotEqualToExpression() while(TokenType == NOT | TokenType == '!'){
         * parseNotEqualToExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseNotEqualToExpression();
        while (token.kind == TokenKind.NOTOP || token.kind == TokenKind.NOT) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseNotEqualToExpression();
            exp1 = BinaryExpression.of(OperatorType.NOT)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseNotEqualToExpression() {
        /*
         * parseEqualEqualExpression() accept(NOT_EQ) while(TokenType == '!='){
         * parseEqualEqualExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseEqualEqualExpression();
        while (token.kind == TokenKind.NOTEQU) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseEqualEqualExpression();
            exp1 = BinaryExpression.of(OperatorType.NOTEQUALS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseEqualEqualExpression() {
        /*
         * parseRealtionalExpression() while(TokenType == '=='){
         * parseRealtionalExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseRelationalExpression();
        while (token.kind == TokenKind.EQUEQU) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseRelationalExpression();
            exp1 = BinaryExpression.of(OperatorType.EQUALS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseRelationalExpression() {
        /*
         * parseBitOrExpression() while(TokenType == <,>,<=,>=,in ,not in, is, is not ){
         * parseBitOrExpression() }
         */
        if (error) return null;
        Token tk = token;
        ExpressionStatement exp1 = parseBitOrExpression();
        if (exp1 == null) {
            error = true;
            return null;
        }
        while (token.kind == TokenKind.LT || token.kind == TokenKind.GT || token.kind == TokenKind.LTE
                || token.kind == TokenKind.GTE || token.kind == TokenKind.IN || token.kind == TokenKind.IS
                || token.kind == TokenKind.NOT) {
            if (error)
                return null;
            OperatorType op = token.kind == TokenKind.LT ? OperatorType.LESS : token.kind == TokenKind.GT ? OperatorType.MORE : token.kind == TokenKind.LTE ? OperatorType.LESSOREQUALS : token.kind == TokenKind.GTE ? OperatorType.MOREOREQUALS : token.kind == TokenKind.IN ? OperatorType.IN : token.kind == TokenKind.IS ? OperatorType.IS : token.kind == TokenKind.NOT ? OperatorType.NOT : null;
            if (token.kind == TokenKind.IS) {
                accept(token.kind);
                op = OperatorType.IS;
                if (token.kind == TokenKind.NOT) {
                    op = OperatorType.ISNOT;
                    accept(token.kind);
                }
            } else if (token.kind == TokenKind.NOT) {
                accept(token.kind);
                if (token.kind == TokenKind.IN) {
                    op = OperatorType.NOTIN;
                    accept(TokenKind.IN);
                }
            }
            ExpressionStatement exp2 = parseBitOrExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;

        return exp1;
    }

    ExpressionStatement parseBitOrExpression() {
        /*
         * parseBitXorExpression() while(TokenType == '|'){ parseBitXorExpression() }
         */

        if (error) return null;
        ExpressionStatement exp1 = parseBitXorExpression();
        while (token.kind == TokenKind.BITOR) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseBitXorExpression();
            exp1 = BinaryExpression.of(OperatorType.BITOR)
                    .right(exp2)
                    .left(exp1);

        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseBitXorExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == '^'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseBitAndExpression();
        while (token.kind == TokenKind.BITAND) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseBitAndExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseBitAndExpression() {
        /*
         * parseBitRightShiftExpression() while(TokenType == '&'){
         * parseBitRightShiftExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseBitRightShiftExpression();
        while (token.kind == TokenKind.ANDOP) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseBitRightShiftExpression();
            exp1 = BinaryExpression.of(OperatorType.AND)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseBitRightShiftExpression() {
        /*
         * parseBitLeftShiftExpression() while(TokenType == '>>' | TokenType == '>>>'){
         * parseBitLeftShiftExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseBitLeftShiftExpression();
        while (token.kind == TokenKind.RSHIFT || token.kind == TokenKind.TRSHIFT) {
            if (error)
                return null;
            OperatorType op = token.kind == TokenKind.RSHIFT ? OperatorType.BITRIGHTSHIFT_SIGNED : OperatorType.BITRIGHTSHIFT_UNSIGNED;
            accept(token.kind);
            ExpressionStatement exp2 = parseBitLeftShiftExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseBitLeftShiftExpression() {
        /*
         * parseSubtractExpression() while(TokenType == '<<' | TokenType == '<<<'){
         * parseSubtractExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseSubtractExpression();
        while (token.kind == TokenKind.LSHIFT) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseSubtractExpression();
            exp1 = BinaryExpression.of(OperatorType.BITLEFTSHIFT)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseSubtractExpression() {
        /*
         * parseAdditionExpression() while(TokenType == '-'){ parseAdditionExpression()
         * }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseAdditionExpression();
        while (token.kind == TokenKind.SUB) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseAdditionExpression();
            exp1 = BinaryExpression.of(OperatorType.MINUS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseAdditionExpression() {
        /*
         * parseMultiplicationExpression() while(TokenType == '+'){
         * parseMultiplicationExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseMultiplicationExpression();
        while (token.kind == TokenKind.ADD) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseMultiplicationExpression();
            exp1 = BinaryExpression.of(OperatorType.PLUS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseMultiplicationExpression() {
        /*
         * parseDivisionExpression() while(TokenType == '*'){ parseDivisionExpression()
         * }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseDivisionExpression();
        while (token.kind == TokenKind.MUL) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement exp2 = parseDivisionExpression();
            exp1 = BinaryExpression.of(OperatorType.TIMES)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseDivisionExpression() {
        /*
         * parseFactorExpression() while(TokenType == /, %, // ){
         * parseFactorExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = parseFactorExpression();
        while (token.kind == TokenKind.DIV || token.kind == TokenKind.MOD || token.kind == TokenKind.FLOORDIV) {
            if (error)
                return null;
            OperatorType op = TokenKind.DIV == token.kind ? OperatorType.DIVIDE : TokenKind.MOD == token.kind ? OperatorType.MODULO : OperatorType.FLOOR;
            accept(token.kind);
            ExpressionStatement exp2 = parseFactorExpression();
//            exp1 = new BinaryExprNode(exp1, exp2, op);
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseFactorExpression() {
        /*
         *
         * if( TokenType == -, ~ ) parseFactorExpression() parsePowerExpression()
         *
         */
        if (error) return null;
        ExpressionStatement exp1 = null;
        if (token.kind == TokenKind.SUB || token.kind == TokenKind.TILDE
                || token.kind == TokenKind.NOT
                || token.kind == TokenKind.NOTOP) {
            if (prevToken().kind == TokenKind.IDENTIFIER) {
                // Handle Case where previous token is identifier and sould not generate
                // UnaryOperator
                exp1 = parsePowerExpression();
            } else {
                OperatorType op = token.kind == TokenKind.SUB ? OperatorType.MINUS : TokenKind.TILDE == token.kind ? OperatorType.TILDE : TokenKind.NOT == token.kind ?  OperatorType.NOT : OperatorType.NOTOP;
                accept(token.kind);
                exp1 = parsePowerExpression();
                exp1 = UnaryExpression.create(exp1, op);
            }
        } else {
            exp1 = parsePowerExpression();
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parsePowerExpression() {
        /*
         * parseAtomExpression() while(TokenType == '**'){ parseAtomExpression() }
         */
        if (error) return null;
        ExpressionStatement exp1 = null, exp2;
        try {
            exp1 = parseAtomExpression();
            while (token.kind == TokenKind.POWER) {
                if (error)
                    return null;
                String op = token.value();
                accept(TokenKind.POWER);
                exp2 = parseFactorExpression();
                exp1 = BinaryExpression.of(OperatorType.POW)
                        .right(exp2)
                        .left(exp1);
//                exp1 = new BinaryExprNode(exp1, exp2, op);
            }
            return exp1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (error) return null;
        return exp1;

    }

    ExpressionStatement parseTrailer(ExpressionStatement oExp) throws ParseException {
        ExpressionStatement node = null;
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
                        // if (token.kind == TokenKind.IDENTIFIER)
                        //     exp1 = parseIdentifier();
                        // else
                        //     exp1 = parseNumberLiteral();
                        exp1 = parseAtomExpression();
                        if (token.kind == TokenKind.COLON) {
                            accept(TokenKind.COLON);
                            exp2 = parseAtomExpression();
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
                    ExpressionStatement e1;
                    debug.add("Atom DOT:" + oExp);
                    accept(TokenKind.DOT);
                    Context context = Context.context;
                    context.enterProperty();
                    e1 = parseIdentifier();
                    if (error)
                        return null;
                    ExpressionStatement trail = e1;
                    while (token.kind != TokenKind.DOT && (token.kind == TokenKind.LSQU || token.kind == TokenKind.LPAREN)) {
                        trail = parseTrailer(trail);
                    }
                    context.leaveProperty();
                    if (trail == null)
                        node = ObjectAccessStatement.create(oExp, e1);
                    else
                        node= ObjectAccessStatement.create(oExp, trail);
//                        node = new ObjectAccessNode(oExp, trail);
                    debug.add("Token Kind: " + token.kind);
                    break;
            }
        }
        return node;
    }

    ExpressionStatement parseAtomExpression() throws ParseException {
        /*
         * List of Trailers parseAtom()
         *
         * trail = parseTrailer() while (trail) trail = parseTrailer()
         */

        if (error) return null;
        debug.add("Atom Expr Node Token: " + token.kind);
        ExpressionStatement oExp = parseAtom();
        if (oExp == null) {
            error = true;
            return null;
        }
        debug.add("Atom Expr Node Token: " + token.kind);
        if (oExp instanceof PropertyAccess || oExp instanceof ThisStatement || oExp instanceof ReferenceLookup) {
            ExpressionStatement trailer;
            while ((trailer = parseTrailer(oExp)) != null) {
                oExp = trailer;
            }
        }

        if (error) return null;
        if (oExp == null) {
            error = true;
            return null;
        }
        return oExp;
    }

    CafeStatement parseExprStmt() {
        if (error) return null;
        ExpressionStatement exp1 = parseLogicalAndExpression();
        ExpressionStatement exp2;
        debug.add("Expr Stmt : " + token.kind);
        if (token.kind == TokenKind.OR || token.kind == TokenKind.OROP) {
            String op = token.value();
            accept(token.kind);
            exp2 = parseLogicalAndExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        } else if (token.kind == TokenKind.EQU) {
            accept(token.kind);
            exp2 = parseValue();

            return AssignmentStatement.create(exp1, exp2);
//            return new AsgnStmtNode(exp1, exp2);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement parseAtom() {
        /*
         * switch TokenType case LPAREN: parseExpressionStatement() accept(RPAREN) case
         * IDENTIFIER: parseIdentifier() case STRINGLITERAL: parseStringLiteral() case
         * NUMLiteral: parseNumberLiteral() case BOOLLiteral: parseBoolLiteral() case
         * NULL: parseNull() case THIS: parseThis()
         */
        if (error) return null;
        debug.add("Atom Node Token: " + token.kind);
        CafeStatement exp1 = null;
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
        if (exp1 == null) {
            error = true;
            return null;
        }
        return (ExpressionStatement) exp1;

    }

    ExpressionStatement parseNull() {
        if (error) return null;
//        Token tk = token;
//        accept(TokenKind.NULL);
//        NullNode nullNode = new NullNode();
//        nullNode.setFirstToken(tk);
        return new NullStatement();
    }

    ExpressionStatement parseThis() {
        if (error) return null;
//        Token tk = token;
//        accept(TokenKind.THIS);
//        ThisNode thisNode = new ThisNode();
//        thisNode.setFirstToken(tk);
        ParseIR.Context context = ParseIR.Context.context;
        boolean isGlobal = false;
        if (context.isModuleScope) {
            isGlobal = true;
        }
        return ThisStatement.create(isGlobal);
    }

    ExpressionStatement parseIdentifier() {
        /*
         * Create Identifier Node. return IdentNode()
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.IDENTIFIER);
        IdenNode iden = new IdenNode(prev.value());
        iden.setFirstToken(prev);
        ParseIR.Context context = ParseIR.Context.context;
        if (context.isProperty()) {
            PropertyAccess prop = PropertyAccess.of(prev.value());
            prop.setFirstToken(prev);
            return prop;
        }else {
            ReferenceLookup refs = ReferenceLookup.of(prev.value());
            refs.setFirstToken(prev);
            return refs;
        }
    }

    ExpressionStatement parseStringLiteral() {
        /*
         * check Quotes accept(STRING LITERAL)
         *
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.STRLIT);
        return new ConstantStatement(prev.value());

    }

    ExpressionStatement parseNumberLiteral() throws ParseException {
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
                return new ConstantStatement(num);
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
            return new ConstantStatement(num2);
        }

        return null;
    }

    ExpressionStatement parseBoolLiteral() {
        /*
         *
         */
        if (error) return null;
        Token tk = token;
        if (token.kind == TokenKind.TRUE) {
            accept(TokenKind.TRUE);
            if (error) return null;

            ConstantStatement boolLit = new ConstantStatement(true);
//            boolLit.setFirstToken(tk);
            return boolLit;
        } else if (token.kind == TokenKind.FALSE) {
            accept(TokenKind.FALSE);
            if (error) return null;

            ConstantStatement boolLit = new ConstantStatement(false);
//            boolLit.setFirstToken(tk);
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
        ExprNode ifCond = null;
        BlockNode ifBlock = new BlockNode();

        Token firstToken = token;

        nextToken();
        accept(TokenKind.LPAREN);
        ifCond = parseLogicalOrExpression();
        if (ifCond == null) {
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
                    while(token.kind != TokenKind.RCURLY)
                        stmt.addAll(parseBlock());
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
         *
         * Not used
         */
        if (error) return null;

        ExpressionStatement exp1 = parseIdentifier();
        accept(TokenKind.DOT);
        ExpressionStatement exp2 = parseIdentifier();
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
        if (init.isEmpty()) return null;
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

    List<AssignmentStatement> parseForIncrement() {
        /*
         *
         * parseAssignmentStatement()
         */
        if (error) return null;
        ExpressionStatement iden, val, exp2;
        List<AssignmentStatement> incrNodes = null;
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
            Context ctx= Context.context;
            ctx.enterProperty();
            while (token.kind == TokenKind.DOT) {
                if (error)
                    return null;
                accept(TokenKind.DOT);
                exp2 = parseIdentifier();
                iden = ObjectAccessStatement.create(iden, exp2);
//                iden = new ObjectAccessNode(iden, exp2);
            }
            ctx.leaveProperty();
            accept(TokenKind.EQU);
            val = parseValue();
//            incrNodes.add(new AsgnStmtNode(iden, val));
            incrNodes.add(AssignmentStatement.create(iden, val));
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
        iden1 = parseIdentifier();
        if (token.kind == TokenKind.COMMA) {
            nextToken();
            iden2 = parseIdentifier();
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
        Token tk = token;
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
                    iden1 = parseIdentifier();
                    if (token.kind == TokenKind.COMMA) {
                        nextToken();
                        iden2 = parseIdentifier();
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
    ExpressionStatement parseObjectCreation() {
        /*
         * List of ObjectNode
         *
         * List.add(parseObject())
         *
         * if Comma { List.add(parseObject()) }
         *
         */

        if (error) return null;
        Map<String, ExprNode> object = new LinkedHashMap<>();
        ExpressionStatement idenNode;
        ExpressionStatement exprNode;

        accept(TokenKind.LCURLY);
        while (token.kind != TokenKind.RCURLY) {
            if (error)
                return null;
            idenNode = parseIdentifier();
            accept(TokenKind.COLON);
            exprNode = parseValue();
            object.put(idenNode.getName(), exprNode);
            debug.add("Obj Creation Token : " + token.kind);
            if (TokenKind.RCURLY != token.kind)
                accept(TokenKind.COMMA);
        }
        debug.add("Object Creation: " + object);
        accept(TokenKind.RCURLY);
        if (error) return null;
        return ObjectCreationStatement.of(object);
//        return new ObjCreationNode(object);

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

    ExpressionStatement parseValue() {
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
        ExpressionStatement valExpr = null;
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

    List<DeclarativeAssignmentStatement> parseVariable() {
        /*
         * List Variables checks grammar for variable Declaration
         *
         *
         * calls parseVariableDeclaration
         *
         * return List
         */
        if (error) return null;
        List<DeclarativeAssignmentStatement> varDeclNodes = new ArrayList<>();
        accept(TokenKind.VAR);
        while (token.kind != TokenKind.SEMICOLON) {
            if (error)
                return null;
            Token tk = token;
            IdenNode idenNode = parseIdentifier();
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
            IdenNode idenNode = parseIdentifier();
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
                idenNodes.add(parseIdentifier());
                // accept(TokenKind.RPAREN);
                break;
            }
            idenNodes.add(parseIdentifier());
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
            case THIS:
            case NULL:
                StmtNode stm6 = parseExprStmt();
                if (stm6 == null) return null;
                blockStmt.add(stm6);
                debug.add("Block Stmt: " + token.kind);
                accept(TokenKind.SEMICOLON);
                break;
            case BREAK:
            case CONTINUE:
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

    List<ExportStmtNode> parseExportStatement() {
        List<ExportStmtNode> exportStmtNode = new ArrayList<ExportStmtNode>();

        accept(TokenKind.EXPORT);
        switch (token.kind) {
            case IDENTIFIER:
                IdenNode id = parseIdentifier();
                if (id == null) return null;
                exportStmtNode.add(new ExportStmtNode(id, null));
                while (token.kind == TokenKind.COMMA) {
                    accept(TokenKind.COMMA);
                    id = parseIdentifier();
                    if (id == null) return null;
                    exportStmtNode.add(new ExportStmtNode(id, null));
                }
                accept(TokenKind.SEMICOLON);
                break;

            case VAR:
                List<VarDeclNode> stm = parseVariable();
                if (stm == null) return null;
                for (VarDeclNode var : stm) {
                    exportStmtNode.add(new ExportStmtNode(var.getIden(), var));
                }
                break;
            case CONST:
                List<ConstDeclNode> stm1 = parseConstVariable();
                if (stm1 == null) return null;
                for (ConstDeclNode var : stm1) {
                    exportStmtNode.add(new ExportStmtNode(var.getIden(), var));
                }
                break;
            case FUNC:
                DeclNode decl = parseFunctionDeclaration();
                if (decl == null) return null;
                exportStmtNode.add(new ExportStmtNode(decl.getIden(), decl));
                break;
            default:
                error = true;
        }
        if (error) return null;
        return exportStmtNode;
    }

    // return Import Statement Node
    ImportStmtNode parseImportStatement() {
        /* List of Imports */

        // accept('@');
        // boolean valid = checkFilePathRegex(token.value());
        // if(valid) return ImportStatement(token.value())
        // else Throw Error
        ImportStmtNode importStmtNode = null;
        Map<IdenNode, IdenNode> blocks = new HashMap<IdenNode, IdenNode>();
        IdenNode id1, id2 = null;

        accept(TokenKind.IMPORT);
        if(token.kind == TokenKind.IDENTIFIER){
            id1 = parseIdentifier();
            if (token.kind == TokenKind.AS) {
                accept(token.kind);
                id2 = parseIdentifier();
            }
            blocks.put(id1, id2);
            while (token.kind == TokenKind.COMMA) {
                accept(TokenKind.COMMA);
                id1 = parseIdentifier();
                id2 = null;
                if (token.kind == TokenKind.AS) {
                    accept(token.kind);
                    id2 = parseIdentifier();
                }
                blocks.put(id1, id2);
            }
        } else {
            if(accept(TokenKind.MUL)) {
                id1 = new IdenNode("*");
                accept(TokenKind.AS);
                id2 = parseIdentifier();
                blocks.put(id1, id2);
            }
        }

        accept(TokenKind.FROM);
//        File file = new File(token.value()+".class");
//        if(!file.exists()){
//
//            logError(INVALID_IMPORT_FILE, token.value());
//            error = true;
//        } else {
        importStmtNode = new ImportStmtNode(blocks, token.value());
        nextToken();
        accept(TokenKind.SEMICOLON);

        return importStmtNode;
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
                    ImportStmtNode importStmtNode = parseImportStatement();
                    if (importStmtNode == null) return null;
                    tree.add(importStmtNode);
                    break;
                case EXPORT:
                    List<ExportStmtNode> exports = parseExportStatement();
                    if(exports == null) return null;
                    tree.addAll(exports);
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
