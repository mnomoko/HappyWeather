package mnomoko.android.com.happyweather.fragment.parent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import mnomoko.android.com.happyweather.data.loader.DataLoader;

/**
 * Created by mnomoko on 04/07/15.
 */
public class FragmentHanger extends Fragment {

    TaskStatusCallback mStatusCallback;
    DataLoader mBackgroundTask;
    boolean isTaskExecuting = false;

    /**
     * Called when a fragment is first attached to its activity.
     * onCreate(Bundle) will be called after this.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStatusCallback = (TaskStatusCallback)activity;
    }

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    /**
     * Called when the fragment is no longer attached to its activity. This is called after     onDestroy().
     */
    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        mStatusCallback = null;
    }

    public static interface TaskStatusCallback {
        void onPreExecute();

        void onProgressUpdate(int progress);

        void onPostExecute();

        void onCancelled();
    }
}
