package org.lucasimi.vptree.flat;

import org.lucasimi.vptree.VPTree.TreeType;
import org.lucasimi.vptree.VPTreeTestSuite;

public class FlatVPTreeTest extends VPTreeTestSuite {

    @Override
    public TreeType getTreeType() {
        return TreeType.FLAT;
    }

}
