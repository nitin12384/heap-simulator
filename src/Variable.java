// Store name also - apart from MemoryBlock members
class Variable extends MemoryBlock 
{
	public String name;

	public Variable(String nm, int strtAdr, int sz) {
		set(strtAdr, sz);
		name = nm;
	}
	
	public String toString() {
		String out = " Name : " + name + "\t" + super.toString();
		return out;
	}
}