package einstellung;


/**
 * Diese Klasse speichert die Tasteneinstellung zu jedem Spieler.
 */
public class tastensave {

    private Node head = null, tail = null;

    private int totalItems = 0;

    public tastensave() {
    }

    public void push(byte b) {
        Node newNode = new Node(b);
        if (head == null) head = tail = newNode;
        else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        totalItems += 1;
    }

    public byte pop() {
        byte result = 0x00;
        if (tail != null) {
            result = tail.data;
            tail = tail.prev;
            if (tail != null) tail.next = null;
            else head = tail;
            totalItems -= 1;
        }
        return result;
    }

    public void removeItems(byte b) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == b) {
                if (temp.prev != null) temp.prev.next = temp.next;
                if (temp.next != null) temp.next.prev = temp.prev;
                if (temp == head) head = temp.next;
                if (temp == tail) tail = temp.prev;
                totalItems -= 1;
            }
            temp = temp.next;
        }
    }


     public void removeAll() {
        head = tail = null;
     }

    public int size() {
        return totalItems;
    }

    public byte getLastItem() {
        byte result = 0x00;
        if (tail != null) result = tail.data;
        return result;
    }

    public boolean contains(byte b) {
        boolean result = false;
        Node temp = head;
        while (temp != null) {
            if (temp.data == b) { result = true; break; }
            temp = temp.next;
        }
        return result;
    }

    private class Node {
        public  byte data = 0x00;
        public Node prev = null, next = null;

        public Node(byte b)
        {
            data = b;
        }

        public Node(Node n)
        {
            data = n.data;
            prev = n.prev;
            next = n.next;
        }
    }
}