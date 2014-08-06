package com.yoavst.quickapps.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.media.RemoteController.MetadataEditor;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity
public class RemoteControlActivity extends BaseQuickCircleActivity {

	//Views in the Activity
	protected ImageButton mPlayPauseButton;
	protected TextView mArtistText;
	protected TextView mTitleText;
	protected ImageView mArtwork;
	protected Bitmap mImageBitmap;
	protected boolean mRegistered = true;

	protected RemoteControlService mRCService;
	protected boolean mBound = false; //flag indicating if service is bound to Activity
	protected boolean mIsPlaying = false; //flag indicating if music is playing

	private RemoteController.OnClientUpdateListener mClientUpdateListener = new RemoteController.OnClientUpdateListener() {
		@Override
		public void onClientTransportControlUpdate(int transportControlFlags) {
			//Do nothing
		}

		@Override
		public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
			switch (state) {
				case RemoteControlClient.PLAYSTATE_PLAYING:
					//if music started playing, we should start updating the play/pause icon
					mIsPlaying = true;
					mPlayPauseButton.setImageResource(R.drawable.music_player_cover_btn_paused);
					break;
				default:
					mIsPlaying = false;
					mPlayPauseButton.setImageResource(R.drawable.music_player_cover_btn_play);
					break;
			}
		}

		@Override
		public void onClientPlaybackStateUpdate(int state) {
			switch (state) {
				case RemoteControlClient.PLAYSTATE_PLAYING:
					mIsPlaying = true;
					mPlayPauseButton.setImageResource(R.drawable.music_player_cover_btn_paused);
					break;
				default:
					mIsPlaying = false;
					mPlayPauseButton.setImageResource(R.drawable.music_player_cover_btn_play);
					break;
			}
		}

		@Override
		public void onClientMetadataUpdate(MetadataEditor editor) {
			//some players write artist name to METADATA_KEY_ALBUMARTIST instead of METADATA_KEY_ARTIST, so we should double-check it
			mArtistText.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
					editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, getString(R.string.unknown))
			));
			mTitleText.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, getString(R.string.unknown)));
			Bitmap bitmap = editor.getBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, null);
			if (bitmap != null) {
				mArtwork.setImageBitmap(bitmap);
				if (mImageBitmap != null) {
					mImageBitmap.recycle();
					mImageBitmap = null;
				}
				mImageBitmap = bitmap;
			} else mArtwork.setImageResource(R.drawable.ic_music_empty);
		}

		@Override
		public void onClientChange(boolean clearing) {
			if (clearing) {
				mArtwork.setImageBitmap(null);
				mArtistText.setText(R.string.unknown);
				mTitleText.setText(R.string.unknown);
			}
		}
	};

	@Click(R.id.prev_button)
	void prevClicked() {
		if (mBound) {
			mRCService.sendPreviousKey();
		}
	}

	@Click(R.id.next_button)
	void nextClicked() {
		if (mBound) {
			mRCService.sendNextKey();
		}
	}

	@Click(R.id.play_pause_button)
	void playOrPauseClicked() {
		if (mBound) {
			if (mIsPlaying) {
				mRCService.sendPauseKey();
			} else {
				mRCService.sendPlayKey();
			}
		}
	}

	@Click(R.id.volume_control)
	void onVolumeTouched() {
		((AudioManager) getSystemService(Context.AUDIO_SERVICE)).adjustStreamVolume(3, 0, 1);
	}

	@Click(R.id.back_btn)
	void onBackClicked() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
		mTitleText = (TextView) findViewById(R.id.title_text);
		mTitleText.setSelected(true);
		mArtistText = (TextView) findViewById(R.id.artist_text);
		mArtistText.setSelected(true);
		mArtwork = (ImageView) findViewById(R.id.album_image);
	}

	@Override
	protected Intent getIntentForOpenCase() {
		if (mRegistered) return mIsPlaying ? mRCService.getCurrentClientIntent() : null;
		else return new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
	}

	@Override
	protected int getLayoutId() {
		return R.layout.music_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent("com.yoavst.quickmusic.BIND_RC_CONTROL_SERVICE");
		try {
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		} catch (RuntimeException e) {
			showUnregistered();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mBound) {
			mRCService.setRemoteControllerDisabled();
		}
		unbindService(mConnection);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			//Getting the binder and activating RemoteController instantly
			RemoteControlService.RCBinder binder = (RemoteControlService.RCBinder) service;
			mRCService = binder.getService();
			if (!mRCService.setRemoteControllerEnabled()) {
				// Not registered on the settings
				showUnregistered();
			}
			mRCService.setClientUpdateListener(mClientUpdateListener);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};

	void showUnregistered() {
		mArtistText.setText(R.string.register_us_please);
		mTitleText.setText(R.string.open_the_case);
		mRegistered = false;
	}
}
