package com.example.memory;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    TextView tv_p1, tv_p2, tv_c; //player1, player2, current player
    ImageView[] iv; //Cards (UI)
    ImageView ivs1, ivs2; //Flipped cards
    Integer[] cards; //Card positions
    int[] img; //Card images
    boolean first; //true - Picking the 1st out of 2 cards
    boolean[] matched; //true - Cards that were matched to their pairs
    int score1, score2, card1, card2; //Players'scores and flipped cards'ids
    TableLayout tl_c; //for finding ivs via tags

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup
        tv_c = findViewById(R.id.textView_Current);
        tv_c.setText("←");
        tv_p1 = findViewById(R.id.textView_Player1);
        tv_p2 = findViewById(R.id.textView_Player2);
        score1 = 0;
        score2 = 0;
        first = true;
        tl_c = findViewById(R.id.TL_Cards);

        //Load images

        img = new int[30];
        img[0] = R.drawable.i0;
        img[1] = R.drawable.i1;
        img[2] = R.drawable.i2;
        img[3] = R.drawable.i3;
        img[4] = R.drawable.i4;
        img[5] = R.drawable.i5;
        img[6] = R.drawable.i6;
        img[7] = R.drawable.i7;
        img[8] = R.drawable.i8;
        img[9] = R.drawable.i9;
        img[10] = R.drawable.i10;
        img[11] = R.drawable.i11;
        img[12] = R.drawable.i12;
        img[13] = R.drawable.i13;
        img[14] = R.drawable.i14;

        //Initialize cards
        iv = new ImageView[30];
        cards = new Integer[30];
        matched = new boolean[30];
        for(int i = 0; i < 30; i++)
            cards[i] = i;
        //Shuffle cards
        Collections.shuffle(Arrays.asList(cards));

        for(int i = 0; i < 30; i++){
            iv[i] = tl_c.findViewWithTag("" + i);
            matched[i] = false;
        }


        //OnClickListeners
        for(int i = 0; i < 30; i++){
            final int finalI = i;
            iv[finalI].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iv[finalI].isEnabled())
                        operateCard(iv[finalI], Integer.parseInt((String) v.getTag()));
                }
            });
        }
    }

    private void operateCard(ImageView ivt, int uiid){
        ivt.setImageResource(img[cards[uiid]%15]); //The UI card that was clicked on

        if(first){ //Only one card flipped over
            ivs1 = findViewById(ivt.getId());
            card1 = uiid;
            //Disable input on flipped card
            iv[uiid].setEnabled(false);
            first = false;
        } else { //Two cards flipped over
            ivs2 = findViewById(ivt.getId());
            card2 = uiid;
            first = true;
            //Check if flipped cards match
            if(cards[card1]%15 == cards[card2]%15){ //match; Assign points
                if(tv_c.getText().equals("←")){
                    score1++;
                    tv_p1.setText("Player 1: " + score1);
                    matched[card1] = true;
                    matched[card2] = true;
                } else {
                    score2++;
                    tv_p2.setText("Player 2: " + score2);
                    matched[card1] = true;
                    matched[card2] = true;
                }
                //Check end condition; Play again dialogue
                if(score1 + score2 >= 15){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Game over!! Scoreboard:\nPlayer 1: " + score1 + "\nPlayer 2: " + score2)
                            .setCancelable(false).setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }else{ //mismatch
                //Disable inputs on all cards (+delay)
                for(int i = 0; i < 30; i++){
                    iv[i].setEnabled(false);
                }
                final Handler handler = new Handler(); //Keep the cards showing for 2 seconds, then flip back
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivs1.setImageResource(R.drawable.i15);
                        ivs2.setImageResource(R.drawable.i15);

                        //Switch players' turns
                        if(tv_c.getText().equals("←")){
                            tv_c.setText("→");
                        } else {
                            tv_c.setText("←");
                        }
                        //Re-enable input on cards that weren't matched yet
                        for(int i = 0; i < 30; i++) {
                            if (!matched[i]) {
                                iv[i].setEnabled(true);
                            }
                        }
                    }
                }, 2000);
            }
        }
    }
}