package org.lucasimi.vptree;

import org.lucasimi.vptree.VPTree.TreeType;

public class SplitVPTreeBenchTest extends VPTreeBenchSuite {

    @Override
    public TreeType getTreeType() {
        return TreeType.SPLIT;
    }

}

