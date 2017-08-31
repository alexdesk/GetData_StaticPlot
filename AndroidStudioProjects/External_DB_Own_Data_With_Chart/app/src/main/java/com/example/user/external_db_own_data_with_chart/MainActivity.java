package com.example.user.external_db_own_data_with_chart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Float.parseFloat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                OnChartGestureListener,
                                                                OnChartValueSelectedListener {

    Button consultar;
    Button consultarporid;
    Button insertar;
    Button actualizar;
    Button borrar;
    EditText idendificador;
    EditText nombre;
    EditText direccion;
    TextView resultado;

    // IP de mi Url
    String IP = "http://79.153.3.182";
    // Rutas de los Web Services
    String GET = IP + "/obtener_alumnos.php";
    //String GET_BY_ID = IP + "/obtener_alumno_por_id.php";
    String GET_BY_ID = IP + "/obtener_medidas_por_id.php";
    String UPDATE = IP + "/actualizar_alumno.php";
    String DELETE = IP + "/borrar_alumno.php";
    String INSERT = IP + "/insertar_alumno.php";

    ObtenerWebService hiloconexion;

    /*---------------------------------------------------------------------------------------------
            CHART
    ---------------------------------------------------------------------------------------------*/

    private LineChart mChart;

    // This is used to store x-axis values
    /*private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("10");
        xVals.add("20");
        xVals.add("30");
        xVals.add("30.5");
        xVals.add("40");

        return xVals;
    }

    // This is used to store Y-axis values
    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(60, 0));
        yVals.add(new Entry(-10, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));

        return yVals;
    }*/

    /*---------------------------------------------------------------------------------------------
            CHART END
    ---------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enlaces con elementos visuales del XML

        consultar = (Button)findViewById(R.id.consultar);
        consultarporid = (Button)findViewById(R.id.consultarid);
        insertar = (Button)findViewById(R.id.insertar);
        actualizar = (Button)findViewById(R.id.actualizar);
        borrar = (Button)findViewById(R.id.borrar);
        idendificador = (EditText)findViewById(R.id.eid);
        nombre = (EditText)findViewById(R.id.enombre);
        direccion = (EditText)findViewById(R.id.edireccion);
        resultado = (TextView)findViewById(R.id.resultado);

        // Listener de los botones

        consultar.setOnClickListener(this);
        consultarporid.setOnClickListener(this);
        insertar.setOnClickListener(this);
        actualizar.setOnClickListener(this);
        borrar.setOnClickListener(this);

        /*---------------------------------------------------------------------------------------------
                 CHART
        ---------------------------------------------------------------------------------------------*/

        mChart = (LineChart) findViewById(R.id.linechart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        //l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("Medidas v1.0");
        mChart.setNoDataTextDescription("No data found.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        mChart.animateY(2500, Easing.EasingOption.EaseInOutQuart);

        /*---------------------------------------------------------------------------------------------
                CHART END
        ---------------------------------------------------------------------------------------------*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.consultar:

                hiloconexion = new ObtenerWebService();
                hiloconexion.execute(GET,"1"); //parametros que recibe doInBackground

                break;
            case R.id.consultarid:

                /*hiloconexion = new ObtenerWebService();
                String cadenallamada = GET_BY_ID + "?idalumno=" + idendificador.getText().toString();
                //hiloconexion.execute(GET_BY_ID,"2");
                hiloconexion.execute(cadenallamada, "2");*/

                hiloconexion = new ObtenerWebService();
                String cadenallamada = GET_BY_ID + "?node_id=" + idendificador.getText().toString();
                //hiloconexion.execute(GET_BY_ID,"2");
                hiloconexion.execute(cadenallamada, "2");

                break;
            case R.id.insertar:

                hiloconexion = new ObtenerWebService();
                hiloconexion.execute(INSERT,"3");

                break;
            case R.id.actualizar:

                hiloconexion = new ObtenerWebService();
                hiloconexion.execute(UPDATE,"4",idendificador.getText().toString(),nombre.getText().toString(),direccion.getText().toString());   // Parámetros que recibe doInBackground
                //hiloconexion.execute(UPDATE,"4");

                break;
            case R.id.borrar:

                hiloconexion = new ObtenerWebService();
                hiloconexion.execute(DELETE,"5");

                break;
            default:

                break;
        }
    }

    /*---------------------------------------------------------------------------------------------
            CHART
    ---------------------------------------------------------------------------------------------*/

    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex()
                + ", high: " + mChart.getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin()
                + ", xmax: " + mChart.getXChartMax()
                + ", ymin: " + mChart.getYChartMin()
                + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /*---------------------------------------------------------------------------------------------
            CHART END
    ---------------------------------------------------------------------------------------------*/

    public class ObtenerWebService extends AsyncTask<String,Void,String> {

        /*---------------------------------------------------------------------------------------------
                CHART
        ---------------------------------------------------------------------------------------------*/

        //arraylists para el chart
        ArrayList<String> miXArray = new ArrayList<String>();
        ArrayList<Entry> miYArray = new ArrayList<Entry>();

        /*---------------------------------------------------------------------------------------------
                 CHART END
        ---------------------------------------------------------------------------------------------*/

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;     //url de donde obtenemos la informacion
            String devuelve = "";

            if (params[1] == "1"){      //Consulta de todos los alumnos

                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    /*connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");*/
                    //connection.setHeader("content-type", "application/json");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK){


                        InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                        // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                        // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                        // StringBuilder.

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);        // Paso toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Accedemos al vector de resultados

                        String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON



                        if (resultJSON.equals("1")){      // hay alumnos a mostrar
                            JSONArray alumnosJSON = respuestaJSON.getJSONArray("alumnos");   // estado es el nombre del campo en el JSON
                            for(int i=0;i<alumnosJSON.length();i++){
                                devuelve = devuelve + alumnosJSON.getJSONObject(i).getString("idalumno") + " " +
                                        alumnosJSON.getJSONObject(i).getString("nombre") + " " +
                                        alumnosJSON.getJSONObject(i).getString("direccion") + "\n";

                            }

                        }
                        else if (resultJSON.equals("2")){
                            devuelve = "No hay alumnos";
                        }


                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;


            }
            else if (params[1] == "2"){    //Consulta por id

                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    /*connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");*/
                    //connection.setHeader("content-type", "application/json");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK){


                        InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                        // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                        // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                        // StringBuilder.

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);        // Paso toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Accedemos al vector de resultados

                        String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                        /*if (resultJSON.equals("1")){      // hay un alumno que mostrar
                            devuelve = devuelve + respuestaJSON.getJSONObject("alumno").getString("idAlumno") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("nombre") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("direccion");
                        }*/

                        if (resultJSON.equals("1")){      // hay alumnos a mostrar
                            JSONArray medidasJSON = respuestaJSON.getJSONArray("medida");   // estado es el nombre del campo en el JSON
                            for(int i=0;i<medidasJSON.length();i++){
                                /*devuelve = devuelve + medidasJSON.getJSONObject(i).getString("idalumno") + " " +
                                        medidasJSON.getJSONObject(i).getString("nombre") + " " +
                                        medidasJSON.getJSONObject(i).getString("direccion") + "\n";*/

                                devuelve = devuelve + medidasJSON.getJSONObject(i).getString("medida") + "\n";

                                /*---------------------------------------------------------------------------------------------
                                        CHART
                                ---------------------------------------------------------------------------------------------*/

                                miXArray.add(medidasJSON.getJSONObject(i).getString("time"));
                                miYArray.add(new Entry(parseFloat(medidasJSON.getJSONObject(i).getString("medida")),i));

                                /*---------------------------------------------------------------------------------------------
                                        CHART END
                                ---------------------------------------------------------------------------------------------*/

                            }

                        }

                        else if (resultJSON.equals("2")){
                            devuelve = "No hay medidas";
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;

            }
            else if (params[1] == "3"){    //Insert

            }
            else if (params[1] == "4"){    //Update

                try {
                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();
                    //Creo el Objeto JSON
                    JSONObject jsonParam = new JSONObject(); //param 0 cadena de conexion / paraam 1 llevo el numero 4(la opcion)
                    jsonParam.put("idalumno",params[2]);
                    jsonParam.put("nombre", params[3]);
                    jsonParam.put("direccion", params[4]);
                    // Envio los parámetros post.
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();


                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader br=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            result.append(line);
                            //response+=line;
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Accedemos al vector de resultados

                        String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {      // hay un alumno que mostrar
                            devuelve = "Alumno actualizado correctamente";

                        } else if (resultJSON.equals("2")) {
                            devuelve = "El alumno no pudo actualizarse";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;

            }
            else if (params[1] == "5"){    //Delete

            }


            return null;
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {
            resultado.setText(s);
            //super.onPostExecute(s);

            /*---------------------------------------------------------------------------------------------
                    CHART
            ---------------------------------------------------------------------------------------------*/

            // add data methods
            setData();
            mChart.forceLayout();
            //setRandomData();

            /*---------------------------------------------------------------------------------------------
                    CHART
            ---------------------------------------------------------------------------------------------*/

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        /*---------------------------------------------------------------------------------------------
                CHART
        ---------------------------------------------------------------------------------------------*/

        private void setData() {
            /*ArrayList<String> xVals = setXAxisValues();
            ArrayList<Entry> yVals = setYAxisValues();*/

            ArrayList<String> xVals = miXArray;
            ArrayList<Entry> yVals = miYArray;

            LineDataSet set1;

            // create a dataset and give it a type
            //set1 = new LineDataSet(yVals, "DataSet 1");
            set1 = new LineDataSet(yVals, "mV");
            set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            // set1.enableDashedLine(10f, 5f, 0f);
            // set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLUE);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            mChart.setData(data);
            mChart.setVisibleXRangeMaximum(10);

        }


        private void setRandomData() { //https://www.youtube.com/watch?v=a20EchSQgpw

            LineData data = new LineData();
            mChart.setData(data);
        }


        /*private void addEntry() {
            LineData data = mChart.getData();

            if (data != null){
                LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

                if (set == null){
                    //creation if null
                    set = createSet();
                    data.addDataSet(set);
                }

                //add a new random value
                data.addXValue("");
                data.addEntry(new Entry((float) (Math.random() * 120 ) + 5f, set.getEntryCount()), 0);

                //notify chart data has changed
                mChart.notifyDataSetChanged();

                //limit number of visible entries
                //mChart.setVisibleXRange();
                mChart.setVisibleXRangeMaximum(20);

                //scroll to the last entry
                mChart.moveViewToX(data.getXValCount() - 7);
            }
        }*/

        /*//method to create set
        private LineDataSet createSet() {
            LineDataSet set = new LineDataSet(null, "Random Data");
            set.setDrawCubic(true);
            set.setCubicIntensity(0.2f);
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(ColorTemplate.getHoloBlue());
            set.setLineWidth(2f);
            set.setCircleSize(1f);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244,117,177));
            set.setValueTextColor(Color.WHITE);
            set.setValueTextSize(10f);

            return set;
        }*/


        /*---------------------------------------------------------------------------------------------
                CHART END
        ---------------------------------------------------------------------------------------------*/


    }

    /*@Override
    protected void onResume() {
        super.onResume();

        //simulate real time data addition

        new Thread(new Runnable() {

            @Override
            public void run() {
                //add 70 entries
                for (int i = 0; i < 70; i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry(); //chart is notified of update in AddEntry method
                        }
                    });

                    //pause between adds
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        //manage error
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/
}
