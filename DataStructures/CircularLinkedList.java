/* Code implementation obtained from:
 * http://algorithms.tutorialhorizon.com/circular-linked-list-complete-implementation/
 * Slight modifications made to work with any object
 * */

public class CircularLinkedList <E> {

	public int max_size = 0;
	public int size =0;
	private Node <E> next_up;
	public Node <E> head=null;
	public Node <E> tail=null;

	public CircularLinkedList (int max) {
		max_size = max;
	}

	//add a new node at the start of the linked list
	public void addNodeAtStart(E data){
		Node <E> n = new Node <E> (data);
		if(size==0){
			head = n;
			tail = n;
			n.next = head;
		}else{
			Node <E> temp = head;
			n.next = temp;
			head = n;
			tail.next = head;
		}
		size++;
	}

	public void add(E data){

		if (max_size != size) { 

			if(size==0){
				addNodeAtStart(data);
				next_up = head;
			}else{
				Node <E> n = new Node <E> (data);
				tail.next =n;
				tail=n;
				tail.next = head;
				size++;
			}
			
		}
	}

	public void deleteNodeFromStart(){
		if(size==0){
		}else{
			head = head.next;
			tail.next=head;
			size--;
		}
	}

	public E elementAt(int index){
		if(index>size){
			return null;
		}
		Node <E> n = head;
		while(index-1!=0){
			n=n.next;
			index--;
		}
		return n.data;
	}
	
	public E next () {
		E toRet = next_up.data;
		next_up = next_up.next;
		return toRet;
	}

	//print the linked list
	public void print(){
		System.out.print("Circular Linked List:");
		Node <E> temp = head;
		if(size<=0){
			System.out.print("List is empty");
		}else{
			do {
				System.out.print(" " + temp.data);
				temp = temp.next;
			}
			while(temp!=head);
		}
		System.out.println();
	}

	//get Size
	public int getSize(){
		return size;
	}

}

class Node <E> {
	E data;
	Node <E> next;
	public Node(E data){
		this.data = data;
	}
}