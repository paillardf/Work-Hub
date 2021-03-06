package com.workhub.android.activity;

import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.core.AID;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.gui.GuiEvent;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.widget.Toast;

import com.workhub.android.R;
import com.workhub.android.element.AbstractElement;
import com.workhub.android.element.BaseElement;
import com.workhub.android.element.PictureElement;
import com.workhub.android.scene.MainScene;
import com.workhub.android.utils.ConstantsAndroid;
import com.workhub.android.utils.Ressources;
import com.workhub.android.utils.SettingsManager;
import com.workhub.jade.agent.ClientAgent;
import com.workhub.jade.agent.ClientAgentInterface;
import com.workhub.jade.agent.CreatorAgent;
import com.workhub.model.ElementModel;
import com.workhub.utils.Constants;
import com.workhub.utils.PDFUtils;

public class HomeActivity extends SimpleLayoutGameActivity implements PropertyChangeListener{

	private Camera mCamera;
	private Ressources res;
	private ServiceConnection serviceConnection;
	private MicroRuntimeServiceBinder microRuntimeServiceBinder;

	private List<AID> mailBox = new ArrayList<AID>();
	private String nickname;
	private AbstractElement askElement;
	private MainScene scene;

	
	
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {


		super.onCreate(pSavedInstanceState);
		nickname = SettingsManager.getInstance(getApplicationContext()).getNickname();
		startJade(nickname, SettingsManager.getInstance(getApplicationContext()).getHost(), "1099" );


		//		startJade(nickname, "192.168.43.67", "1099" );
		//	startJade(nickname, "192.168.1.50", "1099" );

	}

	public void startJade(final String nickname, final String host,
			final String port) {

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
					startContainer(nickname, profile);
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
			startContainer(nickname, profile);
		}
	}
	private void startContainer(final String nickname, final Properties profile) {

		if (!MicroRuntime.isRunning()) {
			final RuntimeService runtimeService = new RuntimeService();
			runtimeService.createMainAgentContainer(new RuntimeCallback<AgentContainerHandler>() {

				@Override
				public void onSuccess(AgentContainerHandler arg0) {
					System.out.println("Successfully start of the container...");
					runtimeService.createNewAgent(arg0, "CreatorAgent", CreatorAgent.class.getName(), null, new RuntimeCallback<AgentHandler>() {
						@Override
						public void onSuccess(AgentHandler agentHandler) {
							runtimeService.startAgent(agentHandler, new RuntimeCallback<Void>() {

								@Override
								public void onFailure(Throwable arg0) {
									System.err.println( "Failed to start the CreatorAgent...");
								}

								@Override
								public void onSuccess(Void arg0) {
									System.out.println("Successfully start of the CreatorAgent...");
								}
							});

						}

						@Override
						public void onFailure(Throwable throwable) {
							System.err.println( "Failed to start the CreatorAgent...");
						} });



					microRuntimeServiceBinder.startAgentContainer(profile, 
							new RuntimeCallback<Void>() {
						@Override
						public void onSuccess(Void thisIsNull) {
							System.out.println("Successfully start of the container...");

							startAgent(nickname);
						}

						@Override
						public void onFailure(Throwable throwable) {
							System.err.println( "Failed to start the container...");
						}
					});

				}

				@Override
				public void onFailure(Throwable arg0) {
				}
			});

		} else {
			startAgent(nickname);
		}
	}


	private void startAgent(final String nickname) {
		microRuntimeServiceBinder.startAgent(nickname,
				ClientAgent.class.getName(),
				new Object[] { this },
				new RuntimeCallback<Void>() {
			@Override
			public void onSuccess(Void thisIsNull) {
				System.out.println("Successfully start of the "
						+ ClientAgent.class.getName() + "...");
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Connexion réussie", Toast.LENGTH_SHORT).show();	

					}
				});

			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println("Failed to start the "
						+ ClientAgent.class.getName() + "...");
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Connexion échouée", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}
	
	private ClientAgentInterface getAgent() throws StaleProxyException, ControllerException{
		return  (ClientAgentInterface) MicroRuntime.getAgent(nickname).getO2AInterface(ClientAgentInterface.class);
	}

	private void fireOnGuiEvent(GuiEvent event) {
		try {
			getAgent().fireOnGuiEvent(event);
			return;
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Problème de connection", Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        quitter();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	void quitter(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Etes vous sûr de vouloir quitter?")
		       .setCancelable(false)
		       .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //TODO clean jade
		        	   HomeActivity.this.finish();
		        	   if(microRuntimeServiceBinder!=null){
		        		   microRuntimeServiceBinder.stopAgentContainer(new RuntimeCallback<Void>() {
								@Override
								public void onSuccess(Void thisIsNull) {
									System.out.println("Successfully stop of the container...");
								}

								@Override
								public void onFailure(Throwable throwable) {
									System.err.println( "Failed to stop the container...");
								}
							});
		        	   }
		                dialog.dismiss();
		                
		           }
		       })
		       .setNegativeButton("Non", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}



	@Override
	public EngineOptions onCreateEngineOptions() {
		int CAMERA_LARGEUR = getResources().getDisplayMetrics().widthPixels;
		int CAMERA_HAUTEUR = getResources().getDisplayMetrics().heightPixels;

		int resolutionX= (int) (CAMERA_LARGEUR);
		int resolutionY= (int) (CAMERA_HAUTEUR);
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

		scene = new MainScene(res); 
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

	public void loadImage(AbstractElement e, int requestCode) {
		this.askElement = e;
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, requestCode);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == PictureElement.SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				String imgPath;
				if(selectedImageUri.toString().startsWith("file://")) {
					imgPath = selectedImageUri.toString().replaceAll("file://", "");
				} else {
					imgPath = getPath(selectedImageUri);
				}
				((PictureElement)askElement).onActivityResult(imgPath);

			}
		}
		if(askElement!=null){
			askElement = null;
		}
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public void sendElement(AID dest, AID elementAgent){
		GuiEvent event = new GuiEvent(null,Constants.EVENT_TYPE_SEND);
		event.addParameter(dest);
		event.addParameter(elementAgent);
		fireOnGuiEvent(event);
	}

	public void receive(){
		if(mailBox.size()>0){
			AID elementAID = mailBox.remove(0);

			BaseElement element = scene.getElement(elementAID);
			if(element!=null){
				element.moveTo(res.getScreenCenter().x, res.getScreenCenter().y);
			}else{
				getElement(elementAID);
			}
			scene.notifyReceiveShorcut(mailBox.size());
		}else{
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Vous n'avez pas de message", Toast.LENGTH_SHORT).show();	
					
				}
			});
					

		}
	}
	public void askEdition(AID elementAgent){
		GuiEvent event = new GuiEvent(null,Constants.EVENT_TYPE_ASK_EDIT);
		event.addParameter(elementAgent);
		fireOnGuiEvent(event);
	}


	public void createElement(int elementType){
		GuiEvent event = new GuiEvent(null,Constants.EVENT_TYPE_CREATE_ELEMENT);

		event.addParameter(elementType);
		fireOnGuiEvent(event);
	}

	public void deleteElement(AID elementAgent){
		GuiEvent event = new GuiEvent(null,Constants.EVENT_TYPE_DELETE);
		event.addParameter(elementAgent);
		fireOnGuiEvent(event);
	}

	public void saveElement(ElementModel model){
		GuiEvent event = new GuiEvent(null, Constants.EVENT_TYPE_SAVE);
		event.addParameter(model);
		fireOnGuiEvent(event);
	}
	
	public void stopEditing(AID elementAgent) {
		GuiEvent event = new GuiEvent(null, Constants.EVENT_TYPE_STOP_EDIT);
		event.addParameter(elementAgent);
		fireOnGuiEvent(event);
		
	}

	public void getElement(AID agentAID ){
		GuiEvent event = new GuiEvent(null, Constants.EVENT_TYPE_CHARGE);
		event.addParameter(agentAID);
		fireOnGuiEvent(event);
	}

	public void getElementList(){
		GuiEvent event = new GuiEvent(null, Constants.EVENT_TYPE_GET_ELEMENTS);
		fireOnGuiEvent(event);
	}

	public void getNeightbourgList(){
		GuiEvent event = new GuiEvent(null, Constants.EVENT_TYPE_GET_NEIGHBOURGS);
		fireOnGuiEvent(event);
	}

	public void export(List<ElementModel> models){
		FileOutputStream outf;
		try {
			File f = new File(ConstantsAndroid.EXT_PATH_FILES);
			f.mkdirs();


			Locale locale = Locale.getDefault();
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);

			f = new File(ConstantsAndroid.EXT_PATH_FILES+"export "+dateFormat.format(new Date())+".pdf");
			f.createNewFile();
			outf = new FileOutputStream(f);
			PDFUtils.createPDF("WorkHub export", nickname, models, outf);		

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			sendIntent.setType("image/*");
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Réunion du "+dateFormat.format(new Date()));
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Export pdf du hub de "+dateFormat.format(new Date()) + " en pièce jointe");
			Uri uri = Uri.fromFile((f));
			sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

			//sendIntent.setType("text/plain");
			startActivity(sendIntent);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		switch ((Integer.parseInt(event.getPropertyName()))) {
		case Constants.EVENT_TYPE_CHANGE:
		{
			AID aidModel = (AID)event.getNewValue();
			BaseElement element = scene.getElement(aidModel);
			if(element!=null){
				getElement(aidModel);
			}
			break;  
		}
		case Constants.EVENT_TYPE_CONTENU:
		{
			ElementModel model = (ElementModel)event.getNewValue();
			BaseElement element = scene.getElement(model.getAgent());
			if(element!=null){
				element.setModel(model);
			}else{
				try {
					scene.addElement(model);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		case Constants.EVENT_TYPE_DIED:
		{
			AID agent = (AID)event.getNewValue();
			BaseElement element = scene.getElement(agent);
			if(element!=null){
				element.remove();
			}
			break;
		}
		case Constants.EVENT_TYPE_ELEMENTS: 
		{
			Map<AID, String> map = (Map<AID, String>)event.getNewValue();

			for (Entry<AID, String> entry : map.entrySet()) {
				scene.addToAdapter(entry);
			}


			break;
		}
		case Constants.EVENT_TYPE_NEIGHBOURS:
		{
			Map<AID, String> map = (Map<AID, String>)event.getNewValue();
			for (Entry<AID, String> entry : map.entrySet()) {
				scene.addToAdapter(entry);
			}

			break;
		}
		case Constants.EVENT_TYPE_CAN_EDIT:
		{
			AID aidModel = (AID)event.getNewValue();
			BaseElement element = scene.getElement(aidModel);
			if(element!=null){
				element.edit();
			}

			break;
		}
		case Constants.EVENT_TYPE_CANT_EDIT:
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Vous ne pouvez pas éditer l'élément", Toast.LENGTH_SHORT).show();	
				}
			});
			break;
		}
		case Constants.EVENT_TYPE_RECEIVE_ELEMENT:
		{
			AID aidModel = (AID)event.getNewValue();
			mailBox.add(aidModel);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Vous avez "+mailBox.size()+" message(s)", Toast.LENGTH_SHORT).show();

				}
			});

			scene.notifyReceiveShorcut(mailBox.size());
			break;
		}
		}

	}
	

}
