package com.jack.game;


import com.badlogic.gdx.math.MathUtils;
import com.jack.game.neuralJunk.Connection;
import com.jack.game.neuralJunk.Node;
import com.jack.game.neuralJunk.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Forer on 4/28/2017.
 */
public class NeuralAI extends AI{
	float aiTimer = 1f;
	float aiCount = 0;
	float aiSpeed = 2f;

	public List<Node> nodeList = new ArrayList<Node>();
	public List<Connection> connectionList = new ArrayList<Connection>();

	public NeuralAI(NeuralAI cloneThis) {
		this.nodeList = cloneThis.nodeList;
		this.connectionList = cloneThis.connectionList;
	}

	public NeuralAI () {
		//New AI from scratch
		//LeftSightBot - 0
		//LeftSightFood - 1
		//MiddleSightBot - 2
		//MiddleSightFood - 3
		//RightSightBot - 4
		//RightSightFood - 5
		//Nothing - 6
		//TurnLeft - 7
		//TurnRight - 8
		//MoveForward - 9
		//Birth - 10
		nodeList.add(new Node("LeftSightBot", 0, NodeType.sensor));
		nodeList.add(new Node("LeftSightFood", 1, NodeType.sensor));
		nodeList.add(new Node("MiddleSightBot", 2, NodeType.sensor));
		nodeList.add(new Node("MiddleSightFood", 3, NodeType.sensor));
		nodeList.add(new Node("RightSightBot", 4, NodeType.sensor));
		nodeList.add(new Node("RightSightFood", 5, NodeType.sensor));
		nodeList.add(new Node("Nothing", 6, NodeType.output));
		nodeList.add(new Node("TurnLeft", 7, NodeType.output));
		nodeList.add(new Node("TurnRight", 8, NodeType.output));
		nodeList.add(new Node("Forward", 9, NodeType.output));
		nodeList.add(new Node("Birth", 10, NodeType.output));
		setupAddConnection();
		setupAddConnection();
	}

	public String toString() {
		String output = "";
		for (Node n : nodeList) {
			if ((n.type != NodeType.output) && (n.type != NodeType.sensor)) {
				output += n.nodeNo + " " + n.type + " " + n.value + " ";
			}
		}

		output += "\n";
		for (Connection c : connectionList) {
			output += c.input.nodeName + " " + c.output.nodeName + " " + c.weight + " " + c.enabled + "\n";
		}

		return output;
	}

	void setupAddConnection() {
		Node n1 = null;
		Node n2 = null;

		while (n1 == null) {
			//Input only
			Node test = nodeList.get(MathUtils.random(nodeList.size()-1));

			if (test.type!=NodeType.output) {
				n1 = test;
			}
		}



		while (n2 == null) {
			//Output only
			Node test = nodeList.get(MathUtils.random(nodeList.size()-1));

			if (test.type!=NodeType.sensor) {
				n2 = test;
			}
		}



		//Is connection unique?
		if (connectionUnique(n1, n2)) {
			float w = MathUtils.random(2.0f) - 1.0f;
			connectionList.add(new Connection(n1, n2, w, true));
		}

	}

	public boolean connectionUnique (Node n1, Node n2) {
		for (Connection c : connectionList) {
			if (c.input == n1 && c.output == n2) {
				return false;
			}
		}
		return true;
	}


	public void update(float dt) {

		if ((!parent.moving) && (!parent.rotating)) {
			if (aiCount >= aiTimer) {
				aiCount=0;
				action();
			} else {
				aiCount+=aiSpeed * dt;
			}
		}

	}

	public void action() {
		resetNodes();

		while (!areAllNodesComplete()) {
			for (Node n : nodeList) {
				//Don't work on worked on stuff
				if (!n.complete) {
					//Work on stuff you can work on
					if (nodesCompleteBeforeThisNode(n)) {
						n.value = getNodeValue(n);
						n.complete = true;
					}
				}
			}
		}

		Node highest = null;
		for (Node n : nodeList) {
			if (n.type == NodeType.output) {
				if (highest == null) {
					highest = n;
				} else if (highest.value < n.value) {
					highest = n;
				}
			}
		}

		switch(highest.nodeNo) {
			//Nothing - 6
			//TurnLeft - 7
			//TurnRight - 8
			//MoveForward - 9
			//Birth - 10
			case 6:
				parent.doNothing();
				break;
			case 7:
				parent.rotateLeft();
				break;
			case 8:
				parent.rotateRight();
				break;
			case 9:
				parent.move();
				break;
			case 10:
				parent.birth();
				break;
		}


		//Debug, show me the values!
		if (false) {
			String output = "";
			for (Node n : nodeList) {
				if (n.type == NodeType.sensor) {
					output += n.nodeName + " " + n.value + " ";
				}
			}
			output += "\n";
			for (Node n : nodeList) {
				if (n.type == NodeType.output) {
					output += n.nodeName + " " + n.value + " ";
				}
			}
			System.out.print(output + "\n");
		}

	}

	public float getNodeValue(Node node) {
		List<Float> weightAnswers = new ArrayList<Float>();
		for (Connection c : connectionList) {
			if (c.output == node) {
				weightAnswers.add(c.input.value * c.weight);
			}
		}

		float output = 0.0f;
		for (Float f : weightAnswers) {
			output += f;
		}

		output = (float) ((1/( 1 + Math.pow(Math.E,(-1*output)))) * 2) - 1f;

		return output;
	}

	public boolean nodesCompleteBeforeThisNode(Node n) {
		List<Node> preNode = new ArrayList<Node>();
		for (Connection c : connectionList) {
			if (c.output == n) {
				preNode.add(c.input);
			}
		}

		for (Node node : preNode) {
			if (node.complete==false) return false;
		}
		return true;

	}

	public void resetNodes() {
		for (Node n : nodeList) {
			if (n.type == NodeType.sensor) {
				n.complete = true;
			} else {
				n.complete = false;
			}
			n.value = 0.0f;
		}

		//LeftSightBot - 0
		//LeftSightFood - 1
		//MiddleSightBot - 2
		//MiddleSightFood - 3
		//RightSightBot - 4
		//RightSightFood - 5
		//Nothing - 6
		//TurnLeft - 7
		//TurnRight - 8
		//MoveForward - 9
		//Birth - 10
		nodeList.get(0).value = parent.leftSightBot();
		nodeList.get(1).value = parent.leftSightFood();
		nodeList.get(2).value = parent.middleSightBot();
		nodeList.get(3).value = parent.middleSightFood();
		nodeList.get(4).value = parent.rightSightBot();
		nodeList.get(5).value = parent.rightSightFood();
	}

	boolean areAllNodesComplete() {
		for (Node n : nodeList) {
			if (n.complete == false) return false;
		}
		return true;
	}
}
