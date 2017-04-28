package com.jack.game.neuralJunk;

/**
 * Created by Forer on 4/28/2017.
 */
public class Connection {
	public Node input;
	public Node output;
	public float weight;
	public boolean enabled;

	public Connection (Node i, Node o, float w, boolean e) {
		input = i;
		output = o;
		weight = w;
		enabled = e;
	}
}
