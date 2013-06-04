package com.workhub.utils;

import jade.core.AID;

public class Constants {
	public final static int TYPE_ELEMENT_TEXT = 1000;
	public final static int TYPE_ELEMENT_PICTURE = 1001;
	public final static int TYPE_ELEMENT_LINK = 1002;
	public final static int TYPE_ELEMENT_FILE = 1003;
	public static final String ELEMENT_AGENT = "ELEMENT";
	public static final String CLIENT_AGENT = "CLIENT";

	public static final int MESSAGE_ACTION_EDIT = 15000; 
	public static final int MESSAGE_ACTION_GET_CONTENT = 15001;
	public static final int MESSAGE_ACTION_GET_TITLE = 15002;
	public static final int MESSAGE_ACTION_SAVE_CONTENT = 15003;
	public static final int MESSAGE_ACTION_DELETE = 15004;
	public static final int MESSAGE_ACTION_SHARE = 15005;
	public static final int MESSAGE_ACTION_CONTENT= 15006;
	public static final int MESSAGE_RECEIVE_ELEMENT_CONTENT= 15007;
	public static final int MESSAGE_RECEIVE_ELEMENT_TITLE= 15008;
	public static final int MESSAGE_ACTION_IS_DYING = 15009;

	public static final String JSON_ACTION = "action";

	public static final int EVENT_TYPE_SAVE = 16000; //Interface -> agent : Sauve l'element
	public static final int EVENT_TYPE_CHANGE = 16001; //agent -> Interface : l'element a changé
	public static final int EVENT_TYPE_CHARGE = 16002; //Interface -> agent : demande de contenu d'element
	public static final int EVENT_TYPE_CONTENU = 16003; //agent -> interface : contenu de l'element
	public static final int EVENT_TYPE_RECEIPT = 16004; //agent -> interface : il y a un nouveau message


	//		Object[] params = {AID dest, AID elementAgent};
	public static final int EVENT_TYPE_SEND = 16005; //interface -> agent : envoi le message
	public static final int EVENT_TYPE_DELETE = 16006; //interface -> agent : Supprime l'agent element
	public static final int EVENT_TYPE_DIED = 16007; //agent -> interface: L'element est mort, ne l'affiche plus
	public static final int EVENT_TYPE_GET_NEIGHBOURGS= 16008; // interface -> agent : quels sont les voisins a qui je peux envoyer ?

	// param: Map<AID, String> = HashMap<AID agentID, String name>()
	public static final int EVENT_TYPE_NEIGHBOURS= 16009; // agent -> interface : liste des voisins
	public static final int EVENT_TYPE_GET_ELEMENTS= 16010; // interface -> agent : quels sont les elements disponibles

	// param: Map<AID, String> = HashMap<AID agentID, String name>()
	public static final int EVENT_TYPE_ELEMENTS= 16011; // agent -> interface : liste des elements
	public static final int EVENT_TYPE_CREATE_ELEMENT= 16012; // interface -> agent : créer l'element
	
	public static final int EVENT_TYPE_CAN_EDIT = 16013; // agent -> interface : edition possible
	public static final int EVENT_TYPE_CANT_EDIT = 16014;// agent -> interface : edition impossible
	public static final int EVENT_TYPE_ASK_EDIT = 16015; // interface -> agent : puis je editer

	//public static final String JSON_ACTION = "action";
	//public static final String JSON_ACTION = "action";

}
