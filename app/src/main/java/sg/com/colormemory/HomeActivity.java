package sg.com.colormemory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import sg.com.colormemory.database.HighScoreDataSource;
import sg.com.colormemory.entity.Card;
import sg.com.colormemory.entity.HighScore;

import java.sql.SQLException;
import java.util.*;

public class    HomeActivity extends AppCompatActivity {

    private static int ROW_COUNT = -1;
    private static int COL_COUNT = -1;
    private Context context;
    private Drawable backImage;
    private int [] [] cards;
    private List<Drawable> images;
    private Card firstCard;
    private Card seconedCard;
    private ButtonListener buttonListener;

    public static HighScoreDataSource datasource;
    private static Object lock = new Object();

    int turns;
    static int score = 0;
    private TableLayout mainTable;
    private UpdateCardsHandler handler;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//    }
//}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        datasource = new HighScoreDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        handler = new UpdateCardsHandler();
        loadImages();
        setContentView(R.layout.activity_home);

        backImage =  getResources().getDrawable(R.drawable.card_bg);

        buttonListener = new ButtonListener();

        mainTable = (TableLayout)findViewById(R.id.TableLayout03);
        context  = mainTable.getContext();

        Button button = (Button) findViewById(R.id.highScoreBtn);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HighScoreActivity.class);
                startActivity(intent);
            }
        });

        newGame(4,4);
    }

    private void newGame(int c, int r) {
        ROW_COUNT = r;
        COL_COUNT = c;
        score = 0;
        cards = new int [COL_COUNT] [ROW_COUNT];


        mainTable.removeView(findViewById(R.id.TableRow01));
        mainTable.removeView(findViewById(R.id.TableRow02));

        TableRow tr = ((TableRow)findViewById(R.id.TableRow03));
        tr.removeAllViews();

        mainTable = new TableLayout(context);
        tr.addView(mainTable);

        for (int y = 0; y < ROW_COUNT; y++) {
            mainTable.addView(createRow(y));
        }

        firstCard=null;
        loadCards();

        turns=0;
        ((TextView)findViewById(R.id.tv1)).setText("Score: "+score);


    }

    private void loadImages() {
        images = new ArrayList<Drawable>();

        images.add(getResources().getDrawable(R.drawable.colour1));
        images.add(getResources().getDrawable(R.drawable.colour2));
        images.add(getResources().getDrawable(R.drawable.colour3));
        images.add(getResources().getDrawable(R.drawable.colour4));
        images.add(getResources().getDrawable(R.drawable.colour5));
        images.add(getResources().getDrawable(R.drawable.colour6));
        images.add(getResources().getDrawable(R.drawable.colour7));
        images.add(getResources().getDrawable(R.drawable.colour8));

    }

    private void loadCards(){
        try{
            int size = ROW_COUNT*COL_COUNT;

            Log.i("loadCards()", "size=" + size);

            ArrayList<Integer> list = new ArrayList<Integer>();

            for(int i=0;i<size;i++){
                list.add(new Integer(i));
            }


            Random r = new Random();

            for(int i=size-1;i>=0;i--){
                int t=0;

                if(i>0){
                    t = r.nextInt(i);
                }

                t=list.remove(t).intValue();
                cards[i%COL_COUNT][i/COL_COUNT]=t%(size/2);

                Log.i("loadCards()", "card["+(i%COL_COUNT)+
                        "]["+(i/COL_COUNT)+"]=" + cards[i%COL_COUNT][i/COL_COUNT]);
            }
        }
        catch (Exception e) {
            Log.e("loadCards()", e+"");
        }

    }

    private TableRow createRow(int y){
        TableRow row = new TableRow(context);
        row.setHorizontalGravity(Gravity.CENTER);

        for (int x = 0; x < COL_COUNT; x++) {
            row.addView(createImageButton(x,y));
        }
        return row;
    }

    private View createImageButton(int x, int y){
        Button button = new Button(context);
        button.setBackgroundDrawable(backImage);
        button.setId(100*x+y);
        button.setOnClickListener(buttonListener);
        return button;
    }

    class ButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            synchronized (lock) {
                if(firstCard!=null && seconedCard != null){
                    return;
                }
                int id = v.getId();
                int x = id/100;
                int y = id%100;
                turnCard((Button) v, x, y);
            }

        }

        private void turnCard(Button button,int x, int y) {
            button.setBackgroundDrawable(images.get(cards[x][y]));

            if(firstCard==null){
                firstCard = new Card(button,x,y);
            }
            else{

                if(firstCard.x == x && firstCard.y == y){
                    return; //the user pressed the same card
                }

                seconedCard = new Card(button,x,y);

                //turns++;
                ((TextView)findViewById(R.id.tv1)).setText("Score: "+score);


                TimerTask tt = new TimerTask() {

                    @Override
                    public void run() {
                        try{
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        }
                        catch (Exception e) {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(tt, 1300);
            }


        }

    }

    class UpdateCardsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }
        public void checkCards(){
            if(cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]){
                firstCard.button.setVisibility(View.INVISIBLE);
                seconedCard.button.setVisibility(View.INVISIBLE);
                score = score+2;
                turns++;
            }
            else {
                seconedCard.button.setBackgroundDrawable(backImage);
                firstCard.button.setBackgroundDrawable(backImage);
                score = score-1;
            }
            ((TextView)findViewById(R.id.tv1)).setText("Score: "+score);
            firstCard=null;
            seconedCard=null;
            if(turns == 8){

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.username);
                //((EditText) promptsView.findViewById(R.id.scoreVal)).setText(score);
                final EditText scorVal = (EditText) promptsView.findViewById(R.id.scoreVal);
                scorVal.setText(String.valueOf(score));


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        if(userInput.getText().toString().length()<1){
                                            alertDialogBuilder.create();

                                        }else{
                                            System.out.println("Time to insert!!" + userInput.getText().toString() + scorVal.getText().toString());
                                            HighScore highScore = new HighScore(userInput.getText().toString(),scorVal.getText().toString());
                                            highScore = datasource.createHighScore(highScore);
                                            System.out.println("After insert: " + highScore.getName() + highScore.getScore());
                                        }
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                newGame(4, 4);
                }

            }
        }
    }

