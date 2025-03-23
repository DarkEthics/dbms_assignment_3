package in.ac.iitd.db362.index.bplustree;

import in.ac.iitd.db362.index.Index;
import in.ac.iitd.db362.parser.QueryNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.time.LocalDate;

/**
 * Starter code for BPlusTree Implementation
 * @param <T> The type of the key.
 */
public class BPlusTreeIndex<T> implements Index<T> {

    protected static final Logger logger = LogManager.getLogger();

    // Note: Do not rename this variable; the test cases will set this when testing. You can however initialize it with a
    // different value for testing your code.
    public static int ORDER = 10;

    // The attribute being indexed
    private String attribute;

    // Our Values are all integers (rowIds)
    private Node<T, Integer> root;
    private final int order; // Maximum children per node

    /** Constructor to initialize the B+ Tree with a given order */
    public BPlusTreeIndex(String attribute) {
        this.attribute = attribute;
        this.order = ORDER;
        this.root = new Node<>();
        this.root.isLeaf = true;
    }

    @Override
    public List<Integer> evaluate(QueryNode node) {
        logger.info("Evaluating predicate using B+ Tree index on attribute " + attribute + " for operator " + node.operator);
        //TODO: Implement me!
        return null;
    }

    @Override
    public void insert(T key, int rowId) {
        //TODO: Implement me!
    }

    @Override
    public boolean delete(T key) {
        //TODO: Bonus
        return false;
    }

    @Override
    public List<Integer> search(T key) {
        //TODO: Implement me!
        //Note: When searching for a key, use Node's getChild() and getNext() methods. Some test cases may fail otherwise!

        logger.info("searching for key: " + key);

        Node<T,Integer> node = root;
        while(!node.isLeaf){
            int idx = 0;
            while(idx < node.keys.size() && key.compareTo(node.keys.get(idx)) > 0){
                idx++;
            }
            node = node.getChild(idx);
        }
        
        for(int i=0; i < node.keys.size(); i++ ){
            if(node.keys.get(i).equals(key)){
                return List.of(node.values.get(i));
            }
        }
        return null;
    }

    /**
     * Function that evaluates a range query and returns a list of rowIds.
     * e.g., 50 < x <=75, then function can be called as rangeQuery(50, false, 75, true)
     * @param startKey
     * @param startInclusive
     * @param endKey
     * @param endInclusive
     * @return all rowIds that satisfy the range predicate
     */
    List<Integer> rangeQuery(T startKey, boolean startInclusive, T endKey, boolean endInclusive) {
        //TODO: Implement me!
        //Note: When searching, use Node's getChild() and getNext() methods. Some test cases may fail otherwise!

        logger.info("Performing range query: " + (startInclusive ? "[" : "(") + startKey + ", " + endKey + (endInclusive ? "]" : ")"));    
        List<Integer> result = new ArrayList<>();
        Node<T, Integer> node = root;

        while(!node.isLeaf){
            int idx = 0;
            while(idx < node.keys.size() && key.compareTo(node.keys.get(idx)) > 0){
                idx++;
            }
            node = node.getChild(idx);
        }

        while(node != null){
            for(int i=0; i < node.keys.size() ; i++){
                T key = node.keys.get(i);

                boolean afterStart = (key.compareTo(startkey) > 0) || (startInclusive && key.compareTo(startKey) ==0);
                boolean beforeEnd  = (key.compareTo(endKey) <0) || (endInclusive && key.compareTo(endKey) ==0);
            
                if(afterStart && beforeEnd){
                    result.add(node.values.get(i));
                }
                else if(key.compareTo(endKey) > 0){
                    return result;
                }
            }
            node = node.getNext();
        }
        return null;
    }

    /**
     * Traverse leaf nodes and collect all keys in sorted order
     * @return all Keys
     */
    public List<T> getAllKeys() {
        // TODO: Implement me!

        logger.info("Collecting all keys in sorted order");
        List<T> allKeys = new ArrayList<>();
        Node<T,Integer> node = root;

        while(!node.isLeaf){
            node = node.getChild(0);
        }

        while(node!=null){
            allKeys.addAll(node.keys);
            node = node.getNext();
        }
        return allKeys;
        return null;
    }

    /**
     * Compute tree height by traversing from root to leaf
     * @return Height of the b+ tree
     */
    public int getHeight() {
        // TODO: Implement me!

        logger.info("Computing height of the B+ Tree");

        int height = 0;
        Node<T,Integer> node = root;

        while(!node.isLeaf){
            node = node.getChild(0);
            height++;
        }
        return height;
        return 0;
    }

    /**
     * Funtion that returns the order of the BPlusTree
     * Note: Do not remove this function!
     * @return
     */
    public int getOrder() {
        return order;
    }


    @Override
    public String prettyName() {
        return "B+Tree Index";
    }
}
