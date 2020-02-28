package compiler.utils;

import java.util.regex.Pattern;

public class Constants {
	public static final String IDEN_REG = "(?:\\b[_a-zA-Z]|\\B\\$)[_$a-zA-Z0-9]*+";
	
	public static final Pattern IDEN_PATTERN = Pattern.compile(IDEN_REG);
}
