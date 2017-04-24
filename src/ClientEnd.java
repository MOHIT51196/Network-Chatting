import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class ClientEnd extends JFrame {

	
	private static final long serialVersionUID = 1L;
	
	private String HOST_NAME = ResourceLoader.getProperty("hostname");
	private int PORT = Integer.parseInt(ResourceLoader.getProperty("port"));
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private JPanel contentPane;
	private JTextArea chatArea;
	private JTextField chatField;
	private JButton btnSend;
	
	public static boolean isAlive;
	private JLabel lblAlert;
	private boolean isAlertVisible;
	private int alertCounter = 0;
	
	private String username;
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ClientEnd() {
		setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		setTitle("< UserName >");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setResizable(false);
		setBounds(700, 100, 550, 450);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.textHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		setUsername(JOptionPane.showInputDialog(ResourceLoader.getProperty("username_dialog")));
		setTitle(username);
		
		chatArea  = new JTextArea();
		chatArea.setFont(new Font("Trebuchet MS", Font.ITALIC, 14));
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setBounds(149, 30, 363, 306);
		chatArea.setBorder(new EmptyBorder(4, 10, 4, 10));
		chatArea.setEditable(false);
		contentPane.add(chatArea);
		
		btnSend = new JButton("SEND");
		btnSend.setFont(new Font("Trebuchet MS", Font.ITALIC, 12));
		btnSend.setBounds(423, 363, 89, 23);
		btnSend.setFocusable(false);
		btnSend.addActionListener((event)->{
			try {
				sendMessage();
				chatField.setText("");
				showAlert("send_alrt");
			} catch (IOException e) {
				showAlert("sendmsg_err");
				e.printStackTrace();
			}
		});
		contentPane.add(btnSend);
		
		chatField = new JTextField();
		chatField.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		chatField.setBounds(149, 363, 253, 23);
		contentPane.add(chatField);
		chatField.setColumns(10);
		
		lblAlert = new JLabel("");
		lblAlert.setBackground(null);
		lblAlert.setForeground(Color.RED);
		lblAlert.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblAlert.setBounds(10, 377, 108, 23);
		lblAlert.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(lblAlert);
		

		setVisible(true);
	}
	
	public void initialize(){
		
		Socket socket;
		try {
			socket = new Socket(HOST_NAME, PORT);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			
			isAlive = true;
			
			
		} catch (IOException e) {
			showAlert("start_err");
			e.printStackTrace();
		}
		
		
	}
	
	public void readMessage() {
		
		while(ClientEnd.isAlive){
			
			String message ;
			try {
				message = inputStream.readUTF();
				
//				chatArea.setAlignmentX(JTextArea.RIGHT_ALIGNMENT);
				chatArea.setText(chatArea.getText() + message);
				
			} catch (IOException e) {
				showAlert("readmsg_err");
				e.printStackTrace();
			}
			

		}
		
		showAlert("disconnect_alrt");
		
	}
	
	private void sendMessage() throws IOException{
		
		if(ServerEnd.isAlive){
			String message = "\nUSER 2 :>  " + chatField.getText();
			outputStream.writeUTF(message);
//			chatArea.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
			chatArea.setText(chatArea.getText() + message);
		}
	
		else{
			showAlert("disconnect_alrt");
		}
	}
	
	private void showAlert(String alertkey){
		
		String alertText = ResourceLoader.getProperty(alertkey);
		
		lblAlert.setText(alertText);
		isAlertVisible = true;
		alertCounter = 0;
		
		new Timer(800, (alrtEvent)->{
			lblAlert.setVisible(isAlertVisible);
			isAlertVisible = !isAlertVisible;
			alertCounter++;
			
			if(alertCounter >= 6){
				lblAlert.setText("");
				lblAlert.setVisible(false);
				( (Timer)alrtEvent.getSource() ).stop();
			}
		}).start();
	}

}
