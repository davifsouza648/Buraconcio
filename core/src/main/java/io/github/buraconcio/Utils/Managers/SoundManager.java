package io.github.buraconcio.Utils.Managers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import io.github.buraconcio.Utils.Common.Constants;

public class SoundManager {
    private static SoundManager instance;

    private Map<String, Sound> loopSounds;
    private Map<String, Sound> sounds;
    private Map<String, Music> music;

    private Map<String, Long> loopSoundIds;
    private Map<String, Long> soundsIds;
    private TreeSet<String> loopsTocando;

    private float masterVolumeSounds = 0f;
    private float masterVolumeMusic = 0f;

    private String currentMusicID;
    private Music currentMusic;

    private SoundManager()
    {
        instance = this;
        sounds = new HashMap<>();
        loopSounds = new HashMap<>();
        music = new HashMap<>();
        loopSoundIds = new HashMap<>();
        soundsIds = new HashMap<>();
        loopsTocando = new TreeSet<>();
    }

    public synchronized static SoundManager getInstance()
    {
        if (instance == null)
            new SoundManager();
        return instance;
    }

    public void loadSound(String id, String path)
    {
        if (!sounds.containsKey(id))
            sounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void loadLoopSound(String id, String path)
    {
        if (!loopSounds.containsKey(id))
            loopSounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void loadMusic(String id, String path)
    {
        if (!music.containsKey(id))
        {
            Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
            m.setLooping(true);
            music.put(id, m);
        }
    }

    public void loopProximity(String id, Vector2 source, Vector2 listener)
    {
        Sound sound = loopSounds.get(id);
        if (sound == null) return;

        float dist = source.dst(listener);
        float volume = Math.max(0f, 1f - dist / Constants.MAX_DISTANCE_AUDIBLE) * masterVolumeSounds;

        Long soundId = loopSoundIds.get(id);
        if (soundId == null)
        {
            long newSoundId = sound.loop(volume);
            loopSoundIds.put(id, newSoundId);
            loopsTocando.add(id);
        }
        else
        {
            sound.setVolume(soundId, volume);
        }
    }

    public void playProximity(String id, Vector2 source, Vector2 listener)
    {
        Sound sound = sounds.get(id);
        if (sound == null) return;

        float dist = source.dst(listener);
        float volume = Math.max(0f, 1f - dist / Constants.MAX_DISTANCE_AUDIBLE) * masterVolumeSounds;

        long soundId = sound.play(volume);
        soundsIds.put(id, soundId);
    }

    public void setMasterVolumeSounds(float volume)
    {
        if (volume < 0 || volume > 1) return;
        this.masterVolumeSounds = volume;

        for (Map.Entry<String, Long> entry : loopSoundIds.entrySet())
        {
            Sound s = loopSounds.get(entry.getKey());
            if (s != null)
                s.setVolume(entry.getValue(), volume);
        }

        for (Map.Entry<String, Long> entry : soundsIds.entrySet())
        {
            Sound s = sounds.get(entry.getKey());
            if (s != null)
                s.setVolume(entry.getValue(), volume);
        }

    }

    public void setMasterVolumeMusic(float volume)
    {
        this.masterVolumeMusic = volume;

        for (Music m : music.values())
        {
            m.setVolume(volume);
        }
    }

    public float getMasterVolumeSounds()
    {
        return this.masterVolumeSounds;
    }

    public float getMasterVolumeMusic()
    {
        return this.masterVolumeMusic;
    }

    public void playSound(String id)
    {
        Sound s = sounds.get(id);
        if (s != null)
        {
            long soundId = s.play(masterVolumeSounds);
            soundsIds.put(id, soundId);
        }
    }

    public void playMusic(String id)
    {
        Music m = music.get(id);
        if (m == null) return;

        if (id.equals(currentMusicID) && currentMusic != null) return;

        if (!id.equals(currentMusicID) && currentMusic != null)
        {
            currentMusic.stop();
        }

        currentMusic = m;
        currentMusicID = id;
        m.setVolume(masterVolumeMusic);
        m.play();
    }

    public void stopMusic()
    {
        if (currentMusic != null)
        {
            currentMusic.stop();
            currentMusic = null;
        }
    }

}
