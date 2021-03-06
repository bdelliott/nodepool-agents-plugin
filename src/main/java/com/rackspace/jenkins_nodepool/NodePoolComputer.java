/*
 * The MIT License
 *
 * Copyright 2018 hughsaunders.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.rackspace.jenkins_nodepool;

import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Queue;
import hudson.model.Slave;
import hudson.slaves.SlaveComputer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.HttpResponse;

/**
 *
 * @author hughsaunders
 */
public class NodePoolComputer extends SlaveComputer {

    private static final Logger LOG = Logger.getLogger(NodePoolComputer.class.getName());
    /**
     * We can get the name from the nodePoolNode object but we store name
     * separately incase nodePoolNode is null after deserialisation then we can
     * use a sensible name in logs.
     */
    private String name;
    private NodePoolNode nodePoolNode;

    public NodePoolComputer(Slave slave) {
        super(slave);

        // required by superclass but shouldn't be used.
        throw new IllegalStateException("Attempting to initialise NodePoolComputer without supplying a NodePoolNode.");
    }

    public NodePoolComputer(final NodePoolSlave nps, final NodePoolNode npn) {
        super(nps);
        setNodePoolNode(npn);
        if (npn != null) {
            name = npn.getName();
        } else {
        Computer.threadPoolForRemoting.submit(() -> {
            try {
                    LOG.log(Level.WARNING, "Removing NodePool Computer {0} on startup as its a nodepool node that will have been destroyed", this.toString());
                    doDoDelete();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Failed to remove NodePool Computer {0}.", this.toString());
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public final void setNodePoolNode(NodePoolNode npn) {
        this.nodePoolNode = npn;
    }

    public NodePoolNode getNodePoolNode() {
        return nodePoolNode;
    }

    @Override
    public HttpResponse doDoDelete() throws IOException {
        try {
            if (nodePoolNode != null) {
                nodePoolNode.release();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return super.doDoDelete();
    }

    @Override
    public String toString() {
        if (nodePoolNode != null) {
            return nodePoolNode.getName();
        } else if (name != null) {
            return name;
        } else {
            return super.toString();
        }
    }

    @Override
    public String getDisplayName() {
        return toString();
    }

    @Override
    public void taskAccepted(Executor executor, Queue.Task task) {
        super.taskAccepted(executor, task);

        NodePoolComputer c = (NodePoolComputer) executor.getOwner();
        LOG.log(Level.FINE, "Starting task {0} on NodePoolComputer {1}", new Object[]{task.getFullDisplayName(), c});
    }

    @Override
    public void taskCompleted(Executor executor, Queue.Task task, long durationMS) {
        super.taskCompleted(executor, task, durationMS);

        LOG.log(Level.FINE, "Task " + task.getFullDisplayName() + " completed normally");
        deleteNodePoolComputer(executor, task);
    }

    @Override
    public void taskCompletedWithProblems(Executor executor, Queue.Task task, long durationMS, Throwable problems) {
        super.taskCompletedWithProblems(executor, task, durationMS, problems);

        LOG.log(Level.FINE, "Task " + task.getFullDisplayName() + " completed with problems", problems);

        deleteNodePoolComputer(executor, task);
    }

    private void deleteNodePoolComputer(Executor executor, Queue.Task task) {

        // indicate that the agent has been programmatically suspended.  this will safely mark the computer as
        // unavailable for tasks before the executor is freed up and Jenkins attempts to send it another task.
        final NodePoolComputer c = (NodePoolComputer) executor.getOwner();
        c.setAcceptingTasks(false);

        LOG.log(Level.INFO, "Deleting NodePoolNode {0} after task {1}", new Object[]{c, task.getFullDisplayName()});

        Computer.threadPoolForRemoting.submit(() -> {
            try {
                c.doDoDelete();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
    }
}
