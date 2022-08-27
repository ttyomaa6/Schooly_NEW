package com.egormoroz.schooly;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.SurfaceView;


import androidx.core.view.MotionEventCompat;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.google.android.filament.Colors;
import com.google.android.filament.Engine;
import com.google.android.filament.EntityInstance;
import com.google.android.filament.EntityManager;
import com.google.android.filament.Fence;
import com.google.android.filament.Filament;
import com.google.android.filament.LightManager;
import com.google.android.filament.Material;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.RenderableManager;
import com.google.android.filament.Skybox;
import com.google.android.filament.TransformManager;
import com.google.android.filament.android.UiHelper;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.AssetLoader;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.filament.gltfio.FilamentInstance;
import com.google.android.filament.gltfio.Gltfio;
import com.google.android.filament.gltfio.MaterialProvider;
import com.google.android.filament.gltfio.ResourceLoader;
import com.google.android.filament.gltfio.UbershaderLoader;
import com.google.android.filament.utils.AutomationEngine;
import com.google.android.filament.utils.Float3;
import com.google.android.filament.utils.GestureDetector;
import com.google.android.filament.utils.Manipulator;
import com.google.android.filament.utils.Utils;
import com.egormoroz.schooly.ModelViewer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FilamentModel {

    UiHelper uiHelper;
    Engine engine;
    Manipulator cameraManipulator;
    ModelViewer modelViewer;
    Choreographer choreographer=Choreographer.getInstance();
    GestureDetector doubleTapDetector;
    AutomationEngine.ViewerContent viewerContent=new AutomationEngine.ViewerContent();
    Float3 float3=new Float3(0.0f, 10.0f, 0.0f);
    Float3  float31=new Float3(-0.01f, 0.27f, -0.11f);
    long loadStartTime;
    Fence loadStartFence;
    byte[] buffer;
    FilamentAsset filamentAsset1;
    URI uri;
    TransformManager transformManager;
    boolean normalizeSkinningWeights = true;
    boolean recomputeBoundingBoxes = false;
    boolean ignoreBindTransform = false;
    Buffer buffer1,bufferToFilament;
    FilamentAsset filamentAsset;
    ResourceLoader resourceLoader;
    MaterialProvider materialProvider;
    AssetLoader assetLoader;
    int a;
    int b=0;
    ArrayList<FilamentAsset> filamentAssets=new ArrayList<>();

    public void initFilament(SurfaceView surfaceView,Buffer buffer,boolean onTouch
            ,LockableNestedScrollView lockableNestedScrollView,String type
            ,boolean transform) throws IOException, URISyntaxException {
        Log.d("#####", "FONOSH780   ");
        Filament.init();
        Gltfio.init();
        Utils.INSTANCE.init();
        cameraManipulator=new Manipulator.Builder()
                .targetPosition(0.0f, 10.5f, -1.0f)
                .orbitHomePosition(0.0f, 16.0f, 28.0f)
                .viewport(surfaceView.getWidth(), surfaceView.getHeight())
                .zoomSpeed(0.05f)
                .orbitSpeed(0.007f, 0.007f)
                .mapMinDistance(-150.0f)
                .build(Manipulator.Mode.ORBIT);
        Log.d("#####", "FONOSH788   ");
        doubleTapDetector=new GestureDetector(surfaceView, cameraManipulator);
        uiHelper=new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        engine=Engine.create();
        filamentAssets=new ArrayList<>();
        modelViewer=new ModelViewer(surfaceView, engine, uiHelper, cameraManipulator);
        setupFilament();
        Log.d("#####", "FONOSH00   ");
        surfaceView.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event) {
                modelViewer.onTouchEvent(event);
                doubleTapDetector.onTouchEvent(event);
                if(lockableNestedScrollView!=null){
                    int action = MotionEventCompat.getActionMasked(event);
                    switch(action) {
                        case (MotionEvent.ACTION_DOWN) :
                            lockableNestedScrollView.setScrollingEnabled(false);
                            return true;
                        case (MotionEvent.ACTION_MOVE) :
                            lockableNestedScrollView.setScrollingEnabled(false);
                            return true;
                        case (MotionEvent.ACTION_UP) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_CANCEL) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_OUTSIDE) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_SCROLL) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                    }
                }
                return onTouch;
            }
        });
        loadGlb(buffer);
        Skybox skybox=new Skybox.Builder()
                .color(0f, 0f, 0f, 1.0f)
                .build(modelViewer.getEngine());
        if(type.equals("regularRender")){
            loadPointLights();
        }else if(type.equals("looksRecycler")){
            loadDefaultLight();
        }
        modelViewer.getScene().setSkybox(skybox);
        Log.d("#####", "FONOSH   ");
    }

    public void loadDefaultLight(){
        int light=EntityManager.get().create();
        float[] float1 = Colors.cct(5_500.0f);
        new LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(float1[0], float1[1], float1[2])
                .intensity(120_000.0f)
                .direction(0.0f, -0.5f, -1.0f)
                .castShadows(false)
                .build(engine, light);
        modelViewer.getScene().addEntity(light);
    }

    public void loadPersonLight(){
        int light=EntityManager.get().create();
        float[] float1 = Colors.cct(5_500.0f);
        new LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(float1[0], float1[1], float1[2])
                .intensity(120_000.0f)
                .direction(0.0f, -0.5f, -1.0f)
                .castShadows(false)
                .build(engine, light);
        int light6=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(50_000_000.0f)
                .falloff(20)
                .position(8.0f, 17f, 0.0f)
                .build(engine, light6);
        int[] lights={light,light6};
        modelViewer.getScene().addEntities(lights);
    }

    public void loadPointLights(){
        int light=EntityManager.get().create();
        float[] float1 = Colors.cct(5_500.0f);
        new LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(float1[0], float1[1], float1[2])
                .intensity(120_000.0f)
                .direction(0.0f, -0.5f, -1.0f)
                .castShadows(false)
                .build(engine, light);
        int light1=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(float1[0], float1[1], float1[2])
                .intensity(120_000.0f)
                .direction(0.0f, -0.5f, -1.0f)
                .castShadows(false)
                .build(engine, light1);
        int light2=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(120_000_000.0f)
                .falloff(20)
                .position(0.0f, 17.0f, 8.0f)
                .build(engine, light2);
        int light3=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(200_000_000.0f)
                .falloff(20)
                .position(-8.0f, 6.0f, -8.0f)
                .build(engine, light3);
        int light4=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(200_000_000.0f)
                .falloff(20)
                .position(8.0f, 6.0f, -8.0f)
                .build(engine, light4);
        int light5=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(80_000_000.0f)
                .falloff(30)
                .position(0.0f, 24.0f, -8.0f)
                .build(engine, light5);
        int light6=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(50_000_000.0f)
                .falloff(20)
                .position(8.0f, 17f, 0.0f)
                .build(engine, light6);
        int light7=EntityManager.get().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(float1[0], float1[1], float1[2])
                .intensity(50_000_000.0f)
                .falloff(20)
                .position(-8.0f, 17.0f, 0.0f)
                .build(engine, light7);
        int[] lights={light,light2,light3,light4,light5,light6,light7};
        modelViewer.getScene().addEntities(lights);
    }

    Choreographer.FrameCallback frameCallback=new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            choreographer.postFrameCallback(frameCallback);
            if(modelViewer!=null){
                modelViewer.render(frameTimeNanos,filamentAsset);
            }
        }
    };

    public void loadGlb(Buffer buffer){
        materialProvider=new UbershaderLoader(engine);
        assetLoader=new AssetLoader(engine,materialProvider,EntityManager.get());
        filamentAsset=assetLoader.createAssetFromBinary(buffer);
        filamentAssets.add(filamentAsset);
        resourceLoader=new ResourceLoader(engine, normalizeSkinningWeights, recomputeBoundingBoxes, ignoreBindTransform);
        resourceLoader.asyncBeginLoad(filamentAsset);
        Animator animator= filamentAsset.getAnimator();
        filamentAsset.releaseSourceData();
        int[] e=filamentAsset.getEntities();
        modelViewer.getScene().addEntities(filamentAsset.getEntities());
        loadStartTime=System.nanoTime();
        loadStartFence=modelViewer.getEngine().createFence();
    }

    public void populateScene(Buffer buffer,Clothes clothes){
        FilamentAsset filamentAsset=assetLoader.createAssetFromBinary(buffer);
        filamentAssets.add(filamentAsset);
        resourceLoader.asyncBeginLoad(filamentAsset);
        filamentAsset.releaseSourceData();
        resourceLoader.asyncUpdateLoad();
        modelViewer.populateScene(filamentAsset);
        if(clothes.getTransformRatio()!=0){
            Float3 float3=new Float3(clothes.getX(), clothes.getY(), clothes.getZ());
            modelViewer.transformToUnitCube(float3,filamentAsset,clothes.getTransformRatio());
        }
    }

    public void populateSceneS(Buffer buffer,float x,float y,float z,float tr){
        FilamentAsset filamentAsset=assetLoader.createAssetFromBinary(buffer);
        filamentAssets.add(filamentAsset);
        resourceLoader.asyncBeginLoad(filamentAsset);
        filamentAsset.releaseSourceData();
        resourceLoader.asyncUpdateLoad();
        modelViewer.populateScene(filamentAsset);
        if(tr!=0){
            Float3 float3=new Float3(z,y,x);
            modelViewer.transformToUnitCube(float3,filamentAsset,tr);
        }
    }

    public void populateSceneFacePart(Buffer buffer) {
        FilamentAsset filamentAsset=assetLoader.createAssetFromBinary(buffer);
        filamentAssets.add(filamentAsset);
        resourceLoader.asyncBeginLoad(filamentAsset);
        filamentAsset.releaseSourceData();
        resourceLoader.asyncUpdateLoad();
        modelViewer.populateScene(filamentAsset);
    }

    public void setMask(Clothes clothes){
        for(int i=0;i<filamentAssets.size();i++){
            FilamentAsset filamentAsset=filamentAssets.get(i);
            int[] entities=filamentAsset.getEntitiesByName(clothes.getClothesTitle());
            if(entities.length!=0){
                modelViewer.getScene().removeEntity(entities[0]);
                filamentAssets.remove(i);
            }
        }
    }

    public void changeColor(String type, Color color){
        Log.d("#####", "WWWWW   "+filamentAssets+"   "+type);
        RenderableManager renderableManager=engine.getRenderableManager();
        for(int i=0;i<filamentAssets.size();i++){
            FilamentAsset filamentAsset=filamentAssets.get(i);
            int[] entities=filamentAsset.getEntitiesByName(type);
            if(entities.length!=0){
                MaterialInstance material=renderableManager.
                        getMaterialInstanceAt(renderableManager.getInstance(entities[0]),0);
                material.setParameter("baseColorFactor",  Colors.RgbaType.SRGB,color.getColorX(), color.getColorY(), color.getColorZ(),1f);
                break;
            }
        }
        Log.d("#####", "WWWWW22   ");
    }

    public void postFrameCallback(){
        choreographer.postFrameCallback(frameCallback);
    }

    public void removeFrameCallback(){
        choreographer.removeFrameCallback(frameCallback);
    }

    public void setupFilament(){
        viewerContent.view=modelViewer.getView();
        viewerContent.sunlight=modelViewer.getLight();
        viewerContent.lightManager=modelViewer.getEngine().getLightManager();
        viewerContent.scene=modelViewer.getScene();
        viewerContent.renderer=modelViewer.getRenderer();

    }

    public void initFilamentForPersonCustom(SurfaceView surfaceView,Buffer buffer){
        Filament.init();
        Gltfio.init();
        Utils.INSTANCE.init();
        cameraManipulator=new Manipulator.Builder()
                .targetPosition(0.8f, 19.5f, 0.0f)
                .orbitHomePosition(3.0f, 19.5f, 4.0f)
                .viewport(surfaceView.getWidth(), surfaceView.getHeight())
                .zoomSpeed(0.07f)
                .mapMinDistance(5.0f)
                .build(Manipulator.Mode.ORBIT);
        uiHelper=new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        engine=Engine.create();
        filamentAssets=new ArrayList<>();
        modelViewer=new ModelViewer(surfaceView, engine, uiHelper, cameraManipulator);
        setupFilament();
        loadGlb(buffer);
        Skybox skybox=new Skybox.Builder()
                .color(0f, 0f, 0f, 1.0f)
                .build(modelViewer.getEngine());
        loadPersonLight();
        modelViewer.getScene().setSkybox(skybox);
    }

    public void setMaskOnFacePart(FacePart facePart){
        for(int i=0;i<filamentAssets.size();i++){
            FilamentAsset filamentAsset=filamentAssets.get(i);
            int[] entities=filamentAsset.getEntitiesByName(facePart.getPartTitle());
            if(entities.length!=0){
                modelViewer.getScene().removeEntity(entities[0]);
                filamentAssets.remove(i);
            }
        }
    }

    public void initNewsFilament(SurfaceView surfaceView,Buffer buffer,boolean onTouch
            ,LockableNestedScrollView lockableNestedScrollView,String type
            ,boolean transform) throws IOException, URISyntaxException {
        Filament.init();
        Gltfio.init();
        Utils.INSTANCE.init();
        cameraManipulator=new Manipulator.Builder()
                .targetPosition(0.0f, 10f, -1.0f)
                .orbitHomePosition(0.0f, 18.0f, 30.0f)
                .viewport(surfaceView.getWidth(), surfaceView.getHeight())
                .zoomSpeed(0.05f)
                .orbitSpeed(0.007f, 0.007f)
                .mapMinDistance(-150.0f)
                .build(Manipulator.Mode.ORBIT);
        uiHelper=new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        doubleTapDetector=new GestureDetector(surfaceView, cameraManipulator);
        engine=Engine.create();
        filamentAssets=new ArrayList<>();
        modelViewer=new ModelViewer(surfaceView, engine, uiHelper, cameraManipulator);
        setupFilament();
        surfaceView.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event) {
                modelViewer.onTouchEvent(event);
                doubleTapDetector.onTouchEvent(event);
                if(lockableNestedScrollView!=null){
                    int action = MotionEventCompat.getActionMasked(event);
                    switch(action) {
                        case (MotionEvent.ACTION_DOWN) :
                            lockableNestedScrollView.setScrollingEnabled(false);
                            return true;
                        case (MotionEvent.ACTION_MOVE) :
                            lockableNestedScrollView.setScrollingEnabled(false);
                            return true;
                        case (MotionEvent.ACTION_UP) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_CANCEL) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_OUTSIDE) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                        case (MotionEvent.ACTION_SCROLL) :
                            lockableNestedScrollView.setScrollingEnabled(true);
                            return true;
                    }
                }
                return onTouch;
            }
        });
        loadGlb(buffer);
        Skybox skybox=new Skybox.Builder()
                .color(0f, 0f, 0f, 1.0f)
                .build(modelViewer.getEngine());
        if(type.equals("regularRender")){
            loadPointLights();
        }else if(type.equals("looksRecycler")){
            loadDefaultLight();
        }
        modelViewer.getScene().setSkybox(skybox);
    }

    public void initShareFilament(SurfaceView surfaceView,Buffer buffer,boolean onTouch
            ,LockableNestedScrollView lockableNestedScrollView,String type
            ,boolean transform) throws IOException, URISyntaxException {
        Filament.init();
        Gltfio.init();
        Utils.INSTANCE.init();
        cameraManipulator=new Manipulator.Builder()
                .targetPosition(0.0f, 10f, -1.0f)
                .orbitHomePosition(9.0f, 18.0f, 30.0f)
                .viewport(surfaceView.getWidth(), surfaceView.getHeight())
                .zoomSpeed(0.05f)
                .orbitSpeed(0.007f, 0.007f)
                .mapMinDistance(-150.0f)
                .build(Manipulator.Mode.ORBIT);
        uiHelper=new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        engine=Engine.create();
        filamentAssets=new ArrayList<>();
        modelViewer=new ModelViewer(surfaceView, engine, uiHelper, cameraManipulator);
        setupFilament();
        loadGlb(buffer);
        Skybox skybox=new Skybox.Builder()
                .color(0f, 0f, 0f, 1.0f)
                .build(modelViewer.getEngine());
        if(type.equals("regularRender")){
            loadPointLights();
        }else if(type.equals("looksRecycler")){
            loadDefaultLight();
        }
        modelViewer.getScene().setSkybox(skybox);
    }
}
