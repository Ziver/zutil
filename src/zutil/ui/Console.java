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

package zutil.ui;

import zutil.io.file.FileUtil;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Creates a Swing console window That takes System.in and
 * System.out as Streams and has the ability to stay in the tray
 * when closed
 *
 * @author Ziver
 *
 */
public class Console{
    public static String DEFAULT_ICON = "zutil/data/JavaConsole.png";
    // UI things
    private JFrame frame;
    private JTextPane console;
    private Document doc;
    private TrayIcon trayIcon;
    private int bufferSize;

    public Console(String title) {
        this(title, 680, 340, 50000, false);
    }

    public Console(String title, boolean tray) {
        this(title, 680, 340, 50000, tray);
    }

    public Console(String title, int width, int height, int buffer, boolean tray) {
        ConsoleInputStream in = new ConsoleInputStream();
        DEFAULT_ICON = FileUtil.find(DEFAULT_ICON).getAbsolutePath();
        initUI(title, in);

        bufferSize = buffer;
        System.setOut(new ConsolePrintStream(System.out, Color.white));
        System.setErr(new ConsolePrintStream(System.out, Color.red, TrayIcon.MessageType.ERROR));
        System.setIn(in);

        enableTray(tray);
        setFrameIcon(Toolkit.getDefaultToolkit().getImage(DEFAULT_ICON));
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    /**
     * initiates the ui
     */
    private void initUI(String title, KeyListener listener) {
        frame = new JFrame(title);

        console = new JTextPane();
        console.setBackground(Color.black);
        console.setForeground(Color.white);
        console.setFont(new Font("Lucida Console", Font.BOLD, 11));
        console.setEditable(false);
        console.addKeyListener(listener);
        doc = new DefaultStyledDocument();
        console.setDocument(doc);

        JScrollPane scroll = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.add(scroll, BorderLayout.CENTER);
    }

    public void setIcon() {

    }

    public void appendConsole(String s, Style style) {
        try {
            doc.insertString(doc.getLength(), s, style);
            if (doc.getLength() > bufferSize) {
                doc.remove(0, doc.getLength() - bufferSize);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables the tray icon and sets the icon
     * @param img The image to use as the icon
     */
    public void setTrayIcon(Image img) {
        enableTray(true);
        trayIcon.setImage(img);
    }

    /**
     * Sets the icon for the Frame
     *
     * @param img The image to use as a icon
     */
    public void setFrameIcon(Image img) {
        frame.setIconImage(img);
    }

    /**
     * Enables the  go down to tray functionality
     *
     * @param 	enable	if tray icon is enabled
     */
    public void enableTray(boolean enable) {
        if (enable && SystemTray.isSupported()) {
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray tray = SystemTray.getSystemTray();

            if (trayIcon == null) {
                // Menu
                PopupMenu menu = new PopupMenu();
                MenuItem item = new MenuItem("Open");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.setVisible(true);
                    }
                });
                menu.add(item);
                menu.addSeparator();
                item = new MenuItem("Exit");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                menu.add(item);

                // Icon
                trayIcon = new TrayIcon(
                        Toolkit.getDefaultToolkit().getImage(DEFAULT_ICON),
                        "Console", menu);
                trayIcon.setImageAutoSize(true);
                trayIcon.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2)
                            frame.setVisible(true);
                    }
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                    public void mousePressed(MouseEvent e) {}
                    public void mouseReleased(MouseEvent e) {}
                });
            }

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        }
        else {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }


    /**
     * The Stream to replace System.out
     *
     * @author Ziver
     *
     *	TrayIcon.MessageType.ERROR 	  An error message
     *	TrayIcon.MessageType.INFO 	  An information message
     *	TrayIcon.MessageType.NONE 	  A simple message
     *	TrayIcon.MessageType.WARNING  A warning message
     */
    private class ConsolePrintStream extends PrintStream{
        private Style style;
        private MessageType trayMessageType;

        public ConsolePrintStream(OutputStream out, Color c) {
            this(out, c, null);
        }

        public ConsolePrintStream(OutputStream out, Color c, MessageType type) {
            super(out);
            style = console.addStyle("PrintStream", null);
            StyleConstants.setForeground(style, c);
            trayMessageType = type;
        }

        public void print(String s) {
            appendConsole(s, style);
            console.setCaretPosition(console.getDocument().getLength());
            if (trayMessageType != null && trayIcon != null) {
                trayIcon.displayMessage(
                        s.substring(0, (s.length() > 25 ? 25 : s.length())) + "...",
                        s, trayMessageType);

            }
        }

        public void println(String s) {
            print(s + "\n");
        }

        public void println() {			 println("");}
        public void println(boolean x) { println("" +x);}
        public void println(char x) {	 println("" +x);}
        public void println(char[] x) {	 println(new String(x));}
        public void println(double x) {	 println("" +x);}
        public void println(float x) {	 println("" +x);}
        public void println(int x) {     println("" +x);}
        public void println(long x) {	 println("" +x);}
        public void println(Object x) {	 println("" +x);}

        public void print(boolean x) {   print("" +x);}
        public void print(char x) {	     print("" +x);}
        public void print(char[] x) {    print(new String(x));}
        public void print(double x) {    print("" +x);}
        public void print(float x) {     print("" +x);}
        public void print(int x) {       print("" +x);}
        public void print(long x) {      print("" +x);}
        public void print(Object x) {     print("" +x);}
    }

    private class ConsoleInputStream extends InputStream implements KeyListener{
        private boolean read = false;
        private int input;

        @Override
        public int read() {
            if (input < 0) {
                input = 0;
                return -1;
            }

            read = true;
            input = 0;
            while (input == 0) {
                try {Thread.sleep(10);} catch (InterruptedException e) {}
            }
            read = false;

            System.out.print((char)input);
            if (input == KeyEvent.VK_ENTER) {
                input = -1;
                return '\n';
            }
            return input;
        }
        /*
        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            System.out.println(off + "-" + len);
            int i;
            for (i=-1; i<len-1; i++) {
                int tmp = read();
                if (tmp < 0) {
                    break;
                }
                else {
                    b[i+off+1] = (byte) tmp;
                }

            }
            System.out.println(new String(b));
            return i;
        }

         */
        public void keyPressed(KeyEvent arg0) {}

        public void keyReleased(KeyEvent arg0) {}

        public void keyTyped(KeyEvent arg) {
            if (read) {
                input = arg.getKeyChar();
            }
        }
    }

}
