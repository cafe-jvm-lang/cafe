package compiler.codegen;

import java.io.FileOutputStream;

public class Main {
public static void main(String[] args) {
	try {
	FileOutputStream fileOpStream = new FileOutputStream(args[0]);
	
	ClassWriter cw = new ClassWriter();
	cw.visit(54, "Test");
	fileOpStream.write(cw.toByteArray());
	fileOpStream.close();
	}
	catch (Exception e) {
		e.printStackTrace();
	}
}
}
