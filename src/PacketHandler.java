import java.util.HashMap;

public class PacketHandler {
	HashMap<String, Integer> bill = new HashMap<>();

	public void change_bill(Packet packet, Integer integer) {
		if (integer <= 0) {
			bill.put(packet.getName(), integer);
		} else bill.remove(packet.getName());
	}

	public void clear_bill(){
		bill.clear();
	}
}
