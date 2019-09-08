package com.example.yoshiki.walking;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//STEP1
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.List;

//STEP2

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
//import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
//import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;


/*

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

*/
/*
STEP1
加速度センサーの値を取得するには MainActivity に SensorEventListener インタフェースを実装します
*/
public class MainActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv,finish;
    Handler h;
    public int button_flag = 0;
    public int sample_count = 0;
    public double gx, gy, gz;
    //STEP2
    public double sacc;

    //STEP5
    ConverterUtils.DataSource source;
    Instances instances;
    Evaluation eval;
    Attribute acceleration;
    Instance instance;
    Classifier classifier;
    /*
    File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    String fileName = "sample.csv";
    File file = new File(path, fileName);
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        LinearLayout ll = new LinearLayout(this);
//        setContentView(ll);
        setContentView(R.layout.activity_main);

        Button stand_btn =findViewById(R.id.stand_button);
        stand_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                button_flag = 1;
                finish.setText("record"+"\n");
            }
        });
        Button walk_btn =findViewById(R.id.walk_button);
        walk_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                button_flag = 2;
                finish.setText("record"+"\n");

            }
        });

        Button run_btn =findViewById(R.id.run_button);
        run_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                button_flag = 3;
                finish.setText("record"+"\n");
            }
        });

        Button arff_btn =findViewById(R.id.arff_button);
        arff_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish.setText("from CSV to Arff"+"\n");
                FromCsvtoArff();
            }
        });

        Button delete_btn =findViewById(R.id.delete_button);
        delete_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish.setText("delete"+"\n");
                File_delete();
            }
        });

        Button learn_btn =findViewById(R.id.learn_button);
        learn_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                finish.setText("learn"+"\n");
                button_flag = 5;

            }
        });

//        tv = new TextView(this);
//        ll.addView(tv);
        tv = (TextView)(findViewById(R.id.sensor_data));
        finish = (TextView)(findViewById(R.id.text_finish));

        h = new Handler();
        h.postDelayed(this, 20);


/*

        try {

//STEP2

            FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/test.csv", true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            //内容を指定する
            pw.print(sa);
            pw.print(",");

            //ファイルに書き出す
            pw.close();

            //終了メッセージを画面に出力する
            System.out.println("出力が完了しました。");

        } catch (IOException ex) {
            //例外時処理
            ex.printStackTrace();
        }

*/

    }

    protected void File_delete() {
        File delete_standing = new File(Environment.getExternalStorageDirectory().getPath() + "/standing.csv");
        delete_standing.delete();
        File delete_walking = new File(Environment.getExternalStorageDirectory().getPath() + "/walking.csv");
        delete_walking.delete();
        File delete_running = new File(Environment.getExternalStorageDirectory().getPath() + "/running.csv");
        delete_running.delete();

    }



        public void CopyOfWekaTest() {
            try {


//                FileInputStream File_arff =openFileInput(Environment.getExternalStorageDirectory().getPath() + "/learn_model.arff");
                String path = Environment.getExternalStorageDirectory().getPath() + "/learn_model.arff";
                source = new DataSource(path);

                instances = source.getDataSet();
                instances.setClassIndex(1);


  //              finish.setText("result:3"+"\n");


                classifier = new J48();
                classifier.buildClassifier(instances);

    //            finish.setText("result:4"+"\n");


                eval = new Evaluation(instances);
                eval.evaluateModel(classifier, instances);
     //           System.out.println(eval.toSummaryString());

      //          finish.setText("result:5"+"\n");

                acceleration = new Attribute("acceleration", 0);
             //   Attribute humidity = new Attribute("humidity", 2);
           //     FastVector win = new FastVector(2);

         //       judgement();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    protected void judgement() {
        try {

            instance = new DenseInstance(2);
            instance.setValue(acceleration, sacc);
            instance.setDataset(instances);

            double result = classifier.classifyInstance(instance);
            int re = (int)result;

                if (re == 0) {
                    finish.setText("standing"+"\n");
                }else if (result == 1){
                    finish.setText("walking"+"\n");
                }else if (result == 2){
                    finish.setText("running"+"\n");
                }


//            finish.setText(String.valueOf(re));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {
        tv.setText("X-axis : " + gx + "\n"
                + "Y-axis : " + gy + "\n"
                + "Z-axis : " + gz + "\n"
                + "Synthetic-axis : " + sacc + "\n"
        );

        if (button_flag == 5){
            judgement();
        }



        h.postDelayed(this, 400);
    }

    /*STEP1
アプリでセンサーを利用するには SensorManager を取得します。
SensorManager を取得したら getSensorList() メソッドで指定したセンサーのオブジェクトを取得します。
具体的なセンサー値をアプリ内で利用するため onResume() メソッド内にセンサー値が変化したことを検知するリスナを登録します。
    */
    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /*STEP1
    登録したリスナは onPause() メソッド内で解除します。
     */

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(this);
    }

    /*STEP1
    加速度センサーの値に変化があった場合の処理を onSensorChanged() メソッド内に記述します。
     */


    @Override
    public void onSensorChanged(SensorEvent event) {

        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];

        sacc = Math.sqrt(gx * gx + gy * gy + gz * gz);


        if(button_flag == 1 && sample_count < 1000) {

            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];

            sacc = Math.sqrt(gx * gx + gy * gy + gz * gz);


//        String FILE = "/test.csv";
                try {

//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//            new FileOutputStream(fw, true), "UTF-8"));

                    sample_count++;
             //       finish.setText(sample_count+"\n");
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/standing.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                    String write_int =
                            String.valueOf(sacc) + "," + " standing" + "\n";
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                } catch (IOException k) {
                    k.printStackTrace();
                }
        }

        if(button_flag == 2 && sample_count<1000) {

            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];

            sacc = Math.sqrt(gx * gx + gy * gy + gz * gz);


//        String FILE = "/test.csv";
                try {

//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//            new FileOutputStream(fw, true), "UTF-8"));

                    sample_count++;
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/walking.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                    String write_int =
                            String.valueOf(sacc) + "," + " walking" + "\n";
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                } catch (IOException k) {
                    k.printStackTrace();
                }

        }

        if(button_flag == 3 && sample_count<1000) {

            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];

            sacc = Math.sqrt(gx * gx + gy * gy + gz * gz);


//        String FILE = "/test.csv";
                try {

//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//            new FileOutputStream(fw, true), "UTF-8"));

                    sample_count++;
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/running.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                    String write_int =
                            String.valueOf(sacc) + "," + " running" + "\n";
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                } catch (IOException k) {
                    k.printStackTrace();
                }
        }

        if(sample_count>=1000){
            button_flag = 0;
            sample_count = 0;
            finish.setText("finish"+"\n");
        }
//追記することをけすことをふせぐためにいちどけす


    }

    public void FromCsvtoArff() {


        String file_standing = null;
        String file_walking = null;
        String file_running = null;

        FileOutputStream File_out;
        FileInputStream File_in;

        try {

            finish.setText("From csv to arff"+"\n");

            File_out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/learn_model.arff"));

            String write =(
                    "@relation file\n\n"+
                            "@attribute acceleration real\n"+
                            "@attribute state{standing,walking,running}\n\n"+
                            "@data\n");
            File_out.write(write.getBytes());

            try {
                File_in = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/standing.csv"));
                byte[] readBytes = new byte[File_in.available()];
                File_in.read(readBytes);
                file_standing = new String(readBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
            File_out.write(file_standing.getBytes());
            try {
                File_in = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/walking.csv"));
                byte[] readBytes = new byte[File_in.available()];
                File_in.read(readBytes);
                file_walking = new String(readBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File_out.write(file_walking.getBytes());
            try {
                File_in = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/running.csv"));
                byte[] readBytes = new byte[File_in.available()];
                File_in.read(readBytes);
                file_running = new String(readBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
            File_out.write(file_running.getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }


        CopyOfWekaTest();

        finish.setText("complete"+"\n");


   }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

