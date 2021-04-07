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
import compiler.ast.Node.ContinueStmtNode;
import compiler.ast.Node.IdenNode;
import compiler.ast.Node.Tag;
import compiler.gen.AnnFuncNameGenerator;
import compiler.ir.*;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
import compiler.util.Position;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static compiler.util.Log.Type.SOURCE_POSITION;
import static compiler.util.Log.Type.SYMBOL_EXPECTED;
import static compiler.util.Messages.message;

public class IRParser extends Parser {
    static {
        ParserFactory.registerParser(ParserType.IRParser, new IRParser());
    }

    @Override
    public CafeModule parse(String moduleName) {
        IRParser.Context.context.createModule(moduleName);
//        IRParser.Context.context.newObjectStack();
        return parseStatements();
    }

    private static final class Context {
        final static IRParser.Context context = new IRParser.Context();

        public CafeModule module;
        private final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
        private final Deque<String> functionStack = new LinkedList<>();
//        private final Deque<Deque<Object>> objectStack = new LinkedList<>();
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
            assert referenceTableStack.peek() != null;
            ReferenceTable blockReferenceTable = referenceTableStack.peek()
                    .fork();
            referenceTableStack.push(blockReferenceTable);
            return Block.create(blockReferenceTable);
        }

        public void leaveScope() {
            referenceTableStack.pop();
        }

        public void enterFunc(String n) {
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

        public IRParser.Context.Scope currentScope() {
            if (functionStack.size() == 0)
                return IRParser.Context.Scope.GLOBAL;
            if (functionStack.size() == 1)
                return IRParser.Context.Scope.LOCAL;
            return IRParser.Context.Scope.CLOSURE;
        }

/*
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
            assert objectStack.peek() != null;
            objectStack.peek()
                    .push(object);
        }

        public Object pop() {
            assert objectStack.peek() != null;
            return objectStack.peek()
                    .pop();
        }

        public Object peek() {
            return objectStack.peek();
        }
*/

        SymbolReference createSymbolReference(String name, SymbolReference.Kind kind, SymbolReference.Scope scope) {
            SymbolReference ref = SymbolReference.of(name, kind, scope);
            assert referenceTableStack.peek() != null;
            referenceTableStack.peek()
                    .add(ref);
            return ref;
        }

        public SymbolReference createSymbolReference(String name, Node.Tag tag) {
            return createSymbolReference(name, getSymbolKind(tag), getSymbolScope());
        }

/*
        public SymbolReference getReference(String name) {
            assert referenceTableStack.peek() != null;
            return referenceTableStack.peek()
                    .get(name);
        }
*/

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
            IRParser.Context.Scope scope = currentScope();
            if (scope == IRParser.Context.Scope.GLOBAL)
                return SymbolReference.Scope.GLOBAL;
            if (scope == IRParser.Context.Scope.LOCAL || scope == IRParser.Context.Scope.CLOSURE)
                return SymbolReference.Scope.LOCAL;
            throw new AssertionError("Invalid Symbol Scope");
        }

        public void addFunction(CafeFunction function) {
            context.module.addFunction(function);
        }
    }




    private Lexer lexer;
    private Token token;
    private Log log;
    private boolean breakAllowed = false;
    private boolean error = false;
    private  List<String> debug = new ArrayList<>();

    private IRParser() {
    }

    private IRParser(ParserFactory factory, Lexer lexer) {
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
    protected IRParser instance(ParserFactory factory, Lexer lexer) {
        return new IRParser(factory, lexer);
    }

//    Token token() {
//        return token;
//    }

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

    private void logError(Object... values) {
        error = true;
        log.report(Log.Type.INVALID_EXPRESSION, token.pos,
                errorDescription(token.pos, message(Log.Type.INVALID_EXPRESSION, values)));

    }

    ExpressionStatement<?> parseLogicalOrExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == OR | TokenType == '||'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseLogicalAndExpression();
        while (token.kind == TokenKind.OROP || token.kind == TokenKind.OR) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseLogicalAndExpression();
//            exp1 = new BinaryExprNode(exp1, exp2, op);
            exp1 = BinaryExpression.of(OperatorType.OR)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;
        return exp1;

    }

    ExpressionStatement<?> parseLogicalAndExpression() {
        /*
         * parseLogicalNotExpression() while(TokenType == AND | TokenType == '&&'){
         * parseLogicalNotExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseLogicalNotExpression();
        while (token.kind == TokenKind.ANDOP || token.kind == TokenKind.AND) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseLogicalNotExpression();
            exp1 = BinaryExpression.of(OperatorType.AND)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseLogicalNotExpression() {
        /*
         * parseNotEqualToExpression() while(TokenType == NOT | TokenType == '!'){
         * parseNotEqualToExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseNotEqualToExpression();
        while (token.kind == TokenKind.NOTOP || token.kind == TokenKind.NOT) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseNotEqualToExpression();
            exp1 = BinaryExpression.of(OperatorType.NOT)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseNotEqualToExpression() {
        /*
         * parseEqualEqualExpression() accept(NOT_EQ) while(TokenType == '!='){
         * parseEqualEqualExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseEqualEqualExpression();
        while (token.kind == TokenKind.NOTEQU) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseEqualEqualExpression();
            exp1 = BinaryExpression.of(OperatorType.NOTEQUALS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseEqualEqualExpression() {
        /*
         * parseRealtionalExpression() while(TokenType == '=='){
         * parseRealtionalExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseRelationalExpression();
        while (token.kind == TokenKind.EQUEQU) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseRelationalExpression();
            exp1 = BinaryExpression.of(OperatorType.EQUALS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseRelationalExpression() {
        /*
         * parseBitOrExpression() while(TokenType == <,>,<=,>=,in ,not in, is, is not ){
         * parseBitOrExpression() }
         */
        if (error) return null;
//        Token tk = token;
        ExpressionStatement<?> exp1 = parseBitOrExpression();
        if (exp1 == null) {
            error = true;
            return null;
        }
        while (token.kind == TokenKind.LT || token.kind == TokenKind.GT || token.kind == TokenKind.LTE
                || token.kind == TokenKind.GTE || token.kind == TokenKind.IN || token.kind == TokenKind.IS
                || token.kind == TokenKind.NOT) {
            if (error)
                return null;
            OperatorType op = token.kind == TokenKind.LT ? OperatorType.LESS : token.kind == TokenKind.GT ? OperatorType.MORE : token.kind == TokenKind.LTE ? OperatorType.LESSOREQUALS : token.kind == TokenKind.GTE ? OperatorType.MOREOREQUALS : token.kind == TokenKind.IN ? OperatorType.IN : token.kind == TokenKind.IS ? OperatorType.IS : OperatorType.NOT;
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
            }else{
                accept(token.kind);
            }
            System.out.println("Relational Operator type: "+op);

            ExpressionStatement<?> exp2 = parseBitOrExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;

        return exp1;
    }

    ExpressionStatement<?> parseBitOrExpression() {
        /*
         * parseBitXorExpression() while(TokenType == '|'){ parseBitXorExpression() }
         */

        if (error) return null;
        ExpressionStatement<?> exp1 = parseBitXorExpression();
        while (token.kind == TokenKind.BITOR) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseBitXorExpression();
            exp1 = BinaryExpression.of(OperatorType.BITOR)
                    .right(exp2)
                    .left(exp1);

        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseBitXorExpression() {
        /*
         * parseLogicalAndExpression() while(TokenType == '^'){
         * parseLogicalAndExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseBitAndExpression();
        while (token.kind == TokenKind.BITAND) {
            if (error)
                return null;
            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseBitAndExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseBitAndExpression() {
        /*
         * parseBitRightShiftExpression() while(TokenType == '&'){
         * parseBitRightShiftExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseBitRightShiftExpression();
        while (token.kind == TokenKind.ANDOP) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseBitRightShiftExpression();
            exp1 = BinaryExpression.of(OperatorType.AND)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseBitRightShiftExpression() {
        /*
         * parseBitLeftShiftExpression() while(TokenType == '>>' | TokenType == '>>>'){
         * parseBitLeftShiftExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseBitLeftShiftExpression();
        while (token.kind == TokenKind.RSHIFT || token.kind == TokenKind.TRSHIFT) {
            if (error)
                return null;
            OperatorType op = token.kind == TokenKind.RSHIFT ? OperatorType.BITRIGHTSHIFT_SIGNED : OperatorType.BITRIGHTSHIFT_UNSIGNED;
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseBitLeftShiftExpression();
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseBitLeftShiftExpression() {
        /*
         * parseSubtractExpression() while(TokenType == '<<' | TokenType == '<<<'){
         * parseSubtractExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseSubtractExpression();
        while (token.kind == TokenKind.LSHIFT) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseSubtractExpression();
            exp1 = BinaryExpression.of(OperatorType.BITLEFTSHIFT)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseSubtractExpression() {
        /*
         * parseAdditionExpression() while(TokenType == '-'){ parseAdditionExpression()
         * }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseAdditionExpression();
        while (token.kind == TokenKind.SUB) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseAdditionExpression();
            exp1 = BinaryExpression.of(OperatorType.MINUS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseAdditionExpression() {
        /*
         * parseMultiplicationExpression() while(TokenType == '+'){
         * parseMultiplicationExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseMultiplicationExpression();
        while (token.kind == TokenKind.ADD) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseMultiplicationExpression();
            exp1 = BinaryExpression.of(OperatorType.PLUS)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseMultiplicationExpression() {
        /*
         * parseDivisionExpression() while(TokenType == '*'){ parseDivisionExpression()
         * }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseDivisionExpression();
        while (token.kind == TokenKind.MUL) {
            if (error)
                return null;
//            String op = token.value();
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseDivisionExpression();
            exp1 = BinaryExpression.of(OperatorType.TIMES)
                    .right(exp2)
                    .left(exp1);
//            exp1 = new BinaryExprNode(exp1, exp2, op);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseDivisionExpression() {
        /*
         * parseFactorExpression() while(TokenType == /, %, // ){
         * parseFactorExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1 = parseFactorExpression();
        while (token.kind == TokenKind.DIV || token.kind == TokenKind.MOD || token.kind == TokenKind.FLOORDIV) {
            if (error)
                return null;
            OperatorType op = TokenKind.DIV == token.kind ? OperatorType.DIVIDE : TokenKind.MOD == token.kind ? OperatorType.MODULO : OperatorType.FLOOR;
            accept(token.kind);
            ExpressionStatement<?> exp2 = parseFactorExpression();
//            exp1 = new BinaryExprNode(exp1, exp2, op);
            exp1 = BinaryExpression.of(op)
                    .right(exp2)
                    .left(exp1);
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parseFactorExpression() {
        /*
         *
         * if( TokenType == -, ~ ) parseFactorExpression() parsePowerExpression()
         *
         */
        if (error) return null;
        ExpressionStatement<?> exp1;
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
                exp1 = UnaryExpression.create(op, exp1);
            }
        } else {
            exp1 = parsePowerExpression();
        }
        if (error) return null;
        return exp1;
    }

    ExpressionStatement<?> parsePowerExpression() {
        /*
         * parseAtomExpression() while(TokenType == '**'){ parseAtomExpression() }
         */
        if (error) return null;
        ExpressionStatement<?> exp1, exp2;
        try {
            exp1 = parseAtomExpression();
            while (token.kind == TokenKind.POWER) {
                if (error)
                    return null;
//                String op = token.value();
                accept(TokenKind.POWER);
                exp2 = parseFactorExpression();
                exp1 = BinaryExpression.of(OperatorType.POW)
                        .right(exp2)
                        .left(exp1);
//                exp1 = new BinaryExprNode(exp1, exp2, op);
            }
            if (error) return null;
            return exp1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    ExpressionStatement<?> parseTrailer(ExpressionStatement<?> oExp) throws ParseException {
        ExpressionStatement<?> node = null;
        Context context = Context.context;
        boolean isProperty = context.isProperty();
        if (token.kind == TokenKind.LPAREN || token.kind == TokenKind.DOT || token.kind == TokenKind.LSQU) {
            switch (token.kind) {
                case LPAREN:
                    accept(TokenKind.LPAREN);

//                    // eg: a.b()
//                    // b is a property of a, thus method invocation node is created.
                    if (context.isProperty()) {
//                        n.invokedOn.accept(this);
                        context.leaveProperty();
                        node = MethodInvocation.create(oExp, parseArgList());
                    }
                    else {
                        // eg: a()
                        // a() is normal function call, thus function invocation node is created.
                        context.leaveProperty();
                        node = FunctionInvocation.create(oExp, parseArgList());
                    }
//                    node = new FuncCallNode(oExp, new ArgsListNode(parseArgList()));
                    if (isProperty) context.enterProperty();
                    accept(TokenKind.RPAREN);
                    break;

                case LSQU:
                    ExpressionStatement<?> exp1, exp2;
                    debug.add("Atom Expr: " + token.kind);
                    context.leaveProperty();
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
                            node = SliceExpression.slice(oExp)
                                    .beginsAt(exp1)
                                    .endsAt(exp2);
//                            node = new SliceNode(oExp, exp1, exp2);
                        } else {
                            accept(TokenKind.RSQU);
                            node = SubscriptExpression.create(oExp, exp1);
//                            node = new SubscriptNode(oExp, exp1);
                            oExp = node;
                        }

                    }
                    if (isProperty) context.enterProperty();
                    break;
                case DOT:
                    ExpressionStatement<?> e1;
                    debug.add("Atom DOT:" + oExp);
                    accept(TokenKind.DOT);
                    context.enterProperty();
                    e1 = parseIdentifier();
                    if (error)
                        return null;
                    ExpressionStatement<?> trail = e1;
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

    ExpressionStatement<?> parseAtomExpression() throws ParseException {
        /*
         * List of Trailers parseAtom()
         *
         * trail = parseTrailer() while (trail) trail = parseTrailer()
         */

        if (error) return null;
        debug.add("Atom Expr Node Token: " + token.kind);
        ExpressionStatement<?> oExp = parseAtom();
        if (oExp == null) {
            error = true;
            return null;
        }
        debug.add("Atom Expr Node Token: " + token.kind);
        if (oExp instanceof PropertyAccess || oExp instanceof ThisStatement || oExp instanceof ReferenceLookup) {
            ExpressionStatement<?> trailer;
            while ((trailer = parseTrailer(oExp)) != null) {
                oExp = trailer;
            }
        }

        if (error) return null;
        return oExp;
    }

    CafeStatement<?> parseExprStmt() {
        if (error) return null;
        ExpressionStatement<?> exp1 = parseLogicalAndExpression();
        ExpressionStatement<?> exp2;
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

    ExpressionStatement<?> parseAtom() {
        /*
         * switch TokenType case LPAREN: parseExpressionStatement<?() accept(RPAREN) case
         * IDENTIFIER: parseIdentifier() case STRINGLITERAL: parseStringLiteral() case
         * NUMLiteral: parseNumberLiteral() case BOOLLiteral: parseBoolLiteral() case
         * NULL: parseNull() case THIS: parseThis()
         */
        if (error) return null;
        debug.add("Atom Node Token: " + token.kind);
        CafeStatement<?> exp1 = null;
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
                logError();
        }
        if (error) return null;
        if (exp1 == null) {
            error = true;
            return null;
        }
        return (ExpressionStatement<?>) exp1;

    }

    ExpressionStatement<?> parseNull() {
        if (error) return null;
//        Token tk = token;
//        accept(TokenKind.NULL);
//        NullNode nullNode = new NullNode();
//        nullNode.setFirstToken(tk);
        return new NullStatement();
    }

    ExpressionStatement<?> parseThis() {
        if (error) return null;
//        Token tk = token;
        accept(TokenKind.THIS);
//        ThisNode thisNode = new ThisNode();
//        thisNode.setFirstToken(tk);
        IRParser.Context context = IRParser.Context.context;
        boolean isGlobal = false;
        if (context.isModuleScope) {
            isGlobal = true;
        }
        return ThisStatement.create(isGlobal);
    }

    ExpressionStatement<?> parseIdentifier() {
        /*
         * Create Identifier Node. return IdentNode()
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.IDENTIFIER);
        IdenNode iden = new IdenNode(prev.value());
        iden.setFirstToken(prev);
        IRParser.Context context = IRParser.Context.context;
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

    ExpressionStatement<?> parseStringLiteral() {
        /*
         * check Quotes accept(STRING LITERAL)
         *
         */
        if (error) return null;
        Token prev = token;
        accept(TokenKind.STRLIT);
        return new ConstantStatement(prev.value());

    }

    ExpressionStatement<?> parseNumberLiteral() throws ParseException {
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

    ExpressionStatement<?> parseBoolLiteral() {
        /*
         *
         */
        if (error) return null;
//        Token tk = token;
        if (token.kind == TokenKind.TRUE) {
            accept(TokenKind.TRUE);
            if (error) return null;

            //            boolLit.setFirstToken(tk);
            return new ConstantStatement(true);
        } else if (token.kind == TokenKind.FALSE) {
            accept(TokenKind.FALSE);
            if (error) return null;

            //            boolLit.setFirstToken(tk);
            return new ConstantStatement(false);
        }
        return null;
    }

//    void parseSubscriptList() {
//        /*
//         * while(LBRACKET) parseSubscript()
//         *
//         */
//    }

//    void parseSubscript() {
//        /*
//         * accept(LBRACKET) parseNumberLiteral() if ( COLON) parseNumberLiteral()
//         * accept(LBRACKET)
//         *
//         *
//         */
//    }

    List<ExpressionStatement<?>> parseArgList() {
        /*
         * parseArg() while(COMMA) parseArg()
         */
        if (error) return null;
        List<ExpressionStatement<?>> args = new ArrayList<>();
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

//    void parseArg() {
//        /*
//         * parseValue()
//         */
//    }

    //void parseTrailer() {
    /*
     * if (LPAREN) parseArgList() else if (DOT) parseIdentifier() else if (LBRACKET)
     * parseSubscriptList() else return false
     *
     */
    //}

//    void parseExpressionStatement() {
//        /*
//         * parseLogAndExpression() if (|| | 'or') parseLogAndExpression() else if (EQUAL
//         * OPERATOR) parseEqualOperator() parseValue() else handle Error
//         *
//         */
//    }

    /* parseStatements */
    ConditionalBranching parseIf() {
        if (error) return null;
        ExpressionStatement<?> ifCond;
        Block ifBlock;

//        Token firstToken = token;

        nextToken();
        accept(TokenKind.LPAREN);
        ifCond = parseLogicalOrExpression();
        if (ifCond == null) {
            //    log.report(Type.ERROR, token.pos, errorDescription(token.pos,  "If without condition!"));
            logError();
            error = true;
            return null;
        }
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        if (token.kind != TokenKind.RCURLY)
            ifBlock = parseLoopBlock();
        else
            ifBlock = parseBlock();

        accept(TokenKind.RCURLY);

        if (error) return null;

        //        IfStmtNode ifNode = new IfStmtNode(ifCond, ifBlock);
//        ifNode.setFirstToken(firstToken);
        return ConditionalBranching
                .branch()
                .condition(ifCond)
                .whenTrue(ifBlock);
    }

    CafeStatement<ConditionalBranching> parseIfStatement() {
        /*
         * Parse If Statement and check if there any 'else' is there, if 'yes' then
         * parseIt and Break otherwise parse Else if statement and append to a list
         */

        if (error) return null;

        ConditionalBranching ifNode;
        if ((ifNode = parseIf()) != null) {
            CafeStatement<ConditionalBranching> elseBlock = null;
            if (token.kind == TokenKind.ELSE) {
//                Token elseFT = token;
                nextToken();
                if (token.kind == TokenKind.IF) {
                    elseBlock = parseIfStatement();
                } else if (accept(TokenKind.LCURLY)) {
                    Block blockNode = parseBlock();
                    accept(TokenKind.RCURLY);
//                    elseBlock.setFirstToken(elseFT);
                }
                if (error) return null;
                ifNode.otherwise(elseBlock);
            }
            return ifNode;
        }
        return null;
    }

//    void parseElseStatement() {
//        /*
//         * accept(ELSE) accept(LCURLY) parseBlockStatements() accept(RCURLY)
//         */
//    }

//    void parseElseIfStatement() {
//        /*
//         * List of ElseIfNodes
//         *
//         * while ( !ELSEIF ){ accept(ELSEIF) accept(LPAREN) parseLogORExpression()
//         * accept(RPAREN) accept(LCURLY) parseBlockStatements() accept(RCURLY)
//         * ElseIf.add(ElseIFNode(condition, block)
//         *
//         * return ElseIfNode
//         */
//    }

//    StmtNode parseAssignmentStatement() {
        /*
         * parseIdentifier() while (DOT) parseIdentifier() parseEqualOperator()
         * parseValue() accept(SEMI)
         *
         * Not used
         */
//        if (error) return null;
//
//        ExpressionStatement<? exp1 = parseIdentifier();
//        accept(TokenKind.DOT);
//        ExpressionStatement<? exp2 = parseIdentifier();
//        exp1 = new ObjectAccessNode(exp1, exp2);
//        debug.add("Obj Access Parse Assign: " + exp1);
//        while (token.kind == TokenKind.DOT) {
//            if (error)
//                return null;
//            accept(TokenKind.DOT);
//            exp2 = parseIdentifier();
//            exp1 = new ObjectAccessNode(exp1, exp2);
//        }
//        accept(TokenKind.EQU);
//        ExprNode exp = parseValue();
//        accept(TokenKind.SEMICOLON);
//        if (error) return null;
//        return new AsgnStmtNode(exp1, exp);
//        return null;
//    }

    /* Parse Loops */
    List<AssignedStatement> parseForInit() {
        /*
         *
         *
         */
        if (error) return null;
        List<AssignedStatement> init = null;
        ExpressionStatement<?> iden, val;
        Context ctx = Context.context;
        if (token.kind == TokenKind.SEMICOLON)
            return init;
        init = new LinkedList<>();
        while (token.kind == TokenKind.VAR || token.kind == TokenKind.IDENTIFIER) {
            if (error)
                return null;
            if (token.kind == TokenKind.VAR) {
                accept(TokenKind.VAR);
                iden = parseIdentifier();
                SymbolReference sym = ctx.createSymbolReference(iden.getName(), Node.Tag.VARDECL);
                accept(TokenKind.EQU);
                val = parseValue();
                DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, val);
                if (token.kind == TokenKind.COMMA)
                    nextToken();
                init.add(stmt);
            } else {
                // this else is not needed but keeping just for reference..
                iden = parseIdentifier();
                SymbolReference sym = ctx.createSymbolReference(iden.getName(), Node.Tag.VARDECL);
                accept(TokenKind.EQU);
                val = parseValue();
                DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, val);
                if (token.kind == TokenKind.COMMA)
                    nextToken();
                init.add(stmt);
            }
        }
        if (error) return null;
        if (init.isEmpty()) return null;
        return init;
    }

    ExpressionStatement<?> parseForCondition() {
        /*
         * parseLogOrEcpression()
         */
        if (error) return null;
        ExpressionStatement<?> cond = null;
        if (token.kind == TokenKind.SEMICOLON)
            return cond;
        cond = parseLogicalOrExpression();
        if (error) return null;
        return cond;
    }

    List<CafeStatement<?>> parseForIncrement() {
        /*
         *
         * parseAssignmentStatement()
         */
        if (error) return null;
        ExpressionStatement<?> iden, val, exp2;
        List<CafeStatement<?>> incrNodes = null;
        if (token.kind == TokenKind.RPAREN) {
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

    CafeStatement<?> parseForStatement() {
        /*
         * accept(FOR) accept(LPAREN) if (SEMI ) parseForCondition() accept(SEMI)
         * parseForIncr() else parseForInit() accept(SEMI) parseForCondition()
         * accept(SEMI) parseForIncr()
         */
        if (error) return null;
        Context context = Context.context;
        ForLoopStatement forLoop = ForLoopStatement.loop();
        context.forLoopStack.push(forLoop);
        List<AssignedStatement> init;
        ExpressionStatement<?> cond;
        List<CafeStatement<?>> incrNode;
        Block block;
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
        forLoop.condition(cond)
                .init(init)
                .postStatement(incrNode)
                .block(block);
        context.forLoopStack.pop();
        return forLoop;
    }

//    CafeStatement<?> parseLoopStatement() {
//        /*
//         * accept(LOOP) parseLoopIdentifier() accept(IN) parseLoopValue()
//         * parseLoopBlock() parseCollectionComprehension()
//         *
//         */
//        if (error) return null;
//        ExpressionStatement<?> iden1 = null, iden2 = null;
//        ExpressionStatement<?> exp = null;
//        Block block=null;
//
//        accept(TokenKind.LOOP);
//        iden1 = parseIdentifier();
//        if (token.kind == TokenKind.COMMA) {
//            nextToken();
//            iden2 = parseIdentifier();
//        }
//        accept(TokenKind.IN);
//        try {
//            exp = parseAtomExpression();
//        } catch (ParseException ignored) {
//        }
//
//        if (exp == null) {
//            if (token.kind == TokenKind.LCURLY)
//                exp = parseCollection();
//            else
//                exp = parseObjectCreation();
//
//        }
//        accept(TokenKind.LCURLY);
//        block = parseLoopBlock();
//        accept(TokenKind.RCURLY);
//        if (error) return null;
//        TODO: Implementation of LoopStmt IR node is yet to be done.
//        return new LoopStmtNode(iden1, iden2, exp, block);
//        return null;
//    }

    CafeStatement<?> parseFlowStatement() {
        /*
         * if(CONTINUE) return ContinueNode if(BREAK) return BreakNode
         */
        if (error) return null;
        Token tk = token;
        Context context = Context.context;
        if (breakAllowed)
            if (token.kind == TokenKind.CONTINUE) {
                accept(TokenKind.CONTINUE);
                accept(TokenKind.SEMICOLON);
                ContinueStmtNode continueStmtNode = new ContinueStmtNode();
                continueStmtNode.setFirstToken(tk);
                return BreakContinueStatement.newContinue()
                        .setEnclosingLoop(context.forLoopStack.peek());
            } else {
                accept(TokenKind.BREAK);
                accept(TokenKind.SEMICOLON);
//                BreakStmtNode breakStmtNode = new BreakStmtNode();
//                breakStmtNode.setFirstToken(tk);
                return BreakContinueStatement.newBreak()
                        .setEnclosingLoop(context.forLoopStack.peek());
            }
        else {
            error = true;
            accept(TokenKind.IDENTIFIER);
        }
        return null;
    }
    /* Parse Loop Done */

    /* Parse Collection */
    ExpressionStatement<?> parseList() {
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
//        List<ExpressionStatement<?> listNode = new ArrayList<>();
        ListCollection listNode = ListCollection.list();
        ExpressionStatement<?> exp1;
        exp1 = parseValue();
//        if (token.kind == TokenKind.RANGE) {
            // TODO: to implement IR node implementation for RANGE
            // accept(TokenKind.RANGE);
            // exp2 = parseValue();
            // accept(TokenKind.RSQU);
            // return new RangeNode(exp1, exp2, RangeNode.Type.LIST);
//        }
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
        return listNode;
    }

    ExpressionStatement<?> parseListCollection() {
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
            return ListCollection.list();
        } else if (token.kind == TokenKind.LOOP) {
            if (error) return null;
            // TODO: implement IR node for Comprehension node
            // return parseComprehension("list");        // Accept Identifier Before
            return null;
        } else {
            if (error) return null;
            return parseList();
        }
    }

//    void parseMap() { // NOT Used by MapColl
//        /*
//         * List of Map<dynamic, dynamic>
//         *
//         * case RBRACKET: return List.add(map)) case LBRACKET: map.addKey(parseValue())
//         * accept(COMMA) map.addValue(parseValue()) List.add(map)
//         *
//         * return map
//         */
//    }

    ExpressionStatement<?> parseMapCollection() {
        /*
         * List of MapCollection List of Comp
         *
         * case: LBRACKET mapCollection.add(parseMap())
         *
         * if (COMMA) : mapCollection.add(parseMap()) if (LOOP )
         * Comp.add(parseForComprehension())
         *
         */
        // TODO: to implement Map IR node
//        if (error) return null;
//        Map<ExprNode, ExprNode> pairs = new LinkedHashMap<>();
//        nextToken();
//        accept(TokenKind.LSQU);
//        debug.add("Map Collection : " + token.kind);
//        while (token.kind != TokenKind.RSQU) {
//            if (error)
//                return null;
//            ExprNode exp2 = new MapCollNode(), exp1 = new MapCollNode();
//            if (token.kind == TokenKind.COMMA)
//                nextToken();
//            accept(TokenKind.LSQU);
//            if (token.kind == TokenKind.RSQU) {
//                pairs.put(exp1, exp2);
//                accept(TokenKind.RSQU);
//                accept(TokenKind.COMMA);
//                continue;
//            } else if (token.kind == TokenKind.LOOP) {
//                return parseComprehension("map");        // Accept Identifier Before
//            } else {
//                exp1 = parseValue();
//                accept(TokenKind.COMMA);
//                exp2 = parseValue();
//                accept(TokenKind.RSQU);
//            }
//
//            pairs.put(exp1, exp2);
//        }
//        accept(TokenKind.RSQU);
//        if (error) return null;
//        return new MapCollNode(pairs);
        return null;
    }

//    ExpressionStatement<?> parseComprehension(String type) {
//        if (error) return null;
//        IdenNode iden1, iden2 = null;
//        ExprNode exp = null;
//        BlockNode block = null;
//        ExprNode ifCond;
//
//        CompTypeNode mapComp = type == "map" ? new MapCompNode()
//                : type == "list" ? new ListCompNode()
//                : type == "set" ? new SetCompNode() : type == "link" ? new LinkCompNode() : null;
//
//        while (token.kind != TokenKind.RSQU) {
//            if (error)
//                return null;
//            switch (token.kind) {
//                case LOOP:
//                    accept(TokenKind.LOOP);
//                    iden1 = parseIdentifier();
//                    if (token.kind == TokenKind.COMMA) {
//                        nextToken();
//                        iden2 = parseIdentifier();
//                    }
//                    accept(TokenKind.IN);
//                    exp = parseCollection();
//                    if (exp == null) {
//                        if (token.kind == TokenKind.LCURLY)
//                            exp = parseObjectCreation();
//                        else
//                            try {
//                                exp = parseAtomExpression();
//                            } catch (ParseException e) {
//                            }
//                    }
//                    mapComp.addExpr(new CompLoopNode(iden1, iden2, exp));
//                    break;
//                case IF:
//                    accept(TokenKind.IF);
//                    accept(TokenKind.LPAREN);
//                    ifCond = parseLogicalOrExpression();
//                    accept(TokenKind.RPAREN);
//                    debug.add("Comprehension: " + token.kind);
//                    mapComp.addExpr(new CompIfNode(ifCond));
//                    break;
//                default:
//                    error = true;
//                    accept(TokenKind.IDENTIFIER);
//            }
//        }
//        accept(TokenKind.RSQU);
//        if (error) return null;
//        return mapComp;
//        return  null;
//    }

//    ExpressionStatement<?> parseSet() {      // Not Used
        /*
         * List of Values
         *
         * accept(LBRACKET) List.add(parseValue()) if(COMMA or DOTDOT)
         * List.add(parseValue()) else if(LOOP_KEYWORD)
         * List.add(parseForComprehension())
         *
         * return List
         */
        // Not used
//        if (error) return null;
//        List<ExprNode> setNode = new ArrayList<>();
//        ExprNode exp1, exp2;
//        exp1 = parseValue();
//        if (token.kind == TokenKind.RANGE) {
//            accept(TokenKind.RANGE);
//            exp2 = parseValue();
//            accept(TokenKind.RSQU);
//            return new RangeNode(exp1, exp2, RangeNode.Type.SET);
//        }
//        setNode.add(exp1);
//        while (token.kind != TokenKind.RSQU) {
//            if (error)
//                return null;
//            accept(TokenKind.COMMA);
//            if (token.kind == TokenKind.RSQU) accept(TokenKind.IDENTIFIER);
//            setNode.add(parseValue());
//        }
//        accept(TokenKind.RSQU);
//        if (error) return null;
//        return new SetCollNode(setNode);
//        return null;
//    }

    ExpressionStatement<?> parseSetCollection() {
        /*
         * List of Collection
         *
         * if (RBRACKET){ return SetNode() }
         *
         * else { return parseSet(); }
         *
         *
         */
        // TODO: to implement SET IR node
//        if (error) return null;
//        nextToken();
//        accept(TokenKind.LSQU);
//        if (token.kind == TokenKind.RSQU) {
//            accept(TokenKind.RSQU);
//            if (error) return null;
//            return new SetCollNode();
//        } else if (token.kind == TokenKind.LOOP) {
//            if (error) return null;
//            return parseComprehension("set");    // Accept Identifier Before
//        } else {
//            if (error) return null;
//            return parseSet();
//        }
        return null;
    }

//    ExpressionStatement<?> parseLink() {  // Not Used
        /*
         * List of Values
         *
         * accept(LBRACKET) List.add(parseValue()) if(COMMA or DOTDOT)
         * List.add(parseValue()) else if(LOOP_KEYWORD)
         * List.add(parseForComprehension())
         *
         * return List
         */
        // not Used
//        if (error) return null;
//        List<ExprNode> listNode = new ArrayList<>();
//        ExprNode exp1, exp2;
//        exp1 = parseValue();
//        if (token.kind == TokenKind.RANGE) {
//            accept(TokenKind.RANGE);
//            exp2 = parseValue();
//            accept(TokenKind.RSQU);
//            return new RangeNode(exp1, exp2, RangeNode.Type.LINK);
//        }
//        listNode.add(exp1);
//        while (token.kind != TokenKind.RSQU) {
//            if (error)
//                return null;
//            accept(TokenKind.COMMA);
//            if (token.kind == TokenKind.RSQU) accept(TokenKind.IDENTIFIER);
//            listNode.add(parseValue());
//        }
//        accept(TokenKind.RSQU);
//        if (error) return null;
//        return new LinkCollNode(listNode);
//        return null;
//    }

    ExpressionStatement<?> parseLinkCollection() {
        /*
         * List of Collection
         *
         * if (RBRACKET){ return ListNode() }
         *
         * else { return parseList(); }
         *
         *
         */
        // TODO: to implement LINK IR node
//        if (error) return null;
//        nextToken();
//        accept(TokenKind.LSQU);
//        if (token.kind == TokenKind.RSQU) {
//            accept(TokenKind.RSQU);
//            if (error) return null;
//            return new LinkCollNode();
//        } else if (token.kind == TokenKind.LOOP) {
//            if (error) return null;
//            return parseComprehension("link");        // Accept Identifier Before
//        } else {
//            if (error) return null;
//            return parseLink();
//        }

        return null;
    }

    ExpressionStatement<?> parseCollection() {
        /*
         * List of Collection
         *
         * read TokenType: case LBRACKET: parseListCollection() case LINK: case SET:
         * parseCollection() case MAP: parseMapCollection()
         *
         * return List
         */
        if (error) return null;
        ExpressionStatement<?> collExpr = null;

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
    ExpressionStatement<?> parseObjectCreation() {
        /*
         * List of ObjectNode
         *
         * List.add(parseObject())
         *
         * if Comma { List.add(parseObject()) }
         *
         */

        if (error) return null;
        Map<String, ExpressionStatement<?>> object = new LinkedHashMap<>();
        ExpressionStatement<?> idenNode;
        ExpressionStatement<?> exprNode;

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

//    void parseObject() { // Not Used
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
//    }

    AnonymousFunction parseAnnFunction() {
        /*
         * accept(FUNC) isAnnFunmc = true parseFunction()
         *
         */
        if (error) return null;
        Context context = Context.context;
        String funcName = "#_ANN_Hello";
        accept(TokenKind.FUNC);
        accept(TokenKind.LPAREN);
        context.enterFunc(funcName);
        funcName = context.getNextAnnFuncName();
        List<String> params = parseParameter();
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        debug.add("Ann Func Node: " + token.kind);
        Block block = parseBlock();
        accept(TokenKind.RCURLY);

        if (!block.hasReturn())
            block.add(ReturnStatement.of(null));
        CafeFunction function = CafeFunction.function(funcName)
                .block(block)
                .withParameters(params);
        if (context.isExport)
            function = function.asExport();

        context.addFunction(function);

        ExpressionStatement<?> expression;
        if (context.currentScope() == IRParser.Context.Scope.CLOSURE) {
            expression = function.asClosure();
        } else {
            expression = FunctionWrapper.wrap(function);
        }
        context.leaveFunc();
//        while (token.kind != TokenKind.RCURLY) {
//            if (error)
//                return null;
//            List<StmtNode> stm = parseBlock();
//            if (stm == null) return null;
//            stmt.addAll(stm);
//        }
//        debug.add("Ann Func Node: " + token.kind);
//        accept(TokenKind.RCURLY);
//        BlockNode block = new BlockNode(); // BlockNode(stmt);
//        block.setStmt(stmt);
        TargetFuncWrapper targetFuncWrapper = (TargetFuncWrapper) expression;
        AnonymousFunction anonymousFunction = AnonymousFunction.func(targetFuncWrapper);
        if (error) return null;
        return anonymousFunction;
    }

    ExpressionStatement<?> parseValue() {
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
        ExpressionStatement<?> valExpr ;
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

//    void parseVariableDeclaration() {
//        /*
//         *
//         * parseIdentifier() parseEqualOperator() parseValue()
//         *
//         * return VariableNode
//         */
//
//    }

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
//            Token tk = token;
            ExpressionStatement<?> iden = parseIdentifier();
            ExpressionStatement<?> exp = null;
            Context ctx = Context.context;
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
            SymbolReference sym = ctx.createSymbolReference(iden.getName(), Node.Tag.VARDECL);
            DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, exp);
            varDeclNodes.add(stmt);
//            VarDeclNode varDecl = new VarDeclNode(idenNode, exp);
//            varDecl.setFirstToken(tk);
//            varDeclNodes.add(varDecl);
        }
        accept(TokenKind.SEMICOLON);
        if (error) return null;
        return varDeclNodes;
    }

    List<DeclarativeAssignmentStatement> parseConstVariable() {
        /*
         * List Variables checks grammar for variable Declaration
         *
         *
         * calls parseVariableDeclaration
         *
         * return List
         */
        if (error) return null;
        List<DeclarativeAssignmentStatement> constDeclNodes = new ArrayList<>();
        accept(TokenKind.CONST);
        Context ctx = Context.context;
        while (token.kind != TokenKind.SEMICOLON) {
            if (error)
                return null;
//            Token tk = token;
            ExpressionStatement<?> iden = parseIdentifier();
            accept(TokenKind.EQU);
            ExpressionStatement<?> exp = parseValue();
            if (token.kind != TokenKind.SEMICOLON)
                accept(TokenKind.COMMA);
            SymbolReference sym = ctx.createSymbolReference(iden.getName(), Tag.CONSTDECL);
            DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, exp);
//            ConstDeclNode constDecl = new ConstDeclNode(idenNo, exp);
//            constDecl.setFirstToken(tk);
            constDeclNodes.add(stmt);
        }
        accept(TokenKind.SEMICOLON);
        if (error) return null;
        return constDeclNodes;

    }

    List<String> parseParameter() {
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
//        boolean varArg = false;
        List<String> idenNodes = new ArrayList<>();

        while (token.kind != TokenKind.RPAREN) {
            if (error)
                return null;
            debug.add("ARG List: " + token.kind);
            debug.add("ARG List: " + token.value());

            if (token.kind == TokenKind.VARARGS) {
                accept(TokenKind.VARARGS);
//                varArg = true;
                idenNodes.add(parseIdentifier().getName());
                // accept(TokenKind.RPAREN);
                break;
            }
            idenNodes.add(parseIdentifier().getName());
            if (TokenKind.RPAREN != token.kind)
                accept(TokenKind.COMMA);
        }

        if (error) return null;
        return idenNodes;
    }

    // ExprNode parseFunctionCall(){
    // IdenNode funcName = parseIdentifier();

    // }

    DeclarativeAssignmentStatement parseFunctionDeclaration() {
        /*
         * List of Parameter BlockNode
         *
         * calls: name = isAnnFunc ? null : parseIdentifier() parameter =
         * parseParameter() BlockNode= parseBlockStatement()
         *
         * FunctionNode(name, parameter, BlockNode); returns FunctionNode
         */
        if (error) return null;
        Context context = Context.context;
        accept(TokenKind.FUNC);
//        Token tk = token;
        ExpressionStatement<?> funcName = parseIdentifier();
        context.enterFunc(funcName.getName());
        accept(TokenKind.LPAREN);
        List<String> arg = parseParameter();
        accept(TokenKind.RPAREN);
        accept(TokenKind.LCURLY);
        Block block = parseBlock();
        accept(TokenKind.RCURLY);
        if (!block.hasReturn())
            block.add(ReturnStatement.of(null));
        CafeFunction function = CafeFunction.function(funcName.getName())
                .block(block)
                .withParameters(arg);
        if (context.isExport)
            function = function.asExport();

        context.addFunction(function);

        ExpressionStatement<?> expression;
        if (context.currentScope() == IRParser.Context.Scope.CLOSURE) {
            expression = function.asClosure();
        } else {
            expression = FunctionWrapper.wrap(function);
        }
        context.leaveFunc();
        SymbolReference ref = context.createSymbolReference(funcName.getName(), Node.Tag.VARDECL);
        DeclarativeAssignmentStatement statement = DeclarativeAssignmentStatement.create(ref, expression);
//        BlockNode block = new BlockNode();
//        block.setStmt(stmt);
        if (error) return null;

//        FuncDeclNode funcDecl = new FuncDeclNode((IdenNode) funcName, arg, block);
//        funcDecl.setFirstToken(tk);
        return statement;
    }

//    void parseDeclarativeStatement() {
//        /*
//         * List of Declarative Statement
//         *
//         * Checks Type of Statement and calls below methods to parse calls parseFunction
//         * calls parseVariable
//         */
//    }

//    void block() {
//        /*
//         * Handles Cases For Each Type of Block Statements like DECL, ASGN, IF etc. and
//         * calls respective methods
//         *
//         */
//    }

//    void parseBlockStatement() {
//        /*
//         * List of Statement
//         *
//         * parse Grammar, checks type of Statement and calls block()
//         */
//
//        /*
//         * switch(token.kind){ case VAR }
//         */
//    }

    ReturnStatement parseReturnStatement() {
        if (error) return null;
        accept(TokenKind.RET);
//        Token tk = token;
        ExpressionStatement<?> exp = parseValue();
        debug.add("Return : " + exp);
        accept(TokenKind.SEMICOLON);
        if (error) return null;
        //        rtrnNode.setFirstToken(tk);
        return ReturnStatement.of(exp);
    }

    Block parseLoopBlock() {
        /*
         * parseBlockStatement() parseFlowStatement()
         */
        if (error) return null;
//        List<CafeStatement<?>> blockStats = new ArrayList<>();
//
//        Context context = Context.context;
//        Block block = context.enterScope();
//        while (token.kind != TokenKind.RCURLY) {
//            if (error)
//                return null;
//            switch (token.kind) {
//                case CONTINUE:
//                case BREAK:
//                    blockStats.add(parseFlowStatement());
//                    break;
//                default:
        Block block = parseBlock();
//                    List<CafeStatement<?>> stm = parseBlock();
//                    if (stm == null) return null;
//                    blockStats.addAll(stm);
//                    for (CafeStatement<?> stmt: blockStats){
//                        block.add(stmt);
//                    }
//            }
//        }
        if (error) return null;
        return block;
    }
    List<CafeStatement<?>> parseStatement() {
        List<CafeStatement<?>> block = new ArrayList<>();
        switch (token.kind) {
            case VAR:
                List<DeclarativeAssignmentStatement> stm = parseVariable();
                System.out.println("Var Statement: "+stm);
                if (stm == null) return null;
                block.addAll(stm);
                break;
            case CONST:
                List<DeclarativeAssignmentStatement> stm1 = parseConstVariable();
                System.out.println("const Statement: "+stm1);
                if (stm1 == null) return null;
                block.addAll(stm1);
                break;
            case FUNC:
                DeclarativeAssignmentStatement decl = parseFunctionDeclaration();
                System.out.println("For Statement: "+decl);
                if (decl == null) return null;
                block.add(decl);
                break;
            case IF:
                CafeStatement<ConditionalBranching> stm2 = parseIfStatement();
                System.out.println("If Statement: "+stm2);
                if (stm2 == null) return null;
                block.add(stm2);
                break;
            case FOR:
                boolean innerLoop = breakAllowed;
                breakAllowed = true;
                CafeStatement<?> stm3 = parseForStatement();
                System.out.println("for Statement: "+stm3);
                if (stm3 == null) return null;
                block.add(stm3);
                breakAllowed = innerLoop;
//                innerLoop = false;
                break;
            case LOOP:
                System.out.println("Entered to Loop:");
//                CafeStatement<?> stm4 = parseLoopStatement();
//                if (stm4 == null) return null;
//                block.add(stm4);
                break;
            case RET:
                CafeStatement<ReturnStatement> stm5 = parseReturnStatement();
                if (stm5 == null) return null;
                block.add(stm5);
                break;
            case IDENTIFIER:
            case THIS:
            case NULL:
                CafeStatement<?> stm6 = parseExprStmt();
                if (stm6 == null) return null;
                block.add(stm6);
                System.out.println("Iden This Block Stmt: " + stm6+error);
                System.out.println("Iden This BlockToken: " + token.value());
                accept(TokenKind.SEMICOLON);
                break;
            case BREAK:
            case CONTINUE:
                CafeStatement<?> stm7 = parseFlowStatement();
                block.add(stm7);
                System.out.println("Break Continue Block Stmt: " + stm7);
                debug.add("Break Continue Block Stmt: " + token.kind);
                break;
            default:
                logError(TokenKind.IDENTIFIER);
        }
        if(error) return null;
        System.out.println("Block Statement: "+block);
        return block;
    }
    // return Block Statement Node
    Block parseBlock() {
        /*
         * List of block Statements calls parseBlockStatement
         */
        if (error) return null;

        Context context = Context.context;
        Block block = context.enterScope();

//        for (Node.StmtNode stmt : n.block) {
//            stmt.accept(this);
//            CafeStatement<?> statement = (CafeStatement<?>) context.pop();
//            block.add(statement);
//        }
//        context.push(block);

        List<CafeStatement<?>> blockStmt = new ArrayList<>();
        debug.add("Token kind " + token.kind);
        System.out.println("Block Entered");
        while(token.kind != TokenKind.RCURLY) {
            if (error) return null;
            List<CafeStatement<?>> stm = parseStatement();
            System.out.println("Block Stmt: "+stm);
            if (stm == null) return null;
            blockStmt.addAll(stm);
        }
        for(CafeStatement<?> stmt: blockStmt){
            block.add(stmt);
        }

        context.leaveScope();
        if (error) return null;
        debug.add("Block Stmt: " + block);
        System.out.println("Block: "+block);
        return block;
    }

    List<DeclarativeAssignmentStatement> parseExportStatement() {
        List<CafeExport> exportStmtNode = new ArrayList<>();

        Context context = Context.context;
        context.isExport = true;
//        String name = n.iden.name;
//        CafeExport export = CafeExport.export(name);
//        context.module.addExport(export);
//        if (n.node == null) {
//            // Just pushing to avoid error
//            context.push(export);
//        } else {
//            n.node.accept(this);
//        }
        List<DeclarativeAssignmentStatement> decl = new ArrayList<>();

        accept(TokenKind.EXPORT);
        switch (token.kind) {
            case IDENTIFIER:
                ExpressionStatement<?> id = parseIdentifier();
                if (id == null) return null;
                exportStmtNode.add(CafeExport.export(id.getName()));
                while (token.kind == TokenKind.COMMA) {
                    accept(TokenKind.COMMA);
                    id = parseIdentifier();
                    if (id == null) return null;
                    exportStmtNode.add(CafeExport.export(id.getName()));
                }
                accept(TokenKind.SEMICOLON);
                break;

            case VAR:
//                List<DeclarativeAssignmentStatement> stm = parseVariable();
                decl = parseVariable();
                if (decl == null) return null;
                for (DeclarativeAssignmentStatement var : decl) {
                    exportStmtNode.add(CafeExport.export(var.getSymbolReference().getName()));
                }
                break;
            case CONST:
                decl = parseConstVariable();
                if (decl == null) return null;
                for (DeclarativeAssignmentStatement var : decl) {
                    exportStmtNode.add(CafeExport.export(var.getSymbolReference().getName()));
                }
                break;
            case FUNC:
                DeclarativeAssignmentStatement funcDecl = parseFunctionDeclaration();
                if (funcDecl == null) return null;
                exportStmtNode.add(CafeExport.export(funcDecl.getName()));
                decl.add(funcDecl);
                break;
            default:
                error = true;
        }

        context.isExport = false;
        for(CafeExport expo : exportStmtNode){
            context.module.addExport(expo);
        }
        if (error) return null;
        return decl;
    }

    // return Import Statement Node
    CafeImport parseImportStatement() {
        /* List of Imports */

        // accept('@');
        // boolean valid = checkFilePathRegex(token.value());
        // if(valid) return ImportStatement(token.value())
        // else Throw Error
//        ImportStmtNode importStmtNode = null;
        Map<String, String> blocks = new HashMap<>();
        ExpressionStatement<?> id1, id2 = null;
        Context context = Context.context;
//        for (Map.Entry<Node.IdenNode, Node.IdenNode> entry : n.importAliasMap.entrySet()) {
//            Node.IdenNode value = entry.getValue();
//            String alias = null;
//            if (value != null)
//                alias = value.name;
//            cafeImport.add(entry.getKey().name, alias);
//        }


        accept(TokenKind.IMPORT);
        if(token.kind == TokenKind.IDENTIFIER){
            id1 = parseIdentifier();
            if (token.kind == TokenKind.AS) {
                accept(token.kind);
                id2 = parseIdentifier();
            }
            blocks.put(id1.getName(), id2 != null ?  id2.getName() : null);
            while (token.kind == TokenKind.COMMA) {
                accept(TokenKind.COMMA);
                id1 = parseIdentifier();
                id2 = null;
                if (token.kind == TokenKind.AS) {
                    accept(token.kind);
                    id2 = parseIdentifier();
                }
                blocks.put(id1.getName(), id2 != null ? id2.getName() : null);
            }
        } else {
            if(accept(TokenKind.MUL)) {
//                id1 = new IdenNode("*");
                accept(TokenKind.AS);
                id2 = parseIdentifier();
                blocks.put("*", id2.getName());
            }
        }

        accept(TokenKind.FROM);
//        File file = new File(token.value()+".class");
//        if(!file.exists()){
//
//            logError(INVALID_IMPORT_FILE, token.value());
//            error = true;
//        } else {

        CafeImport cafeImport = CafeImport.of(token.value());
//        importStmtNode = new ImportStmtNode(blocks, token.value());
        nextToken();
        accept(TokenKind.SEMICOLON);
        for (Map.Entry<String, String> entry : blocks.entrySet()) {
            cafeImport.add(entry.getKey(), entry.getValue());
        }
        context.module.addImport(cafeImport);
        return cafeImport;
    }

    // return Statement Node

    // return List Of Statements
    CafeModule parseStatements() {
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
        List<CafeStatement<?>> tree = new ArrayList<>();
        while (token.kind != TokenKind.END) {
            if (error)
                return null;
            switch (token.kind) {
                case IMPORT:
                    CafeImport importStmtNode = parseImportStatement();
                    if (importStmtNode == null) return null;
                    tree.add(importStmtNode);
                    break;
                case EXPORT:
                    List<DeclarativeAssignmentStatement> exports = parseExportStatement();
                    if(exports == null) return null;
                    tree.addAll(exports);
                    break;
                default:
                    List<CafeStatement<?>> stmt = parseStatement();
                    if (stmt == null) return null;
                    tree.addAll(stmt);
                    break;
            }
        }
        if (error) return null;
        debug.add("Block Statements " + tree);
        for( CafeStatement<?> stm: tree){
            Context.context.module.add(stm);
        }
//        return new ProgramNode(tree);
        return Context.context.module;
    }

    // void parseStatementAsBlock() {
    // 	/*
    // 	 * switch(token.kind){ case IF: case FOR: case }
    // 	 */
    // }
}
