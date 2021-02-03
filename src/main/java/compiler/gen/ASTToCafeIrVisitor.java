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

package compiler.gen;

import compiler.ast.Node;
import compiler.ir.*;

import java.util.*;

public class ASTToCafeIrVisitor implements Node.Visitor {

    private static final class Context {
        final static Context context = new Context();

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

        public Scope currentScope() {
            if (functionStack.size() == 0)
                return Scope.GLOBAL;
            if (functionStack.size() == 1)
                return Scope.LOCAL;
            return Scope.CLOSURE;
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
            Scope scope = currentScope();
            if (scope == Scope.GLOBAL)
                return SymbolReference.Scope.GLOBAL;
            if (scope == Scope.LOCAL || scope == Scope.CLOSURE)
                return SymbolReference.Scope.LOCAL;
            throw new AssertionError("Invalid Symbol Scope");
        }

        public void addFunction(CafeFunction function) {
            context.module.addFunction(function);
        }
    }

    public CafeModule transform(Node.ProgramNode n, String moduleName) {
        Context.context.createModule(moduleName);
        Context.context.newObjectStack();
        visitProgram(n);
        return Context.context.module;
    }

    @Override
    public void visitProgram(Node.ProgramNode n) {
        Context context = Context.context;
        for (Node.StmtNode stmt : n.stmts) {
            stmt.accept(this);
            context.module.add((CafeStatement<?>) context.pop());
        }
    }

    @Override
    public void visitVarDecl(Node.VarDeclNode n) {
        Node.IdenNode iden = n.getIden();
        SymbolReference sym = Context.context.createSymbolReference(iden.name, Node.Tag.VARDECL);
        if (n.value != null)
            n.value.accept(this);
        else
            Context.context.push(null);
        DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, Context.context.pop());
        Context.context.push(stmt);
    }

    @Override
    public void visitIden(Node.IdenNode n) {
        Context context = Context.context;
        if (context.isProperty())
            context.push(PropertyAccess.of(n.name));
        else
            context.push(ReferenceLookup.of(n.name));
    }

    @Override
    public void visitConstDecl(Node.ConstDeclNode n) {
        Context context = Context.context;
        Node.IdenNode iden = n.getIden();
        SymbolReference sym = context.createSymbolReference(iden.name, Node.Tag.CONSTDECL);
        n.val.accept(this);
        DeclarativeAssignmentStatement stmt = DeclarativeAssignmentStatement.create(sym, context.pop());
        context.push(stmt);
    }

    @Override
    public void visitNumLit(Node.NumLitNode n) {
        ConstantStatement c = ConstantStatement.of(n.lit);
        Context.context.push(c);
    }

    @Override
    public void visitStrLit(Node.StrLitNode n) {
        ConstantStatement c = ConstantStatement.of(n.lit);
        Context.context.push(c);
    }

    @Override
    public void visitBoolLit(Node.BoolLitNode n) {
        ConstantStatement c = ConstantStatement.of(n.lit);
        Context.context.push(c);
    }

    @Override
    public void visitFuncDecl(Node.FuncDeclNode n) {
        Context context = Context.context;
//        context.enterFunc(n);
//        String name = n.getIden().name;
//        n.params.accept(this);
//        List<String> params = (List<String>) context.pop();
//        n.block.accept(this);
//        Block block = (Block) context.pop();
//        if (!block.hasReturn())
//            block.add(ReturnStatement.of(null));
//        CafeFunction function = CafeFunction.function(name)
//                                            .block(block)
//                                            .withParameters(params);
//        if (context.isExport)
//            function = function.asExport();
//
//        context.addFunction(function);
//
//        ExpressionStatement<?> expression;
//        if (context.currentScope() == Context.Scope.CLOSURE) {
//            expression = function.asClosure();
//        } else {
//            expression = FunctionWrapper.wrap(function);
//        }
//        context.leaveFunc();

        visitFunc(n);
        String name = (String) context.pop();
        ExpressionStatement<?> expression = (ExpressionStatement<?>) context.pop();
        SymbolReference ref = context.createSymbolReference(name, Node.Tag.VARDECL);
        DeclarativeAssignmentStatement statement = DeclarativeAssignmentStatement.create(ref, expression);
        context.push(statement);
    }

    @Override
    public void visitAnnFunc(Node.AnnFuncNode n) {
        Context context = Context.context;
        visitFunc(n);
        String name = (String) context.pop();
        TargetFuncWrapper targetFuncWrapper = (TargetFuncWrapper) context.pop();

//        CafeFunction function;
//        if(expression instanceof FunctionWrapper){
//            function = ((FunctionWrapper) expression).getTarget();
//        }
//        else {
//            function = ((CafeClosure) expression).getTarget();
//        }

        AnonymousFunction anonymousFunction = AnonymousFunction.func(targetFuncWrapper);
        context.push(anonymousFunction);
    }

    private void visitFunc(Node.FuncNode n) {
        Context context = Context.context;
        context.enterFunc(n);

        String name = n.getName();
        Node.ParameterListNode params = n.getParams();
        Node.BlockNode blockNode = n.getBlock();

        if (name == null || name.isEmpty()) {
            // anonymous function name
            name = context.getNextAnnFuncName();
        }

        params.accept(this);
        List<String> paramList = (List<String>) context.pop();
        blockNode.accept(this);
        Block block = (Block) context.pop();

        if (!block.hasReturn())
            block.add(ReturnStatement.of(null));
        CafeFunction function = CafeFunction.function(name)
                                            .block(block)
                                            .withParameters(paramList);
        if (context.isExport)
            function = function.asExport();

        context.addFunction(function);

        ExpressionStatement<?> expression;
        if (context.currentScope() == Context.Scope.CLOSURE) {
            expression = function.asClosure();
        } else {
            expression = FunctionWrapper.wrap(function);
        }
        context.leaveFunc();
        context.push(expression);
        context.push(name);
    }

    @Override
    public void visitObjCreation(Node.ObjCreationNode n) {
        Context context = Context.context;
        Map<Node.IdenNode, Node.ExprNode> map = n.prop;
        Map<String, ExpressionStatement<?>> mapped = new LinkedHashMap<>();

        for (Map.Entry<Node.IdenNode, Node.ExprNode> entry : map.entrySet()) {
            String key = entry.getKey().name;
            entry.getValue()
                 .accept(this);
            mapped.put(key, ExpressionStatement.of(context.pop()));
        }

        ObjectCreationStatement creationStatement = ObjectCreationStatement.of(mapped);
        context.push(creationStatement);
    }

    @Override
    public void visitBlock(Node.BlockNode n) {
        Context context = Context.context;
        Block block = context.enterScope();

        for (Node.StmtNode stmt : n.block) {
            stmt.accept(this);
            CafeStatement<?> statement = (CafeStatement<?>) context.pop();
            block.add(statement);
        }
        context.push(block);
        context.leaveScope();
    }

    @Override
    public void visitListColl(Node.ListCollNode n) {

    }

    @Override
    public void visitSetColl(Node.SetCollNode n) {

    }

    @Override
    public void visitLinkColl(Node.LinkCollNode n) {

    }

    @Override
    public void visitMapColl(Node.MapCollNode n) {

    }

    @Override
    public void visitBinaryExpr(Node.BinaryExprNode n) {
        Context context = Context.context;
        n.e1.accept(this);
        n.e2.accept(this);
        BinaryExpression expr = BinaryExpression.of(n.op)
                                                .right(context.pop())
                                                .left(context.pop());
        context.push(expr);
    }

    @Override
    public void visitUnaryExpr(Node.UnaryExprNode n) {
        Context context = Context.context;
        n.e.accept(this);
        UnaryExpression expr = UnaryExpression.create(
                n.op,
                context.pop()
        );
        context.push(expr);
    }

    @Override
    public void visitThis(Node.ThisNode n) {
        Context context = Context.context;
        boolean isGlobal = false;
        if (context.isModuleScope) {
            isGlobal = true;
        }
        context.push(ThisStatement.create(isGlobal));
    }

    @Override
    public void visitNull(Node.NullNode n) {
        Context context = Context.context;
        context.push(new NullStatement());
    }

    @Override
    public void visitFuncCall(Node.FuncCallNode n) {
        Context context = Context.context;

        boolean isProperty = context.isProperty;
        context.isProperty = false;
        n.args.accept(this);
        context.isProperty = isProperty;

        // eg: a.b()
        // b is a property of a, thus method invocation node is created.
        if (context.isProperty()) {
            n.invokedOn.accept(this);
            context.push(MethodInvocation.create(context.pop(), (List) context.pop()));
        } else {
            // eg: a()
            // a() is normal function call, thus function invocation node is created.
            n.invokedOn.accept(this);
            context.push(FunctionInvocation.create(
                    context.pop(), (List) context.pop()
            ));
        }
    }

    @Override
    public void visitSubscript(Node.SubscriptNode n) {
        Context context = Context.context;
        n.index.accept(this);
        n.subscriptOf.accept(this);
        context.push(SubscriptStatement.create(context.pop(), context.pop()));
    }

    @Override
    public void visitObjAccess(Node.ObjectAccessNode n) {
        Context context = Context.context;

        context.enterProperty();
        n.prop.accept(this);
        context.leaveProperty();

        n.accessedOn.accept(this);
        context.push(ObjectAccessStatement.create(context.pop(), context.pop()));
    }

    @Override
    public void visitSlice(Node.SliceNode n) {

    }

    @Override
    public void visitArgsList(Node.ArgsListNode n) {
        Context context = Context.context;
        List<Object> args = new LinkedList<>();
        for (Node.ExprNode arg : n.args) {
            arg.accept(this);
            args.add(context.pop());
        }
        context.push(args);
    }

    @Override
    public void visitParamList(Node.ParameterListNode n) {
        Context context = Context.context;
        List<String> params = new LinkedList<>();
        for (Node.IdenNode param : n.params) {
            params.add(param.name);
        }
        context.push(params);
    }

    @Override
    public void visitImportStmt(Node.ImportStmtNode n) {
        Context context = Context.context;
        CafeImport cafeImport = CafeImport.of(n.directory);
        for (Map.Entry<Node.IdenNode, Node.IdenNode> entry : n.importAliasMap.entrySet()) {
            Node.IdenNode value = entry.getValue();
            String alias = null;
            if (value != null)
                alias = value.name;
            cafeImport.add(entry.getKey().name, alias);
        }
        context.module.addImport(cafeImport);
        context.push(cafeImport);
    }

    @Override
    public void visitExportStmt(Node.ExportStmtNode n) {
        Context context = Context.context;
        context.isExport = true;
        String name = n.iden.name;
        CafeExport export = CafeExport.export(name);
        context.module.addExport(export);
        if (n.node == null) {
            // Just pushing to avoid error
            context.push(export);
        } else {
            n.node.accept(this);
        }
        context.isExport = false;
        //context.push(export);
    }

    @Override
    public void visitAsgnStmt(Node.AsgnStmtNode n) {
        Context context = Context.context;
        n.rhs.accept(this);
        n.lhs.accept(this);
        AssignmentStatement statement = AssignmentStatement.create(context.pop(), context.pop());
        context.push(statement);
    }

    @Override
    public void visitIfStmt(Node.IfStmtNode n) {
        Context context = Context.context;
        n.ifBlock.accept(this);
        n.ifCond.accept(this);

        ConditionalBranching conditionalBranching = ConditionalBranching
                .branch()
                .condition(context.pop())
                .whenTrue(context.pop());

        if (n.elsePart != null) {
            n.elsePart.accept(this);
            conditionalBranching.otherwise(context.pop());
        }
        context.push(conditionalBranching);
    }

    @Override
    public void visitElseStmt(Node.ElseStmtNode n) {
        n.elsePart.accept(this);
    }

    @Override
    public void visitForStmt(Node.ForStmtNode n) {
        Context context = Context.context;
        ForLoopStatement forLoop = ForLoopStatement.loop();
        context.forLoopStack.push(forLoop);

        List<AssignedStatement> initStatements = null;
        if (n.init != null) {
            initStatements = new LinkedList<>();
            for (Node.StmtNode stmt : n.init) {
                stmt.accept(this);
                initStatements.add((AssignedStatement) context.pop());
            }
        }

        n.cond.accept(this);
        ExpressionStatement<?> condition = (ExpressionStatement) context.pop();

        LinkedList<CafeStatement<?>> postStatements = null;
        if (n.counters != null) {
            postStatements = new LinkedList<>();
            for (Node.StmtNode stmt : n.counters) {
                stmt.accept(this);
                postStatements.add((CafeStatement<?>) context.pop());
            }
        }

        n.block.accept(this);
        Block block = (Block) context.pop();
        forLoop.condition(condition)
               .init(initStatements)
               .postStatement(postStatements)
               .block(block);

        context.forLoopStack.pop();
        context.push(forLoop);
    }

    @Override
    public void visitLoopStmt(Node.LoopStmtNode n) {

    }

    @Override
    public void visitReturnStmt(Node.ReturnStmtNode n) {
        Context context = Context.context;
        n.expr.accept(this);
        ReturnStatement returnStatement = ReturnStatement.of(context.pop());
        context.push(returnStatement);
    }

    @Override
    public void visitContinueStmt(Node.ContinueStmtNode n) {
        Context context = Context.context;
        BreakContinueStatement continueStatement = BreakContinueStatement.newContinue()
                                                                         .setEnclosingLoop(context.forLoopStack.peek());

        context.push(continueStatement);
    }

    @Override
    public void visitBreakStmt(Node.BreakStmtNode n) {
        Context context = Context.context;
        BreakContinueStatement breakStatement = BreakContinueStatement.newBreak()
                                                                      .setEnclosingLoop(context.forLoopStack.peek());
        context.push(breakStatement);
    }

    @Override
    public void visitListComp(Node.ListCompNode n) {

    }

    @Override
    public void visitLinkComp(Node.LinkCompNode n) {

    }

    @Override
    public void visitSetComp(Node.SetCompNode n) {

    }

    @Override
    public void visitMapComp(Node.MapCompNode n) {

    }

    @Override
    public void visitCompLoop(Node.CompLoopNode n) {

    }

    @Override
    public void visitCompIf(Node.CompIfNode n) {

    }

    @Override
    public void visitListRange(Node.RangeNode n) {

    }
}
