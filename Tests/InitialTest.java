import java.util.Iterator;

/* Eric Frye
 *  InitialTest is a basic test; to be used for quick testing of any small feature
 * */

public class InitialTest {

	public static void main(String[] args) {
				
		CircularLinkedList <Integer> test = new CircularLinkedList <Integer> (9);
		
		for (int i = 0; i < 9; i++) {
			test.add(i);
		}
		
		test.print();
		
		test.add(10);
		
		for (int i = 50; i < 100; i++) {
			System.out.println (test.next());
		}
		
	}
	
	

}
