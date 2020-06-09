package compiler.parser;

import java.util.List;

import compiler.parser.Tokens.NamedToken;
import compiler.parser.Tokens.NumericToken;
import compiler.parser.Tokens.StringToken;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
import compiler.util.LogType.Errors;
import compiler.util.Position;

public class Tokenizer {

	private Log log;
	private Tokens tokens;
	private CharReader reader;

	private TokenKind tokenKind;
	
	// Temporary variable to hold value for literals and identifiers. To be changed.
	private String litValue;
	
	private int lineNum = 1;
	private int linePos = 1;
	
	protected Tokenizer(ScannerFactory scannerFactory, List<Character> buff) {
		this(scannerFactory, new CharReader(buff));
	}

	protected Tokenizer(ScannerFactory scannerFactory, CharReader reader) {
		log = scannerFactory.log;
		tokens = scannerFactory.tokens;
		this.reader = reader;
	}

	void scanIden() {
		reader.putChar(reader.ch);
		reader.scanChar();
		boolean isIden = true;
		while(true) {
			switch (reader.ch) {
		     case 'A': case 'B': case 'C': case 'D': case 'E':
		     case 'F': case 'G': case 'H': case 'I': case 'J':
		     case 'K': case 'L': case 'M': case 'N': case 'O':
		     case 'P': case 'Q': case 'R': case 'S': case 'T':
		     case 'U': case 'V': case 'W': case 'X': case 'Y':
		     case 'Z':
		     case 'a': case 'b': case 'c': case 'd': case 'e':
		     case 'f': case 'g': case 'h': case 'i': case 'j':
		     case 'k': case 'l': case 'm': case 'n': case 'o':
		     case 'p': case 'q': case 'r': case 's': case 't':
		     case 'u': case 'v': case 'w': case 'x': case 'y':
		     case 'z':
		     case '$': case '_':
		     case '0': case '1': case '2': case '3': case '4':
		     case '5': case '6': case '7': case '8': case '9':
		    	 reader.putChar(reader.ch);
		    	 reader.scanChar();
		    	 continue;
		    default:
		    	isIden = false;
			}
			
			if(!isIden) {
				litValue = reader.getSavedBufferAsString(true);
				tokenKind = tokens.lookupKind(litValue);
				return;
			}
		}
	}
	
	void scanFractional() {
		boolean isFractional = true;
		
		while(true) {
			reader.scanChar();
			switch(reader.ch) {
			case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            	reader.putChar(reader.ch);
            	continue;
            default:
            	isFractional = false;
			}
			
			if(!isFractional) {
				return;
			}
		}
	}
	
	void scanNum() {
		reader.putChar(reader.ch);
		boolean isNum = true;
		
		while(true) {
			reader.scanChar();
			switch(reader.ch) {
			case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            	reader.putChar(reader.ch);
            	continue;
            case '.':
            	reader.putChar(reader.ch);
            	scanFractional();
            	isNum = false;
            	break;
            default:
            	isNum = false;
			}
			
			if(!isNum) {
				litValue = reader.getSavedBufferAsString(true);
				tokenKind = TokenKind.NUMLIT;
			}
		}
	}
	
	Token readToken() {
		int posBp = reader.bp;
		int pos=1;
	LOOP: while(true) {	
			switch(reader.ch) {
			// Ignore white spaces
			case ' ':
			case '\t':
				do {
					pos++;
					linePos++;
					reader.scanChar();
				}while(reader.ch == ' ' || reader.ch == '\t');
				break;
			case '\r':
			case '\n':
				do {
					lineNum++;
					linePos = 1;
					reader.scanChar();
				}while(reader.ch == '\r' || reader.ch == '\n');
				break;
			case 'A': case 'B': case 'C': case 'D': case 'E':
	        case 'F': case 'G': case 'H': case 'I': case 'J':
	        case 'K': case 'L': case 'M': case 'N': case 'O':
	        case 'P': case 'Q': case 'R': case 'S': case 'T':
	        case 'U': case 'V': case 'W': case 'X': case 'Y':
	        case 'Z':
	        case 'a': case 'b': case 'c': case 'd': case 'e':
	        case 'f': case 'g': case 'h': case 'i': case 'j':
	        case 'k': case 'l': case 'm': case 'n': case 'o':
	        case 'p': case 'q': case 'r': case 's': case 't':
	        case 'u': case 'v': case 'w': case 'x': case 'y':
	        case 'z':
	        case '$': case '_':
	        	pos = linePos;
	        	scanIden();
				break LOOP;
	        case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            	pos = linePos;
            	scanNum();
            	break LOOP;
            case '.':
            	reader.scanChar(); tokenKind = TokenKind.DOT; break LOOP;
            case ',':
                reader.scanChar(); tokenKind = TokenKind.COMMA; break LOOP;
            case ':':
            	reader.scanChar(); tokenKind=TokenKind.COLON;    break LOOP;
            case ';':
                reader.scanChar(); tokenKind = TokenKind.SEMICOLON; break LOOP;
            case '(':
                reader.scanChar(); tokenKind = TokenKind.LPAREN; break LOOP;
            case ')':
                reader.scanChar(); tokenKind = TokenKind.RPAREN; break LOOP;
            case '[':
                reader.scanChar(); tokenKind = TokenKind.LSQU; break LOOP;
            case ']':
                reader.scanChar(); tokenKind = TokenKind.RSQU; break LOOP;
            case '{':
                reader.scanChar(); tokenKind = TokenKind.LCURLY; break LOOP;
            case '}':
                reader.scanChar(); tokenKind = TokenKind.RCURLY; break LOOP;
            case '>':
            	//>=
            	//>>
            	//>>>
            	reader.scanChar();
            	switch(reader.ch) {
	            	case '=':
	            		reader.scanChar(); tokenKind = TokenKind.GTE; break LOOP;
	            	case '>':
	            		reader.scanChar();
	            		if(reader.ch == '>') {
	            			reader.scanChar(); tokenKind = TokenKind.TRSHIFT; break LOOP;
	            		}
	            		else {
	            			tokenKind = TokenKind.RSHIFT; break LOOP;
	            		}
	            	default:
	            		tokenKind = TokenKind.GT; break LOOP;
            	}
            case '<':
            	//<=
            	//<<
            	reader.scanChar();
            	switch(reader.ch) {
	            	case '=':
	            		reader.scanChar(); tokenKind = TokenKind.LTE; break LOOP;
	            	case '<':
	            		reader.scanChar();tokenKind = TokenKind.LSHIFT; break LOOP;
	            	default:
	            		tokenKind = TokenKind.LT; break LOOP;
            	}
            	          	
            case '!':
            	//!=
            	reader.scanChar();
            	if(reader.ch == '=') {
            		reader.scanChar();tokenKind =TokenKind.NOTEQU;break LOOP;
            	}
            	else {
            		tokenKind =TokenKind.NOTOP;break LOOP;
            	}
            case '=':
            	//==
            	reader.scanChar();
            	if(reader.ch == '=') {
            		reader.scanChar();tokenKind =TokenKind.EQUEQU;break LOOP;
            	}
            	else {
            		tokenKind =TokenKind.EQU;break LOOP;
            	}
            case '&':
            	reader.scanChar();
            	if(reader.ch == '&') {
            		reader.scanChar();tokenKind=TokenKind.ANDOP;break LOOP;
            	}
            	else {
            		tokenKind=TokenKind.BITAND;break LOOP;
            	}
            case '|':
            	reader.scanChar();
            	if(reader.ch == '|') {
            		reader.scanChar();tokenKind=TokenKind.OROP;break LOOP;
            	}
            	else {
            		tokenKind=TokenKind.BITOR;break LOOP;
            	}
            case '^':
            	reader.scanChar();tokenKind=TokenKind.BITAND;break LOOP;
            case '+':
            	reader.scanChar();tokenKind=TokenKind.ADD;break LOOP;
            case '-':
            	reader.scanChar();tokenKind=TokenKind.SUB;break LOOP;
            case '*':
            	// **
            	// */
            	reader.scanChar();
            	if(reader.ch == '*') {
            		reader.scanChar();tokenKind=TokenKind.POWER;break LOOP; 
            	}
            	else if(reader.ch == '/') {
            		reader.scanChar();tokenKind=TokenKind.RCOMMENT;break LOOP;
            	}
            	else {
            		tokenKind=TokenKind.MUL;break LOOP;
            	}
            case '/' :
             // "//"
             // /*	
            	reader.scanChar();
            	if(reader.ch == '/') {
            		reader.scanChar();tokenKind=TokenKind.FLOORDIV;break LOOP;
            	}
            	else if(reader.ch == '*') {
            		reader.scanChar();tokenKind=TokenKind.LCOMMENT;break LOOP;
            	}
            	else {
            		tokenKind=TokenKind.DIV;break LOOP;	
            	}
            case '~':
            	reader.scanChar();tokenKind=TokenKind.TILDE;break LOOP;
            case '#':
            	reader.scanChar();tokenKind=TokenKind.SINGLECOMMENT;break LOOP;
            case '@':
            	reader.scanChar();tokenKind=TokenKind.IMPORT;break LOOP;
            case '%':
            	reader.scanChar();tokenKind=TokenKind.MOD;break LOOP;
            case '\'':
            	reader.scanChar();tokenKind=TokenKind.SQOUTE;break LOOP;
            case '"':
            	reader.scanChar();tokenKind=TokenKind.DQOUTE;break LOOP;
			default:
				log.error(pos, Errors.ILLEGAL_CHARACTER);
			}	
		}
		int endPosBp = reader.bp;
		linePos = pos + (posBp - endPosBp);
		Position p = new Position(lineNum, pos, linePos);
		
		switch(tokenKind.tag) {
			case DEFAULT: return new Token(tokenKind, p, null);
			case NAMED: return new NamedToken(tokenKind,litValue,p,null);
			case STRING: return new StringToken(tokenKind,litValue,p,null);
			case NUMERIC: return new NumericToken(tokenKind,litValue,p,null);
			default:
				throw new AssertionError();
		}
	}
}
