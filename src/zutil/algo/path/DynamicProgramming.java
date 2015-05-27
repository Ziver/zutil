/*
 * Copyright (c) 2015 ezivkoc
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
 */

package zutil.algo.path;

public class DynamicProgramming {
	public static char[][] words = new char[][]{
		"bibba".toCharArray(),
		"bitas".toCharArray(),
		"brott".toCharArray(),
		"bl�ja".toCharArray(),
		"boson".toCharArray()
	};

	public static void main(String[] args){
		new DynamicProgramming().search();
	}
	/*

int search(words[][][])
	matrix[][][] = 0
	shortest = -1
	
	for w=0->length(words)
		for y=0->length(words)
			for x=0->length(words)
				// f�rsta raden i matrisen
				if y == 0
					// finns f�rsta bokstaven i r�tt position i f�rsta ordet?
					if words[0][x] != words[w][0]
						matrix[w][y][x] = -1
					else
						matrix[w][y][x] = 0
				else
					// om f�reg�ende �r negativ s�tt nuvarande till negativ
					if matrix[w][y-1][x] < 0
						matrix[w][y-1][x] = -1
					// h�r s� h�nder det riktiga i algoritmen
					else
						tmp = minstaForskjutning(words[y], words[w][y], x)
						if tmp >= 0
							matrix[w][y][x] = matrix[w][y-1][x] + tmp
						else
							matrix[w][y][x] = -1
					// kolla om det �r sista raden i matrisen
					if y == length(matrix)
						if (tmp < shortest || shortest < 0) && tmp >= 0
							shortest = tmp;
							
	return shortest
	
int minstaForskjutning(word[], find, index){
	minsta = -1
	for i=0->length(word)
		if word[i] == cfind && (abs(index-i) < minsta || minsta < 0)
			minsta = abs(index-i)
			
	return minsta
	
	 */

	public int search(){
		int[][][] matrix = new int[words.length][words.length][words.length];
		int shortest = -1;

		for(int w=0; w<words.length ;w++){ //lodr�ta ordet
			System.out.print("\n\n"+new String(words[w])+"\n ");
			for(int y=0; y<words.length ;y++){ // v�gr�ta ordet
				System.out.print("\n"+ new String(words[y])+": ");
				for(int x=0; x<words.length ;x++){ // psition i y
					// f�rsta v�gr�ta ordet
					if(y == 0){
						if(words[0][x] != words[w][0]){
							matrix[w][y][x] = -1;
						}
						else{
							matrix[w][y][x] = 0;
						}
					}
					//resten av de v�gr�ta orden
					else{
						if(matrix[w][y-1][x] < 0){
							matrix[w][y][x] = -1;
						}
						else{
							int tmp = minstaForskjutning(words[y], words[w][y], x);
							if(tmp >= 0){
								matrix[w][y][x] = matrix[w][y-1][x] + tmp;
							}
							else{
								matrix[w][y][x] = -1;
							}
						}
					}
					if(y == words.length-1){
						int tmp = matrix[w][y][x];
						if((tmp<shortest || shortest<0) 
								&& tmp>= 0){
							shortest = tmp;
						}						
					}
					System.out.print(" "+matrix[w][y][x]);
				}	
			}	
		}

		System.out.println("\n\nKortaste f�rflyttningen: "+shortest);
		return shortest;
	}

	private int minstaForskjutning(char[] word, char cfind, int index){
		int minsta = -1;
		for(int i=0; i<word.length ;i++){
			if(word[i] == cfind && (Math.abs(index-i)<minsta || minsta<0)){
				minsta = Math.abs(index-i);
			}
		}
		return minsta;
	}
}
