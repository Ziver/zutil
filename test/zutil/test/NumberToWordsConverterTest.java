package zutil.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import zutil.converters.NumberToWordsConverter;


@RunWith(Parameterized.class)
public class NumberToWordsConverterTest {
		
	@Parameters
	public static Collection<Object[]> parameters() {		
		Object[][] data = new Object[][] {
			{-77,  "minus seventy seven"}
			, {-2, "minus two"}
			, {1,  "one"}
			, {0,  "zero"}
			, {7,  "seven"}
			, {11, "eleven"}
			, {12, "twelve"}
			, {17, "seventeen"}
			, {20, "twenty"}
			, {21, "twenty one"}
			, {23, "twenty three"}
			, {25, "twenty five"}
			, {30, "thirty"}
			, {34, "thirty four"}
			, {50, "fifty"}
			, {70, "seventy"}
			, {100, "one hundred"}
			, {110, "one hundred ten"}
			, {131, "one hundred thirty one"}
			, {222, "two hundred twenty two"}
			, {1000, "one thousand"}
			, {10_000, "ten thousand"}
			, {100_000, "one hundred thousand"}
			, {1000_000, "one million"}
			, {10_000_000, "ten million"}
			, {Integer.MAX_VALUE, "two billion one hundred forty seven million four hundred eighty three thousand six hundred forty seven"}
			, {Integer.MIN_VALUE, "minus two billion one hundred forty seven million four hundred eighty three thousand six hundred forty eight"}
			};
		return Arrays.asList(data);
	}
	
	
	private int input;
	private String expected;
	

	public NumberToWordsConverterTest(int input, String expected) {
		this.input = input;
		this.expected = expected;
	}

	@Test
	public void testConvert() {
		assertThat(new NumberToWordsConverter().convert(input)
													, is(equalTo(expected)));
	}

}
	
	
