package com.smartec.moyu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    public static final String ARG_TITLE = "TITLE";
    public static final String ARG_FULL_SNIPPET = "FULL_SNIPPET";

    private String title;
    private String fullSnippet;

    public static DialogFragment newInstance(String title, String fullSnippet){
        DialogFragment fragment = new DialogFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        b.putString(ARG_FULL_SNIPPET, fullSnippet);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        title = args.getString(ARG_TITLE);
        fullSnippet = args.getString(ARG_FULL_SNIPPET);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(fullSnippet).create();
        return dialog;
    }

}
