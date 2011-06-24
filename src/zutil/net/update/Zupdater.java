package zutil.net.update;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JButton;

import zutil.ProgressListener;
import zutil.StringUtil;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalExclusionType;


public class Zupdater extends JFrame implements ProgressListener<UpdateClient, FileInfo>{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JLabel lblSpeed;
	private JLabel lblFile;
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Zupdater frame = new Zupdater();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public void progressUpdate(UpdateClient source, FileInfo info, double percent) {
			lblFile.setText( info.getPath() );
			progressBar.setValue( (int)percent );
			progressBar.setString( StringUtil.formatBytesToString( source.getTotalReceived() ) +
					" / "+StringUtil.formatBytesToString( source.getTotalSize() ));
			
			lblSpeed.setText( StringUtil.formatBytesToString(((UpdateClient)source).getSpeed())+"/s" );
	}
	
	
	/**
	 * Create the frame.
	 */
	public Zupdater() {
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Updating...");
		setBounds(100, 100, 537, 124);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		progressBar = new JProgressBar();
		progressBar.setString("2.5 MB / 5 MB");
		progressBar.setValue(50);
		progressBar.setStringPainted(true);
		progressBar.setToolTipText("");
		
		lblFile = new JLabel("apa.zip");
		
		lblSpeed = new JLabel("200 kb/s");
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JSeparator separator = new JSeparator();
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JLabel lblFile_1 = new JLabel("File:");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
					.addGap(10))
				.addComponent(separator, GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblFile_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblFile, GroupLayout.PREFERRED_SIZE, 331, GroupLayout.PREFERRED_SIZE)
					.addGap(60)
					.addComponent(lblSpeed, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCancel))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSpeed)
						.addComponent(lblFile)
						.addComponent(lblFile_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addContainerGap(14, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
