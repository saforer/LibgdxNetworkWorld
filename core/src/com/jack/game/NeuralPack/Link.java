package com.jack.game.NeuralPack;

/**
 * Created by Forer on 5/2/2017.
 */
public class Link {
	//Sensors are tier 0, links FROM the sensors are tier 1 sensors
	public Node in;
	public Node out;
	public float weight;

	public Link (Node in, Node out, float weight) {
		this.in = in;
		this.out = out;
		this.weight = weight;
	}
}
