import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import struts.SteamAccount;
import api.Steam;


public class SteamLogin extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JTextField txtAccessCode;
	private JTextField txtCaptchaCode;
	boolean needsCaptcha = false;
	boolean needAccessCode = false;
	String captchaGID;
	//String authPath = "./machineAuths/";
	File authPath = new File("./machineAuths/");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					} catch (Exception e) {
						try {
							UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					SteamLogin frame = new SteamLogin();
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
	public SteamLogin() {
		
		if(!authPath.exists())
			authPath.mkdir();
		
		setResizable(false);
		setTitle("Steam Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 303, 214);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 0;
		contentPane.add(lblUsername, gbc_lblUsername);
		
		txtUsername = new JTextField();
		GridBagConstraints gbc_txtUsername = new GridBagConstraints();
		gbc_txtUsername.insets = new Insets(0, 0, 5, 5);
		gbc_txtUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUsername.gridx = 1;
		gbc_txtUsername.gridy = 0;
		contentPane.add(txtUsername, gbc_txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		contentPane.add(lblPassword, gbc_lblPassword);
		
		txtPassword = new JPasswordField();
		GridBagConstraints gbc_txtPassword = new GridBagConstraints();
		gbc_txtPassword.insets = new Insets(0, 0, 5, 5);
		gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPassword.gridx = 1;
		gbc_txtPassword.gridy = 1;
		contentPane.add(txtPassword, gbc_txtPassword);
		
		JLabel lblAccessCode = new JLabel("Access Code:");
		GridBagConstraints gbc_lblAccessCode = new GridBagConstraints();
		gbc_lblAccessCode.anchor = GridBagConstraints.EAST;
		gbc_lblAccessCode.insets = new Insets(0, 0, 5, 5);
		gbc_lblAccessCode.gridx = 0;
		gbc_lblAccessCode.gridy = 2;
		contentPane.add(lblAccessCode, gbc_lblAccessCode);
		
		txtAccessCode = new JTextField();
		txtAccessCode.setEnabled(false);
		GridBagConstraints gbc_txtAccessCode = new GridBagConstraints();
		gbc_txtAccessCode.insets = new Insets(0, 0, 5, 5);
		gbc_txtAccessCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAccessCode.gridx = 1;
		gbc_txtAccessCode.gridy = 2;
		contentPane.add(txtAccessCode, gbc_txtAccessCode);
		txtAccessCode.setColumns(10);
		
		JLabel lblCaptchaImg = new JLabel("Captcha Img:");
		lblCaptchaImg.setBackground(Color.black);
		GridBagConstraints gbc_lblCaptchaImg = new GridBagConstraints();
		gbc_lblCaptchaImg.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaptchaImg.gridx = 0;
		gbc_lblCaptchaImg.gridy = 3;
		contentPane.add(lblCaptchaImg, gbc_lblCaptchaImg);

		final JLabel lblCaptcha = new JLabel("");
		lblCaptcha.setSize(206, 40);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		contentPane.add(lblCaptcha, gbc_lblNewLabel);
		
		JLabel lblCaptchaCode = new JLabel("Captcha Code:");
		GridBagConstraints gbc_lblCaptchaCode = new GridBagConstraints();
		gbc_lblCaptchaCode.anchor = GridBagConstraints.EAST;
		gbc_lblCaptchaCode.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaptchaCode.gridx = 0;
		gbc_lblCaptchaCode.gridy = 4;
		contentPane.add(lblCaptchaCode, gbc_lblCaptchaCode);
		
		txtCaptchaCode = new JTextField();
		txtCaptchaCode.setEnabled(false);
		GridBagConstraints gbc_txtCaptchaCode = new GridBagConstraints();
		gbc_txtCaptchaCode.insets = new Insets(0, 0, 5, 5);
		gbc_txtCaptchaCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCaptchaCode.gridx = 1;
		gbc_txtCaptchaCode.gridy = 4;
		contentPane.add(txtCaptchaCode, gbc_txtCaptchaCode);
		txtCaptchaCode.setColumns(10);
		gbc_txtCaptchaCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCaptchaCode.gridx = 1;
		gbc_txtCaptchaCode.gridy = 5;
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SteamAccount account = null;
				String machineAuth = getMachineAuth(txtUsername.getText());
				try {
					
					if(needAccessCode)
						account = Steam.login(txtUsername.getText(), txtPassword.getText(), txtAccessCode.getText(), machineAuth);
					else if(needsCaptcha)
						account = Steam.login(txtUsername.getText(), txtPassword.getText(), captchaGID, txtCaptchaCode.getText(), machineAuth);
					else if(needsCaptcha && needAccessCode)
						account = Steam.login(txtUsername.getText(), txtPassword.getText(), captchaGID, txtCaptchaCode.getText(), txtAccessCode.getText(), machineAuth);
					else
						account = Steam.login(txtUsername.getText(), txtPassword.getText(), machineAuth);
					
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Login failed, try again.");
				}
				
				if(account != null && account.getResponse() != null && account.getResponse().get("message") != null && ((String)account.getResponse().get("message")).equals("Incorrect login")){
					JOptionPane.showMessageDialog(null, "Incorrect Password.");
				}
				
				if(account != null && account.getResponse() != null && account.getResponse().get("emailauth_needed") != null && ((Boolean)account.getResponse().get("emailauth_needed")).booleanValue()){
					needAccessCode = true;
					JOptionPane.showMessageDialog(null, "Please check email for access code and try again.");
					txtAccessCode.setEnabled(true);
				}
				
				if(account != null && account.getResponse() != null && account.getResponse().get("captcha_needed") != null && ((Boolean)account.getResponse().get("captcha_needed")).booleanValue()){
					needsCaptcha = true;
					captchaGID = account.getResponse().get("captcha_gid").toString();
					
					try {
						ImageIcon icon = new ImageIcon(new URL("https://steamcommunity.com/public/captcha.php?gid=" + captchaGID));
						lblCaptcha.setIcon(icon);
						JOptionPane.showMessageDialog(null, "Catpcha needed, please enter the text in the image.");
						txtCaptchaCode.setEnabled(true);
					} catch (MalformedURLException e) {
						JOptionPane.showMessageDialog(null, "Error grabbing catpcha, try again.");
					}
				}
				
				if(account != null && account.getResponse() != null && account.getResponse().get("login_complete") != null && ((Boolean)account.getResponse().get("login_complete")).booleanValue()){

					writeMachineAuthFile(txtUsername.getText(), account.getSteamMachineAuthCookie());
					SteamBot bot = new SteamBot(account);
					bot.show();
					dispose();
					
				}
			}
		});
		
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.anchor = GridBagConstraints.EAST;
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 1;
		gbc_button.gridy = 5;
		contentPane.add(login, gbc_button);
		
	}
	
	public String getMachineAuth(String username){
		if(!authPath.exists()){
			authPath.mkdir();
			return null;
		}
		
		File file = new File(authPath.getPath() + "/" + username.toLowerCase() + ".txt");
		
		if(!file.exists())
			return null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String cookie = br.readLine();
			br.close();
			
			return cookie;
		} catch (Exception e) {
			return null;
		} 
	}
	public void writeMachineAuthFile(String username, String cookie){
		if(!authPath.exists())
			authPath.mkdir();
		
		File file = new File(authPath.getPath() + "/" + username.toLowerCase() + ".txt");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(cookie + "\r\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
