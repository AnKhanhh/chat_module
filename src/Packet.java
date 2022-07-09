import java.io.Serializable;

public class Packet implements Serializable {
	String name;
	int price;

	public Packet(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}
}
