package com.workhub.jade.behaviour;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.MessageTemplate.MatchExpression;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.workhub.utils.Constants;
import com.workhub.utils.MessageFactory;
import com.workhub.utils.Utils;

public class CreateElementBehaviour extends CyclicBehaviour {

	private MessageTemplate template = new MessageTemplate(new MatchExpression() {

		@Override
		public boolean match(ACLMessage msg) {
			//TODO: prévoir sis c'est un message systeme. Dans ce cas ce n'est pas forcement un JSON
			//MalformedJsonException
			JsonParser js = new JsonParser();
			int action = ((JsonObject) js.parse(msg.getContent())).get(Constants.JSON_ACTION).getAsInt();
			switch (action) {
			case Constants.MESSAGE_ACTION_CREATE_ELEMENT:
				return true;
			default:
				return false;
			}

		}
	});

	@Override
	public void action() {
		ACLMessage message = myAgent.receive(template);
		if (message!=null){
			ACLMessage answer = null;

			JsonParser js = new JsonParser();
			int action = ((JsonObject) js.parse(message.getContent())).get(Constants.JSON_ACTION).getAsInt();

			// si MESSAGE_ACTION_GET_CONTENT renvoie le contenu en envoyant au client un message MESSAGE_RECEIVE_ELEMENT_TITLE 

			switch(action){
			case Constants.MESSAGE_ACTION_CREATE_ELEMENT :				
				AgentController newElement;
				try {
					AgentContainer controller = myAgent.getContainerController();
					Date dNow = new Date( );
					SimpleDateFormat ft = 
							new SimpleDateFormat ("hh:mm:ss");

					newElement = controller.createNewAgent("Nouvel element : "+ft.format(dNow),"com.workhub.jade.agent.ElementAgent",new Object[]{MessageFactory.getAgentType(message), message.getSender()});
					newElement.start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private AID findClientAgent(String agent){
		DFAgentDescription[] result = Utils.agentSearch(myAgent, Constants.CLIENT_AGENT);

		for(DFAgentDescription df : result){
			if(df.getName().getName().equals(agent)){
				return df.getName();
			}
		}
		return null;

	}
}