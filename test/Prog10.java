import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

public class Prog10 {
	private static RandomAccessFile file;
	private static String prefix;
	private static LinkedList<byte[]> records;
	private static int recordLength = 78;
	private static int probs;

	public static void main(String[] args) throws IOException {
		if (args.length != 2){
			System.out.println("Usage: Prog10 path prefix");
			return;
		}
		
		file = new RandomAccessFile(args[0], "r");
		prefix = args[1].toLowerCase();
		records = new LinkedList<>();
		probs = 0;
		
		searchByPrefix(0, file.length() / recordLength);
		
		if (records.isEmpty()){
			System.out.println("No county with name starts with '" + prefix + "' is found.");
		} else {
			System.out.println("The counties whose names are prefixed with the letters 'barb' are:");
			System.out.println();
			for (byte[] record : records){
				System.out.println(decodeRecord(record));
			}
		}
		System.out.println();
		System.out.println("The number of 'mid' records read was " + probs);
	}	

	private static void searchByPrefix(long start, long end) throws IOException {
		if (end <= start)
			return;
		long mid = (start + end) / 2;
		probs++;
		
		printProbe(start, mid, end);
		
		byte[] buffer = new byte[recordLength];
		String county;
		
		county = readEntry(mid, buffer);
		
		if (county.toLowerCase().startsWith(prefix)){
			for (long i = mid; i < end; i++){
				county = readEntry(i, buffer);
				if (county.toLowerCase().startsWith(prefix) == false)
					break;
				records.add(Arrays.copyOf(buffer, buffer.length));
			}
			for (long i = mid - 1; i >= start; i--){
				county = readEntry(i, buffer);
				if (county.toLowerCase().startsWith(prefix) == false)
					break;
				records.add(Arrays.copyOf(buffer, buffer.length));
			}
			return;
		}
		
		if (county.toLowerCase().compareTo(prefix) < 0)
			searchByPrefix(mid + 1, end);
		if (county.toLowerCase().compareTo(prefix) > 0)
			searchByPrefix(start, mid);
	}

	private static String readEntry(long offset, byte[] buffer) throws IOException {
		file.seek(offset * recordLength);
		file.read(buffer);
		return new String(buffer, 14, 64).trim();
	}
	
	private static String decodeRecord(byte[] record) {
		String str = new String(record, 0, 2);
		str += "\t" + ByteBuffer.wrap(record, 10, 4).getInt();
		str += "\t" + (new String(record, 14, 64));
		return str.trim();
	}

	private static void printProbe(long start, long mid, long end) throws IOException {
		byte[] buffer = new byte[recordLength];
		
		System.out.println("Probe #" + probs + ":");
		System.out.println("Low:\t" + start + " " + "(" + readEntry(start, buffer) + ")");
		System.out.println("Mid:\t" + mid + " " + "(" + readEntry(mid, buffer) + ")");
		System.out.println("High:\t" + (end - 1) + " " + "(" + readEntry(end - 1, buffer) + ")");
		System.out.println();
	}

}
