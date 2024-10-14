package com.example.myapplication;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.math.Vector3;

import java.lang.ref.WeakReference;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import android.graphics.Point;  // Make sure you import this for screen coordinates
public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener {

    private ArFragment arFragment;
    private Renderable model1; // First model
    private Renderable model2; // Second model
    private Renderable model3; // Third model (potato3)
    private ViewRenderable viewRenderable;
    private Scene scene;
    private boolean model1Placed = false; // For first model
    private boolean model2Placed = false; // For second model
    private boolean model3Placed = false; // For third model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addFragmentOnAttachListener(this);

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }

        loadModels();

        // Set up the button listener
        Button tossButton = findViewById(R.id.tossButton);
        tossButton.setOnClickListener(v -> {
            //removeModels();   ///Fix this later
            loadPotato3();
        });
    }

    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnSessionConfigurationListener(this);
            arFragment.setOnViewCreatedListener(this);
        }
    }

    @Override
    public void onViewCreated(ArSceneView arSceneView) {
        arFragment.setOnViewCreatedListener(null);
        scene = arFragment.getArSceneView().getScene();
    }

    @Override
    public void onSessionConfiguration(Session session, Config config) {
        // Disable plane detection
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);

        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        }
    }

    public void loadModels() {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);

        // Load the first model (potato)
        ModelRenderable.builder()
                .setSource(this, Uri.parse("https://slaydesequeira.github.io/MyGlbFiles/potato.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model1 = model;
                        addModelToScene1();  // Add the first model to the scene immediately after loading
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load potato model", Toast.LENGTH_LONG).show();
                    return null;
                });

        // Load the second model (potato2)
        ModelRenderable.builder()
                .setSource(this, Uri.parse("https://slaydesequeira.github.io/MyGlbFiles/flour_aridll.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model2 = model;
                        addModelToScene2();  // Add the second model to the scene immediately after loading
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load potato2 model", Toast.LENGTH_LONG).show();
                    return null;
                });

        // Load the viewRenderable (optional)
        ViewRenderable.builder()
                .setView(this, R.layout.view_model_title)
                .build()
                .thenAccept(viewRenderable -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.viewRenderable = viewRenderable;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load view renderable", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    private void addModelToScene1() {
        if (model1 != null && scene != null && !model1Placed) {
            TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setRenderable(model1);
            modelNode.setWorldPosition(new Vector3(5, 0, -10)); // Position first model 2 meters in front of the camera
            scene.addChild(modelNode);
            model1Placed = true;
        }
    }

    private void addModelToScene2() {
        if (model2 != null && scene != null && !model2Placed) {
            TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setRenderable(model2);
            modelNode.setWorldPosition(new Vector3(0, 0, -2)); // Position second model 1 meter to the right of the first model
            scene.addChild(modelNode);
            model2Placed = true;
        }
    }

    private void removeModels() {
        // Remove the existing models from the scene
        scene.removeChild(getModelNodeByName("model1"));
        scene.removeChild(getModelNodeByName("model2"));
        model1Placed = false;
        model2Placed = false;
    }

    private void loadPotato3() {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);

        // Load the third model (potato3)
        ModelRenderable.builder()
                .setSource(this, Uri.parse("https://slaydesequeira.github.io/MyGlbFiles/tandoori_aloo_paratha.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model3 = model;
                        addModelToScene3();  // Add the third model to the scene immediately after loading
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load potato3 model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    private void addModelToScene3() {
        if (model3 != null && scene != null && !model3Placed) {
            TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setRenderable(model3);
            modelNode.setWorldPosition(new Vector3(0, 0, -2)); // Position the third model 2 meters in front of the camera
            scene.addChild(modelNode);
            model3Placed = true;
        }
    }

    private TransformableNode getModelNodeByName(String name) {
        // You can implement a method to find and return the model node based on its name.
        // For now, return null as placeholder.
        return null;
    }
}

