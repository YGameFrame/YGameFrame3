package ygame.framework.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public final class YAudioManager
{
	private static final int DEFAULT_MAX_STREAM = 10;

	private final WeakReference<Context> context;
	private final SoundPool soundPool = new SoundPool(DEFAULT_MAX_STREAM,// 同时播放的音效
			AudioManager.STREAM_MUSIC, 0);
	private Map<String, Integer> soundMap = new HashMap<String, Integer>();

	YAudioManager(YSystem system)
	{
		context = new WeakReference<Context>(system.YVIEW.getContext());
	}

	public int playSound(int soundResId, int loopCounts, float rate,
			float volume)
	{
//		AudioManager am = (AudioManager) context.get()
//				.getSystemService(Context.AUDIO_SERVICE);
//		float audioMaxVolum = am
//				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 音效最大值
		// float audioCurrentVolum = am
		// .getStreamVolume(AudioManager.STREAM_MUSIC);
		// float audioRatio = audioCurrentVolum / audioMaxVolum;
		final float audioRatio = volume > 1 ? 1 : volume < 0 ? 0
				: volume;

		Integer soundId = soundMap.get(soundResId + "");
		if (null == soundId)
		{
			soundId = soundPool.load(context.get(), soundResId, 1);
			soundMap.put(soundResId + "", soundId);
		}

		return soundPool.play(soundId, audioRatio,// 左声道音量
				audioRatio,// 右声道音量
				1, // 优先级
				loopCounts,// 循环播放次数
				rate);// 回放速度，该值在0.5-2.0之间 1为正常速度
	}

	public void pauseSound(int streamId)
	{
		soundPool.pause(streamId);
	}
}
