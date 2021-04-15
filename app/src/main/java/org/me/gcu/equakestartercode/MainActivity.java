///////////////////////////////////////////////////////////////////////////////
//
// ZYGIMANTAS DOMARKAS
// S1718169
//
///////////////////////////////////////////////////////////////////////////////

package org.me.gcu.equakestartercode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//google map imports
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnClickListener {
    private final String url1 = "";
    private final String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    String text;
    List<QuakeData> quakesData = new ArrayList<>();
    List<QuakeData> filteredByDateQuakesData = new ArrayList<>();
    private TextView rawDataDisplay;
    private Button startButton;
    private RecyclerView recyclerView;
    private String result = "";
    private LinkedList<QuakeData> quakeDataList = new LinkedList<>();
    private String description;
    private final MyItemRecyclerViewAdapter dataAdapter = new MyItemRecyclerViewAdapter(quakeDataList);
    private Button btnFilterByDate, btnFilterByDeepest, btnFilterByShallowest, btnFilterByNortherly,
            btnFilterBySoutherly, btnFilterByWesterly, btnFilterByEasterly, btnFilterByMagnitude;
    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
//        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        startButton = findViewById(R.id.startButton);
        recyclerView = findViewById(R.id.recyclerView);
        btnFilterByDate = findViewById(R.id.btn_filter_date);
        btnFilterByDeepest = findViewById(R.id.btn_filter_by_deepest);
        btnFilterByShallowest = findViewById(R.id.btn_filter_by_shallowest);
        btnFilterByNortherly = findViewById(R.id.btn_filter_by_northerly);
        btnFilterBySoutherly = findViewById(R.id.btn_filter_by_southerly);
        btnFilterByWesterly = findViewById(R.id.btn_filter_by_westerly);
        btnFilterByEasterly = findViewById(R.id.btn_filter_by_easterly);
        btnFilterByMagnitude = findViewById(R.id.btn_filter_by_magnitude);
        recyclerView.setAdapter(dataAdapter);
        startButton.setOnClickListener(this);
//        Spinner filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
//        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(MainActivity.this,
//                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.filterNames));
//        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        filterSpinner.setAdapter(filterAdapter);
        Log.e("MyTag", "after startButton");
        // More Code goes here
        btnFilterByDate.setOnClickListener(view -> openDatePickerDialog());

        btnFilterByDeepest.setOnClickListener(view -> {
            filterByDeepest();
        });

        btnFilterByShallowest.setOnClickListener(view -> {
            filterByShallowest();
        });

         btnFilterByNortherly.setOnClickListener(view -> {
             filterByNortherly();
        });

         btnFilterBySoutherly.setOnClickListener(view -> {
                filterBySoutherly();
        });

         btnFilterByWesterly.setOnClickListener(view -> {
             filterByWesterly();
        });

        btnFilterByEasterly.setOnClickListener(view -> {
            filterByEasterly();
        });

        btnFilterByMagnitude.setOnClickListener(view -> {
            filterByMagnitude();
        });

        dataAdapter.setItemListener(quakeData -> {
            Intent intent = new Intent(getBaseContext(), QuakeDetailsActivity.class);
            intent.putExtra("quakeData", quakeData);
            startActivity(intent);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void getMarkerInfo() {
        MarkerOptions options = new MarkerOptions();
        int temp1 = Integer.parseInt(null);

        for (int i = 0; i < quakeDataList.size(); i++) {
            QuakeData e = quakeDataList.get(i);

            double magnitude = Double.parseDouble(e.getMagnitude());
            System.out.println(magnitude);
            if (magnitude >= 3) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else if (magnitude >= 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            } else if (magnitude >= 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //Move camera to United Kingdom
        LatLng unitedKingdom = new LatLng(55.3781, -3.4360);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(unitedKingdom));


    }

    public void onZoom(View view) {
        if (view.getId() == R.id.zoomIn) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() == R.id.zoomOut) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

    private void filterByDeepest() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> deepest = new ArrayList<>();
            deepest.add(filteredByDateQuakesData.stream()
                    .sorted(Comparator.comparing(quakeData -> Integer.parseInt(quakeData.getDepth().trim().split(" ")[1])))
                    .collect(Collectors.toList()).get(filteredByDateQuakesData.size() - 1));

        //Updating UI
        dataAdapter.updateValues(deepest);
    }

    private void filterByShallowest() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> shallowest = new ArrayList<>();
            shallowest.add(filteredByDateQuakesData.stream()
                        .sorted(Comparator.comparing(quakeData -> Integer.parseInt(quakeData.getDepth().trim().split(" ")[1])))
                        .collect(Collectors.toList()).get(0));

        //Updating UI
        dataAdapter.updateValues(shallowest);
    }

    private void filterByNortherly() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> northerly = new ArrayList<>();
        DoubleSummaryStatistics statsLong = filteredByDateQuakesData.stream().mapToDouble(value -> Double.parseDouble(value.getGeoLat())).summaryStatistics();

        List<QuakeData> sortedNortherly = filteredByDateQuakesData.stream()
                                                        .sorted(Comparator.comparing(quakeData -> Float.parseFloat(quakeData.getGeoLat().trim())))
                                                        .collect(Collectors.toList());

        Collections.reverse(sortedNortherly.subList(0, sortedNortherly.toArray().length));
        northerly.add(sortedNortherly.get(0));
        //Updating UI
        dataAdapter.updateValues(northerly);
    }

    private void filterBySoutherly() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> southerly = new ArrayList<>();
        DoubleSummaryStatistics statsLong = filteredByDateQuakesData.stream().mapToDouble(value -> Double.parseDouble(value.getGeoLat())).summaryStatistics();
        List<QuakeData> sortedSoutherly = filteredByDateQuakesData.stream()
                                                        .sorted(Comparator.comparing(quakeData -> Float.parseFloat(quakeData.getGeoLat().trim())))
                                                        .collect(Collectors.toList());
        southerly.add(sortedSoutherly.get(0));

        //Updating UI
        dataAdapter.updateValues(southerly);
    }

    private void filterByWesterly() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }

        //Applying filter with filtered data
        List<QuakeData> westerly = new ArrayList<>();
        DoubleSummaryStatistics statsLong = filteredByDateQuakesData.stream().mapToDouble(value -> Double.parseDouble(value.getGeoLong())).summaryStatistics();
        List<QuakeData> sortedWesterly = filteredByDateQuakesData.stream()
                .sorted(Comparator.comparing(quakeData -> Float.parseFloat(quakeData.getGeoLong().trim())))
                .collect(Collectors.toList());
        westerly.add(sortedWesterly.get(0));

        //Updating UI
        dataAdapter.updateValues(westerly);
    }

    private void filterByEasterly() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> easterly = new ArrayList<>();

        DoubleSummaryStatistics statsLat = filteredByDateQuakesData.stream().mapToDouble(value -> Double.parseDouble(value.getGeoLong())).summaryStatistics();

        List<QuakeData> sortedEasterly = filteredByDateQuakesData.stream()
                .sorted(Comparator.comparing(quakeData -> Float.parseFloat(quakeData.getGeoLong().trim())))
                .collect(Collectors.toList());

        Collections.reverse(sortedEasterly.subList(0, sortedEasterly.toArray().length));
        easterly.add(sortedEasterly.get(0));


        //Updating UI
        dataAdapter.updateValues(easterly);
    }

    private void filterByMagnitude() {
        if (filteredByDateQuakesData == null || filteredByDateQuakesData.isEmpty()) {
            Toast.makeText(this, "Please apply date filter first", Toast.LENGTH_SHORT).show();
            return;
        }
        //Applying filter with filtered data
        List<QuakeData> largestMagnitude = new ArrayList<>();
            largestMagnitude.add(filteredByDateQuakesData.stream()
                            .sorted(Comparator.comparingDouble(QuakeData::getMagnitudeD).reversed())
                            .collect(Collectors.toList()).get(0));

        //Updating UI
        dataAdapter.updateValues(largestMagnitude);
    }


    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max, final Date date){
        return !(date.before(min) || date.after(max));
    }

    private void openDatePickerDialog() {
        if (quakeDataList == null) {
            Toast.makeText(this, "Please fetch data first", Toast.LENGTH_SHORT).show();
            return;
        }
        MaterialDatePicker datePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .setSelection(
                            Pair.create(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                            )
                        )
                        .build();

        datePicker.show(getSupportFragmentManager(), "PICKER_DIALOG");
        datePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(MainActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
            }
        });



        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {

                Long startDate = selection.first;
                Long endDate = selection.second;
                final Pair<Date, Date> rangeDate = new Pair<>(new Date(startDate), new Date(endDate));
                Date dateStart = rangeDate.first;
                Date dateEnd = rangeDate.second;
                Toast.makeText(MainActivity.this, "Selected startDate" + dateStart + "Selected endDate" + dateEnd, Toast.LENGTH_SHORT).show();
                //Applying filter
                List<QuakeData> filteredByDateList =
                        quakeDataList
                                .stream()
                                .filter(quakeData -> isDateInBetweenIncludingEndPoints(dateStart, dateEnd, parseDate(quakeData.getEqDate())))
                                .collect(Collectors.toList());

                //Setting filteredByDates to apply other filters
                filteredByDateQuakesData = filteredByDateList;

                //Updating UI
                dataAdapter.updateValues(filteredByDateList);
            }
        });
    }

    public void onClick(View view) {
        Log.e("MyTag", "in onClick");
        startProgress();
        Log.e("MyTag", "after startProgress");
    }


    public void startProgress() {
        // Run network access on a separate thread from a thread pool
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new DataRetriever(urlSource), 0, 5, TimeUnit.MINUTES);
    } //

    private LinkedList<QuakeData> parseEqData(String parseData) {
        QuakeData quakeData = new QuakeData();
        LinkedList<QuakeData> eqList = new LinkedList<QuakeData>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(parseData));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            // create a new instance of employee
                            quakeData = new QuakeData();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText();


                    case XmlPullParser.END_TAG:
                        if (tagName != null) {
                            if ("item".equalsIgnoreCase(tagName)) {
                                eqList.add(quakeData);
                            } else if ("title".equalsIgnoreCase(tagName)) {
                                quakeData.setTitle(text);
                            } else if ("description".equalsIgnoreCase(tagName)) {
                                quakeData.setDescription(text);
                            } else if ("link".equalsIgnoreCase(tagName)) {
                                quakeData.setLink(text);
                            } else if ("pubDate".equalsIgnoreCase(tagName)) {
                                quakeData.setPubDate(text);
                            } else if ("category".equalsIgnoreCase(tagName)) {
                                quakeData.setCategory(text);
                            } else if ("lat".equalsIgnoreCase(tagName)) {
                                quakeData.setGeoLat(text);
                            } else if ("long".equalsIgnoreCase(tagName)) {
                                quakeData.setGeoLong(text);
                            }
                        }
                        break;

                    default:
                        break;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        Log.e("earthquakeSize", "" + eqList.size());
        return eqList;
    }

    private Calendar getCalFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    private Date parseDate(String s) {
        try {
            return new SimpleDateFormat("EEE, dd MMM yyyy").parse(s.split(": ")[1].substring(0, 16));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return null;
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class DataRetriever implements Runnable {
        private final String url;

        public DataRetriever(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            Log.d("DATA RETRIEVAL", "Time passed " + System.currentTimeMillis());

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag", "after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void run() {
                    //parsing the data here
                    Log.d("UI thread", "I am the UI thread" + result);
//                    rawDataDisplay.setText(result);
                    String notNull = result.replace("null", "");
                    quakeDataList = parseEqData(notNull);
                    Log.e("quakeDataListSize", String.valueOf(quakeDataList.size()));

                    try {
                        Date date = new SimpleDateFormat("EEE, dd MMM yyyy").parse(quakeDataList.get(0).getEqDate().split(": ")[1].substring(0, 16));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Date filtering


                    Calendar a1 = Calendar.getInstance();
                    a1.setTime(new Date());
                    a1.add(Calendar.DAY_OF_MONTH, -1);


                    Calendar b = Calendar.getInstance();
                    b.setTime(new Date());

                    List<QuakeData> filteredByDateList =
                            quakeDataList
                                    .stream()
                                    .filter(quakeData -> getCalFromDate(parseDate(quakeData.getEqDate())).before(b) && getCalFromDate(parseDate(quakeData.getEqDate())).after(a1))
                                    .collect(Collectors.toList());


                    List<QuakeData> dateFilterList = quakeDataList.stream()
                            .sorted(Comparator.comparingDouble(QuakeData::getMagnitudeD)
                                    .reversed())
                            .collect(Collectors.toList());


                    //Setting received values
                    quakesData = quakeDataList.stream()
                            .sorted(Comparator.comparingDouble(QuakeData::getMagnitudeD)
                                    .reversed())
                            .collect(Collectors.toList());

                    dataAdapter.updateValues(quakesData);

                    for (int i = 0; i < quakeDataList.size(); i++) {
                        QuakeData e = quakeDataList.get(i);


                        double doubleLat = Double.parseDouble(e.getGeoLat());
                        double doubleLong = Double.parseDouble(e.getGeoLong());
                        LatLng currentCoordinates = new LatLng(doubleLat, doubleLong);
                        BitmapDescriptor icon = null;

                        double magnitude = Double.parseDouble(String.valueOf(e.getMagnitudeD()));

                        if (magnitude >= 3) {
                            Log.d("mag3", String.valueOf(magnitude));
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        } else if (magnitude >= 2) {
                            Log.d("mag2", String.valueOf(magnitude));
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        } else if (magnitude >= 1) {
                            Log.d("mag1", String.valueOf(magnitude));
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                        } else {
                            Log.d("magElse", String.valueOf(magnitude));
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        }
                        mMap.addMarker(new MarkerOptions().position(currentCoordinates)
                                .title(e.getLocation())
                                .icon(icon)
                        );
                    }
                }
            });
        }

    }
}