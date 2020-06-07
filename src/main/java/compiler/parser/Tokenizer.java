package compiler.parser;

import java.util.List;

import compiler.parser.Tokens.NamedToken;
import compiler.parser.Tokens.NumericToken;
import compiler.parser.Tokens.StringToken;
import compiler.parser.Tokens.Token;
import compiler.parser.Tokens.TokenKind;
import compiler.util.Log;
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
		boolean isIden = true;
		while(true) {
			reader.scanChar();
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
	
	void scanNum() {
		reader.putChar(reader.ch);
		boolean isNum = true;
		
		while(true) {
			reader.scanChar();
			switch(reader.ch) {
			case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9': case '.':
            	reader.putChar(reader.ch);
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
		int pos;
	LOOP: while(true) {	
			switch(reader.ch) {
			// Ignore white spaces
			case ' ':
			case '\t':
				do {
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
            	
            case ',':
                reader.scanChar(); tk = TokenKind.COMMA; break loop;
            case ':':
            	reader.scanChar(); tk=TokenKind.COLON;    break loop;
            case ';':
                reader.scanChar(); tk = TokenKind.SEMICOLON; break loop;
            case '(':
                reader.scanChar(); tk = TokenKind.LPAREN; break loop;
            case ')':
                reader.scanChar(); tk = TokenKind.RPAREN; break loop;
            case '[':
                reader.scanChar(); tk = TokenKind.LSQU; break loop;
            case ']':
                reader.scanChar(); tk = TokenKind.RSQU; break loop;
            case '{':
                reader.scanChar(); tk = TokenKind.LCURLY; break loop;
            case '}':
                reader.scanChar(); tk = TokenKind.RCURLY; break loop;
            case '>':
            	//>=
            	//>>
            	//>>>
            	
            case '<':
            	//<=
            	//<<
            	          	
            //case '!=':
            	reader.scanChar();tk=TokenKind.NOTEQU;break loop;

            case '=':
            	//==
            	
            case '|':
            	reader.scanChar();tk=TokenKind.BITOR;break loop;
            case '^':
            	reader.scanChar();tk=TokenKind.BITAND;break loop;

            case '+':
            	reader.scanChar();tk=TokenKind.ADD;break loop;
            case '-':
            	reader.scanChar();tk=TokenKind.SUB;break loop;
            case '*':
            	// **
            	reader.scanChar();tk=TokenKind.MUL;break loop;
            case '/' :
            	// "//"
            	reader.scanChar();tk=TokenKind.DIV;break loop;
            
            	reader.scanChar();tk=TokenKind.FLOORDIV;break loop;
            case '%':
            	reader.scanChar();tk=TokenKind.MOD;break loop;

                
          
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
