package compiler.lexer.tokentypes;

public enum OpTokenType implements TokenType{
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
}
