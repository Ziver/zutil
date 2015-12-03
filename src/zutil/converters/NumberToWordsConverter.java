package zutil.converters;

import java.util.HashMap;

public class NumberToWordsConverter {
	
	private static final String ZERO_STRINGS = "zero";
	// Indexes to lookup
	private static final int[] NUMERIC_INDEXES = new int[]{
		9,/*1000 000 000*/
		6,/*1000 000*/
		3,/*1000*/
		2 /*100*/};
	private static final HashMap<Long, String> NUMERIC_STRINGS;
	static{
		NUMERIC_STRINGS = new HashMap<>();
		NUMERIC_STRINGS.put(1l, "one");
		NUMERIC_STRINGS.put(2l, "two");
		NUMERIC_STRINGS.put(3l, "three");
		NUMERIC_STRINGS.put(4l, "four");
		NUMERIC_STRINGS.put(5l, "five");
		NUMERIC_STRINGS.put(6l, "six");
		NUMERIC_STRINGS.put(7l, "seven");
		NUMERIC_STRINGS.put(8l, "eight");
		NUMERIC_STRINGS.put(9l, "nine");
		NUMERIC_STRINGS.put(10l, "ten");
		NUMERIC_STRINGS.put(11l, "eleven");
		NUMERIC_STRINGS.put(12l, "twelve");
		NUMERIC_STRINGS.put(13l, "thirteen");
		NUMERIC_STRINGS.put(14l, "fourteen");
		NUMERIC_STRINGS.put(15l, "fifteen");
		NUMERIC_STRINGS.put(16l, "sixteen");
		NUMERIC_STRINGS.put(17l, "seventeen");
		NUMERIC_STRINGS.put(18l, "eightteen");
		NUMERIC_STRINGS.put(19l, "nineteen");
		
		NUMERIC_STRINGS.put(20l, "twenty");
		NUMERIC_STRINGS.put(30l, "thirty");
		NUMERIC_STRINGS.put(40l, "forty");
		NUMERIC_STRINGS.put(50l, "fifty");
		NUMERIC_STRINGS.put(60l, "sixty");
		NUMERIC_STRINGS.put(70l, "seventy");
		NUMERIC_STRINGS.put(80l, "eighty");
		NUMERIC_STRINGS.put(90l, "ninety");
		
		NUMERIC_STRINGS.put(100l, "hundred");
		NUMERIC_STRINGS.put(1_000l, "thousand");
		NUMERIC_STRINGS.put(1000_000l, "million");
		NUMERIC_STRINGS.put(1000_000_000l, "billion");
	}
	
	
	/**
	 * Given an integer in the range -20 to 20 will return a String with
	 * that number converted to words. For example, an input of 15 results in 
	 * an output of "fifteen". An input of -4 returns "minus four".
	 * 
	 * @param	 num	a number to be converted to words.
	 * @return the number as words.
	 */
	public String convert(int num) {
		if (num == 0)
			return ZERO_STRINGS;
		
		long tmpNum = num;
		StringBuilder buffer = new StringBuilder();
		if (tmpNum < 0){ // Negative number
			tmpNum *= -1;
			buffer.append("minus ");
		}
		
		for(int i : NUMERIC_INDEXES){
			long pow = (int)Math.pow(10, i);
			if (tmpNum >= pow){
				long numberAtIndex = tmpNum/pow; // The number at position 3
				tmpNum -= numberAtIndex*pow;
				buffer.append( convert((int)numberAtIndex) ).append(" ");
				buffer.append( NUMERIC_STRINGS.get( pow ) ).append(" ");
			}
		}
		if (tmpNum >= 20){ // second number in the integer
			long numberAtIndex = ((tmpNum % 100)/10)*10; // The number at position 2
			tmpNum -= numberAtIndex;
			buffer.append( NUMERIC_STRINGS.get(numberAtIndex) ).append(" ");
		}
		if (NUMERIC_STRINGS.containsKey(tmpNum))
			buffer.append(NUMERIC_STRINGS.get(tmpNum));
		return buffer.toString().trim();
	}


}
