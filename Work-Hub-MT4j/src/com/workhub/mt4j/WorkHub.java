package com.workhub.mt4j;

import org.mt4j.MTApplication;

public class WorkHub extends MTApplication {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3337824151386218881L;

	public static void main(String[] args) {
		System.out.println("pouet");
		initialize();
	}
 
	@Override
	public void startUp() {
		addScene(new WorkHubScene(this, "WorkHub"));
	}
}
