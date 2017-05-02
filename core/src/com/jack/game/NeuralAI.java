package com.jack.game;


import com.badlogic.gdx.math.MathUtils;
import com.jack.game.NeuralPack.Link;
import com.jack.game.NeuralPack.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Forer on 5/2/2017.
 */
public class NeuralAI extends AI{
	/*
	This is to be a neural network, not neat, neat will be neatAi
	 */

	float actionCount = 0;
	int listWidth = 4;
	int listHeight = 4;
	List<List<Node>> masterList = new ArrayList<List<Node>>();
	List<Link> linkList = new ArrayList<Link>();

	public NeuralAI(NeuralAI parentAI) {
		//First add sensors
		/*
			leftsightfood
			leftsightbot
			middlesightfood
			middlesightbot
			rightsightfood
			rightsightbot
			constant
			wallInFront
		 */
		List<Node> sensorList = new ArrayList<Node>();
		for (int i = 0; i <= 8; i++) {
			sensorList.add(new Node());
		}
		masterList.add(sensorList);

		//Add the filler center node stuff)
		List<Node> temp;
		for (int x = 0; x<listWidth; x++) {
			temp = new ArrayList<Node>();
			for (int y = 0; y<listHeight; y++) {
				temp.add(new Node());
			}
			masterList.add(temp);
		}


		//Add the output nodes
		/*
			rotateleft
			rotateright
			move
			nothing
			birth
		 */
		List<Node> outputList = new ArrayList<Node>();
		for (int i = 0; i <= 5; i++) {
			outputList.add(new Node());
		}
		masterList.add(outputList);


		//We have a parent, Use the connections that they gave us
		for (Link l : parentAI.linkList) {
			linkList.add(new Link(l.in, l.out, l.weight));
		}
	}

	public NeuralAI() {
		//First add sensors
		/*
			leftsightfood
			leftsightbot
			middlesightfood
			middlesightbot
			rightsightfood
			rightsightbot
			constant
			wallInFront
		 */
		List<Node> sensorList = new ArrayList<Node>();
		for (int i = 0; i <= 8; i++) {
			sensorList.add(new Node());
		}
		masterList.add(sensorList);

		//Add the filler center node stuff)
		List<Node> temp;
		for (int x = 0; x<listWidth; x++) {
			temp = new ArrayList<Node>();
			for (int y = 0; y<listHeight; y++) {
				temp.add(new Node());
			}
			masterList.add(temp);
		}


		//Add the output nodes
		/*
			rotateleft
			rotateright
			move
			nothing
			birth
		 */
		List<Node> outputList = new ArrayList<Node>();
		for (int i = 0; i <= 5; i++) {
			outputList.add(new Node());
		}
		masterList.add(outputList);


		//Now connect all of them up!
		for (int i = 0; i <= listWidth; i++) {
			temp = masterList.get(i);
			List<Node> temp2 = masterList.get((i+1));
			for (Node n : temp2) {
				for (Node node : temp) {
					linkList.add(new Link(node, n, MathUtils.random(2.0f) - 1.0f));
				}
			}
		}
	}


	@Override
	public void update(float dt) {
		if (actionCount >= parent.map.actionTimer) {
			actionCount=0.0f;

			List<Node> sensors = masterList.get(0);

		/*
			leftsightfood
			leftsightbot
			middlesightfood
			middlesightbot
			rightsightfood
			rightsightbot
			constant
			wallInFront
		 */
			sensors.get(0).value = parent.leftSightFood();
			sensors.get(1).value = parent.leftSightBot();
			sensors.get(2).value = parent.middleSightFood();
			sensors.get(3).value = parent.middleSightBot();
			sensors.get(4).value = parent.rightSightFood();
			sensors.get(5).value = parent.rightSightBot();
			sensors.get(6).value = 1.0f;
			sensors.get(7).value = parent.wallInFront();

			List<Node> currentTier;
			Node currentNode;
			List<Link> allLinks;
			for (int i = 1; i <= listWidth+1; i++) {
				currentTier = masterList.get(i);
				for (int j = 0; j < currentTier.size(); j++) {
					currentNode = currentTier.get(j);
					allLinks = getLinkWithNodeAsOutput(currentNode);
					for (Link l : allLinks) {
						currentNode.value += nodeLinkValue(l);
					}
					currentNode.value = sigmoid(currentNode.value);
				}
			}

		/*
			rotateleft
			rotateright
			move
			nothing
			birth
		 */
			int highestInt = 0;
			float highestValue = 0;
			for (int i = 0; i <= 5; i++) {
				if (masterList.get(listWidth+1).get(i).value > highestValue) {
					highestInt = i;
					highestValue = masterList.get(listWidth+1).get(i).value;
				}
			}

			List<Node> sensList = masterList.get(0);
			String out = "";

			out += "LF " + sensList.get(0).value;
			out += " LB " + sensList.get(1).value;
			out += " MF " + sensList.get(2).value;
			out += " MB " + sensList.get(3).value;
			out += " RF " + sensList.get(4).value;
			out += " RB " + sensList.get(5).value;
			out += " Con " + sensList.get(6).value;
			out += " Wall " + sensList.get(7).value;

			out += "\n";
			List<Node> outList = masterList.get(listWidth+1);

			out += "Left " + outList.get(0).value;
			out += " Right " + outList.get(1).value;
			out += " Move " + outList.get(2).value;
			out += " Nothing " + outList.get(3).value;
			out += " Birth " + outList.get(4).value + "\n";

			System.out.print(out);

			switch (highestInt) {
				case 0:
					parent.rotateLeft();
					break;
				case 1:
					parent.rotateRight();
					break;
				case 2:
					parent.move();
					break;
				case 3:
					parent.doNothing();
					break;
				case 4:
					parent.birth();
					break;
			}
		} else {
			actionCount+=dt;
		}
	}


	public List<Link> getLinkWithNodeAsOutput (Node n) {
		List<Link> out = new ArrayList<Link>();
		for (Link l : linkList) {
			if (l.out == n) {
				out.add(l);
			}
		}
		return out;
	}

	public float nodeLinkValue (Link l) {
		return l.in.value * l.weight;
	}

	public float sigmoid(float in) {
		return (float) (1 / (1 + Math.exp(-in)));
	}
}
