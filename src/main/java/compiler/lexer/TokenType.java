package compiler.lexer;

/**
 * A list of all Tokens specified in Language Specification.
 * @author Dhyey 
 */
public enum TokenType {
	
	/**
	 * "{"
	 * @category Seperators
	 */
	SEP_LBRACE,
	
	/** "}" */
	SEP_RBRACE,
	
	/** "(" */
	SEP_LPAREN,
	
	/** ")" */
	SEP_RPAREN,
	
	/** ";" */
	SEP_SEMI,
	
	/** "+" */
	OP_PLUS,
	
	/** "-" */
	 OP_MINUS,
	
	/** "/" */
	 OP_DIVIDE,
	
	/** "*" */
	 OP_MULTIPLY ,
	 
	 /** "=" */
	 OP_ASG ,
	
//	/** "+=" */
//	 OP_PLUS_ASG ,
//	
//	/** "-=" */
//	 OP_MINUS_ASG ,
//	
//	/** "/=" */
//	 OP_DIV_ASG ,
//	
//	/** "*=" */
//	 OP_MUL_ASG ,
	
	/** "++" */
	 OP_INC ,
	
	/** "--" */
	 OP_DEC ,
	
	
	/** 
	 * "&&"
	 * @category Logical Operators 
	 */
	 OP_LOGAND ,
	
	/** 
	 * "||"
	 * @category Logical Operators 
	 */
	 OP_LOGOR ,
	
	/** 
	 * "!"
	 * @category Logical Operators 
	 */
	 OP_LOGNOT ,
	
	/** 
	 * "&"
	 * @category Bitwise Operators 
	 */
	 OP_BITAND ,
	
	/** 
	 * "|"
	 * @category Bitwise Operators 
	 */
	 OP_BITOR ,
	
	/** 
	 * "^"
	 * @category Bitwise Operators 
	 */
	 OP_BITXOR,
	
	/** 
	 * "~"
	 * @category Bitwise Operators 
	 */
	 OP_BITCOMPLIMENT,
	
	/** 
	 * "<<"
	 * @category Bitwise Operators 
	 */
	 OP_BITLEFTSHIFT,
	
	/** 
	 * ">>"
	 * @category Bitwise Operators 
	 */
	 OP_BITRIGHTSHIFT,
	
	/** 
	 * "<"
	 * @category Relational Operators 
	 */
	 OP_RELLT,
	 OP_RELGT,
	 OP_RELLE,
	 OP_RELGE,
	 OP_RELEE,
			
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
