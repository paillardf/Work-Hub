package com.workhub.mt4j;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
/**
 * This class represents the buttons displayed in the corners
 * We use a MTRoundRectangle which has its width = height and arcHeight = arcWidth = width /2 to build a circle
 * This is necessary in order to have a round button
 *
 */
public class WorkHubButton extends MTRoundRectangle {
	private MTApplication mtApplication;
	private MTTextArea buttonText;
	private WorkHubScene scene;

	public WorkHubButton(String text, int corner, int rayon, int segments,
			int textXPos, int textYPos, MTApplication mtApplication, WorkHubScene scene) {
		super(getXPositionFromCorner(corner, mtApplication, rayon),
				getYPositionFromCorner(corner, mtApplication, rayon), MT4JConstants.Z_POSITION_DEFAULT_BUTTON,
				rayon * 2, rayon * 2, rayon, rayon, segments, mtApplication);
		this.mtApplication = mtApplication;
		this.scene = scene;

		buttonText = new MTTextArea(getMtApplication(), FontManager
				.getInstance().createFont(getMtApplication(), "arial.ttf", 20,
						new MTColor(0, 0, 0, 255),
						new MTColor(0, 0, 0, 255)));
		buttonText.setNoFill(true);
		buttonText.setPickable(false);
		buttonText.setText(text);
		buttonText.setNoStroke(true);
		buttonText.setPositionRelativeToParent(new Vector3D(textXPos, textYPos));
		addChild(buttonText);

		setFillColor(new MTColor(110, 200, 240, 255));
		setStrokeColor(new MTColor(110, 170, 200, 255));		

		unregisterAllInputProcessors();
		removeAllGestureEventListeners();
		assignActions(mtApplication, text);
	}

	private void assignActions(final MTApplication mtApplication, String text) {
		registerInputProcessor(new TapAndHoldProcessor(mtApplication, 700));
		addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(mtApplication, this));
		addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapAndHoldEvent tahe = (TapAndHoldEvent) ge;
				switch (tahe.getId()) {
				case TapAndHoldEvent.GESTURE_DETECTED:
					break;
				case TapAndHoldEvent.GESTURE_UPDATED:
					break;
				case TapAndHoldEvent.GESTURE_ENDED:
					switch (buttonText.getText()) {
					case MT4JConstants.BUTTON_ID_MENU:
						scene.openContextualMenu(tahe.getLocationOnScreen(), MT4JConstants.CONTEXT_MAIN_MENU);
						break;
					case MT4JConstants.BUTTON_ID_ENVOYER:
						if(tahe.isHoldComplete()) {
							scene.openContextualMenu(tahe.getLocationOnScreen(), WorkHubButton.this);
						}
						break;
					case MT4JConstants.BUTTON_ID_RECEVOIR:
						if(tahe.isHoldComplete()) {
							scene.openContextualMenu(tahe.getLocationOnScreen(), WorkHubButton.this);
						}
						else if(tahe.getElapsedTime() < 300 && JadeInterface.getInstance().hasMessages()){
							if(scene.getElement(JadeInterface.getInstance().getLastMessageAID()) == null) {
								ContextMenu.importLocation.add(tahe.getLocationOnScreen());
								JadeInterface.getInstance().receiveElement();
							}
							else {
								// La vue est deja presente
								JadeInterface.getInstance().removeLastMessage();
							}
						}
						break;
					case MT4JConstants.BUTTON_ID_MASQUER:
						if(tahe.isHoldComplete()) {
							scene.openContextualMenu(tahe.getLocationOnScreen(), WorkHubButton.this);
						}
						break;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	public static int getXPositionFromCorner(int corner, MTApplication mtApplication, int rayon) {
		int x = ((corner & 0x01) == 0) ? -rayon : mtApplication.getWidth()-rayon;
		return x;
	}
	public static int getYPositionFromCorner(int corner, MTApplication mtApplication, int rayon) {
		int y = ((corner & 0x10) == 0) ? -rayon : mtApplication.getHeight()-rayon;
		return y;
	}

	public MTApplication getMtApplication() {
		return mtApplication;
	}

	public void setMtApplication(MTApplication mtApplication) {
		this.mtApplication = mtApplication;
	}

	public String getText() {
		return buttonText.getText();
	}
}
