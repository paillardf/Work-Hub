package com.workhub.mt4j;

import org.mt4j.MTApplication;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

public class WorkHubScene extends AbstractScene {
	private WorkHubButton menuButton;
	private WorkHubButton envoyerButton;
	private WorkHubButton recevoirButton;
	private WorkHubButton masquerButton;
	
	public WorkHubScene(MTApplication mtApplication, String name) throws WorkHubException{
		super(mtApplication, name);
		this.setClearColor(new MTColor(146, 150, 188, 255));
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		menuButton = new WorkHubButton(Constants.BUTTON_ID_MENU, Constants.CORNER_TOP_LEFT, 130, 1000, 40, 40, getMTApplication());
		envoyerButton = new WorkHubButton(Constants.BUTTON_ID_ENVOYER, Constants.CORNER_BOTTOM_RIGHT, 130, 1000, 980, 700, getMTApplication());
		recevoirButton = new WorkHubButton(Constants.BUTTON_ID_RECEVOIR, Constants.CORNER_BOTTOM_LEFT, 130, 1000, 50, 700, getMTApplication());
		masquerButton = new WorkHubButton(Constants.BUTTON_ID_MASQUER, Constants.CORNER_TOP_RIGHT, 130, 1000, 980, 40, getMTApplication());
		masquerButton.setPositionGlobal(new Vector3D(mtApplication.getWidth()-20, -20));
		this.getCanvas().addChild(menuButton);
		this.getCanvas().addChild(masquerButton);
		this.getCanvas().addChild(envoyerButton);
		this.getCanvas().addChild(recevoirButton);

		getCanvas().registerInputProcessor(new TapAndHoldProcessor(mtApplication, 1000));
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
						openContextualMenu(tahe.getLocationOnScreen());
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
	}
	
	public void openContextualMenu(Vector3D location) {
		ContextMenu contextMenu = new ContextMenu(getCanvas(), (int)location.x, (int)location.y, getMTApplication(), Constants.CONTEXT_MAIN_MENU);
		this.getCanvas().addChild(contextMenu);
	}

	@Override
	public void init() {
	}

	@Override
	public void shutDown() {
	}

	public AbstractElementView addElementView(Integer elementId)
			throws WorkHubException {
		switch (elementId) {
		case Constants.ELEMENT_TEXT:
			TextElementView textElement = new TextElementView(200, 200, 200,200, getMTApplication());
			this.getCanvas().addChild(textElement);
			break;
		case Constants.ELEMENT_LINK:
			LinkElementView linkElement = new LinkElementView(200, 200, 200,200, getMTApplication());
			this.getCanvas().addChild(linkElement);
			break;
		case Constants.ELEMENT_IMAGE:
			ImageElementView imageElement = new ImageElementView("Image/defaultImage.jpg", 200, 200, 200, 200, getMTApplication());
			this.getCanvas().addChild(imageElement);
			break;
		case Constants.ELEMENT_FILE:
			FileElementView fileElement = new FileElementView(200, 200, 200, 200, getMTApplication());
			this.getCanvas().addChild(fileElement);
			break;
		default:
			throw new WorkHubException("Type d'�l�ment invalide.");
		}
		return null;
	}
}
