/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.parser;


public class Base64Encoder {

    public static String encode( byte[] data ){
        return write( data );
    }
    public static String encode( String data ){
        return write( data.getBytes() );
    }

    private static String write( byte[] data ){
        char[] buffer = new char[getBufferLength(data.length)];
        int buffIndex = 0;

        int rest = 0; // how much rest we have
        for( int i=0; i<data.length; i++){
            byte b = 0;
            switch(rest){
                case 0:
                    b  = (byte)((data[i] >> 2)    & 0b0011_1111);
                    break;
                case 2:
                    b  = (byte) ((data[i-1] << 4) & 0b0011_0000);
                    b |= (byte) ((data[i]   >> 4) & 0b0000_1111);
                    break;
                case 4:
                    b  = (byte) ((data[i-1] << 2) & 0b0011_1100);
                    b |= (byte) ((data[i]   >> 6) & 0b0000_0011);
                    break;
                case 6:
                    --i; // Go back one element
                    b  = (byte) (data[i]          & 0b0011_1111);
                    break;
            }

            rest = (rest + 2) % 8;
            buffer[buffIndex++] = getChar(b);
        }
        // Any rest left?
        if(rest == 2)
            buffer[buffIndex++] = getChar((byte) ((data[data.length-1] << 4) & 0b0011_0000));
        else if(rest == 4)
            buffer[buffIndex++] = getChar((byte) ((data[data.length-1] << 2) & 0b0011_1100));
        else if(rest == 6)
            buffer[buffIndex++] = getChar((byte) (data[data.length-1]        & 0b0011_1111));

        // Add padding
        for(; buffIndex<buffer.length; ++buffIndex)
            buffer[buffIndex] = '=';

        return new String(buffer);
    }

    private static int getBufferLength(int length) {
        int buffLength = (int) Math.ceil(length*8/6.0);
        // Padding
        if(buffLength%4 != 0)
            buffLength += 4 - buffLength%4;
        return buffLength;
    }


    @SuppressWarnings("PointlessBitwiseExpression")
    private static char getChar(byte b ){
        switch(b){
            case (byte)( 0 & 0xff): return 'A';
            case (byte)( 1 & 0xff): return 'B';
            case (byte)( 2 & 0xff): return 'C';
            case (byte)( 3 & 0xff): return 'D';
            case (byte)( 4 & 0xff): return 'E';
            case (byte)( 5 & 0xff): return 'F';
            case (byte)( 6 & 0xff): return 'G';
            case (byte)( 7 & 0xff): return 'H';
            case (byte)( 8 & 0xff): return 'I';
            case (byte)( 9 & 0xff): return 'J';
            case (byte)(10 & 0xff): return 'K';
            case (byte)(11 & 0xff): return 'L';
            case (byte)(12 & 0xff): return 'M';
            case (byte)(13 & 0xff): return 'N';
            case (byte)(14 & 0xff): return 'O';
            case (byte)(15 & 0xff): return 'P';
            case (byte)(16 & 0xff): return 'Q';
            case (byte)(17 & 0xff): return 'R';
            case (byte)(18 & 0xff): return 'S';
            case (byte)(19 & 0xff): return 'T';
            case (byte)(20 & 0xff): return 'U';
            case (byte)(21 & 0xff): return 'V';
            case (byte)(22 & 0xff): return 'W';
            case (byte)(23 & 0xff): return 'X';
            case (byte)(24 & 0xff): return 'Y';
            case (byte)(25 & 0xff): return 'Z';

            case (byte)(26 & 0xff): return 'a';
            case (byte)(27 & 0xff): return 'b';
            case (byte)(28 & 0xff): return 'c';
            case (byte)(29 & 0xff): return 'd';
            case (byte)(30 & 0xff): return 'e';
            case (byte)(31 & 0xff): return 'f';
            case (byte)(32 & 0xff): return 'g';
            case (byte)(33 & 0xff): return 'h';
            case (byte)(34 & 0xff): return 'i';
            case (byte)(35 & 0xff): return 'j';
            case (byte)(36 & 0xff): return 'k';
            case (byte)(37 & 0xff): return 'l';
            case (byte)(38 & 0xff): return 'm';
            case (byte)(39 & 0xff): return 'n';
            case (byte)(40 & 0xff): return 'o';
            case (byte)(41 & 0xff): return 'p';
            case (byte)(42 & 0xff): return 'q';
            case (byte)(43 & 0xff): return 'r';
            case (byte)(44 & 0xff): return 's';
            case (byte)(45 & 0xff): return 't';
            case (byte)(46 & 0xff): return 'u';
            case (byte)(47 & 0xff): return 'v';
            case (byte)(48 & 0xff): return 'w';
            case (byte)(49 & 0xff): return 'x';
            case (byte)(50 & 0xff): return 'y';
            case (byte)(51 & 0xff): return 'z';

            case (byte)(52 & 0xff): return '0';
            case (byte)(53 & 0xff): return '1';
            case (byte)(54 & 0xff): return '2';
            case (byte)(55 & 0xff): return '3';
            case (byte)(56 & 0xff): return '4';
            case (byte)(57 & 0xff): return '5';
            case (byte)(58 & 0xff): return '6';
            case (byte)(59 & 0xff): return '7';
            case (byte)(60 & 0xff): return '8';
            case (byte)(61 & 0xff): return '9';
            case (byte)(62 & 0xff): return '+';
            case (byte)(63 & 0xff): return '/';
        }
        return 0;
    }
}
