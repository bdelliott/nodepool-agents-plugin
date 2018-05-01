package com.rackspace.jenkins_nodepool.views;

import com.rackspace.jenkins_nodepool.NodePoolJob;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Track historical information about Jenkins jobs handled via the NodePool
 * plugin.
 */
public class NodePoolJobHistory implements Iterable<NodePoolJob> {

    private static final int MAX_HISTORY_LENTH = 100;

    private List<NodePoolJob> job = new ArrayList(MAX_HISTORY_LENTH);

    public void addJob(NodePoolJob job) {

        // TODO add job
        // TODO prune jobs
    }

    @Override
    public Iterator<NodePoolJob> iterator() {

        // TODO return a concurrent modification safe copy to iterate over.
        return null;
    }
}
