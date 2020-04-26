package sample;


import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;


public class Main extends Application {

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    private boolean turn_flag = false;
    private boolean music_flag = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, 1900, 1080, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-15);
        scene.setCamera(camera);

        Rotate rotateX = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(20, 0, 0, 0, Rotate.Y_AXIS);

        //rotating scene

        scene.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            if(!me.isShiftDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
                rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });

        //scrolling
        scene.addEventHandler(ScrollEvent.SCROLL, event ->
        {
            double delta = event.getDeltaY();
            camera.translateZProperty().set(camera.getTranslateZ() + delta);
        });

        //import wings
        ObjModelImporter see = new ObjModelImporter();

        see.read(new File("shapes/wing.OBJ").toURI().toString());
        MeshView one = see.getImport()[0];
        MeshView two = see.getImport()[1];

        see.read(new File("shapes/wing2.OBJ").toURI().toString());
        MeshView three = see.getImport()[0];
        MeshView four = see.getImport()[1];

        Translate tr = new Translate(2,0.05);
        Group wing_left = new Group();
        wing_left.getChildren().addAll(one,two);
        wing_left.getTransforms().addAll(tr);

        Translate tr2 = new Translate(-2,0.05);
        Group wing_right = new Group();
        wing_right.getChildren().addAll(three,four);
        wing_right.getTransforms().addAll(tr2);

        sceneRoot.getChildren().addAll(wing_left,wing_right);


        see.read(new File("shapes/background.OBJ").toURI().toString());
        Group backgr = new Group();
        backgr.getChildren().addAll(see.getImport()[0],see.getImport()[1],see.getImport()[2],see.getImport()[3],see.getImport()[4]);
        sceneRoot.getChildren().addAll(backgr);

        see.read(new File("shapes/test2.OBJ").toURI().toString());
        MeshView main_box = see.getImport()[0];
        MeshView main_box2 = see.getImport()[1];
        main_box.setTranslateY(0.2);
        main_box2.setTranslateY(0.2);
        sceneRoot.getChildren().addAll(main_box,main_box2);

        see.read(new File("shapes/bottom.OBJ").toURI().toString());
        MeshView bottom = see.getImport()[0];
        MeshView bottom2 = see.getImport()[1];
        MeshView bottom3 = see.getImport()[2];
        bottom.setTranslateY(0.2);
        bottom2.setTranslateY(0.2);
        bottom3.setTranslateY(0.2);

        Group bottom_module = new Group();
        bottom_module.getChildren().addAll(bottom,bottom2,bottom3);

        Rectangle rectangle3 = new Rectangle(3,0.5,Color.TRANSPARENT);
        rectangle3.setMouseTransparent(true);
        rectangle3.setDepthTest(DepthTest.DISABLE);
        sceneRoot.getChildren().addAll(bottom_module,rectangle3);
        rectangle3.setTranslateX(-1.5);


        // wings + dragging
        Rectangle rectangle = new Rectangle(2.4,1,Color.TRANSPARENT);
        rectangle.setMouseTransparent(true);
        rectangle.setDepthTest(DepthTest.DISABLE);
        rectangle.setTranslateX(0.8);

        Rectangle rectangle2 = new Rectangle(2.4,1,Color.TRANSPARENT);
        rectangle2.setMouseTransparent(true);
        rectangle2.setDepthTest(DepthTest.DISABLE);
        rectangle2.setTranslateX(-3.2);

        Rotate rotating = new Rotate(90, 0, 0, 0, Rotate.X_AXIS);
        rectangle.getTransforms().add(rotating);
        rectangle.setTranslateZ(-1);
        rectangle2.getTransforms().add(rotating);
        rectangle2.setTranslateZ(-1);

        sceneRoot.getChildren().addAll(rectangle,rectangle2);


        // dragging animation
        bottom_module.setOnDragDetected((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                bottom_module.setMouseTransparent(true);
                rectangle3.setMouseTransparent(false);
                bottom_module.setCursor(Cursor.MOVE);
                bottom_module.startFullDrag();
            }
        });

        bottom_module.setOnMouseReleased((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                bottom_module.setMouseTransparent(false);
                rectangle3.setMouseTransparent(true);
                bottom_module.setCursor(Cursor.DEFAULT);
            }
        });

        wing_left.setOnDragDetected((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                wing_left.setMouseTransparent(true);
                rectangle.setMouseTransparent(false);
                wing_left.setCursor(Cursor.MOVE);
                wing_left.startFullDrag();
            }
        });

        wing_left.setOnMouseReleased((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                wing_left.setMouseTransparent(false);
                rectangle.setMouseTransparent(true);
                wing_left.setCursor(Cursor.DEFAULT);
            }
        });

        wing_right.setOnDragDetected((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                wing_right.setMouseTransparent(true);
                rectangle2.setMouseTransparent(false);
                wing_right.setCursor(Cursor.MOVE);
                wing_right.startFullDrag();
            }
        });

        wing_right.setOnMouseReleased((MouseEvent event)-> {
            if(event.isShiftDown())
            {
                wing_right.setMouseTransparent(false);
                rectangle2.setMouseTransparent(true);
                wing_right.setCursor(Cursor.DEFAULT);
            }
        });

        rectangle.setOnMouseDragOver((MouseDragEvent event)->
        {
            if(event.isShiftDown())
            {
                Point3D coords = event.getPickResult().getIntersectedPoint();
                coords = rectangle.localToParent(coords);
                wing_left.setTranslateX(coords.getX()-2);
            }

        });

        rectangle2.setOnMouseDragOver((MouseDragEvent event)-> {
            if(event.isShiftDown())
            {
                Point3D coords = event.getPickResult().getIntersectedPoint();
                coords = rectangle2.localToParent(coords);
                wing_right.setTranslateX(coords.getX()+2);
            }

        });

        rectangle3.setOnMouseDragOver((MouseDragEvent event)-> {
            if(event.isShiftDown())
            {
                Point3D coords = event.getPickResult().getIntersectedPoint();
                coords = rectangle3.localToParent(coords);
                bottom_module.setTranslateY(coords.getY());
            }

        });



        //pressed realised material
        PhongMaterial realised = new PhongMaterial();
        PhongMaterial realised2 = new PhongMaterial();
        PhongMaterial press_material = new PhongMaterial();

        PhongMaterial plane_material = new PhongMaterial();
        PhongMaterial text_material = new PhongMaterial();
        text_material.setDiffuseMap(new Image("file:images/keys/piano.jpg"));
        realised.setDiffuseColor(Color.BLACK);
        realised2.setDiffuseColor(Color.WHITESMOKE);

        plane_material.setDiffuseColor(Color.DARKGREY);
        press_material.setDiffuseColor(Color.RED);


        Cylinder switch_button = new Cylinder(0.1,0.2);
        switch_button.setTranslateX(-1.8);
        switch_button.setTranslateZ(0.8);
        switch_button.setTranslateY(-0.1);
        switch_button.setMaterial(realised);

        Cylinder music_button = new Cylinder(0.1,0.2);
        music_button.setTranslateX(0);
        music_button.setTranslateZ(0.8);
        music_button.setTranslateY(-0.1);
        music_button.setMaterial(realised);


        Media open_sound = new Media(new File("sounds/opening_sound.mp3").toURI().toString());
        MediaPlayer mp = new MediaPlayer(open_sound);
        Media close_sound = new Media(new File("sounds/closing_sound.mp3").toURI().toString());
        MediaPlayer mp2 = new MediaPlayer(close_sound);

        //background music
        Media bit = new Media(new File("sounds/hard_bass.mp3").toURI().toString());
        MediaPlayer mp3 = new MediaPlayer(bit);


        //turn the keyboard on or off
        music_button.setOnMousePressed(e -> {
            if (!music_flag)
            {
                music_button.setTranslateY(-0.075);
                music_button.setMaterial(press_material);
                music_flag = true;
                if(turn_flag!=false)
                mp3.play();
                System.out.println("Wlaczony2!");
            }
            else
            {
                mp3.pause();
                music_button.setTranslateY(-0.1);
                music_button.setMaterial(realised);
                music_flag=false;
                System.out.println("Wylaczony2!");
            }

        });

        switch_button.setOnMousePressed(e -> {
            if (!turn_flag)
            {
                switch_button.setTranslateY(-0.075);
                switch_button.setMaterial(press_material);
                turn_flag = true;
                System.out.println("Wlaczony!");
                mp.play();
                mp2.stop();
            }
            else
            {
                mp.stop();
                mp2.play();
                switch_button.setTranslateY(-0.1);
                switch_button.setMaterial(realised);
                turn_flag = false;
                System.out.println("Wylaczony!");
                text_material.setDiffuseMap(new Image("file:images/keys/piano.jpg"));
            }
        });

        switch_button.setOnMouseReleased(event->
        {
            mp3.stop();
            music_button.setTranslateY(-0.1);
            music_button.setMaterial(realised);
            music_flag=false;
            System.out.println("Wylaczony2!");
        });


/*
        Box key1 = new Box(0.25, 0.1, 1);
        key1.setTranslateY(-0.25);
        key1.setTranslateZ(-0.45);
        key1.setTranslateX(-1.5);
        key1.setMaterial(realised);

        Box key2 = new Box(0.25, 0.1, 1);
        key2.setTranslateY(-0.25);
        key2.setTranslateZ(-0.35);
        key2.setTranslateX(-0.75);
        key2.setMaterial(realised);

        Box key3 = new Box(0.25, 0.1, 1);
        key3.setTranslateY(-0.25);
        key3.setTranslateZ(-0.30);
        key3.setTranslateX(0);
        key3.setMaterial(realised);

        Box key4 = new Box(0.25, 0.1, 1);
        key4.setTranslateY(-0.25);
        key4.setTranslateZ(-0.35);
        key4.setTranslateX(0.75);
        key4.setMaterial(realised);

        Box key5 = new Box(0.25, 0.1, 1);
        key5.setTranslateY(-0.25);
        key5.setTranslateZ(-0.45);
        key5.setTranslateX(1.5);
        key5.setMaterial(realised);
*/

        Box plane = new Box(3.8, 0.055, 1);
        plane.setTranslateY(-0.17);
        plane.setTranslateZ(-0.45);
        plane.setTranslateX(0);
        plane.setMaterial(plane_material);

        Box plane_music = new Box(2, 1, 0.055);
        plane_music.setTranslateY(-0.5);
        plane_music.setTranslateZ(1);
        plane_music.setTranslateX(0);
        plane_music.setRotationAxis(Rotate.X_AXIS);
        plane_music.setRotate(-8);
        plane_music.setMaterial(plane_material);

        Box text = new Box(2, 1, 0.001);
        text.setTranslateY(-0.5);
        text.setTranslateZ(0.97);
        text.setTranslateX(0);
        text.setRotationAxis(Rotate.X_AXIS);
        text.setRotate(-8);
        text.setMaterial(text_material);


        Box key1 = new Box(0.15, 0.1, 1);
        key1.setTranslateY(-0.25);
        key1.setTranslateZ(-0.45);
        key1.setTranslateX(-1.68);
        key1.setMaterial(realised);

        Box key2 = new Box(0.15, 0.1, 1);
        key2.setTranslateY(-0.25);
        key2.setTranslateZ(-0.45);
        key2.setTranslateX(-1.52);
        key2.setMaterial(realised2);

        Box key3 = new Box(0.15, 0.1, 1);
        key3.setTranslateY(-0.25);
        key3.setTranslateZ(-0.45);
        key3.setTranslateX(-1.36);
        key3.setMaterial(realised);

        Box key4 = new Box(0.15, 0.1, 1);
        key4.setTranslateY(-0.25);
        key4.setTranslateZ(-0.45);
        key4.setTranslateX(-1.20);
        key4.setMaterial(realised2);

        Box key5 = new Box(0.15, 0.1, 1);
        key5.setTranslateY(-0.25);
        key5.setTranslateZ(-0.45);
        key5.setTranslateX(-1.04);
        key5.setMaterial(realised);

        Box key6 = new Box(0.15, 0.1, 1);
        key6.setTranslateY(-0.25);
        key6.setTranslateZ(-0.45);
        key6.setTranslateX(-0.88);
        key6.setMaterial(realised2);

        Box key7 = new Box(0.15, 0.1, 1);
        key7.setTranslateY(-0.25);
        key7.setTranslateZ(-0.45);
        key7.setTranslateX(-0.72);
        key7.setMaterial(realised);

        Box key8 = new Box(0.15, 0.1, 1);
        key8.setTranslateY(-0.25);
        key8.setTranslateZ(-0.45);
        key8.setTranslateX(-0.56);
        key8.setMaterial(realised2);

        Box key9 = new Box(0.15, 0.1, 1);
        key9.setTranslateY(-0.25);
        key9.setTranslateZ(-0.45);
        key9.setTranslateX(-0.40);
        key9.setMaterial(realised);

        Box key10 = new Box(0.15, 0.1, 1);
        key10.setTranslateY(-0.25);
        key10.setTranslateZ(-0.45);
        key10.setTranslateX(-0.24);
        key10.setMaterial(realised2);

        Box key11 = new Box(0.15, 0.1, 1);
        key11.setTranslateY(-0.25);
        key11.setTranslateZ(-0.45);
        key11.setTranslateX(-0.08);
        key11.setMaterial(realised);

        Box key12 = new Box(0.15, 0.1, 1);
        key12.setTranslateY(-0.25);
        key12.setTranslateZ(-0.45);
        key12.setTranslateX(0.08);
        key12.setMaterial(realised2);

        Box key13 = new Box(0.15, 0.1, 1);
        key13.setTranslateY(-0.25);
        key13.setTranslateZ(-0.45);
        key13.setTranslateX(0.24);
        key13.setMaterial(realised);

        Box key14 = new Box(0.15, 0.1, 1);
        key14.setTranslateY(-0.25);
        key14.setTranslateZ(-0.45);
        key14.setTranslateX(0.40);
        key14.setMaterial(realised2);

        Box key15 = new Box(0.15, 0.1, 1);
        key15.setTranslateY(-0.25);
        key15.setTranslateZ(-0.45);
        key15.setTranslateX(0.56);
        key15.setMaterial(realised);

        Box key16 = new Box(0.15, 0.1, 1);
        key16.setTranslateY(-0.25);
        key16.setTranslateZ(-0.45);
        key16.setTranslateX(0.72);
        key16.setMaterial(realised2);

        Box key17 = new Box(0.15, 0.1, 1);
        key17.setTranslateY(-0.25);
        key17.setTranslateZ(-0.45);
        key17.setTranslateX(0.88);
        key17.setMaterial(realised);

        Box key18 = new Box(0.15, 0.1, 1);
        key18.setTranslateY(-0.25);
        key18.setTranslateZ(-0.45);
        key18.setTranslateX(1.04);
        key18.setMaterial(realised2);

        Box key19 = new Box(0.15, 0.1, 1);
        key19.setTranslateY(-0.25);
        key19.setTranslateZ(-0.45);
        key19.setTranslateX(1.20);
        key19.setMaterial(realised);

        Box key20 = new Box(0.15, 0.1, 1);
        key20.setTranslateY(-0.25);
        key20.setTranslateZ(-0.45);
        key20.setTranslateX(1.36);
        key20.setMaterial(realised2);

        Box key21 = new Box(0.15, 0.1, 1);
        key21.setTranslateY(-0.25);
        key21.setTranslateZ(-0.45);
        key21.setTranslateX(1.52);
        key21.setMaterial(realised);

        Box key22 = new Box(0.15, 0.1, 1);
        key22.setTranslateY(-0.25);
        key22.setTranslateZ(-0.45);
        key22.setTranslateX(1.68);
        key22.setMaterial(realised2);

        //import sounds for keys
        Media sound = new Media(new File("sounds/one.wav").toURI().toString());
        Media sound2 = new Media(new File("sounds/two.wav").toURI().toString());
        Media sound3 = new Media(new File("sounds/three.wav").toURI().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        MediaPlayer mediaPlayer2 = new MediaPlayer(sound2);
        MediaPlayer mediaPlayer3 = new MediaPlayer(sound3);


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB && turn_flag) {
                key1.setTranslateY(-0.225);
                key1.setRotationAxis(Rotate.X_AXIS);
                key1.setRotate(2.5);
                key1.setMaterial(press_material);
                playSound("sounds/c3.wav");
                text_material.setDiffuseMap(new Image(getClass().getResourceAsStream("c3.jpg")));

                //record("D:/informatyka/keyboard/sounds/c3.wav","D:/informatyka/keyboard/sounds/final.wav");
                Writter("c3");
            }

            if (event.getCode() == KeyCode.Q && turn_flag) {
                key2.setTranslateY(-0.225);
                key2.setRotationAxis(Rotate.X_AXIS);
                key2.setRotate(2.5);
                key2.setMaterial(press_material);
                playSound("sounds/d3.wav");
                text_material.setDiffuseMap(new Image("file:images/keys/d3.jpg"));

                record("D:/informatyka/keyboard/sounds/d3.wav","D:/informatyka/keyboard/sounds/final.wav");
                Writter("d3");

            }

            if (event.getCode() == KeyCode.W && turn_flag) {
                key3.setTranslateY(-0.225);
                key3.setRotationAxis(Rotate.X_AXIS);
                key3.setRotate(2.5);
                key3.setMaterial(press_material);
                playSound("sounds/e3.wav");
                text_material.setDiffuseMap(new Image("file:images/keys/e3.jpg"));

                Writter("e3");

            }

            if (event.getCode() == KeyCode.E && turn_flag) {
                key4.setTranslateY(-0.225);
                key4.setRotationAxis(Rotate.X_AXIS);
                key4.setRotate(2.5);
                key4.setMaterial(press_material);
                playSound("sounds/f3.wav");
                Writter("f3");
                text_material.setDiffuseMap(new Image("file:images/keys/f3.jpg"));

            }

            if (event.getCode() == KeyCode.R && turn_flag) {
                key5.setTranslateY(-0.225);
                key5.setRotationAxis(Rotate.X_AXIS);
                key5.setRotate(2.5);
                key5.setMaterial(press_material);
                playSound("sounds/g3.wav");
                Writter("g3");
                text_material.setDiffuseMap(new Image("file:images/keys/g3.jpg"));

            }

            if (event.getCode() == KeyCode.T && turn_flag) {
                key6.setTranslateY(-0.225);
                key6.setRotationAxis(Rotate.X_AXIS);
                key6.setRotate(2.5);
                key6.setMaterial(press_material);
                playSound("sounds/r3.wav");
                text_material.setDiffuseMap(new Image("file:images/keys/a3.jpg"));

            }

            if (event.getCode() == KeyCode.Y && turn_flag) {
                key7.setTranslateY(-0.225);
                key7.setRotationAxis(Rotate.X_AXIS);
                key7.setRotate(2.5);
                key7.setMaterial(press_material);
                playSound("sounds/b3.wav");
                Writter("b3");
                text_material.setDiffuseMap(new Image("file:images/keys/b3.jpg"));

            }

            if (event.getCode() == KeyCode.U && turn_flag) {
                key8.setTranslateY(-0.225);
                key8.setRotationAxis(Rotate.X_AXIS);
                key8.setRotate(2.5);
                key8.setMaterial(press_material);
                playSound("sounds/c4.wav");
                Writter("c4");
                text_material.setDiffuseMap(new Image("file:images/keys/c4.jpg"));

            }

            if (event.getCode() == KeyCode.I && turn_flag) {
                key9.setTranslateY(-0.225);
                key9.setRotationAxis(Rotate.X_AXIS);
                key9.setRotate(2.5);
                key9.setMaterial(press_material);
                playSound("sounds/d4.wav");
                Writter("d4");
                text_material.setDiffuseMap(new Image("file:images/keys/d4.jpg"));

            }

            if (event.getCode() == KeyCode.O && turn_flag) {
                key10.setTranslateY(-0.225);
                key10.setRotationAxis(Rotate.X_AXIS);
                key10.setRotate(2.5);
                key10.setMaterial(press_material);
                playSound("sounds/e4.wav");
                Writter("e4");
                text_material.setDiffuseMap(new Image("file:images/keys/e4.jpg"));

            }

            if (event.getCode() == KeyCode.P && turn_flag) {
                key11.setTranslateY(-0.225);
                key11.setRotationAxis(Rotate.X_AXIS);
                key11.setRotate(2.5);
                key11.setMaterial(press_material);
                playSound("sounds/f4.wav");
                Writter("f4");
                text_material.setDiffuseMap(new Image("file:images/keys/f4.jpg"));

            }

            if (event.getCode() == KeyCode.Z && turn_flag) {
                key12.setTranslateY(-0.225);
                key12.setRotationAxis(Rotate.X_AXIS);
                key12.setRotate(2.5);
                key12.setMaterial(press_material);
                playSound("sounds/g4.wav");
                Writter("g4");
                text_material.setDiffuseMap(new Image("file:images/keys/g4.jpg"));

            }

            if (event.getCode() == KeyCode.X && turn_flag) {
                key13.setTranslateY(-0.225);
                key13.setRotationAxis(Rotate.X_AXIS);
                key13.setRotate(2.5);
                key13.setMaterial(press_material);
                playSound("sounds/r4.wav");
                Writter("a4");
                text_material.setDiffuseMap(new Image("file:images/keys/a4.jpg"));

            }

            if (event.getCode() == KeyCode.C && turn_flag) {
                key14.setTranslateY(-0.225);
                key14.setRotationAxis(Rotate.X_AXIS);
                key14.setRotate(2.5);
                key14.setMaterial(press_material);
                playSound("sounds/b4.wav");
                Writter("b4");
                text_material.setDiffuseMap(new Image("file:images/keys/b4.jpg"));

            }

            if (event.getCode() == KeyCode.V && turn_flag) {
                key15.setTranslateY(-0.225);
                key15.setRotationAxis(Rotate.X_AXIS);
                key15.setRotate(2.5);
                key15.setMaterial(press_material);
                playSound("sounds/c5.wav");
                Writter("c5");
                text_material.setDiffuseMap(new Image("file:images/keys/c5.jpg"));

            }

            if (event.getCode() == KeyCode.B && turn_flag) {
                key16.setTranslateY(-0.225);
                key16.setRotationAxis(Rotate.X_AXIS);
                key16.setRotate(2.5);
                key16.setMaterial(press_material);
                playSound("sounds/d5.wav");
                Writter("d5");
                text_material.setDiffuseMap(new Image("file:images/keys/d5.jpg"));

            }

            if (event.getCode() == KeyCode.N && turn_flag) {
                key17.setTranslateY(-0.225);
                key17.setRotationAxis(Rotate.X_AXIS);
                key17.setRotate(2.5);
                key17.setMaterial(press_material);
                playSound("sounds/e5.wav");
                text_material.setDiffuseMap(new Image("file:images/keys/e5.jpg"));

            }

            if (event.getCode() == KeyCode.M && turn_flag) {
                key18.setTranslateY(-0.225);
                key18.setRotationAxis(Rotate.X_AXIS);
                key18.setRotate(2.5);
                key18.setMaterial(press_material);
                playSound("sounds/f5.wav");
                Writter("f5");
                text_material.setDiffuseMap(new Image("file:images/keys/f5.jpg"));

            }

            if (event.getCode() == KeyCode.COMMA && turn_flag) {
                key19.setTranslateY(-0.225);
                key19.setRotationAxis(Rotate.X_AXIS);
                key19.setRotate(2.5);
                key19.setMaterial(press_material);
                playSound("sounds/g5.wav");
                Writter("g5");
                text_material.setDiffuseMap(new Image("file:images/keys/g5.jpg"));

            }

            if (event.getCode() == KeyCode.PERIOD && turn_flag) {
                key20.setTranslateY(-0.225);
                key20.setRotationAxis(Rotate.X_AXIS);
                key20.setRotate(2.5);
                key20.setMaterial(press_material);
                playSound("sounds/r5.wav");
                Writter("a5");
                text_material.setDiffuseMap(new Image("file:images/keys/a5.jpg"));

            }

            if (event.getCode() == KeyCode.SLASH && turn_flag) {
                key21.setTranslateY(-0.225);
                key21.setRotationAxis(Rotate.X_AXIS);
                key21.setRotate(2.5);
                key21.setMaterial(press_material);
                playSound("sounds/b5.wav");
                Writter("b5");
                text_material.setDiffuseMap(new Image("file:images/keys/b5.jpg"));

                //record("D:/informatyka/keyboard/sounds/b5.wav","D:/informatyka/keyboard/sounds/final.wav");

            }

            if (event.getCode() == KeyCode.L && turn_flag) {
                key22.setTranslateY(-0.225);
                key22.setRotationAxis(Rotate.X_AXIS);
                key22.setRotate(2.5);
                key22.setMaterial(press_material);
                playSound("sounds/c6.wav");
                Writter("c6");
                text_material.setDiffuseMap(new Image("file:images/keys/c6.jpg"));

            }
        });

        /*
        scene.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.Q) {
                key1.setTranslateY(-0.25);
                key1.setMaterial(realised);
                mediaPlayer.stop();
            }
            if (event.getCode() == KeyCode.W) {
                key2.setTranslateY(-0.25);
                key2.setMaterial(realised);
                mediaPlayer2.stop();
            }
            if (event.getCode() == KeyCode.E) {
                key3.setTranslateY(-0.25);
                key3.setMaterial(realised);
                mediaPlayer3.stop();
            }
            if (event.getCode() == KeyCode.R) {
                key4.setTranslateY(-0.25);
                key4.setMaterial(realised);
                mediaPlayer.stop();
            }
            if (event.getCode() == KeyCode.T) {
                key5.setTranslateY(-0.25);
                key5.setMaterial(realised);
                mediaPlayer2.stop();
            }
        });
*/
        scene.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.TAB) {
                key1.setTranslateY(-0.25);
                key1.setRotate(0);
                key1.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.Q) {
                key2.setTranslateY(-0.25);
                key2.setRotate(0);
                key2.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.W) {
                key3.setTranslateY(-0.25);
                key3.setRotate(0);
                key3.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.E) {
                key4.setTranslateY(-0.25);
                key4.setRotate(0);
                key4.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.R) {
                key5.setTranslateY(-0.25);
                key5.setRotate(0);
                key5.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.T) {
                key6.setTranslateY(-0.25);
                key6.setRotate(0);
                key6.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.Y) {
                key7.setTranslateY(-0.25);
                key7.setRotate(0);
                key7.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.U) {
                key8.setTranslateY(-0.25);
                key8.setRotate(0);
                key8.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.I) {
                key9.setTranslateY(-0.25);
                key9.setRotate(0);
                key9.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.O) {
                key10.setTranslateY(-0.25);
                key10.setRotate(0);
                key10.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.P) {
                key11.setTranslateY(-0.25);
                key11.setRotate(0);
                key11.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.Z) {
                key12.setTranslateY(-0.25);
                key12.setRotate(0);
                key12.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.X) {
                key13.setTranslateY(-0.25);
                key13.setRotate(0);
                key13.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.C) {
                key14.setTranslateY(-0.25);
                key14.setRotate(0);
                key14.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.V) {
                key15.setTranslateY(-0.25);
                key15.setRotate(0);
                key15.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.B) {
                key16.setTranslateY(-0.25);
                key16.setRotate(0);
                key16.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.N) {
                key17.setTranslateY(-0.25);
                key17.setRotate(0);
                key17.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.M) {
                key18.setTranslateY(-0.25);
                key18.setRotate(0);
                key18.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.COMMA) {
                key19.setTranslateY(-0.25);
                key19.setRotate(0);
                key19.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.PERIOD) {
                key20.setTranslateY(-0.25);
                key20.setRotate(0);
                key20.setMaterial(realised2);
            }

            if (event.getCode() == KeyCode.SLASH) {
                key21.setTranslateY(-0.25);
                key21.setRotate(0);
                key21.setMaterial(realised);
            }

            if (event.getCode() == KeyCode.L) {
                key22.setTranslateY(-0.25);
                key22.setRotate(0);
                key22.setMaterial(realised2);
            }
        });

        sceneRoot.getChildren().addAll(key1,key2,key3,key4,key5, key6, key7, key8, key9, key10,
                key11, key12, key13, key14, key15, key16, key17, key18, key19,
                key20, key21, key22, switch_button, music_button, plane, plane_music, text);
        // sceneRoot.getChildren().addAll(key1,key2,key3,key4,key5,switch_button, music_button);

        sceneRoot.getTransforms().addAll(rotateX, rotateY);
        primaryStage.setTitle("Projekt");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(url));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                    Thread.sleep(5000);

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public static void record (String wavFile1, String music){
        //String wavFile1 = "D:\\wav1.wav";
        //String wavFile2 = "D:\\wav2.wav";

        try {
            AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(wavFile1));
            AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(music));

            AudioInputStream appendedFiles =
                    new AudioInputStream(
                            new SequenceInputStream(clip2, clip1),
                            clip1.getFormat(),
                            clip1.getFrameLength() + clip2.getFrameLength());

            AudioSystem.write(appendedFiles,
                    AudioFileFormat.Type.WAVE,
                    new File(music));
        } catch(FileNotFoundException s){
            try {
                AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(wavFile1));
                AudioSystem.write(clip1,
                        AudioFileFormat.Type.WAVE,
                        new File(music));
            }catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Writter(String fileContent)
    {
        try {
            fileContent += "\r\n";
            File file = new File("C:\\Users\\Daniel\\Desktop\\text2.txt");
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(fileContent);

            br.close();
            fr.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
