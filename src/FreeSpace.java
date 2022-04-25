// FreeSpace class - Store nothing more than MemoryBlock
public class FreeSpace extends MemoryBlock 
{
	@Override
	public String toString() {
		String out = " FreeSpace\t" + super.toString();
		return out;
	}

	// this == obj2 return 0
	// this > obj2 return 1
	// this < obj2 return -1
	// Somehow automatic casting is not working .. had to use this
	public int compare(FreeSpace obj2) {
		int ans = super.compare((MemoryBlock) obj2);
		return ans;
	}
}
