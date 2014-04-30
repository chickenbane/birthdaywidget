package com.googlejobapp.birthdaywidget;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class ContactListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

	private static final String TAG = "ContactListFragment";
	private static final String[] PROJECTION = { Data._ID, Data.LOOKUP_KEY,
			Data.DISPLAY_NAME_PRIMARY, Event.START_DATE };

	private static final String SELECTION = Data.MIMETYPE + " = ? AND "
			+ Event.TYPE + " = ?";
	private static final String[] SELECTION_ARGS = { Event.CONTENT_ITEM_TYPE,
			"" + Event.TYPE_BIRTHDAY };

	private static final int INDEX_CONTACT_ID = 0;
	private static final int INDEX_LOOKUP_KEY = 1;

	private SimpleCursorAdapter mAdapter;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null, new String[] {
						Data.DISPLAY_NAME_PRIMARY, Event.START_DATE },
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		setListAdapter(mAdapter);

		getListView().setOnItemClickListener(this);

		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return new CursorLoader(getActivity(), Data.CONTENT_URI, PROJECTION,
				SELECTION, SELECTION_ARGS, null);
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {
		mAdapter.swapCursor(null);

	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {

		final Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
		cursor.moveToPosition(position);
		final long contactId = cursor.getLong(INDEX_CONTACT_ID);
		final String lookupKey = cursor.getString(INDEX_LOOKUP_KEY);
		final Uri contactUri = Contacts.getLookupUri(contactId, lookupKey);

		Log.v(TAG, "Contact URI: " + contactUri);
		Log.v(TAG, "num=" + cursor.getCount());
	}

}
