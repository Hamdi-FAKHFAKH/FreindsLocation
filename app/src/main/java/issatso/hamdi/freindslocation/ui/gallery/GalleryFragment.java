package issatso.hamdi.freindslocation.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.HashMap;

import issatso.hamdi.freindslocation.JSONParser;
import issatso.hamdi.freindslocation.databinding.FragmentGalleryBinding;

import java.util.concurrent.TimeUnit;

public class GalleryFragment extends Fragment {
    EditText ed_name, ed_num, ed_lon, ed_lat;
    Button btn_c, btn_i;
    int succes;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ed_name = binding.edNameInsert;
        ed_num = binding.edNumInsert;
        ed_lon = binding.edLonInsert;
        ed_lat = binding.edLatInsert;
        btn_c = binding.btnCInsert;
        binding.btnIInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Upload(getActivity()).execute();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class Upload extends AsyncTask {


        private AlertDialog alertDialog,alertDialog2;
        private Context con;

        public Upload(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setMessage("wait...");
            alertDialog = builder.create();
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {

                alertDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                if (succes == 1) {
                    builder.setMessage(" Data saved successfully");
                    alertDialog2 = builder.create();
                    alertDialog2.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog2.dismiss();
                        }
                    }, 1500);


                } else {
                    builder.setMessage(" Data not saved" );
                    alertDialog2 = builder.create();
                    alertDialog2.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog2.dismiss();
                        }
                    }, 1500);
                }


        }


        @Override
        protected Object doInBackground(Object[] objects) {
            String url = "http://192.168.5.18:8012/servicephp/add.php";
            HashMap<String, String> hm = new HashMap<>();
            hm.put("nom", ed_name.getText().toString());
            hm.put("numero", ed_num.getText().toString());
            hm.put("longitude", ed_lon.getText().toString());
            hm.put("latitude", ed_lat.getText().toString());
            Log.d("dd", hm.toString());


            try {
                JSONObject json = new JSONParser().makeHttpRequest(url, "POST", hm);
                succes = json.getInt("success");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}