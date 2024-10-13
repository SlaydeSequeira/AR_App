package com.terminus.yttutorial;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private Scene scene;
    private ModelRenderable potatoRenderable, bowlRenderable;
    private TransformableNode potatoNode, bowlNode;
    private boolean potatoInBowl = false;
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the AR Fragment
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        scene = arFragment.getArSceneView().getScene();

        Button tossButton = findViewById(R.id.tossButton);

        // Load the 3D models
        loadModels();

        tossButton.setOnClickListener(v -> {
            if (!potatoInBowl) {
                tossPotatoIntoBowl();
            }
        });
    }

    private void loadModels() {
        // Load potato model
        ModelRenderable.builder()
                .setSource(this, Uri.parse("potato.glb"))
                .build()
                .thenAccept(renderable -> potatoRenderable = renderable);

        // Load bowl model
        ModelRenderable.builder()
                .setSource(this, Uri.parse("bowl.glb"))
                .build()
                .thenAccept(renderable -> bowlRenderable = renderable)
                .thenRun(this::addBowlToScene);  // Add bowl to the scene after it is loaded
    }

    private void addBowlToScene() {
        bowlNode = new TransformableNode(arFragment.getTransformationSystem());
        bowlNode.setRenderable(bowlRenderable);
        bowlNode.setWorldPosition(new Vector3(0, 0, -1)); // Position bowl 1 meter away from the camera
        scene.addChild(bowlNode);
    }

    private void tossPotatoIntoBowl() {
        potatoNode = new TransformableNode(arFragment.getTransformationSystem());
        potatoNode.setRenderable(potatoRenderable);
        potatoNode.setWorldPosition(new Vector3(0, 1, -1)); // Start potato above the bowl
        scene.addChild(potatoNode);

        // Animate potato falling into the bowl
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                runOnUiThread(() -> {
                    Vector3 currentPosition = potatoNode.getWorldPosition();
                    potatoNode.setWorldPosition(new Vector3(currentPosition.x, currentPosition.y - 0.01f, currentPosition.z));

                    // Check if potato is inside the bowl (simple collision check based on position)
                    if (currentPosition.y <= 0.2 && Math.abs(currentPosition.x) < 0.1 && Math.abs(currentPosition.z + 1) < 0.1) {
                        potatoInBowl = true;
                    }
                });
                try {
                    Thread.sleep(10); // Control speed of the animation
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
