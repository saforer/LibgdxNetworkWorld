package com.jack.aistuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.jack.game.Decker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Forer on 8/15/2017.
 */
public class NeatAI extends AI {

	List<Node> nodeList = new ArrayList<Node>();

	public NeatAI(Decker parent) {
		this(parent, "0S!1S!2S!3S!4S!5S!6S!7S!8S!9S!10S!11S!12S!13S!14S!15S!16O!17O!18O!19O!20O");
	}

	public NeatAI(Decker parent, String inputData) {
		this.parent = parent;

		convertStringToNodeList(inputData);


		Mutate();
	}

	void convertStringToNodeList(String input) {
		//String example
		//    0S!1S!2S!3H: 0,1; 1,1; 2,1!4O: 3,1!5O: 3,1

		/*
			RULES:
			----------------------
			! separates nodes
			: separates node from connections
			, separates elements of connection
			; separates connections from connections
			S means sensor
			O means output
			H means hidden

			0S!1S!2S
			means node 0 node 1 node 2

			0S!1S!2S!3O: 0, 1; 1, 1; 2, 1
			means node 0 node 1 node 2
			node 3 connected to 0, weight 1
			node 3 connected to 1, weight 1
			node 3 connected to 2, weight 1
		 */



		//remove spaces
		input = input.replaceAll("\\s", "");

		List<String> firstOrderList = Arrays.asList(input.split("!")); //This separates nodes from other nodes

		String currentParentNode = ""; //currentParentNode is the current node being worked on, important for later

		for (int i = 0; i < firstOrderList.size(); i++) {
			String secondLine = firstOrderList.get(i);
			if (secondLine.contains(":")) {
				List<String> secondOrderList = Arrays.asList(secondLine.split(":")); //This separates nodes from their connections
				for (int j = 0; j < secondOrderList.size(); j++) {
					String thirdLine = secondOrderList.get(j);
					if (j > 0) {
						currentParentNode = secondOrderList.get(0);
					}

					if (thirdLine.contains(";")) {
						List<String> thirdOrderList = Arrays.asList(thirdLine.split(";")); //This separates connections from other connections
						for (int k = 0; k < thirdOrderList.size(); k++) {
							//This will be in the form of (parent),(weight), and will be passed as such to the stringToConnection
							String fourthLine = thirdOrderList.get(k);
							//This is the list of connections for the node currently worked on
							addParentToNode(currentParentNode + " " + fourthLine);
						}
					} else {

						if (j > 0) {
							addParentToNode(currentParentNode + " " + thirdLine); //Only one connection for this node
						} else {
							addNodeFromString(thirdLine); //This is the node currently being declared it exists fresh
						}
					}
				}
			} else {
				addNodeFromString(secondLine); //No connections for this node!
			}
		}
	}

	public String nodeListToString () {
		String output = "";
		for (Node n : nodeList) {
			String nodeType = "";
			switch (n.type) {
				case sensor:
					nodeType+="S";
					break;
				case hidden:
					nodeType+="H";
					break;
				case output:
					nodeType+="O";
					break;
			}
			if (n.parentList.isEmpty()) {
				output += n.number + nodeType + "!";
			} else {
				output += n.number + nodeType + ":";
				for (Connection c : n.parentList) {
					output+=c.parentNumber + "," + c.weight+";";
				}
				output+="!";
			}
		}
		return output;
	}

	void addNodeFromString (String nodeString) {
		String chopped = nodeString.substring(nodeString.length()-1);
		NeuronType type;



		if (chopped.equalsIgnoreCase("S")) {
			type = NeuronType.sensor;
		} else if (chopped.equalsIgnoreCase("O")) {
			type = NeuronType.output;
		} else {
			type = NeuronType.hidden;
		}

		int nodeNumber = Integer.parseInt(nodeString.substring(0, nodeString.length() - 1));


		addNode(nodeNumber, type);
	}

	void addParentToNode (String connectionString) {
		int child = 0;
		int parentID = 0;
		float weight = 0f;

		String[] split = connectionString.split("\\s"); //This splits the child from parentid,weight
		String childString = split[0].substring(0, split[0].length() -1);
		child = Integer.parseInt(childString);
		String[] split2 = split[1].split(","); // This splits the parentID from the weight

		parentID = Integer.parseInt(split2[0]);
		weight = Float.parseFloat(split2[1]);


		addParent(child, parentID, weight);
	}



	void calculate() {
		int currentNode = 0;

		while (allNodesIncomplete()) {

			if (currentNode == 11) {
				//wtf
				return;
			}


			if (nodeList.get(currentNode).complete) {
				currentNode++;
			} else if (nodeList.get(currentNode).type == NeuronType.sensor) {
				nodeList.get(currentNode).complete = true;
				currentNode = 0;
			} else if (nodeList.get(currentNode).parentsComplete()) {
				nodeList.get(currentNode).calc();
				nodeList.get(currentNode).complete = true;
				currentNode = 0;
			} else if (!nodeList.get(currentNode).parentsComplete()) {
				currentNode++;
			}
		}


	}

	void printAllNodes() {
		for (Node n : nodeList) {
			System.out.println(n.toString());
		}
	}

	void setValueManually(int nodeNumber, float value) {
		getNodeByIdentifier(nodeNumber).value = value;
	}

	void getOutput() {
		for (Node n : nodeList) {
			if (n.type == NeuronType.output) {
				getValueManually(n.number);
			}
		}
	}

	float getValueManually(int nodeNumber) {
		Node currentNode = getNodeByIdentifier(nodeNumber);
		return currentNode.value;
	}

	boolean allNodesIncomplete() {
		for (Node n : nodeList) {
			if (!n.complete) return true;
		}
		return false;
	}

	void resetAllNodes() {
		for (Node n : nodeList) {
			n.reset();
		}
	}


	void addNode(int idNumber, NeuronType type) {
		Node node = new Node(this);
		node.number = idNumber;
		node.type = type;
		if (node.neatAI == null) {
			node.neatAI = this;
		}
		nodeList.add(node);
	}

	void addParent (int child, int parentID, float weight) {
		Node childNode = getNodeByIdentifier(child);
		childNode.parentList.add(new Connection(parentID, weight));
	}

	Node getNodeByIdentifier(int identifier) {
		for (Node n : nodeList) {
			if (n.number == identifier) return n;
		}
		return null;
	}

	@Override
	public void update() {

		//Reset neurode values
		resetAllNodes();

		//reset sensors back to proper values
		resetSensors();

		if (!parent.currentlyDoingSomething) {
			//Calculate output
			calculate();

			//Do output
			int currentHighestNode = 19;
			float currentHighestValue = getValueManually(19); //Eat

			if (currentHighestValue < getValueManually(17)) {
				currentHighestNode = 17;
				currentHighestValue = getValueManually(17); //Left
			}

			if (currentHighestValue < getValueManually(16)) {
				currentHighestNode = 16;
				currentHighestValue = getValueManually(16); //Forward
			}

			if (currentHighestValue < getValueManually(18)) {
				currentHighestNode = 18;
				currentHighestValue = getValueManually(18); //Right
			}

			if (currentHighestValue < getValueManually(20)) {
				currentHighestNode = 20;
				currentHighestValue = getValueManually(20); //Egg
			}


			switch (currentHighestNode) {
				case 16:
					parent.move();
					break;
				case 17:
					parent.rotateLeft();
					break;
				case 18:
				default:
					parent.rotateRight();
					break;
				case 20:
					parent.layEgg();
					break;
				case 19:
					parent.eat();
					break;
			}
		}
	}



	void resetSensors() {
		setValueManually(0, parent.bugInFront?1:0);
		setValueManually(1, parent.bugLeft?1:0);
		setValueManually(2, parent.bugRight?1:0);
		setValueManually(3, parent.bugBehind?1:0);
		setValueManually(4, parent.foodInFront?1:0);
		setValueManually(5, parent.foodLeft?1:0);
		setValueManually(6, parent.foodRight?1:0);
		setValueManually(7, parent.foodBehind?1:0);
		setValueManually(8, parent.wallInFront?1:0);
		setValueManually(9, parent.wallLeft?1:0);
		setValueManually(10, parent.wallRight?1:0);
		setValueManually(11, parent.wallBehind?1:0);
		setValueManually(12, 1f);
		setValueManually(13, -1f);
		setValueManually(14, parent.foodPercent);
		setValueManually(15, parent.currentFood);
	}

	public void printInput() {
		for (Node n : nodeList) {
			if (n.type == NeuronType.sensor) {
				System.out.print(n.number + " " + n.value + " ");
			}
		}
		System.out.print("\n");
	}

	public void printOutput() {
		for (Node n : nodeList) {
			if (n.type == NeuronType.output) {
				System.out.print(n.number + " " + n.value + " ");
			}
		}
		System.out.print("\n");
	}



	public void Mutate() {
		float randomFloat = MathUtils.random(1);

		if (randomFloat > .75) {
			randomChangeWeight();
		}

		randomFloat = MathUtils.random(1);

		if (randomFloat > .5) {
			randomAddConnection();
		}

		randomFloat = MathUtils.random(1);

		if (randomFloat > .2) {
			randomDisableConnection();
		}

		randomFloat = MathUtils.random(1);

		if (randomFloat > .2) {
			randomEnableConnection();
		}

		randomFloat = MathUtils.random(1);

		if (randomFloat > .1) {
			randomAddNode();
		}
	}

	void randomAddConnection() {
		//Parent can't be an output or can't be a node that would create an infinite loop
		//child can't be a sensor
		//weight must be between -1 & 1

		List<Node> validParents = new ArrayList<Node>();
		List<Node> validChildren = new ArrayList<Node>();
		float randomWeight = MathUtils.random(0, 1);

		for (Node n : nodeList) {
			switch (n.type) {
				case hidden:
					validParents.add(n);
					validChildren.add(n);
					break;
				case output:
					validChildren.add(n);
					break;
				case sensor:
					validParents.add(n);
					break;
			}
		}

		Node selectedChild = null;
		while (selectedChild == null) {
			int randomIntForChild = MathUtils.random(validChildren.size() - 1);
			selectedChild = validChildren.get(randomIntForChild);
		}


		Node selectedParent = null;
		while (selectedParent == null) {
			int randomIntForParent = MathUtils.random(validParents.size() - 1);

			selectedParent = validParents.get(randomIntForParent);

			for (Connection c : selectedChild.parentList) {
				if (c.parentNumber == randomIntForParent) {
					selectedParent = null;
				}
			}

			if (selectedParent != null) {
				String[] parentArray = getParents(selectedParent).split("\\s");


				for (int i = 0; i < parentArray.length; i++) {
					if (parentArray[i] == selectedChild.number + "") {
						selectedParent = null;
					}
				}
			}

		}

		addParent(selectedChild.number, selectedParent.number, randomWeight);
	}

	void randomChangeWeight() {
		//Get nodes that have connections
		List<Node> hasConnection = new ArrayList<Node>();

		for (int i = 0; i < nodeList.size(); i++) {
			if (!nodeList.get(i).parentList.isEmpty()) {
				hasConnection.add(nodeList.get(i));
			}
		}

		if (!hasConnection.isEmpty()) {
			//Select a random node from the list of nodes that have a connection
			int randomInt = MathUtils.random(hasConnection.size() - 1);
			Node randomNode = hasConnection.get(randomInt);

			//Find a random connection on the node that has a connection
			randomInt = MathUtils.random(randomNode.parentList.size() - 1);
			Connection randomConnection = randomNode.parentList.get(randomInt);

			//Set a random weight on the connection
			randomConnection.weight = MathUtils.random(-1f, 1f);
		}
	}

	void randomDisableConnection() {
		//Get nodes that have connections
		List<Node> hasConnection = new ArrayList<Node>();

		for (int i = 0; i < nodeList.size(); i++) {
			if (!nodeList.get(i).parentList.isEmpty()) {
				hasConnection.add(nodeList.get(i));
			}
		}

		//Select a random node from the list of nodes that have a connection
		if (hasConnection.isEmpty()) {
			return;
		}
		int randomInt = MathUtils.random(hasConnection.size() -1);
		Node randomNode = hasConnection.get(randomInt);

		//Find a random connection on the node that has a connection
		randomInt = MathUtils.random(randomNode.parentList.size() -1);
		Connection randomConnection = randomNode.parentList.get(randomInt);


		//Break connection (disable connection)
		randomConnection.enabled = false;
	}

	void randomAddNode() {
		//Get nodes that have connections
		List<Node> hasConnection = new ArrayList<Node>();

		for (int i = 0; i < nodeList.size(); i++) {
			if (!nodeList.get(i).parentList.isEmpty()) {
				hasConnection.add(nodeList.get(i));
			}
		}

		//Select a random node from the list of nodes that have a connection
		if (hasConnection.isEmpty()) {
			return;
		}
		int randomInt = MathUtils.random(hasConnection.size() -1);
		Node randomNode = hasConnection.get(randomInt);

		//Find a random connection on the node that has a connection
		randomInt = MathUtils.random(randomNode.parentList.size() -1);
		Connection randomConnection = randomNode.parentList.get(randomInt);


		//Break connection (disable connection)
		randomConnection.enabled = false;

		//New Node
		Node n = new Node(this);
		n.number = getNewNodeNumber();
		n.type = NeuronType.hidden;
		nodeList.add(n);

		//New node connection to old parent node, same weight as before
		addParent(n.number, randomConnection.parentNumber, randomConnection.weight);


		//Old child to new node, weight 1
		addParent(randomNode.number, n.number, 1f);
	}

	int getNewNodeNumber() {
		return nodeList.size();
	}

	void randomEnableConnection() {
		//Find connection that has been disabled
		//Re-enable it
	}

	String getParents(Node n) {

		if (n.parentList.isEmpty()) {
			return "";
		}

		String output = "";
		for (Connection c : n.parentList) {
			output += c.parentNumber + " ";
		}
		return output;
	}

	public int hiddenNodeCount () {
		int output = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).type == NeuronType.hidden) {
				output++;
			}
		}
		return output;
	}
}

class Node {
	NeatAI neatAI;
	List<Connection> parentList = new ArrayList<Connection>();
	int number; //Identifier
	float value;
	boolean complete = false;
	NeuronType type = NeuronType.hidden;

	public Node(NeatAI ai) {
		neatAI = ai;
	}


	public void reset() {
		if (type != NeuronType.sensor) {
			value = 0f;
			complete = false;
		}
	}

	public boolean parentsComplete () {
		for (Connection c : parentList) {
			if (c.enabled) {
				//is there an incomplete node?
				Node p = neatAI.getNodeByIdentifier(c.parentNumber);
				if (!p.complete) return false;
			}
		}
		return true;
	}

	public void calc() {
		for (Connection c : parentList) {
			if (c.enabled) {
				Node currentParent = neatAI.getNodeByIdentifier((c.parentNumber));
				value += currentParent.value * c.weight;
			}
		}
	}


	public String toString() {
		String output = "";
		output += number;
		output += " ";
		output += value;
		if (!parentList.isEmpty()) {
			output += "\n";
			for (Connection c : parentList) {
				output += "    ";
				output += c.parentNumber;
				output += " ";
				output += c.weight;
				output += "\n";
			}
		}
		return output;
	}
}

class Connection {
	int parentNumber;
	float weight;
	boolean enabled = true;

	public Connection(int parentID, float weight) {
		this.parentNumber = parentID;
		this.weight = weight;
	}
}



enum NeuronType {
	sensor,
	hidden,
	output
}