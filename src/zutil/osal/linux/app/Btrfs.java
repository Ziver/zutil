package zutil.osal.linux.app;

import zutil.osal.MultiCommandExecutor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Wrapper for the linux Btrfs command to query and modify btrfs based filesystems.
 */
public class Btrfs {
    private static String BTRFS_CMD = "btrfs";
    private static SimpleDateFormat DATE_PARSER = new SimpleDateFormat("dow mon dd hh:mm:ss zzz yyyy");

    // The disk used as an id to btrfs
    private String path;
    // Terminal where all commands will be executed.
    private MultiCommandExecutor terminal;


    public Btrfs(String path) {
        this(path, new MultiCommandExecutor());
    }
    public Btrfs(String path, MultiCommandExecutor terminal) {
        this.path = path;
        this.terminal = terminal;
    }


    // ****************************************************
    // Balance functions
    // ****************************************************

    public void startBalance() throws IOException {
        execBtrfsCommand("balance start", path);
    }

    public void cancelBalance() throws IOException {
        execBtrfsCommand("balance cancel", path);
    }

    public void getBalanceProgress() throws IOException {
        execBtrfsCommand("balance status", path);
    }

    // ****************************************************
    // Scrub functions
    // ****************************************************

    public void startScrub() throws IOException {
        execBtrfsCommand("scrub start", path);
    }

    public void cancelScrub() throws IOException {
        execBtrfsCommand("scrub cancel", path);
    }

    public void getScrubProgress() throws IOException {
        execBtrfsCommand("scrub status", path);
    }

    public Date getLastScrubDate() throws IOException {
        String log = execBtrfsCommand("scrub status", path);

        return null;
    }

    // ****************************************************
    // Utility functions
    // ****************************************************

    public String execBtrfsCommand(String params, String path) throws IOException {
        terminal.exec(BTRFS_CMD + " " + params + " " + path);

        return terminal.readAll();
    }
}
