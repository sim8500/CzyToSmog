package dev.sim8500.airlycmp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sbernad on 18/09/2017.
 */

public class StationStatusView extends LinearLayout {

    protected TextView nameTxtView;
    protected TextView statusTxtView;


    public StationStatusView(Context context) {
        super(context);

        init();
    }

    public StationStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public StationStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public StationStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    protected void init()
    {
        inflate(getContext(), R.layout.view_station_row, this);

        nameTxtView = (TextView)this.findViewById(R.id.cityNameTxtView);
        statusTxtView = (TextView)this.findViewById(R.id.levelStatusTxtView);
    }

    public TextView getNameTextView() {
        return nameTxtView;
    }

    public TextView getStatusTextView() {
        return statusTxtView;
    }
}
