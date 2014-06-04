package OptionDialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;

import Components.BottomPanel;
import Components.CButton;
import Gui.JEditor;
import Layouts.FlowCustomLayout;

public class UpdateInstructionsDialog {

	private static UpdateInstructionsDialog instance;
	private JDialog dialog;
	private JEditorPane pane;
	private CButton updateNow,close;
	
	public static UpdateInstructionsDialog getInstance(){
		
		if(instance == null){
			instance = new UpdateInstructionsDialog();
		}
		
		return instance;
	}
	
	public UpdateInstructionsDialog(){
		init();
	}
	
	public void init(){
		
		dialog = new JDialog();
		pane = new JEditorPane();
		updateNow = new CButton("Update now", "Update JEditor now", 'U', null, null);
		close = new CButton("Close", "close the dialog and go back", 'C', KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), null);
		setPaneProperties();
		setDialogProperties();	
	}
	
	public void setPaneProperties(){
		pane.setEditable(false);
		pane.setBackground(dialog.getBackground());
		pane.setOpaque(false);
		
		InputStream input = BottomPanel.class.getResourceAsStream("/other/update_instructions.html");
		Scanner in = new Scanner(input);
		String temp = "";
		
		while(in.hasNext()){
			temp += in.nextLine();
		}
		in.close();
		
		pane.setContentType("text/html");
		pane.setText(temp);
	}
	
	public void setDialogProperties(){
		dialog.setTitle("How to update JEditor?");
		dialog.setSize(new Dimension(800,600));
		dialog.setLayout(new BorderLayout());
		dialog.add(getInstructionsPanel() , BorderLayout.CENTER);
		dialog.add(getButtonPanel() , BorderLayout.SOUTH);
		dialog.setLocationRelativeTo(JEditor.frame);
	}
	
	public JPanel getInstructionsPanel(){
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Instructions"));
		panel.setLayout(new BorderLayout());
		panel.add(pane , BorderLayout.CENTER);
		return panel;
	}
	
	public JPanel getButtonPanel(){
		JPanel panel = new JPanel(new FlowCustomLayout(FlowLayout.RIGHT));
		
		updateNow.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String link = "https://github.com/musaeed/JEditor/raw/master/bin/jeditor.jar";
				String fileName = System.getProperty("user.home")+"/.cache/JEditor/jeditor.jar";
				
				try{
					URL url = new URL(link);
					URLConnection c = url.openConnection();
					c.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");

					InputStream input;
					input = c.getInputStream();
					byte[] buffer = new byte[4096];
					int n = -1;

					OutputStream output = new FileOutputStream(new File(fileName));
					while ((n = input.read(buffer)) != -1) {
						if (n > 0) {
							output.write(buffer, 0, n);
						}
					}
					output.close();
				} catch(Exception e){
					e.printStackTrace();
					return;
				}
				
				
				JPanel panel = new JPanel(new BorderLayout());
				JLabel label = new JLabel("This action requires sudo rights. Please enter your password:");
				JPasswordField pass = new JPasswordField(10);
				panel.add(label , BorderLayout.NORTH);
				panel.add(pass , BorderLayout.CENTER);
				String[] options = new String[]{"OK", "Cancel"};
				int option = JOptionPane.showOptionDialog(null, panel, "Enter password",JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, options, options[1]);

				if(option != 0){
					return;
				}
				
				String[] cmd = {"/bin/bash","-c","echo "+new String(pass.getPassword())+"| sudo -S mv "+System.getProperty("user.home")+"/.cache/JEditor/jeditor.jar /usr/share/jeditor/"};
				
				try {
					
					Runtime.getRuntime().exec(cmd);
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				dialog.dispose();
				JOptionPane.showMessageDialog(JEditor.frame, "JEditor successfully updated. Please restart the application.", "Update", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		
		panel.add(updateNow);
		panel.add(close);
		return panel;
	}
	
	public void show(){
		dialog.setVisible(true);
	}
}