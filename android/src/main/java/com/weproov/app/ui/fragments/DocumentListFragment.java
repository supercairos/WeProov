package com.weproov.app.ui.fragments;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.InjectView;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.adapter.DocumentAdapter;
import com.weproov.app.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<WeProov>>, AdapterView.OnItemClickListener {

	private static final String DOCUMENT_QUERY_STRING = "document_query_string";
	private static final String DOCUMENT_LIST_MODE_KEY = "document_mode_key";
	private static final String DOCUMENT_ID = "download_key_id";
	private static final String DOCUMENT_FILE = "download_key_id";

	public static final int MODE_ALL = 0;
	public static final int MODE_ONLY_CHECKOUT = 1;

	private static final int LOADER_ID = 1337;


	private Handler mHandler = new Handler();
	private final List<WeProov> mProovs = new ArrayList<>();
	private ContentObserver mDataObserver = new ThrottledContentObserver(mHandler, 500) {

		@Override
		public void onChangeThrottled() {
			getLoaderManager().restartLoader(LOADER_ID, null, DocumentListFragment.this);
		}
	};

	private long mDownloadId = -1;
	private DownloadManager mDownloadManager;
	private ProgressDialog mProgressDialog;

	private File mPdfFile;
	private ContentObserver mDownloadObserver = new ThrottledContentObserver(mHandler, 500) {

		@Override
		public void onChangeThrottled() {
			if (mDownloadId > -1) {
				Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(mDownloadId));
				if (c != null) {
					try {
						if (c.moveToFirst()) {
//							int filenameIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
							int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
							int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

//							String filename = c.getString(filenameIndex);
							long size = c.getInt(sizeIndex);
							long downloaded = c.getInt(downloadedIndex);

							if (size != -1) {
								mProgressDialog.setMax(100);
								mProgressDialog.setIndeterminate(false);
								mProgressDialog.setProgress((int) (downloaded * 100.0 / size));
							}
						}
					} finally {
						c.close();
					}
				}
			}
		}
	};

	private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (mDownloadId >= 0 && mDownloadId == downloadId) {
				// Reset state
				mDownloadId = -1;

				Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
				if (c != null) {
					try {
						if (c.moveToFirst()) {
							int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
							if (status == DownloadManager.STATUS_SUCCESSFUL) {
								PdfTool.open(context, Uri.fromFile(mPdfFile));
							}
						}
					} finally {
						c.close();
					}
				}
			}
		}
	};

	@InjectView(R.id.document_list_view)
	ListView mListView;

	private DocumentAdapter mAdapter;

	private int mMode;
	private String mQuery;

	public void setQuery(String query) {
		mQuery = query;
		getLoaderManager().restartLoader(LOADER_ID, null, DocumentListFragment.this);
	}

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

		if (savedInstanceState != null) {
			mQuery = savedInstanceState.getString(DOCUMENT_QUERY_STRING);
			mDownloadId = savedInstanceState.getInt(DOCUMENT_ID);
			String path = savedInstanceState.getString(DOCUMENT_FILE);
			if (!TextUtils.isEmpty(path)) {
				mPdfFile = new File(path);
			}
		}

		mDownloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_document_list, container, false);
	}

	@Override
	public Loader<List<WeProov>> onCreateLoader(int id, Bundle args) {
		return new BaseAsyncTaskLoader<List<WeProov>>(getActivity()) {
			@Override
			public List<WeProov> loadInBackground() {
				From from = new Select().from(WeProov.class).as("weproov")
						.join(CarInfo.class).as("car").on("car._id = weproov.car")
						.join(ClientInfo.class).as("client").on("client._id = weproov.client");

				from.where("1 = 1");
				if (mMode == MODE_ONLY_CHECKOUT) {
					from.and("weproov.checkout = ?", true);
				}

				if (!TextUtils.isEmpty(mQuery)) {
					from.and("(car.plate LIKE ?", "%" + mQuery + "%");
					from.or("car.model LIKE ?", "%" + mQuery + "%");
					from.or("client.lastname LIKE ?", "%" + mQuery + "%");
					from.or("client.firstname LIKE ?", "%" + mQuery + "%");
					from.or("car.brand LIKE ?)", "%" + mQuery + "%");
				}

				Dog.v("Query >> " + from.toSql());
				List<WeProov> proovs = from.execute();

				// Preload pictures;
				for (WeProov proov : proovs) {
					proov.getPictures();
				}

				Dog.d("Proovs size : " + proovs.size());

				return proovs;
			}
		};
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		getLoaderManager().restartLoader(LOADER_ID, null, this);

		getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(WeProov.class, null), true, mDataObserver);
		getActivity().getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);
		getActivity().registerReceiver(mDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().getContentResolver().unregisterContentObserver(mDataObserver);
		getActivity().getContentResolver().unregisterContentObserver(mDownloadObserver);
		getActivity().unregisterReceiver(mDownloadCompleteReceiver);

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		Intent i = new Intent(getActivity(), DocumentDisplayActivity.class);
//		i.putExtra(DocumentDisplayActivity.EXTRA_WEPROOV_ID, .getId());
//		startActivity(i);
		String objectId = mAdapter.getItem(position).proovCode;
		if(!TextUtils.isEmpty(objectId)) {
			String file = getUrl(objectId);
			Dog.d("Url is >> " + file);
			if (PdfTool.isSupported(getActivity())) {
				download(objectId, file);
			} else {
				PdfTool.askToOpenPDFThroughGoogleDrive(getActivity(), file);
			}
		} else {
			Snackbar.make(mListView, R.string.document_is_not_availlable_yet, Snackbar.LENGTH_LONG).show();
		}
	}

	private String getUrl(String id) {
		String hash2 = CryptoUtils.sha1(CryptoUtils.sha1(id) + "@wePr00v!@");
		String hash1 = CryptoUtils.sha1(CryptoUtils.sha1(id) + "weProov2k15@");
		return "http://weproov.com/visu/temp/download_pdf.php?id=" + id + "&hash=" + hash1 + "&hash2=" + hash2;
	}

	private void download(String id, String string) {

		// Get filename
		final String filename = id + ".pdf"; //string.substring(string.lastIndexOf("/") + 1);

		// The place where the downloaded PDF file will be put
		mPdfFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
		if (mPdfFile.exists()) {
			// If we have downloaded the file before, just go ahead and show it.
			mDownloadId = -1;
			PdfTool.open(getActivity(), Uri.fromFile(mPdfFile));
			return;
		}

		// Show progress dialog while downloading
		mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.pdf_show_local_progress_title), getString(R.string.pdf_show_local_progress_content, filename), true);

		// Create the download request
		DownloadManager.Request r = new DownloadManager.Request(Uri.parse(string));
		r.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, filename);

		// Enqueue the request
		mDownloadId = mDownloadManager.enqueue(r);
	}

	@Override
	public void onLoadFinished(Loader<List<WeProov>> loader, List<WeProov> data) {
		if (data != null) {
			mProovs.clear();
			mProovs.addAll(data);

			if (mAdapter == null) {
				mAdapter = new DocumentAdapter(getActivity(), mProovs);
				mAdapter.setQuery(mQuery);
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.setQuery(mQuery);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<List<WeProov>> loader) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(DOCUMENT_QUERY_STRING, mQuery);
		outState.putLong(DOCUMENT_ID, mDownloadId);
		if (mPdfFile != null) {
			outState.putString(DOCUMENT_FILE, mPdfFile.getAbsolutePath());
		}
	}


}
