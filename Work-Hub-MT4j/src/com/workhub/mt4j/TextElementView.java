package com.workhub.mt4j;

import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

public class TextElementView extends AbstractElementView {
	private MTTextArea content;

	public TextElementView(PApplet pApplet, Vertex[] vertices) {
		super(pApplet, vertices);		
		content = new MTTextArea(pApplet, FontManager.getInstance().createFont(
				pApplet, "arial.ttf", 18, new MTColor(50, 50, 50, 255),
				new MTColor(0, 0, 0, 255)));
		content.setNoFill(true);
		content.setText("Ajoutez votre texte ici");
		content.setPickable(false);
		content.setNoStroke(true);
		content.setPositionRelativeToParent(new Vector3D(300, 260));
		addChild(content);
	}

	public MTTextArea getContent() {
		return content;
	}

	public void setContent(MTTextArea content) {
		this.content = content;
	}
}
