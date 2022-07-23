import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedServer {
	public static void main(String[] args) {
		ServerSocket ss = null, obj_ss = null;
		Socket s, obj_s;
		int port = 4000, obj_port = 8000;

		try {
			ss = new ServerSocket(port);
			obj_ss = new ServerSocket(obj_port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				s = ss.accept();
				obj_s = obj_ss.accept();
				System.out.println("Connection established");
				Threaded threaded = new Threaded(s,obj_s);
				threaded.start();
			} catch (IOException e) {
				System.out.println("Can't establish connection");
				e.printStackTrace();
			}
		}
	}
}

class Threaded extends Thread {
	Socket socket, obj_socket;
	DataInputStream din;
	DataOutputStream dout;
	ObjectInputStream oin;
	String line;

	public Threaded(Socket s, Socket obj_s){
		this.socket = s;
		this.obj_socket = obj_s;
	}

	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			oin  = new ObjectInputStream(obj_socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error in thread");
			e.printStackTrace();
		}
			try {
				while (line==null || !line.equals("quit")) {
					line = din.readUTF();
					System.out.println("Received and echo: " + line);
					dout.writeUTF(line);
					dout.flush();
				}
			} catch (IOException e) {
				line = this.getName();
				System.out.println("Error: Client "+line+" terminated");
			} catch (NullPointerException ee) {
				line = this.getName();
				System.out.println("Error: Client "+line+" closed connection");
			}finally {
				try {
					din.close();
					dout.close();
					oin.close();
					socket.close();
					obj_socket.close();
					System.out.println("Connection closed successfully");
				} catch (IOException e) {
					System.out.println("Close error");
					e.printStackTrace();
				}
			}
	}
}