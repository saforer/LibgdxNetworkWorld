package com.jack.game.neuralJunk;

/**
 * Created by Forer on 4/28/2017.
 */
public class Node {
	public String nodeName;
	public int nodeNo;
	public NodeType type;
	public float value;
	public boolean complete;

	public Node (String name, int no, NodeType type) {
		this.nodeName = name;
		this.nodeNo = no;
		this.type = type;
	}

	public Node (int no) {
		nodeName = "Random";
		type = NodeType.hidden;
		nodeNo = no;
	}
}
