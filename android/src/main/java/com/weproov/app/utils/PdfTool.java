package com.weproov.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import com.weproov.app.R;

import java.io.File;

public class PdfTool {

	private static final String GOOGLE_DRIVE_PDF_READER_PREFIX = "http://drive.google.com/viewer?url=";
	private static final String PDF_MIME_TYPE = "application/pdf";
	private static final String HTML_MIME_TYPE = "text/html";

	/**
	 * Show a dialog asking the user if he wants to open the PDF through Google Drive
	 */
	public static void askToOpenPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.pdf_show_online_dialog_title)
				.setMessage(R.string.pdf_show_online_dialog_question)
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openPDFThroughGoogleDrive(context, pdfUrl);
					}
				})
				.show();
	}

	/**
	 * Launches a browser to view the PDF through Google Drive
	 */
	public static void openPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse(GOOGLE_DRIVE_PDF_READER_PREFIX + pdfUrl), HTML_MIME_TYPE);
		context.startActivity(i);
	}

	/**
	 * Open a local PDF file with an installed reader
	 */
	public static void open(Context context, Uri localUri) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(localUri, PDF_MIME_TYPE);
		context.startActivity(i);
	}

	/**
	 * Checks if any apps are installed that supports reading of PDF files.
	 */
	public static boolean isSupported(Context context) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		final File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.pdf");
		i.setDataAndType(Uri.fromFile(tempFile), PDF_MIME_TYPE);
		return context.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}
}