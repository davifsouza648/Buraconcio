package io.github.buraconcio.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Objects.Flag;

public class MapRenderer extends OrthogonalTiledMapRenderer
{
    private static float scale = 1/32f;
    private static TiledMap map;
    private Texture backgroundTexture;
    private Rectangle spawnArea;

    public MapRenderer(String mapName)
    {
        super(map = new TmxMapLoader().load("maps/" + mapName + "/" + mapName + ".tmx"), scale);
        backgroundTexture = new Texture("maps/" + mapName + "/background" + mapName + ".png");
    }

    public void createCollisions()
    {
        float pixelsPerMeter = 32f;

        MapObjects objects = getMap().getLayers().get("Objetos").getObjects();

        for (MapObject object : objects)
        {
            if ("SpawnArea".equals(object.getName())) {
                spawnArea = ((RectangleMapObject) object).getRectangle();
            }
            
            else if ("Box".equals(object.getName()))
            {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

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

            else if ("Buraco".equals(object.getName()))
            {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                float minX = rect.x / pixelsPerMeter;
                float minY = rect.y / pixelsPerMeter;
                float maxX = (rect.x + rect.width) / pixelsPerMeter;
                float maxY = (rect.y + rect.height) / pixelsPerMeter;

                float randomX = (float) (minX + Math.random() * (maxX - minX));
                float randomY = (float) (minY + Math.random() * (maxY - minY));

                Vector2 pos = new Vector2(randomX, randomY);

                new Flag(pos, 1f);
            }

            else if (object.getName() != null && object.getName().startsWith("Curva"))
            {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                createArcCollider(ellipse, object.getName(), pixelsPerMeter, 0.005f);
            }

            else if ("DiagonalPrincipal".equals(object.getName()) || "DiagonalSecundaria".equals(object.getName()))
            {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                float x = (rect.x + rect.width / 2) / pixelsPerMeter;
                float y = (rect.y + rect.height / 2) / pixelsPerMeter;
                float width = rect.width / pixelsPerMeter;
                float height = rect.height / pixelsPerMeter;

                PhysicsEntity entity = new PhysicsEntity(new Vector2(x, y), new Vector2(width, height));

                Vector2 p1, p2;

                if ("DiagonalSecundaria".equals(object.getName())) {
                    // Da parte inferior esquerda para superior direita
                    p1 = new Vector2((rect.x) / pixelsPerMeter, (rect.y) / pixelsPerMeter);
                    p2 = new Vector2((rect.x + rect.width) / pixelsPerMeter, (rect.y + rect.height) / pixelsPerMeter);
                } else {
                    // Da parte superior esquerda para inferior direita
                    p1 = new Vector2((rect.x) / pixelsPerMeter, (rect.y + rect.height) / pixelsPerMeter);
                    p2 = new Vector2((rect.x + rect.width) / pixelsPerMeter, (rect.y) / pixelsPerMeter);
                }

                // Centraliza os pontos no corpo físico
                p1.sub(x, y);
                p2.sub(x, y);

                // Cria um retângulo fino (linha com espessura)
                Vector2 dir = new Vector2(p2).sub(p1).nor();
                Vector2 normal = new Vector2(-dir.y, dir.x).scl(0.02f); // Espessura da linha (ajustável)

                PolygonShape shape = new PolygonShape();
                shape.set(new Vector2[]{
                    p1.cpy().add(normal),
                    p1.cpy().sub(normal),
                    p2.cpy().sub(normal),
                    p2.cpy().add(normal)
                });

                entity.getBody().createFixture(shape, 0f);
                shape.dispose();
            }

        }
    }

    private void createArcCollider(Ellipse ellipse, String name, float pixelsPerMeter, float thickness) {
        float x = ellipse.x / pixelsPerMeter;
        float y = ellipse.y / pixelsPerMeter;
        float width = ellipse.width / pixelsPerMeter;
        float height = ellipse.height / pixelsPerMeter;

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        float startAngle = 0f;
        float sweepAngle = 0f;

        if (name.equalsIgnoreCase("CurvaCompleta")) {
            startAngle = 0f;
            sweepAngle = (float) (2 * Math.PI);
        } else if (name.startsWith("CurvaBaixo")) {
            startAngle = (float) Math.PI;            
            sweepAngle = (float) Math.PI;            
        } else if (name.startsWith("CurvaCima")) {
            startAngle = 0f;                        
            sweepAngle = (float) Math.PI;           
        } else if (name.startsWith("Curva")) {
            // Verificar se é Curva1, 2, 3 ou 4
            int curvaIndex = 1;
            try {
                String indexString = name.replaceAll("[^0-9]", "");
                if (!indexString.isEmpty()) curvaIndex = Integer.parseInt(indexString);
            } catch (NumberFormatException ignored) {}

            sweepAngle = (float) (Math.PI / 2);

            switch (curvaIndex) {
                case 1: // inferior esquerdo
                    startAngle = (float) Math.PI;
                    break;
                case 2: // inferior direito
                    startAngle = (float) (3 * Math.PI / 2);
                    break;
                case 3: // superior direito
                    startAngle = 0f;
                    break;
                case 4: // superior esquerdo
                    startAngle = (float) (Math.PI / 2);
                    break;
                default:
                    startAngle = 0f;
                    break;
            }
        }

        // Raios da elipse
        float outerRadiusX = width / 2f;
        float outerRadiusY = height / 2f;
        float innerRadiusX = outerRadiusX - thickness;
        float innerRadiusY = outerRadiusY - thickness;

        // Suavidade da curva (quanto mais segmentos, mais suave)
        int segments = 50;

        Vector2[] outerArc = new Vector2[segments + 1];
        Vector2[] innerArc = new Vector2[segments + 1];

        for (int i = 0; i <= segments; i++) {
            float angle = startAngle + sweepAngle * i / segments;

            outerArc[i] = new Vector2(
                    centerX + (float) Math.cos(angle) * outerRadiusX,
                    centerY + (float) Math.sin(angle) * outerRadiusY
            );
            innerArc[i] = new Vector2(
                    centerX + (float) Math.cos(angle) * innerRadiusX,
                    centerY + (float) Math.sin(angle) * innerRadiusY
            );
        }

        // Cria entidade física
        PhysicsEntity entity = new PhysicsEntity(new Vector2(centerX, centerY), new Vector2(width, height));

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

    public void renderBackground() {
        getBatch().begin();

        float texWidth = backgroundTexture.getWidth() * scale;
        float texHeight = backgroundTexture.getHeight() * scale;

        float camLeft = getViewBounds().x;
        float camBottom = getViewBounds().y;
        float camRight = camLeft + getViewBounds().width;
        float camTop = camBottom + getViewBounds().height;

        for (float x = camLeft; x < camRight; x += texWidth) {
            for (float y = camBottom; y < camTop; y += texHeight) {
                getBatch().draw(backgroundTexture, x, y, texWidth, texHeight);
            }
        }

        getBatch().end();
    }

    public Vector2 getRandomSpawnPosition() {
        if (spawnArea == null) {
            throw new RuntimeException("SpawnArea não definida no mapa!");
        }

        float pixelsPerMeter = 32f;

        float minX = spawnArea.x / pixelsPerMeter;
        float minY = spawnArea.y / pixelsPerMeter;
        float maxX = (spawnArea.x + spawnArea.width) / pixelsPerMeter;
        float maxY = (spawnArea.y + spawnArea.height) / pixelsPerMeter;

        float randomX = (float) (minX + Math.random() * (maxX - minX));
        float randomY = (float) (minY + Math.random() * (maxY - minY));

        return new Vector2(randomX, randomY);
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        map.dispose();
    }
}
