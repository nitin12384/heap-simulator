public class MemoryBlock 
{
	public int startAdr;
	public int endAdr; 
	public int size;// size = endAdr - startAdr + 1
	
	public MemoryBlock() {
	
	}
	@Override
	public String toString() {
		String out = "Address : (" + startAdr + " \tto  " + endAdr + "),\tSize : " + size + "\n";
		return out;
	}

	// this == obj2 return 0
	// this > obj2 return 1
	// this < obj2 return -1
	public int compare(MemoryBlock obj2) {
		// comparison based on startAdr
		int ans = 0;
		if (this.startAdr > obj2.startAdr) {
			ans = 1;
		} 
		else if (this.startAdr < obj2.startAdr) {
			ans = -1;
		}
		return ans;
	}

	// size should be >0, startAdr should be >=0
	public void set(int startAdr_Val, int size_Val) {
		size = size_Val;
		startAdr = startAdr_Val;
		endAdr = startAdr + size - 1;
	}
}

