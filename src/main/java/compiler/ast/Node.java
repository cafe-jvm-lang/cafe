package compiler.ast;

import compiler.util.Position;

import java.util.*;

import static compiler.ast.Node.Tag.*;
import static compiler.parser.Tokens.Token;

public abstract class Node {

    private Token firstToken;
    private Token lastToken;
    private Position position = null;

    public void setFirstToken(Token firstToken) {
        this.firstToken = firstToken;
    }

    public void setLastToken(Token lastToken) {
        this.lastToken = lastToken;
    }

    public Position getSourcePosition() {
        if (position == null) {
            int startLine = firstToken.pos.getStartLine();
            int startColumn = firstToken.pos.getStartColumn();
            int endLine = firstToken.pos.getEndLine();
            int endColumn = firstToken.pos.getEndColumn();

            if (lastToken != null) {
                endLine = lastToken.pos.getEndLine();
                endColumn = lastToken.pos.getEndColumn();
            }
            position = Position.of(startColumn, startLine, endColumn, endLine);
        }

        return position;
    }

    public abstract Tag getTag();

    public abstract void accept(Visitor visitor);

    public enum Tag {
        VARDECL, IDEN, CONSTDECL, NUMLIT, STRLIT, BOOLLIT, FUNCDECL, OBJCREATION, BLOCK, ANNFUNC, LIST, SET, LINKEDLIST,
        MAP, BINEXPR, UNEXPR, THIS, NULL, FUNCCALL, SUBSCRIPT, SLICE, OBJACCESS, ARGSLIST, PARAMLIST, IMPORT, ASGN, IF,
        ELSE, FOR, LOOP, RETURN, CONTINUE, BREAK, LISTCOMP, SETCOMP, LINKCOMP, MAPCOMP, COMPLOOP, COMPIF, RANGE,
        PROGRAM;
    }

    public static abstract class StmtNode extends Node {

    }

    public static abstract class ExprNode extends StmtNode {

    }

    public static abstract class DeclNode extends StmtNode {

    }

    public static class ProgramNode extends StmtNode {
        public List<StmtNode> stmts;

        public ProgramNode(List<StmtNode> tr) {
            stmts = tr;
        }

        @Override
        public Tag getTag() {
            return PROGRAM;
        }

        @Override
        public void accept(Visitor v) {
            v.visitProgram(this);
        }

    }

    public static class IdenNode extends ExprNode {
        public String name;

        public IdenNode(String n) {
            name = n;
        }

        @Override
        public Tag getTag() {
            return IDEN;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIden(this);
        }
    }

    public static class NumLitNode extends ExprNode {
        public Number lit;

        public NumLitNode(Number v) {
            lit = v;
        }

        @Override
        public Tag getTag() {
            return NUMLIT;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNumLit(this);
        }
    }

    public static class StrLitNode extends ExprNode {
        public String lit;

        public StrLitNode(String v) {
            this.lit = v;
        }

        @Override
        public Tag getTag() {
            return STRLIT;
        }

        @Override
        public void accept(Visitor v) {
            v.visitStrLit(this);
        }
    }

    public static class BoolLitNode extends ExprNode {
        public boolean lit;

        public BoolLitNode(boolean l) {
            this.lit = l;
        }

        @Override
        public Tag getTag() {
            return BOOLLIT;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBoolLit(this);
        }
    }

    public static class ObjCreationNode extends ExprNode {
        public Map<IdenNode, ExprNode> prop = new LinkedHashMap<>();

        public ObjCreationNode() {
        }

        public ObjCreationNode(Map<IdenNode, ExprNode> m) {
            prop = m;
        }

        public void addProp(IdenNode n, ExprNode e) {
            prop.put(n, e);
        }

        public void setProp(Map<IdenNode, ExprNode> m) {
            prop = m;
        }

        @Override
        public Tag getTag() {
            return OBJCREATION;
        }

        @Override
        public void accept(Visitor v) {
            v.visitObjCreation(this);
        }

    }

    public static class AnnFuncNode extends ExprNode {
        public ParameterListNode params;
        public BlockNode block;

        public AnnFuncNode(ParameterListNode a, BlockNode b) {
            params = a;
            block = b;
        }

        @Override
        public Tag getTag() {
            return ANNFUNC;
        }

        @Override
        public void accept(Visitor v) {
            v.visitAnnFunc(this);
        }
    }

    public static class RangeNode extends ExprNode {
        public static enum Type {
            LIST,
            SET,
            LINK
        }

        public ExprNode rangeStart;
        public ExprNode rangeEnd;
        public Type type;

        public RangeNode(ExprNode rangeStart, ExprNode rangeEnd, Type type) {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
            this.type = type;
        }

        @Override
        public Tag getTag() {
            return RANGE;
        }

        @Override
        public void accept(Visitor v) {
            v.visitListRange(this);
        }
    }

    static abstract class ListTypeCollNode extends ExprNode {
        public List<ExprNode> val = new ArrayList<>();

        public void addToColl(ExprNode n) {
            val.add(n);
        }

        public void setColl(List<ExprNode> l) {
            val = l;
        }
    }

    public static class ListCollNode extends ListTypeCollNode {
        public ListCollNode() {
        }

        public ListCollNode(List<ExprNode> l) {
            val = l;
        }

        @Override
        public Tag getTag() {
            return LIST;
        }

        @Override
        public void accept(Visitor v) {
            v.visitListColl(this);
        }
    }

    public static class SetCollNode extends ListTypeCollNode {
        public SetCollNode() {
        }

        public SetCollNode(List<ExprNode> l) {
            val = l;
        }

        @Override
        public Tag getTag() {
            return SET;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSetColl(this);
        }
    }

    public static class LinkCollNode extends ListTypeCollNode {
        public LinkCollNode() {
        }

        public LinkCollNode(List<ExprNode> l) {
            val = l;
        }

        @Override
        public Tag getTag() {
            return LINKEDLIST;
        }

        @Override
        public void accept(Visitor v) {
            v.visitLinkColl(this);
        }
    }

    public static class MapCollNode extends ExprNode {
        public Map<ExprNode, ExprNode> pairs = new HashMap<>();

        public MapCollNode() {
        }

        public MapCollNode(Map<ExprNode, ExprNode> m) {
            pairs = m;
        }

        public void addPair(ExprNode n1, ExprNode n2) {
            pairs.put(n1, n2);
        }

        @Override
        public Tag getTag() {
            return MAP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitMapColl(this);
        }
    }

    public static abstract class CompNode extends ExprNode {
    }

    public static class CompLoopNode extends CompNode {
        public IdenNode var1;
        public IdenNode var2;
        public ExprNode collection;

        public CompLoopNode(IdenNode var1, ExprNode collection) {
            this(var1, null, collection);
        }

        public CompLoopNode(IdenNode var1, IdenNode var2, ExprNode collection) {
            this.var1 = var1;
            this.var2 = var2;
            this.collection = collection;
        }

        @Override
        public Tag getTag() {
            return COMPLOOP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitCompLoop(this);
        }
    }

    public static class CompIfNode extends CompNode {
        public ExprNode ifCond;

        public CompIfNode(ExprNode ifCond) {
            this.ifCond = ifCond;
        }

        @Override
        public Tag getTag() {
            return COMPIF;
        }

        @Override
        public void accept(Visitor v) {
            v.visitCompIf(this);
        }
    }

    public static abstract class CompTypeNode extends ExprNode {
        public List<CompNode> nested;

        public CompTypeNode() {
            nested = new ArrayList<>();
        }

        public CompTypeNode(List<CompNode> n) {
            nested = n;
        }

        public void addExpr(CompNode n) {
            nested.add(n);
        }

        public void setComp(List<CompNode> n) {
            nested = n;
        }
    }

    public static class ListCompNode extends CompTypeNode {
        @Override
        public Tag getTag() {
            return LISTCOMP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitListComp(this);
        }
    }

    public static class LinkCompNode extends CompTypeNode {
        @Override
        public Tag getTag() {
            return LINKCOMP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitLinkComp(this);
        }
    }

    public static class SetCompNode extends CompTypeNode {
        @Override
        public Tag getTag() {
            return SETCOMP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSetComp(this);
        }
    }

    public static class MapCompNode extends CompTypeNode {
        @Override
        public Tag getTag() {
            return MAPCOMP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitMapComp(this);
        }
    }

    public static class BinaryExprNode extends ExprNode {
        public ExprNode e1;
        public ExprNode e2;
        public String op;

        public BinaryExprNode(ExprNode n1, ExprNode n2, String op) {
            e1 = n1;
            e2 = n2;
            this.op = op;
        }

        @Override
        public Tag getTag() {
            return BINEXPR;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBinaryExpr(this);
        }
    }

    public static class UnaryExprNode extends ExprNode {
        public ExprNode e;
        public String op;

        public UnaryExprNode(ExprNode n, String op) {
            e = n;
            this.op = op;
        }

        @Override
        public Tag getTag() {
            return UNEXPR;
        }

        @Override
        public void accept(Visitor v) {
            v.visitUnaryExpr(this);
        }
    }

    public static class ThisNode extends ExprNode {
        @Override
        public Tag getTag() {
            return THIS;
        }

        @Override
        public void accept(Visitor v) {
            v.visitThis(this);
        }
    }

    public static class NullNode extends ExprNode {
        @Override
        public Tag getTag() {
            return NULL;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNull(this);
        }
    }

    /**
     *
     */
    public static class FuncCallNode extends ExprNode {
        /*
         * Ex | invoked-on | args sum(5,x) | sum | (5,x); a[2](10) | a[5] | (10);
         */
        public ExprNode invokedOn;
        public ArgsListNode args;

        public FuncCallNode(ExprNode e, ArgsListNode p) {
            invokedOn = e;
            args = p;
        }

        @Override
        public Tag getTag() {
            return FUNCCALL;
        }

        @Override
        public void accept(Visitor v) {
            v.visitFuncCall(this);
        }
    }

    public static class SubscriptNode extends ExprNode {
        /*
         * Ex |subscript-on| subscript-index sum()[5] | sum() | [5]; a[2] | a | [2];
         */
        public ExprNode subscriptOf;
        public ExprNode index;

        public SubscriptNode(ExprNode s, ExprNode i) {
            subscriptOf = s;
            index = i;
        }

        @Override
        public Tag getTag() {
            return SUBSCRIPT;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSubscript(this);
        }
    }

    public static class ObjectAccessNode extends ExprNode {
        public ExprNode accessedOn;
        public ExprNode prop;

        public ObjectAccessNode(ExprNode e, ExprNode p) {
            accessedOn = e;
            prop = p;
        }

        @Override
        public Tag getTag() {
            return OBJACCESS;
        }

        @Override
        public void accept(Visitor v) {
            v.visitObjAccess(this);
        }
    }

    public static class SliceNode extends ExprNode {
        public ExprNode slicedOn;
        public ExprNode start;
        public ExprNode end;

        public SliceNode(ExprNode slicedOn, ExprNode start, ExprNode end) {
            this.slicedOn = slicedOn;
            this.start = start;
            this.end = end;
        }

        @Override
        public Tag getTag() {
            return SLICE;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSlice(this);
        }
    }

    public static class ArgsListNode extends Node {
        public List<ExprNode> args = new ArrayList<>();

        public ArgsListNode() {
        }

        public ArgsListNode(List<ExprNode> l) {
            args = l;
        }

        public void addArgs(ExprNode n) {
            args.add(n);
        }

        public void setArgs(List<ExprNode> l) {
            args = l;
        }

        @Override
        public Tag getTag() {
            return ARGSLIST;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArgsList(this);
        }
    }

    public static class VarDeclNode extends DeclNode {
        public IdenNode var;
        public ExprNode value;
        public VarDeclNode(IdenNode var) {
            this(var, null);
        }

        public VarDeclNode(IdenNode var, ExprNode value) {
            this.var = var;
            this.value = value;
        }

        @Override
        public Tag getTag() {
            return VARDECL;
        }

        @Override
        public void accept(Visitor v) {
            v.visitVarDecl(this);
        }
    }

    public static class ConstDeclNode extends DeclNode {
        public IdenNode var;
        public ExprNode val;

        public ConstDeclNode(IdenNode v, ExprNode e) {
            var = v;
            val = e;
        }

        @Override
        public Tag getTag() {
            return CONSTDECL;
        }

        @Override
        public void accept(Visitor v) {
            v.visitConstDecl(this);
        }
    }

    public static class FuncDeclNode extends DeclNode {
        public IdenNode name;
        public ParameterListNode params;
        public BlockNode block;

        public FuncDeclNode(IdenNode name, ParameterListNode params, BlockNode block) {
            this.name = name;
            this.params = params;
            this.block = block;
        }

        @Override
        public Tag getTag() {
            return FUNCDECL;
        }

        @Override
        public void accept(Visitor v) {
            v.visitFuncDecl(this);
        }
    }

    public static class ParameterListNode extends Node {
        public List<IdenNode> params = new ArrayList<>();
        boolean containsVarArg = false;

        public ParameterListNode() {
        }

        public ParameterListNode(List<IdenNode> l, boolean varArg) {
            params = l;
            containsVarArg = varArg;
        }

        public void addParam(IdenNode n) {
            params.add(n);
        }

        public void setArgs(List<IdenNode> l) {
            params = l;
        }

        @Override
        public Tag getTag() {
            return PARAMLIST;
        }

        @Override
        public void accept(Visitor v) {
            v.visitParamList(this);
        }
    }

    public static class ImportStmtNode extends StmtNode {
        public String path;

        public ImportStmtNode(String path) {
            this.path = path;
        }

        @Override
        public Tag getTag() {
            return IMPORT;
        }

        @Override
        public void accept(Visitor v) {
            v.visitImportStmt(this);
        }
    }

    public static class AsgnStmtNode extends StmtNode {
        public ExprNode lhs;
        public ExprNode rhs;

        public AsgnStmtNode(ExprNode lhs, ExprNode rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public Tag getTag() {
            return ASGN;
        }

        @Override
        public void accept(Visitor v) {
            v.visitAsgnStmt(this);
        }
    }

    public static class IfStmtNode extends StmtNode {
        public ExprNode ifCond;
        public BlockNode ifBlock;
        public StmtNode elsePart;

        public IfStmtNode(ExprNode ifCond, BlockNode ifBlock) {
            this(ifCond, ifBlock, null);
        }

        public IfStmtNode(ExprNode ifCond, BlockNode ifBlock, StmtNode elsePart) {
            this.ifCond = ifCond;
            this.ifBlock = ifBlock;
            this.elsePart = elsePart;
        }

        // can be another if block or else block;
        public void setElsePart(StmtNode n) {
            elsePart = n;
        }

        @Override
        public Tag getTag() {
            return IF;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIfStmt(this);
        }
    }

    public static class ElseStmtNode extends StmtNode {
        public StmtNode parentIf;
        public BlockNode elsePart;

        public ElseStmtNode(StmtNode parentIf, BlockNode elsePart) {
            this.parentIf = parentIf;
            this.elsePart = elsePart;
        }

        @Override
        public Tag getTag() {
            return ELSE;
        }

        @Override
        public void accept(Visitor v) {
            v.visitElseStmt(this);
        }
    }

    public static class ForStmtNode extends StmtNode {
        public List<StmtNode> init;
        public ExprNode cond;
        public List<AsgnStmtNode> counters;
        public BlockNode block;

        public ForStmtNode(ExprNode cond, BlockNode block) {
            this(null, cond, null, block);
        }

        public ForStmtNode(List<StmtNode> init, ExprNode cond, BlockNode block) {
            this(init, cond, null, block);
        }

        public ForStmtNode(ExprNode cond, List<AsgnStmtNode> counters, BlockNode block) {
            this(null, cond, counters, block);
        }

        public ForStmtNode(List<StmtNode> init, ExprNode cond, List<AsgnStmtNode> counters, BlockNode block) {
            this.init = init;
            this.cond = cond;
            this.counters = counters;
            this.block = block;
        }

        @Override
        public Tag getTag() {
            return FOR;
        }

        @Override
        public void accept(Visitor v) {
            v.visitForStmt(this);
        }
    }

    public static class LoopStmtNode extends StmtNode {
        public IdenNode var1;
        public IdenNode var2;
        public ExprNode collection;
        public BlockNode block;

        public LoopStmtNode(IdenNode var1, ExprNode collection, BlockNode block) {
            this(var1, null, collection, block);
        }

        public LoopStmtNode(IdenNode var1, IdenNode var2, ExprNode collection, BlockNode block) {
            this.var1 = var1;
            this.var2 = var2;
            this.collection = collection;
            this.block = block;
        }

        @Override
        public Tag getTag() {
            return LOOP;
        }

        @Override
        public void accept(Visitor v) {
            v.visitLoopStmt(this);
        }
    }

    public static class ReturnStmtNode extends StmtNode {
        public ExprNode expr;

        public ReturnStmtNode(ExprNode expr) {
            this.expr = expr;
        }

        @Override
        public Tag getTag() {
            return RETURN;
        }

        @Override
        public void accept(Visitor v) {
            v.visitReturnStmt(this);
        }
    }

    public static class ContinueStmtNode extends StmtNode {
        @Override
        public Tag getTag() {
            return CONTINUE;
        }

        @Override
        public void accept(Visitor v) {
            v.visitContinueStmt(this);
        }
    }

    public static class BreakStmtNode extends StmtNode {
        @Override
        public Tag getTag() {
            return BREAK;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBreakStmt(this);
        }
    }

    public static class BlockNode extends Node {
        public List<StmtNode> block = new ArrayList<>();

        public void addStmt(StmtNode n) {
            block.add(n);
        }

        public void setStmt(List<StmtNode> n) {
            block = n;
        }

        @Override
        public Tag getTag() {
            return BLOCK;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBlock(this);
        }
    }

    public interface Visitor {
        void visitProgram(ProgramNode n);

        void visitVarDecl(VarDeclNode n);

        void visitIden(IdenNode n);

        void visitConstDecl(ConstDeclNode n);

        void visitNumLit(NumLitNode n);

        void visitStrLit(StrLitNode n);

        void visitBoolLit(BoolLitNode n);

        void visitFuncDecl(FuncDeclNode n);

        void visitObjCreation(ObjCreationNode n);

        void visitBlock(BlockNode n);

        void visitAnnFunc(AnnFuncNode n);

        void visitListColl(ListCollNode n);

        void visitSetColl(SetCollNode n);

        void visitLinkColl(LinkCollNode n);

        void visitMapColl(MapCollNode n);

        void visitBinaryExpr(BinaryExprNode n);

        void visitUnaryExpr(UnaryExprNode n);

        void visitThis(ThisNode n);

        void visitNull(NullNode n);

        void visitFuncCall(FuncCallNode n);

        void visitSubscript(SubscriptNode n);

        void visitObjAccess(ObjectAccessNode n);

        void visitSlice(SliceNode n);

        void visitArgsList(ArgsListNode n);

        void visitParamList(ParameterListNode n);

        void visitImportStmt(ImportStmtNode n);

        void visitAsgnStmt(AsgnStmtNode n);

        void visitIfStmt(IfStmtNode n);

        void visitElseStmt(ElseStmtNode n);

        void visitForStmt(ForStmtNode n);

        void visitLoopStmt(LoopStmtNode n);

        void visitReturnStmt(ReturnStmtNode n);

        void visitContinueStmt(ContinueStmtNode n);

        void visitBreakStmt(BreakStmtNode n);

        void visitListComp(ListCompNode n);

        void visitLinkComp(LinkCompNode n);

        void visitSetComp(SetCompNode n);

        void visitMapComp(MapCompNode n);

        void visitCompLoop(CompLoopNode n);

        void visitCompIf(CompIfNode n);

        void visitListRange(RangeNode n);
    }
}
