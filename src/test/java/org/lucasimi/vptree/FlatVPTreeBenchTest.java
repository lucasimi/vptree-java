package org.lucasimi.vptree;

import org.lucasimi.vptree.VPTree.TreeType;

public class FlatVPTreeBenchTest extends VPTreeBenchSuite {

    @Override
    public TreeType getTreeType() {
        return TreeType.FLAT;
    }

}

