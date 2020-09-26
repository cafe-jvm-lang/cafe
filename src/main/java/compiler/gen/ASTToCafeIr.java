package compiler.gen;

import compiler.ast.Node;
import compiler.cafelang.ir.Block;
import compiler.cafelang.ir.CafeModule;
import compiler.cafelang.ir.ReferenceTable;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class ASTToCafeIr implements Node.Visitor {

    private static final class Context{
        final static Context context = new Context();

        public CafeModule module;
        private final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
        private final Deque<Deque<Object>> objectStack = new LinkedList<>();
        private boolean isModuleScope = true;

        private Context(){}

        public CafeModule createModule(String name){
            ReferenceTable global = new ReferenceTable();
            referenceTableStack.push(global);
            module = CafeModule.create(name, global);
            return module;
        }

        public Block enterScope(){
            ReferenceTable blockReferenceTable = referenceTableStack.peek().fork();
            referenceTableStack.push(blockReferenceTable);
            isModuleScope = false;
            return Block.create(blockReferenceTable);
        }

        public void leaveScope(){
            referenceTableStack.pop();
            isModuleScope = true;
        }

        public void newObjectStack() {
            objectStack.push(new LinkedList<>());
        }

        public void popObjectStack() {
            objectStack.pop();
        }

        public boolean isModuleScope() {
            return isModuleScope;
        }
    }

    private <T extends Node> void iterChildern(Collection<T> nodes){
        for(T child: nodes)
            child.accept(this);
    }

    public CafeModule transform(Node.ProgramNode n){
        Context.context.newObjectStack();
        visitProgram(n);
        return Context.context.module;
    }

    @Override
    public void visitProgram(Node.ProgramNode n) {

        iterChildern(n.stmts);
    }

    @Override
    public void visitVarDecl(Node.VarDeclNode n) {

    }

    @Override
    public void visitIden(Node.IdenNode n) {

    }

    @Override
    public void visitConstDecl(Node.ConstDeclNode n) {

    }

    @Override
    public void visitNumLit(Node.NumLitNode n) {

    }

    @Override
    public void visitStrLit(Node.StrLitNode n) {

    }

    @Override
    public void visitBoolLit(Node.BoolLitNode n) {

    }

    @Override
    public void visitFuncDecl(Node.FuncDeclNode n) {

    }

    @Override
    public void visitObjCreation(Node.ObjCreationNode n) {

    }

    @Override
    public void visitBlock(Node.BlockNode n) {

    }

    @Override
    public void visitAnnFunc(Node.AnnFuncNode n) {

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

    }

    @Override
    public void visitUnaryExpr(Node.UnaryExprNode n) {

    }

    @Override
    public void visitThis(Node.ThisNode n) {

    }

    @Override
    public void visitNull(Node.NullNode n) {

    }

    @Override
    public void visitFuncCall(Node.FuncCallNode n) {

    }

    @Override
    public void visitSubscript(Node.SubscriptNode n) {

    }

    @Override
    public void visitObjAccess(Node.ObjectAccessNode n) {

    }

    @Override
    public void visitSlice(Node.SliceNode n) {

    }

    @Override
    public void visitArgsList(Node.ArgsListNode n) {

    }

    @Override
    public void visitParamList(Node.ParameterListNode n) {

    }

    @Override
    public void visitImportStmt(Node.ImportStmtNode n) {

    }

    @Override
    public void visitAsgnStmt(Node.AsgnStmtNode n) {

    }

    @Override
    public void visitIfStmt(Node.IfStmtNode n) {

    }

    @Override
    public void visitElseStmt(Node.ElseStmtNode n) {

    }

    @Override
    public void visitForStmt(Node.ForStmtNode n) {

    }

    @Override
    public void visitLoopStmt(Node.LoopStmtNode n) {

    }

    @Override
    public void visitReturnStmt(Node.ReturnStmtNode n) {

    }

    @Override
    public void visitContinueStmt(Node.ContinueStmtNode n) {

    }

    @Override
    public void visitBreakStmt(Node.BreakStmtNode n) {

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
