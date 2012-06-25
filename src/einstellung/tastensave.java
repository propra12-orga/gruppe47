package einstellung;

/**
 * File:         tastensave.java
 * Copyright:    Copyright (c) 2012
 * @author Musab Kaya
 * @version 1.6
 */

/**
 * This class keeps track of the keyboard input in a queue for each player.
 */
public class tastensave {
	/** head and tail nodes */
    private Node head = null, tail = null;
	/** total nodes */
    private int totalItems = 0;

	/**
     * Default constructor.
     */
    public tastensave() {
	/** place holder */
    }

	/**
     * Pushes an item into the queue.
     * @param b byte to be pushed
     */
    public void push(byte b) {
        /** create the new node object */
        Node newNode = new Node(b);
        /** if list is empty then head and tail equals new object */
        if (head == null) head = tail = newNode;
        /** if list isn't empty */
        else {
            /** last object's next object equals new object */
            tail.next = newNode;
            /** new object's previous object equals last object */
            newNode.prev = tail;
            /** tail pointer now points to the new object */
            tail = newNode;
        }
        /** increase total items count */
        totalItems += 1;
    }

    /**
     * Removes an item from the list then return it.
     * @return last item in the list
     */
    public byte pop() {
        /** setup object to be returned */
        byte result = 0x00;
        /** if list isn't empty */
        if (tail != null) {
            /** copy last item's data */
            result = tail.data;
            /** last item now becomes last item's previous item */
            tail = tail.prev;
            /** if list isn't empty now then set last item to point to null */
            if (tail != null) tail.next = null;
            /** if list is empty then head = tail = null */
            else head = tail;
            /** decrease total items count */
            totalItems -= 1;
        }
        /** return the object */
        return result;
    }

    /**
     * Removes all instances of an item
     * @param b byte to be removed
     */
    public void removeItems(byte b) {
        /** create temporary node and set it to point to head */
        Node temp = head;
        /** loop while end of list not reached */
        while (temp != null) {
            /** if current pointed to object's data is equal to parameter */
            if (temp.data == b) {
                /** reset object's previous link */
                if (temp.prev != null) temp.prev.next = temp.next;
                /** reset object's next link */
                if (temp.next != null) temp.next.prev = temp.prev;
                /** if object is the head of list then reset head */
                if (temp == head) head = temp.next;
                /** if object is the tail of list then reset tail */
                if (temp == tail) tail = temp.prev;
                /** decrease total items count */
                totalItems -= 1;
            }
            /** get next item in the list */
            temp = temp.next;
        }
    }

	/**
     * Removes all elements
     */
     public void removeAll() {
        head = tail = null;
     }

    /**
     * @return size of the list
     */
    public int size() {
        return totalItems;
    }

    /**
     * @return the last item in the list.
     */
    public byte getLastItem() {
        /** setup data to be returned */
        byte result = 0x00;
        /** if list isn't empty then copy last item's data */
        if (tail != null) result = tail.data;
        /** return the data */
        return result;
    }

    /**
     * @return whether an item is in the list or not
     */
    public boolean contains(byte b) {
        /** setup data to be returned (default to false) */
        boolean result = false;
        /** create temporary Node object for navigation */
        Node temp = head;
        /** loop till end of list is reached */
        while (temp != null) {
            /** if data found then get otta here */
            if (temp.data == b) { result = true; break; }
            /** get next item in the list */
            temp = temp.next;
        }
        /** return the result */
        return result;
    }

    /**
     * This is the data object for the queue.
     */
    private class Node {
        /** the key data */
        public  byte data = 0x00;
        /** pointers / links */
        public Node prev = null, next = null;

        /**
         * Construct with data
         */
        public Node(byte b)
        {
            /** set data */
            data = b;
        }

        /**
         * Copy constructor.
         * @param n node to be copied
         */
        public Node(Node n)
        {
            /** copy data */
            data = n.data;
            /** copy the links */
            prev = n.prev;
            next = n.next;
        }
    }
}