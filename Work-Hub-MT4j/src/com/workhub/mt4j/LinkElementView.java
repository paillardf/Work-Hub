package com.workhub.mt4j;

import java.io.IOException;

import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;

import processing.core.PApplet;

public class LinkElementView extends TextElementView{
	public LinkElementView(float x, float y, float width,
			float height, PApplet applet) {
		super(x, y, width, height, applet);
		content.setFont(FontManager.getInstance().createFont(
				applet, "arial.ttf", 18, new MTColor(50, 50, 50, 255),
				MTColor.BLUE));
		content.setText("http://www.google.com");
		
		registerInputProcessor(new TapProcessor(applet, 25, true, 350));
		addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isDoubleTap()){
					try {
						java.awt.Desktop.getDesktop().browse(java.net.URI.create(content.getText()));
					} catch (IOException e) {
						try {
							java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://"+content.getText()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
				return false;
			}
		});
	}
}
