package io.github.buraconcio.Utils.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

public class CursorManager {
    private static Cursor arrowCursor;
    private static Cursor handCursor;
    private static Cursor ibeamCursor;
    private static Cursor gameCursor;

    private static boolean loaded = false;

    public static void loadCursors() {
        if (loaded) return;

        arrowCursor = createCursor("cursor/arrowCursor.png", 5, 0);
        handCursor = createCursor("cursor/handCursor.png", 14, 1);
        ibeamCursor = createCursor("cursor/ibeamCursor.png", 2, 15);
        gameCursor = createCursor("cursor/gameCursor.png", 5, 0);

        loaded = true;
    }

    private static Cursor createCursor(String path, int xHotspot, int yHotspot) {
        Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose();
        return cursor;
    }

    public static void applyHandCursorOnHover(Actor actor) {
        loadCursors();

        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setCursor(handCursor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setCursor(arrowCursor);
            }
        });
    }


    public static void applyIbeamCursorOnHover(final com.badlogic.gdx.scenes.scene2d.ui.TextField textField) {
        loadCursors();

        textField.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setCursor(ibeamCursor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //só volta para arrow se o textField não tiver foco
                if (!textField.hasKeyboardFocus()) {
                    Gdx.graphics.setCursor(arrowCursor);
                }
            }
        });

        //Escuta o foco do teclado para garantir o cursor certo
        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    Gdx.graphics.setCursor(ibeamCursor);
                } else {
                    Gdx.graphics.setCursor(arrowCursor);
                }
            }
        });
    }



    public static void resetToArrow() {
        loadCursors();
        Gdx.graphics.setCursor(arrowCursor);
    }

    public static void setGameCursor(){
        loadCursors();
        Gdx.graphics.setCursor(gameCursor);
    }

}
