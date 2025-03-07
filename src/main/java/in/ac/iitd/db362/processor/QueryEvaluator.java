package in.ac.iitd.db362.processor;

import in.ac.iitd.db362.catalog.Catalog;
import in.ac.iitd.db362.index.Index;
import in.ac.iitd.db362.parser.Operator;
import in.ac.iitd.db362.parser.QueryNode;

import java.util.*;

/**
 * Starter code for Query Evaluator
 */
public class QueryEvaluator {

    /**
     * Evaluate the query represented by the parse tree.
     * For predicate nodes, return a list of row IDs by calling evaluatePredicate() .
     * For boolean operators, performs set operations:
     * - AND: Intersection of left and right results.
     * - OR: Union of left and right results.
     * - NOT: Complement of the result (assume row IDs from 0 to maxRowId).
     *
     * @param node The current query node.
     * @param maxRowId The maximum row ID (min is assumed to be 0).
     * @return A list of row IDs that satisfy the query.
     */
    public static List<Integer> evaluateQuery(QueryNode node, int maxRowId) {
        // Note: When traversing the parse tree, for each leaf node you must call
        // the evalautePredicate(node) method that is provided.
        // TODO: Implement me!
        return null;
    }

    /**
     * Note: do not change or remove this function! This method **must** be called from the evaluateQuery() method
     * when processing a leaf (predicate) node.
     */
    private static List<Integer> evaluatePredicate(QueryNode node) {
        System.out.println("Evaluating predicate: " + node.attribute + " " + node.operator + " " + node.value
                + (node.operator == Operator.RANGE ? " and " + node.secondValue : ""));

        //Let's get an index to work with
        Catalog catalog = Catalog.getInstance();
        Index index = catalog.getIndex(node.attribute, node.operator);
        return index.evaluate(node);
    }
}
