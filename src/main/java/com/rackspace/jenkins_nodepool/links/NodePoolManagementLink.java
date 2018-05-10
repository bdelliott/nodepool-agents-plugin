package com.rackspace.jenkins_nodepool.links;

import com.rackspace.jenkins_nodepool.NodePool;
import com.rackspace.jenkins_nodepool.NodePoolJobHistory;
import com.rackspace.jenkins_nodepool.NodePools;
import hudson.Extension;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Adds a link under "Manage Jenkins" which can be accessed to display information related to the health and
 * functioning of the Jenkins+NodePool environment.
 */
@Extension
public class NodePoolManagementLink extends hudson.model.ManagementLink {

    @CheckForNull
    @Override
    public String getIconFileName() {
        return "network.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return "NodePool";
    }

    @Override
    public String getDescription() {
        return "View health and diagnostic information related to the NodePool plugin.";
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return "nodepool-view";
    }

    public List<NodePool> getNodePools() {
        final NodePools nodePools = NodePools.get();
        return nodePools.getNodePools();
    }

    public NodePoolJobHistory getJobHistory() {
        final NodePools nodePools = NodePools.get();
        return nodePools.getJobHistory();
    }
}