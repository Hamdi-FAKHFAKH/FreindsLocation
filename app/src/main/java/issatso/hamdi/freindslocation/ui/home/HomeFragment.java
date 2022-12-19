package issatso.hamdi.freindslocation.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import issatso.hamdi.freindslocation.JSONParser;
import issatso.hamdi.freindslocation.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public ArrayAdapter ad;
    public ArrayList<MyLocation> data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data = new ArrayList<MyLocation>();
        ad = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1 /* format graphique des item dans la liste */, data);
        ListView lv = binding.lvHome;
        lv.setAdapter(ad);

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Telechargment(getActivity()).execute();

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /* Service Asynchrone  */
    class Telechargment extends AsyncTask {
        int succes;
        Context con;
        AlertDialog alertDialog;

        public Telechargment(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            // executer par UI thread (Main)
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("Téléchargement de Données");
            builder.setMessage("wait...");
            alertDialog = builder.create();
            alertDialog.show();

        }

        @Override
        protected void onPostExecute(Object o) {
            // aprés l'execution de thread
            ad.notifyDataSetChanged();
            alertDialog.dismiss();
            if(succes == 0){
                Toast.makeText(con, "NO DATA FOUND", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = "http://192.168.5.18:8012/servicephp/getAll.php";
            JSONObject json = new JSONParser().makeHttpRequest(url, "GET", null);
            try {
                 succes = json.getInt("success");
                if (succes == 0) {
                    String msg = json.getString("message");

                } else {
                    JSONArray tab = json.getJSONArray("Ami");
                    data.clear();
                    for (int i = 0; i < tab.length(); i++) {
                        JSONObject l = tab.getJSONObject(i);
                        String nom = l.getString("nom");
                        String numero = l.getString("numero");
                        String longitude = l.getString("longitude");
                        String latitude = l.getString("latitude");
                        data.add(new MyLocation(nom, numero, longitude, latitude));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}