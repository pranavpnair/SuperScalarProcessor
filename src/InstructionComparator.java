import java.util.Comparator;


public class InstructionComparator implements Comparator<Info>{
	public int compare(Info x, Info y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (x.instno < y.instno)
        {
            return -1;
        }
        if (x.instno > y.instno)
        {
            return 1;
        }
        return 0;
    }
}
