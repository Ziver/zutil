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

package zutil.math;

public class Tick {

    /**
     * Ticks a given string(increments the string with one)
     *
     * @param   ts      is the string to tick
     * @param   maxChar is the maximum number of characters in the string
     * @return the ticked string
     */
    public static String tick(String ts, int maxChar){
        StringBuffer ret = new StringBuffer(ts.trim());
        int index = ret.length()-1;

        if(ret.length() < maxChar){
            ret.append('a');
        }
        else{
            while(index >= 0){
                char c = increment(ret.charAt(index));
                if(c != 0){
                    if(index == 0 && ret.length() < maxChar) ret.append('a');
                    if(index == 0) ret = new StringBuffer(""+c);
                    else ret.setCharAt(index,c);
                    break;
                }
                else{
                    //ret.setCharAt(index,'a');
                    ret.deleteCharAt(index);
                    index--;
                }
            }
        }

        return ret.toString();
    }

    /**
     * Increments the char with one after the swedish alfabet
     *
     * @param   c   is the char to increment
     * @return the incremented char in lowercase 0 if it reached the end
     */
    public static char increment(char c){
        switch(Character.toLowerCase(c)){
        case 'z':       return (char)134;
        case (char)134: return (char)132;
        case (char)132: return (char)148;
        }
        c = (char)(Character.toLowerCase(c) + 1);
        if(isAlphabetic(c)){
            return c;
        }
        return 0;
    }

    /**
     * Checks if the char is a valid character in
     * the Swedish alphabet
     *
     * @param   c   is the char to check
     * @return true if the char is a valid letter
     */
    public static boolean isAlphabetic(char c){
        switch(Character.toLowerCase(c)){
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
        case (char)134:
        case (char)132:
        case (char)148:
            return true;
        default:
            return false;
        }
    }
}
