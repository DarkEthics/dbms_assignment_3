package in.ac.iitd.db362.index.bplustree;

import java.util.List;

// B+ Tree node structure.
// Note: Do not modify this class
public class Node<K, V> {
    boolean isLeaf;
    List<K> keys;
    List<V> values; // should be null/empty for non-leaf nodes!
    List<Node<K, V>> children;
    Node<K, V> next; // For leaf node linking

    /**
     * The function returns a child node of this node.
     * @param offset
     * @return return child node
     */
    Node<K,V> getChild(int offset) {
        assert isLeaf = false;
        return this.children.get(offset);
    }

    /**
     * The function returns the next node pointed by a leaf node.
     * @return next leaf node
     */
    Node<K,V> getNext() {
        assert isLeaf = true;
        return this.next;
    }
}