import java.io.Serializable;

public class Packet implements Serializable {
	String food;

	public Packet(int i){
		switch (i) {
			case 1 -> food = "apple";
			case 2 -> food = "banana";
			default -> food = "not_supported";
		}
	}

	public String getPacket() {
		return food;
	}
}
