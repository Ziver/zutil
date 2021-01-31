/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Ziver Koc
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

package zutil.osal;

import java.io.File;


/**
 * A class for handling multiple binaries located in a folder separated by OS and platform.
 * <p>
 * Example folder structure:
 * <pre>
 * root/
 *   - linux-amd64/
 *   - linux-arm/
 *   - linux-amd64/
 *   - linux-powerpc/
 *   - linux-x86/
 *   - osx-amd64/
 *   - osx-ppc/
 *   - osx-x86/
 *   - win-amd64/
 *   - win-arm/
 *   - win-x86/
 * </pre>
 */
public class MultiPlatformBinaryManager {

    /**
     *
     * @param root      The root directory where all the binaries are located
     * @param binary    The name of the binary.
     * @return the path to the binary for the current hw platform
     */
    public static File getPath(File root, String binary) {
        String os;
        switch (OSAbstractionLayer.getInstance().getOSType()) {
            case Linux:   os = "linux"; break;
            case MacOS:   os = "osx"; break;
            case Windows: os = "win"; break;
            default:      os = "unknown"; break;
        }

        String arch;
        switch (System.getProperty("os.arch")) {
            case "aarch64": arch = "arm64"; break;
            case "PowerPC": arch = "ppc"; break;
            case "x86_32":  arch = "x86"; break;
            case "x86_64":  arch = "amd64"; break;

            // Source: http://lopica.sourceforge.net/os.html
            // arm
            // arm64
            // x86
            // i386
            // i686
            // amd64
            // ppc
            // ppc64
            // sparc
            default: arch = System.getProperty("os.arch").toLowerCase(); break;
        }

        String platform = os + "-" + arch;
        File platformPath = new File(root, platform);
        return new File(platformPath, binary);
    }
}
