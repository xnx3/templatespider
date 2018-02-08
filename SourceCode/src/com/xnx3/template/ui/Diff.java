package com.xnx3.template.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Diff extends JFrame {
	public boolean use = true;	//是否正在使用此窗口。若是在使用，则是true，若是已经用完，则为false
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Diff frame = new Diff();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Diff() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
//				use = false;
				System.out.println("windowClosed");
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
				use = false;
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
				System.out.println("windowDeiconified");
			}
		});
		setBounds(100, 100, 1521, 891);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
	}

	public JPanel getContentPane() {
		return contentPane;
	}

}
