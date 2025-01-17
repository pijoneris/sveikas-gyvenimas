package com.sveikata.productions.mabe.sveikasgyvenimas;

/**
 * Created by Martyno on 2016.10.03.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * Created by Benas on 9/18/2016.
 */
public class PlayAdapter extends  RecyclerView.Adapter<PlayAdapter.ViewHolder> {

    private boolean show = false;
    private Context context;
    private ArrayList<PlayInfoHolder> play_info_holder;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;


    public PlayAdapter(Context context, ArrayList<PlayInfoHolder> play_info_holder) {
        this.context = context;
        this.play_info_holder = play_info_holder;

        layoutInflater = LayoutInflater.from(context);
        sharedPreferences = context.getSharedPreferences("DataPrefs", Context.MODE_PRIVATE);

    }

    public void remove(int position) {
        play_info_holder.remove(position);
        notifyItemRemoved(position);

    }

    public void add(PlayInfoHolder info, int position) {
        play_info_holder.add(position,info);
        notifyDataSetChanged();
    }




    @Override
    public int getItemViewType(int position) {
        return play_info_holder.get(position).getType();
    }

    @Override
    public PlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       PlayAdapter.ViewHolder viewHolder = null;

        Log.i("type", String.valueOf(viewType));

        switch (viewType){
            case 0:
                View layout_preview = layoutInflater.inflate(R.layout.calculator_preview, parent, false);
                viewHolder = new ViewHolder(layout_preview, 0);
                return viewHolder;
            case 1:
                View new_challenge = layoutInflater.inflate(R.layout.new_challenge_layout, parent, false);
                viewHolder = new ViewHolder(new_challenge, 1);
                return viewHolder;

            case 2:
                View send_challenge = layoutInflater.inflate(R.layout.send_challenge, parent,false);
                viewHolder = new ViewHolder(send_challenge,2);
                return viewHolder;

            case 3:
                View completed_challenge = layoutInflater.inflate(R.layout.challenge_icon, parent, false);
                viewHolder = new ViewHolder(completed_challenge, 3);
                return viewHolder;
            case 4:
                View challenge_in_progress = layoutInflater.inflate(R.layout.challenge_in_progress, parent, false);
                viewHolder = new ViewHolder(challenge_in_progress, 4);
                return viewHolder;

            case 5:
                View game = layoutInflater.inflate(R.layout.start_playing, parent, false);
                viewHolder = new ViewHolder(game, 5);
                return viewHolder;
            case 6:
                View game_quiz = layoutInflater.inflate(R.layout.start_playing_quiz, parent, false);
                viewHolder = new ViewHolder(game_quiz, 6);
                return viewHolder;
            case 7:
                View organs = layoutInflater.inflate(R.layout.human_organs, parent, false);
                viewHolder = new ViewHolder(organs, 7);
                return viewHolder;


        }

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(PlayAdapter.ViewHolder holder, int position) {
        final PlayInfoHolder data = play_info_holder.get(position);

        switch (data.getType()){

            case 0: //Preview calculators


                break;
            case 1: //current challenge layout
                holder.challenge_title.setText(data.getChallengeTitle());
                holder.challenge_description.setText(data.getChallengeDescription());
                holder.challenge_sender.setText(data.getChallengeSender());
                holder.challenge_note.setText("Pastaba:" +data.getChallengeNote());
                holder.timer.setText("Iššūkis truks " + data.getChallengeTime() + " d.");

                break;
            case 2: //Send challenge layout
                break;

            case 3: //Completed challenges
                holder.completed_challenge_description.setText(data.getChallengeDescription());
                holder.completed_challenge_title.setText(data.getChallengeTitle());
                holder.completed_challenge_note.setText("Pastaba:" +data.getChallengeNote());
                holder.completed_challenge_sender.setText(data.getChallengeSender());
                holder.completed_challenge_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckingUtils.shareChallenge(data.getChallengeTitle(),
                                context, "www.google.com",data.getChallengeDescription() );
                    }
                });


                break;
            case 4: //Challenge in progress layout
                holder.challenge_in_progress_description.setText(data.getChallengeDescription());
                holder.challenge_in_progress_title.setText(data.getChallengeTitle());
                holder.challenge_in_progress_sender.setText(data.getChallengeSender());
                holder.challenge_in_progress_note.setText("Pastaba:" +data.getChallengeNote());
                countDownStart(holder.days_proggress, holder.hours_progress, holder.minutes_progress, holder.seconds_progress, data.getChallengeTime());

                break;




        }

    }


    public void countDownStart(final TextView dayView, final TextView hoursView, final TextView minutesView, final TextView secondsView, final String data) {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Here Set your Event Date
                    Date futureDate = dateFormat.parse(data);
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        dayView.setText("" + String.format("%02d", days));
                        hoursView.setText("" + String.format("%02d", hours));
                        minutesView.setText("" + String.format("%02d", minutes));
                        secondsView.setText("" + String.format("%02d", seconds));
                    } else {
                        String username = sharedPreferences.getString("username", "");
                        String password = sharedPreferences.getString("password", "");
                        handler.removeCallbacks(this);
                        new ServerManager(context, "COMPLETE_CHALLENGE").execute("COMPLETE_CHALLENGE", username, password);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable, 0);
    }

    @Override
    public int getItemCount() {
        return play_info_holder.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //Human organs
        private GifImageView human_organs;
        private TextView link_to_more_info;
        private TextView link_to_bbc_movie;
        private TextView link_external_movie;

        private Typeface verdanaFont;


        //Calculator preview layout
        private TextView calculator_text;
        private TextView challenge_text;
        private ImageView arrow_left;
        private ImageView arrow_right;
        private ImageView calculator_preview_image;
        private Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        private Animation top_down_anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/Verdana.ttf");
        private int which_image;
        boolean isAnimRunning = false;



        //Send challenge
        private GifImageView go_to_send_activity;

        //Start Playing
        private GifImageView start_playing;

        //New challenge layout
        private TextView timer;
        private TextView challenge_title;
        private TextView challenge_note;
        private TextView challenge_description;
        private ImageButton accept_challenge;
        private ImageButton decline_challenge;
        private TextView challenge_sender;


        //Challenge in progress layout
        private TextView challenge_in_progress_title;
        private TextView challenge_in_progress_description;
        private TextView challenge_in_progress_sender;
        private TextView challenge_in_progress_note;

        //Completed challenge layout
        private TextView completed_challenge_title;
        private TextView completed_challenge_description;
        private TextView completed_challenge_sender;
        private TextView completed_challenge_note;
        private AppCompatButton completed_challenge_share;

        public TextView days_proggress;
        public TextView hours_progress;
        public TextView minutes_progress;
        public TextView seconds_progress;

        private ImageButton failed;
        private ImageView get_random_question;
        private GifImageView start_playing_quiz;
        public ViewHolder(View itemView, int type) {
            super(itemView);
            


            switch (type) {

                case 0: //Calculator previews

                    arrow_left = (ImageView) itemView.findViewById(R.id.arrow_left);
                    arrow_right = (ImageView) itemView.findViewById(R.id.arrow_right);
                    calculator_preview_image = (ImageView)itemView.findViewById(R.id.calculator_preview_image);
                    calculator_preview_image.setImageResource(R.drawable.calories_calculator);
//                    challenge_text = (TextView) itemView.findViewById(R.id.challanges_text);

//                    challenge_text.setTypeface(verdanaFont);


                    arrow_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!isAnimRunning){
                                which_image--;
                                if(which_image<0){
                                    which_image=5;
                                }
                                switch (which_image){
                                    case 0:
                                        calculator_preview_image.setImageResource(R.drawable.calories_calculator);
                                        break;
                                    case 1:
                                        calculator_preview_image.setImageResource(R.drawable.water_calculator);
                                        break;
                                    case 2:
                                        calculator_preview_image.setImageResource(R.drawable.your_drink);
                                        break;
                                    case 3:
                                        calculator_preview_image.setImageResource(R.drawable.info_sheet);
                                        break;
                                    case 4:
                                        calculator_preview_image.setImageResource(R.drawable.limit_calculator);
                                        break;
                                    case 5:
                                        calculator_preview_image.setImageResource(R.drawable.calories_in_alcohol);
                                        break;
                                }
                            }
                        }
                    });

                    arrow_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!isAnimRunning){
                                which_image++;
                                if(which_image>=6){
                                    which_image=0;
                                }

                                switch (which_image){
                                    case 0:
                                        calculator_preview_image.setImageResource(R.drawable.calories_calculator);
                                        break;
                                    case 1:
                                        calculator_preview_image.setImageResource(R.drawable.water_calculator);
                                        break;
                                    case 2:
                                        calculator_preview_image.setImageResource(R.drawable.your_drink);
                                        break;
                                    case 3:
                                        calculator_preview_image.setImageResource(R.drawable.info_sheet);
                                        break;
                                    case 4:
                                        calculator_preview_image.setImageResource(R.drawable.limit_calculator);
                                        break;
                                    case 5:
                                        calculator_preview_image.setImageResource(R.drawable.calories_in_alcohol);
                                        break;

                                }
                            }
                        }
                    });

                    calculator_preview_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchCalculator(which_image);
                        }
                    });

                    break;

                case 1: //New challenge layout


                    challenge_description = (TextView) itemView.findViewById(R.id.new_challenge_description);
                    challenge_title = (TextView) itemView.findViewById(R.id.new_challenge_title);
                    challenge_note = (TextView) itemView.findViewById(R.id.new_challenge_note);
                    accept_challenge = (ImageButton) itemView.findViewById(R.id.accept_button);
                    decline_challenge = (ImageButton) itemView.findViewById(R.id.decline_button);
                    challenge_sender = (TextView) itemView.findViewById(R.id.challenge_sender);
                    timer = (TextView) itemView.findViewById(R.id.days);
                    timer.setTypeface(tf);

                    //Accepting challenge
                    accept_challenge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!CheckingUtils.isNetworkConnected(context)){
                                CheckingUtils.createErrorBox("Norint priimti iššūkį, jums reikia interneto", context, R.style.PlayDialogStyle);
                                CheckingUtils.vibrate(context, 100);
                                return;
                            }else{
                                PlayActivity.shouldAddInfo=true;
                                HealthyLifeActivity.addData=true;
                                InterestingFactsActivity.addFactsFirstTime=true;
                                AskQuestionsActivity.addFAQData=true;

                                new ServerManager(context, "").execute("ACCEPT_CHALLENGE", sharedPreferences.getString("username", ""), sharedPreferences.getString("password", ""));

                            }

                        }
                    });
                    decline_challenge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!CheckingUtils.isNetworkConnected(context)){
                                CheckingUtils.createErrorBox("Norint nusiųsti iššūkį, tau reikia interneto", context, R.style.PlayDialogStyle);
                                CheckingUtils.vibrate(context, 100);
                                return;
                            }else{
                                PlayActivity.shouldAddInfo=true;
                                HealthyLifeActivity.addData=true;
                                InterestingFactsActivity.addFactsFirstTime=true;
                                AskQuestionsActivity.addFAQData=true;

                                remove(getAdapterPosition());
                                new ServerManager(context, "").execute("DECLINE_CHALLENGE", sharedPreferences.getString("username", ""), sharedPreferences.getString("password", ""));

                            }


                        }
                    });


                    break;

                case 2: //send challenge button

                            go_to_send_activity = (GifImageView) itemView.findViewById(R.id.go_to_send_challenge_activity);

                            go_to_send_activity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(context, CreateChallengeManually.class));
                                }
                            });


                    break;


                case 3: //Trophy layout
                    completed_challenge_title = (TextView) itemView.findViewById(R.id.trophy_title);
                    completed_challenge_description = (TextView) itemView.findViewById(R.id.trophy_description);
                    completed_challenge_sender = (TextView) itemView.findViewById(R.id.trophy_sender);
                    completed_challenge_note = (TextView) itemView.findViewById(R.id.trophy_note);
                    completed_challenge_share = (AppCompatButton) itemView.findViewById(R.id.send_challenge_ccmpleted);
                    break;

                case 4: //Challenge in progress layout
                    challenge_in_progress_description = (TextView) itemView.findViewById(R.id.challenge_in_progress_description);
                    challenge_in_progress_title = (TextView) itemView.findViewById(R.id.challenge_in_progress_title);
                    challenge_in_progress_sender = (TextView) itemView.findViewById(R.id.challenge_in_progress_sender);
                    challenge_in_progress_note = (TextView) itemView.findViewById(R.id.challenge_in_progress_note);

                    days_proggress = (TextView) itemView.findViewById(R.id.days);
                    hours_progress = (TextView) itemView.findViewById(R.id.hours );
                    minutes_progress = (TextView) itemView.findViewById(R.id.minutes);
                    seconds_progress = (TextView) itemView.findViewById(R.id.seconds);

                    days_proggress.setTypeface(verdanaFont);
                    hours_progress.setTypeface(verdanaFont);
                    minutes_progress.setTypeface(verdanaFont);
                    seconds_progress.setTypeface(verdanaFont);

                    failed = (ImageButton) itemView.findViewById(R.id.failed_button);
                    failed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!CheckingUtils.isNetworkConnected(context)){
                                CheckingUtils.createErrorBox("FAIL", context, R.style.PlayDialogStyle);
                                CheckingUtils.vibrate(context, 100);
                                return;
                            }else{

                                String username = sharedPreferences.getString("username", "");
                                String password = sharedPreferences.getString("password", "");
                                remove(getAdapterPosition());
                                new ServerManager(context, "I_FAILED_CHALLENGE").execute("I_FAILED_CHALLENGE", username, password);


                            }
                        }
                    });
                    break;
                case 5:
                    start_playing = (GifImageView) itemView.findViewById(R.id.start_playing_game);

                    start_playing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, GameScreen.class));
                           }
                        }

                    );

                    break;
                case 6:
                    start_playing_quiz = (GifImageView) itemView.findViewById(R.id.start_playing_quiz_gifbutton);
                    start_playing_quiz.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, QuizActivity.class));
                        }
                    });

                    break;

                case 7:
                    link_to_more_info = (TextView) itemView.findViewById(R.id.link_to_human_organs);
                    link_to_bbc_movie = (TextView) itemView.findViewById(R.id.bbc_movie);
                    link_external_movie = (TextView) itemView.findViewById(R.id.filmukas);


                    link_external_movie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://www.youtube.com/watch?v=lny5u-HIwbg&list=PLu2ub6G0HrRSqTOtHLxB5h0J-9EIODE3z&index=2");
                            Intent intent = new Intent();
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });

                    link_to_bbc_movie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://www.youtube.com/watch?v=qjaaqyhMYPY&list=PLu2ub6G0HrRSqTOtHLxB5h0J-9EIODE3z&index=3");
                            Intent intent = new Intent();
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });

                    link_to_more_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://drinkwise.org.au/alcohol-and-your-health/#");
                            Intent intent = new Intent();
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });


                    human_organs = (GifImageView) itemView.findViewById(R.id.human_organs_gif);
                    human_organs.setOnClickListener(new View.OnClickListener() {

                        int timesClicked = 0;
                        @Override
                        public void onClick(View v) {
                            timesClicked++;


                            if(timesClicked > 9){
                                timesClicked = 0;
                            }

                            final GifDrawable drawable;


                            try {




                                switch (timesClicked){
                                case 0:
                                    drawable = new GifDrawable(context.getResources(), R.drawable.starting);
                                    drawable.setLoopCount(1);
                                    human_organs.setImageDrawable(drawable);

                                    break;

                                case 1:
                                    drawable = new GifDrawable(context.getResources(), R.drawable.organs_1);
                                    drawable.setLoopCount(1);
                                    human_organs.setImageDrawable(drawable);

                                    break;

                                    case 2:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_2);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 3:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_3);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 4:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_4);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 5:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_5);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 6:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_6);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;
                                    case 7:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_7);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 8:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_8);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                                    case 9:
                                        drawable = new GifDrawable(context.getResources(), R.drawable.organs_9);
                                        drawable.setLoopCount(1);
                                        human_organs.setImageDrawable(drawable);

                                        break;

                            }




                            } catch (IOException e) {
                            e.printStackTrace();
                        }
                        }
                    });

                    break;
            }

        }
    }

    private void launchCalculator(final int which_img)
    {

        new AlertDialog.Builder(context, R.style.PlayDialogStyle)
                .setMessage("Ar jums yra daugiau negu 18 metų ?")
                .setPositiveButton("Taip, įeiti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = null;
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        switch (which_img){
                            case 0:
                                uri = Uri.parse("http://www.megaukismaistu.lt/2016/kaloriju-iseikvojimo-skaiciuokle");
                                intent.setData(uri);
                                context.startActivity(intent);

                                break;
                            case 1:
                                uri = Uri.parse("http://www.sulieknek.lt/skaiciuokles/skysciu-paros-normos-skaiciuokle/");
                                intent.setData(uri);
                                context.startActivity(intent);

                                break;
                            case 2:
                                uri = Uri.parse("https://www.drinkiq.com/en-gb/whats-in-your-drink/");
                                intent.setData(uri);
                                context.startActivity(intent);

                                break;
                            case 3:
                                uri = Uri.parse("http://www.los.lt/kiek-kaloriju-suvartojama-dirbant-ir-sportuojant/");
                                intent.setData(uri);
                                context.startActivity(intent);

                                break;
                            case 4:
                                uri = Uri.parse("https://www.drinkiq.com/en-gb/drink-calculator/");
                                intent.setData(uri);
                                context.startActivity(intent);
                                break;

                            case 5:
                                uri = Uri.parse("https://www.drinkaware.co.uk/alcohol-facts/health-effects-of-alcohol/calories/calories-in-alcohol/");
                                intent.setData(uri);
                                context.startActivity(intent);

                                break;
                        }

                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();




    }
}