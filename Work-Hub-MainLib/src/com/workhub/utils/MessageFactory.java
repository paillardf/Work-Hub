package com.workhub.utils;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.UnsupportedEncodingException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.workhub.jade.agent.ClientAgent;
import com.workhub.jade.agent.ElementAgent;
import com.workhub.model.ElementModel;
//import com.workhub.model.FileElementModel;
import com.workhub.model.LinkElementModel;
import com.workhub.model.PictureElementModel;
import com.workhub.model.TextElementModel;

public class MessageFactory {	
	
	public static ACLMessage createMessage(ElementAgent sender, AID receiver, int MessageType){
		int performatif;
		String content = "message vide";
		JsonObject j = new JsonObject();


		switch (MessageType) {
		case Constants.MESSAGE_ACTION_EDIT:
			boolean autorization = sender.lockEdit(receiver);
			j.addProperty("can_edit", autorization);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_EDIT);			
			break;
			
		case Constants.MESSAGE_ACTION_CONTENT:
			// annonce que l'element a ete modifie et que le Client (receiver) doit le mettre a jour
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_CONTENT);
			break;
		
		
		case Constants.MESSAGE_RECEIVE_ELEMENT_CONTENT:
			j = getElementContent(sender, j);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_SHARE);
			break;
		
		case Constants.MESSAGE_RECEIVE_ELEMENT_TITLE:
			j = getElementTitle(sender, j);
			performatif = ACLMessage.INFORM;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_SHARE);
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
		return message;
		
	}
	
	public static ACLMessage createMessage(ClientAgent sender, AID receiver, int MessageType){
		
		int performatif;
		String content = "message vide";
		JsonObject j = new JsonObject();
		

		switch (MessageType) {
		case Constants.MESSAGE_ACTION_EDIT:
			performatif = ACLMessage.QUERY_IF;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_EDIT);
			
			break;
			
		
		case Constants.MESSAGE_ACTION_DELETE:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_DELETE);
			break;
		
		case Constants.MESSAGE_ACTION_GET_CONTENT:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_GET_CONTENT);
			
			break;
			
		case Constants.MESSAGE_ACTION_GET_TITLE:
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_GET_TITLE);
			break;
			
		case Constants.MESSAGE_ACTION_SHARE:
			// TODO on envoie a un autre agent client l'indication qu'on partage, on donne id de l'élément
			performatif = ACLMessage.REQUEST;
			j.addProperty(Constants.JSON_ACTION, Constants.MESSAGE_ACTION_SHARE);
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
		return message;
		
	}
	
	public static JsonObject getElementContent(Agent sender, JsonObject j){
		int type = (((ElementAgent)sender).getContentModel()).getType();
		int color = (((ElementAgent)sender).getContentModel()).getColor();
		String title = (((ElementAgent)sender).getContentModel()).getTitle();
		
		j.addProperty("type", type);
		j.addProperty("color", color);
		j.addProperty("title", title);
		
		if(type==Constants.TYPE_ELEMENT_PICTURE){
			try {
				String picture_str = new String( ((PictureElementModel)(((ElementAgent)sender).getContentModel())).getContent(), "UTF-8");
				j.addProperty("content", picture_str);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		else if(type==Constants.TYPE_ELEMENT_LINK){
			String url = ((LinkElementModel)(((ElementAgent)sender).getContentModel())).getContent();
			j.addProperty("content", url);
		}
		
		else if(type==Constants.TYPE_ELEMENT_TEXT){
			String text = ((TextElementModel)(((ElementAgent)sender).getContentModel())).getContent();
			j.addProperty("content", text);
		}
		
		//TODO
		/*else if(type == Constants.TYPE_ELEMENT_FILE){
			String file_str = new String( ((FileElementModel)(((ElementAgent)sender).getContentModel())).getContent(), "UTF-8");
			
		}*/
		
		return j;
	}
	
	public static JsonObject getElementTitle(ElementAgent sender, JsonObject j){
		String title = sender.getContentModel().getTitle();
		j.addProperty("title", title);
		return j;
	}
	
	
	public static ElementModel getModel(ACLMessage message){
		
		JsonParser js = new JsonParser();
		int color = ((JsonObject) js.parse(message.getContent())).get("color").getAsInt();
		String title = ((JsonObject) js.parse(message.getContent())).get("title").getAsString();
		
		AID agent = message.getSender();
		int type_model = ((JsonObject) js.parse(message.getContent())).get("type").getAsInt();
		
		ElementModel model = null;
		
		String content = ((JsonObject) js.parse(message.getContent())).get("content").getAsString();

		
		if(type_model == Constants.TYPE_ELEMENT_PICTURE){
			byte[] image= content.getBytes();
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
	
}
