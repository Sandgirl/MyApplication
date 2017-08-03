package com.example.admin.myapplication2;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class MainActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.firstvideo)
    ImageView firstvideo;
    @BindView(R.id.targetView)
    ImageView targetView;
    @BindView(R.id.targetButton)
    Button targetButton;
    @BindView(R.id.playbuttonFirstChoice)
    Button playbuttonFirstChoice;
    @BindView(R.id.playbuttonSecondChoice)
    Button playbuttonSecondChoice;
    @BindView(R.id.playbuttonFirstVideo)
    Button playbuttonFirstVideo;
    @BindView(R.id.target)
    LinearLayout target;
    @BindViews({R.id.choice1, R.id.choice2})
    List<ImageView> choiceList;

    VideoView video;
    int firstDrawableScene;
    int selectedChoiceIndex;
    int secondChoiceIndex;
    List<Integer> videosForOneScene = new ArrayList<>();
    String firstDrawableSceneName;
    String randomDrawableSceneName;
    String nextDrawableSceneName;
    int nextDrawableScene;
    List<Integer> keyList;
    Map<Integer, String> drawableIdsforFirstVideo = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        selectedChoiceIndex = randomChoice();
        secondChoiceIndex = 1 - selectedChoiceIndex;

        playbuttonFirstChoice.setOnClickListener(this);
        playbuttonSecondChoice.setOnClickListener(this);
        playbuttonFirstVideo.setOnClickListener(this);

        //es wird per Zufall eine Filsequenzausgewählt und davon die erste Szene (nur eine ImageDatei)
        List<Integer> keyListFirstDrawableScene = new ArrayList<>(getFirstVideoDrawableIds().keySet());
        firstDrawableScene = keyListFirstDrawableScene.get(new Random().nextInt(keyListFirstDrawableScene.size()));
        firstDrawableSceneName = getFirstVideoDrawableIds().get(firstDrawableScene);
        //ausgewählte Szene wird dem ImageView zugeteilt
        firstvideo.setImageResource(firstDrawableScene);

        //es wird eine Map erstellt mit alles Szenebildern einer Szene mit Name und der Id
        getVideoDrawableNextId();

        //die direkt auf die erste Szene folgende Szene
        keyList = new ArrayList<>(drawableIdsforFirstVideo.keySet());
        nextDrawableScene = firstDrawableScene + 1;
        nextDrawableSceneName = drawableIdsforFirstVideo.get(nextDrawableScene);
        drawableIdsforFirstVideo.remove(nextDrawableScene);
        keyList.remove(new Integer(nextDrawableScene));
        keyList.remove(new Integer(firstDrawableScene));

        //eine zufällige Szene aus der gewählten Reihe
        int randomDrawableScene = keyList.get(new Random().nextInt(keyList.size()));
        randomDrawableSceneName = drawableIdsforFirstVideo.get(randomDrawableScene);

        getVideosForOneScene();

        targetButton.setVisibility(View.INVISIBLE);

        choiceList.get(selectedChoiceIndex).setTag(nextDrawableScene);
        choiceList.get(selectedChoiceIndex).setImageResource(nextDrawableScene);
        choiceList.get(secondChoiceIndex).setTag(randomDrawableScene);
        choiceList.get(secondChoiceIndex).setImageResource(randomDrawableScene);

        MyDragListener mDragListen = new MyDragListener();

        for (ImageView imageView : choiceList) {
            imageView.setOnLongClickListener(new MyClickListener());
            imageView.setOnDragListener(mDragListen);
        }

        target.setOnDragListener(mDragListen);
    }


    public void clearVideoView() {
        video = new VideoView(this);

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                layout.removeView(video);
            }
        });
    }

    public void setVideo(String sceneNumber, String sceneNumberGroup) {
        for (Field f : com.example.admin.myapplication2.R.raw.class.getFields()) {
            Context mContext = getApplicationContext();
            String field = f.getName();
            int sceneField = getResources().getIdentifier(field, "raw", mContext.getPackageName());

            if (field.contains(sceneNumber) & field.contains(sceneNumberGroup)) {
                video.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + sceneField));
                startVideo(video);

            }
        }
    }

    @Override
    public void onClick(View v) {
        String sceneNumberGroup;
        String sceneNumber;

        clearVideoView();

        int selectedChoiceId = choiceList.get(selectedChoiceIndex).getId();
        layout.removeView(video);

        switch (v.getId()) {
            case R.id.playbuttonFirstChoice:
                //choice1
                if (choiceList.get(0).getId() == selectedChoiceId) {
                    video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            layout.removeView(video);
                        }
                    });

                    sceneNumberGroup = nextDrawableSceneName.substring(1, 3);
                    sceneNumber = nextDrawableSceneName.substring(5);
                } else {
                    sceneNumberGroup = randomDrawableSceneName.substring(1, 3);
                    sceneNumber = randomDrawableSceneName.substring(5);
                }

                setVideo(sceneNumber, sceneNumberGroup);
                playbuttonFirstChoice.setVisibility(View.INVISIBLE);
                break;
            case R.id.playbuttonSecondChoice:
                //choice2

                if (choiceList.get(1).getId() == selectedChoiceId) {
                    video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            layout.removeView(video);
                        }
                    });

                    sceneNumberGroup = nextDrawableSceneName.substring(1, 3);
                    sceneNumber = nextDrawableSceneName.substring(5);
                } else {
                    sceneNumberGroup = randomDrawableSceneName.substring(1, 3);
                    sceneNumber = randomDrawableSceneName.substring(5);
                }

                setVideo(sceneNumber, sceneNumberGroup);
                playbuttonSecondChoice.setVisibility(View.INVISIBLE);
                break;
            case R.id.playbuttonFirstVideo:
                //firstvideo
                sceneNumberGroup = firstDrawableSceneName.substring(1, 3);
                sceneNumber = firstDrawableSceneName.substring(5);

                setVideo(sceneNumber, sceneNumberGroup);
                playbuttonFirstVideo.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public int randomChoice() {
        selectedChoiceIndex = new Random().nextInt(choiceList.size());
        secondChoiceIndex = 1 - selectedChoiceIndex;
        return selectedChoiceIndex;
    }

    public void startVideo(VideoView actualVideo) {
        layout.removeView(actualVideo);
        layout.addView(actualVideo);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.view.ViewGroup.LayoutParams layoutParams = video.getLayoutParams();
        layoutParams.width = metrics.widthPixels;
        layoutParams.height = metrics.heightPixels;
        actualVideo.setLayoutParams(layoutParams);
        actualVideo.setMediaController(new MediaController(this));
        actualVideo.start();
    }

    // Map mit allen Namen und Ids ersten der Szenebilder
    public Map<Integer, String> getFirstVideoDrawableIds() {
        Map<Integer, String> drawableIds = new HashMap<>();

        for (Field f : com.example.admin.myapplication2.R.drawable.class.getDeclaredFields()) {
            String field = f.getName();

            if (field.startsWith("s") && field.endsWith("01")) {
                drawableIds.put((getResources().getIdentifier(field, "drawable", getPackageName())), field);
            }
        }
        return drawableIds;
    }


    //Map mit allen Name und id  Szenebildern einer gewählten Serie
    public Map<Integer, String> getVideoDrawableNextId() {
        //Liste mit allen Videos des zum zuerst gewählten Video
        String firstScene = getResources().getResourceEntryName(firstDrawableScene);
        String subString = firstScene.substring(0, 3);

        for (Field f : com.example.admin.myapplication2.R.drawable.class.getDeclaredFields()) {
            String field = f.getName();

            if (field.contains(subString) && !field.endsWith("01")) {
                drawableIdsforFirstVideo.put(getResources().getIdentifier(field, "drawable", getPackageName()), field);
            }
        }
        return drawableIdsforFirstVideo;
    }

    //Zuordnung des Videos
    public List<Integer> getVideosForOneScene() {

        String firstScene = getResources().getResourceEntryName(firstDrawableScene);
        String subString = firstScene.substring(1, 3);


        for (Field f : com.example.admin.myapplication2.R.raw.class.getDeclaredFields()) {
            String field = f.getName();

            if (field.contains(subString)) {
                videosForOneScene.add(getResources().getIdentifier(field, "raw", getPackageName()));
            }
        }

        return videosForOneScene;
    }

    public void nextRound() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (drawableIdsforFirstVideo.size()) {

            case 1:
                targetView.setImageResource(R.drawable.frame);
                selectedChoiceIndex = randomChoice();
                nextDrawableScene++;
                nextDrawableSceneName = drawableIdsforFirstVideo.get(nextDrawableScene);
                choiceList.get(selectedChoiceIndex).setTag(nextDrawableScene);
                choiceList.get(selectedChoiceIndex).setImageResource(nextDrawableScene);
                if (choiceList.get(selectedChoiceIndex) == choiceList.get(0)) {
                    playbuttonFirstChoice.setVisibility(View.VISIBLE);
                    choiceList.get(1).setImageResource(R.drawable.frameafter);
                } else {
                    playbuttonSecondChoice.setVisibility(View.VISIBLE);
                    choiceList.get(0).setImageResource(R.drawable.frameafter);
                }
                drawableIdsforFirstVideo.remove(nextDrawableScene);
                keyList.remove(new Integer(nextDrawableScene));
                break;
            case 0:
                Context context = getApplicationContext();

                Toast.makeText(context, "Bravo Sie haben alle Videos richtig sortiert",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                if (drawableIdsforFirstVideo.size() > 1) {

                    targetView.setImageResource(R.drawable.frame);
                    targetView.setTag(R.drawable.frame);
                    selectedChoiceIndex = randomChoice();
                    secondChoiceIndex = 1 - selectedChoiceIndex;

                    //die direkt auf die erste Szene folgende Szene
                    nextDrawableScene++;
                    //TODO:Szene rauslöschen
                    nextDrawableSceneName = drawableIdsforFirstVideo.get(nextDrawableScene);
                    drawableIdsforFirstVideo.remove(nextDrawableScene);
                    keyList.remove(new Integer(nextDrawableScene));

                    //eine zufällige Szene aus der gewählten Reihe
                    int randomDrawableScene = keyList.get(new Random().nextInt(keyList.size()));
                    randomDrawableSceneName = drawableIdsforFirstVideo.get(randomDrawableScene);

                    choiceList.get(selectedChoiceIndex).setTag(nextDrawableScene);
                    choiceList.get(selectedChoiceIndex).setImageResource(nextDrawableScene);
                    choiceList.get(secondChoiceIndex).setTag(randomDrawableScene);
                    choiceList.get(secondChoiceIndex).setImageResource(randomDrawableScene);

                    playbuttonFirstChoice.setVisibility(View.VISIBLE);
                    playbuttonSecondChoice.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    //Klick Listener, setzt vor dem draggen ein, also anklicken ziehen...
    private final class MyClickListener implements View.OnLongClickListener {
        public boolean onLongClick(View view) {
            if (playbuttonFirstVideo.getVisibility() == View.INVISIBLE & playbuttonSecondChoice.getVisibility() == View.INVISIBLE & playbuttonFirstChoice.getVisibility() == View.INVISIBLE) {
                Context context = getApplicationContext();
                // create it from the object's tag
                ClipData.Item item = new ClipData.Item(view.getTag().toString());

                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, //data to be dragged
                        shadowBuilder, //drag shadow
                        view, //local data about the drag and drop operation
                        0   //no needed flags
                );
            } else {
                Context context = getApplicationContext();
                Toast.makeText(context, "Bitte erst alle Videos ansehen",
                        Toast.LENGTH_LONG).show();
            }

            return true;
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //event enthält das Objekt was bewegt wird und was passiert also noch am draggen, losgelassen ect.
            // view ist der view wo man das objekt loslässt

            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    break;

                case DragEvent.ACTION_DROP:

                    ImageView ls = (ImageView) event.getLocalState();
                    //TODO:+i ändern
                    if (v == findViewById(R.id.target) && (Integer) ls.getTag() == nextDrawableScene) {

                        targetView.setImageResource(firstDrawableScene + 1);
                        ls.setImageResource(R.drawable.frameafter);
                        targetView.setTag(firstDrawableScene + 1);
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Herzlichen Glückwunsch Du hast die Aufgabe gelöst",
                                Toast.LENGTH_LONG).show();

                        if ((Integer) targetView.getTag() != R.drawable.frame) {
                            nextRound();
                        }
                    } else {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Deine Wahl ist nicht korrekt",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:

                default:
                    break;
            }
            return true;
        }
    }


}

