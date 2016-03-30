package com.yathams.loginsystem;

/**
 * Created by vyatham on 14/03/16.
 */
public class AsyncTask extends android.os.AsyncTask<String, Void, String> {

    private BaseActivity mBaseActivity;

    public AsyncTask(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBaseActivity.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        return mBaseActivity.doInBackground(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mBaseActivity.onPostExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mBaseActivity.onCanceled();
    }
}
