import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;

public class ServerEnd extends JFrame {

	
	private static final long serialVersionUID = 1L;
	
	private final int PORT = Integer.valueOf(ResourceLoader.getProperty("port"));
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private JPanel contentPane;

	private JTextArea chatArea;
	private JTextField chatField;
	private JButton btnSend;
	private static JPanel chatListPanel;
	
	public static boolean isAlive;
	private JLabel lblAlert;
	private boolean isAlertVisible;
	private int alertCounter = 0;
	private static int chatlistX = 0;
	private static int chatlistY = 0;
	
	private static  ServerEnd instance;
	
	public static ServerEnd newInstance(){
		
		if(instance == null){
			instance = new ServerEnd();
		}
		
		return instance;
	}
	
	
	private ServerEnd() {
		setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		setTitle("Master User");
		setBackground(Color.GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		setBounds(80, 100, 550, 450);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.textHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		chatArea = new JTextArea();
		chatArea.setFont(new Font("Trebuchet MS", Font.ITALIC, 14));
		chatArea.setBounds(149, 32, 363, 313);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
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
		chatField.setFont(new Font("Trebuchet MS", Font.ITALIC, 12));
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
		
		chatListPanel = new JPanel();
		chatListPanel.setBounds(10, 45, 120, 300);
		contentPane.add(chatListPanel);
		chatListPanel.setLayout(new GridLayout(1, 1, 0, 1));
		
		JLabel lblChatBuddies = new JLabel("Chat Buddies");
		lblChatBuddies.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatBuddies.setForeground(Color.WHITE);
		lblChatBuddies.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		lblChatBuddies.setBounds(10, 11, 108, 23);
		contentPane.add(lblChatBuddies);
		
		setVisible(true);
	}
	
	
	public void initialize() {
		
		isAlive = true;
		
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(PORT);
			Socket socket = serverSocket.accept();
			
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			
			
			
		} catch (IOException e) {
			showAlert("start_err");
			e.printStackTrace();
		}
		
		
	}
	
	public void readMessage() {
		
		while(ServerEnd.isAlive){
			
			String message;
			try {
				message = inputStream.readUTF();
				

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
			String message = "\nUSER 1 :>  " + chatField.getText();
			outputStream.writeUTF(message);

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
	
	public static void generateClientList(ArrayList<ClientEnd> clientList){
		
		int listSize = clientList.size();
		
		for(int i=0; i<listSize; i++){
			
			if( i == 0 ){
				chatlistX = 0;
				chatlistY = 0;
			}
			else{
				chatlistY += 100;
			}
			
			JLabel userLbl = new JLabel(clientList.get(0).getUsername());
			userLbl.setBounds(chatlistX, chatlistY, 120, 60);
			userLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
			userLbl.setHorizontalAlignment(JLabel.CENTER);
			userLbl.setOpaque(true);
			userLbl.setVisible(true);
			chatListPanel.add(userLbl);
			
			System.out.println("Added Client "+clientList.get(0).getUsername());
			
		}
	}
	
	private static int count=0;
	public static void generateClientList(ClientEnd client){
		
			
			if( count == 0 ){
				chatlistX = chatListPanel.getX();
				chatlistY = chatListPanel.getY();
			}
			else{
				chatlistY += 300;
			}
			
			JLabel userLbl = new JLabel(client.getUsername());
			userLbl.setBounds(chatlistX, chatlistY, 120, 300);
			userLbl.setOpaque(true);
			userLbl.setVisible(true);
			userLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
			chatListPanel.add(userLbl);
			
			count++;
	}
}
