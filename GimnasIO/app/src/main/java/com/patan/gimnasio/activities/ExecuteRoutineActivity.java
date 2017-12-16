package com.patan.gimnasio.activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.patan.gimnasio.R;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.domain.ExerciseFull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuteRoutineActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Chronometer mChronometer;
    private boolean mChronoStarted;

    private GymnasioDBAdapter db;

    private ArrayList<ExerciseFull> mExercises;

    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new GymnasioDBAdapter(this);
        db.open();

        Long routineId = getIntent().getLongExtra("Routine", 0);
        mExercises = getRoutineExercisesDB(routineId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_routine);



        startButton = (FloatingActionButton) findViewById(R.id.startButton);
        stopButton = (FloatingActionButton) findViewById(R.id.stopButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mChronometer = (Chronometer) findViewById(R.id.chronometer3);

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
    public void onPause() {

        super.onPause();
        mChronometer.stop();
    }

    @Override
    public void onResume() {

        super.onResume();
        if(mChronoStarted){
            mChronometer.start();
        }
    }

    public ArrayList<ExerciseFull> getRoutineExercisesDB(Long id_in) {
        ArrayList<ExerciseFull> efrArray = new ArrayList<>();
        // Cogemos los ejercicios de la rutina
        Cursor exercises = db.getExercisesFromRoutine(id_in);
        startManagingCursor(exercises);
        if (exercises != null) {
            int times = exercises.getCount();
            int index_id = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID);
            int index_name = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME);
            int index_muscle = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_MUSCLE);
            int index_description = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_DESC);
            int index_image = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_IMG);
            int index_tag = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EX_TAG);
            int index_series = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXSER);
            int index_rep = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXREP);
            int index_relax = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXRT);

            for (int i = 0; i < times; i++) {
                int series = exercises.getInt(index_series);
                int repetitions = exercises.getInt(index_rep);
                double relaxTime = exercises.getDouble(index_relax);
                String name = exercises.getString(index_name);
                String muscle = exercises.getString(index_muscle);
                String description = exercises.getString(index_description);
                String image = exercises.getString(index_image);
                String[] tags = exercises.getString(index_tag).split(",");
                ExerciseFull ex = new ExerciseFull(name, muscle, description, image, repetitions, series, relaxTime, new ArrayList<>(Arrays.asList(tags)));
                efrArray.add(ex);
                exercises.moveToNext();
            }
        }
        stopManagingCursor(exercises);
        return efrArray;
    }

    public void startChrono(View v) {
        mChronometer.start();
        mChronoStarted = true;
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }
    public void stopChrono(View view) {
        mChronometer.stop();
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_execute_routine, menu);
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

    public void playAnimation(View view) {
        ImageView swipe1 = (ImageView) view.findViewById(R.id.swipe);
        ViewAnimator viewAnimator = (ViewAnimator) view.findViewById(R.id.viewanimator);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        in.setDuration(1000);
        out.setDuration(1000);
        if(swipe1.getVisibility() == View.INVISIBLE) {
            viewAnimator.showPrevious();
            //swipe1.setVisibility(View.VISIBLE);
        } else {
            viewAnimator.setInAnimation(in);
            viewAnimator.setOutAnimation(out);
            viewAnimator.setAnimateFirstView(true);
            viewAnimator.showNext();
            //swipe1.setVisibility(View.INVISIBLE);
        }

    }

    public void closeActivity(View view) {
        finish();
    }

    public void startCountdown(View view) {
        //getFragmentManager().findFragmentByTag("rt" + )
    }


    /**
     * A start fragment containing the start view of execute routine.
     */
    public static class StartFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public StartFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StartFragment newInstance(int sectionNumber) {
            StartFragment fragment = new StartFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_start, container, false);

            return rootView;
        }

    }

    /**
     * A start fragment containing the start view of execute routine.
     */
    public static class ExerciseFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ExerciseFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ExerciseFragment newInstance(int sectionNumber, ArrayList<ExerciseFull> exercises) {
            ExerciseFragment fragment = new ExerciseFragment();
            Bundle args = new Bundle();
            sectionNumber += -1;
            ExerciseFull ex = exercises.get(sectionNumber/2);
            args.putString("name", ex.getName());
            args.putString("image", ex.getImage());
            args.putString("muscle", ex.getMuscle());
            args.putString("description", ex.getDescription());
            args.putStringArrayList("tags", ex.getTags());
            args.putInt("series", ex.getSeries());
            args.putInt("reps", ex.getRepetitions());
            args.putDouble("relax", ex.getRelaxTime());
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //TODO: colocar aqui el resto de info del ejercicio
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_exercise, container, false);

            TextView nameTv = (TextView) rootView.findViewById(R.id.ex_name);
            nameTv.setText(getArguments().getString("name"));

            ImageView imageView = (ImageView) rootView.findViewById(R.id.ex_image);
            File path = new File(getArguments().getString("image", "gym1.jpg"));
            Bitmap myBitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setAdjustViewBounds(true);

            TextView descriptionTv = (TextView) rootView.findViewById(R.id.ex_description);
            descriptionTv.setText(getArguments().getString("description"));

//            TextView tagsTv = (TextView) rootView.findViewById(R.id.ex_tags);
//            tagsTv.setText(getArguments().getStringArrayList("tags").toString());

            TextView muscleTv = (TextView) rootView.findViewById(R.id.ex_muscle);
            muscleTv.setText(getArguments().getString("muscle"));

            TextView repsTv = (TextView) rootView.findViewById(R.id.ex_reps);
            String repsText = "Repeticiones: " + Integer.toString(getArguments().getInt("reps"));
            repsTv.setText(repsText);

            TextView seriesTv = (TextView) rootView.findViewById(R.id.ex_series);
            String seriesText = "Series: " + Integer.toString(getArguments().getInt("series"));
            seriesTv.setText(seriesText);

            return rootView;
        }
    }

    /**
     * A start fragment containing the start view of execute routine.
     */
    public static class RelaxFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private ProgressBar mProgressBar;

        private TextView mCountdownTv;

        public RelaxFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RelaxFragment newInstance(int sectionNumber, ArrayList<ExerciseFull> exercises) {
            RelaxFragment fragment = new RelaxFragment();
            Bundle args = new Bundle();
            sectionNumber += -2;
            ExerciseFull ex = exercises.get(sectionNumber/2);
            args.putString("name", ex.getName());
            args.putDouble("relaxTime", ex.getRelaxTime());
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_relax, container, false);

            mProgressBar = (ProgressBar) rootView.findViewById(R.id.relaxTimeProgress);

            System.out.println("tiempo: " + (int) getArguments().getDouble("relaxTime"));
            mProgressBar.setMax((int)getArguments().getDouble("relaxTime"));

            mCountdownTv = (TextView) rootView.findViewById(R.id.relaxTimeCountdown) ;

            new CountDownTimer((long)getArguments().getDouble("relaxTime") * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    mProgressBar.incrementProgressBy(1);
                    mCountdownTv.setText(Long.toString(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    mProgressBar.incrementProgressBy(1);
                    mCountdownTv.setText("0");
                }
            }.start();

            return rootView;
        }

        public void startCountdown(View v) {
            System.out.println("VAMO A VE");

        }

    }

    /**
     * A start fragment containing the start view of execute routine.
     */
    public static class EndFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public EndFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EndFragment newInstance(int sectionNumber) {
            EndFragment fragment = new EndFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_end, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 2) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                startChrono(findViewById(R.id.activity_routine_execute));
            }
            position = position + 1;
            if(position == 1) {
                return StartFragment.newInstance(position);
            } else if(position < getCount() && (position % 2 != 1)) {
                return ExerciseFragment.newInstance(position, mExercises);
            } else if(position < getCount() && (position % 2 == 1)) {
                return RelaxFragment.newInstance(position, mExercises);
            } else {
                return EndFragment.newInstance(position);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mExercises.size()*2 + 2;
        }
    }
}
