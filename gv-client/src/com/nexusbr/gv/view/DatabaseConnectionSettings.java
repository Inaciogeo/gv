package com.nexusbr.gv.view;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.org.funcate.glue.service.utils.DatabaseConnection;

import com.nexusbr.gv.main.MainGV;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class DatabaseConnectionSettings extends JFrame {

	private static DatabaseConnectionSettings instance;

	/** <Attribute type JPanel */

	private JTextField host;

	private JTextField port;

	private JTextField user;

	private JTextField dbName;

	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;

	private JButton btnConectar;

	private JButton btnCancelar;

	boolean connectionCreated;
	private JPasswordField passwordField;
	private JPanel panel;
	private JPanel panel_2;

	/**
	 * <Attribute type boolean
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DatabaseConnectionSettings() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
		getContentPane().setBackground(new Color(0, 0, 0));
		getContentPane().setLayout(null);
		getContentPane().setBounds(0, 0, 200, 300);

		JLabel lblDadosDaConexo = new JLabel("Dados da conexão");
		lblDadosDaConexo.setHorizontalAlignment(SwingConstants.CENTER);
		lblDadosDaConexo.setFont(new Font("SansSerif", Font.BOLD, 15));
		lblDadosDaConexo.setBounds(1, 96, 469, 14);
		getContentPane().add(lblDadosDaConexo);

		panel = new JPanel();
		panel.setBackground(new Color(255, 255, 255));
		panel.setBorder(new LineBorder(new Color(192, 192, 192)));
		panel.setBounds(24, 120, 419, 111);
		getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblHost = new JLabel("Host:");
		lblHost.setForeground(new Color(128, 128, 128));
		lblHost.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblHost.setBounds(14, 50, 46, 14);
		panel.add(lblHost);

		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setForeground(new Color(128, 128, 128));
		lblPorta.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblPorta.setBounds(14, 72, 46, 14);
		panel.add(lblPorta);

		JLabel lblTipoDoBanco = new JLabel("Tipo do Banco de Dados:");
		lblTipoDoBanco.setForeground(new Color(128, 128, 128));
		lblTipoDoBanco.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblTipoDoBanco.setBounds(14, 27, 185, 14);
		panel.add(lblTipoDoBanco);

		host = new JTextField();
		host.setBounds(213, 45, 191, 26);
		panel.add(host);
		host.setColumns(10);

		comboBox = new JComboBox();
		comboBox.setBounds(211, 18, 194, 28);
		panel.add(comboBox);
		comboBox.setBackground(new Color(255, 255, 255));
		comboBox.setFont(new Font("SansSerif", Font.BOLD, 12));
		comboBox.addItem("Postgres");
		comboBox.addItem("Access");

		port = new JTextField();
		port.setBounds(213, 71, 191, 26);
		panel.add(port);
		port.setColumns(10);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(192, 192, 192)));
		panel_1.setBackground(new Color(255, 255, 255));
		panel_1.setBounds(24, 240, 420, 101);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblUsurio = new JLabel("Usu\u00E1rio:");
		lblUsurio.setForeground(new Color(128, 128, 128));
		lblUsurio.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblUsurio.setBounds(9, 16, 126, 14);
		panel_1.add(lblUsurio);

		JLabel lblSenha = new JLabel("Senha:");
		lblSenha.setForeground(new Color(128, 128, 128));
		lblSenha.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblSenha.setBounds(9, 42, 126, 14);
		panel_1.add(lblSenha);

		JLabel lblNomeDoBanco = new JLabel("Nome do Banco de Dados:");
		lblNomeDoBanco.setForeground(new Color(128, 128, 128));
		lblNomeDoBanco.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblNomeDoBanco.setBounds(9, 68, 173, 14);
		panel_1.add(lblNomeDoBanco);

		user = new JTextField();
		user.setBounds(212, 7, 191, 26);
		panel_1.add(user);
		user.setColumns(10);

		dbName = new JTextField();
		dbName.setBounds(212, 62, 191, 26);
		panel_1.add(dbName);
		dbName.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(212, 34, 191, 26);
		panel_1.add(passwordField);

		btnConectar = new JButton("Conectar");
		btnConectar.setForeground(Color.BLACK);
		btnConectar.setBackground(new Color(255, 255, 255));
		btnConectar.setFont(new Font("SansSerif", Font.BOLD, 14));

		btnConectar.setBounds(78, 348, 106, 59);
		getContentPane().add(btnConectar);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.setForeground(Color.BLACK);
		btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnCancelar.setBackground(new Color(255, 255, 255));

		btnCancelar.setBounds(282, 349, 106, 59);
		getContentPane().add(btnCancelar);

		this.setUndecorated(true);

		this.setBackground(new Color(255, 255, 255));
		getContentPane().setLayout(null);

		panel_2 = new JPanel();
		panel_2.setBackground(new Color(255, 255, 255));
		panel_2.setBorder(new LineBorder(new Color(220, 220, 220)));
		panel_2.setBounds(1, 1, 466, 428);
		getContentPane().add(panel_2);

		JLabel lblLogo = new JLabel("");
		lblLogo.setIcon(new ImageIcon(DatabaseConnectionSettings.class
				.getResource("/com/nexusbr/gv/images/geopx_Icon120x75.png")));
		panel_2.add(lblLogo);

		this.setAlwaysOnTop(true);

		this.setBounds(0, 0, 468, 430);

		this.setLocationRelativeTo(null);

		this.setVisible(true);

		DatabaseConnectionSettingsListener listener = new DatabaseConnectionSettingsListener(this);

		this.addListener(listener);
	}

	public static DatabaseConnectionSettings getInstance() {
		if (instance == null) {
			instance = new DatabaseConnectionSettings();
			instance.setFocusable(true);
		}
		return instance;
	}

	private void addListener(DatabaseConnectionSettingsListener listener) {

		btnCancelar.addActionListener(listener);
		btnConectar.addActionListener(listener);

	}

	public JTextField getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host.setText(host);
	}

	public JTextField getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port.setText(port);
	}

	public JTextField getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user.setText(user);
	}

	public JPasswordField getPassword() {
		return passwordField;
	}

	public void setPassword(String password) {
		this.passwordField.setText(password);
	}

	public JTextField getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName.setText(dbName);
	}

	@SuppressWarnings("rawtypes")
	public JComboBox getComboBox() {
		return comboBox;
	}

	@SuppressWarnings("rawtypes")
	public void setComboBox(JComboBox comboBox) {
		this.comboBox = comboBox;
	}

	public JButton getBtnConectar() {
		return btnConectar;
	}

	public void setBtnConectar(JButton btnConectar) {
		this.btnConectar = btnConectar;
	}

	public JButton getBtnCancelar() {
		return btnCancelar;
	}

	public void setBtnCancelar(JButton btnCancelar) {
		this.btnCancelar = btnCancelar;
	}

	@SuppressWarnings("deprecation")
	public void connect() {
		try {
			DatabaseConnection.createXMLDatabaseConfig(
					this.getHost().getText(), this.getPassword().getText(),
					this.getUser().getText(), this.getDbName().getText(), this.getPort().getText(), 2);
			MainGV.main(null);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean connectionCreated() {
		return connectionCreated;
	}
}