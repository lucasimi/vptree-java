package org.lucasimi.vptree.split;

import org.lucasimi.vptree.VPTree.TreeType;
import org.lucasimi.vptree.VPTreeTestSuite;

public class SplitVPTreeTest extends VPTreeTestSuite {

    @Override
    public TreeType getTreeType() {
        return TreeType.SPLIT;
    }

}
