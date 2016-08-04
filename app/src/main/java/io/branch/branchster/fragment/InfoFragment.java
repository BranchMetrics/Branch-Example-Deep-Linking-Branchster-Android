package io.branch.branchster.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.branch.branchster.MonsterViewerActivity;
import io.branch.branchster.R;

/**
 * @author sahilverma
 * @since 06-03-2015
 */
public class InfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "InfoFragment";
    private OnFragmentInteractionListener mListener;
    private Button mButton;


    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        mButton = (Button) v.findViewById(R.id.button);
        mButton.setOnClickListener(this);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            ((TextView)v.findViewById(R.id.version_name_txt)).setText(String.format(getString(R.string.version_name),versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return v;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "Error", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }

    @Override
    public void onClick(View view) {
        Uri webpage = Uri.parse("http://branch.io?bmp=branchster-android");
        Intent i = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(i);
    }

}
