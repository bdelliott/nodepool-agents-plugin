package com.rackspace.jenkins_nodepool;

import hudson.model.Queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap a Jenkins task so we can track some information over the NodePool processing cycle.
 */
public class NodePoolJob {

    private final Queue.Task task;

    private List<Attempt> attempts = new ArrayList<Attempt>();  // attempts to provision

    NodePoolJob(Queue.Task task) {
        this.task = task;
    }

    public Queue.Task getTask() {
        return task;
    }

    void addAttempt() {
        attempts.add(new Attempt());
    }

    void failAttempt(Exception e) {
        // mark current attempt as a failure
        getCurrentAttempt().e = e;
    }

    public void setRequest(NodeRequest request) {
        // save the request on the current attempt
        getCurrentAttempt().request = request;
    }

    private Attempt getCurrentAttempt() {
        final int sz = attempts.size();
        return attempts.get(sz-1);
    }

    class Attempt {
        NodeRequest request;
        Exception e;
    }
}
