package com.workhub.mt4j;

import java.util.Map;

import jade.core.AID;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import com.workhub.model.ElementModel;
import processing.core.PImage;

public class WorkHubScene extends AbstractScene {
	private WorkHubButton menuButton;
	private WorkHubButton envoyerButton;
	private WorkHubButton recevoirButton;
	private WorkHubButton masquerButton;
	private MTImage imageFond;
	
	public WorkHubScene(MTApplication mtApplication, String name) throws WorkHubException{
		super(mtApplication, name);
		this.setClearColor(new MTColor(198, 200, 200, 255));
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		PImage image = mtApplication.loadImage("Image/logoWH.png");
		imageFond = new MTImage(image, mtApplication);
		imageFond.setNoFill(true);
		imageFond.setNoStroke(true);
		imageFond.setPickable(false);
		imageFond.setAnchor(PositionAnchor.CENTER);
		imageFond.setPositionGlobal(new Vector3D(mtApplication.getWidth()/2f, mtApplication.getHeight()/2f));
		imageFond.getImage().setNoStroke(true);
		getCanvas().addChild(imageFond);
		
		menuButton = new WorkHubButton(MT4JConstants.BUTTON_ID_MENU, MT4JConstants.CORNER_TOP_LEFT, 130, 1000, 40, 40, getMTApplication(), this);
		envoyerButton = new WorkHubButton(MT4JConstants.BUTTON_ID_ENVOYER, MT4JConstants.CORNER_BOTTOM_RIGHT, 130, 1000, 980, 700, getMTApplication(), this);
		recevoirButton = new WorkHubButton(MT4JConstants.BUTTON_ID_RECEVOIR, MT4JConstants.CORNER_BOTTOM_LEFT, 130, 1000, 50, 700, getMTApplication(), this);
		masquerButton = new WorkHubButton(MT4JConstants.BUTTON_ID_MASQUER, MT4JConstants.CORNER_TOP_RIGHT, 130, 1000, 980, 40, getMTApplication(), this);
		masquerButton.setPositionGlobal(new Vector3D(mtApplication.getWidth()-20, -20));
		this.getCanvas().addChild(menuButton);
		this.getCanvas().addChild(masquerButton);
		this.getCanvas().addChild(envoyerButton);
		this.getCanvas().addChild(recevoirButton);

		getCanvas().registerInputProcessor(new TapAndHoldProcessor(mtApplication, 700));
		getCanvas().addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(mtApplication, getCanvas()));
		getCanvas().addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapAndHoldEvent tahe = (TapAndHoldEvent)ge;
				switch (tahe.getId()) {
				case TapAndHoldEvent.GESTURE_DETECTED:
					break;
				case TapAndHoldEvent.GESTURE_UPDATED:
					break;
				case TapAndHoldEvent.GESTURE_ENDED:
					if (tahe.isHoldComplete()){
						openContextualMenu(tahe.getLocationOnScreen(), MT4JConstants.CONTEXT_BACKGROUND_MENU);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		LassoProcessor lassoProcessor = new LassoProcessor(mtApplication, getCanvas(), getSceneCam());
		getCanvas().registerInputProcessor(lassoProcessor);
		getCanvas().addGestureListener(LassoProcessor.class, new LassoAction(mtApplication, this, getCanvas().getClusterManager(), getCanvas()));
	}
	
	public void openContextualMenu(Vector3D location, int menuType) {
		ContextMenu contextMenu = new ContextMenu(getCanvas(), (int)location.x, (int)location.y, getMTApplication(), this, menuType);
		this.getCanvas().addChild(contextMenu);
	}
	
	public void openContextualMenu(Vector3D location, WorkHubButton source) {
		ContextMenu contextMenu = new ContextMenu(source, (int)location.x, (int)location.y, getMTApplication(), this, MT4JConstants.CONTEXT_SHORTCUT_MENU);
		this.getCanvas().addChild(contextMenu);
	}

	// Utilise pour traiter EVENT_TYPE_ELEMENTS
	public void openContextualMenu(Map<AID, String> map) {
		Vector3D location = MT4JUtils.removeBeginning(ContextMenu.elementViewLocation);
		ContextMenu contextMenu = new ContextMenu(getCanvas(), (int)location.x, (int)location.y, getMTApplication(), this, map);
		this.getCanvas().addChild(contextMenu);
	}
	
	@Override
	public void init() {
	}

	@Override
	public void shutDown() {
	}
	
	public AbstractElementView getElement(AID aid) {
		for(MTComponent comp : getCanvas().getChildren()) {
			if(comp instanceof AbstractElementView) {
				AbstractElementView elt = (AbstractElementView)comp;
				if(elt.getModel() != null && elt.getModel().getAgent().equals(aid)) {
					return elt;
				}
			}
		}
		return null;
	}
	
	// Ajoute le modele au premier element qui correspond.
	public void attachModel(ElementModel model) {
		int type = model.getType();
		MTComponent[] children = getCanvas().getChildren();
		boolean found = false;
		for(int i = 0 ; i < children.length && !found ; i++) {
			if(children[i] instanceof AbstractElementView) {
				AbstractElementView element = (AbstractElementView)children[i];
				if(element.getType() == type && element.getModel() == null) {
					element.setModel(model);
					found = true;
				}
			}
		}
	}
}
