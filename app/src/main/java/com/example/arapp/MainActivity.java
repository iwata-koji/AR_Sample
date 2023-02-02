package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArFragment fragment;
    private ModelRenderable modelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ARフラグメント取得
        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        // 3Dモデル読み込み
        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/halloween.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "読み込み失敗", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        if(fragment != null ) {
            // 平面タップ検知リスナー
            fragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        if (modelRenderable == null) {
                            return;
                        }
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(fragment.getArSceneView().getScene());

                        TransformableNode model = new TransformableNode(fragment.getTransformationSystem());
                        model.setParent(anchorNode);
                        model.setRenderable(modelRenderable);

                        //大きさ・向きを調整します。
                        model.getScaleController().setMinScale(0.01f);
                        model.getScaleController().setMaxScale(2.0f);
                        //v:width v1:? v2:height
                        model.setLocalScale(new Vector3(0.5f,0f,0.5f));
                        model.setLocalPosition(new Vector3(0,0.0f,0));
                        //y軸(縦軸)を中心に180度回転
                        model.setLocalRotation(Quaternion.axisAngle(new Vector3(0,1,0),-120));

                        model.setParent(anchorNode);
                        model.select();
                    });
        }
    }
}