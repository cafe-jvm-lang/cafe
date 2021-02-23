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

package compiler.analyzer;

import compiler.ast.Node;
import compiler.ast.Node.*;
import compiler.util.Context;
import compiler.util.Log;
import compiler.util.Position;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import static compiler.util.Log.Type.*;
import static compiler.util.Messages.message;

public class SemanticsChecker implements Node.Visitor {
    protected static final Context.Key<SemanticsChecker> semanticsKey = new Context.Key<>();

    // Global Symbol Table
    private final SymbolTable GST;

    // Current Symbol Table
    private SymbolTable CST;

    private enum Expr {
        LHS, RHS
    }

    private enum ObjAcc {
        accesedOn, prop
    }

    private Expr exprType = null;
    private ObjAcc objType = null;
    // caller node
    private Node.Tag caller = null;

    private boolean isSubscriptIndex = false;

    private Log log;

    public static SemanticsChecker instance(Context context) {
        SemanticsChecker instance = context.get(semanticsKey);
        if (instance == null)
            instance = new SemanticsChecker(context);
        return instance;
    }

    private SemanticsChecker(Context context) {
        context.put(semanticsKey, this);

        GST = new SymbolTable(null);
        CST = GST;

        log = Log.instance(context);
    }

    private Symbol symbol(String name, boolean isConst) {
        return new Symbol(name, isConst);
    }

    private class FuncStack {
        private Deque<Node.FuncNode> funcStack = new LinkedList<>();

        boolean isGlobal() {
            return funcStack.size() == 0;
        }

        boolean isTopLevel() {
            return funcStack.size() == 1;
        }

        boolean isClosure() {
            return funcStack.size() > 1;
        }

        SymbolTable push(Node.FuncNode n, SymbolTable t) {
            funcStack.push(n);
            SymbolTable table = new SymbolTable(t);
            if (isClosure()) {
                table = table.dontOverrideParentDeclarations();
            }
            return table;
        }

        Node.FuncNode pop() {
            return funcStack.pop();
        }
    }

    private FuncStack funcStack = new FuncStack();

    @Override
    public void visitProgram(ProgramNode n) {
        for (StmtNode stmt : n.stmts) {
            stmt.accept(this);
        }
    }

    @Override
    public void visitVarDecl(VarDeclNode n) {
        Symbol sym = symbol(n.getIden().name, false);
        if (!CST.insert(sym))
            logError(DUPLICATE_SYMBOL, n,
                    message(DUPLICATE_SYMBOL, n.getIden().name));
        if (n.value != null)
            n.value.accept(this);
    }

    @Override
    public void visitIden(IdenNode n) {
        if (exprType == Expr.LHS) {
            if (CST.isSymbolConstant(n.name)) {
                logError(REASSIGN_CONSTANT, n,
                        message(REASSIGN_CONSTANT, n.name));
            }
        }
//        else {
//            if (!CST.isSymbolPresent(n.name))
//                logError(SYMBOL_NOT_DECLARED, n,
//                        message(SYMBOL_NOT_DECLARED, n.name));
//        }
    }

    @Override
    public void visitConstDecl(ConstDeclNode n) {
        Symbol sym = symbol(n.getIden().name, true);
        if (!CST.insert(sym))
            logError(DUPLICATE_SYMBOL, n,
                    message(DUPLICATE_SYMBOL, n.getIden().name));
        n.val.accept(this);
    }

    @Override
    public void visitNumLit(NumLitNode n) {
        if (exprType == Expr.LHS && !isSubscriptIndex)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, n.lit.toString()));
    }

    @Override
    public void visitStrLit(StrLitNode n) {
        if (exprType == Expr.LHS && !isSubscriptIndex)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, n.lit));
    }

    @Override
    public void visitBoolLit(BoolLitNode n) {
        if (exprType == Expr.LHS)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, String.valueOf(n.lit)));
    }

    @Override
    public void visitFuncDecl(FuncDeclNode n) {
        Symbol sym = symbol(n.getIden().name, false);
        if (!CST.insert(sym))
            logError(DUPLICATE_SYMBOL, n,
                    message(DUPLICATE_SYMBOL, n.getIden().name));
        CST = funcStack.push(n, CST);
        n.params.accept(this);
        n.block.accept(this);
        funcStack.pop();
        CST = CST.parent;
    }

    @Override
    public void visitObjCreation(ObjCreationNode n) {
        for (Map.Entry<IdenNode, ExprNode> entry : n.prop.entrySet()) {
            entry.getValue()
                 .accept(this);
        }
    }

    @Override
    public void visitBlock(BlockNode n) {
        for (StmtNode stmt : n.block) {
            stmt.accept(this);
        }
    }

    @Override
    public void visitAnnFunc(AnnFuncNode n) {
        CST = funcStack.push(n, CST);
        ;
        n.params.accept(this);
        n.block.accept(this);
        CST = CST.parent;
    }

    @Override
    public void visitListColl(ListCollNode n) {
        for (ExprNode expr : n.val)
            expr.accept(this);
    }

    @Override
    public void visitSetColl(SetCollNode n) {
        for (ExprNode expr : n.val)
            expr.accept(this);
    }

    @Override
    public void visitLinkColl(LinkCollNode n) {
        for (ExprNode expr : n.val)
            expr.accept(this);
    }

    @Override
    public void visitMapColl(MapCollNode n) {
        for (Map.Entry<ExprNode, ExprNode> entry : n.pairs.entrySet()) {
            entry.getKey()
                 .accept(this);
            entry.getValue()
                 .accept(this);
        }
    }

    @Override
    public void visitBinaryExpr(BinaryExprNode n) {
        if (exprType != Expr.LHS) {
            n.e1.accept(this);
            n.e2.accept(this);
        } else {
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, ""));
        }
    }

    @Override
    public void visitUnaryExpr(UnaryExprNode n) {
        if (exprType != Expr.LHS) {
            n.e.accept(this);
        } else {
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, ""));
        }
    }

    @Override
    public void visitThis(ThisNode n) {
        if (exprType == Expr.LHS)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, "this"));
    }

    @Override
    public void visitNull(NullNode n) {
        if (exprType == Expr.LHS)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, "null"));
    }

    @Override
    public void visitFuncCall(FuncCallNode n) {
        // method call name resolution will be done at runtime.
//		if(objType != ObjAcc.prop) {
//			n.invokedOn.accept(this);
//		}
        if (objType == null && exprType == Expr.LHS && !isSubscriptIndex)
            logError(LHS_EXPR_ERROR, n,
                    message(LHS_EXPR_ERROR, n.invokedOn));
        n.args.accept(this);

    }

    @Override
    public void visitSubscript(SubscriptNode n) {
        if (objType == ObjAcc.accesedOn)
            n.subscriptOf.accept(this);
        isSubscriptIndex = true;
        n.index.accept(this);
        isSubscriptIndex = false;
    }

    @Override
    public void visitObjAccess(ObjectAccessNode n) {
        Node.Tag tag;
        if (exprType == Expr.LHS) {
            tag = n.prop.getTag();
            if (tag != Node.Tag.IDEN && tag != Node.Tag.SUBSCRIPT) {
                logError(LHS_EXPR_ERROR, n,
                        message(LHS_EXPR_ERROR, ""));
                return;
            }
            exprType = null;
        }


        objType = ObjAcc.accesedOn;
        n.accessedOn.accept(this);
        objType = ObjAcc.prop;
        if (n.prop.getTag() != Node.Tag.IDEN)
            n.prop.accept(this);
        objType = null;
    }

    @Override
    public void visitSlice(SliceNode n) {
        if (objType == ObjAcc.accesedOn)
            n.slicedOn.accept(this);
        n.start.accept(this);
        n.end.accept(this);
    }

    @Override
    public void visitArgsList(ArgsListNode n) {
        for (ExprNode expr : n.args) {
            expr.accept(this);
        }
    }

    @Override
    public void visitParamList(ParameterListNode n) {
        for (IdenNode iden : n.params) {
            Symbol sym = symbol(iden.name, false);
            if (!CST.insert(sym))
                logError(DUPLICATE_SYMBOL, n,
                        message(DUPLICATE_SYMBOL, iden.name));
        }
    }

    @Override
    public void visitImportStmt(ImportStmtNode n) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visitExportStmt(ExportStmtNode n) {

    }

    @Override
    public void visitAsgnStmt(AsgnStmtNode n) {
        exprType = Expr.LHS;
        n.lhs.accept(this);
        exprType = Expr.RHS;
        n.rhs.accept(this);
    }

    @Override
    public void visitIfStmt(IfStmtNode n) {
        n.ifCond.accept(this);

        CST = new SymbolTable(CST).dontOverrideParentDeclarations();
        n.ifBlock.accept(this);
        CST = CST.parent;
        if (n.elsePart != null) {
            CST = new SymbolTable(CST).dontOverrideParentDeclarations();
            n.elsePart.accept(this);
            CST = CST.parent;
        }
    }

    @Override
    public void visitElseStmt(ElseStmtNode n) {
        n.elsePart.accept(this);
    }

    @Override
    public void visitForStmt(ForStmtNode n) {
        CST = new SymbolTable(CST).dontOverrideParentDeclarations();
        if (n.init != null) {
            for (StmtNode stmt : n.init)
                stmt.accept(this);
        }
        n.cond.accept(this);
        if (n.counters != null) {
            for (AsgnStmtNode stmt : n.counters)
                stmt.accept(this);
        }
        n.block.accept(this);
        CST = CST.parent;
    }

    @Override
    public void visitLoopStmt(LoopStmtNode n) {
        n.var1.accept(this);
        n.var2.accept(this);
        n.collection.accept(this);
        n.block.accept(this);
    }

    @Override
    public void visitReturnStmt(ReturnStmtNode n) {
        if (funcStack.isGlobal()) {
            logError(RETURN_OUTSIDE_BLOCK, n,
                    message(RETURN_OUTSIDE_BLOCK));
            return;
        }
        n.expr.accept(this);
    }

    @Override
    public void visitContinueStmt(ContinueStmtNode n) {

    }

    @Override
    public void visitBreakStmt(BreakStmtNode n) {

    }

    @Override
    public void visitListComp(ListCompNode n) {
        for (CompNode node : n.nested)
            node.accept(this);
    }

    @Override
    public void visitLinkComp(LinkCompNode n) {
        for (CompNode node : n.nested)
            node.accept(this);
    }

    @Override
    public void visitSetComp(SetCompNode n) {
        for (CompNode node : n.nested)
            node.accept(this);
    }

    @Override
    public void visitMapComp(MapCompNode n) {
        for (CompNode node : n.nested)
            node.accept(this);
    }

    @Override
    public void visitCompLoop(CompLoopNode n) {

    }

    @Override
    public void visitCompIf(CompIfNode n) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visitListRange(RangeNode n) {

    }

    private String errorDescription(Position position, String message) {
        return message + ' ' + message(SOURCE_POSITION, position.getStartLine(), position.getStartColumn());
    }

    private void logError(Log.Type issue, Node n, String message) {
        log.report(issue, n.getSourcePosition(),
                errorDescription(n.getSourcePosition(), message));
    }

}
