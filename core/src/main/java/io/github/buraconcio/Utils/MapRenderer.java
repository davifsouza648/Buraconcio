package io.github.buraconcio.Utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapRenderer extends OrthogonalTiledMapRenderer
{
    private static float scale = 1/32f;
    private static TiledMap map;
    
    public MapRenderer(String mapName)
    {
        super(map = new TmxMapLoader().load("maps/" + mapName + "/" + mapName + ".tmx"), scale);
    }



    
}
