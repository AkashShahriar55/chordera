package com.blz.cookietech.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

import com.blz.cookietech.Listener.TimePickerListener;
import com.blz.cookietech.cookietechmetronomelibrary.R;
import com.blz.cookietech.cookietechmetronomelibrary.View.TimePicker;

import java.util.Locale;
import java.util.Objects;

public class TimerDialog extends AppCompatDialogFragment implements TimePickerListener {
    private static final String TAG = "TimerDialog";

    private TimePicker timePicker;
    private TextView dialogTimeText;
    private Button timerEnablingBtn,timerDialogOkBtn;
    private boolean isTimerEnabled;
    private int minutes;

    private TimerDialogListener timerDialogListener;

    public TimerDialog(boolean isTimerEnabled, int minutes) {
        this.isTimerEnabled = isTimerEnabled;
        this.minutes = minutes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_timer_dialog,null);
        builder.setView(view);

        /** Initialize time picker and set default value passed from where it is called **
         * But time picker works on position value with respect divisor of five
         **/
        timePicker = view.findViewById(R.id.timePicker);
        int timeOfPosition = (minutes/5) - 1;
        timePicker.setTime(timeOfPosition);

        /** Initialize enable/ disable button **/
        timerEnablingBtn = view.findViewById(R.id.timerEnablingBtn);
        changeActivateBtnAppearance();

        /** Initialize Dialog Time Text **/
        dialogTimeText = view.findViewById(R.id.dialogTimeText);
        timePicker.setTimeSetListener(this);
        /** Initialize the Finish Button and set onClick Listener **/
        timerDialogOkBtn = view.findViewById(R.id.timerDialogOkBtn);

        timerDialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timerDialogListener.getTimePickerStatus(minutes,isTimerEnabled);
                dismiss();
            }
        });

        /** Get default time or previously set time from where it is called and set it for dialog time text**/
        String time = String.format(Locale.getDefault(),"%02d",minutes);
        dialogTimeText.setText(time);
        changeActivateBtnAppearance();

        timerEnablingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTimerEnabled = !isTimerEnabled;
                changeActivateBtnAppearance();

            }
        });



        final AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(view);




        return dialog;
    }

    private void changeActivateBtnAppearance() {
        if (isTimerEnabled){
            timerEnablingBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.dialog_btn_active_background,null));
            timerEnablingBtn.setText(R.string.deactivate);
        }
        else {
            timerEnablingBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.dialog_btn_background,null));
            timerEnablingBtn.setText(R.string.activate);
        }
    }

    @Override
    public void onTimeSet(int timerMinute) {

        String time = String.format(Locale.getDefault(),"%02d", timerMinute);
        minutes = timerMinute;
        dialogTimeText.setText(time);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called");



        super.onAttach(context);
        try {
            timerDialogListener = (TimerDialogListener) getParentFragment();

        } catch (ClassCastException e) {
            Log.e("Error AdServiceDialog", Objects.requireNonNull(e.getMessage()));
        }

    }

    public interface TimerDialogListener {
        void getTimePickerStatus(int minute,boolean isEnabled);
    }
}
