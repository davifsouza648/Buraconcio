package io.github.buraconcio.Utils;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import io.github.buraconcio.Utils.Constants;

public class SoundManager 
{
    private static SoundManager instance;

    private Map<String, Sound> loopSounds;
    private Map<String, Sound> sounds;
    private Map<String, Music> music;
    private TreeSet<String> loopsTocando;
    private Map<String, Long> loopSoundIds;

    private float masterVolume = 0.7f;
    private String currentMusicID;
    private Music currentMusic;


    private SoundManager()
    {
        instance = this;
        sounds = new HashMap<>();
        music = new HashMap<>();
        loopSounds = new HashMap<>();
        loopsTocando = new TreeSet<>();
        loopSoundIds = new HashMap<>(); 

    }

    public synchronized static SoundManager getInstance() 
    {
        if (instance == null)
            new SoundManager();
        return instance;
    }
    

    public void loadSound(String id, String path) 
    {
        if(sounds.get(id) != null) return;
        sounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void loadLoopSound(String id, String path)
    {
        if(loopSounds.get(id) != null) return;
        loopSounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void loadMusic(String id, String path) 
    {
        if(music.get(id) != null) return;
        Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
        m.setLooping(true);
        music.put(id, m);
    }

    public void loopProximity(String id, Vector2 source, Vector2 listener) {
        Sound sound = loopSounds.get(id);
        if (sound == null) return;

        float dist = source.dst(listener);
        float volume = Math.max(0f, 1f - dist / Constants.MAX_DISTANCE_AUDIBLE) * masterVolume;

        Long soundId = loopSoundIds.get(id);
        
        if (soundId == null) {
            // Se não está tocando, inicia o loop
            long newSoundId = sound.loop(volume);
            loopSoundIds.put(id, newSoundId);
            loopsTocando.add(id);
        } else {
            sound.setVolume(soundId, volume);
        }
    }

    public void playProximity(String id, Vector2 source, Vector2 listener) 
    {
        Sound sound = sounds.get(id);
        if (sound != null) 
        {
            float dist = source.dst(listener);
            float volume = Math.max(0f, 1f - dist / Constants.MAX_DISTANCE_AUDIBLE);
            sound.play(volume * masterVolume);
        }
    }

    public void setMasterVolume(float volume)
    {
        if(volume < 0 || volume > 1) return;
        this.masterVolume = volume;
    }

    public void playSound(String id)
    {
        Sound s = sounds.get(id);
        if(s != null) s.play(masterVolume);
    }

    public void playMusic(String id)
    {
        Music m = music.get(id);
        if (m == null) return;

        if (currentMusicID == id && currentMusic != null) return;
        if(currentMusicID != id && currentMusic != null){currentMusic.stop();}
        currentMusic = m;
        currentMusicID = id;
        m.play();

    }

    public void stopMusic()
    {
        if(currentMusic != null) currentMusic.stop();
    }


}
