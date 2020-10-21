package com.cookietech.chordlibrary.Fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordlibrary.AppComponent.ChordInfoSpannableAdapter;
import com.cookietech.chordlibrary.Chord;
import com.cookietech.chordlibrary.ChordClass;
import com.cookietech.chordlibrary.ChordFactory;
import com.cookietech.chordlibrary.ChordsAdapter;
import com.cookietech.chordlibrary.Root;
import com.cookietech.chordlibrary.databinding.FragmentChordLibraryBinding;
import com.cookietech.chordlibrary.databinding.LayoutChordLibraryBottomSheetBinding;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordLibraryFragment extends Fragment implements ChordsAdapter.Communicator, MidiDriver.OnMidiStartListener {

    private ChordsAdapter chordsAdapter;
    private int canvasWidth ;
    private int canvasHeight;

    private ChordFactory chordFactory;
    private ArrayList<Root> rootArrayList = new ArrayList<>();
    ArrayList<Chord> chords =new ArrayList<>();

    int previouslyScrolled = 0;

    private MidiDriver midiDriver;
    private byte[] event;
    private int[] midiRoots = {40,45,50,55,59,64};

    private FragmentChordLibraryBinding binding;
    private DialogFragment dialogFragment ;
    private boolean isChordSelectorInitiated = false;
    private Bitmap fretboard;

    private List<String> homeList =new ArrayList<>();
    private List<String> typeList = new ArrayList<>();
    private int selectedHomeIndex = 0;
    LayoutChordLibraryBottomSheetBinding  bottomSheetBinding;

    int dy;

    public static ChordLibraryFragment newInstance() {
        return new ChordLibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeList.addAll(Arrays.asList("A","A#", "B", "C","C#","D","D#","E","F","F#","G","G#"));
        typeList.addAll(Arrays.asList("major","minor","dim","aug","2","7","m7","maj7", "dim7","m/maj7","7+5","7sus2","7sus4","6","m6","9","-9","m9"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordLibraryBinding.inflate(getLayoutInflater(),container,false);
        bottomSheetBinding = binding.bottomSheet;
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate the driver.
        midiDriver = new MidiDriver();
        // Set the listener.
        midiDriver.setOnMidiStartListener(this);



        chordFactory = new ChordFactory(requireContext());
        rootArrayList = chordFactory.getRoots();
        if(!rootArrayList.isEmpty()){
            homeList = new ArrayList<>();
            for (Root root:rootArrayList){
                homeList.add(root.getName());
            }
        }



        updateChordTypeList(selectedHomeIndex);




        chords = rootArrayList.get(0).getChordClasses().get(0).getChords();

        binding.chordsRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.chordsRecyclerview.setLayoutManager(layoutManager);
        chordsAdapter = new ChordsAdapter(requireContext(),chords,this);
        binding.chordsRecyclerview.setAdapter(chordsAdapter);



        Chord chord = chords.get(0);
        binding.fretbardContainer.setChord(chord);









        binding.chordSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.chordSelectorHolder.getVisibility() == View.VISIBLE){
                    hideChordSelector();
                }else{
                    showChordSelector();
                }
            }
        });

        binding.chordSelectorHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChordSelector();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("akash_debug", "onClick: ");
            }
        });


        SetUpChordSelector();



    }

    private void updateChordTypeList(int selectedHomeIndex) {
        if(!rootArrayList.get(selectedHomeIndex).getChordClasses().isEmpty()){
            typeList = new ArrayList<>();
            for(ChordClass chordClass:rootArrayList.get(0).getChordClasses()){
                typeList.add(chordClass.getName());
            }
        }
    }


    private void hideChordSelector() {
        binding.chordSelectorBody.animate().alpha(0).scaleX(0).scaleY(0).setDuration(100);
        binding.chordSelectorBody.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                binding.chordSelectorHolder.setVisibility(View.INVISIBLE);
            }
        },100);
    }

    private void showChordSelector() {
        binding.chordSelectorBody.animate().alpha(1).scaleX(1).scaleY(1).setDuration(100);
        binding.chordSelectorBody.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                binding.chordSelectorHolder.setVisibility(View.VISIBLE);
            }
        },100);
    }


    @Override
    public void onChordSelected(int position) {
        Chord chord = chords.get(position);
        setChord(chord);
    }


    @Override
    public void onResume() {
        super.onResume();
        midiDriver.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        midiDriver.stop();
    }



    private void setChord(final Chord chord) {
      /*  fretBoardGenerator.generateChord(chord);
        int scrollingDistance = FretBoardGenerator.getScrollIngDistance();
        dy = scrollingDistance - previouslyScrolled;
      *//*  binding.ivFretboard.invalidate();
        binding.fretBoardScroller.smoothScrollBy(0,dy);*/

        binding.fretbardContainer.setChord(chord);
        playChord(chord);

        setChordInfo(chord);

        //previouslyScrolled += dy;



    }

    private void setChordInfo(Chord chord) {
        SpannableStringBuilder spannableStringBuilder = new ChordInfoSpannableAdapter(chord);
        bottomSheetBinding.bottomInfo.setText(spannableStringBuilder);
    }

    private void playChord(Chord chord) {
        int interval = 0;
        for (int i = 0; i < 6; i++) {
            int note = chord.getNotes().get(i);
            int midiRoot = midiRoots[i];
            if(note != -1){
                final byte midiNote = (byte) (midiRoot + note);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendPlayNote(midiNote);
                    }
                },interval);
                interval += 50;
            }

        }
    }

    private void sendPlayNote(byte note) {

        // Construct a program change to select the instrument on channel 1:
        event = new byte[2];
        event[0] = (byte)(0xC0 | 0x00); // 0xC0 = program change, 0x00 = channel 1
        event[1] = (byte)25;

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 50;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    private void SetUpChordSelector(){
        changeTheTopChordText(0,0);
        binding.chordPicker.setOptions(new PicketOptions.Builder()
                .linkage(false)                                   // 是否联动
                .dividedEqually(true)                            // 每列宽度是否均等分
                .backgroundColor(Color.parseColor("#ffffff"))     // 背景颜色
                .dividerColor(Color.parseColor("#22374C"))
                .cyclic(true)
                .build());

        final PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 2;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                switch (component){
                    case 0:
                        return homeList.size();
                    case 1:
                        return typeList.size();
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                String str = "";
                switch (component) {
                    case 0:
                        str = homeList.get(row);
                        break;
                    case 1:
                        str = typeList.get(row);
                        break;
                }
                return new StringItemView(String.valueOf(str)).onCreateView(parent);
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {

                String str = "";
                switch (component) {
                    case 0:
                        str = homeList.get(row);
                        break;
                    case 1:
                        str = typeList.get(row);
                        break;
                }
                new StringItemView(String.valueOf(str)).onBindView(parent, convertView, row);
            }
        };
        binding.chordPicker.setAdapter(adapter);
        binding.chordPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                // 选中后的回调
                chords = rootArrayList.get(position[0]).getChordClasses().get(position[1]).getChords();
                updateChordTypeList(position[0]);
                Chord chord = chords.get(0);
                binding.fretbardContainer.setChord(chord);
                chordsAdapter.setChords(chords);
                changeTheTopChordText(position[0],position[1]);

            }
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chordSelectorHolder.setVisibility(View.INVISIBLE);
            }
        });
        Log.d("akash_debug", "onViewCreated: ");
    }



    private void sendStopNote(byte note) {

        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    @Override
    public void onMidiStart() {
        Log.d("akash_debug", "onMidiStart: ");
    }

    private void changeTheTopChordText(int selectedHome, int selectedType){
        String selectedChord = "";
        if(typeList.get(selectedType).equals("major")){
            selectedChord = homeList.get(selectedHome);
        }else if(typeList.get(selectedType).equals("minor")){
            selectedChord = homeList.get(selectedHome)+"m";
        }else{
            selectedChord = homeList.get(selectedHome)+typeList.get(selectedType);
        }

        binding.selectedChord.setText(selectedChord);
        binding.chordSelectorText.setText(selectedChord);
    }
}