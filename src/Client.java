import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//to do: add keyboard strokes
public class Client extends JFrame {
	static Socket s, obj_s;
	static DataOutputStream dout;
	static DataInputStream din;
	static ObjectOutputStream oout;

	String host = "localhost";
	int port = 4000, obj_port = 8000;
	String nickname = "Client";
	String oldmsg, msgout = "";
	private String color = "#000000";

	private JTextField c_msg;
	private JTextPane c_area;
	private JButton c_button;
	private JPanel mainpn;
	private JScrollPane c_scroll;

	public Client() {
		super("client");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(mainpn);
		this.c_area.setEditable(false);
		this.pack();
		this.setVisible(true);

		c_area.setContentType("text/html");
		appendToPane(c_area,"<h1>list of commands:</h1><ul>" +
				"<li><b>@name</b> to change your name</li>"+
				"<li><b>#00ffff</b> to change color of your name</li>"+
				"<li><b>!1</b> or <b>!2</b> to test object stream</li>"+
				"<li>arrow up to revert to revert to last message</li></ul><br/>");

		c_button.addActionListener(new ActionListener() {

			/**
			 * Invoked when an action occurs.
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					msgout = c_msg.getText().trim();
					oldmsg = msgout;
					c_msg.requestFocus();
					c_msg.setText(null);
					if (msgout.equals("")) return;
					else if (msgout.charAt(0) == '@') {
						String temp = msgout.substring(1);
						nickname = temp.replace(" ", "_");
						return;
					} else if (msgout.charAt(0) == '!' && Character.isDigit(msgout.charAt(1))) {
						oout.writeObject(new Packet(Character.getNumericValue(msgout.charAt(1))));
						dout.writeUTF(msgout);
						System.out.println(msgout.charAt(1));
						appendToPane(c_area, "<span><i>an object has been sent to server</i></span>");
						return;
					} else if (msgout.charAt(0) == '#') {
						changeColor(msgout);
						return;
					}
					appendToPane(c_area, "<span><b style='color: " + color + "'>" + nickname + ": </b>" + msgout + "</span>");
					dout.writeUTF(msgout);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		c_msg.addKeyListener(new KeyAdapter() {
			/**
			 * Invoked when a key has been pressed.
			 * @param e key pressed
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						msgout = c_msg.getText().trim();
						oldmsg = msgout;
						c_msg.requestFocus();
						c_msg.setText(null);
						if (msgout.equals("")) return;
						appendToPane(c_area, "<span><b style='color: " + color + "'>" + nickname + ": </b>" + msgout + "</span>");
						dout.writeUTF(msgout);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					String current_msg = c_msg.getText().trim();
					c_msg.setText(oldmsg);
					oldmsg = current_msg;
				}
			}
		});

		try {
			s = new Socket(host, port);
			obj_s = new Socket(host, obj_port);
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());
			oout = new ObjectOutputStream(obj_s.getOutputStream());
			String msgin = "";
			while (!msgin.equals("exit")) {
				msgin = din.readUTF();
				appendToPane(c_area, "<span><b>Server: </b>" + msgin + "</span>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
	}

	private void appendToPane(JTextPane tp, String msg) {
		HTMLDocument doc = (HTMLDocument) tp.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			tp.setCaretPosition(doc.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeColor(String hexColor) {
		// validate string
		Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
		Matcher m = colorPattern.matcher(hexColor);
		if (m.matches()) {
			Color c = Color.decode(hexColor);
			// check if color is too bright
			double luma = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
			if (luma > 160) {
				System.out.println("Color too bright");
				return;
			}
			this.color = hexColor;
			System.out.println("Color changed successfully");
			return;
		}
		System.out.println("Wrong color format");
	}
}
