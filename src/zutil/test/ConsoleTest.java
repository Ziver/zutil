package zutil.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import zutil.ui.Console;

public class ConsoleTest {
	public static void main(String[] args) throws IOException{
		new Console("Console Test");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while(true){
			System.out.println("hello= "+in.readLine());
			for(int i=0; i<20 ;i++){
				System.out.println(i+"Hello World!!!sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
				System.err.println(i+"Hello World!!!sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}
}
