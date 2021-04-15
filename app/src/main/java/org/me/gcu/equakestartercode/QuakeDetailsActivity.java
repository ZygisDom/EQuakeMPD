///////////////////////////////////////////////////////////////////////////////
//
// ZYGIMANTAS DOMARKAS
// S1718169
//
///////////////////////////////////////////////////////////////////////////////

package org.me.gcu.equakestartercode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class QuakeDetailsActivity extends AppCompatActivity {

    public QuakeData quakeData;

    public TextView titleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_details);
        titleView = (TextView) findViewById(R.id.titleView);

        Bundle bundle = getIntent().getExtras();
        quakeData = bundle.getParcelable("quakeData");

        titleView.setText(quakeData.description.replace(";", "\n\n\n"));
    }
}