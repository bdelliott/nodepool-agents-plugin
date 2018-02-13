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
package org.wherenow.jenkins_nodepool;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum State{
	requested, pending, fulfilled, failed
}

/**
 * Represents a nodepool node request. Data format is JSON dump of following dict structure:
 * 	---
 * 	node_types:
 * 		- label1
 * 		- label2
 *	requestor: string id (eg hostname)
 * 	state: string requested|pending|fulfilled|failed
 * 	state_time: float seconds since epoch 
 * 
 * @author hughsaunders
 */
public class NodeRequest {

	private Map<String, Object> data;
	private Gson gson;
	
	public NodeRequest(String label)	{
		this("jenkins", Arrays.asList( new String[] { label }));
	}

	public NodeRequest(List labels)	{
		this("jenkins", labels);
	}
	@SuppressFBWarnings
	public NodeRequest(String requestor, List labels) {
		this.gson = new Gson();
		this.data = new HashMap();
		this.data.put("node_types", labels);
		this.data.put("requestor", requestor);
		this.data.put("state", State.requested);
		this.data.put("state_time", System.currentTimeMillis()/1000);
	}
	
	@Override
	public String toString(){
		String jsonStr = gson.toJson(this.data);
		return jsonStr;
	}

}
