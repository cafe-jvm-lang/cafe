package compiler.lexer.tokentypes;

/**
 * A list of all Tokens specified in Language Specification.
 * @author Dhyey 
 */
public enum KWTokenType implements TokenType{			
	 KW_VAR,
	 KW_NULL,
	 KW_FUNC, 
	 KW_IF,
	 KW_ELSE, 
	 KW_AND,
	 KW_OR, 
	 KW_NOT, 
	 KW_XOR, 
	 KW_RETURN, 
	 KW_LOOP,
	 KW_BREAK, 
	 KW_CONTINUE, 
	
	 TK_IDENTIFIER,
	 TK_NUMLITERAL; 
}
