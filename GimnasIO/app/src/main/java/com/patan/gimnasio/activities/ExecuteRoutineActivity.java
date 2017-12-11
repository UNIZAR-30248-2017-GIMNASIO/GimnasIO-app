package com.patan.gimnasio.activities;

import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewAnimator;

import com.patan.gimnasio.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_routine);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        public static ExerciseFragment newInstance(int sectionNumber) {
            ExerciseFragment fragment = new ExerciseFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_exercise, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format_exercise, getArguments().getInt(ARG_SECTION_NUMBER)));
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

        public RelaxFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RelaxFragment newInstance(int sectionNumber) {
            RelaxFragment fragment = new RelaxFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_execute_routine_relax, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format_relax, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
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
            position = position + 1;
            if(position == 1) {
                return StartFragment.newInstance(position);
            } else if(position < getCount() && (position % 2 != 1)) {
                return ExerciseFragment.newInstance(position);
            } else if(position < getCount() && (position % 2 == 1)) {
                return RelaxFragment.newInstance(position);
            } else {
                return EndFragment.newInstance(position);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}
