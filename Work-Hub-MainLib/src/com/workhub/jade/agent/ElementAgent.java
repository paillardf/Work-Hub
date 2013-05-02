package com.workhub.jade.agent;
import com.workhub.jade.behaviour.ContentBehaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class ElementAgent extends Agent {
	
	 private int color;
	 private String title;
	 private AID editor;
	 //ElementModel contentModel;

	 public ElementAgent(int color, String title) {
		 this.color = color;
		 this.title = title;
		 this.editor = null;
		 
		//this.contentModel = ModelFactory.createDefaultModel();
		this.addBehaviour(new ContentBehaviour());
		}
	 
	 public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public AID getEditor() {
		return editor;
	}
	public void setEditor(AID editor) {
		this.editor = editor;
	}
	public boolean lockEdit(ClientAgent agent){
		 // si editor n'est pas null et est encore connect� retourner false (ne peut pas modifier)
		if(this.editor != null){
			int state = agent.getState(); // etat 6 : agent deleted, etat 4 : suspended 
			if(state == 6 || state == 4){
				setEditor(agent.getAID());
				return true;
			}
			else{
				return false; //tu peux pas �diter
			}
		}
		else{
			setEditor(agent.getAID());
			return true; // tu peux �diter
		}		 
	 }
}
