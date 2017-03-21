package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.finappl.R.id.calendar_date_cell_date_key;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class CalendarGridViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private final Context mContext;
    private LayoutInflater inflater;
    private final int LAYOUT = R.layout.calendar_day;

    private List<Date> datesList = new ArrayList<>();
    private Map<String, DayLedger> ledger;
    private UserMO user;
    private String today = UI_DATE_FORMAT_SDF.format(new Date());

    public CalendarGridViewAdapter(Context context, List<Date> datesList, Map<String, DayLedger> ledger, UserMO user) {
        super();
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.datesList = datesList;
        this.user = user;
        this.ledger = ledger;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(LAYOUT, null);

            mHolder.calendar_day_date_tv = (TextView) convertView.findViewById(R.id.calendar_day_date_tv);
            mHolder.calendar_day_ll = (LinearLayout) convertView.findViewById(R.id.calendar_day_ll);
            mHolder.calendar_day_content_rl = (RelativeLayout) convertView.findViewById(R.id.calendar_day_content_rl);
            mHolder.calendar_day_transaction_ll = (LinearLayout) convertView.findViewById(R.id.calendar_day_transaction_ll);
            mHolder.calendar_day_transactions_amt_tv = (TextView) convertView.findViewById(R.id.calendar_day_transactions_amt_tv);
            mHolder.calendar_day_transfer_ll = (LinearLayout) convertView.findViewById(R.id.calendar_day_transfer_ll);
            mHolder.calendar_day_transfers_amt_tv = (TextView) convertView.findViewById(R.id.calendar_day_transfers_amt_tv);

            convertView.setTag(LAYOUT, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(LAYOUT);
        }

        Date dateObj = datesList.get(position);
        String cellDate = UI_DATE_FORMAT_SDF.format(dateObj);
        int date = Integer.parseInt(cellDate.split("-")[0]);

        //set cell data
        if(ledger != null && ledger.containsKey(cellDate)){
            DayLedger dayLedger = ledger.get(cellDate);

            //transaction
            if(dayLedger.isHasTransactions()){
                mHolder.calendar_day_transaction_ll.setVisibility(View.VISIBLE);
                mHolder.calendar_day_transactions_amt_tv = FinappleUtility.shortenAmountView(mHolder.calendar_day_transactions_amt_tv, user, dayLedger.getTransactionsAmountTotal());
            }

            //transfer
            if(dayLedger.isHasTransfers()){
                mHolder.calendar_day_transfer_ll.setVisibility(View.VISIBLE);
                mHolder.calendar_day_transfers_amt_tv = FinappleUtility.shortenAmountView(mHolder.calendar_day_transfers_amt_tv, user, dayLedger.getTransfersAmountTotal());
            }
        }

        //date text
        mHolder.calendar_day_date_tv.setText(String.valueOf(date));

        //grey dates for prev and next month
        if((position < 7 && date > 24) || (position > 27 && date < 14)){
            if(position < 7 && date > 24){
                mHolder.calendar_day_date_tv.setTag("PREV_MONTH");
            }
            else{
                mHolder.calendar_day_date_tv.setTag("NEXT_MONTH");
            }
            mHolder.calendar_day_date_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transactions_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transfers_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transaction_ll.findViewById(R.id.calendar_day_transaction_ind_tv).setBackgroundResource(R.drawable.circle_calendar_transaction_transfer_indicator_disabled);
            mHolder.calendar_day_transfer_ll.findViewById(R.id.calendar_day_transfer_ind_tv).setBackgroundResource(R.drawable.circle_calendar_transaction_transfer_indicator_disabled);
        }
        //todays date
        else if(today.equals(cellDate)){
            mHolder.calendar_day_date_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarTodayDate));
        }

        mHolder.calendar_day_content_rl.setTag(calendar_date_cell_date_key, dateObj);

        setFont(mHolder.calendar_day_ll);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private TextView calendar_day_date_tv;
        private LinearLayout calendar_day_ll;
        private RelativeLayout calendar_day_content_rl;
        private LinearLayout calendar_day_transaction_ll;
        private TextView calendar_day_transactions_amt_tv;
        private LinearLayout calendar_day_transfer_ll;
        private TextView calendar_day_transfers_amt_tv;
    }

    public Object getItem(int position) {
        return datesList.get(position);
    }

    @Override
    public int getCount() {
        return 42;
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        final Typeface font = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }
}
