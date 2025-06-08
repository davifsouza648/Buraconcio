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
            else if (object.getName() != null && object.getName().startsWith("Curva")) 
            {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();

                float x = ellipse.x / pixelsPerMeter;
                float y = ellipse.y / pixelsPerMeter;
                float width = ellipse.width / pixelsPerMeter;
                float height = ellipse.height / pixelsPerMeter;

                int curvaIndex = 1;
                try {
                    String name = object.getName().replaceAll("[^0-9]", "");
                    if (!name.isEmpty()) curvaIndex = Integer.parseInt(name);
                } catch (NumberFormatException ignored) {}

                float centerX = x;
                float centerY = y;
                float startAngle = 0;

                switch (curvaIndex) {
                    case 1: // inferior esquerdo
                        centerX = x + width - 8f/pixelsPerMeter;
                        centerY = y + height - 8f/pixelsPerMeter;
                        startAngle = (float) Math.PI;
                        break;
                    case 2: // inferior direito
                        centerX = x;
                        centerY = y + height - 8f/pixelsPerMeter;
                        startAngle = (float) (3 * Math.PI / 2);
                        break;
                    case 3: // superior direito
                        centerX = x;
                        centerY = y;
                        startAngle = 0f;
                        break;
                    case 4: // superior esquerdo
                        centerX = x + width;
                        centerY = y;
                        startAngle = (float) (Math.PI / 2);
                        break;
                }

                float outerRadius = width; 
                float thickness = 0.2f;
                float innerRadius = outerRadius - thickness;

                int segments = 10;
                Vector2[] outerArc = new Vector2[segments + 1];
                Vector2[] innerArc = new Vector2[segments + 1];

                for (int i = 0; i <= segments; i++) {
                    float angle = startAngle + (float) (Math.PI / 2) * i / segments;

                    outerArc[i] = new Vector2(
                        centerX + (float) Math.cos(angle) * outerRadius,
                        centerY + (float) Math.sin(angle) * outerRadius
                    );
                    innerArc[i] = new Vector2(
                        centerX + (float) Math.cos(angle) * innerRadius,
                        centerY + (float) Math.sin(angle) * innerRadius
                    );
                }

                PhysicsEntity entity = new PhysicsEntity(new Vector2(centerX, centerY), new Vector2(outerRadius * 2, outerRadius * 2));

                for (int i = 0; i < segments; i++) {
                    Vector2 p1 = innerArc[i];
                    Vector2 p2 = innerArc[i + 1];
                    Vector2 p3 = outerArc[i + 1];
                    Vector2 p4 = outerArc[i];

                    // Triângulo 1
                    PolygonShape shape1 = new PolygonShape();
                    shape1.set(new Vector2[]{
                        new Vector2(p1.x - centerX, p1.y - centerY),
                        new Vector2(p2.x - centerX, p2.y - centerY),
                        new Vector2(p3.x - centerX, p3.y - centerY)
                    });
                    entity.getBody().createFixture(shape1, 0f);
                    shape1.dispose();

                    // Triângulo 2
                    PolygonShape shape2 = new PolygonShape();
                    shape2.set(new Vector2[]{
                        new Vector2(p1.x - centerX, p1.y - centerY),
                        new Vector2(p3.x - centerX, p3.y - centerY),
                        new Vector2(p4.x - centerX, p4.y - centerY)
                    });
                    entity.getBody().createFixture(shape2, 0f);
                    shape2.dispose();
                }
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
