import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
	static ServerSocket ss, obj_ss;
	static Socket s, obj_s;
	static DataOutputStream dout;
	static DataInputStream din;
	static ObjectInputStream oin;

	int port = 4000, obj_port = 8000;

	private JTextPane s_area;
	private JPanel mainpn;
	private JTextField s_msg;
	private JButton s_button;
	private JScrollPane s_scroll;

	public Server() {
		super("server");
		this.setContentPane(mainpn);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);

		s_area.setContentType("text/html");

		s_button.addActionListener(new ActionListener() {
			/**
			 * Invoked when an action occurs.
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String msgout = s_msg.getText().trim();
					dout.writeUTF(msgout);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		try {
			ss = new ServerSocket(port);
			obj_ss = new ServerSocket(obj_port);
			s = ss.accept();
			obj_s = obj_ss.accept();
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());
			oin=new ObjectInputStream(obj_s.getInputStream());
			String msgin = "";
			while(!msgin.equals("exit")){
				msgin = din.readUTF();
//				if (msgin.charAt(0)=='!'){
//					try {
//						Packet packet =(Packet) oin.readObject();
//						appendToPane(s_area, "<span>Object received: <i>"+packet.getPacket()+"</i></span>");
//						continue;
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//				}
				appendToPane(s_area,"<span>client: "+msgin+"</span>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToPane(JTextPane tp, String msg){
		HTMLDocument doc = (HTMLDocument)tp.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			tp.setCaretPosition(doc.getLength());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
	}
}
