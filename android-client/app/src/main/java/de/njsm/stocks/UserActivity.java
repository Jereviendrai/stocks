package de.njsm.stocks;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.network.DeleteDeviceTask;
import de.njsm.stocks.backend.network.NewDeviceTask;

public class UserActivity extends AppCompatActivity
        implements NewDeviceTask.TicketCallback,
                   AdapterView.OnItemLongClickListener,
                   LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_USER_ID = "de.njsm.stocks.UserActivity.id";
    public static final String KEY_USER_NAME = "de.njsm.stocks.UserActivity.name";

    protected int mUserId;
    protected String mUsername;

    protected ListView mList;
    protected SimpleCursorAdapter mAdapter;
    protected Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle extras = getIntent().getExtras();

        if (extras != null &&
                extras.containsKey(KEY_USER_NAME)) {
            mUserId = extras.getInt(KEY_USER_ID);
            mUsername = extras.getString(KEY_USER_NAME);
        } else if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_USER_NAME)){
            mUserId = savedInstanceState.getInt(KEY_USER_ID);
            mUsername = savedInstanceState.getString(KEY_USER_NAME);
        }

        TextView tv = (TextView) findViewById(R.id.user_detail_name);
        assert tv != null;
        tv.setText(mUsername);

        mList = (ListView) findViewById(R.id.user_detail_device_list);
        assert mList != null;
        mList.setOnItemLongClickListener(this);

        String[] sourceName = {SqlDeviceTable.COL_NAME};
        int[] destIds = {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                sourceName,
                destIds,
                0
        );
        mList.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_USER_ID, mUserId);
        outState.putString(KEY_USER_NAME, mUsername);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mUserId = savedInstanceState.getInt(KEY_USER_ID);
        mUsername = savedInstanceState.getString(KEY_USER_NAME);
    }

    public void addDevice(View v) {
        final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(16, 0, 16, 0);
        layout.addView(textField);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_new_device))
                .setView(layout)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = textField.getText().toString().trim();
                        NewDeviceTask task = new NewDeviceTask(UserActivity.this, UserActivity.this);
                        task.execute(name, String.valueOf(UserActivity.this.mUserId), UserActivity.this.mUsername);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    public void applyToTicket(String ticket) {
        Intent i = new Intent(this, QrCodeDisplayActivity.class);
        i.putExtra(QrCodeDisplayActivity.KEY_TICKET, ticket);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://" + StocksContentProvider.AUTHORITY + "/" + SqlDeviceTable.NAME);
        String[] selectionArgs = {String.valueOf(mUserId) };

        return new CursorLoader(this, uri,
                null, null, selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCursor == null) {
            return true;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(position);
        final int deviceId = mCursor.getInt(mCursor.getColumnIndex(SqlDeviceTable.COL_ID));
        final String username = mCursor.getString(mCursor.getColumnIndex(SqlDeviceTable.COL_NAME));
        final int userId = mCursor.getInt(mCursor.getColumnIndex(SqlDeviceTable.COL_USER));
        mCursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                username);
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_delete_device))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteDeviceTask task = new DeleteDeviceTask(UserActivity.this);
                        task.execute(new UserDevice(deviceId, username, userId));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        return true;
    }
}
