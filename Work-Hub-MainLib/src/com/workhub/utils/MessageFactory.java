package com.workhub.utils;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.pdf.codec.Base64;
import com.workhub.jade.agent.ClientAgent;
import com.workhub.jade.agent.ElementAgent;
import com.workhub.model.ElementModel;
import com.workhub.model.LinkElementModel;
import com.workhub.model.PictureElementModel;
import com.workhub.model.TextElementModel;
//import com.workhub.model.FileElementModel;

public class MessageFactory {	


	public static ACLMessage createMessage(ElementAgent sender, AID receiver, int MessageType){
		int performatif;
		String content = "message vide";
		JsonObject j = new JsonObject();
		//AID receiver_final = new AID(receiver.toString(), AID.ISGUID);//findElementAgent(agent);


		switch (MessageType) {
		case Constants.MESSAGE_ACTION_EDIT:

			boolean autorization = sender.lockEdit(receiver);
			Utils.agentSearch(sender, Constants.CLIENT_AGENT);
			j.addProperty(Constants.JSON_CAN_EDIT, autorization);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_EDIT);			
			break;

		case Constants.MESSAGE_ACTION_CONTENT:
			// annonce que l'element a ete modifie et que le Client (receiver) doit le mettre a jour
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_CONTENT);
			break;

		case Constants.MESSAGE_ACTION_IS_DYING:
			// annonce que l'element a ete modifie et que le Client (receiver) doit le mettre a jour
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_IS_DYING);
			break;	
			
		case Constants.MESSAGE_ACTION_ELEMENT_CHANGED:
			// annonce que l'element a ete modifie et que le Client (receiver) doit le mettre a jour
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_ELEMENT_CHANGED);
			break;

		case Constants.MESSAGE_RECEIVE_ELEMENT_CONTENT:
			j = getElementContent(sender.getContentModel(), j);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_RECEIVE_ELEMENT_CONTENT);
			break;

		case Constants.MESSAGE_RECEIVE_ELEMENT_TITLE:

			j = getElementTitle(sender.getContentModel(), j);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_RECEIVE_ELEMENT_TITLE);
			break;
			
		case Constants.MESSAGE_RECEIVE_ALL_TITLES:
			j = getElementTitle(sender.getContentModel(), j);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_RECEIVE_ALL_TITLES);
			break;

		default:
			System.err.println("Type de message invalide");
			return null;
		}

		content = j.toString();
		ACLMessage message = new ACLMessage(performatif);
		message.setContent(content);
		message.setSender(sender.getAID());
		message.addReceiver(receiver);
		
		if(receiver == null)
		{
			return null;
		}		
		else{
			return message;
		}

	}
	public static ACLMessage createMessage(ClientAgent sender, AID receiver, int MessageType){
		return createMessage(sender, receiver, MessageType, null);
	}
	public static ACLMessage createMessage(ClientAgent sender, AID receiver, int MessageType , Object params){

		int performatif;
		String content = "message vide";
		JsonObject j = new JsonObject();
		//AID receiver_final = new AID(receiver.toString(), AID.ISGUID);


		switch (MessageType) {
		case Constants.MESSAGE_ACTION_EDIT:
			performatif = ACLMessage.QUERY_IF;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_EDIT);

			break;
		case Constants.MESSAGE_ACTION_STOP_EDIT:
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_STOP_EDIT);

			break;
		case Constants.MESSAGE_ACTION_CREATE_ELEMENT:
			performatif = ACLMessage.QUERY_IF;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_CREATE_ELEMENT);
			j.addProperty(Constants.JSON_AGENT_TYPE, ""+params);
			break;


		case Constants.MESSAGE_ACTION_DELETE:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_DELETE);
			break;

		case Constants.MESSAGE_ACTION_GET_CONTENT:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_GET_CONTENT);	
			break;

		case Constants.MESSAGE_ACTION_GET_TITLE:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_GET_TITLE);
			break;
			
		case Constants.MESSAGE_ACTION_GET_ALL_TITLES :
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_GET_ALL_TITLES);
			break;

		case Constants.MESSAGE_ACTION_SHARE:
			// Quand on veut creer un message de type MESSAGE_ACTION_SHARE : sender et receiver : ClientAgent, dans params : AID de l'element a envoyer
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_SHARE);
			j.addProperty(Constants.JSON_ELEMENT, ((AID)params).getName());
			break;

		case Constants.MESSAGE_ACTION_SAVE_CONTENT: 
			j = getElementContent((ElementModel)params, j);
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, ""+Constants.MESSAGE_ACTION_SAVE_CONTENT);
			break;

		default:
			System.err.println("Type de message invalide");
			return null;
		}

		content = j.toString();
		ACLMessage message = new ACLMessage(performatif);
		message.setContent(content);
		message.setSender(sender.getAID());
		message.addReceiver(receiver);
		if(receiver == null)
		{
			return null;
		}		
		else{
			return message;
		}

	}

	public static JsonObject getElementContent(ElementModel model, JsonObject j){


		int type = model.getType();

		int color = model.getColor();
		String title = model.getTitle();

		j.addProperty("type", type);
		j.addProperty("color", color);
		j.addProperty("title", title);

		if(type==Constants.TYPE_ELEMENT_PICTURE){
			byte[] content = ((PictureElementModel)model).getContent();
			if(content!=null){
				String picture_str = Base64.encodeBytes(content, Base64.NO_OPTIONS);
				j.addProperty(Constants.JSON_CONTENT, picture_str);
			}
		}

		else if(type==Constants.TYPE_ELEMENT_LINK){
			String url = ((LinkElementModel)(model)).getContent();
			j.addProperty(Constants.JSON_CONTENT, url);
		}

		else if(type==Constants.TYPE_ELEMENT_TEXT){
			String text = ((TextElementModel)model).getContent();
			j.addProperty(Constants.JSON_CONTENT, text);
		}

		//TODO
		/*else if(type == Constants.TYPE_ELEMENT_FILE){
			String file_str = new String( ((FileElementModel)(((ElementAgent)sender).getContentModel())).getContent(), "UTF-8");

		}*/

		return j;
	}

	public static JsonObject getElementTitle(ElementModel model, JsonObject j){
		String title = model.getTitle();
		j.addProperty(Constants.JSON_TITLE, title);
		return j;
	}

	public static Integer getAgentType(ACLMessage message){

		JsonParser js = new JsonParser();
		int type = ((JsonObject) js.parse(message.getContent())).get(Constants.JSON_AGENT_TYPE).getAsInt();
		return type;
	}

	public static ElementModel getModel(ACLMessage message){

		JsonParser js = new JsonParser();
		int color = ((JsonObject) js.parse(message.getContent())).get("color").getAsInt();
		String title = ((JsonObject) js.parse(message.getContent())).get("title").getAsString();

		AID agent = message.getSender();
		int type_model = ((JsonObject) js.parse(message.getContent())).get("type").getAsInt();

		ElementModel model = null;
		String content = null;

		JsonElement content_json = ((JsonObject) js.parse(message.getContent())).get("content");
		if(content_json!=null){
			content = content_json.getAsString();

		}

		if(type_model == Constants.TYPE_ELEMENT_PICTURE){
			byte[] image = null;
			if(content!=null){
				image = Base64.decode(content, Base64.NO_OPTIONS);
			}
			model = new PictureElementModel(color, title, agent, image);

		}

		else if (type_model == Constants.TYPE_ELEMENT_FILE){
			// TODO 
			model = new TextElementModel(color, title, agent, content);
		}

		else if(type_model == Constants.TYPE_ELEMENT_LINK){
			model = new LinkElementModel(color, title, agent, content);
		}

		else if(type_model == Constants.TYPE_ELEMENT_TEXT){
			model = new TextElementModel(color, title, agent, content);
		}

		return (ElementModel)model;


	}
	public static Map<AID, String> getTitle(ACLMessage message) {
		Map<AID, String> e = new HashMap<AID, String>();
		JsonParser js = new JsonParser();
		String title = ((JsonObject) js.parse(message.getContent())).get("title").getAsString();

		AID agent = message.getSender();
		e.put(agent, title);
		return e;
	}

}
