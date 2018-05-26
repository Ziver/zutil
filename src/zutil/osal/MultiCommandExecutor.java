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


    public MultiCommandExecutor(){
        try {
            // Generate delimiter
            byte[] tmp = new byte[16];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(tmp);
            delimiter = Base64Encoder.encode(tmp);

            //init shell
            String shellCmd;
            switch (OSAbstractionLayer.getInstance().getOSType()){
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

        } catch (RuntimeException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Unable to initiate shell",e);
        }
    }


    public void exec(String cmd) throws IOException {
        while (readLine() != null); // read the output from previous exec
        eol = false;

        stdin.write(cmd);
        switch (OSAbstractionLayer.getInstance().getOSType()){
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


    @Override
    public void close(){
        try {
            // finally close the shell by execution exit command
            stdin.write("exit");
            stdin.newLine();
            stdin.flush();
            process.destroy();
            process = null;
            stdin = null;
            stdout = null;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
