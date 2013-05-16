package com.workhub.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.MessageTemplate.MatchExpression;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.workhub.utils.Constants;
// Behaviour agent client
public class ContentClientBehaviour extends CyclicBehaviour{

	
	private MessageTemplate template = new MessageTemplate(new MatchExpression() {
		@Override
		public boolean match(ACLMessage msg) {
			JsonParser js = new JsonParser();
			int action = ((JsonObject) js.parse(msg.getContent())).get(Constants.JSON_ACTION).getAsInt();
			switch (action) {
			case Constants.MESSAGE_ACTION_CONTENT:
			case Constants.MESSAGE_ACTION_EDIT:
				return true;
			default:
				return false;
			}
		}
	});
	
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}

}
