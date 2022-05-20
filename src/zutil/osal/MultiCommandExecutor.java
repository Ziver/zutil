/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

import zutil.parser.Base64Encoder;

import java.io.*;
import java.security.SecureRandom;

/**
 * This class starts a platform specific shell and runs all commands
 * in a single process thereby lowering the execution time.
 *
 * <br><br>
 */
public class MultiCommandExecutor implements AutoCloseable{
    private static final String SHELL_WINDOWS = "cmd.exe";
    private static final String SHELL_LINUX   = "/bin/bash";

    private String delimiter;
    private Process process;
    private BufferedWriter stdin;
    private BufferedReader stdout;
    private boolean eol = true;


    public MultiCommandExecutor() {
        try {
            // Generate delimiter
            byte[] tmp = new byte[16];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(tmp);
            delimiter = Base64Encoder.encode(tmp);

            //init shell
            String shellCmd;
            switch (OSAbstractionLayer.getInstance().getOSType()) {
                case Windows:
                    shellCmd = SHELL_WINDOWS; break;
                case Linux:
                case MacOS:
                    shellCmd = SHELL_LINUX; break;
                default:
                    throw new RuntimeException("Unsupported OS");
            }
            process = new ProcessBuilder(shellCmd).start();

            //get stdin of shell
            stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to initiate shell", e);
        }
    }

    /**
     * Method will execute a given command. Note that any previous output of a command will be flushed.
     *
     * @param cmd       Is a String containing the command to execute.
     */
    public synchronized void exec(String cmd) throws IOException {
        clear();
        eol = false;

        stdin.write(cmd);
        switch (OSAbstractionLayer.getInstance().getOSType()) {
            case Windows:
                stdin.write(" & echo & echo " + delimiter); break;
            case Linux:
            case MacOS:
                stdin.write(" ; echo ; echo " + delimiter); break;
        }
        stdin.newLine();
        stdin.flush();
    }

    /**
     * @return a String containing all content until the end of the command execution.
     */
    public String readAll() throws IOException {
        StringBuffer buff = new StringBuffer();
        String line;

        while ((line= readLine()) != null) {
            buff.append(line).append('\n');
        }

        return buff.toString();
    }

    /**
     * @return one line from command execution, or null if the command has finished running
     */
    public String readLine() throws IOException {
        if (eol)
            return null;

        String line = stdout.readLine();
        if (line != null && line.startsWith(delimiter)) {
            eol = true;
            return null;
        }

        return line;
    }

    /**
     * Will clear any existing command output from buffer.
     */
    public void clear() throws IOException {
        while (readLine() != null); // read the output from previous exec
    }

    @Override
    public void close() {
        try {
            // close the shell by execution exit command
            stdin.write("exit");
            stdin.newLine();
            stdin.flush();
            process.destroy();
            process = null;
            stdin = null;
            stdout = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
