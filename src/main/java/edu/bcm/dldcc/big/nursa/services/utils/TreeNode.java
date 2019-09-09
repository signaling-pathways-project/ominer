package edu.bcm.dldcc.big.nursa.services.utils;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SunBurstData;

import java.util.HashMap;

/**
 * Created by alexey on 6/30/15.
 * a helper Class to make it easier to build a tree structure for graps
 * it basically represents a node of tree with unlimited number of children.
 * Each child has name, and can be searched by the name.
 */

public class TreeNode {
    private HashMap<String, TreeNode> children = new HashMap<String, TreeNode>();

    private SunBurstData node;

    public TreeNode addChild(String key, SunBurstData child) {

        if (null != children.get(key)) {
            throw new RuntimeException("Already have this child");
        }
        TreeNode newChild = new TreeNode(child);
        children.put(key, newChild );

        return newChild;
    }

    public TreeNode(SunBurstData data) {
        node = data;
    }

    public SunBurstData getNode() {
        return node;
    }

    public TreeNode get(String key) {
        return children.get(key);
    }
}
