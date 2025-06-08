package io.github.buraconcio.Utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Objects.Flag;

public class MapRenderer extends OrthogonalTiledMapRenderer
{
    private static float scale = 1/32f;
    private static TiledMap map;
    
    public MapRenderer(String mapName)
    {
        super(map = new TmxMapLoader().load("maps/" + mapName + "/" + mapName + ".tmx"), scale);
    }

    public void createCollisions()
    {
        float pixelsPerMeter = 32f;
        float tileSize = 1f; 
        
        MapObjects objects = getMap().getLayers().get("Objetos").getObjects();

        for(MapObject object: objects)
        {
            System.out.println(object.getName());
            if ("Box".equals(object.getName())) //No tiled apenas sete o nome do objeto criado que ele será reconhecido aqui, caso não esteja nesses ifs, crie um.
            {
                Rectangle rect = ((RectangleMapObject)object).getRectangle();

                float x = (rect.x + rect.width / 2) / pixelsPerMeter;
                float y = (rect.y + rect.height / 2) / pixelsPerMeter;
                float width = rect.width / pixelsPerMeter;
                float height = rect.height / pixelsPerMeter;

                PhysicsEntity entity = new PhysicsEntity(new Vector2(x, y), new Vector2(width, height));
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(width / 2, height / 2);
                entity.getBody().createFixture(shape, 0f);
                shape.dispose();
            }

            else if("Buraco".equals(object.getName()))
            {
                Rectangle rect = ((RectangleMapObject)object).getRectangle();
                Vector2 pos = new Vector2((rect.x + rect.width / 2f) / pixelsPerMeter, (rect.y + rect.height/2f) / pixelsPerMeter);
                new Flag(pos, rect.width / pixelsPerMeter);
            }
        }

        // for (int x = 0; x < layer.getWidth(); x++)
        // {
        //     for (int y = 0; y < layer.getHeight(); y++)
        //     {
        //         TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        //         if (cell != null && cell.getTile() != null)
        //         {
        //             int tileId = cell.getTile().getId();

        //             if (tileId == 3)
        //             {
        //                 PhysicsEntity wall1 = new PhysicsEntity(new Vector2((x + 0.5f) * tileSize, (y + 0.5f) * tileSize), new Vector2(tileSize, tileSize));
        //                 PolygonShape wallBox = new PolygonShape();
        //                 wallBox.setAsBox(tileSize / 2, tileSize / 2);
        //                 wall1.getBody().createFixture(wallBox, 0f);
        //                 wallBox.dispose();
        //             }
        //         }
        //     }
        // }
    }



    
}
