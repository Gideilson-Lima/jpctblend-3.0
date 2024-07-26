package com.andresjesse.jpctblendae.actors;

import android.content.res.AssetManager;
import android.util.Log;

import com.andresjesse.jpctblendae.IActor;
import com.lima.gui.ChildCamera;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import java.io.IOException;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;

public class Player implements IActor {

    private Animated3D player;
    private World world;
    private Object3D ellipsoid;
    private SimpleVector ellipsoidCollision = new SimpleVector(0.5f,2.3f,0.4f);
    public Animated3D getActor(){
        return player;
    }
    public Player(AssetManager assets){

        try {
            String vertexShader = null, fragmentShader=null;
            vertexShader = Loader.loadTextFile(assets.open("shaders/vertexshader_offset.glsl"));
            fragmentShader = Loader.loadTextFile(assets.open("shaders/fragmentshader_general.glsl"));

            GLSLShader shader = new GLSLShader(vertexShader, fragmentShader);
            shader.setStaticUniform("invRadius", 0.0003f);

            player = BonesIO.loadGroup(assets.open("hugemap/meshs/suit.bones")).get(0);
            player.setShader(shader);
            Texture tx = new Texture(assets.open("hugemap/textures/suit.png"),true);
            TextureManager.getInstance().addTexture("player",tx);
            int len = player.getSkinClipSequence().getSize();
            for (int i=0;i<len;i++)
                Log.d("ANIMATION",player.getSkinClipSequence().getClip(i).getName()+" i:"+i);
            player.setTexture("player");
            player.build();
            player.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPosition(SimpleVector pos) {
        player.clearTranslation();
        player.translate(pos);
    }

    @Override
    public void setRotation(SimpleVector pos) {
        player.clearRotation();
        player.rotateX(pos.x);
        player.rotateY(pos.y);
        player.rotateZ(pos.z);
    }

    @Override
    public void addToWorld(World world) {
        this.world = world;
        world.addObject(player);
    }

    @Override
    public void removeFromWorld() {
        world.removeObject(player);
    }

    @Override
    public void act() {
        SimpleVector sv = player.getTransformedCenter();
        sv.y+=0.5;

        // finally apply the gravity:
        SimpleVector t = new SimpleVector(0, 0.2f, 0);
        t = player.checkForCollisionEllipsoid(t, ellipsoidCollision, 1);
        player.translate(t);
    }
}
