/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package zutil.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class parses a string with math
 * and solves it
 * 
 * @author Ziver
 */
public class MathParser {

	public static MathNode parse(String functionString){
		StringBuffer functionStringBuffer = new StringBuffer(functionString+(char)0);
		MathNode node = new MathNode();

		parse(functionStringBuffer, new StringBuffer(), null, node);

		System.out.println("----------------------------------------------------------------------");
		System.out.println(node+" = "+node.exec());
		System.out.println("----------------------------------------------------------------------");
		return node;
	}

	private static void parse(StringBuffer functionString, StringBuffer temp, MathOperation previus, MathNode rootNode){
		if(functionString.length() <= 0){
			return;
		}
		char c = functionString.charAt(0);
		functionString.deleteCharAt(0);
		System.out.println("char: "+c);
		MathOperation current = null;

		if(!Character.isWhitespace(c)){
			if(isNumber(c)){
				temp.append(c);
			}
			else{
				Math container = new MathNumber();
				if(temp.length() > 0){
					System.out.println("("+Double.parseDouble(temp.toString())+")");
					((MathNumber)container).num = Double.parseDouble(temp.toString());
					temp.delete(0, temp.length());
				}

				if(rootNode.math == null){
					System.out.println("Initializing rootNode");
					previus = getOperation(c);
					System.out.println("operation: "+previus.getClass().getName());
					previus.math1 = container;
					rootNode.math = previus;
				}
				else{
					if(c == '('){
						MathNode parantes = new MathNode();
						MathOperation previusParantes = previus;
						parse(functionString, temp, previus, parantes);
						previusParantes.math2 = parantes;
						System.out.println(parantes);
						container = parantes;
						
						// get the next operation
						c = functionString.charAt(0);
						functionString.deleteCharAt(0);
						System.out.println("char: "+c);
					}
					
						current = getOperation(c);
						System.out.println("operation: "+current.getClass().getName());
						current.math1 = container;
						previus.math2 = current;
					
					if(c == ')'){
						return;	
					}
				}
			}
		}

		if(current != null) parse(functionString, temp, current, rootNode);
		else parse(functionString, temp, previus, rootNode);
		return;
	}

	private static boolean isNumber(char c){
		if(Character.isDigit(c)){
			return true;
		}
		return false;
	}

	private static MathOperation getOperation(char c){
		switch(c){
		case '+': return new MathAddition();
		case '-': return new MathSubtraction();
		case '*': return new MathMultiplication();
		case '/': return new MathDivision();
		case '%': return new MathModulus();
		case '^': return new MathPow();
		case ')':
		case (char)0: return new EmptyMath();
		default: return null;
		}
	}

	public static void main(String[] args){
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				System.out.print(">>Math: ");
				parse(in.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

abstract class Math{
	public abstract double exec();

	public abstract String toString();
}

class MathNode extends Math{
	Math math;

	public double exec() {
		return math.exec();
	}

	public String toString() {
		return "( "+math.toString()+" )";
	}
}

class MathNumber extends Math{
	double num;

	public double exec() {
		return num;
	}

	public String toString() {
		return ""+num;
	}
}

abstract class MathOperation extends Math{
	Math math1;
	Math math2;

	public abstract double exec();
}

class MathAddition extends MathOperation{
	public double exec() {
		return math1.exec() + math2.exec();
	}

	public String toString() {
		return math1.toString()+" + "+math2.toString();
	}
}

class MathSubtraction extends MathOperation{
	public double exec() {
		return math1.exec() - math2.exec();
	}

	public String toString() {
		return math1.toString()+" - "+math2.toString();
	}
}

class MathMultiplication extends MathOperation{
	public double exec() {
		return math1.exec() * math2.exec();
	}

	public String toString() {
		return math1.toString()+" * "+math2.toString();
	}
}

class MathDivision extends MathOperation{
	public double exec() {
		return math1.exec() / math2.exec();
	}

	public String toString() {
		return math1.toString()+" / "+math2.toString();
	}
}

class MathModulus extends MathOperation{
	public double exec() {
		return math1.exec() % math2.exec();
	}

	public String toString() {
		return math1.toString()+" % "+math2.toString();
	}
}

class MathPow extends MathOperation{
	public double exec() {
		double ret = 1;
		double tmp1 = math1.exec();
		double tmp2 = math2.exec();
		for(int i=0; i<tmp2 ;i++){
			ret *= tmp1;
		}
		return ret;
	}

	public String toString() {
		return math1.toString()+"^"+math2.toString();
	}
}

class EmptyMath extends MathOperation{
	public double exec() {
		return math1.exec();
	}

	public String toString() {
		return math1.toString();
	}
}
