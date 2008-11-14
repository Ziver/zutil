package zutil.algo.path;

public class DynamicProgramming {
	public static char[][] words = new char[][]{
		"bibba".toCharArray(),
		"bitas".toCharArray(),
		"brott".toCharArray(),
		"blöja".toCharArray(),
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
				// första raden i matrisen
				if y == 0
					// finns första bokstaven i rätt position i första ordet?
					if words[0][x] != words[w][0]
						matrix[w][y][x] = -1
					else
						matrix[w][y][x] = 0
				else
					// om föregående är negativ sätt nuvarande till negativ
					if matrix[w][y-1][x] < 0
						matrix[w][y-1][x] = -1
					// här så händer det riktiga i algoritmen
					else
						tmp = minstaForskjutning(words[y], words[w][y], x)
						if tmp >= 0
							matrix[w][y][x] = matrix[w][y-1][x] + tmp
						else
							matrix[w][y][x] = -1
					// kolla om det är sista raden i matrisen
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

		for(int w=0; w<words.length ;w++){ //lodräta ordet
			System.out.print("\n\n"+new String(words[w])+"\n ");
			for(int y=0; y<words.length ;y++){ // vågräta ordet
				System.out.print("\n"+ new String(words[y])+": ");
				for(int x=0; x<words.length ;x++){ // psition i y
					// första vågräta ordet
					if(y == 0){
						if(words[0][x] != words[w][0]){
							matrix[w][y][x] = -1;
						}
						else{
							matrix[w][y][x] = 0;
						}
					}
					//resten av de vågräta orden
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

		System.out.println("\n\nKortaste förflyttningen: "+shortest);
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
