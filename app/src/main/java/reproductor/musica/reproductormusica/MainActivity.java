package reproductor.musica.reproductormusica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean isFirstTime = true;
    private ListView lv_playlist;
    private ArrayAdapter adapter;
    private ArrayList<Cancion> canciones = new ArrayList<Cancion>();
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private boolean isActivePlaying = false;
    private Button btn_play, btn_prev, btn_next;
    private SeekBar volumeSeekBar;
    private SeekBar advanceSeekBar;
    private TextView txtNombreCancion;
    private TextView txtLyrics;
    private int cancionActual;
    private int duration, act_duration;
    private final String[] nomCanciones = {"black___pearl_jam", "come_as_you_are___nirvana", "redemption_song___bob_marley", "musica_sin_tiempo__cultura_profetica", "abuelito_dime_tu___heidi", "hymn_for_the_weekend___coldplay", "perfect___ed", "donde_esta_el_amor___pablo_alboran"};

    public void play_pause_Clicked(View view){
        if(!isActivePlaying){
            mediaPlayer.start();
            int i = mediaPlayer.getCurrentPosition();
            if(mediaPlayer.getCurrentPosition() == 0){
                txtLyrics.animate().cancel();
                txtLyrics.setY(0);
            }
            isActivePlaying = true;
            btn_play.setBackgroundResource(R.drawable.pause_button);
            txtLyrics.animate().translationYBy(-2500f).setDuration((duration-act_duration));
        }else{
            mediaPlayer.pause();
            isActivePlaying = false;
            btn_play.setBackgroundResource(R.drawable.play_button);
            txtLyrics.animate().cancel();
        }

    }

    public void previous_Clicked(View view){
        int prev;
        if(cancionActual == 0){
            prev = 7;
        }else{
            prev = cancionActual - 1;
        }
        prepararCancion(prev);
    }

    public void next_Clicked(View view){
        int next;
        if(cancionActual == 7){
            next = 0;
        }else{
            next = cancionActual + 1;
        }
        prepararCancion(next);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cancionActual = -1;

        btn_play = findViewById(R.id.btn_play);
        btn_prev = findViewById(R.id.btn_prev);
        btn_next = findViewById(R.id.btn_next);

        lv_playlist = findViewById(R.id.lv_playlist);

        canciones.add(new Cancion("Black", "Pearl Jam"));
        canciones.add(new Cancion("Come As You Are", "Nirvana"));
        canciones.add(new Cancion("Redemption Song", "Bob Marley"));
        canciones.add(new Cancion("Música Sin Tiempo", "Cultura Profética"));
        canciones.add(new Cancion("Abuelito Dime Tu", "Heidi"));
        canciones.add(new Cancion("Hymn For The Weekend", "Coldplay"));
        canciones.add(new Cancion("Perfect", "Ed Sheeran ft. Andrea Bocelli"));
        canciones.add(new Cancion("Donde Está El Amor", "Pablo Alborán"));

        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1, canciones){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView1=(TextView) view.findViewById(android.R.id.text1);
                TextView textView2=(TextView) view.findViewById(android.R.id.text2);

                textView1.setText(canciones.get(position).nombre);
                textView2.setText(canciones.get(position).artista);

                return view;
            }
        };

        lv_playlist.setAdapter(adapter);
        lv_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prepararCancion(i);
            }
        });

        txtLyrics = findViewById(R.id.txtLyrics);
        txtNombreCancion = findViewById(R.id.txtNombreCancion);
        //txtNombreCancion.setText(canciones.get(0).nombre.toUpperCase() + " - " + canciones.get(0).artista);
        mediaPlayer = MediaPlayer.create(this, R.raw.black___pearl_jam);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        //Manejo de avance

        advanceSeekBar = findViewById(R.id.advanceSeekBar);


        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });

        advanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                int m = seekBar.getMax();
                int y = seekBar.getProgress();
                float x = ((2500*y) / m);

                x = (float) (x+(x*0.01));
                txtLyrics.animate().cancel();
                txtLyrics.setY(-x);
                if(isActivePlaying){
                        txtLyrics.animate().translationYBy(-2500f).setDuration((duration-act_duration));
                }
            }
        });
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                advanceSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            }

        }, 0, 1000);

        prepararCancion(0);
        isFirstTime = false;
    }

    private void prepararCancion(int canc){
        if(canc != cancionActual) {
            mediaPlayer.stop();
            txtLyrics.animate().cancel();
            isActivePlaying = false;
            advanceSeekBar.setProgress(0);
            btn_play.setBackgroundResource(R.drawable.play_button);
            txtNombreCancion.setText(canciones.get(canc).nombre.toUpperCase() + " - " + canciones.get(canc).artista);
            txtLyrics.setText("");

            int id = getResources().getIdentifier(nomCanciones[canc], "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), id);

            switch (canc) {
                case 0:
                    txtLyrics.setText("\n\n\nBLACK - Pearl Jam\n\n\n" +
                            "Sheets of empty canvas\n" +
                            "Untouched sheets of clay\n" +
                            "Were laid spread out before me\n" +
                            "As her body once did\n" +
                            "All five horizons\n" +
                            "Revolved around her soul\n" +
                            "As the earth to the sun\n" +
                            "Now the air I tasted and breathed\n" +
                            "Has taken a turn\n\n" +
                            "Oh and all I taught her was everything\n" +
                            "Oh I know she gave me all that she wore\n\n" +
                            "And now my bitter hands\n" +
                            "Chafe beneath the clouds\n" +
                            "Of what was everything\n" +
                            "Oh the pictures have\n" +
                            "All been washed in black\n" +
                            "Tattooed everything\n\n\n" +
                            "I take a walk outside\n" +
                            "I'm surrounded by\n" +
                            "Some kids at play\n" +
                            "I can feel their laughter\n" +
                            "So why do I sear\n\n\n\n" +
                            "Oh, and twisted thoughts that spin\n" +
                            "Round my head\n" +
                            "I'm spinning\n" +
                            "Oh, I'm spinning\n" +
                            "How quick the sun can, drop away\n\n\n\n" +
                            "And now my bitter hands\n" +
                            "Cradle broken glass\n" +
                            "Of what was everything\n" +
                            "All the pictures had\n" +
                            "All been washed in black\n" +
                            "Tattooed everything\n" +
                            "All the love gone bad\n" +
                            "Turned my world to black\n" +
                            "Tattooed all I see\n" +
                            "All that I am\n" +
                            "All I'll be\n\n\n\n\n\n\n\n" +
                            "I know someday you'll have a beautiful life\n" +
                            "I know you'll be a star\n" +
                            "In somebody else's sky\n" +
                            "But why\n" +
                            "Why\n" +
                            "Why can't it be\n" +
                            "Why can't it be mine");
                    break;
                case 1:
                    txtLyrics.setText("\n\n\nCOME AS YOU ARE - Nirvana\n\n\n\n\nCome as you are, as you were\n" +
                            "As I want you to be\n" +
                            "As a friend, as a friend\n" +
                            "As an known enemy\n" +
                            "Take your time, hurry up\n" +
                            "The choice is yours, don't be late\n\n" +
                            "Take a rest as a friend\n" +
                            "As an old\n" +
                            "Memoria,\n memoria\n" +
                            "Memoria,\n memoria\n\n" +
                            "Come doused in mud, soaked in bleach\n" +
                            "As I want you to be\n" +
                            "As a trend, as a friend\n" +
                            "As an old\n" +
                            "Memoria,\n memoria\n" +
                            "Memoria,\n memoria\n\n\n\n\n\n\n\n" +
                            "And I swear that I don't have a gun\n" +
                            "No I don't have a gun\n" +
                            "No I don't have a gun\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Memoria,\n memoria\n" +
                            "Memoria,\n memoria\n\n" +
                            "(No I don't have a gun)\n" +
                            "And I swear that I don't have a gun\n" +
                            "No I don't have a gun\n" +
                            "No I don't have a gun\n" +
                            "No I don't have a gun\n" +
                            "No I don't have a gun");
                    break;
                case 2:
                    txtLyrics.setText("\n\n\nREDEMPTION SONG - Bob Marley\n\n\n\nOld pirates, yes, they rob I;\n" +
                            "Sold I to the merchant ships,\n" +
                            "Minutes after they took I\n" +
                            "From the bottomless pit.\n" +
                            "But my hand was made strong\n" +
                            "By the 'and of the Almighty.\n" +
                            "We forward in this generation\n" +
                            "Triumphantly.\n" +
                            "\n" +
                            "Won't you help to sing\n" +
                            "These songs of freedom?\n" +
                            "'Cause all I ever have:\n" +
                            "Redemption songs;\n" +
                            "Redemption songs.\n" +
                            "\n\n\n\n" +
                            "Emancipate yourselves from mental slavery;\n" +
                            "None but ourselves can free our minds.\n" +
                            "Have no fear for atomic energy,\n" +
                            "'Cause none of them can stop the time.\n\n\n" +
                            "How long shall they kill our prophets,\n" +
                            "While we stand aside and look? Ooh!\n" +
                            "Some say it's just a part of it:\n" +
                            "We've got to fulfill the book.\n\n\n\n\n" +
                            "Won't you help to sing\n" +
                            "These songs of freedom?\n" +
                            "'Cause all I ever have:\n\n\n" +
                            "Redemption songs;\n" +
                            "Redemption songs;\n" +
                            "Redemption songs.\n" +
                            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Emancipate yourselves from mental slavery;\n" +
                            "None but ourselves can free our mind.\n" +
                            "Wo! Have no fear for atomic energy,\n" +
                            "'Cause none of them-a can-a stop-a the time.\n\n\n" +
                            "How long shall they kill our prophets,\n" +
                            "While we stand aside and look?\n" +
                            "Yes, some say it's just a part of it:\n" +
                            "We've got to fulfill the book.\n\n" +
                            "Won't you help to sing\n" +
                            "These songs of freedom?\n" +
                            "'Cause all I ever had:\n" +
                            "Redemption songs\n" +
                            "All I ever had:\n" +
                            "Redemption songs:\n" +
                            "These songs of freedom,\n" +
                            "Songs of freedom.");
                    break;
                case 3:
                    txtLyrics.setText("\n\nMÚSICA SIN TIEMPO - Cultura Profética\n\n\n" +
                            "Música sin tiempo, música sin edad\n" +
                            "Música cual viento, hacia la eternidad\n" +
                            "Música en el pecho, música no hay de mas\n" +
                            "Música que se ha hecho, con la finalidad\n" +
                            "De tocar fibra laririri laririra laririrera\n" +
                            "Música si, marca el pulgar, lleva mi huella\n" +
                            "\n" +
                            "Música el silencio, música he de soñar\n" +
                            "Música en un lienzo, un suspiro en el cristal\n\n\n" +
                            "Música en un beso, música visceral\n" +
                            "Música solo eso, toda mi debilidad (y mi constancia)\n\n" +
                            "Laririri laririra laririrera\n" +
                            "Música si, marca el pulgar, lleva mi huella\n\n\n\n" +
                            "Laririri laririra laririrera\n" +
                            "Música si, marca el pulgar, lleva mi huella\n" +
                            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Música un encuentro, música mineral\n" +
                            "Música en un hueco, con la urgencia de aflorar\n" +
                            "Música sin precio, música laboral\n\n\n" +
                            "Música un derecho en izquierda o por igual (no discrimina)\n\n" +
                            "Laririri laririra laririrera\n" +
                            "Música si, marca el pulgar, lleva mi huella\n\n\n\n" +
                            "Laririri laririra laririrera\n" +
                            "Música si, marca el pulgar, lleva mi huella");
                    break;
                case 4:
                    txtLyrics.setText("\n\n\nABUELITO DIME TU - Heidi\n\n\n\n" +
                            "Abuelito, dime tu: \n" +
                            "¿Qué sonidos son los que oigo yo? \n" +
                            "Abuelito, dime tu: \n" +
                            "¿Por qué yo en la nube voy? \n" +
                            "Dime ¿por qué huele el aire así? \n" +
                            "Dime ¿por qué yo soy tan feliz? \n" +
                            "Abuelito, \n" +
                            "nunca yo de ti me alejaré. \n" +
                            "\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Abuelito, dime tu: \n" +
                            "lo que dice el viento en su canción. \n" +
                            "Abuelito, dime tu: \n" +
                            "¿por qué llovió, por qué nevó? \n\n\n\n\n" +
                            "Dime ¿por qué todo es blanco? \n\n" +
                            "Dime ¿por qué yo soy tan feliz? \n\n" +
                            "Abuelito, \n" +
                            "nunca yo de ti me alejaré. \n" +
                            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Abuelito, dime tu: \n" +
                            "si el abeto a mi me puede hablar. \n" +
                            "Abuelito, dime tu: \n" +
                            "¿por qué la luna ya se va? \n\n\n" +
                            "Dime ¿por qué hasta aquí subí? \n" +
                            "Dime ¿por qué yo soy tan feliz? \n" +
                            "Abuelito, \n" +
                            "nunca yo de ti me alejaré");
                    break;
                case 5:
                    txtLyrics.setText("\n\nHYMN FOR THE WEEKEND - Coldplay ft. Beyonce\n\n\n" +
                            "Oh, angel sent from up above\n" +
                            "You know you make my world light up\n" +
                            "When I was down, when I was hurt\n" +
                            "You came to lift me up\n" +
                            "Life is a drink and love's a drug\n" +
                            "Oh, now I think I must be miles up\n" +
                            "When I was a river dried up\n" +
                            "You came to rain a flood\n\n\n\n" +
                            "And said drink from me, drink from me\n" +
                            "When I was so thirsty\n" +
                            "Pour on a symphony\n" +
                            "Now I just can't get enough\n" +
                            "Put your wings on me, wings on me\n" +
                            "When I was so heavy\n" +
                            "Pour on a symphony\n\n" +
                            "When I'm low, low, low, low\n" +
                            "Ah-oh-ah-oh-ah\n\n" +
                            "Got me feeling drunk and high\n" +
                            "So high, so high\n" +
                            "Ah-oh-ah-oh-ah\n\n" +
                            "Now I’m feeling drunk and high\n" +
                            "So high, so high\n\n\n\n\n\n\n\n\n\n\n" +
                            "Oh, angel sent from up above\n" +
                            "I feel it coursing through my blood\n" +
                            "Life is a drink, your love's about\n" +
                            "To make the stars come out\n\n\n\n\n\n\n\n" +
                            "Put your wings on me, wings on me\n" +
                            "When I was so heavy\n" +
                            "Pour on a symphony\n" +
                            "When I'm low, low, low, low\n" +
                            "Ah-oh-ah-oh-ah\n\n" +
                            "Got me feeling drunk and high\n" +
                            "So high, so high\n" +
                            "Ah-oh-ah-oh-ah\n\n" +
                            "Now I’m feeling drunk and high\n" +
                            "So high, so high\n\n\n\n" +
                            "Ah-oh-ah-oh-ah\n" +
                            "la-la-la-la-la-la-la\n" +
                            "So high, so high\n\n" +
                            "Ah-oh-ah-oh-ah\n" +
                            "Now I’m feeling drunk and high\n" +
                            "So high, so high\n" +
                            "That we shoot across the sky\n" +
                            "That we shoot across the\n" +
                            "That we shoot across the sky\n" +
                            "That we shoot across the\n" +
                            "That we shoot across the sky\n" +
                            "That we shoot across the\n" +
                            "That we shoot across the sky\n" +
                            "That we shoot across the");
                    break;
                case 6:
                    txtLyrics.setText("PERFECT - Ed Sheeran ft. Andrea Bocelli\n\n" +
                            "[Ed Sheeran:]\n" +
                            "I found a love for me\n" +
                            "Oh darling, just dive right in and follow my lead\n" +
                            "Well, I found a girl, beautiful and sweet\n" +
                            "Oh, I never knew you were the someone waiting for me\n" +
                            "'Cause we were just kids when we fell in love\n" +
                            "Not knowing what it was\n" +
                            "I will not give you up this time\n" +
                            "But darling, just kiss me slow, your heart is all I own\n" +
                            "And in your eyes, you're holding mine\n" +
                            "\n" +
                            "Baby, I'm dancing in the dark with you between my arms\n" +
                            "Barefoot on the grass, listening to our favourite song\n" +
                            "When you said you looked a mess, I whispered underneath my breath\n" +
                            "But you heard it, darling, you look perfect tonight\n" +
                            "\n\n\n\n\n\n\n\n" +
                            "[Andrea Bocelli:]\n" +
                            "Sei la mia donna\n" +
                            "La forza delle onde del mare\n" +
                            "Cogli i miei sogni e i miei segreti molto di più\n" +
                            "Spero che un giorno, l'amore che ci ha accompagnato\n" +
                            "Diventi casa, la mia famiglia, diventi noi\n\n\n\n\n\n\n" +
                            "E siamo sempre bambini ma\n" +
                            "Nulla è impossibile\n\n\n" +
                            "Stavolta non ti lascerò\n" +
                            "Mi baci piano ed io torno ad esistere\n" +
                            "E nel tuo sguardo crescerò\n" +
                            "\n\n\n\n\n\n\n" +
                            "Ballo con te, nell'oscurità\n" +
                            "Stretti forte e poi, a piedi nudi noi\n" +
                            "Dentro la nostra musica\n" +
                            "Ti ho guardato ridere e sussurando ho detto:\n" +
                            "\"Tu stasera, vedi, sei perfetta per me\"\n" +
                            "\n\n\n\n\n\n\n\n\n" +
                            "[Ed Sheeran & Andrea Bocelli:]\n" +
                            "Ballo con te, nell'oscurità\n" +
                            "Stretti forte e poi, a piedi nudi noi\n" +
                            "Dentro la nostra musica\n" +
                            "Ho creduto sempre in noi\n" +
                            "Perché sei un angelo e io ti ho aspettato\n" +
                            "Quanto ti ho aspettato\n" +
                            "Perché tu stasera sei perfetta per me");
                    break;
                case 7:
                    txtLyrics.setText("\n\nDONDE ESTÁ EL AMOR - Pablo Alborán\n\n" +
                            "No hace falta que me quites la mirada\n" +
                            "Para que entienda que ya no queda nada\n" +
                            "Aquella luna que antes nos bailaba\n" +
                            "Se ha cansado y ahora nos da la espalda\n\n\n" +
                            "¿Dónde está el amor del que tanto hablan?\n" +
                            "¿Por qué no nos sorprende y rompe nuestra calma?\n\n" +
                            "Déjame que vuelva a acariciar tu pelo\n" +
                            "Déjame que funda tu pecho en mi pecho\n" +
                            "Volveré a pintar de colores el cielo\n" +
                            "Haré que olvides de una vez el mundo entero\n" +
                            "Déjame tan solo que hoy roce tu boca\n" +
                            "Déjame que voy a detener las horas\n" +
                            "Volveré a pintar de azul el universo\n" +
                            "Haré que todo esto solo sea un sueño\n\n\n\n\n" +
                            "Tengo contados todos los besos que nos damos\n" +
                            "Y tú fugitiva, andas perdida en otro lado\n\n\n\n\n\n" +
                            "Yo no quiero caricias de otros labios\n" +
                            "No quiero tus manos en otras manos\n" +
                            "Porque yo quiero que volvamos a intentarlo\n\n\n\n\n\n\n" +
                            "¿Dónde está el amor del que tanto hablan?\n" +
                            "¿Por qué no nos sorprende y rompe nuestra calma?\n\n\n\n\n" +
                            "Déjame que vuelva a acariciar tu pelo\n" +
                            "Déjame que funda tu pecho en mi pecho\n" +
                            "Volveré a pintar de colores el cielo\n" +
                            "Haré que olvides de una vez el mundo entero\n" +
                            "Déjame tan solo que hoy roce tu boca\n" +
                            "Déjame que voy a detener las horas\n" +
                            "Volveré a pintar de azul el universo\n" +
                            "Haré que todo esto solo sea un sueño\n" +
                            "\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                            "Déjame que vuelva a acariciar tu pelo\n" +
                            "Déjame que funda tu pecho en mi pecho\n" +
                            "Volveré a pintar de colores el cielo\n" +
                            "Haré que olvides de una vez el mundo entero\n" +
                            "Déjame tan solo que hoy roce tu boca\n" +
                            "Déjame que voy a detener las horas\n" +
                            "Volveré a pintar de azul el universo\n" +
                            "Haré que todo esto solo sea un sueño");
                    break;
            }

            txtLyrics.setY(0);
            cancionActual = canc;
            duration = mediaPlayer.getDuration();
            act_duration = 0;
            //final int advance = mediaPlayer.getCurrentPosition();
            int progress = mediaPlayer.getCurrentPosition();

            advanceSeekBar.setMax(duration);
            advanceSeekBar.setProgress(progress);

            if(!isFirstTime){
                play_pause_Clicked(btn_play);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeSeekBar.setProgress(currentVolume);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeSeekBar.setProgress(currentVolume);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
