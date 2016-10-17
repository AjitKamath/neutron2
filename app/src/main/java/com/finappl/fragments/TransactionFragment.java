package com.finappl.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.RepeatMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.TransactionModel;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.DB_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.FRAGMENT_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_AMOUNT;
import static com.finappl.utils.Constants.FRAGMENT_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_REPEAT;
import static com.finappl.utils.Constants.FRAGMENT_SPENTON;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION;
import static com.finappl.utils.Constants.REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.SPENTON_OBJECT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_TIME_FORMAT_SDF;

/**
 * Created by ajit on 21/3/16.
 */
public class TransactionFragment extends DialogFragment implements ImageButton.OnClickListener {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private TextView addUpdateDateTV;
    private ImageView addUpdateTranBackImg;
    private EditText addUpdateTranNameET;
    private TextView transactionContentCurrencyTV;
    private TextView transactionContentAmountTV;
    private ScrollView addUpdatePageSV;
    private LinearLayout transactionContentCategoryLL;
    private LinearLayout transactionContentAccountLL;
    private LinearLayout transactionContentSpentonLL;
    private TextView transactionContentAccountCurrencyTV;
    private TextView transactionContentAccountTotalTV;
    private RadioButton addUpdateTranExpRadio;
    private RadioButton addUpdateTranIncRadio;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private EditText addUpdateNoteET;
    private ImageView transactionSaveIV;
    private CheckBox transactionSchedueCB;
    private LinearLayout transactionSchedLL;
    private LinearLayout transactionContentRepeatLL;
    private CheckBox transactionContentRepeatCB;
    private View transactionContentNotifyDivider;
    private LinearLayout transactionContentNotifyLL;
    private RadioGroup transactionContentNotifyRG;
    private RadioButton transactionContentNotifyAddRB;
    private RadioButton transactionContentAutoAddRB;
    private TextView transactionContentNotifyAddTimeTV;
    private TextView transactionContentAutoAddTimeTV;
    private View transactionContentScheduleDivider;
    private LinearLayout transactionContentScheduleLL;
    private TextView transactionContentScheduleFromDateTV;
    private TextView transactionContentScheduleUptoDateTV;

    private UserMO loggedInUserObj;

    private CalendarDbService calendarDbService;
    private TransactionsDbService transactionsDbService;
    private AuthorizationDbService authorizationDbService;

    private List<CategoryMO> categoriesList;
    private List<AccountsMO> accountList;
    private List<SpentOnMO> spentOnList;

    private List<RepeatMO> repeatsList;

    private TransactionModel transactionModelObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getTransactionFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        try{
            transactionModelObj.setTRAN_DATE(UI_DATE_FORMAT_SDF.parse(String.valueOf(addUpdateDateTV.getText())));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Parse Exception "+pe);
            return ;
        }

        transactionModelObj.setTRAN_AMT(Double.parseDouble(String.valueOf(transactionContentAmountTV.getText()).replaceAll(",", "")));
        transactionModelObj.setTRAN_NAME(String.valueOf(addUpdateTranNameET.getText()));

        transactionModelObj.setCAT_ID(((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());
        transactionModelObj.setACC_ID(((AccountsMO)transactionContentAccountLL.getTag()).getACC_ID());
        transactionModelObj.setSPNT_ON_ID(((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        transactionModelObj.setTRAN_TYPE(String.valueOf(getView().findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        transactionModelObj.setTRAN_NOTE(String.valueOf(addUpdateNoteET.getText()));
        transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(transactionContentRepeatCB.isChecked()){
            transactionModelObj.setREPEAT_ID(((RepeatMO)transactionContentRepeatLL.getTag()).getREPEAT_ID());
            transactionModelObj.setNOTIFY(String.valueOf(getView().findViewById(transactionContentNotifyRG.getCheckedRadioButtonId()).getTag()));

            if(R.id.transactionContentNotifyAddRBId == transactionContentNotifyRG.getCheckedRadioButtonId()){
                transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentNotifyAddTimeTV.getText()));
            }
            else if(R.id.transactionContentAutoAddRBId == transactionContentNotifyRG.getCheckedRadioButtonId()){
                transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentAutoAddTimeTV.getText()));
            }


        }
    }

    private void getTransactionFromBundle() {
        transactionModelObj = (TransactionModel) getArguments().get(TRANSACTION_OBJECT);
    }

    private void setupPage() {
        //common setup
        getMasterData();

        //set Currency Code
        transactionContentCurrencyTV.setText(loggedInUserObj.getCUR_CODE());
        //set up account currency code
        transactionContentAccountCurrencyTV.setText(loggedInUserObj.getCUR_CODE());

        if(transactionModelObj.getTRAN_ID() != null){
            //this transaction is being edited
        }
        else{
            String dateStr = (UI_DATE_FORMAT_SDF.format(transactionModelObj.getTRAN_DATE())).toUpperCase();

            addUpdateDateTV.setText(dateStr);

            //set default category to be set
            CategoryMO categoryMO = transactionsDbService.getDefaultCategory(loggedInUserObj.getUSER_ID());
            setCategory(categoryMO);

            //set default account to be set
            setAccount(getDefaultAccount(accountList));

            //set default spenton to be set
            SpentOnMO spentOnMO = transactionsDbService.getDefaultSpentOn(loggedInUserObj.getUSER_ID());
            setSpenton(spentOnMO);

            //set default Repeat to be set
            RepeatMO repeatMO = transactionsDbService.getDefaultRepeat();
            setRepeat(repeatMO);

            //REPEAT
            transactionContentRepeatLL.setVisibility(View.GONE);
            transactionContentNotifyDivider.setVisibility(View.GONE);
            transactionContentNotifyLL.setVisibility(View.GONE);

            //NOTIFY
            transactionContentNotifyAddTimeTV.setVisibility(View.VISIBLE);
            transactionContentAutoAddTimeTV.setVisibility(View.INVISIBLE);

            //SCHEDULE
            transactionContentScheduleDivider.setVisibility(View.GONE);
            transactionContentScheduleLL.setVisibility(View.GONE);
            transactionContentScheduleFromDateTV.setText(dateStr);
        }
    }

    private void getMasterData() {
        categoriesList = calendarDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = calendarDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        repeatsList = calendarDbService.getAllRepeats();
    }

    private void showTransactionDatePicker(int year, int month, int day) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("day", day);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(tranDateListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showScheduleFromDatePicker(int year, int month, int day) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("day", day);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(tranDateListener);
        date.show(getFragmentManager(), "Date Picker");
    }


    private void showScheduleUptoDatePicker(int year, int month, int day) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("day", day);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(tranDateListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showNotifyTimePicker(int hour, int minute) {
        TimePickerFragment time = new TimePickerFragment();

        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        time.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        time.setCallBack(notifyTimeListener);
        time.show(getFragmentManager(), "Time Picker");
    }

    private void showAutoAddTimePicker(int hour, int minute) {
        TimePickerFragment time = new TimePickerFragment();

        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        time.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        time.setCallBack(autoAddTimeListener);
        time.show(getFragmentManager(), "Time Picker");
    }

    private void initComps(View view){
        addUpdateDateTV = (TextView) view.findViewById(R.id.addUpdateDateTVId);
        addUpdateTranBackImg = (ImageView) view.findViewById(R.id.addUpdateTranBackImgId);
        addUpdateTranNameET = (EditText) view.findViewById(R.id.addUpdateTranNameETId);
        transactionContentCurrencyTV = (TextView) view.findViewById(R.id.transactionContentCurrencyTVId);
        transactionContentAmountTV = (TextView) view.findViewById(R.id.transactionContentAmountTVId);

        addUpdatePageSV = (ScrollView) view.findViewById(R.id.addUpdatePageSVId);

        transactionContentCategoryLL = (LinearLayout) view.findViewById(R.id.transactionContentCategoryLLId);
        transactionContentAccountLL = (LinearLayout) view.findViewById(R.id.transactionContentAccountLLId);
        transactionContentSpentonLL = (LinearLayout) view.findViewById(R.id.transactionContentSpentonLLId);

        transactionContentAccountCurrencyTV = (TextView) view.findViewById(R.id.transactionContentAccountCurrencyTVId);
        transactionContentAccountTotalTV = (TextView) view.findViewById(R.id.transactionContentAccountTotalTVId);

        addUpdateTranExpRadio = (RadioButton) view.findViewById(R.id.addUpdateTranExpRadioId);
        addUpdateTranIncRadio = (RadioButton) view.findViewById(R.id.addUpdateTranIncRadioId);
        addUpdateTranExpIncRadioGrp = (RadioGroup) view.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateNoteET = (EditText) view.findViewById(R.id.addUpdateNoteETId);
        transactionSaveIV = (ImageView) view.findViewById(R.id.transactionSaveIVId);

        transactionContentRepeatLL = (LinearLayout) view.findViewById(R.id.transactionContentRepeatLLId);
        transactionContentRepeatCB = (CheckBox) view.findViewById(R.id.transactionContentRepeatCBId);

        transactionContentNotifyDivider = view.findViewById(R.id.transactionContentNotifyDividerId);
        transactionContentNotifyLL = (LinearLayout) view.findViewById(R.id.transactionContentNotifyLLId);

        transactionContentNotifyRG = (RadioGroup) view.findViewById(R.id.transactionContentNotifyRGId);
        transactionContentNotifyAddRB = (RadioButton) view.findViewById(R.id.transactionContentNotifyAddRBId);
        transactionContentAutoAddRB = (RadioButton) view.findViewById(R.id.transactionContentAutoAddRBId);

        transactionContentNotifyAddTimeTV = (TextView) view.findViewById(R.id.transactionContentNotifyAddTimeTVId);
        transactionContentAutoAddTimeTV = (TextView) view.findViewById(R.id.transactionContentAutoAddTimeTVId);

        transactionContentScheduleDivider = view.findViewById(R.id.transactionContentScheduleDividerId);
        transactionContentScheduleLL = (LinearLayout) view.findViewById(R.id.transactionContentScheduleLLId);

        transactionContentScheduleFromDateTV = (TextView) view.findViewById(R.id.transactionContentScheduleFromDateTVId);
        transactionContentScheduleUptoDateTV = (TextView) view.findViewById(R.id.transactionContentScheduleUptoDateTVId);

        addUpdateDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                        String dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                        String dateStrArr[] = dateStr.split("-");

                        int year = Integer.parseInt(dateStrArr[0]);
                        int month = Integer.parseInt(dateStrArr[1]);
                        int day = Integer.parseInt(dateStrArr[2]);

                        showTransactionDatePicker(year, month, day);

                    }
                    catch(ParseException e){
                        Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                    }

            }
        });

        transactionContentScheduleFromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    String dateStrArr[] = dateStr.split("-");

                    int year = Integer.parseInt(dateStrArr[0]);
                    int month = Integer.parseInt(dateStrArr[1]);
                    int day = Integer.parseInt(dateStrArr[2]);

                    showScheduleFromDatePicker(year, month, day);

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionContentScheduleUptoDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String dateStr = String.valueOf(((TextView)v).getText());

                    if("FOREVER".equalsIgnoreCase(dateStr)){
                        dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(transactionContentScheduleFromDateTV.getText())));
                    }
                    else{
                        dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    }

                    String dateStrArr[] = dateStr.split("-");

                    int year = Integer.parseInt(dateStrArr[0]);
                    int month = Integer.parseInt(dateStrArr[1]);
                    int day = Integer.parseInt(dateStrArr[2]);

                    showScheduleUptoDatePicker(year, month, day);
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionContentNotifyAddTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    String timeStrArr[] = timeStr.split(":");

                    int hour = Integer.parseInt(timeStrArr[0]);
                    int minute = Integer.parseInt(timeStrArr[1]);

                    showNotifyTimePicker(hour, minute);

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionContentAutoAddTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    String timeStrArr[] = timeStr.split(":");

                    int hour = Integer.parseInt(timeStrArr[0]);
                    int minute = Integer.parseInt(timeStrArr[1]);

                    showAutoAddTimePicker(hour, minute);

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionSaveIV.setOnClickListener(this);
        addUpdateTranBackImg.setOnClickListener(this);

        transactionContentAmountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmountFragment();
            }
        });
        transactionContentCategoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryFragment();
            }
        });
        transactionContentAccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountFragment();
            }
        });
        transactionContentSpentonLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpentonFragment();
            }
        });
        transactionContentRepeatLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatFragment();
            }
        });

        transactionContentRepeatCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    transactionContentRepeatLL.setVisibility(View.VISIBLE);
                    transactionContentNotifyDivider.setVisibility(View.VISIBLE);
                    transactionContentNotifyLL.setVisibility(View.VISIBLE);
                    transactionContentScheduleDivider.setVisibility(View.VISIBLE);
                    transactionContentScheduleLL.setVisibility(View.VISIBLE);

                    addUpdatePageSV.post(new Runnable() { public void run() { addUpdatePageSV.fullScroll(View.FOCUS_DOWN); } });
                }
                else{
                    addUpdatePageSV.post(new Runnable() { public void run() { addUpdatePageSV.fullScroll(View.FOCUS_UP); } });

                    transactionContentRepeatLL.setVisibility(View.GONE);
                    transactionContentNotifyDivider.setVisibility(View.GONE);
                    transactionContentNotifyLL.setVisibility(View.GONE);
                    transactionContentScheduleDivider.setVisibility(View.GONE);
                    transactionContentScheduleLL.setVisibility(View.GONE);
                }
            }
        });

        transactionContentNotifyRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.transactionContentNotifyAddRBId == checkedId){
                    transactionContentNotifyAddTimeTV.setVisibility(View.VISIBLE);
                    transactionContentAutoAddTimeTV.setVisibility(View.INVISIBLE);
                }
                else if(R.id.transactionContentAutoAddRBId == checkedId){
                    transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentAutoAddTimeTV.getText()));
                    transactionContentNotifyAddTimeTV.setVisibility(View.INVISIBLE);
                    transactionContentAutoAddTimeTV.setVisibility(View.VISIBLE);
                }
            }
        });

        addUpdateTranNameET.addTextChangedListener(fieldTextWatcher);
        transactionContentAmountTV.addTextChangedListener(fieldTextWatcher);
    }

    private AccountsMO getDefaultAccount(List<AccountsMO> accountList){
        for(AccountsMO iterList : accountList){
            if(iterList.getACC_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private void showAmountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_AMOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_AMOUNT_OBJECT, String.valueOf(transactionContentAmountTV.getText()));

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        AmountFragment amountFragment = new AmountFragment();
        amountFragment.setArguments(bundle);
        amountFragment.setTargetFragment(currentFrag, 0);
        amountFragment.show(manager, FRAGMENT_AMOUNT);
    }

    private void showRepeatFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_REPEAT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(REPEAT_OBJECT, (Serializable) repeatsList);
        bundle.putSerializable(SELECTED_REPEAT_OBJECT, ((RepeatMO)transactionContentRepeatLL.getTag()).getREPEAT_NAME());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        RepeatsFragment repeatsFragment = new RepeatsFragment();
        repeatsFragment.setArguments(bundle);
        repeatsFragment.setTargetFragment(currentFrag, 0);
        repeatsFragment.show(manager, FRAGMENT_REPEAT);
    }


    private void showCategoryFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CATEGORY);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_OBJECT, (Serializable) categoriesList);
        bundle.putSerializable(SELECTED_CATEGORY_OBJECT, ((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(bundle);
        categoriesFragment.setTargetFragment(currentFrag, 0);
        categoriesFragment.show(manager, FRAGMENT_CATEGORY);
    }

    private void showAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountsMO)transactionContentAccountLL.getTag()).getACC_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        AccountsFragment accountsFragment = new AccountsFragment();
        accountsFragment.setArguments(bundle);
        accountsFragment.setTargetFragment(currentFrag, 0);
        accountsFragment.show(manager, FRAGMENT_ACCOUNT);
    }

    private void showSpentonFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SPENTON);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SPENTON_OBJECT, (Serializable) spentOnList);
        bundle.putSerializable(SELECTED_SPENTON_OBJECT, ((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        SpentonsFragment spentonsFragment = new SpentonsFragment();
        spentonsFragment.setArguments(bundle);
        spentonsFragment.setTargetFragment(currentFrag, 0);
        spentonsFragment.show(manager, FRAGMENT_SPENTON);
    }

    private void getLoggedInUser(){
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
    }

    @Override
    public void onClick(View v) {
        String messageStr = "Error !!";
        if(v.getId() == transactionSaveIV.getId()){
            getInputs();

            //if transactionModelObj contains transactionId, then its an update. if not its a new transaction
            if(transactionModelObj.getTRAN_ID() == null){
                transactionModelObj.setTRAN_ID(IdGenerator.getInstance().generateUniqueId("TRAN"));
                long result = transactionsDbService.addNewTransaction(transactionModelObj);

                if(result == -1) {
                    messageStr = "Failed to create a new Transaction !";
                }
                else{
                    messageStr = "New Transaction created";
                }
            }
            else{
                long result = transactionsDbService.updateOldTransaction(transactionModelObj);

                if(result == 0) {
                    messageStr = "Failed to update Transaction/Account !";
                } else if(result == 1){
                    messageStr = "Transaction updated";
                }
                else{
                    messageStr = "Unknown error !";
                }
            }
        }

        CalendarActivity activity = (CalendarActivity) this.getActivity();
        activity.onFinishUserDialog(messageStr);
        this.dismiss();
    }

    private void setUpSaveButton(){
        LinearLayout transactionsButtonLL = (LinearLayout) getView().findViewById(R.id.transactionsButtonLLId);
        if("HIDDEN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
            transactionsButtonLL.animate().setDuration(250).translationX(-80);
            transactionSaveIV.setTag("SHOWN");

        } else{
            transactionsButtonLL.animate().setDuration(250).translationX(0);
            transactionSaveIV.setTag("HIDDEN");
        }
    }

    TextWatcher fieldTextWatcher;
    {
        fieldTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String amountStr = String.valueOf(transactionContentAmountTV.getText());

                if(!String.valueOf(addUpdateTranNameET.getText()).trim().isEmpty() && !(amountStr.equalsIgnoreCase("0") || amountStr.equalsIgnoreCase("0.0"))){
                    if("HIDDEN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
                        setUpSaveButton();
                    }
                }
                else{
                    if("SHOWN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
                        setUpSaveButton();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }

    private DatePickerDialog.OnDateSetListener tranDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "TRANSACTION_DATE");
        }
    };

    private DatePickerDialog.OnDateSetListener scheduleFromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "SCHEDULE_FROM");
        }
    };

    private DatePickerDialog.OnDateSetListener scheduleUptoDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "SCHEDULE_UPTO");
        }
    };

    private TimePickerDialog.OnTimeSetListener notifyTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int hour, int minute) {
            setTime(hour, minute, "NOTIFY_TIME");
        }
    };

    private TimePickerDialog.OnTimeSetListener autoAddTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int hour, int minute) {
            setTime(hour, minute, "AUTO_ADD_TIME");
        }
    };

    private void setDate(int year, int month, int day, String uiComponentStr) {
        try {
            String dateStr = year + "-" + month + "-" + day;
            dateStr = UI_DATE_FORMAT_SDF.format(DB_DATE_FORMAT_SDF.parseObject(dateStr));

            if("TRANSACTION_DATE".equalsIgnoreCase(uiComponentStr)){
                addUpdateDateTV.setText(dateStr.toUpperCase());
            }
            if("SCHEDULE_FROM".equalsIgnoreCase(uiComponentStr)){
                transactionContentScheduleFromDateTV.setText(dateStr.toUpperCase());
            }
            if("SCHEDULE_UPTO".equalsIgnoreCase(uiComponentStr)){
                transactionContentScheduleUptoDateTV.setText(dateStr.toUpperCase());
            }
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error in parsing the date : "+e);
        }
    }

    private void setTime(int hour, int minute, String uiComponentStr) {
        try {
            String timeStr = hour + ":" + minute;
            timeStr = UI_TIME_FORMAT_SDF.format(DB_TIME_FORMAT_SDF.parseObject(timeStr));

            if("NOTIFY_TIME".equalsIgnoreCase(uiComponentStr)){
                transactionContentNotifyAddTimeTV.setText(timeStr.toUpperCase());
            }
            if("AUTO_ADD_TIME".equalsIgnoreCase(uiComponentStr)){
                transactionContentAutoAddTimeTV.setText(timeStr.toUpperCase());
            }
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error in parsing the time : "+e);
        }
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public TransactionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();

        getLoggedInUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            //addUpdateTranAmtET.requestFocus();
        }
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
        transactionsDbService = new TransactionsDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
    }

    public void onFinishDialog(CategoryMO categoryMO) {
        setCategory(categoryMO);
    }
    public void onFinishDialog(AccountsMO accountsMO) {
        setAccount(accountsMO);
    }
    public void onFinishDialog(SpentOnMO spentOnMO) {
        setSpenton(spentOnMO);
    }
    public void onFinishDialog(RepeatMO repeatMO) {
        setRepeat(repeatMO);
    }
    public void onFinishDialog(String amountStr) {
        setAmount(amountStr);
    }

    private void setCategory(CategoryMO categoryMO){
        ((TextView)transactionContentCategoryLL.findViewById(R.id.transactionContentCategoryTVId)).setText(categoryMO.getCAT_NAME());
        transactionContentCategoryLL.setTag(categoryMO);
    }

    private void setAccount(AccountsMO accountsMO){
        ((TextView)transactionContentAccountLL.findViewById(R.id.transactionContentAccountTVId)).setText(accountsMO.getACC_NAME());

        Double accountTotal = accountsMO.getACC_TOTAL();
        if(accountTotal <= 0){
            if(accountTotal < 0) {
                accountTotal = accountTotal * -1;
            }
            transactionContentAccountTotalTV.setTextColor(transactionContentAccountTotalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            transactionContentAccountTotalTV.setTextColor(transactionContentAccountTotalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }
        transactionContentAccountTotalTV.setText(FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), String.valueOf(accountTotal)));

        transactionContentAccountLL.setTag(accountsMO);
    }

    private void setSpenton(SpentOnMO spentOnMO){
        ((TextView)transactionContentSpentonLL.findViewById(R.id.transactionContentSpentonTVId)).setText(spentOnMO.getSPNT_ON_NAME());
        transactionContentSpentonLL.setTag(spentOnMO);
    }
    private void setRepeat(RepeatMO repeatMo){
        ((TextView)transactionContentRepeatLL.findViewById(R.id.transactionContentRepeatTVId)).setText(repeatMo.getREPEAT_NAME());
        transactionContentRepeatLL.setTag(repeatMo);
    }

    private void setAmount(String amountStr){
        transactionContentAmountTV.setText(amountStr);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
    }
}