package compiler.parser;

import compiler.lexer.tokentypes.TokenType;
import compiler.utils.Constants;

public final class Utility {
	protected final static boolean isSemi() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_SEMI)
			return true;
		return false;
	}

	protected final static boolean isLParen() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_LPAREN)
			return true;
		return false;
	}

	protected final static boolean isRParen() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_RPAREN)
			return true;
		return false;
	}

	protected final static boolean isLBrace() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_LBRACE)
			return true;
		return false;
	}

	protected final static boolean isRBrace() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_RBRACE)
			return true;
		return false;
	}

	protected final static boolean isComma() {
		if (Parser.currT.getTokenType() == TokenType.SepTokenType.SEP_COMMA)
			return true;
		return false;
	}
	
	protected final static boolean isOpLogNot() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_LOGNOT)
			return true;
		return false;
	}
	
	protected final static boolean isOpBitCompliment() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_BITCOMPLIMENT)
			return true;
		return false;
	}
	
	protected final static boolean isOpMultiply() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_MULTIPLY)
			return true;
		return false;
	}
	
	protected final static boolean isOpDivide() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_DIVIDE)
			return true;
		return false;
	}
	
	protected final static boolean isOpPlus() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_PLUS)
			return true;
		return false;
	}
	
	protected final static boolean isOpMinus() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_MINUS)
			return true;
		return false;
	}
	
	protected final static boolean isOpEq() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_ASG)
			return true;
		return false;
	}
	
	protected final static boolean isNumLiteral() {
		if (Parser.currT.getTokenType() == TokenType.KWTokenType.TK_NUMLITERAL)
			return true;
		return false;
	}
	
	protected final static boolean isIdentifier() {
		String iden = Parser.currT.getTokenValue();
		
		if (Parser.currT.getTokenType() == TokenType.KWTokenType.TK_IDENTIFIER) {
			if (Constants.IDEN_PATTERN.matcher(iden).matches()) {
				return true;
			}
		}
		return false;
	}

	protected final static boolean isOpRelLT() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELLT)
			return true;
		return false;
	}
	
	protected final static boolean isOpRelGT() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELGT)
			return true;
		return false;
	}
	
	protected final static boolean isOpRelLE() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELLE)
			return true;
		return false;
	}
	
	protected final static boolean isOpRelGE() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELGE)
			return true;
		return false;
	}
	
	protected final static boolean isOpRelEE() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELEE)
			return true;
		return false;
	}
	
	protected final static boolean isOpRelNE() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_RELNE)
			return true;
		return false;
	}
	
	protected final static boolean isOpLogAnd() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_LOGAND)
			return true;
		return false;
	}
	
	protected final static boolean isOpLogOr() {
		if (Parser.currT.getTokenType() == TokenType.OpTokenType.OP_LOGOR)
			return true;
		return false;
	}
	
	protected final static boolean isFunc() {
		if (Parser.currT.getTokenType() == TokenType.KWTokenType.KW_FUNC)
			return true;
		return false;
	}
	
	protected final static boolean isReturnStmt() {
		if (Parser.currT.getTokenType() == TokenType.KWTokenType.KW_RETURN)
			return true;
		return false;
	}
}
