package com.nexusbr.gv.view.components;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Font;

@SuppressWarnings("serial")
public class LoadingFeaturesStop extends JFrame implements Runnable{

	private JPanel contentPane;
	private static LoadingFeaturesStop instance;
	
	public static LoadingFeaturesStop getInstance(){
		if(instance==null)
			instance = new LoadingFeaturesStop();
		return instance;
	}

	/**
	 * Launch the application.
	 */
	public void open(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoadingFeaturesStop() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 200, 106);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Carregando geometrias...");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 11, 164, 19);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(LoadingFeaturesStop.class.getResource("/br/org/funcate/glue/image/loading_bar.gif")));
		label.setBounds(10, 30, 207, 25);
		contentPane.add(label);
	}

	@Override
	public void run() {
	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoadingFeaturesStop.getInstance().dispose();	
	}
}
