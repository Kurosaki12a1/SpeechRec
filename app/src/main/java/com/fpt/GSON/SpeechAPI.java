package com.fpt.GSON;

import java.util.List;

public class SpeechAPI{
	private List<HypothesesItem> hypotheses;
	private String id;
	private int status;

	public void setHypotheses(List<HypothesesItem> hypotheses){
		this.hypotheses = hypotheses;
	}

	public List<HypothesesItem> getHypotheses(){
		return hypotheses;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"SpeechAPI{" + 
			"hypotheses = '" + hypotheses + '\'' + 
			",id = '" + id + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}