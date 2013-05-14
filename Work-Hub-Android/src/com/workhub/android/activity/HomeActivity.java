package com.workhub.android.activity;

import jade.android.AgentContainerHandler;
import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.gui.GuiAgent;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.workhub.android.R;
import com.workhub.android.scene.MainScene;
import com.workhub.android.utils.Constants;
import com.workhub.android.utils.Ressources;
import com.workhub.jade.agent.ClientAgent;
import com.workhub.jade.agent.ClientAgentInterface;

public class HomeActivity extends SimpleLayoutGameActivity {

	private Camera mCamera;
	private Ressources res;
	private ServiceConnection serviceConnection;
	private MicroRuntimeServiceBinder microRuntimeServiceBinder;

	private ClientAgentInterface myAgent;
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		
		
		super.onCreate(pSavedInstanceState);
		
		startJade("Florian", AndroidHelper.getLocalIPAddress(), "1099",agentStartupCallback );
		
	}
	
	private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
		@Override
		public void onSuccess(AgentController agent) {
		}

		@Override
		public void onFailure(Throwable throwable) {
			System.out.println("Nickname already in use!");
		}
	};

	public void startJade(final String nickname, final String host,
			final String port,
			final RuntimeCallback<AgentController> agentStartupCallback) {

		final Properties profile = new Properties();
		profile.setProperty(Profile.MAIN_HOST, host);
	    profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.TRUE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		if (AndroidHelper.isEmulator()) {
			// Emulator: this is needed to work with emulated devices
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
		// Emulator: this is not really needed on a real device
		profile.setProperty(Profile.LOCAL_PORT, "1099");

		if (microRuntimeServiceBinder == null) {
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
					System.out.println("Gateway successfully bound to MicroRuntimeService");
					startContainer(nickname, profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
					System.out.println("Gateway unbound from MicroRuntimeService");
				}
			};
			System.out.println( "Binding Gateway to MicroRuntimeService...");
			bindService(new Intent(getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			System.out.println( "MicroRumtimeGateway already binded to service");
			startContainer(nickname, profile, agentStartupCallback);
		}
	}

	private void startContainer(final String nickname, final Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		
		if (!MicroRuntime.isRunning()) {
			RuntimeService runtimeService = new RuntimeService();
			runtimeService.createMainAgentContainer(new RuntimeCallback<AgentContainerHandler>() {
				
				@Override
				public void onSuccess(AgentContainerHandler arg0) {
					System.out.println("Successfully start of the container...");
					startAgent(nickname, agentStartupCallback);
					microRuntimeServiceBinder.startAgentContainer(profile, 
							new RuntimeCallback<Void>() {
								@Override
								public void onSuccess(Void thisIsNull) {
									System.out.println("Successfully start of the container...");
									startAgent(nickname, agentStartupCallback);
								}

								@Override
								public void onFailure(Throwable throwable) {
									System.out.println( "Failed to start the container...");
								}
							});
					
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
		} else {
			startAgent(nickname, agentStartupCallback);
		}
	}

	private void startAgent(final String nickname,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		microRuntimeServiceBinder.startAgent(nickname,
				ClientAgent.class.getName(),
				new Object[] { this },
				new RuntimeCallback<Void>() {
					@Override
					public void onSuccess(Void thisIsNull) {
						System.out.println("Successfully start of the "
								+ ClientAgent.class.getName() + "...");
						try {
							myAgent = (ClientAgentInterface) MicroRuntime.getAgent(nickname).getO2AInterface(ClientAgentInterface.class);
						} catch (ControllerException e) {
							agentStartupCallback.onFailure(e); 
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						System.out.println("Failed to start the "
								+ ClientAgent.class.getName() + "...");
						agentStartupCallback.onFailure(throwable);
					}
				});
	}
	

	
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		int CAMERA_LARGEUR = getResources().getDisplayMetrics().widthPixels;
		int CAMERA_HAUTEUR = getResources().getDisplayMetrics().heightPixels;

		int resolutionX= (int) (Constants.SCREEN_WIDTH);
		int resolutionY= (int) (Constants.SCREEN_HEIGHT);
		float x = Math.min(((float)resolutionX/CAMERA_LARGEUR), ((float)resolutionY/CAMERA_HAUTEUR));
		resolutionX = (int) (CAMERA_LARGEUR*x);
		resolutionY = (int) (CAMERA_HAUTEUR*x);
		mCamera = new Camera(0, 0, resolutionX, resolutionY);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(CAMERA_LARGEUR, CAMERA_HAUTEUR), mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {

		res = new Ressources(this, mCamera);
	}

	@Override
	protected Scene onCreateScene() {

		MainScene scene = new MainScene(res); 
		scene.populate();
		return scene;
	}

	@Override
	protected int getLayoutID() {
		return R.layout.game_activity;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.layout_rendersurfaceview;
	}


}
