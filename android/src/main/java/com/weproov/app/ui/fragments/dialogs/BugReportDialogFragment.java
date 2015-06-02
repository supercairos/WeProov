package com.weproov.app.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.SlackTask;
import com.weproov.app.models.Feedback;

public class BugReportDialogFragment extends BaseDialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.fragment_bug_report, null);
		final EditText titleEdit = (EditText) view.findViewById(R.id.edit_bug_title);
		final EditText contentEdit = (EditText) view.findViewById(R.id.edit_bug_content);
		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_action_about)
				.setTitle(R.string.feedback)
				.setView(view)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String title = titleEdit.getEditableText().toString();
								String content = contentEdit.getEditableText().toString();
								SlackTask.save(new Feedback(title, content));
								Toast.makeText(getActivity().getApplicationContext(), R.string.feedback_posted_successfully, Toast.LENGTH_LONG).show();
							}
						}
				)
				.create();
	}
}
