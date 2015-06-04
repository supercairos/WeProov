package com.weproov.app.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.InjectView;
import com.activeandroid.content.ContentProvider;
import com.weproov.app.R;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.adapter.DocumentAdapter;

public class DocumentListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String DOCUMENT_LIST_MODE_KEY = "document_mode_key";
	public static final int MODE_ALL = 1;
	public static final int MODE_ONLY_CHECKOUT = 1;

	private static final int LOADER_ID = 1337;

	@InjectView(R.id.document_list_view)
	ListView mListView;

	private DocumentAdapter mAdapter;

	private int mMode;

	public static Fragment newInstance(int mode) {
		DocumentListFragment fragment = new DocumentListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(DOCUMENT_LIST_MODE_KEY, mode);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mMode = getArguments().getInt(DOCUMENT_LIST_MODE_KEY);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_document_list, container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), ContentProvider.createUri(WeProov.class, null), null, null, null, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (mAdapter == null) {
			mAdapter = new DocumentAdapter(getActivity(), data);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}
