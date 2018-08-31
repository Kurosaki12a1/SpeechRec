package com.fpt.GSON;

public class HypothesesItem{
	private String utterance;

	public void setUtterance(String utterance){
		this.utterance = utterance;
	}

	public String getUtterance(){
		return utterance;
	}

	@Override
 	public String toString(){
		return 
			"HypothesesItem{" + 
			"utterance = '" + utterance + '\'' + 
			"}";
		}
}
